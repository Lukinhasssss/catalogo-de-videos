# To build and run:

# build stage
FROM gradle:8.7-jdk21-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar

# build runtime
FROM eclipse-temurin:21-jre-alpine

# Install curl
RUN apk add --no-cache curl

ARG JAR_FILE=/app/build/libs/app*.jar

COPY --from=builder $JAR_FILE /app.jar

# Download do OpenTelemetry Java Agent
RUN wget -O /opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.4.0/opentelemetry-javaagent.jar

# Download do Elastic APM Java Agent
#RUN wget -O /apm-agent.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.49.0/elastic-apm-agent-1.49.0.jar
COPY --from=docker.elastic.co/observability/apm-agent-java:latest /usr/agent/elastic-apm-agent.jar /apm-agent.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT [ "java", \
             "-javaagent:/apm-agent.jar", \
             "-Delastic.apm.service_name=catalogo-de-videos", \
             "-Delastic.apm.server_url=http://apm-codeflix:8200", \
             "-Delastic.apm.application_packages=com.lukinhasssss", \
             "-Delastic.apm.capture_body=all", \
             "-Delastic.apm.environment=codeflix", \
             "-javaagent:/opentelemetry-javaagent.jar", \
             "-Dotel.service.name=catalogo-de-videos", \
             "-Dotel.exporter.otlp.endpoint=http://jaeger-codeflix:4318", \
             "-Dotel.trace.exporter=zipkin", \
             "-Dotel.trace.exporter.zipkin.endpoint=http://jaeger-codeflix:9411/api/v2/spans", \
             "-jar", "/app.jar" \
]