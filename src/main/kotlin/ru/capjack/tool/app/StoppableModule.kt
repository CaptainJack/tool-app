package ru.capjack.tool.app

import ru.capjack.tool.utils.Stoppable

abstract class StoppableModule : Stoppable {
	@Volatile
	private var running = true
	
	final override fun stop() {
		if (running) {
			synchronized(this) {
				if (running) {
					running = false
					doStop()
				}
			}
		}
	}
	
	protected abstract fun doStop()
}