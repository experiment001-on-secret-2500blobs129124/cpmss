# DevOps

## Environments

Three environments, each with its own Spring profile and configuration file.

| Environment | Profile | Config File | Purpose |
|---|---|---|---|
| Development | `dev` | `application-dev.yml` | Local development. PostgreSQL via Docker. Debug logging. Data seeding enabled. |
| Testing | `test` | `application-test.yml` | Automated tests. Testcontainers spins up isolated PostgreSQL. Wiped after each run. |
| Production | `prod` | `application-prod.yml` | Live system. No seeding. Info-level logging only. |

Active profile is set in the `.env` file:

```env
# .env — never commit this file
SPRING_PROFILES_ACTIVE=dev

DB_URL=jdbc:postgresql://localhost:5432/cpmss
DB_USERNAME=cpmss_user
DB_PASSWORD=changeme

JWT_SECRET=your-secret-key
```

Configuration files reference these variables:

```yaml
# application.yml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
jwt:
  secret: ${JWT_SECRET}
```

### Secret Management Per Environment

| Environment | How secrets are provided |
|---|---|
| `dev` | `.env` file on local machine — always in `.gitignore`, never committed |
| `test` | Testcontainers manages the DB automatically. JWT secret is a hardcoded value in `application-test.yml` |
| `prod` | Jenkins credentials store — injected via `withCredentials`, no `.env` file on server |

Docker Compose loads the `.env` file automatically (dev only).

---

## Docker

### Services

```yaml
# docker-compose.yml
services:
  app:
    build: .
    ports: ["8080:8080"]
    env_file: .env
    depends_on: [postgres]
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  postgres:
    image: postgres:16
    env_file: .env
    environment:
      POSTGRES_DB: cpmss
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports: ["5432:5432"]

  # Future: Redis
  # redis:
  #   image: redis:7-alpine
  #   ports: ["6379:6379"]

volumes:
  postgres_data:
```

### Dockerfile

Multi-stage build: compile in one stage, run in a minimal JRE image.

```dockerfile
# Stage 1: Build
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Reverse Proxy — Nginx

Tomcat (embedded in Spring Boot) handles HTTP. Nginx sits in front for TLS
termination (HTTPS) and reverse proxy. TLS certificates are provided by
Let's Encrypt via Certbot — a free certificate authority that auto-renews.

```nginx
# In nginx.conf — limit_req_zone must be in the http {} context, not server {}
http {
    limit_req_zone $binary_remote_addr zone=auth:10m rate=10r/m;

    server {
        listen 443 ssl;
        server_name yourdomain.com;

        ssl_certificate     /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

        # Rate limiting applied to auth endpoints only
        location /api/v1/auth/ {
            limit_req zone=auth burst=5 nodelay;
            proxy_pass http://app:8080;
        }

        location / {
            proxy_pass         http://app:8080;
            proxy_set_header   Host $host;
            proxy_set_header   X-Real-IP $remote_addr;
            proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header   X-Forwarded-Proto $scheme;
        }
    }
}
```

---

## CI/CD — Jenkins

Jenkins runs as a Docker container. A `Jenkinsfile` at the repo root defines
the pipeline.

### Running Jenkins

```bash
docker run -d \
  -p 8081:8080 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

### Pipeline

```groovy
// Jenkinsfile
pipeline {
    agent any

    environment {
        IMAGE_NAME = 'cpmss'
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('API Test') {
            steps {
                sh '''
                    python3 -m venv .venv
                    source .venv/bin/activate
                    pip install -r tests/api/requirements.txt
                    python3 tests/api/run_tests.py
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ."
                sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push ${IMAGE_NAME}:${BUILD_NUMBER}"
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'db-password', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'jwt-secret',  variable: 'JWT_SECRET'),
                    sshUserPrivateKey(credentialsId: 'deploy-ssh-key', keyFileVariable: 'SSH_KEY')
                ]) {
                    sh '''
                        # Write secrets to a temp .env file locally — never inline in the command
                        # string, which would expose them in the server process list (ps aux).
                        printf 'DB_PASSWORD=%s\nJWT_SECRET=%s\n' "$DB_PASSWORD" "$JWT_SECRET" > /tmp/deploy.env

                        # Transfer the .env file to the server, then deploy
                        scp -i $SSH_KEY /tmp/deploy.env deploy@server:/app/.env
                        ssh -i $SSH_KEY deploy@server "cd /app && docker compose pull && docker compose up -d"

                        # Remove the local temp file immediately
                        rm -f /tmp/deploy.env
                    '''
                }
            }
        }
    }

    post {
        failure {
            echo 'Pipeline failed — notify dev'
        }
    }
}
```

### Credentials Required in Jenkins

| Credential ID | Type | Used in |
|---|---|---|
| `dockerhub-creds` | Username/Password | Docker Push stage |
| `db-password` | Secret text | Deploy stage |
| `jwt-secret` | Secret text | Deploy stage |
| `deploy-ssh-key` | SSH private key | Deploy stage — key must be pre-authorized on the server |

### Trigger

Every push to `master` triggers the full pipeline via GitHub webhook. PRs
trigger Build + Test stages only (no deploy).

### Rollback

Each push produces a tagged image (`cpmss:BUILD_NUMBER`) that remains in the
registry. Add an `image:` key to the `app` service in `docker-compose.yml` to
enable tag-based rollback:

```yaml
services:
  app:
    build: .
    image: cpmss:${IMAGE_TAG:-latest}  # add this line
    ports: ["8080:8080"]
    ...
```

To roll back to a previous build:

```bash
ssh -i $SSH_KEY deploy@server \
  "cd /app && IMAGE_TAG=42 docker compose up -d"
```

Keep at least the last 5 build-numbered images in the registry.

---

## Health Check

Spring Boot Actuator provides `/actuator/health`:

```json
{ "status": "UP" }
```

Health check is defined in the Docker Compose service above (30s interval,
3 retries before the container is marked unhealthy).
