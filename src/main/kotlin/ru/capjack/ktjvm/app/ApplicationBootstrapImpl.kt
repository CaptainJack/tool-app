package ru.capjack.ktjvm.app

import ru.capjack.kt.inject.Binder
import ru.capjack.kt.inject.Injection
import ru.capjack.kt.utils.Stoppable
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

class ApplicationBootstrapImpl(
	private val dir: Path,
	private val env: String? = null
) : ApplicationBootstrap {
	
	private val injection = Injection()
	private val moduleClasses = mutableListOf<KClass<out ApplicationModule>>()
	private var configLoaders = mutableListOf<ApplicationConfigLoader>()
	
	constructor(args: Array<String>) : this(
		args.getOrNull(0)
			?.let { Paths.get(it).toAbsolutePath() }
			?: throw RuntimeException("Argument for application work dir required"),
		args.getOrNull(1)
	)
	
	init {
		if (!Files.isDirectory(dir)) {
			throw RuntimeException("ApplicationImpl work dir not exist ($dir)")
		}
		
		configLoaders.add(YamlApplicationConfigLoader())
	}
	
	override fun module(clazz: KClass<out ApplicationModule>) {
		moduleClasses.add(clazz)
	}
	
	override fun modules(vararg classes: KClass<out ApplicationModule>) {
		moduleClasses.addAll(classes)
	}
	
	override fun inject(configuration: Binder.() -> Unit) {
		injection.configure(configuration)
	}
	
	override fun config(loader: ApplicationConfigLoader) {
		configLoaders.add(loader)
	}
	
	fun run(): Stoppable {
		return ApplicationImpl(injection, moduleClasses, configLoaders, dir, env)
	}
}

