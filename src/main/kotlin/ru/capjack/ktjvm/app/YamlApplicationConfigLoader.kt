package ru.capjack.ktjvm.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path
import kotlin.reflect.KClass

class YamlApplicationConfigLoader : ApplicationConfigLoader {
	private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
	
	override fun match(file: String): Boolean {
		return when (file.substringAfterLast('.').toLowerCase()) {
			"yml", "yaml" -> true
			else          -> false
		}
	}
	
	override fun <T : Any> load(file: Path, type: KClass<out T>): T {
		return mapper.readValue(file.toFile(), type.java)
	}
}