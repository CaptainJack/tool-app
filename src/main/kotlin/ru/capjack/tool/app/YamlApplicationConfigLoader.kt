package ru.capjack.tool.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import kotlin.reflect.KClass

class YamlApplicationConfigLoader : ApplicationConfigLoader {
	private val yaml = ObjectMapper(YAMLFactory()).registerKotlinModule()
	
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
