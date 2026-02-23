package apelsin.space.solar_bot.service

import kotlinx.coroutines.*
import apelsin.space.solar_bot.telegram.TelegramBotService
import java.time.ZoneId
import java.time.ZonedDateTime

class SubscriptionManager(
    private val scope: CoroutineScope
) {
    private val scheduleZone = ZoneId.of("Europe/Moscow")
    private val startHour = 0
    private var job: Job? = null

    fun onUserSubscribed() {
        if (job?.isActive == true) return
        startScheduler()
    }

    fun onUserUnsubscribed() {
        if (BotUserService.countSubscribedUsers() == 0L) {
            stopScheduler()
        }
    }

    private fun startScheduler() {
        job = scope.launch {
            while (isActive) {
                waitUntilNextRun(startHour)
                if (BotUserService.countSubscribedUsers() > 0) {
                    sendToAll()
                } else {
                    stopScheduler()
                }
            }
        }
    }

    private suspend fun waitUntilNextRun(hour: Int) {
        val now = ZonedDateTime.now(scheduleZone)
        var nextRun = now
            .withHour(hour)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        if (!now.isBefore(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }
        val millis = nextRun.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        delay(millis.coerceAtLeast(0))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun sendToAll() = supervisorScope {
        val users = BotUserService.getAllSubscribedUsers()
        if (users.isEmpty()) return@supervisorScope
        val dispatcher = Dispatchers.IO.limitedParallelism(10)
        users
            .map { user ->
                launch(dispatcher) {
                    runCatching {
                        TelegramBotService.sendSolarData(user)
                    }
                }
            }
            .joinAll()
    }

    private fun stopScheduler() {
        job?.cancel()
        job = null
    }
}
