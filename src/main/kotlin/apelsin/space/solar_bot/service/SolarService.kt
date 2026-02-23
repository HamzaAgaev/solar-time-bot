package apelsin.space.solar_bot.service

import apelsin.space.solar_bot.exception.ApiFetchException
import apelsin.space.solar_bot.model.SolarData
import apelsin.space.solar_bot.model.SolarResults
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object SolarService {
    private const val API_URL = "https://api.sunrise-sunset.org/json"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getSolarInfo(latitude: Float, longitude: Float): SolarResults {
        val response: SolarData = client.get(API_URL) {
            parameter("lat", latitude)
            parameter("lng", longitude)
            parameter("formatted", 0)
        }.body()
        if (response.status != "OK") {
            throw ApiFetchException("Невозможно обратиться к API солнечного времени.")
        }
        return response.results
    }
}