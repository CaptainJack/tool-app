package ru.capjack.tool.app

import ru.capjack.tool.depin.Binder
import kotlin.reflect.KClass

interface ApplicationConfiguration {
	var dir: String
	var configEnv: String?
	
	fun module(clazz: KClass<*>)
	
	fun modules(vararg classes: KClass<*>)
	
	fun injection(configuration: Binder.() -> Unit)
	
	fun configLoader(loader: ApplicationConfigLoader)
}

inline fun <reified T : Any> ApplicationConfiguration.module() = module(T::class)


