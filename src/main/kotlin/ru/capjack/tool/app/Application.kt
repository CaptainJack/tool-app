package ru.capjack.tool.app

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.util.ContextInitializer.CONFIG_FILE_PROPERTY
import ch.qos.logback.core.util.OptionHelper
import org.slf4j.LoggerFactory
import ru.capjack.tool.depin.Binder
import ru.capjack.tool.depin.Injection
import ru.capjack.tool.depin.addSmartProducerForAnnotatedClass
import ru.capjack.tool.depin.addSmartProducerForAnnotatedParameter
import ru.capjack.tool.logging.info
import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Stoppable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class Application(
	private val dir: Path,
	private val configEnv: String?,
	private val configLoaders: List<ApplicationConfigLoader>,
	modules: List<KClass<*>>,
	injections: List<Binder.() -> Unit>
) : Stoppable {
	
	internal constructor(configuration: ApplicationConfigurationImpl) : this(
		Paths.get(configuration.dir),
		configuration.configEnv,
		configuration.configLoaders,
		configuration.modules,
		configuration.injections
	)
	
	constructor(args: Array<String>, configuration: ApplicationConfiguration.() -> Unit) : this(ApplicationConfigurationImpl(args).apply(configuration))
	
	private val logger = ownLogger
	private val running = AtomicBoolean(true)
	private val moduleStoppers: Deque<Pair<String, Stoppable>> = LinkedList()
	
	init {
		if (OptionHelper.getSystemProperty(CONFIG_FILE_PROPERTY) == null) {
			val file = resolveConfigPath("logback.xml").toFile()
			if (file.exists()) {
				JoranConfigurator().apply {
					context = (LoggerFactory.getILoggerFactory() as LoggerContext).also(LoggerContext::reset)
					doConfigure(file)
				}
			}
		}
		
		try {
			logger.info("Starting...")
			
			Runtime.getRuntime().addShutdownHook(Thread(::stop, "ApplicationShutdownHook"))
			
			val currentProducingModule = object {
				var clazz: KClass<*>? = null
			}
			
			val injector = Injection()
				.configure {
					addSmartProducerForAnnotatedClass(::provideApplicationConfig)
					addSmartProducerForAnnotatedParameter(::provideApplicationPath)
					addProduceObserverBefore { actual ->
						val expect = currentProducingModule.clazz
						if (expect != null && modules.any { it == actual } && expect != actual) {
							throw RuntimeException("Broken startup sequence of modules, it is expected ${expect.qualifiedName!!} but ${actual.qualifiedName!!} creates")
						}
					}
				}
				.apply {
					injections.forEach { configure(it) }
				}
				.build()
			
			modules.forEach {
				val name = it.qualifiedName!!
				logger.info { "Start module $name" }
				currentProducingModule.clazz = it
				val module = injector.get(it)
				if (module is Stoppable) {
					moduleStoppers.addFirst(name to module)
				}
			}
			currentProducingModule.clazz = null
			
			logger.info("Started")
		}
		catch (e: Throwable) {
			logger.error("Start fail", e)
			stop()
		}
	}
	
	override fun stop() {
		if (running.compareAndSet(true, false)) {
			logger.info("Stopping...")
			
			for (stopper in moduleStoppers) {
				logger.info { "Stop module ${stopper.first}" }
				try {
					stopper.second.stop()
				}
				catch (e: Throwable) {
					logger.warn("Stop module ${stopper.first} fails", e)
				}
			}
			
			logger.info("Stopped")
		}
	}
	
	private fun resolvePath(path: String): Path {
		return dir.resolve(path)
	}
	
	private fun resolveConfigPath(path: String): Path {
		if (configEnv != null && path.contains('.')) {
			val envPath = "${path.substringBeforeLast('.')}.$configEnv.${path.substringAfterLast('.')}"
			resolvePath(envPath).takeIf { Files.exists(it) }?.also {
				return it
			}
		}
		return resolvePath(path)
	}
	
	private fun provideApplicationConfig(annotation: ApplicationConfig, type: KClass<out Any>): Any {
		val file = resolveConfigPath(annotation.file).toFile()
		
		if (!file.exists()) {
			throw IllegalArgumentException("ApplicationConfig file '$file' not exist")
		}
		
		val loader = configLoaders.find { it.match(file) }
			?: throw RuntimeException("ApplicationConfig loader for file '$file' not found")
		
		return loader.load(file, type)
	}
	
	private fun provideApplicationPath(annotation: ApplicationPath, parameter: KParameter): Any {
		val path = resolvePath(annotation.value)
		val type = parameter.type.jvmErasure
		
		return when {
			type.isSubclassOf(Path::class)   -> path
			type.isSubclassOf(String::class) -> path.toString()
			type.isSubclassOf(File::class)   -> path.toFile()
			else                             -> throw IllegalArgumentException("ApplicationPath type '$type' not supported")
		}
	}
}
