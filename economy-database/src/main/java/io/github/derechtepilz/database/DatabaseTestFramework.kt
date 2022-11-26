package io.github.derechtepilz.database

import org.bukkit.plugin.Plugin
import java.util.*
import kotlin.random.Random

object DatabaseTestFramework {

	private val uuidList: MutableList<UUID> = mutableListOf()

	@JvmStatic
	fun initialize(database: Database, plugin: Plugin) {
		// Test registration of 50 players at once
		var databaseQueryBuilder = DatabaseQueryBuilder(database)
		val startRegistration: Double = System.currentTimeMillis() / 1000.0
		for (i in 0 until 50) {
			val uuid: UUID = UUID.randomUUID()
			val balance: Double = Random.nextDouble(1.0, 2.0 + 10 * i)
			val lastInterest: Long = System.currentTimeMillis()
			val startBalance: Double = Random.nextDouble(1.0, 2.0 + i)

			uuidList.add(uuid)
			databaseQueryBuilder.registerPlayer(uuid, balance, lastInterest, startBalance)
		}

		val finishRegistration: Double = System.currentTimeMillis() / 1000.0
		plugin.logger.info("Registering 50 players took ${finishRegistration - startRegistration}s!")

		// Test changing random values of 50 players at one
		databaseQueryBuilder = DatabaseQueryBuilder(database)
		val startValueChange: Double = System.currentTimeMillis() / 1000.0
		for (i in 0 until 50) {
			when (Random.nextInt(0, 2)) {
				0 -> databaseQueryBuilder.updateBalance(uuidList[i], Random.nextDouble(1.0, 2.0 + 10 * i))
				1 -> databaseQueryBuilder.updateLastInterest(uuidList[i], System.currentTimeMillis())
				2 -> databaseQueryBuilder.updateStartBalance(uuidList[i], Random.nextDouble(1.0, 2.0 + i))
			}
		}

		val finishValueChange: Double = System.currentTimeMillis() / 1000.0
		plugin.logger.info("Changing values of 50 players took ${finishValueChange - startValueChange}s!")
	}

	@JvmStatic
	fun clean(database: Database, plugin: Plugin) {
		val startDeletePlayers: Double = System.currentTimeMillis() / 1000.0
		for (uuid in uuidList) {
			DatabaseQueryBuilder(database).deletePlayer(uuid)
		}
		val finishDeletePlayer: Double = System.currentTimeMillis() / 1000.0
		plugin.logger.info("Deleting 50 players took ${finishDeletePlayer - startDeletePlayers}s!")
	}

}