package ru.capjack.tool.app

inline fun <reified T : ApplicationModule> ApplicationBootstrap.module() = module(T::class)