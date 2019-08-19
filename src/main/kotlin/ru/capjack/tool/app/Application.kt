package ru.capjack.tool.app

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.util.ContextInitializer.CONFIG_FILE_PROPERTY
import ch.qos.logback.core.util.OptionHelper
import org.slf4j.LoggerFactory
import ru.capjack.tool.depin.Binder
import ru.capjack.tool.depin.Injection
import ru.capjack.tool.depin.registerSmartProducerForAnnotatedClass
import ru.capjack.tool.depin.registerSmartProducerForAnnotatedParameter
import ru.capjack.tool.logging.info
import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Stoppable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

class Application(
	modules: List<KClass<*>>,
	injections: List<Binder.() -> Unit>,
	private val configLoaders: List<ApplicationConfigLoader>,
	private val dir: Path,
	private val env: String?
) : Stoppable {
	
	companion object {
		inline operator fun invoke(args: Array<String>, build: ApplicationBuilder.() -> Unit): Application {
			return ApplicationBuilder(args).also(build).build()
		}
	}
	
	private val logger = ownLogger
	private val running = AtomicBoolean(true)
	private val moduleStoppers: Deque<Pair<String, Stoppable>> = LinkedList()
	
	init {
		if (OptionHelper.getSystemProperty(CONFIG_FILE_PROPERTY) == null) {
			JoranConfigurator().apply {
				context = (LoggerFactory.getILoggerFactory() as LoggerContext).also(LoggerContext::reset)
				doConfigure(resolveConfigPath("logback.xml").toFile())
			}
		}
		
		try {
			logger.info("Starting...")
			
			Runtime.getRuntime().addShutdownHook(Thread(::stop, "ApplicationShutdown"))
			
			val injector = Injection()
				.configure {
					registerSmartProducerForAnnotatedClass(::factoryConfig)
					registerSmartProducerForAnnotatedParameter(::factoryPath)
				}
				.apply {
					injections.forEach { configure(it) }
				}
				.build()
			
			modules.forEach {
				val name = it.qualifiedName ?: it.jvmName
				logger.info { "Start module $name" }
				val module = injector.get(it)
				if (module is Stoppable) {
					moduleStoppers.addFirst(name to module)
				}
			}
			
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
		if (env != null && path.contains('.')) {
			val envPath = "${path.substringBeforeLast('.')}.$env.${path.substringAfterLast('.')}"
			resolvePath("config/$envPath").takeIf { Files.exists(it) }?.also {
				return it
			}
		}
		return resolvePath("config/$path")
	}
	
	private fun factoryConfig(
		annotation: ApplicationConfig,
		type: KClass<out Any>
	): Any {
		val file = annotation.file
		
		val loader = configLoaders.find { it.match(file) }
			?: throw RuntimeException("ApplicationConfig loader for file '$file' not found")
		
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
		val type = parameter.type.jvmErasure
		
		return when {
			type.isSubclassOf(Path::class)   -> path
			type.isSubclassOf(String::class) -> path.toString()
			type.isSubclassOf(File::class)   -> path.toFile()
			else                             -> throw IllegalArgumentException("ApplicationPath type '$type' not supported")
		}
	}
}
