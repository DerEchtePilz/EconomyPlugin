package io.github.derechtepilz.economy.updatemanagement

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class APIRequest(private val url: String) {
	private var reader: BufferedReader? = null

	fun request(): String {
		val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
		connection.requestMethod = "GET"
		val responseCode: Int = connection.responseCode
		if (responseCode > 299) {
			throw IllegalStateException("Could not finish api request!")
		} else {
			reader = BufferedReader(InputStreamReader(connection.inputStream))
		}
		val responseContent = StringBuffer()
		var line = reader!!.readLine()
		while (line != null) {
			responseContent.append(line)
			line = reader!!.readLine()
		}
		reader!!.close()
		return responseContent.toString()
	}
}