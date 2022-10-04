package me.derechtepilz.database

import java.sql.Connection
import java.util.*

class DatabaseQueryBuilder(private val database: Database) {

    private val connection: Connection = database.connection

    fun registerPlayer(uuid: UUID, balance: Double, lastInterest: Long, startBalance: Double): DatabaseQueryBuilder {
        database.registerPlayer(connection, uuid, balance, lastInterest, startBalance)
        return this
    }

    fun updateBalance(uuid: UUID, balance: Double): DatabaseQueryBuilder {
        database.updateBalance(connection, uuid, balance)
        return this
    }

    fun updateLastInterest(uuid: UUID, lastInterest: Long): DatabaseQueryBuilder {
        database.updateLastInterest(connection, uuid, lastInterest)
        return this
    }

    fun updateStartBalance(uuid: UUID, startBalance: Double): DatabaseQueryBuilder {
        database.updateStartBalance(connection, uuid, startBalance)
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