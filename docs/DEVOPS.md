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
# .env
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

Docker Compose loads the `.env` file automatically.

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
server {
    listen 443 ssl;
    server_name yourdomain.com;

    ssl_certificate     /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location / {
        proxy_pass         http://app:8080;
        proxy_set_header   Host $host;
        proxy_set_header   X-Real-IP $remote_addr;
        proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
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
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'ssh deploy@server "cd /app && docker-compose pull && docker-compose up -d"'
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

### Trigger

Every push to `master` triggers the full pipeline via GitHub webhook. PRs
trigger Build + Test stages only (no deploy).



## Health Check

Spring Boot Actuator provides `/actuator/health`:

```json
{ "status": "UP" }
```

Docker Compose health check:

```yaml
# docker-compose.yml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
```
