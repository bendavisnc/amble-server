FROM clojure:temurin-21-tools-deps AS build

ARG PORT 
ARG CLIENT_URL 
ARG SQLITE_DB

WORKDIR /app

COPY deps.edn .
COPY build.clj .
COPY src src
COPY resources resources
COPY migrations/initfreshdb.sql migrations/initfreshdb.sql

RUN clj -T:build uber

FROM eclipse-temurin:21-jre

WORKDIR /app

# Install sqlite3
RUN apt-get update && apt-get install -y --no-install-recommends sqlite3 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*-standalone.jar app.jar
COPY --from=build /app/migrations migrations

EXPOSE 80

COPY docker-main.sh /app/docker-main.sh
RUN chmod +x /app/docker-main.sh

CMD ["/app/docker-main.sh"]