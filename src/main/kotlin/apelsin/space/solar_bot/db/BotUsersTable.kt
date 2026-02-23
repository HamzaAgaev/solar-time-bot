package apelsin.space.solar_bot.db

import org.jetbrains.exposed.sql.Table

object BotUsersTable: Table("bot_users") {
    val id = long("id")
    val state = varchar("state", 32)
    val latitude = float("latitude").nullable()
    val longitude = float("longitude").nullable()

    override val primaryKey = PrimaryKey(id)
}