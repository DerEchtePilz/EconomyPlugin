package me.derechtepilz.economy.updatemanagement

class UpdateDownload {
    private val checkUpdate: UpdateChecker = UpdateChecker()

    fun downloadUpdate() {
        if (!checkUpdate.isUpdateAvailable()) {
            return
        }
        val downloadUrl = "https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases"

    }

}