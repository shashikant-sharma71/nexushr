# NexusHR Quick Start

## 1. Start backend (Java 21 + Maven)
```bash
cd backend
mvn clean test
mvn spring-boot:run
```
Backend: http://localhost:8080
Health: http://localhost:8080/actuator/health
Swagger: http://localhost:8080/swagger-ui/index.html
H2 console (local only): http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/nexushr;MODE=PostgreSQL;AUTO_SERVER=TRUE`, user `sa`, blank password)

## 2. Start frontend (Node 22 + npm)
In a second terminal:
```bash
cd frontend
npm install
npm run build
npm run dev
```
Frontend: http://localhost:5173

## 3. Demo logins (local profile only)
- Admin: admin@nexushr.local / Admin@123
- HR: hr@nexushr.local / Hr@12345
- Manager: manager@nexushr.local / Manager@123
- Employee: employee@nexushr.local / Employee@123

## Notes
- Default local profile uses H2. No Docker or PostgreSQL is needed for local development.
- Production profile uses PostgreSQL + Flyway and requires DB credentials, CORS_ORIGINS and a unique JWT_SECRET.
- Never copy real secrets into source control. The archive intentionally does not include a `.env` file.
- AI Insights is a deterministic, explainable demo heuristic, not a trained ML model or an employment decision engine.
- Payroll figures are illustrative and are not statutory payroll calculations.
