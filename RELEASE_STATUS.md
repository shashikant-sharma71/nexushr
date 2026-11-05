# NexusHR release candidate status

**Status: DEPLOYMENT-CONFIGURED / RELEASE VERIFICATION REQUIRED**

This package contains the source project plus production deployment scaffolding for Render (Spring Boot + managed PostgreSQL) and Vercel (Vite frontend).

## Added in this preparation pass
- Render Blueprint for an isolated NexusHR PostgreSQL database and Spring Boot API service.
- Production Spring profile configured for PostgreSQL, Flyway migrations, schema validation, required JWT secret, exact-origin CORS, hidden H2 console, and disabled public OpenAPI docs.
- Vercel SPA rewrite configuration.
- Frontend API base URL no longer defaults to localhost in production builds.
- Production environment variable example and deployment runbook.
- Git ignore rules for environment files, build artifacts, local databases, and hosting metadata.

## Verification completed locally (2026-11-05)
- Backend `mvn clean verify` completed successfully on Java 21.
- JUnit: 23 tests passed, with 0 failures, 0 errors, and 0 skipped.
- Frontend `npm ci --no-audit --no-fund` completed successfully.
- Frontend `npm run build` completed successfully using Vite.

## Not claimed as complete
- Render Docker image build and hosted service startup have not yet been verified.
- Fresh managed PostgreSQL migrations and persistence after redeployment have not yet been verified.
- Live production endpoint, authentication, role authorization, and browser E2E tests have not yet been executed.
- Email invitations, temporary-password delivery, and mandatory first-login password change remain unimplemented.
- Payroll is illustrative; the project is not certified for statutory payroll or real employee-data processing.

Do not call this release production-ready until every checkbox in `docs/PRODUCTION_DEPLOYMENT.md` passes on the actual hosting environment.


## 2026-11-04 audit remediation (source changes; runtime verification pending)
- Added explicit HTTP 403 handling for method-security access-denied exceptions and server-side logging for unexpected exceptions.
- Aligned Docker Compose database variables with the production datasource configuration; production profile also accepts DB_URL.
- Switched frontend Docker build to `npm ci` for lockfile-reproducible dependency installation.
- Added the missing `.env.example` and Admin User/Team Management documentation referenced by README.
- Added per-user JWT token-version validation and Flyway V6 migration so admin password resets revoke previously issued access tokens.
- Email invitation delivery and mandatory first-login password change remain unimplemented and are explicitly documented as such.
- Maven, frontend, PostgreSQL and runtime authorization tests must pass before this package can be called release-ready.
