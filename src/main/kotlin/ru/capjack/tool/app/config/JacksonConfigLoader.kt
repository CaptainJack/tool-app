package ru.capjack.tool.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ru.capjack.tool.app.ConfigLoader
import java.io.File
import kotlin.reflect.KClass

abstract class JacksonConfigLoader<M : ObjectMapper>(val mapper: M) : ConfigLoader {
	
	init {
		mapper.registerKotlinModule()
	}
	
	override fun <T : Any> load(file: File, type: KClass<out T>): T {
		return mapper.readValue(file, type.java)
	}
}