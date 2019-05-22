package ru.capjack.tool.app

import ru.capjack.tool.utils.Stoppable

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
