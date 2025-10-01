FROM clojure:temurin-21-tools-deps AS build

ARG PORT 
ARG CLIENT_URL 
ARG SQLITE_DB

WORKDIR /app

COPY deps.edn .
COPY build.clj .
COPY src src
COPY src_amble_shutdown src_amble_shutdown
COPY resources resources
COPY migrations/initfreshdb.sql migrations/initfreshdb.sql

RUN clj -T:build server-uber

RUN clj -T:build shutdown-uber

RUN jar tf target/shutdown.jar | grep amble_shutdown

FROM eclipse-temurin:21-jre

WORKDIR /app

# Install sqlite3
RUN apt-get update && apt-get install -y --no-install-recommends sqlite3 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/server.jar .

COPY --from=build /app/target/shutdown.jar .

COPY --from=build /app/migrations migrations

EXPOSE 80

COPY docker-main.sh /app/docker-main.sh
RUN chmod +x /app/docker-main.sh

CMD ["/app/docker-main.sh"]