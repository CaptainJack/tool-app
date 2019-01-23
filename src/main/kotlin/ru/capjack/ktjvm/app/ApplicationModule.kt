package ru.capjack.ktjvm.app

import ru.capjack.kt.utils.Stoppable

interface ApplicationModule {
	fun start(): Stoppable
}