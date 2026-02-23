package apelsin.space.solar_bot.telegram

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton

const val sendGeoMessage = "Отправить местоположение"
const val subscribeMessage = "Подписаться"
const val getSolarDataMessage = "Получить информацию"
const val unsubscribeMessage = "Отписаться"

const val startReply = """
👋 Привет! Меня зовут Solar Bot!

Я помогу тебе узнать:
🌅 время восхода солнца  
☀️ солнечный полдень (зенит)  
🌇 время заката  

Чтобы начать работу, отправь своё местоположение 📍
"""


const val sendGeoReply = """
📍 Пожалуйста, отправь своё местоположение через геометку в Telegram.
"""


const val gotGeoReply = """
✅ Отлично! Я получил твоё местоположение.

Теперь ты можешь:
🔔 Подписаться на ежедневную рассылку
☀️ Получить данные о времени солнца прямо сейчас
"""


const val gotDataReply = """
🌞 Данные по солнцу на сегодня в твоем городе:

🌅 Восход: %s
☀️ Зенит: %s
🌇 Закат: %s
"""

const val timezoneErrorReply = """
❌ Не удалось определить часовой пояс для твоей геолокации.
Попробуй отправить местоположение заново.
"""

const val subscribedReply = """
🔔 Подписка активирована!

Я буду отправлять данные каждый день в 00:00 по МСК.
"""

const val unsubscribedReply = """
❌ Подписка отключена.

Ты всегда можешь подписаться снова.
"""


val setGeoPositionKeyboardReply = KeyboardReplyMarkup(keyboard = listOf(
    listOf(KeyboardButton(sendGeoMessage))
),
    resizeKeyboard = true
)
val subscribeKeyboardReply = KeyboardReplyMarkup(keyboard = listOf(
    listOf(KeyboardButton(subscribeMessage)),
    listOf(KeyboardButton(getSolarDataMessage)),
    listOf(KeyboardButton(sendGeoMessage))
),
    resizeKeyboard = true
)
val unsubscribeKeyboardReply = KeyboardReplyMarkup(keyboard = listOf(
    listOf(KeyboardButton(unsubscribeMessage))
),
    resizeKeyboard = true
)