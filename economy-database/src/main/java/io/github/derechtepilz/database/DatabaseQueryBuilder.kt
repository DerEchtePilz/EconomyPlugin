package io.github.derechtepilz.database

import java.sql.Connection
import java.util.*

class DatabaseQueryBuilder(private val database: Database) {

    private val connection: Connection = database.connection

    fun registerPlayer(uuid: UUID, balance: Double, lastInterest: Long, startBalance: Double): DatabaseQueryBuilder {
        database.registerPlayer(uuid, balance, lastInterest, startBalance)
        return this
    }

    fun updateBalance(uuid: UUID, balance: Double): DatabaseQueryBuilder {
        database.updateBalance(uuid, balance)
        return this
    }

    fun updateLastInterest(uuid: UUID, lastInterest: Long): DatabaseQueryBuilder {
        database.updateLastInterest(uuid, lastInterest)
        return this
    }

    fun updateStartBalance(uuid: UUID, startBalance: Double): DatabaseQueryBuilder {
        database.updateStartBalance(uuid, startBalance)
        return this
    }

    fun deletePlayer(uuid: UUID): DatabaseQueryBuilder {
        database.deletePlayer(connection, uuid)
        return this
    }

    fun commit() {
        connection.commit()
    }

}