package apelsin.space.solar_bot.db

import apelsin.space.solar_bot.model.BotUser
import apelsin.space.solar_bot.model.BotUserState
import apelsin.space.solar_bot.model.GeoPosition
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BotUserRepository {
    fun addUser(id: Long) = transaction {
        BotUsersTable.insertIgnore {
            it[BotUsersTable.id] = id
            it[state] = BotUserState.STARTED.name
        }
    }

    fun userExists(id: Long): Boolean = transaction {
        BotUsersTable.selectAll()
            .where { BotUsersTable.id eq id }
            .count() > 0
    }

    private fun mapToModel(row: ResultRow) =
        BotUser(row[BotUsersTable.id]).apply {
            state = BotUserState.valueOf(row[BotUsersTable.state])
            val latitude = row[BotUsersTable.latitude]
            val longitude = row[BotUsersTable.longitude]
            if (latitude != null && longitude != null) {
                geoPosition = GeoPosition(latitude.toFloat(), longitude.toFloat())
            }
        }

    fun getUser(id: Long): BotUser? = transaction {
        BotUsersTable.selectAll()
            .where { BotUsersTable.id eq id }
            .map {
                mapToModel(it)
            }
            .singleOrNull()
    }

    fun updateGeoPosition(id: Long, latitude: Float, longitude: Float) = transaction {
        BotUsersTable.update({ BotUsersTable.id eq id }) {
            it[BotUsersTable.latitude] = latitude
            it[BotUsersTable.longitude] = longitude
        }
    }

    fun updateState(id: Long, state: BotUserState) = transaction {
        BotUsersTable.update({ BotUsersTable.id eq id }) {
            it[BotUsersTable.state] = state.name
        }
    }

    fun getAllSubscribedUsers(): List<BotUser> = transaction {
        BotUsersTable.selectAll()
            .where { BotUsersTable.state eq BotUserState.SUBSCRIBED.name }
            .map {
                mapToModel(it)
            }
    }

    fun countSubscribedUsers(): Long = transaction {
        BotUsersTable.selectAll()
            .where { BotUsersTable.state eq BotUserState.SUBSCRIBED.name }
            .count()
    }
}
