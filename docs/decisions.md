# Decision log

## 2026-10-12 — Local frontend and XLSX parsing

- The frontend is buildless HTML, CSS, and browser JavaScript because this was explicitly requested. Its API modules still keep HTTP access separate from UI behavior.
- Apache POI is the only capability dependency added beyond the Spring base stack. It is needed to support the required `.xlsx` batch upload alongside CSV.
- The existing `Base-BE` Maven wrapper and project base are retained. New application code lives under `com.learnflow.backend`; legacy `org.hit.chiikaiwabe` source remains in place but is excluded from compilation because it violates the current package, schema, and API conventions.
- MySQL 8.4 runs in Docker only for local development, following the user's database override. Flyway owns schema changes and Hibernate is always set to `validate`.
- MySQL has no `TIMESTAMPTZ` type, so time columns use `TIMESTAMP(6)`, the JDBC/session timezone is forced to UTC, and Java continues to map timestamps as `Instant`.
