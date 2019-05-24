package ru.capjack.tool.app

inline fun <reified T : Any> ApplicationBuilder.module() = module(T::class)
