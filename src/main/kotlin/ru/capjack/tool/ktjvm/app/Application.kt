package ru.capjack.tool.ktjvm.app

import ru.capjack.tool.kt.utils.Stoppable

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
