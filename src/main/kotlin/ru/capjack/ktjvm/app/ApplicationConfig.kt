package ru.capjack.ktjvm.app

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class ApplicationConfig(val file: String)
