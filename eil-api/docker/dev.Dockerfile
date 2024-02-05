# syntax = docker/dockerfile:experimental

## BUILD image ##
FROM eclipse-temurin:11 as builder
WORKDIR /build

# Copy files
COPY ./eil-api/build/libs/eil-api.jar ./app.jar
COPY ./eil-api/import ./import

# Extract layers
RUN java -Djarmode=layertools -jar ./app.jar extract

## RUN Image ##
FROM eclipse-temurin:11
WORKDIR /app

COPY --from=builder /build/dependencies /app
COPY --from=builder /build/snapshot-dependencies /app
COPY --from=builder /build/spring-boot-loader /app
COPY --from=builder /build/application /app
COPY --from=builder /build/import /app/import

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
