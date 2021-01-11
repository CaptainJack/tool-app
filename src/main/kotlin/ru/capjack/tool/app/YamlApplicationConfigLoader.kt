package ru.capjack.tool.app

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ru.capjack.tool.app.internal.TimeStringToSecondsIntDeserializationProblemHandler
import java.io.File
import kotlin.reflect.KClass

class YamlApplicationConfigLoader : ApplicationConfigLoader {
	private val yaml = YAMLMapper()
		.registerKotlinModule()
		.addHandler(TimeStringToSecondsIntDeserializationProblemHandler())
	
	override fun match(file: File): Boolean {
		return when (file.extension.toLowerCase()) {
			"yml", "yaml" -> true
			else          -> false
		}
	}
	
	override fun <T : Any> load(file: File, type: KClass<out T>): T {
		return yaml.readValue(file, type.java)
	}
}
