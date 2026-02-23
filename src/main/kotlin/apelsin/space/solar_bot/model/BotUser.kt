package apelsin.space.solar_bot.model

enum class BotUserState {
    STARTED,
    SET_GEO_POSITION,
    UNSUBSCRIBED,
    SUBSCRIBED
}

data class GeoPosition(
    val latitude: Float,
    val longitude: Float
)

data class BotUser(val id: Long) {
    var state = BotUserState.STARTED
    var geoPosition: GeoPosition? = null
}
