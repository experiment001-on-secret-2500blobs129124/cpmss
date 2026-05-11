# CPMSS — Compound Management System

An ambitious Software Project.

CPMSS is a full-stack compound management system with a Spring Boot backend, PostgreSQL, MinIO object storage, and a Next.js frontend.

The project is organized assuming this local layout:

    software_engineering/
      cpmss/        # backend
      cpmss-front/  # frontend

---

## Quick Start

From the backend repository:

    cd cpmss
    scripts/dev-stack.sh --auto-set

This starts the local stack, runs the first-admin setup when available, and prints the admin login credentials.

Then open the frontend:

    http://localhost:3000

If you started the stack with `--run` and the database is empty, complete first-admin setup at:

    http://localhost:3000/setup

If you started the stack with `--auto-set`, setup has already been performed by the script. Log in at:

    http://localhost:3000/login

Backend API docs are available at:

    http://localhost:8080/swagger-ui.html
    http://localhost:8080/v3/api-docs

---

## Local Full-Stack Runner

The backend repository includes:

    scripts/dev-stack.sh

The script is intended for local development and demo runs. It derives paths from the repository layout, so it does not depend on a hardcoded home directory.

### Script Options

| Option | What it does |
|--------|--------------|
| `--run` | Starts PostgreSQL, MinIO, the Spring Boot backend, and the Next.js frontend. |
| `--auto-set` | Runs the full stack, calls `POST /setup`, creates the first admin if setup is still open, and prints/saves the local admin credentials. |
| `--stop` | Stops the frontend, backend, Gradle daemons, and Docker services. |
| `--clean-rebuild` | Stops everything, removes Docker volumes, runs backend tests, reinstalls/builds the frontend, and verifies the local build. |
| `--status` | Shows Docker service status, saved process IDs, and whether backend/frontend URLs are reachable. |
| `--logs` | Tails backend and frontend logs from `.dev-stack/logs/`. |
| `--smoke` | Checks MinIO, OpenAPI, and frontend availability. |
| `--credentials` | Prints the saved local admin credentials if `--auto-set` previously created them. |

### Common Script Commands

Start everything:

    scripts/dev-stack.sh --run

Start everything and create the first admin:

    scripts/dev-stack.sh --auto-set

Check status:

    scripts/dev-stack.sh --status

Tail logs:

    scripts/dev-stack.sh --logs

Stop everything:

    scripts/dev-stack.sh --stop

Nuke local database volumes and rebuild:

    scripts/dev-stack.sh --clean-rebuild

Print saved local admin credentials:

    scripts/dev-stack.sh --credentials

---

## Local Environment

The backend uses the `dev` profile locally.

A typical backend `.env` file is:

    SPRING_PROFILES_ACTIVE=dev

    DB_URL=jdbc:postgresql://127.0.0.1:5433/cpmss
    DB_USERNAME=cpmss_user
    DB_PASSWORD=cpmss_pass

    MINIO_ROOT_USER=minioadmin
    MINIO_ROOT_PASSWORD=minioadmin

    JWT_SECRET=cpmss-dev-secret-key-not-for-production-at-least-256-bits-long-padding

The frontend uses:

    NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

The dev runner writes this value to `cpmss-front/.env.local`.

---

## Docker Ports

The default local services are:

| Service | URL / Port |
|---------|------------|
| Backend | `http://localhost:8080` |
| Frontend | `http://localhost:3000` |
| MinIO API | `http://localhost:9000` |
| MinIO Console | `http://localhost:9001` |
| PostgreSQL | `127.0.0.1:5433` when using the local override |

If host PostgreSQL already uses port `5432`, keep a local untracked `docker-compose.override.yml` like this:

    services:
      postgres:
        ports:
          - "5433:5432"

The backend `.env` should then use:

    DB_URL=jdbc:postgresql://127.0.0.1:5433/cpmss

---

## Manual Backend Startup

Start Docker services:

    cd cpmss
    docker compose up -d postgres minio

Load local environment variables:

    set -a
    . ./.env
    set +a

Run Spring Boot:

    ./gradlew bootRun

Backend should become available at:

    http://localhost:8080

OpenAPI should become available at:

    http://localhost:8080/v3/api-docs

---

## Manual Frontend Startup

In a second terminal:

    cd cpmss-front

Create or update `.env.local`:

    printf 'NEXT_PUBLIC_API_BASE_URL=http://localhost:8080\n' > .env.local

Install dependencies:

    npm install

Run the frontend:

    npm run dev

Frontend should become available at:

    http://localhost:3000

---

## Manual First-Admin Setup

The first admin setup endpoint works only while the user table is empty. After first setup, the endpoint is permanently unavailable.

Create the first admin manually:

    curl -i -X POST http://localhost:8080/setup \
      -H 'Content-Type: application/json' \
      -d '{"email":"admin@compound.com","password":"AdminPass123!"}'

Then log in from the frontend using the same email and password.

For normal local use, prefer:

    scripts/dev-stack.sh --auto-set

because it starts the stack first and saves the generated local credentials under `.dev-stack/`.

---

## Manual Verification

Backend tests:

    cd cpmss
    ./gradlew test

Frontend lint:

    cd cpmss-front
    npm run lint

Frontend production build:

    cd cpmss-front
    npm run build

Backend diff check:

    cd cpmss
    git diff --check

Frontend diff check:

    cd cpmss-front
    git diff --check

---

## Manual Shutdown

Stop backend/frontend processes if they were started manually:

    fuser -k 8080/tcp 2>/dev/null || true
    fuser -k 3000/tcp 2>/dev/null || true

Stop Gradle daemons:

    cd cpmss
    ./gradlew --stop

Stop Docker services:

    docker compose down

Remove local database/object-storage volumes:

    docker compose down -v

Use `down -v` only when you intentionally want to reset the local database and MinIO data.

---

## Local Generated Files

The following local files/directories are not product source and should stay untracked:

| Path | Purpose |
|------|---------|
| `.dev-stack/` | Local process IDs, logs, and generated admin credentials |
| `docker-compose.override.yml` | Developer-specific Docker port overrides |
| `cpmss-front/.env.local` | Local frontend API base URL |
| `cpmss-front/.next/` | Next.js build output |
| `cpmss-front/node_modules/` | Frontend dependencies |

---

## Seed Data

`V10__seed_catalog_data.sql` contains required catalog/reference rows such as roles, departments, qualifications, and shift types.

Fake demo data should not be added to normal production migrations.

If richer demo data is needed later, prefer a local dev-only API seeding script that creates records through real backend workflows instead of bypassing business rules with raw SQL.

---

## Commit Convention

See [`COMMIT_CONVENTION.md`](./COMMIT_CONVENTION.md) for the commit message style used in this project.

---

## Documentation

| Document | Description |
|----------|-------------|
| [Requirements](docs/REQUIREMENTS.md) | Business behavior, role permissions, rules, and workflows |
| [Non-Functional Requirements](docs/NON_FUNCTIONAL_REQUIREMENTS.md) | Cross-cutting quality and operational requirements |
| [Stack](docs/STACK.md) | Tools, libraries, and technology choices |
| [Architecture](docs/ARCHITECTURE.md) | System design, request lifecycle, patterns |
| [Database](docs/DATABASE.md) | Schema design decisions, migrations, and bootstrap |
| [Conventions](docs/CONVENTIONS.md) | Implementation patterns every developer follows |
| [DevOps](docs/DEVOPS.md) | Environments, Docker, Nginx, Jenkins CI/CD |
| [Errors](docs/ERRORS.md) | HTTP error mapping and response shape guide |
| [Logging](docs/LOGGING.md) | SLF4J/Logback logging policy |
| [Testing](docs/TESTING.md) | Testing policy and layer guide |
| [Naming](docs/NAMING.md) | Java naming conventions |
| [Documentation](docs/DOCUMENTATION.md) | Javadoc standard |
| [Coding Style](docs/CODING_STYLE.md) | Java formatting and structural rules |
| [Commit Convention](COMMIT_CONVENTION.md) | Git commit style |
