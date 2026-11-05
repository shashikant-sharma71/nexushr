# NexusHR — HR & Workforce Intelligence Platform

NexusHR is a portfolio/demo HRMS with role-scoped workflows for workforce operations. It includes an admin/HR workspace, manager team views, employee self-service, attendance, leave, payroll examples, performance reviews, departments, and explainable workforce signals.

> **Scope notice:** This is a portfolio submission, not a certified payroll, tax, legal-compliance, or production HR product. Payroll calculations are illustrative. AI Insights uses deterministic heuristic rules, not a trained predictive model; outputs must not be used for automated employment decisions.

## Technology
- Frontend: React 19, Vite 6, JavaScript, Lucide icons
- Backend: Java 21, Spring Boot 3.5.5, Spring Web, Spring Data JPA
- Security: Spring Security, JWT, BCrypt, role-based method authorization
- Local database: H2 (file-backed)
- Production-style database: PostgreSQL 17 with Flyway migrations
- API docs: Springdoc OpenAPI / Swagger UI
- Runtime health: Spring Boot Actuator

## Roles
- **ADMIN:** platform administration and all HR operations
- **HR:** employee, attendance, leave, payroll, performance and workforce operations
- **MANAGER:** assigned team, team attendance, team leave approvals and team performance
- **EMPLOYEE:** own attendance, leave, payroll, performance and profile

The UI navigation is role-scoped, and backend endpoints enforce authorization independently.

## Local setup
Requirements: Java 21, Maven 3.9+, Node.js 22+, npm 10+.

Terminal 1 — backend:
```bash
cd backend
mvn clean test
mvn spring-boot:run
```

Terminal 2 — frontend:
```bash
cd frontend
npm install
npm run build
npm run dev
```

Open http://localhost:5173. Backend runs at http://localhost:8080. Health check: http://localhost:8080/actuator/health. OpenAPI UI: http://localhost:8080/swagger-ui/index.html.

Local mode uses H2 and does not require Docker or PostgreSQL. The local demo initializer is disabled when the `prod` profile is active.

## Local demo accounts
- Admin: `admin@nexushr.local` / `Admin@123`
- HR: `hr@nexushr.local` / `Hr@12345`
- Manager: `manager@nexushr.local` / `Manager@123`
- Employee: `employee@nexushr.local` / `Employee@123`

These accounts are local demo fixtures only. Never use these passwords outside a local demo. Production profile does not create demo users. For the first production boot only, set `BOOTSTRAP_ADMIN_EMAIL` and a unique `BOOTSTRAP_ADMIN_PASSWORD` (12+ characters); the bootstrap creates an ADMIN only when no ADMIN account exists. If the configured email already belongs to a non-admin user, startup fails instead of promoting or overwriting that account. Remove both bootstrap variables after the first successful boot.

## Attendance safeguards
- Employee self-service and Admin/HR actions share the same transactional service.
- The employee row is locked during check-in/check-out, serializing same-employee operations; the existing unique employee/day constraint remains the final database guard.
- Duplicate check-in/check-out returns HTTP 409; checkout without check-in returns HTTP 400; unknown employee returns HTTP 404.
- Admin/HR corrections use a separate PATCH endpoint, require a reason, validate chronological order and work date, and create an audit record for each changed timestamp.
- Audit history is readable only by Admin/HR and has no mutation endpoint.

## Production-style Docker setup
1. Copy `.env.example` to `.env`.
2. Replace the database password and JWT secret with unique strong values (JWT secret minimum 32 characters).
3. Set `CORS_ORIGINS` to the exact deployed frontend origin(s).
4. Run `docker compose config` to validate configuration, then `docker compose up --build` only in an environment dedicated to this project.

Docker Compose starts its own PostgreSQL service and named volume. Do not point it at another project's database or reuse another project's volume. Production profile requires `JWT_SECRET`; it does not fall back to the local development secret.

## Security and operational caveats
This submission does not claim enterprise production certification. Email invitations and forced first-login password changes are not implemented; accounts are created with an administrator-provided initial password. Before real employee data is used, add and independently verify MFA, invitation lifecycle, refresh-token rotation, centralized secret storage, rate limiting, formal payroll/tax rules, backup/restore drills, privacy retention controls, security review/penetration testing, and deployment observability. Password reset now revokes previously issued access tokens using a per-user token version. Rotate any secret that has ever been committed or shared.

## Verification
See [docs/TESTING_AND_SUBMISSION.md](docs/TESTING_AND_SUBMISSION.md) for the scenario matrix, commands, demo checklist, and the verification status for this packaged build.

## Additional HR workflows (Milestone update)
- Recruitment pipeline: Admin/HR can create candidates, move candidates through APPLIED, SCREENING, INTERVIEW, OFFERED, HIRED and REJECTED stages; only Admin can delete candidate records.
- Holiday calendar: authenticated users can view holidays; Admin/HR can add them; Admin can remove them.
- Announcements: authenticated users see announcements addressed to everyone or their own role; Admin/HR can publish; Admin can delete.
- Notifications inbox: users can list their own notifications and mark only their own notifications as read. Notification responses intentionally omit account credentials.
- Manager team APIs now return limited employee projections rather than full Employee entities, preventing salary and other HR-only fields from leaking into team views. Manager dashboard payroll counts are scoped to the manager's team.
- Attendance follow-up signals no longer use salary as a proxy. The displayed signal is based only on recent late-attendance entries and must be reviewed with the employee; it is not an attrition prediction or an employment decision.

Production PostgreSQL migration `V3__recruitment_calendar_announcements.sql` adds the new recruitment, holiday and announcement tables. Local H2 uses Hibernate schema update.


## V4 update

See `docs/NOTIFICATIONS_AND_PAYSLIP_V4.md` for per-user announcement notifications, unread indicators, and downloadable PDF payslips. Replace placeholder company details and validate statutory payroll rules before production use.

## V5 — Admin user and team management

The Admin workspace now includes **User & Team Management** for creating Employee, Manager, HR, and Admin accounts, changing roles, activating/deactivating accounts, assigning Employees to active Managers, and reviewing recent Admin audit entries. Manager team APIs remain scoped to the authenticated Manager. Leave approval is a two-step workflow: Manager review (`MANAGER_APPROVED`) followed by HR/Admin final approval (`APPROVED`). See `docs/ADMIN_USER_TEAM_MANAGEMENT_V5.md` for API details and verification cases.

Create Manager accounts before assigning team members. A Manager with assigned reports cannot be demoted or deactivated until the Admin reassigns those employees. The last active Admin cannot be demoted or deactivated.


## Public production deployment (Render + Vercel)

This repository includes `render.yaml` for the Spring Boot API and managed PostgreSQL, and `frontend/vercel.json` for the Vite single-page app. Deploy only after the verification gates in `docs/PRODUCTION_DEPLOYMENT.md` pass. The Render blueprint creates billable hosted resources; review the selected plan and pricing before applying it.

1. Push this reviewed project to a new private GitHub repository. Do not upload `.env` files, credentials, database dumps, `target/`, or `node_modules/`.
2. In Render, create a Blueprint from the repository and review the PostgreSQL and Web Service plans before confirming. Set `CORS_ORIGINS` to the exact Vercel production origin (for example `https://your-app.vercel.app`). Set a unique `BOOTSTRAP_ADMIN_EMAIL` and strong `BOOTSTRAP_ADMIN_PASSWORD` (12+ characters) for the first boot only.
3. In Vercel, import the same repository and set Root Directory to `frontend`. Add `VITE_API_URL` as the full Render API origin plus `/api` (for example `https://nexushr-api.onrender.com/api`) for Production, Preview, and Development as appropriate.
4. After the first successful backend boot and admin login, remove `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` from Render. Do not use local demo credentials in production.
5. Verify health, authentication, role permissions, all core workflows, database persistence after a restart, and browser console/network errors. See `docs/PRODUCTION_DEPLOYMENT.md`.

**Important:** The hosted app is a portfolio/demo HRMS, not a certified payroll, tax, legal-compliance, or enterprise HR system. Do not upload real employee personal or payroll data until a separate security, privacy, backup/restore, and compliance review is completed.
