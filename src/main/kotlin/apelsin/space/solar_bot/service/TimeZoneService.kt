package apelsin.space.solar_bot.service

import net.iakovlev.timeshape.TimeZoneEngine
import java.time.ZoneId

object TimeZoneService {
    private val engine = TimeZoneEngine.initialize()

    fun getZoneId(latitude: Float, longitude: Float): ZoneId? {
        return engine.query(latitude.toDouble(), longitude.toDouble()).orElse(null)
    }
}