package io.github.derechtepilz.economy.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.derechtepilz.economy.Main
import java.io.BufferedReader
import java.io.InputStreamReader

class LanguageManager(private val main: Main) {

	private val englishTranslation: MutableMap<String, String> = mutableMapOf()
	private val germanTranslation: MutableMap<String, String> = mutableMapOf()

	fun init() {
		val reader = main.getResource("lang.json")!!.bufferedReader()
		val builder = StringBuilder()
		var line: String?
		while (reader.readLine().also { line = it } != null) {
			builder.append(line)
		}
		val translations: JsonObject = JsonParser.parseString(builder.toString()).asJsonObject
		for (key in translations.keySet()) {
			val englishValue: String = translations[key].asJsonObject["en_en"].asString
			val germanValue: String = translations[key].asJsonObject["de_de"].asString

			englishTranslation[key] = englishValue
			germanTranslation[key] = germanValue
		}
		reader.close()
	}

	fun getTranslation(key: String): String {
		return when (main.language) {
			Language.EN_EN -> if (englishTranslation.containsKey(key)) englishTranslation[key]!! else key
			Language.DE_DE -> if (germanTranslation.containsKey(key)) germanTranslation[key]!! else key
		}
	}

	enum class Language(val languageId: String) {
		EN_EN("en_en"),
		DE_DE("de_de")
	}

}