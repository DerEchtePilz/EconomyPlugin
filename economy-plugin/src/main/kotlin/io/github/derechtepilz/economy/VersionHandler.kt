package io.github.derechtepilz.economy

object VersionHandler {

	private val supportedVersions = arrayOf(
		"1.13", "1.13.1", "1.13.2",
		"1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4",
		"1.15", "1.15.1", "1.15.2",
		"1.16", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5",
		"1.17", "1.17.1",
		"1.18", "1.18.1", "1.18.2",
		"1.19", "1.19.1", "1.19.2", "1.19.3"
	)

	fun isVersionSupported(version: String): Boolean {
		for (supportedVersion in supportedVersions) {
			if (supportedVersion == version) return true
		}
		return false
	}
}