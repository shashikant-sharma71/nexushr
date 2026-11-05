#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
command -v java >/dev/null || { echo 'Java is required (21+).'; exit 1; }
command -v mvn >/dev/null || { echo 'Maven 3.9+ is required. Install Maven, then rerun.'; exit 1; }
command -v node >/dev/null || { echo 'Node.js 22+ is required.'; exit 1; }
command -v npm >/dev/null || { echo 'npm is required.'; exit 1; }
java -version 2>&1 | head -1
node --version
mvn -f "$ROOT/backend/pom.xml" clean test
npm --prefix "$ROOT/frontend" ci
npm --prefix "$ROOT/frontend" audit
npm --prefix "$ROOT/frontend" run build
echo 'NexusHR verification commands completed successfully.'
