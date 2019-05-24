package ru.capjack.tool.app

import ru.capjack.tool.depin.Binder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

class ApplicationBuilder(
	private val dir: Path,
	private val env: String? = null
) {
	private val modules = mutableListOf<KClass<*>>()
	private val injections = mutableListOf<Binder.() -> Unit>()
	private var configLoaders = mutableListOf<ApplicationConfigLoader>()
	
	constructor(args: Array<String>) : this(
		args.getOrNull(0)
			?.let { Paths.get(it).toAbsolutePath() }
			?: throw RuntimeException("Argument for application work dir required"),
		args.getOrNull(1)
	)
	
	init {
		if (!Files.isDirectory(dir)) {
			throw RuntimeException("Application work dir not exist ($dir)")
		}
		
		configLoaders.add(YamlApplicationConfigLoader())
	}
	
	fun module(clazz: KClass<*>) {
		modules.add(clazz)
	}
	
	fun modules(vararg classes: KClass<*>) {
		modules.addAll(classes)
	}
	
	fun inject(configuration: Binder.() -> Unit) {
		injections.add(configuration)
	}
	
	fun config(loader: ApplicationConfigLoader) {
		configLoaders.add(loader)
	}
	
	fun build(): Application {
		return Application(modules, injections, configLoaders, dir, env)
	}
}
