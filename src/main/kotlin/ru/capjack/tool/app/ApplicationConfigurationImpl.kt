package ru.capjack.tool.app

import ru.capjack.tool.app.config.PropertiesConfigLoader
import ru.capjack.tool.app.config.YamlConfigLoader
import ru.capjack.tool.app.config.handler.TimeStringToSecondsIntDeserializationProblemHandler
import ru.capjack.tool.depin.Binder
import kotlin.reflect.KClass

internal class ApplicationConfigurationImpl(args: Array<String>) : ApplicationConfiguration {
	override var dir: String = args.getOrNull(0) ?: "."
	override var configEnv: String? = args.getOrNull(1)
	
	val modules = mutableListOf<KClass<*>>()
	val injections = mutableListOf<Binder.() -> Unit>()
	var configLoaders = mutableListOf<ConfigLoader>()
	
	init {
		val configs = listOf(YamlConfigLoader(), PropertiesConfigLoader())
		val handler = TimeStringToSecondsIntDeserializationProblemHandler()
		configs.forEach { it.mapper.addHandler(handler) }
		configLoaders(configs)
	}
	
	override fun module(clazz: KClass<*>) {
		modules.add(clazz)
	}
	
	override fun modules(list: List<KClass<*>>) {
		modules.addAll(list)
	}
	
	override fun injection(configuration: Binder.() -> Unit) {
		injections.add(configuration)
	}
	
	override fun configLoader(loader: ConfigLoader) {
		configLoaders.add(loader)
	}
	
	override fun configLoaders(list: List<ConfigLoader>) {
		configLoaders.addAll(list)
	}
}