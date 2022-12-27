package io.github.derechtepilz.economy.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.derechtepilz.economy.Main
import org.fusesource.jansi.Ansi
import java.io.*
import java.util.*
import kotlin.collections.HashMap

class Config(private val main: Main) {

	private var config: MutableMap<String, String> = mutableMapOf()
	private var defaultConfigValues: MutableMap<String, String> = mutableMapOf()
	private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
	private val configFile: File = File(File("./plugins/Economy"), "config.json")

	private var isLoaded = false

	@Suppress("UNCHECKED_CAST")
	@Throws(FileNotFoundException::class)
	fun loadConfig() {
		if (isLoaded) {
			main.logger.info(Ansi().fgRed().a("The config is already loaded! Please do not call the loadConfig() method twice!").toString())
			return
		}
		if (!configFile.exists()) {
			resetConfig()
		}
		config = gson.fromJson(FileReader(configFile), HashMap::class.java) as MutableMap<String, String>
		defaultConfigValues = gson.fromJson(getValuesFromDefaultConfig(), HashMap::class.java) as MutableMap<String, String>
		checkConfigValues(config, defaultConfigValues)
		saveConfig()
		isLoaded = true
	}

	fun get(value: String): String? {
		return config[value]
	}

	fun set(key: String?, value: String?) {
		config[key as String] = value as String
 		saveConfig()
	}

	private fun saveConfig() {
		save(gson.toJson(config))
	}

	operator fun contains(value: String): Boolean {
		return config.containsKey(value)
	}

	@Suppress("UNCHECKED_CAST")
	fun reloadConfig() {
		try {
			config = gson.fromJson(FileReader(configFile), HashMap::class.java) as MutableMap<String, String>
		} catch (exception: IOException) {
			val ioException = IOException("Failed to reload config!")
			main.logger.severe(ioException.message)
		}
	}

	fun resetConfig() {
		save(getValuesFromDefaultConfig())
	}

	private fun save(configValues: String) {
		try {
			val pluginFolder = File("./plugins/Economy")
			if (!pluginFolder.exists()) {
				pluginFolder.mkdir()
			}
			val config = File(pluginFolder, "config.json")
			if (!config.exists()) {
				config.createNewFile()
			}
			val writer: Writer = FileWriter(config)
			writer.write(configValues)
			writer.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun getValuesFromDefaultConfig(): String {
		val config: InputStream = main.getResource("config.json")!!
		val reader = BufferedReader(InputStreamReader(config))
		val builder = StringBuilder()
		try {
			var line: String?
			while (reader.readLine().also { line = it } != null) {
				builder.append(line)
			}
		} catch (exception: IOException) {
			val ioException = IOException("Failed to load default config!")
			main.logger.severe(ioException.message)
			main.logger.severe(Arrays.toString(ioException.stackTrace))
		}
		return builder.toString()
	}

	private fun checkConfigValues(config: MutableMap<String, String>, defaultConfig: MutableMap<String, String>) {
		if (config.size == defaultConfig.size) {
			return
		}
		val updatedConfig: MutableMap<String, String> = mutableMapOf()

		if (config.size > defaultConfig.size) {
			// config.json in plugins/Economy/config.json has more elements than src/main/resources/config.json
			// Only values that are present in both configs will be written to the updated config
			// If no values were present in the updated config, the default values will be set
			for (key in defaultConfig.keys) {
				if (config.containsKey(key)) {
					updatedConfig[key] = config[key]!!
				}
			}
			if (updatedConfig.isEmpty()) {
				for (key in defaultConfig.keys) {
					updatedConfig[key] = defaultConfig[key]!!
				}
			}
		} else {
			// config.json in plugins/Economy/config.json has fewer elements than src/main/resources/config.json
			// First, write all the values from plugins/Economy/config.json into the updated config
			// Then add the new values that weren't present before
			for (key in defaultConfig.keys) {
				if (config.containsKey(key)) {
					updatedConfig[key] = config[key]!!
					continue
				}
				updatedConfig[key] = defaultConfig[key]!!
			}
		}
		this.config = updatedConfig
		this.defaultConfigValues = defaultConfig
	}

}