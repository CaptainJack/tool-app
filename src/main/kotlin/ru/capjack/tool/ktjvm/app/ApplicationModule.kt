package ru.capjack.tool.ktjvm.app

import ru.capjack.tool.kt.utils.Stoppable

interface ApplicationModule {
	fun start(): Stoppable
}