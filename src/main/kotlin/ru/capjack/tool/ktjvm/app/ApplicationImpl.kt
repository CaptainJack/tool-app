package ru.capjack.tool.ktjvm.app

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import org.slf4j.LoggerFactory
import ru.capjack.tool.kt.inject.Binder
import ru.capjack.tool.kt.inject.Injection
import ru.capjack.tool.kt.inject.Injector
import ru.capjack.tool.kt.inject.registerSmartProducerForAnnotatedClass
import ru.capjack.tool.kt.inject.registerSmartProducerForAnnotatedParameter
import ru.capjack.tool.kt.logging.Logging
import ru.capjack.tool.kt.logging.debug
import ru.capjack.tool.kt.logging.getLogger
import ru.capjack.tool.kt.reflect.kClass
import ru.capjack.tool.kt.utils.Stoppable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

class ApplicationImpl(
	injection: Injection,
	moduleClasses: List<KClass<out ApplicationModule>>,
	private val configLoaders: List<ApplicationConfigLoader>,
	private val dir: Path,
	private val env: String?
) : Application {
	
	private val _running = AtomicBoolean(true)
	private val logger = Logging.getLogger<Application>()
	private val moduleStoppers = mutableListOf<ModuleStopper>()
	
	override val running: Boolean
		get() = _running.get()
	
	init {
		JoranConfigurator().apply {
			context = (LoggerFactory.getILoggerFactory() as LoggerContext).also(LoggerContext::reset)
			doConfigure(resolveConfigPath("logback.xml").toFile())
		}
		
		try {
			logger.info("Starting...")
			Runtime.getRuntime().addShutdownHook(Thread(::stop, "ApplicationShutdown"))
			
			val modules = synchronized(_running) {
				injection.configure(::configureInjection)
				
				val injector = injection.build()
				createModules(injector, moduleClasses)
			}
			
			if (_running.get()) {
				startModules(modules)
				
				logger.info("Started")
			}
		}
		catch (e: Throwable) {
			logger.error("Start fail", e)
			stop()
		}
	}
	
	override fun stop() {
		if (!_running.compareAndSet(true, false)) {
			return
		}
		
		logger.info("Stopping...")
		
		stopModules()
		
		logger.info("Stopped")
	}
	
	private fun configureInjection(binder: Binder) {
		binder.registerSmartProducerForAnnotatedClass(::factoryConfig)
		binder.registerSmartProducerForAnnotatedParameter(::factoryPath)
	}
	
	private fun createModules(injector: Injector, classes: List<KClass<out ApplicationModule>>): List<ModuleStarter> {
		return classes.map {
			val name = it.qualifiedName ?: it.jvmName
			logger.debug { "Create module $name" }
			ModuleStarter(name, injector.get(it))
		}
	}
	
	private fun startModules(starters: List<ModuleStarter>) {
		for (starter in starters) {
			val b = synchronized(_running) {
				if (_running.get()) {
					logger.debug { "Start module ${starter.name}" }
					moduleStoppers.add(ModuleStopper(starter.name, starter.module.start()))
					false
				}
				else true
			}
			if (b) {
				break
			}
		}
	}
	
	private fun stopModules() {
		synchronized(_running) {
			for (stopper in moduleStoppers.asReversed()) {
				logger.debug { "Stop module ${stopper.name}" }
				try {
					stopper.stopper.stop()
				}
				catch (e: Throwable) {
					logger.warn("Stop module ${stopper.name} fails", e)
				}
			}
		}
	}
	
	private fun resolvePath(path: String): Path {
		return dir.resolve(path)
	}
	
	private fun resolveConfigPath(path: String): Path {
		return resolvePath("config/$path")
	}
	
	private fun factoryConfig(
		annotation: ApplicationConfig,
		type: KClass<out Any>
	): Any {
		val file = annotation.file
		
		val loader = configLoaders.find { it.match(file) }
			?: throw RuntimeException("ApplicationConfig loader for file '$file' not found")
		
		if (env != null) {
			val envFile = "${file.substringBeforeLast('.')}.$env.${file.substringAfterLast('.')}"
			resolveConfigPath(envFile).takeIf { Files.exists(it) }?.let {
				return loader.load(it, type)
			}
		}
		
		resolveConfigPath(file).takeIf { Files.exists(it) }?.let {
			return loader.load(it, type)
		}
		
		throw IllegalArgumentException("ApplicationConfig file '$file' not exist")
	}
	
	private fun factoryPath(
		annotation: ApplicationPath,
		parameter: KParameter
	): Any {
		val path = resolvePath(annotation.value)
		val type = parameter.type.kClass
		
		return when {
			type.isSubclassOf(Path::class)   -> path
			type.isSubclassOf(String::class) -> path.toString()
			type.isSubclassOf(File::class)   -> path.toFile()
			else                             -> throw IllegalArgumentException("ApplicationPath type '$type' not supported")
		}
	}
	
	private abstract class ModuleHolder(val name: String)
	
	private class ModuleStarter(name: String, val module: ApplicationModule) : ModuleHolder(name)
	
	private class ModuleStopper(name: String, val stopper: Stoppable) : ModuleHolder(name)
	
}