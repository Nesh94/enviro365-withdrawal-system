# Enviro365 Investments — Withdrawal Notice System

A full-stack system for Enviro365 Investments' automated withdrawal notice
process: investors can view their portfolio, submit withdrawal notices,
have them checked against business rules, and export a CSV statement of
their withdrawal history.

Built for the junior developer assessment (Section 01–03 of the brief).

## ⚠️ Before you submit: rename the package

The assessment asks for the package `com.enviro.assessment.junior.yourname`.
Everything here uses the placeholder `yourname` — replace it with your own
name before submitting. The quickest way:

```bash
# from the project root
find src -type f -name "*.java" -exec sed -i 's/junior\.yourname/junior.yourfirstname/g' {} +
mv src/main/java/com/enviro/assessment/junior/yourname src/main/java/com/enviro/assessment/junior/yourfirstname
mv src/test/java/com/enviro/assessment/junior/yourname src/test/java/com/enviro/assessment/junior/yourfirstname
```

(On macOS, `sed -i` needs a backup suffix: `sed -i '' 's/.../.../g'`.)

## Stack

- **Backend**: Java 17, Spring Boot 3.3, Spring Data JPA, Bean Validation, H2 (in-memory)
- **Frontend**: plain HTML/CSS/JS (no build step) — see `frontend/index.html`
- **Tests**: JUnit 5 + Mockito (`WithdrawalServiceTest`)

## Project structure

```
src/main/java/com/enviro/assessment/junior/yourname/
├── entity/          Investor, Product, WithdrawalNotice (JPA entities)
├── dto/              PortfolioDTO, ProductDTO, WithdrawalRequestDTO, WithdrawalResponseDTO, InvestorSummaryDTO
├── repository/       Spring Data JPA repositories
├── service/          PortfolioService, WithdrawalService (business rules), CsvExportService
├── controller/       PortfolioController, WithdrawalController, ReportController
├── exception/        GlobalExceptionHandler + custom exceptions + ErrorResponse
└── enums/            WithdrawalType, WithdrawalStatus
src/main/resources/
├── application.properties   H2 + JPA config
└── data.sql                 seed data (4 investors, 5 products, 2 sample withdrawals)
src/test/java/.../service/
└── WithdrawalServiceTest.java
frontend/
└── index.html        portfolio dashboard, withdrawal form, history table, CSV download
```

## Running the backend

Requires JDK 17+ and Maven.

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8081`. The H2 console is available at
`http://localhost:8081/h2-console` (JDBC URL `jdbc:h2:mem:enviro365db`,
user `sa`, blank password) if you want to inspect the seeded data.

Run the unit tests:

```bash
mvn test
```

> **Note:** I built and reviewed this code carefully but could not run
> `mvn` against Maven Central inside the sandbox I wrote it in (no network
> access to repo.maven.apache.org there), so please run `mvn test` and
> `mvn spring-boot:run` yourself as a first step and let me know if
> anything doesn't compile — happy to fix it immediately.

## Running the frontend

`frontend/index.html` is a static file with no build step — just open it
in a browser (or serve it with any static file server) once the backend
is running. It calls the API at `http://localhost:8081/api`, configurable
via the `API_BASE` constant near the top of the `<script>` block.

## API reference

| Method | Path                                  | Purpose                                   |
|--------|----------------------------------------|--------------------------------------------|
| GET    | `/api/investors`                      | List investors (id + name), for the picker |
| GET    | `/api/portfolio/{investorId}`         | Investor details + their products          |
| POST   | `/api/withdrawals`                    | Submit a withdrawal notice                  |
| GET    | `/api/withdrawals?investorId=&status=&withdrawalType=&startDate=&endDate=` | Filtered withdrawal history |
| GET    | `/api/reports/withdrawals/csv?...`    | Same filters, as a CSV download             |

`POST /api/withdrawals` body:
```json
{ "productId": 1, "amount": 5000.00, "withdrawalType": "STANDARD" }
```

## Business rules (`WithdrawalService`)

Applied in this order, each with its own error message:

1. **Retirement withdrawals only allowed if age > 65** — checked against
   the investor's date of birth.
2. **Withdrawal must not exceed the product balance.**
3. **Withdrawal must not exceed 90% of the product balance.**

A request that passes all three deducts the amount from the product
balance and is recorded as `APPROVED`. A request that fails any rule is
rejected with a `400` and a specific message — no partial state is
written (the balance update and the notice record happen in one
`@Transactional` method).

## Advanced requirements implemented (3+ required)

- ✅ **Global exception handling** — `GlobalExceptionHandler` (`@RestControllerAdvice`) turns
  `ResourceNotFoundException`, `InvalidWithdrawalException`, and bean-validation failures into
  a consistent JSON error shape (`ErrorResponse`), instead of raw stack traces.
- ✅ **DTO layer** — entities are never returned directly from controllers; every response goes
  through a DTO (`PortfolioDTO`, `WithdrawalResponseDTO`, etc.).
- ✅ **Input validation** — Bean Validation annotations on `WithdrawalRequestDTO`
  (`@NotNull`, `@DecimalMin`) plus business-rule validation in the service layer.
- ✅ **Unit tests** — `WithdrawalServiceTest` covers all three business rules (pass/fail cases,
  including the boundary at exactly 90%) plus the not-found path, using Mockito to isolate the
  service from the database.
- ✅ **UI validation** — the withdrawal form uses HTML5 `required`/`min`/`step` constraints, shows
  the product's available balance and 90% cap inline, and surfaces backend validation errors
  (including field-level messages) without a page reload.

## AI usage disclosure

See [`AI_USAGE.md`](./AI_USAGE.md) — this project was built with AI assistance (Claude), disclosed
as required by the assessment brief, with an explanation of what was generated and why each
design/business-rule decision was made.
