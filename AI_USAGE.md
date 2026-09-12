# AI Usage Disclosure

As permitted (and required to be disclosed) by the assessment brief, I used
an AI assistant (Claude, by Anthropic) to help build this project.

**Please personalize this file before submitting** — it's written from the
AI's side of the collaboration; add your own notes on what you understood,
changed, or would explain differently, since the brief asks *you* to be
able to explain and understand all AI-assisted code.

## What the AI generated

- The full Spring Boot backend: entities, DTOs, repositories, services,
  controllers, and global exception handling.
- The business-rule validation logic in `WithdrawalService`.
- The unit tests in `WithdrawalServiceTest`.
- The frontend (`frontend/index.html`) — a single-file HTML/CSS/JS app.
- The seed data in `data.sql` and this README.

## Why it's structured this way

- **DTOs instead of returning entities directly** — keeps the API contract
  stable even if the entity/database shape changes, and avoids leaking JPA
  proxy/lazy-loading details (or the bidirectional `Investor` ↔ `Product`
  relationship) into JSON responses.
- **Business rules live in the service layer, not the controller** —
  `WithdrawalService.validateBusinessRules()` is the single place that
  encodes "retirement needs age > 65", "can't exceed balance", "can't
  exceed 90% of balance", so it can be unit-tested in isolation with
  Mockito (no database needed) and reused if another entry point calls it
  later.
- **`@Transactional` on `submitWithdrawal`** — the balance deduction and
  the `WithdrawalNotice` record are written together; if anything fails
  partway through, neither change is committed.
- **`GlobalExceptionHandler`** — centralizes error formatting so every
  endpoint returns the same JSON error shape, which the frontend relies on
  to show specific validation messages instead of a generic failure.
- **`@ToString.Exclude` / `@EqualsAndHashCode.Exclude` on the
  `Investor.products` / `Product.investor` fields** — without this,
  Lombok's generated `toString()`/`equals()` on the two sides of the
  bidirectional relationship would recurse into each other infinitely.
  This is a real bug worth understanding if you're asked about the code.

## What I'd suggest double-checking / understanding before submitting

- Walk through `WithdrawalService.submitWithdrawal()` line by line — this
  is the core of the assessment and the part most likely to come up in
  follow-up questions.
- Run `mvn test` yourself and read through `WithdrawalServiceTest` to see
  how each business rule is exercised.
- Try the flows manually against the running app (retirement withdrawal
  for an investor under 65, a withdrawal just over 90%, a withdrawal
  exceeding balance) and confirm the error messages make sense to you.
- Consider whether you'd want to change any naming, add more edge-case
  tests, or restructure anything — make it yours.

<!-- Add your own notes below -->

## My own notes (candidate)

-
