package apelsin.space.solar_bot.service

import apelsin.space.solar_bot.db.BotUserRepository
import apelsin.space.solar_bot.model.BotUserState

object BotUserService {
    fun addUser(id: Long) {
        BotUserRepository.addUser(id)
    }
    fun userExists(id: Long) = BotUserRepository.userExists(id)
    fun getUser(id: Long) = BotUserRepository.getUser(id)
    fun updateGeoPosition(id: Long, latitude: Float, longitude: Float) {
        BotUserRepository.updateGeoPosition(id, latitude, longitude)
    }
    fun updateState(id: Long, state: BotUserState) {
        BotUserRepository.updateState(id, state)
    }
    fun getAllSubscribedUsers() = BotUserRepository.getAllSubscribedUsers()
    fun countSubscribedUsers() = BotUserRepository.countSubscribedUsers()
}