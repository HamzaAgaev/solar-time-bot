package apelsin.space.solar_bot

import com.typesafe.config.ConfigFactory

object Config {
    private val config = ConfigFactory.load()
        .withFallback(ConfigFactory.systemEnvironment())
        .resolve()

    val botToken: String get() = config.getString("bot.token")
    val dbJdbcUrl: String get() = config.getString("db.jdbcUrl")
    val dbUsername: String get() = config.getString("db.username")
    val dbPassword: String get() = config.getString("db.password")
    val dbDriverClassName: String get() = config.getString("db.driverClassName")
    val dbMaximumPoolSize: Int get() = config.getInt("db.maximumPoolSize")
}
