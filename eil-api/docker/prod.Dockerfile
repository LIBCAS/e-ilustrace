# syntax = docker/dockerfile:experimental

## BUILD image ##
FROM gradle:7.4.2-jdk11-alpine AS builder
WORKDIR /build

# COPY config files
COPY ./eil-api/docker/global.settings.gradle ./settings.gradle
COPY ./eil-api/docker/global.build.gradle ./build.gradle

# Copy files
COPY ./entity-views ./entity-views
COPY ./common ./common
COPY ./eil-api ./eil-api

# Run build
RUN \
    --mount=type=cache,id=gradle,target=/root/.gradle \
    --mount=type=cache,id=gradle,target=/home/gradle/.gradle \

    --mount=type=cache,id=gradle-entity-views,target=/build/entity-views/.gradle \
    --mount=type=cache,id=gradle-entity-views-build,target=/build/entity-views/build \

    --mount=type=cache,id=gradle-entity-views-processor,target=/build/entity-views/entity-views-processor/.gradle \
    --mount=type=cache,id=gradle-entity-views-processor-build,target=/build/entity-views/entity-views-processor/build \

    --mount=type=cache,id=gradle-entity-views-api,target=/build/entity-views/entity-views-api/.gradle \
    --mount=type=cache,id=gradle-entity-views-api-build,target=/build/entity-views/entity-views-api/build \

    --mount=type=cache,id=gradle-common,target=/build/common/.gradle \
    --mount=type=cache,id=gradle-common-build,target=/build/common/build \

    --mount=type=cache,id=gradle-eil-api-root,target=/build/.gradle \
    --mount=type=cache,id=gradle-eil-api,target=/build/eil-api/.gradle \
    --mount=type=cache,id=gradle-eil-api-build,target=/build/eil-api/build \

    gradle --no-daemon build

# extract layers
RUN --mount=type=cache,id=gradle-eil-api-build,target=/build/eil-api/build \
    java -Djarmode=layertools -jar ./eil-api/build/libs/eil-api.jar extract

## RUN Image ##
FROM eclipse-temurin:11
WORKDIR /app

COPY --from=builder /build/dependencies /app
COPY --from=builder /build/snapshot-dependencies /app
COPY --from=builder /build/spring-boot-loader /app
COPY --from=builder /build/application /app
COPY --from=builder /build/eil-api/import /app/import
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
