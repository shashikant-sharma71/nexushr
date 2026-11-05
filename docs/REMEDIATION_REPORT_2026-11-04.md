# NexusHR remediation report — 2026-11-04

This report describes source changes made to the uploaded archive. It is not a claim that the application has passed runtime or production verification.

## Remediated in source

1. Added explicit `AccessDeniedException` handling returning HTTP 403; added a unit regression test.
2. Configured Spring Security to return HTTP 401 for unauthenticated requests and HTTP 403 for filter-chain access denials.
3. Added server-side stack-trace logging for unexpected API exceptions while keeping generic client responses.
4. Aligned Docker Compose DB variables (`DB_HOST`, `DB_PORT`, `DB_NAME`) with the production datasource; production config also accepts `DB_URL`.
5. Changed frontend Docker build to `npm ci` and the verification script to use `npm ci`.
6. Added the missing root `.env.example` referenced by README.
7. Added the missing Admin User & Team Management V5 documentation referenced by README.
8. Added per-user JWT token versions. Admin password reset increments the version, invalidating tokens previously issued to that user. Added Flyway V6 migration.

## Still incomplete / not safe to claim as fixed

- Email invitations and forced first-login password change are not implemented. Admin-created accounts still use an administrator-supplied initial password.
- Refresh-token rotation is not implemented; this app currently uses short-lived stateless access JWTs and token-version invalidation on password reset.
- Payroll remains illustrative and is not statutory/payroll-compliance software.
- AI insights are deterministic heuristics, not a trained predictive model.

## Verification status

- Source edits applied and reviewed.
- Frontend build was attempted in the Linux audit environment but could not run because the archived `node_modules` lacks the Linux Rollup optional binary. A clean `npm ci` is required on the target machine.
- Maven is unavailable in this audit environment; backend tests and compilation have not been executed here.
- Docker/PostgreSQL runtime and Flyway V1–V6 execution have not been run here.
- End-to-end role authorization must be checked against a running backend before release.

Run `scripts/verify.sh` on the Mac with Java 21, Maven 3.9+, Node 22+, and npm 10+. Then run the PostgreSQL migration/runtime checks described in `docs/PRODUCTION_DEPLOYMENT.md`. Do not deploy until all gates pass.
