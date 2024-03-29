package ru.capjack.tool.app.holders

@Suppress("DuplicatedCode")
open class SafePositiveIntHolder<R>(
	value: Int,
	private val lock: Any
) {
	@Volatile
	private var current = value
	
	val value get() = current
	
	protected open fun getMaximumValue() = Int.MAX_VALUE
	
	protected open fun onChange(old: Int, new: Int, reason: R) {}
	
	protected open fun afterChange(old: Int, new: Int, reason: R) {}
	
	fun set(value: Int, reason: R) {
		require(value >= 0) { "Negative value" }
		
		val changed: Boolean
		val old: Int
		var new = value
		
		synchronized(lock) {
			val max = getMaximumValue()
			if (new > max) {
				new = max
			}
			
			old = current
			if (new == old) {
				changed = false
			}
			else {
				changed = true
				current = new
				onChange(old, new, reason)
			}
		}
		
		if (changed) {
			afterChange(old, new, reason)
		}
	}
	
	fun add(value: Int, reason: R): Int {
		if (value == 0) return current
		
		require(value > 0) { "Negative value" }
		
		var changed: Boolean
		val old: Int
		var new: Int
		
		synchronized(lock) {
			old = current
			val max = getMaximumValue()
			if (old == max) {
				changed = false
				new = old
			}
			else {
				new = old + value
				if (new > max || new < old) {
					new = max
				}
				changed = true
				current = new
				onChange(old, new, reason)
			}
		}
		
		if (changed) {
			afterChange(old, new, reason)
		}
		
		return new
	}
	
	fun take(value: Int, reason: R): Boolean {
		if (value == 0) return true
		
		require(value > 0) { "Negative value" }
		
		val changed: Boolean
		val old: Int
		val new: Int
		
		synchronized(lock) {
			old = current
			if (old >= value) {
				changed = true
				new = old - value
				current = new
				onChange(old, new, reason)
			}
			else {
				changed = false
				new = old
			}
		}
		
		if (changed) {
			afterChange(old, new, reason)
		}
		
		return changed
	}
}