# PulseWatch

**API monitoring and anomaly detection platform with AI-powered incident analysis.**

PulseWatch monitors API telemetry, detects unusual behavior such as high error rates and latency degradation, and uses an AI model to analyze detected anomalies and generate an incident summary.

The project is built around an **event-driven architecture using Kafka**, with Spring Boot handling the core application logic and PostgreSQL providing persistent storage.

---

## Why PulseWatch?

API failures are not always simple outages.

An API may remain available while experiencing:

- A sudden increase in error rate
- Significant latency degradation
- A spike in response times compared with previous behavior

PulseWatch continuously analyzes API telemetry to identify these conditions and produces an AI-assisted explanation when an anomaly is detected.

---

## Architecture

```text
                    PulseWatch
                        │
                        ▼
                 Telemetry API
                        │
                        ▼
                      Kafka
                        │
                        ▼
               Store Telemetry
                        │
                        ▼
             Periodic Monitoring
                        │
                        ▼
              Calculate Metrics
                        │
                        ▼
               Detect Anomaly
                        │
                 anomaly found
                        │
                        ▼
                      Kafka
                        │
                        ▼
                  AI Analysis
                        │
                        ▼
                Store Incident
```
---

## Key Features

### Service Registration

Services can be registered with PulseWatch so that their telemetry can be monitored.

```http
POST /api/services
```

Example:

```json
{
  "id": 1,
  "name": "payment-api",
  "environment": "local",
  "baseUrl": "http://localhost:8081",
  "healthEndpoint": "/actuator/health"
}
```

---

### Telemetry Ingestion

Telemetry can be submitted through:

```http
POST /api/telemetry
```
Example:

```json
{
  "serviceName": "payment-api",
  "environment": "local",
  "endpoint": "/payments",
  "statusCode": 500,
  "latencyMs": 1840,
  "timestamp": "2026-08-16T00:10:00Z"
}
```

Telemetry is published asynchronously to Kafka and processed by a downstream consumer.

This keeps the API ingestion path decoupled from persistence.

---

### Monitoring & Metrics

PulseWatch periodically evaluates registered services using a configurable monitoring window.

The monitoring engine calculates:

| Metric | Description |
|---|---|
| Request Count | Number of requests observed |
| Error Count | Number of failed requests |
| Error Rate | Percentage of failed requests |
| Average Latency | Average response time |
| P95 Latency | 95th percentile response time |

The current monitoring window is compared with the previous window to identify changes in service behavior.

---

### Anomaly Detection

PulseWatch currently detects anomalies using configurable rules.

For example:

```text
Error Rate > configured threshold
        ↓
HIGH_ERROR_RATE
```

```text
P95 Latency > configured threshold
        ↓
HIGH_LATENCY
```

```text
Current latency significantly higher
than previous monitoring window
        ↓
LATENCY_SPIKE
```

Detected anomalies are represented using structured events and published to Kafka.

---

### AI-Powered Incident Analysis

Detected anomalies are consumed asynchronously and passed to the AI analysis layer.

The AI receives information such as:

- Service name
- Anomaly severity
- Detected signals
- Current metrics
- Previous metrics
- Detection timestamp

The goal is not to let the AI detect anomalies itself.

Instead:

> **Deterministic rules detect the anomaly; AI explains and summarizes it.**

This separation keeps anomaly detection predictable while using AI where it provides the most value — incident interpretation and summarization.

PulseWatch currently uses **Spring AI with Ollama** for local AI inference.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application development |
| Spring Boot | Backend framework |
| Spring Kafka | Event-driven processing |
| Apache Kafka | Asynchronous event streaming |
| PostgreSQL | Persistent storage |
| Spring Data JPA | Database access |
| Spring AI | AI integration |
| Ollama | Local LLM inference |
| Bean Validation | Request validation |
| Docker | Containerization |

---

## Project Structure

```text
com.pulsewatch
│
├── ai
│   ├── AiIncidentAnalysisService
│   └── model
│
├── anomaly
│   ├── model
│   └── service
│
├── configuration
│
├── controller
│
├── exception
│
├── model
│
├── monitoring
│   ├── model
│   └── service
│
├── repository
│
├── service
│
├── simulator
│
└── telemetry
    ├── consumer
    ├── controller
    ├── model
    ├── repository
    └── service
```

The codebase is organized around application responsibilities such as telemetry, monitoring, anomaly detection, and AI analysis rather than putting all components into a single package.

---

## Kafka Design

PulseWatch uses Kafka at two important boundaries:

```text
Telemetry API
     │
     ▼
Telemetry Topic
     │
     ▼
Telemetry Consumer
     │
     ▼
PostgreSQL
```

and:

```text
Monitoring
     │
     ▼
Anomaly Topic
     │
     ▼
AI Consumer
     │
     ▼
AI Analysis
```

This provides loose coupling between components and prevents slower downstream processing, particularly AI inference, from blocking the monitoring flow.

The service name is used as the Kafka message key for anomaly events, helping preserve ordering for events belonging to the same service.

---

## Configuration

Environment-specific configuration is externalized through environment variables.

Example:

```properties
spring.datasource.url=${DATASOURCE_URL}
spring.datasource.username=${DATASOURCE_USERNAME}
spring.datasource.password=${DATASOURCE_PASSWORD}

spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS}

pulsewatch.kafka.telemetry-topic=${KAFKA_TELEMETRY_TOPIC}
pulsewatch.kafka.anomaly-topic=${KAFKA_ANOMALY_TOPIC}

spring.ai.ollama.base-url=${OLLAMA_BASE_URL}
spring.ai.ollama.chat.model=${OLLAMA_CHAT_MODEL}
```

Anomaly detection thresholds are also configurable:

```properties
pulsewatch.anomaly.errorRateThreshold=${ANOMALY_ERROR_RATE_THRESHOLD}
pulsewatch.anomaly.p95LatencyThresholdMs=${ANOMALY_P95_LATENCY_THRESHOLD_MS}
pulsewatch.anomaly.spikeMultiplier=${ANOMALY_SPIKE_MULTIPLIER}
pulsewatch.monitoring.window.minutes=${MONITORING_WINDOW_MINUTES}
```

---

## Running Locally

### Prerequisites

Make sure the following are available:

- Java 21
- Maven
- PostgreSQL
- Apache Kafka
- Ollama

### 1. Clone the repository

```bash
git clone https://github.com/abhishek1704/pulsewatch.git
cd pulsewatch
```

### 2. Start PostgreSQL and Kafka

Make sure PostgreSQL and Kafka are running locally.

Configure the required environment variables before starting the application.

### 3. Start Ollama

Install Ollama and pull the model configured for PulseWatch.

For example:

```bash
ollama pull <model-name>
```

### 4. Start PulseWatch

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

---

## Example Flow

Register a service:

```http
POST /api/services
```

Then submit telemetry:

```json
{
  "serviceName": "payment-service",
  "statusCode": 500,
  "latencyMs": 1800
}
```

After sufficient telemetry is collected, the monitoring scheduler evaluates the service.

For example:

```text
Current Window

Error Rate:       38.9%
P95 Latency:      1807 ms

Previous Window

Error Rate:        4.2%
P95 Latency:       420 ms
```

PulseWatch may identify:

```text
Severity: CRITICAL

Signals:
- HIGH_ERROR_RATE
- HIGH_LATENCY
```

The anomaly is then published to Kafka and consumed by the AI analysis component.

---

## Design Decisions

### Why Kafka?

Telemetry ingestion and anomaly analysis are asynchronous operations.

Kafka provides a buffer between producers and consumers and allows downstream processing to operate independently from the API request.

It also provides a natural integration point for AI processing, which can take considerably longer than normal application logic.

### Why compare monitoring windows?

A static threshold can identify obvious problems, but comparing the current window with the previous window helps identify sudden changes in service behavior.

For example:

```text
Previous P95: 300 ms
Current P95:  1500 ms
```

Even if the absolute threshold is not extremely high, the sudden increase is worth investigating.

### Why use rules before AI?

Anomaly detection is deterministic and should be predictable.

AI is better suited to interpreting the detected signals and turning raw metrics into a human-readable incident analysis.

Therefore, PulseWatch deliberately separates:

```text
Detection → Rules
Analysis  → AI
```

---

## Error Handling

PulseWatch handles failures according to the boundary where they occur.

### HTTP layer

REST API errors are handled through a global exception handler and returned as structured error responses.

### Kafka consumers

Consumer-side processing failures are allowed to propagate to Kafka's error-handling infrastructure rather than being silently swallowed.

### Scheduled monitoring

Monitoring failures are isolated so that an issue while processing one registered service does not prevent other services from being evaluated.

### Asynchronous Kafka producers

Kafka publishing uses asynchronous callbacks, allowing the API and monitoring flows to remain non-blocking while publish failures are logged and handled separately.

---

## Current Scope

PulseWatch intentionally focuses on a small but complete monitoring workflow:

```text
Telemetry
   ↓
Kafka
   ↓
Persistence
   ↓
Monitoring
   ↓
Anomaly Detection
   ↓
Kafka
   ↓
AI Analysis
   ↓
Incident
```

The project is designed as a backend-focused demonstration of:

- Event-driven architecture
- Asynchronous processing
- Kafka consumers and producers
- API monitoring
- Metric calculation
- Rule-based anomaly detection
- AI integration
- Persistence
- Error handling

---

## Future Improvements

Possible future enhancements include:

- Persistent incident management
- Kafka retry and dead-letter topics
- Containerized local environment
- Kubernetes deployment
- Improved anomaly detection algorithms
- Historical anomaly trends
- Monitoring dashboard

These are intentionally outside the core workflow and can be added independently as the project evolves.

---

## What I Learned

PulseWatch was built to explore how a traditional Spring Boot backend can be extended into an event-driven system with asynchronous processing and AI capabilities.

The project provided hands-on experience with:

- Designing Kafka-based asynchronous workflows
- Separating ingestion, processing, monitoring, and analysis responsibilities
- Designing domain events
- Handling failures at different architectural boundaries
- Calculating operational metrics such as P95 latency
- Integrating LLMs into an existing backend workflow
- Keeping AI as an analysis layer rather than making it responsible for deterministic system behavior

---

## License

This project is for learning and portfolio purposes.