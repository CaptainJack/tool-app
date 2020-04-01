package ru.capjack.tool.app

import org.yaml.snakeyaml.Yaml
import java.io.File
import kotlin.reflect.KClass

class YamlApplicationConfigLoader : ApplicationConfigLoader {
	private val yaml = Yaml()
	
	override fun match(file: File): Boolean {
		return when (file.extension.toLowerCase()) {
			"yml", "yaml" -> true
			else          -> false
		}
	}
	
	override fun <T : Any> load(file: File, type: KClass<out T>): T {
		return file.reader().use {
			yaml.loadAs(it, type.java)
		}
	}
}
