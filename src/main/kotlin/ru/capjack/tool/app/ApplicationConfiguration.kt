package ru.capjack.tool.app

import ru.capjack.tool.depin.Binder
import kotlin.reflect.KClass

interface ApplicationConfiguration {
	var dir: String
	var configEnv: String?
	
	fun module(clazz: KClass<*>)
	
	fun modules(list: List<KClass<*>>)
	
	fun injection(configuration: Binder.() -> Unit)
	
	fun configLoader(loader: ConfigLoader)
	
	fun configLoaders(list: List<ConfigLoader>)
}

inline fun <reified T : Any> ApplicationConfiguration.module() = module(T::class)


