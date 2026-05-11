#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
ROOT_DIR="$(cd -- "$BACKEND_DIR/.." && pwd)"
FRONTEND_DIR="${FRONTEND_DIR:-$ROOT_DIR/cpmss-front}"

STATE_DIR="$BACKEND_DIR/.dev-stack"
PID_DIR="$STATE_DIR/pids"
LOG_DIR="$STATE_DIR/logs"
CREDENTIALS_FILE="$STATE_DIR/admin-credentials.env"

BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"
FRONTEND_URL="${FRONTEND_URL:-http://localhost:3000}"

mkdir -p "$PID_DIR" "$LOG_DIR"

log() {
  printf '%s\n' "$*"
}

die() {
  printf 'ERROR: %s\n' "$*" >&2
  exit 1
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || die "Required command not found: $1"
}

check_layout() {
  [[ -f "$BACKEND_DIR/gradlew" ]] || die "Backend gradlew not found at $BACKEND_DIR/gradlew"
  [[ -d "$FRONTEND_DIR" ]] || die "Frontend directory not found at $FRONTEND_DIR"
  [[ -f "$FRONTEND_DIR/package.json" ]] || die "Frontend package.json not found at $FRONTEND_DIR/package.json"
}

require_basics() {
  require_command docker
  require_command curl
  require_command npm
  docker compose version >/dev/null 2>&1 || die "Docker Compose plugin is not available"
  check_layout
}

load_backend_env() {
  if [[ -f "$BACKEND_DIR/.env" ]]; then
    set -a
    # shellcheck source=/dev/null
    . "$BACKEND_DIR/.env"
    set +a
  fi

  export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"
  export DB_URL="${DB_URL:-jdbc:postgresql://127.0.0.1:5433/cpmss}"
  export DB_USERNAME="${DB_USERNAME:-cpmss_user}"
  export DB_PASSWORD="${DB_PASSWORD:-cpmss_pass}"
  export MINIO_ROOT_USER="${MINIO_ROOT_USER:-minioadmin}"
  export MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD:-minioadmin}"
  export MINIO_ENDPOINT="${MINIO_ENDPOINT:-http://localhost:9000}"
  export MINIO_BUCKET="${MINIO_BUCKET:-cpmss-files}"
  export JWT_SECRET="${JWT_SECRET:-cpmss-dev-secret-key-not-for-production-at-least-256-bits-long-padding}"
}

http_ok() {
  local url="$1"
  curl -fsSL --max-time 5 "$url" >/dev/null 2>&1
}

wait_for_url() {
  local url="$1"
  local name="$2"
  local timeout_seconds="$3"
  local start
  start="$(date +%s)"

  while true; do
    if http_ok "$url"; then
      log "$name is ready: $url"
      return 0
    fi

    if (( "$(date +%s)" - start >= timeout_seconds )); then
      log "$name did not become ready in ${timeout_seconds}s."
      return 1
    fi

    sleep 2
  done
}

wait_for_postgres() {
  local timeout_seconds=90
  local start
  start="$(date +%s)"

  while true; do
    if (cd "$BACKEND_DIR" && docker compose exec -T postgres pg_isready -U "${DB_USERNAME:-cpmss_user}" >/dev/null 2>&1); then
      log "PostgreSQL is ready."
      return 0
    fi

    if (( "$(date +%s)" - start >= timeout_seconds )); then
      die "PostgreSQL did not become ready in ${timeout_seconds}s."
    fi

    sleep 2
  done
}

wait_for_minio() {
  local health_url="${MINIO_ENDPOINT%/}/minio/health/live"
  wait_for_url "$health_url" "MinIO" 90 || die "MinIO health check failed."
}

stop_pid_file() {
  local file="$1"
  local label="$2"
  local pid

  if [[ ! -f "$file" ]]; then
    return 0
  fi

  pid="$(cat "$file" 2>/dev/null || true)"
  rm -f "$file"

  if [[ -z "$pid" ]]; then
    return 0
  fi

  if kill -0 "$pid" 2>/dev/null; then
    log "Stopping $label pid $pid..."
    kill "$pid" 2>/dev/null || true
    sleep 2
    if kill -0 "$pid" 2>/dev/null; then
      kill -9 "$pid" 2>/dev/null || true
    fi
  fi
}

kill_port_if_available() {
  local port="$1"

  if command -v fuser >/dev/null 2>&1; then
    fuser -k "${port}/tcp" >/dev/null 2>&1 || true
  fi
}

ensure_frontend_env() {
  cat > "$FRONTEND_DIR/.env.local" <<ENVLOCAL
NEXT_PUBLIC_API_BASE_URL=$BACKEND_URL
ENVLOCAL
}

start_docker() {
  load_backend_env
  log "Starting PostgreSQL and MinIO..."
  (cd "$BACKEND_DIR" && docker compose up -d postgres minio)
  wait_for_postgres
  wait_for_minio
}

start_backend() {
  load_backend_env

  if http_ok "$BACKEND_URL/v3/api-docs"; then
    log "Backend is already running: $BACKEND_URL"
    return 0
  fi

  log "Starting Spring Boot backend..."
  (
    cd "$BACKEND_DIR"
    load_backend_env
    exec ./gradlew bootRun
  ) >"$LOG_DIR/backend.log" 2>&1 &

  printf '%s\n' "$!" > "$PID_DIR/backend.pid"

  if ! wait_for_url "$BACKEND_URL/v3/api-docs" "Spring Boot" 180; then
    log "Backend log:"
    tail -n 120 "$LOG_DIR/backend.log" || true
    die "Backend failed to start."
  fi
}

start_frontend() {
  ensure_frontend_env

  if http_ok "$FRONTEND_URL"; then
    log "Frontend is already running: $FRONTEND_URL"
    return 0
  fi

  if [[ ! -d "$FRONTEND_DIR/node_modules" ]]; then
    log "Frontend node_modules missing. Running npm install..."
    (cd "$FRONTEND_DIR" && npm install)
  fi

  log "Starting Next.js frontend..."
  (
    cd "$FRONTEND_DIR"
    exec npm run dev
  ) >"$LOG_DIR/frontend.log" 2>&1 &

  printf '%s\n' "$!" > "$PID_DIR/frontend.pid"

  if ! wait_for_url "$FRONTEND_URL" "Next.js" 120; then
    log "Frontend log:"
    tail -n 120 "$LOG_DIR/frontend.log" || true
    die "Frontend failed to start."
  fi
}

run_command() {
  require_basics
  start_docker
  start_backend
  start_frontend

  log
  log "Full stack is running."
  log "Backend:  $BACKEND_URL"
  log "Swagger:  $BACKEND_URL/swagger-ui.html"
  log "OpenAPI:  $BACKEND_URL/v3/api-docs"
  log "Frontend: $FRONTEND_URL"
}

stop_command() {
  require_basics

  log "Stopping frontend/backend processes..."
  stop_pid_file "$PID_DIR/frontend.pid" "frontend"
  stop_pid_file "$PID_DIR/backend.pid" "backend"

  kill_port_if_available 3000
  kill_port_if_available 8080

  log "Stopping Gradle daemons..."
  (cd "$BACKEND_DIR" && ./gradlew --stop) || true

  log "Stopping Docker services..."
  (cd "$BACKEND_DIR" && docker compose down) || true

  log "Stopped."
}

clean_rebuild_command() {
  require_basics

  log "Stopping everything and removing Docker volumes..."
  stop_pid_file "$PID_DIR/frontend.pid" "frontend"
  stop_pid_file "$PID_DIR/backend.pid" "backend"
  kill_port_if_available 3000
  kill_port_if_available 8080
  (cd "$BACKEND_DIR" && ./gradlew --stop) || true
  (cd "$BACKEND_DIR" && docker compose down -v --remove-orphans)

  log "Cleaning backend..."
  (cd "$BACKEND_DIR" && ./gradlew clean test)

  log "Cleaning frontend build cache..."
  rm -rf "$FRONTEND_DIR/.next"

  log "Installing frontend dependencies..."
  (cd "$FRONTEND_DIR" && npm install)

  log "Linting frontend..."
  (cd "$FRONTEND_DIR" && npm run lint)

  log "Building frontend..."
  (cd "$FRONTEND_DIR" && npm run build)

  log "Clean rebuild completed."
}

generate_password() {
  local token

  if command -v openssl >/dev/null 2>&1; then
    token="$(openssl rand -hex 6)"
  else
    token="$(date +%s)"
  fi

  printf 'AdminPass-%s!\n' "$token"
}

save_credentials() {
  local email="$1"
  local password="$2"

  {
    printf 'ADMIN_EMAIL=%q\n' "$email"
    printf 'ADMIN_PASSWORD=%q\n' "$password"
  } > "$CREDENTIALS_FILE"

  chmod 600 "$CREDENTIALS_FILE"
}

print_credentials() {
  if [[ ! -f "$CREDENTIALS_FILE" ]]; then
    die "No saved local admin credentials found."
  fi

  # shellcheck source=/dev/null
  . "$CREDENTIALS_FILE"

  log "Saved local admin credentials:"
  log "Email:    ${ADMIN_EMAIL:-}"
  log "Password: ${ADMIN_PASSWORD:-}"
}

auto_set_command() {
  require_basics

  log "auto-set includes run: starting stack first if needed..."
  run_command

  local email
  local password
  local payload
  local response_file
  local status

  email="${ADMIN_EMAIL:-admin@compound.com}"

  if [[ -f "$CREDENTIALS_FILE" ]]; then
    # shellcheck source=/dev/null
    . "$CREDENTIALS_FILE"
    email="${ADMIN_EMAIL:-$email}"
    password="${ADMIN_PASSWORD:-}"
  else
    password="${ADMIN_PASSWORD:-$(generate_password)}"
  fi

  if [[ -z "$password" ]]; then
    password="$(generate_password)"
  fi

  response_file="$(mktemp)"
  payload="$(printf '{"email":"%s","password":"%s"}' "$email" "$password")"

  log
  log "Calling first-run setup..."
  status="$(
    curl -sS -o "$response_file" -w '%{http_code}' \
      -X POST "$BACKEND_URL/setup" \
      -H 'Content-Type: application/json' \
      -d "$payload" || true
  )"

  if [[ "$status" == "200" || "$status" == "201" ]]; then
    save_credentials "$email" "$password"
    log "Setup completed."
    log "Admin email:    $email"
    log "Admin password: $password"
    rm -f "$response_file"
    return 0
  fi

  if [[ "$status" == "404" || "$status" == "409" || "$status" == "400" ]]; then
    log "Setup is not available or was already completed. HTTP $status"
    log "Response:"
    cat "$response_file" || true
    rm -f "$response_file"

    if [[ -f "$CREDENTIALS_FILE" ]]; then
      log
      print_credentials
      return 0
    fi

    die "Setup is closed and no saved local admin credentials exist."
  fi

  log "Unexpected setup response. HTTP $status"
  cat "$response_file" || true
  rm -f "$response_file"
  die "auto-set failed."
}

smoke_command() {
  require_basics
  load_backend_env

  log "Smoke checks..."
  wait_for_minio
  wait_for_url "$BACKEND_URL/v3/api-docs" "OpenAPI" 20 || die "OpenAPI is not reachable."
  wait_for_url "$FRONTEND_URL" "Frontend" 20 || die "Frontend is not reachable."
  log "Smoke checks passed."
}

status_command() {
  require_basics

  log "Docker services:"
  (cd "$BACKEND_DIR" && docker compose ps) || true

  log
  log "PID files:"
  find "$PID_DIR" -type f -maxdepth 1 -print -exec cat {} \; 2>/dev/null || true

  log
  log "URLs:"
  if http_ok "$BACKEND_URL/v3/api-docs"; then
    log "Backend:  UP $BACKEND_URL"
  else
    log "Backend:  DOWN $BACKEND_URL"
  fi

  if http_ok "$FRONTEND_URL"; then
    log "Frontend: UP $FRONTEND_URL"
  else
    log "Frontend: DOWN $FRONTEND_URL"
  fi
}

logs_command() {
  mkdir -p "$LOG_DIR"
  touch "$LOG_DIR/backend.log" "$LOG_DIR/frontend.log"
  tail -n 80 -f "$LOG_DIR/backend.log" "$LOG_DIR/frontend.log"
}

usage() {
  cat <<USAGE
Usage:
  scripts/dev-stack.sh --run
  scripts/dev-stack.sh --auto-set
  scripts/dev-stack.sh --stop
  scripts/dev-stack.sh --clean-rebuild
  scripts/dev-stack.sh --status
  scripts/dev-stack.sh --logs
  scripts/dev-stack.sh --smoke
  scripts/dev-stack.sh --credentials

Commands:
  --run            Start Docker, backend, and frontend.
  --auto-set       Run the stack, call /setup, and print/save local admin credentials.
  --stop           Stop frontend, backend, Gradle daemons, and Docker services.
  --clean-rebuild  Stop everything, remove Docker volumes, rebuild backend and frontend.
  --status         Show Docker/PID/URL status.
  --logs           Tail backend and frontend logs.
  --smoke          Check MinIO, OpenAPI, and frontend availability.
  --credentials    Print saved local admin credentials.

Environment overrides:
  FRONTEND_DIR     Override frontend directory. Default: ../cpmss-front next to backend.
  BACKEND_URL      Default: http://localhost:8080
  FRONTEND_URL     Default: http://localhost:3000
  ADMIN_EMAIL      Default: admin@compound.com
  ADMIN_PASSWORD   Optional. Generated if omitted and setup is open.
USAGE
}

main() {
  local command="${1:-}"

  case "$command" in
    --run|run)
      run_command
      ;;
    --auto-set|auto-set)
      auto_set_command
      ;;
    --stop|stop)
      stop_command
      ;;
    --clean-rebuild|clean-rebuild)
      clean_rebuild_command
      ;;
    --status|status)
      status_command
      ;;
    --logs|logs)
      logs_command
      ;;
    --smoke|smoke)
      smoke_command
      ;;
    --credentials|credentials)
      print_credentials
      ;;
    --help|-h|help|"")
      usage
      ;;
    *)
      usage
      die "Unknown command: $command"
      ;;
  esac
}

main "$@"
