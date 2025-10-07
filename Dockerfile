FROM clojure:temurin-21-tools-deps AS build

ARG PORT 
ARG CLIENT_URL 
ARG SQLITE_DB
ARG DBBACKUP
ARG DBBACKUP_REMOTE

WORKDIR /app

COPY deps.edn .
COPY build.clj .
COPY src src
COPY src_dbbackup src_dbbackup
COPY resources resources
COPY migrations/initfreshdb.sql migrations/initfreshdb.sql

RUN clj -T:build server-uber

RUN clj -T:build dbbackupread-uber
RUN clj -T:build dbbackupwrite-uber

FROM eclipse-temurin:21-jre

WORKDIR /app

# Install sqlite3 & git
RUN apt-get update && apt-get install -y --no-install-recommends sqlite3 git \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/server.jar .

COPY --from=build /app/target/dbbackupread.jar .
COPY --from=build /app/target/dbbackupwrite.jar .

COPY --from=build /app/migrations migrations

EXPOSE 80

COPY docker-main.sh /app/docker-main.sh
RUN chmod +x /app/docker-main.sh

CMD ["/app/docker-main.sh"]