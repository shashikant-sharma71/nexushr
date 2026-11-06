# NexusHR --- HR & Workforce Intelligence Platform

NexusHR is a portfolio/demo Human Resource Management System (HRMS)
built to bring common workforce operations into one role-aware web
application. It provides separate experiences for platform
administrators, HR users, managers and employees, with workflows for
employee administration, attendance, leave, payroll examples,
performance reviews, recruitment, holidays, announcements and
notifications.

> **Project scope and safety notice**
>
> NexusHR is an internship/portfolio demonstration, not a certified
> payroll, tax, legal-compliance or enterprise production HR product.
> Payroll calculations and payslips are illustrative and must be checked
> against applicable statutory rules before any real use. AI Insights
> currently uses deterministic heuristic rules, not a trained predictive
> model. Its outputs are explainable signals for human review only and
> must never be used to automate hiring, firing, promotion, compensation
> or other employment decisions.

## 1. Project at a glance

  -----------------------------------------------------------------------
  Item                                Details
  ----------------------------------- -----------------------------------
  Project                             NexusHR --- HR & Workforce
                                      Intelligence Platform

  Type                                Full-stack web application / HRMS
                                      portfolio project

  Frontend                            React 19, Vite 6, JavaScript,
                                      Lucide icons

  Backend                             Java 21, Spring Boot 3.5.5, Spring
                                      Web, Spring Data JPA

  Security                            Spring Security, JWT, BCrypt,
                                      role-based method authorization

  Local database                      H2 (file-backed)

  Production-style database           PostgreSQL 17 with Flyway
                                      migrations

  API documentation                   Springdoc OpenAPI / Swagger UI

  Health monitoring                   Spring Boot Actuator

  Frontend hosting                    Vercel

  Backend hosting                     Render

  Managed database                    Neon PostgreSQL
  -----------------------------------------------------------------------

## 2. Demo access on Live server

The following are the demonstration accounts configured in the hosted
internship workspace. Passwords are deliberately **not published in this
README** because the hosted application is publicly reachable. Share
temporary passwords privately with the evaluator, and change any shared
demo password after evaluation.

  -------------------------------------------------------------------------------------------
  User / Display    User ID / Email       /                Role       /       Password
  name                                                                      
  ----------------- ------------------------------------- ----------------- -----------------
  NexusHR Platform  `shashikantkumarsharma71@gmail.com`   ADMIN            Hacker0987@#
  
  SHASHIKANT SHARMA `shashikantsharma0714@gmail.com`      MANAGER           12345678

  Juhi Sharma       `juhi.hr.test@gmail.com`              HR                12345678

  Abhishek Kumar    `abhishekkumar@gmail.com`             EMPLOYEE          12345678

  Nitish Kumar      `nitishkumar@gmail.com`               EMPLOYEE          12345678

  -------------------------------------------------------------------------------------------

## 2. Demo access on Local Server

The following are the demonstration accounts configured in the hosted
internship workspace. Passwords are deliberately **not published in this
README** because the hosted application is publicly reachable. Share
temporary passwords privately with the evaluator, and change any shared
demo password after evaluation.

  -------------------------------------------------------------------------------------------
  User / Display           User ID / Email       /                Role       /       Password
  name                                                                      
  ----------------- -------------------------------------      ----------------- -----------------
  ADMIN	                    admin@nexushr.local	                  Admin             Admin@123
  HR	                    hr@nexushr.local	                  Hr                Hr@12345
  MANAGER	                manager@nexushr.local	              Manager           Manager@123
  EMPLOYEE	                employee@nexushr.local	              Employee          Employee@123

  -------------------------------------------------------------------------------------------
Role	Email / User ID	Password

## 3. Roles and access model

The application has four primary roles. Navigation is role-scoped in the
frontend, and backend endpoints independently enforce authorization.
Hiding a menu item in the browser is not treated as a security boundary.

  -----------------------------------------------------------------------
  Role                                Main responsibilities
  ----------------------------------- -----------------------------------
  **ADMIN**                           Platform administration and all HR
                                      operations; user and team
                                      administration; role changes;
                                      account activation/deactivation;
                                      administrative audit review.

  **HR**                              Employee, attendance, leave,
                                      payroll, performance and workforce
                                      operations.

  **MANAGER**                         Assigned team views, team
                                      attendance, team leave review and
                                      team performance.

  **EMPLOYEE**                        Own profile, attendance, leave,
                                      payroll and performance
                                      information.
  -----------------------------------------------------------------------

### Important access rules

-   Managers receive limited employee projections for their assigned
    team rather than complete employee entities. This prevents salary
    and other HR-only fields from being exposed in team views.
-   Manager dashboard payroll counts are scoped to that manager's
    assigned team.
-   Employees can access their own records, not other employees' private
    HR information.
-   Notifications are user-scoped: a user can list their own
    notifications and mark only their own notifications as read.
-   Notification responses intentionally omit account credentials.
-   Only Admin/HR can read attendance correction audit history. There is
    no mutation endpoint for audit history.

## 4. Features and modules

### 4.1 Dashboard and role-specific workspace

After authentication, users enter a workspace based on their role. The
dashboard provides relevant shortcuts and workforce information while
respecting the user's access scope. Manager views are limited to
assigned reports, and employee-facing views focus on the signed-in
employee's own information.

### 4.2 User and Team Management --- Admin

The Admin workspace supports:

-   Creating Employee, Manager, HR and Admin accounts.
-   Changing user roles.
-   Activating and deactivating accounts.
-   Assigning employees to active managers.
-   Reviewing recent Admin audit entries.
-   Viewing role, department, reporting manager, account status and team
    assignment.

Operational safeguards:

-   Create Manager accounts before assigning employees to them.
-   A Manager with assigned reports cannot be demoted or deactivated
    until the Admin reassigns those employees.
-   The last active Admin cannot be demoted or deactivated.
-   A manager's team APIs remain scoped to the authenticated manager.

### 4.3 People / Employee Management

Employee and workforce information is organized for HR administration
and employee self-service. Access to sensitive fields is restricted
according to role. Manager team APIs use limited employee projections
instead of returning full employee entities.

### 4.4 Attendance

Attendance supports employee self-service and Admin/HR operations
through a shared transactional service.

Key safeguards:

-   The employee row is locked during check-in/check-out, serializing
    concurrent operations for the same employee.
-   The unique employee/day database constraint remains a final
    duplicate-protection guard.
-   Duplicate check-in or check-out returns **HTTP 409**.
-   Check-out without a check-in returns **HTTP 400**.
-   An unknown employee returns **HTTP 404**.
-   Admin/HR corrections use a separate `PATCH` endpoint, require a
    reason, validate chronological order and work date, and create an
    audit record for each changed timestamp.
-   Attendance correction audit history is readable only by Admin/HR and
    has no mutation endpoint.

Attendance follow-up signals are based on recent late-attendance
entries. Salary is not used as a proxy. These signals require human
review and are not attrition predictions or employment decisions.

### 4.5 Leave Management

The leave workflow supports a two-step approval process:

1.  A Manager reviews a request and may move it to `MANAGER_APPROVED`.
2.  HR/Admin performs the final review and may move it to `APPROVED`.

This separates team-level review from the final HR decision.

### 4.6 Payroll and Payslips

The project includes payroll examples and downloadable PDF payslips.
These are intended to demonstrate HR application workflows and document
generation. They are not a substitute for statutory payroll
calculations, tax advice, accounting controls or a payroll provider.
Placeholder company details must be replaced and all applicable rules
independently validated before any real-world use.

### 4.7 Performance Reviews

The performance area provides a place for performance-related
information and review workflows. Access follows the role model so that
employees see their own information and managers work within their
assigned team.

### 4.8 Attendance Insights / AI Insights

The current workforce signals are deterministic and rule-based. They are
designed to make the reason behind a signal understandable. They are not
produced by a trained predictive model, do not estimate employee
attrition, and must not be used as the sole basis for any employment
decision.

### 4.9 Departments

Department information helps organize employees and supports team and
workforce views.

### 4.10 Recruitment pipeline

Admin/HR can create candidates and move them through these stages:

`APPLIED` → `SCREENING` → `INTERVIEW` → `OFFERED` → `HIRED`

A candidate may also be moved to `REJECTED`. Only Admin can delete
candidate records.

### 4.11 Holiday calendar

Authenticated users can view holidays. Admin/HR can add holidays, and
Admin can remove them.

### 4.12 Announcements

Authenticated users can see announcements addressed to everyone or to
their own role. Admin/HR can publish announcements, and Admin can delete
them.

### 4.13 Notifications inbox

Users can list their own notifications and mark only their own
notifications as read. Per-user announcement notifications and unread
indicators are included. Notification responses intentionally do not
expose account credentials.

## 5. Technology and architecture

NexusHR uses a separated frontend/backend architecture:

``` text
User's browser
     |
     v
React 19 + Vite 6 frontend
     |
     | HTTPS / REST API (JWT)
     v
Java 21 + Spring Boot 3.5.5 backend
     |
     +---- Spring Security / role authorization
     |
     +---- Spring Data JPA
     |
     v
PostgreSQL 17 (hosted on Neon)
     |
     +---- Flyway database migrations
```

Local development uses a file-backed H2 database. The production-style
configuration uses PostgreSQL 17 and Flyway migrations. The frontend and
backend are deployed independently.

### Main technologies

  Technology             Purpose
  ---------------------- --------------------------------------------
  Java 21                Backend language/runtime
  Spring Boot 3.5.5      Backend framework and REST APIs
  Spring Web             HTTP endpoints
  Spring Data JPA        Persistence and repository layer
  Spring Security        Authentication and authorization
  JWT                    Stateless access-token authentication
  BCrypt                 Password hashing
  React 19               Frontend component framework
  Vite 6                 Frontend development server and build tool
  JavaScript             Frontend application logic
  Lucide icons           Interface icons
  H2                     Local file-backed database
  PostgreSQL 17          Production-style relational database
  Flyway                 Versioned database migrations
  Springdoc OpenAPI      API documentation
  Spring Boot Actuator   Runtime health endpoint
  Maven                  Backend build and dependency management
  Node.js / npm          Frontend runtime and package management

## 6. Local setup

### Prerequisites

-   Java 21
-   Maven 3.9+
-   Node.js 22+
-   npm 10+

Local mode uses H2 and does not require Docker or PostgreSQL.

### Step 1 --- Start the backend

Open Terminal 1:

``` bash
cd backend
mvn clean test
mvn spring-boot:run
```

The backend runs at:

`http://localhost:8080`

### Step 2 --- Start the frontend

Open Terminal 2:

``` bash
cd frontend
npm install
npm run build
npm run dev
```

The frontend runs at:

`http://localhost:5173`

### Step 3 --- Open the application

-   Application: `http://localhost:5173`
-   Backend health: `http://localhost:8080/actuator/health`
-   Swagger UI: `http://localhost:8080/swagger-ui/index.html`

The local demo initializer is disabled when the `prod` profile is
active.

## 7. Production-style configuration

The repository includes a Docker Compose configuration for a dedicated
project environment. It starts its own PostgreSQL service and named
volume.

1.  Copy `.env.example` to `.env`.
2.  Replace the database password and JWT secret with unique strong
    values. The JWT secret must be at least 32 characters.
3.  Set `CORS_ORIGINS` to the exact deployed frontend origin(s).
4.  Validate the Compose configuration with `docker compose config`.
5.  Start the stack with `docker compose up --build` only in an
    environment dedicated to NexusHR.

**Isolation warning:** Do not point this Compose setup at another
project's database, container, network or volume. Do not reuse another
project's named volume.

The production profile requires `JWT_SECRET`; it does not fall back to
the local development secret.

## 8. Public deployment

The project has been deployed using:

  -------------------------------------------------------------------------------------------------------
  Component               Platform                Address
  ----------------------- ----------------------- -------------------------------------------------------
  Frontend                Vercel                  https://nexushr-mauve.vercel.app/

  Backend API             Render                  https://nexushr-api-4t3u.onrender.com

  Backend health          Render / Actuator       https://nexushr-api-4t3u.onrender.com/actuator/health

  Database                Neon                    PostgreSQL managed database
  -------------------------------------------------------------------------------------------------------

The repository contains `render.yaml` for the Spring Boot API and
managed PostgreSQL, and `frontend/vercel.json` for the Vite single-page
application.

### Deployment sequence

1.  Push the reviewed project to a Git repository. Do not upload `.env`
    files, credentials, database dumps, `target/` or `node_modules/`.
2.  In Render, create a Blueprint from the repository and review the
    PostgreSQL and Web Service plans before confirming. Hosted resources
    may be billable.
3.  Configure `CORS_ORIGINS` with the exact Vercel production origin.
4.  Set `BOOTSTRAP_ADMIN_EMAIL` and a unique strong
    `BOOTSTRAP_ADMIN_PASSWORD` (12+ characters) for the first production
    boot only.
5.  In Vercel, import the repository and set the Root Directory to
    `frontend`.
6.  Set `VITE_API_URL` to the full Render API origin plus `/api` for the
    required deployment environments.
7.  After the first successful backend boot and Admin login, remove
    `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` from Render.
8.  Verify health, authentication, role permissions, core workflows,
    database persistence after restart, and browser console/network
    errors.

The first-boot bootstrap creates an Admin only when no Admin account
exists. If the configured email already belongs to a non-Admin user,
startup fails rather than promoting or overwriting that account.

## 9. Security and privacy

Security is applied at both the UI and backend layers. The frontend
navigation is role-scoped, while backend authorization independently
protects endpoints and methods.

Current safeguards include:

-   JWT-based authentication.
-   BCrypt password hashing.
-   Role-based method authorization.
-   Manager team data returned as limited projections.
-   Per-user notification access.
-   Attendance correction reasons and audit records.
-   Production profile requiring a configured JWT secret.
-   Production bootstrap safeguards to avoid silently promoting or
    overwriting an existing non-Admin account.

### Known limitations and recommended hardening

This submission does not claim enterprise production certification.
Before using real employee data, the following should be added and
independently verified:

-   Multi-factor authentication (MFA).
-   Invitation lifecycle and forced first-login password changes.
-   Refresh-token rotation and comprehensive session management.
-   Centralized secret storage and regular credential rotation.
-   Rate limiting and abuse protection.
-   Formal payroll/tax rules and expert compliance review.
-   Backup and restore drills.
-   Privacy and data-retention controls.
-   Independent security review and penetration testing.
-   Deployment observability, alerting and incident response.

Email invitations and forced first-login password changes are not
implemented. Accounts are created with an administrator-provided initial
password. Password reset revokes previously issued access tokens using a
per-user token version.

**Never commit or publish real credentials. Rotate any secret that has
ever been committed or shared.** Do not place hosted account passwords
in this public README. Provide temporary evaluator credentials
privately.

## 10. Development challenges and learning

The project involved several practical challenges that are common in
full-stack application development.

### 10.1 Frontend and backend integration

A screen can render correctly while its API call fails. Integration
requires checking the API base URL, request method, headers, response
status and backend logs together. Configuring the frontend API URL
correctly for local and hosted environments was an important part of
deployment.

### 10.2 Authentication versus authorization

A successful login does not automatically grant access to every
endpoint. The backend must verify the token and the user's role for each
protected operation. Role-specific pages and API permissions therefore
need to be tested separately.

### 10.3 Role-scoped data access

Different roles need different levels of access to employee information.
Manager views must not accidentally expose salary or other HR-only
fields. The project addresses this by returning limited employee
projections from manager team APIs and scoping manager dashboard payroll
counts to their assigned team.

### 10.4 Concurrent attendance operations

Two check-in/check-out requests for the same employee can arrive close
together. The attendance service uses a transactional flow and locks the
employee row during the operation, while the unique employee/day
constraint provides an additional database-level safeguard.

### 10.5 Multi-step leave approvals

Leave approval is not a single unrestricted action. The workflow
separates Manager review from final HR/Admin approval, making the
approval responsibility clearer.

### 10.6 Database schema changes

As modules were added, the database schema needed to evolve without
losing consistency. Flyway migrations provide versioned PostgreSQL
schema changes. The local H2 environment uses Hibernate schema update,
while the production-style PostgreSQL database uses migrations.

### 10.7 Deployment configuration

The frontend, backend and database run on separate hosted services.
Correct environment variables, CORS origins, API URLs, database
connection settings and first-boot Admin configuration are all required
for the application to work after deployment.

### 10.8 Security and privacy trade-offs

HR applications handle sensitive employee information. The project
reinforced the importance of returning only the fields a role needs,
protecting notification ownership, keeping credentials out of source
control and clearly communicating that sample payroll and heuristic
insights are not certified decision systems.

### What I learned

-   Building a full-stack application across independent frontend,
    backend and database layers.
-   Designing REST API integration and debugging HTTP errors.
-   Applying authentication and role-based authorization.
-   Structuring relational data and managing schema migrations.
-   Handling transactional workflows and concurrent attendance requests.
-   Deploying a web application using Vercel, Render and Neon.
-   Treating privacy, security, testing and documentation as part of
    software development---not as last-minute additions.

## 11. Testing and verification

The repository includes a scenario matrix, commands, demo checklist and
verification notes in:

`docs/TESTING_AND_SUBMISSION.md`

Production deployment gates and operational checks are documented in:

`docs/PRODUCTION_DEPLOYMENT.md`

The following areas should be verified before a review or release:

-   Backend unit and integration tests.
-   Frontend production build.
-   Login for each role.
-   Access allowed and denied for role-protected endpoints.
-   User creation, role changes and account activation/deactivation.
-   Manager team scoping and sensitive-field protection.
-   Attendance duplicate and invalid-operation responses.
-   Attendance correction reason and audit history.
-   Manager and HR/Admin leave approval stages.
-   Recruitment pipeline transitions.
-   Holiday and announcement permissions.
-   Per-user notification ownership and read status.
-   Payslip generation using sample data only.
-   Database persistence after a backend restart.
-   Browser console and network errors on the hosted application.

Do not interpret the presence of a module in the navigation as proof
that every edge case has passed. Use the verification documents and live
test results for the final status.

## 12. Known limitations

-   This is an internship/portfolio HRMS, not a certified enterprise HR
    product.
-   Payroll and payslips are illustrative and require statutory
    validation before real use.
-   AI Insights are deterministic heuristic signals, not a trained
    predictive model.
-   AI/workforce signals are not suitable for automated employment
    decisions.
-   Email invitations and forced first-login password changes are not
    implemented.
-   MFA, formal compliance review, full operational observability and
    independent penetration testing remain future hardening work.
-   Sample accounts and sample employee/payroll data must not be treated
    as real personnel records.

## 13. Future improvements

Potential next steps include:

1.  Add MFA and a secure account invitation/password onboarding flow.
2.  Add refresh-token rotation and stronger session controls.
3.  Expand automated end-to-end tests for every role and major workflow.
4.  Add audit coverage for all sensitive administrative actions.
5.  Add configurable attendance and leave policies.
6.  Add secure reporting/export with field-level permissions.
7.  Complete backup/restore exercises and operational alerting.
8.  Conduct an independent privacy, security and compliance review.
9.  Validate payroll rules with qualified domain experts before any real
    payroll use.
10. Evaluate any future AI feature for explainability, bias, privacy and
    human oversight before release.

## 14. Project documentation

  -----------------------------------------------------------------------------
  Document                                  Purpose
  ----------------------------------------- -----------------------------------
  `docs/TESTING_AND_SUBMISSION.md`          Test scenarios, commands, demo
                                            checklist and verification status

  `docs/PRODUCTION_DEPLOYMENT.md`           Deployment gates and
                                            production-style deployment checks

  `docs/NOTIFICATIONS_AND_PAYSLIP_V4.md`    V4 notification and payslip details

  `docs/ADMIN_USER_TEAM_MANAGEMENT_V5.md`   V5 Admin user/team management API
                                            details and verification cases
  -----------------------------------------------------------------------------

## 15. Conclusion

NexusHR brings together common HR and workforce workflows in a single
role-aware web application. It demonstrates a modern React frontend
connected to a Java Spring Boot backend, relational persistence,
JWT-based security, database migrations and cloud deployment.

The most valuable part of the work was learning how the pieces behave
together: a frontend feature depends on API correctness, API correctness
depends on authorization and data design, and a deployed system depends
on careful configuration and verification. The project also highlights
the additional responsibility involved in handling employee information
and presenting payroll or workforce insights safely.

NexusHR is submitted as a learning and portfolio project. Its current
capabilities and limitations are documented above so reviewers can
understand both what has been implemented and what would be required
before real organizational use.

------------------------------------------------------------------------

**Project:** NexusHR --- HR & Workforce Intelligence Platform\
**Prepared by:** Shashikant Sharma\
**Programme:** ZIDIO Development Internship\
**Source repository:** https://github.com/shashikant-sharma71/nexushr\
**Live application:** https://nexushr-mauve.vercel.app/\
**Backend health:**
https://nexushr-api-4t3u.onrender.com/actuator/health
