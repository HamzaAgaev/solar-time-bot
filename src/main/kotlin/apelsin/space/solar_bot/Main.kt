package apelsin.space.solar_bot

import apelsin.space.solar_bot.db.DatabaseFactory
import apelsin.space.solar_bot.telegram.TelegramBotService

fun main() {
    DatabaseFactory.init()
    TelegramBotService.init()
}