#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND="$ROOT/backend"
FRONTEND="$ROOT/frontend"
LOG_DIR="$ROOT/.runtime-logs"
mkdir -p "$LOG_DIR"

for command in java mvn node npm curl; do
  command -v "$command" >/dev/null 2>&1 || { echo "Missing required command: $command" >&2; exit 1; }
done

JAVA_MAJOR="$(java -version 2>&1 | awk -F '[".]' '/version/ {print $2; exit}')"
if [[ "${JAVA_MAJOR:-0}" -lt 21 ]]; then
  echo "Java 21 or newer is required." >&2
  exit 1
fi

BACKEND_PORT=8085
FRONTEND_PORT=5173
if curl -fsS "http://127.0.0.1:${BACKEND_PORT}/actuator/health" >/dev/null 2>&1; then
  echo "Port ${BACKEND_PORT} already serves an application; refusing to interfere." >&2
  exit 1
fi
if lsof -nP -iTCP:"${BACKEND_PORT}" -sTCP:LISTEN >/dev/null 2>&1; then
  echo "Port ${BACKEND_PORT} is occupied; stop and inspect that process before continuing." >&2
  exit 1
fi

if [[ ! -d "$FRONTEND/node_modules" ]]; then
  (cd "$FRONTEND" && npm ci)
fi

(cd "$BACKEND" && mvn -q test package -DskipTests=false)

(
  cd "$BACKEND"
  PORT="$BACKEND_PORT" \
  DB_URL='jdbc:h2:mem:nexushr_isolated_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE' \
  DB_USERNAME=sa DB_PASSWORD= \
  DDL_AUTO=create-drop \
  SPRING_FLYWAY_ENABLED=false \
  mvn -q spring-boot:run
) >"$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
cleanup() {
  kill "$BACKEND_PID" 2>/dev/null || true
  wait "$BACKEND_PID" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

ready=0
for _ in $(seq 1 60); do
  if curl -fsS "http://127.0.0.1:${BACKEND_PORT}/actuator/health" >/dev/null 2>&1; then ready=1; break; fi
  if ! kill -0 "$BACKEND_PID" 2>/dev/null; then break; fi
  sleep 2
done
if [[ "$ready" -ne 1 ]]; then
  echo "Backend did not become healthy. Recent log output:" >&2
  tail -80 "$LOG_DIR/backend.log" >&2
  exit 1
fi

echo "Backend healthy at http://localhost:${BACKEND_PORT}"
echo "Frontend at http://localhost:${FRONTEND_PORT}"
echo "Isolated H2 in-memory database; no Docker or production DB is used."
(cd "$FRONTEND" && VITE_API_URL="http://localhost:${BACKEND_PORT}/api" npm run dev -- --host 127.0.0.1 --port "$FRONTEND_PORT")
