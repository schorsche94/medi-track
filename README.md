# Medi Track

Medi Track is a Spring Boot application with a Telegram bot for tracking medications, treatment schedules, and daily doses. PostgreSQL stores users, medications, and schedules; MongoDB stores daily medication intakes; Kafka delivers due reminders to the bot.

## Features

- Register a Telegram user with `/start` and open the medication menu.
- Add, view, edit, and delete medications. A medication includes its name, active substance, strength, form, category, and personal instructions.
- Create, view, and delete medication schedules with treatment dates, dose times and amounts, interval, pause, intake instructions, and status.
- View today's doses and mark each dose as taken or skipped.
- Send Telegram reminders for scheduled doses.
- Hide the reply keyboard after saving a medication schedule.

Medication forms and categories are loaded by Flyway. The migration currently does not add sample medications, so add a medication in the bot to get started.

## Technology

- Java 21 and Spring Boot
- Telegram Bots long polling
- PostgreSQL with Spring Data JPA and Flyway
- MongoDB for intake records
- Kafka for reminder delivery
- Docker Compose for local infrastructure or the full application stack

## Requirements

- Java 21 and Maven 3.9+ to run the app from your machine
- Docker Compose
- A Telegram bot token created through [BotFather](https://t.me/BotFather)

## Configuration

Create a `.env` file in the project root. It is ignored by Git; keep your bot token and database password private.

```properties
TELEGRAM_BOT_TOKEN=your-telegram-bot-token
DB_NAME=medication
DB_USER=medi_user
DB_PASSWORD=choose-a-local-password
```

The default local application settings use PostgreSQL at `localhost:5438` with database `medication`, user `medi_user`, and password `medi_pwd`. MongoDB uses `localhost:27017` and the `DB_USER` and `DB_PASSWORD` values from `.env` (defaulting to `medi_user` and `medi_pwd`). Kafka is expected at `localhost:9092`.

For a local run, either use those database credentials or override the Spring connection settings with environment variables such as `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`. The Docker Compose app uses the `docker` Spring profile and connects to the services by their Compose hostnames.

## Run with Docker Compose

The Compose stack builds and starts the application, PostgreSQL, MongoDB, Kafka, and Kafka UI. Set `TELEGRAM_BOT_TOKEN` in `.env` before starting:

```bash
docker compose up --build -d
```

Useful local endpoints:

| Service | Address |
| --- | --- |
| Application | `http://localhost:8080` |
| PostgreSQL | `localhost:5438` |
| MongoDB | `localhost:27017` |
| Kafka | `localhost:9092` |
| Kafka UI | `http://localhost:8081` |

View application logs with `docker compose logs -f app`. Stop the services with `docker compose down`. Named volumes retain database and Kafka data; add `-v` to `down` only if you intend to delete that data.

## Run the application from your machine

Start PostgreSQL, MongoDB, and Kafka from Compose:

```bash
docker compose up -d postgres_db mongodb kafka
```

Then start the Spring Boot application in the default profile:

```bash
./mvnw spring-boot:run
```

On Windows, use:

```powershell
./mvnw.cmd spring-boot:run
```

Flyway applies the PostgreSQL migrations on application startup. The Telegram token must be available as `TELEGRAM_BOT_TOKEN` (for example, in the root `.env` file).

## Use the Telegram bot

Open a conversation with `@medi_track_bot` and send `/start`. Choose **Medication menu** to add or list medications. From a medication card, choose **Schedule medication** and follow the prompts.

For a schedule, enter a positive treatment duration in days or `-` to provide an end date instead. Enter a start date in `YYYY-MM-DD` format; it can be today or a future date. When duration is supplied, the end date is calculated inclusively from the start date. Otherwise, provide an end date after the start date. Enter scheduled doses as a JSON array of `time` and `dosage` objects, for example:

```json
[
  {"time": "08:00", "dosage": "1 tablet"},
  {"time": "20:00", "dosage": "500 mg"}
]
```

The dose times use 24-hour `HH:mm` format. The bot checks that the dose value is a JSON array; each entry should include a valid time and a dose description. Use `-` at optional prompts to keep the current value while editing or to skip it when creating a schedule. Choose an `ACTIVE` schedule to receive reminders. After a schedule is saved, the bot hides the reply keyboard.

You can open today's dose list from the main menu or send `/show_today_medication_list`. Each dose can be marked **Medication taken** or **Medication skipped**.

## Scheduling and reminders

At startup, and every day at midnight in `Europe/Chisinau`, the application prepares that day's intake records. If it is 23:50 or later when the app starts, it also prepares the next day's records. Active schedules that cover the date are used to create the intake records.

Every minute, due pending intakes are published to the Kafka topic `medication-events`. The consumer sends a Telegram reminder with the medication, dose, scheduled time, and any schedule instructions. Schedule times are interpreted in `Europe/Chisinau`; the today's list displays its scheduled timestamps in UTC.

## Build and tests

Package the application:

```bash
./mvnw package
```

On Windows:

```powershell
./mvnw.cmd package
```

Run the test suite with `./mvnw test` or `./mvnw.cmd test` on Windows. The current test suite includes an application context startup test.

## Project layout

- `src/main/java/.../telegram_bot` — Telegram update handling, menus, and conversation flows.
- `src/main/java/.../service` — medication, schedule, intake, and daily schedule logic.
- `src/main/java/.../domain` — PostgreSQL entities and repositories, domain models, and MongoDB documents.
- `src/main/java/.../schedule` — daily schedule preparation and due-intake dispatch jobs.
- `src/main/java/.../kafka` — reminder event producer and Telegram reminder consumer.
- `src/main/resources/db/migration` — Flyway schema and reference data migrations.
- `src/main/resources/application*.properties` — default and Docker-profile configuration.
- `docker-compose.yaml` and `Dockerfile` — local services and containerized application.
