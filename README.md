# spring-banking-api

> A Spring Boot 3 banking & payments backend with JWT auth, double-entry ledger, idempotent transfers, and PostgreSQL.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-lightgrey?style=flat-square)

---

## Overview

A production-grade REST API that models core banking operations — account management, fund transfers, and a double-entry ledger — built with Spring Boot 3, Java 21 virtual threads, and PostgreSQL.

Key engineering decisions:
- **Double-entry ledger** — every transfer creates two ledger entries (debit + credit), so the books always balance
- **Idempotent transfers** — duplicate requests are safely detected via `Idempotency-Key` header; no double charges
- **ACID transactions** — fund transfers use `@Transactional` with pessimistic row locking to prevent race conditions
- **`BigDecimal` everywhere** — monetary values never touch `double` or `float`
- **Immutable audit trail** — every state change is recorded with actor, timestamp, and before/after snapshot

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2, Java 21 |
| Security | Spring Security + JWT (jjwt 0.12) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Idempotency cache | Redis |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Testing | JUnit 5, Testcontainers |
| Observability | Spring Actuator, Micrometer |

---

## Getting Started

### Prerequisites

- Java 17
- Docker & Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/your-username/spring-banking-api.git
cd spring-banking-api
```

### 2. Start infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL on port `5432` and Redis on port `6379`.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## API Reference

### Authentication

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
```

All other endpoints require a `Bearer` token in the `Authorization` header.

### Accounts

```http
POST   /api/v1/accounts          # Open a new account
GET    /api/v1/accounts/my       # List all accounts for current user
GET    /api/v1/accounts/{id}     # Get account details
PATCH  /api/v1/accounts/{id}/status  # Freeze / close (admin only)
```

### Transfers

```http
POST /api/v1/transfers           # Initiate a fund transfer
GET  /api/v1/transfers/{id}      # Get transfer status
```

**Idempotent transfer request:**

```http
POST /api/v1/transfers
Authorization: Bearer <token>
Idempotency-Key: a8f3e1c2-0d4b-4f7a-9e2d-123456789abc
Content-Type: application/json

{
  "fromAccountNumber": "ACC-00000001",
  "toAccountNumber":   "ACC-00000002",
  "amount":            "250.00",
  "currency":          "USD",
  "description":       "Rent payment"
}
```

Sending the same `Idempotency-Key` twice returns the original response — the transfer is not executed again.

### Transactions

```http
GET /api/v1/accounts/{id}/transactions?page=0&size=20
```

---

## Project Structure

```
src/main/java/com/bank/
├── account/          # Account entity, service, controller, DTOs
├── transaction/      # Transaction + LedgerEntry entities, repository
├── transfer/         # Transfer service (the @Transactional debit+credit core)
├── idempotency/      # Idempotency key filter and Redis store
├── audit/            # Immutable audit log
├── security/         # JWT provider, Spring Security config, User entity
├── exception/        # Global exception handler (@RestControllerAdvice)
└── config/           # Redis config, OpenAPI config

src/main/resources/
├── application.yml
└── db/migration/
    ├── V1__create_users_and_accounts.sql
    ├── V2__create_transactions_and_ledger.sql
    └── V3__create_audit_log.sql
```

---

## How Fund Transfers Work

Every transfer goes through these steps inside a single `@Transactional` method:

```
1. Validate request + check idempotency key
2. Lock both accounts with PESSIMISTIC_WRITE (prevents race conditions)
3. Check sender has sufficient funds
4. Debit sender   → write DEBIT  ledger entry
5. Credit receiver → write CREDIT ledger entry
6. Save transaction as COMPLETED
7. Store idempotency key in Redis (TTL: 24h)
8. Write audit log entry
```

If any step throws, the entire transaction rolls back — balances are never left in an inconsistent state.

---

## Running Tests

```bash
./mvnw test
```

Integration tests use **Testcontainers** to spin up a real PostgreSQL instance — no mocking the database.

---

## Environment Variables

For production, override these via environment variables:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `SPRING_DATA_REDIS_HOST` | Redis host |
| `APP_JWT_SECRET` | 256-bit JWT signing secret |

---

## License

MIT# Banking-systems
A Spring Boot 3 banking &amp; payments backend with JWT auth, double-entry ledger, idempotent transfers, and PostgreSQL.
