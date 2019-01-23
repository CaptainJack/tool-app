package ru.capjack.ktjvm.app

import ru.capjack.kt.utils.Stoppable

interface Application : Stoppable {
	val running: Boolean
	
	companion object {
		@JvmStatic
		operator fun invoke(args: Array<String>, init: ApplicationBootstrap.() -> Unit): Stoppable {
			return ApplicationBootstrapImpl(args)
				.apply(init)
				.run()
		}
	}
}
