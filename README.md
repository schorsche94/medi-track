# Medi Track

Medi Track is a Spring Boot application with a Telegram bot for keeping a personal medication list. It stores medications and Telegram users in PostgreSQL and uses Flyway to create and initialize the database schema.

## Features

- Register a user through the bot's `/start` command.
- Add a medication by entering its name, active substance, dosage, form, category, and instructions.
- List medications visible to a user, including shared catalog entries and medications they added.
- Delete a medication added by the current user.
- Create a medication schedule from a medication's menu, including dates, frequency, dose times, intervals, pauses, and intake instructions.

Schedules are stored in PostgreSQL and linked to the selected medication and Telegram user. The “medications for today” view and medication editing are not currently available.

## Requirements

- Java 21
- Maven 3.9+ (or an IDE with Maven support)
- Docker Compose
- A Telegram bot token from [BotFather](https://t.me/BotFather)

## Configuration

Create a `.env` file in the project root. It is excluded from Git; do not commit your bot token or database password.

```properties
TELEGRAM_BOT_TOKEN=your-telegram-bot-token
DB_NAME=medication
DB_USER=medi_user
DB_PASSWORD=choose-a-local-password
```

The PostgreSQL connection in `application.properties` expects database `medication`, user `medi_user`, and port `5438`. Keep the corresponding `DB_NAME` and `DB_USER` values above in sync with those settings if you change them. Docker Compose maps the container's PostgreSQL port to `localhost:5438`.

## Run locally

Start PostgreSQL:

```bash
docker compose up -d postgres_db
```

Start the Spring Boot application from the project root:

```bash
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run` instead. Alternatively, run `com.schorsche94.medi_track.MediTrackApplication` from your IDE. Flyway applies the migrations on startup.

Open Telegram and start a conversation with `@medi_track_bot` using `/start`. Use the medication menu to add or list medications. To schedule a medication, open its menu and select **Schedule medication**, then answer the prompts for treatment duration, start and end dates, daily frequency, scheduled doses, interval, pause, and intake instructions.

For scheduled doses, enter a valid JSON array, for example:

```json
[
  {"time": "08:00", "dosage": "1 tablet"},
  {"time": "20:00", "dosage": "500 mg"}
]
```

The bot rejects malformed JSON and values that are not arrays, and lets you try again. Enter `-` to leave an optional value unchanged or skip it. The bot token must be configured before the application starts.

To stop the local database:

```bash
docker compose down
```

## Build

```bash
./mvnw package
```

On Windows:

```powershell
./mvnw.cmd package
```

## Project layout

- `src/main/java/.../telegram_bot` — Telegram update handling, menus, and medication and schedule conversation flows.
- `src/main/java/.../service` — application services and ownership checks.
- `src/main/java/.../domain` — medication models, JPA entities, repositories, and mappers.
- `src/main/resources/db/migration` — Flyway schema and reference data migrations.
- `src/main/resources/application.properties` — Spring, PostgreSQL, Flyway, and Telegram configuration.

## Notes

- The `medications` seed insert in `V2__seed_medications.sql` is currently commented out. A new database gets the medication forms and categories, but no shared medication catalog entries.
- User-added medications are associated with the Telegram chat ID. Only the user who added a medication can delete it.
- The Compose file also defines a MongoDB service, but the current application flow uses PostgreSQL; MongoDB is not required for the features described above.
