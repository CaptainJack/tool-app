package ru.capjack.tool.app

import java.nio.file.Path
import kotlin.reflect.KClass

interface ApplicationConfigLoader {
	fun match(file: String): Boolean
	
	fun <T : Any> load(file: Path, type: KClass<out T>): T
}
