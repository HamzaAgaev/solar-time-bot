package apelsin.space.solar_bot.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolarData(
    val results: SolarResults,
    val status: String
)

@Serializable
data class SolarResults(
    val sunrise: String,
    val sunset: String,
    @SerialName("solar_noon")
    val solarNoon: String
)