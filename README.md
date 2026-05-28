# Kotlin Spring Auth (Modular Monolith)

Production-oriented backend built with **Kotlin + Spring Boot 3** using a **modular monolith** architecture.

## Architecture

Code is organized by module under `app/src/main/kotlin/com/example/backend`:

- `shared/`: cross-cutting configuration, exception handling, security helpers, reusable API response model
- `auth/`: registration, login, JWT generation/validation, auth filter
- `user/`: user entity, repository, service, controller, DTOs, mapper
- `application/`: Spring Boot entry point

The project uses layered module internals (`controller`, `service`, `repository`, `entity`, `dto`, `mapper`) to keep responsibilities separated and SOLID-friendly.

## Tech Stack

- Kotlin JVM (Java 17 toolchain)
- Spring Boot 3.3
- Spring Web, Security, Validation, Data JPA
- SQLite (`sqlite-jdbc`) + Hibernate community SQLite dialect
- JWT via `jjwt`
- BCrypt password hashing

## Database

SQLite configuration is in:

- `app/src/main/resources/application.yml`

Defaults:

- DB file: `./data/backend.db`
- Hibernate: `ddl-auto: update`
- Users table includes:
  - `id`
  - `email` (unique)
  - `username` (unique)
  - `password`
  - `role`
  - `created_at`

## Run Locally

```bash
cd /tmp/workspace/gabriel-q7/kotlin-spring-auth
./gradlew :app:bootRun
```

Run tests:

```bash
./gradlew test
```

Build:

```bash
./gradlew build
```

## Security

- Stateless JWT authentication
- Public endpoints:
  - `POST /auth/register`
  - `POST /auth/login`
- All other endpoints require a valid JWT bearer token
- BCrypt is used for password hashing

## API Endpoints

### Auth

#### `POST /auth/register`

Request:

```json
{
  "email": "john@example.com",
  "username": "john_doe",
  "password": "StrongPass123"
}
```

Response (`201 Created`):

```json
{
  "success": true,
  "message": "User registered",
  "data": {
    "token": "<jwt>",
    "tokenType": "Bearer",
    "expiresInSeconds": 3600
  }
}
```

#### `POST /auth/login`

Request:

```json
{
  "email": "john@example.com",
  "password": "StrongPass123"
}
```

Response (`200 OK`):

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "<jwt>",
    "tokenType": "Bearer",
    "expiresInSeconds": 3600
  }
}
```

### User

> Add the Authorization header using the credential returned by login/register.

- `GET /users/me`
- `GET /users/{id}`
- `PUT /users/me`
- `DELETE /users/me`

`PUT /users/me` request example:

```json
{
  "email": "newmail@example.com",
  "username": "new_username",
  "password": "NewStrongPass123"
}
```

## Validation and Error Handling

- Jakarta Validation for email/username/password constraints
- Global `@RestControllerAdvice` returns consistent responses
- Handles:
  - `400` validation errors
  - `401` authentication errors
  - `403` access denied
  - `404` not found
  - `409` duplicate resources
