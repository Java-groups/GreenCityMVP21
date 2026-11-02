<p align="center">
  <img src="https://raw.githubusercontent.com/ita-social-projects/GreenCity/master/docs-photos/GreenCity%20Logo.png" alt="GreenCity" width="320">
</p>

<h1 align="center">GreenCity</h1>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License: MIT"></a>
  <img src="https://img.shields.io/badge/Java-21-437291?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3.5">
  <img src="https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white" alt="PostgreSQL 16">
  <img src="https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white" alt="Docker">
</p>

<p align="center">
  Backend of the GreenCity learning project: eco habits, eco news, and the related REST API.<br>
  Students run it locally. The code comes from GreenCity (SoftServe IT Academy).
</p>

## Modules

| Module | Contents |
| --- | --- |
| `service-api` | DTOs and shared contracts |
| `dao` | entities, repositories, Liquibase migrations |
| `service` | business logic |
| `core` | Spring Boot application. Entry point: `greencity.GreenCityApplication` |

## Prerequisites

- JDK 21
- Docker Desktop

A separate Maven install is unnecessary. The repository includes `mvnw` and `mvnw.cmd`.

## Database and files

```bash
docker compose up -d db azurite
```

PostgreSQL listens on `localhost:5432`. Database, user, and password are `greencity`. Liquibase creates the schema and seed data on the first application start.

Azurite listens on port `10000` and stores uploaded images. It is a local Blob Storage emulator, so no Azure account is required. The `dev` profile already points at it:

```text
AZURE_CONNECTION_STRING=UseDevelopmentStorage=true
AZURE_CONTAINER_NAME=images
```

`UseDevelopmentStorage=true` is the emulator shortcut for `http://127.0.0.1:10000/devstoreaccount1` and the public Azurite account `devstoreaccount1`. Override both variables only when you want another storage account. `AzureCloudStorageService` creates the `images` container on the first upload and makes blobs readable by URL.

Run the application from the IDE so it can reach Azurite on `127.0.0.1`. A successful upload returns a URL like `http://127.0.0.1:10000/devstoreaccount1/images/...`.

## Run from the IDE

The default profile is `dev`. Set these environment variables before starting:

```text
DATASOURCE_URL=jdbc:postgresql://localhost:5432/greencity
DATASOURCE_USER=greencity
DATASOURCE_PASSWORD=greencity
```

Open the project as a Maven project, select JDK 21, and run `greencity.GreenCityApplication` from the `core` module.

Swagger UI: http://localhost:8080/swagger-ui/index.html

Public GET methods (habits, eco news, categories, tags) work without a token. Protected methods expect `Authorization: Bearer <access token>` for a user that already exists in the database. Liquibase inserts the service account `green.city.ubs@gmail.com`. Sign-up and sign-in (`/ownSecurity`) live in a separate user service at `greencityuser.server.address` (default `http://localhost:8060`).

`EMAIL_ADDRESS`, `EMAIL_PASSWORD`, and `GOOGLE_CLIENT_ID` are optional for a local start. They are used for confirmation email and Google sign-in.

## Run the application in Docker

Build the jar, then start the application together with the database:

```bash
mvnw.cmd -B -pl core -am package -DskipTests
docker compose up --build app
```

On macOS and Linux use `./mvnw` instead of `mvnw.cmd`. The application container listens on http://localhost:8080 and uses the `docker` profile. Image upload from that container still expects Azurite on the host, so use the IDE run when you need file upload.

## Tests and code style

Tests start PostgreSQL with Testcontainers, so Docker must be running.

```bash
mvnw.cmd -B verify
```

Checkstyle (`checkstyle.xml`) and the formatter (`formatter.xml`) run in the `validate` phase, which `verify` includes.

## CI

[`.github/workflows/main.yml`](.github/workflows/main.yml) builds the project, runs tests, and builds the local image `greencity:local` on every push and pull request. The image is not published.
