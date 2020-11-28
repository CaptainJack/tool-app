package ru.capjack.tool.app

import ru.capjack.tool.depin.Binder
import kotlin.reflect.KClass

internal class ApplicationConfigurationImpl(args: Array<String>) : ApplicationConfiguration {
	override var dir: String = args.getOrNull(0) ?: "."
	override var configEnv: String? = args.getOrNull(1)
	
	val modules = mutableListOf<KClass<*>>()
	val injections = mutableListOf<Binder.() -> Unit>()
	var configLoaders = mutableListOf<ApplicationConfigLoader>(
		YamlApplicationConfigLoader()
	)
	
	override fun module(clazz: KClass<*>) {
		modules.add(clazz)
	}
	
	override fun <T : Any> module(clazz: KClass<out T>, bind: KClass<T>) {
		module(clazz)
		injection { bind(bind, clazz) }
	}
	
	override fun modules(vararg classes: KClass<*>) {
		modules.addAll(classes)
	}
	
	override fun injection(configuration: Binder.() -> Unit) {
		injections.add(configuration)
	}
	
	override fun configLoader(loader: ApplicationConfigLoader) {
		configLoaders.add(loader)
	}
}