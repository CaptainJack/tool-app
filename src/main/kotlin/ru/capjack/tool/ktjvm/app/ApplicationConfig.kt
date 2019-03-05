package ru.capjack.tool.ktjvm.app

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class ApplicationConfig(val file: String)
