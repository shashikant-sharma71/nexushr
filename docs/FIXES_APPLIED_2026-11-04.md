# NexusHR review fixes — 2026-11-04

## Changes

- Login now uses `findWithEmployeeByEmailIgnoreCase`, which fetches the linked employee graph before creating the authentication response.
- The authenticated-user repository query now fetches employee, department, and manager associations.
- `/api/me` returns `EmployeeDtos.Profile` rather than serializing a JPA entity/Hibernate proxy.
- Employee/Manager dashboard requests fail with HTTP 409 and a clear profile-link message if the required employee profile is absent. HR/Admin retain the organization-wide dashboard path.
- Non-production demo initialization now runs in one explicit transaction and repairs existing demo employee department/manager links and user-to-employee associations. Existing passwords are not overwritten.
- Added a DTO mapping regression test.

## Isolated local run

Run `./scripts/run-isolated.sh` from this project directory on a machine with Java 21+, Maven, Node.js, npm, and network access for dependency installation. It uses backend port 8085, frontend port 5173, and an in-memory H2 database named `nexushr_isolated_test`. It refuses to start if port 8085 is already occupied. It does not invoke Docker Compose or a production profile.

Demo credentials are the non-production credentials defined in `DataInitializer`; change them before any real deployment.

## Verification limitation in the ZIP-processing environment

The archive was produced on macOS and contains macOS `node_modules`; the Linux build environment lacks the matching Rollup optional binary. A clean `npm ci` could not complete because package registry access timed out. Maven is not installed in the processing environment and Maven installation/package repository access also timed out. Therefore Java compilation, Maven tests, frontend build, and application startup could not be truthfully marked as passed here. Run the script on the user's Mac to perform those checks in the intended isolated environment.
