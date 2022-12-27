package io.github.derechtepilz.economy.utils

import io.github.derechtepilz.economy.Main

class TranslatableComponent(private val key: String, private vararg val replacements: String) {

	/**
	 * %1 -> first element
	 *
	 * %2 -> second element
	 *
	 * %3 -> third element
	 */
	@Suppress("ReplaceManualRangeWithIndicesCalls")
	fun toMessage(): String {
		var translation: String = Main.main.languageManager.getTranslation(key)
		for (i in 0 until replacements.size) {
			translation = translation.replace("%${i + 1}", replacements[i])
		}
		return translation
	}

}