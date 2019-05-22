package ru.capjack.tool.app

import ru.capjack.tool.utils.Stoppable

interface ApplicationModule {
	fun start(): Stoppable
}