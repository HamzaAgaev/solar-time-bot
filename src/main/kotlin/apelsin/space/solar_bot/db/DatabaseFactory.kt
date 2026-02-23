package apelsin.space.solar_bot.db

import apelsin.space.solar_bot.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = Config.dbJdbcUrl
            username = Config.dbUsername
            password = Config.dbPassword
            driverClassName = Config.dbDriverClassName
            maximumPoolSize = Config.dbMaximumPoolSize
        }
        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        createSchema()
    }

    private fun createSchema() {
        transaction {
            SchemaUtils.create(BotUsersTable)
        }
    }
}
