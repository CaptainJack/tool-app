package ru.capjack.tool.app.internal

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler

internal class TimeStringToSecondsIntDeserializationProblemHandler : DeserializationProblemHandler() {
	private val regex = Regex("\\s*(?:(\\d+)d)?\\s*(?:(\\d+)h)?\\s*(?:(\\d+)m)?\\s*(?:(\\d+)s)?\\s*")
	
	override fun handleWeirdStringValue(ctxt: DeserializationContext, targetType: Class<*>, valueToConvert: String, failureMsg: String): Any {
		if (targetType == Int::class.java) {
			val e = regex.matchEntire(valueToConvert)
			if (e != null) {
				var v = 0
				e.groups[1]?.also { v += it.value.toInt() * 60 * 60 * 24 }
				e.groups[2]?.also { v += it.value.toInt() * 60 * 60 }
				e.groups[3]?.also { v += it.value.toInt() * 60 }
				e.groups[4]?.also { v += it.value.toInt() }
				return v
			}
		}
		
		return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg)
	}
}