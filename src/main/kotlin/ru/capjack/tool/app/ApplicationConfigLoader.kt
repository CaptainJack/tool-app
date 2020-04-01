package ru.capjack.tool.app

import java.io.File
import kotlin.reflect.KClass

interface ApplicationConfigLoader {
	fun match(file: File): Boolean
	
	fun <T : Any> load(file: File, type: KClass<out T>): T
}
