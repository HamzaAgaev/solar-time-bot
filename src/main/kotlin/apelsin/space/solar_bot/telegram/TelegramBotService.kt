package apelsin.space.solar_bot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import apelsin.space.solar_bot.Config
import apelsin.space.solar_bot.model.BotUser
import apelsin.space.solar_bot.model.BotUserState
import apelsin.space.solar_bot.service.BotUserService
import apelsin.space.solar_bot.service.SolarService
import apelsin.space.solar_bot.service.SubscriptionManager
import apelsin.space.solar_bot.service.TimeZoneService
import java.time.ZoneId
import java.time.ZonedDateTime

object TelegramBotService {
    private lateinit var subscriptionManager: SubscriptionManager
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val bot = bot {
        token = Config.botToken

        dispatch {
            command("start") {
                startHandle(message)
            }

            text(sendGeoMessage) {
                geoMessageHandle(message)
            }

            location{
                locationHandle(message, location)
            }

            text(subscribeMessage) {
                subscribeHandle(message)
            }

            text(unsubscribeMessage) {
                unsubscribeHandle(message)
            }

            text(getSolarDataMessage) {
                appScope.launch { getSolarDataHandle(message) }
            }
        }
    }

    fun init() {
        subscriptionManager = SubscriptionManager(appScope)
        if (BotUserService.countSubscribedUsers() > 0) {
            subscriptionManager.onUserSubscribed()
        }

        bot.startPolling()
    }

    private fun getPrivateSender(message: Message) =
        if (message.chat.type == "private") message.from else null

    private fun getBotUser(id: Long) = BotUserService.getUser(id)

    private fun startHandle(message: Message) {
        getPrivateSender(message)
            ?.takeIf {!BotUserService.userExists(it.id)}
            ?.also {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = startReply,
                    replyMarkup = setGeoPositionKeyboardReply
                )
                BotUserService.addUser(it.id)
            }
    }

    private fun geoMessageHandle(message: Message) {
        getPrivateSender(message)
            ?.let { getBotUser(it.id) }
            ?.takeIf {it.state in setOf(BotUserState.STARTED, BotUserState.UNSUBSCRIBED)}
            ?.also {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = sendGeoReply,
                    replyMarkup = ReplyKeyboardRemove()
                )
                BotUserService.updateState(it.id, BotUserState.SET_GEO_POSITION)
            }
    }

    private fun locationHandle(message: Message, location: Location) {
        getPrivateSender(message)
            ?.let { getBotUser(it.id) }
            ?.takeIf {it.state == BotUserState.SET_GEO_POSITION}
            ?.also {
                BotUserService.updateGeoPosition(it.id, location.latitude, location.longitude)
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = gotGeoReply,
                    replyMarkup = subscribeKeyboardReply,
                )
                BotUserService.updateState(it.id, BotUserState.UNSUBSCRIBED)
            }
    }

    private fun subscribeHandle(message: Message) {
        getPrivateSender(message)
            ?.let { getBotUser(it.id) }
            ?.takeIf {it.state == BotUserState.UNSUBSCRIBED}
            ?.also {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = subscribedReply,
                    replyMarkup = unsubscribeKeyboardReply
                )
                BotUserService.updateState(it.id, BotUserState.SUBSCRIBED)
                subscriptionManager.onUserSubscribed()
            }
    }

    private fun unsubscribeHandle(message: Message) {
        getPrivateSender(message)
            ?.let { getBotUser(it.id) }
            ?.takeIf {it.state == BotUserState.SUBSCRIBED}
            ?.also {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = unsubscribedReply,
                    replyMarkup = subscribeKeyboardReply
                )
                BotUserService.updateState(it.id, BotUserState.UNSUBSCRIBED)
                subscriptionManager.onUserUnsubscribed()
            }
    }

    private suspend fun getSolarDataHandle(message: Message) {
        getPrivateSender(message)
            ?.let { getBotUser(it.id) }
            ?.takeIf {it.state in setOf(BotUserState.UNSUBSCRIBED, BotUserState.SUBSCRIBED)}
            ?.also {
                sendSolarData(it)
            }
    }

    private fun String.toLocalTime(zoneId: ZoneId) =
        ZonedDateTime.parse(this).withZoneSameInstant(zoneId).toLocalTime()
    
    suspend fun sendSolarData(user: BotUser) {
        user.geoPosition?.let {
            runCatching {
                Pair(
                    TimeZoneService.getZoneId(it.latitude, it.longitude),
                    SolarService.getSolarInfo(it.latitude, it.longitude)
                )
            }
            .fold(
                onSuccess = {
                    val (zoneId, result) = it
                    if (zoneId != null) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(user.id),
                            text = gotDataReply.format(
                                result.sunrise.toLocalTime(zoneId),
                                result.solarNoon.toLocalTime(zoneId),
                                result.sunset.toLocalTime(zoneId)
                            )
                        )
                    } else {
                        bot.sendMessage(
                            chatId = ChatId.fromId(user.id),
                            text = timezoneErrorReply
                        )
                    }
                },
                onFailure = {
                    it.message?.let { message ->
                        bot.sendMessage(
                            chatId = ChatId.fromId(user.id),
                            text = message
                        )
                    }
                }
            )
        }
    }
}