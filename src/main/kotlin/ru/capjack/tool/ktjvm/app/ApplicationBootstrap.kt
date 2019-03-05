package ru.capjack.tool.ktjvm.app

import ru.capjack.tool.kt.inject.Binder
import kotlin.reflect.KClass

interface ApplicationBootstrap {
	fun module(clazz: KClass<out ApplicationModule>)
	
	fun modules(vararg classes: KClass<out ApplicationModule>)
	
	fun inject(configuration: Binder.() -> Unit)
	
	fun config(loader: ApplicationConfigLoader)
}