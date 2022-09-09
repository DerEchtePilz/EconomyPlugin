package me.derechtepilz.economy.utility

class UnsuccessfulAPIRequestException(override val message: String): RuntimeException(message) {
}