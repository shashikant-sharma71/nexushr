# NexusHR — Test & Submission Record

## Verification status for this delivered archive

The source archive was unpacked and inspected. The original ZIP integrity check passed. This working environment has Java 21 and Node 22, but does **not** have Maven installed, has no Maven dependency cache, and cannot resolve external package registries. Therefore Maven/JUnit execution and npm dependency installation/build could not be performed in this environment. The test cases below are included in source but are **not marked as passed**. Run the commands on the target Mac before claiming a green build.

| Check | Status | Evidence / action |
|---|---|---|
| Original uploaded ZIP integrity | PASS | `unzip -t` reported no errors |
| Clean source extraction | PASS | Excluded `__MACOSX`, old `target` artifacts, `.DS_Store`, and uploaded `.env` |
| Backend Maven build | NOT RUN | Maven executable unavailable in build environment |
| Backend JUnit tests | NOT RUN | Maven executable unavailable; tests are included |
| Frontend npm install/build | NOT RUN | Registry DNS unavailable in build environment |
| Local H2 runtime/API smoke tests | NOT RUN | Runtime requires Maven dependencies |
| PostgreSQL/Flyway integration | NOT RUN | No Docker daemon or Maven available in build environment |
| Browser role walkthrough | NOT RUN | Requires running backend and frontend |

## Included automated tests
`backend/src/test/java/com/nexushr/service/AttendanceServiceTest.java` contains nine service tests covering:
1. Check-in creates today's record.
2. Duplicate check-in returns 409 and preserves the original timestamp.
3. Missing employee returns 404.
4. Check-out without check-in returns 400.
5. Duplicate check-out returns 409 and preserves the original timestamp.
6. Attendance correction records actor identity and audit history.
7. Correction rejects check-out earlier than check-in.
8. Correction rejects timestamps outside the attendance work date.
9. Correction rejects requests with no timestamp changes.

`AttendanceAuthorizationTest.java` adds two annotation checks confirming that attendance/audit routes and user provisioning declare ADMIN/HR method-security guards. These are source-level guard checks, not a substitute for live HTTP authorization tests.

`backend/src/test/java/com/nexushr/repo/UserEmployeeRelationshipTest.java` adds three JPA repository tests for the Admin user-directory fetch graph, manager team scoping, and manager employee-profile loading. These tests have been added but not executed in the current build environment.

## Manual scenario matrix

| Area | Scenario | Expected result |
|---|---|---|
| Authentication | Valid Admin/HR/Manager/Employee credentials | JWT issued; role-specific workspace |
| Authentication | Invalid password | 401; no token |
| Authentication | Expired/invalid token | 401; frontend clears session and returns to login |
| Authorization | Employee opens Admin/HR attendance API | 403 |
| Authorization | Manager requests another manager's team | Only own assigned team returned |
| Attendance | First check-in | Record created, status PRESENT or LATE |
| Attendance | Duplicate check-in | 409; original timestamp unchanged |
| Attendance | Check-out before check-in | 400 |
| Attendance | First check-out | Check-out timestamp saved |
| Attendance | Duplicate check-out | 409; original timestamp unchanged |
| Attendance | Unknown employee ID | 404 |
| Attendance | Two simultaneous same-employee operations | Employee-row lock serializes; duplicate action rejected |
| Correction | Admin/HR corrects timestamp with reason | Record updated and audit row created |
| Correction | Missing/blank reason | 400 validation response |
| Correction | Check-out before check-in | 400; no audit row written |
| Correction | Timestamp date differs from attendance work date | 400; no update |
| Correction | Employee/Manager requests correction/history | 403 |
| Correction | History retrieval | Actor, field, old/new value, reason and timestamp returned |
| Leave | Employee creates valid date range | PENDING request linked to own employee profile |
| Leave | End date before start date | 400 |
| Leave | Manager approves own team request | Status updated |
| Payroll | HR generates current period payslip | Illustrative payroll row generated |
| Performance | HR views reviews | Review list returned |
| Departments | HR creates department | Department persisted |
| Health | Actuator health | `{"status":"UP"}` |

## Run verification on Mac

```bash
# Terminal 1 — backend
cd backend
mvn clean test
mvn spring-boot:run
```

```bash
# Terminal 2 — frontend
cd frontend
npm install
npm run build
npm run dev
```

```bash
# Terminal 3 — health smoke check
curl -i http://localhost:8080/actuator/health
```

Before production-style Docker use, set real values in `.env`, then validate the Compose file:

```bash
docker compose config
```

Do not run Docker Compose against another project's environment, volumes, or database.

## Production first-admin bootstrap

For a first production deployment only, provide `BOOTSTRAP_ADMIN_EMAIL` and a unique `BOOTSTRAP_ADMIN_PASSWORD` of at least 12 characters. The production bootstrap creates one ADMIN only when the users table is empty. Remove these variables after the first successful boot. If the database already contains any users, no bootstrap account is created.

## Attendance correction API

`PATCH /api/attendance/{attendanceId}/correction` (ADMIN/HR only):

```json
{
  "checkIn": "2026-11-03T09:05:00",
  "checkOut": "2026-11-03T18:00:00",
  "reason": "Approved correction after manager review"
}
```

At least one timestamp must be supplied. Omit a timestamp to leave that field unchanged. `reason` is required. The actor is taken from the authenticated JWT identity, never from the request body.

`GET /api/attendance/{attendanceId}/history` (ADMIN/HR only) returns the immutable audit history.

## Submission checklist
- [ ] `mvn clean test` passes on Java 21 and test count is reviewed (not executed in current review environment).
- [ ] `npm install` and `npm run build` pass.
- [ ] Local health endpoint returns HTTP 200.
- [ ] Sign in with each of the four local demo roles and verify role-specific navigation/API access.
- [ ] Execute the manual scenario matrix, especially attendance correction and role restrictions.
- [ ] Verify PostgreSQL V1 + V2 + V3 migration in a disposable database only.
- [ ] Replace all secrets and passwords before any deployment.
- [ ] Remove demo users/data before any real deployment.

A convenience runner is included at `scripts/verify.sh`. It runs Maven tests and the frontend production build; it does not start Docker, modify a database, or deploy anything.

## Milestone additions to verify
1. As Admin/HR, create a candidate and change its stage; confirm Employee and Manager receive `403` from `/api/recruitment`.
2. As Admin/HR, add a holiday; confirm all roles can read `/api/holidays`, while only Admin/HR can create and only Admin can delete.
3. As Admin/HR, publish an announcement for `ALL` and another for `MANAGER`; confirm Employee sees only `ALL` and Manager sees both.
4. Confirm `/api/notifications` returns only the signed-in user's notifications and that another user's notification ID returns `403` on `PATCH /api/notifications/{id}/read`.
5. As Manager, inspect `/api/manager/team`, `/attendance`, `/leaves` and `/performance`; responses must not contain salary fields. Confirm team payroll count is team-scoped.
6. Confirm `/api/dashboard/ai-risk` is Admin/HR-only and its signals use late-attendance counts only. Do not use the signal for automated employment decisions.

These new scenarios have been added to the source package but still require execution on the user's isolated local environment before the milestone can be marked verified.


See [FINAL_REVIEW_REPORT.md](FINAL_REVIEW_REPORT.md) for the current review-copy changes, exact verification limitations, and release decision.
