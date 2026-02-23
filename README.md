# Solar Time Bot

Telegram-бот для получения времени восхода, зенита и заката солнца по геолокации пользователя. Поддержка ежедневной рассылки в 00:00 МСК.

## Стек

- Kotlin 1.9, JVM 21
- Kotlin Telegram Bot, Ktor Client
- PostgreSQL, Exposed, HikariCP
- Typesafe Config

## Запуск

### Docker

```bash
# Настроить .env (BOT_TOKEN обязателен)
docker compose up -d
```

### Локально

```bash
./gradlew run
```

