package ru.capjack.tool.app

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class ApplicationConfig(val file: String = "")
