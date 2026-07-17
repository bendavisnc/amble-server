FROM clojure:temurin-25-tools-deps AS build

ARG PORT 
ARG CLIENT_URL 
ARG SQLITE_DB
ARG DBBACKUP
ARG DBBACKUP_REMOTE
ARG DBBACKUP_USERNAME
ARG DBBACKUP_PRIVATE_KEY

WORKDIR /app

COPY deps.edn .
COPY build.clj .
COPY src src
COPY resources resources
COPY migrations/initfreshdb.sql migrations/initfreshdb.sql
COPY scripts scripts

RUN clojure -X:deps tree
RUN clojure -T:build server-uber

FROM eclipse-temurin:25-jre

ARG SQLITE_DB
ENV SQLITE_DB=${SQLITE_DB}
ARG DBBACKUP
ENV DBBACKUP=${DBBACKUP}
ARG DBBACKUP_REMOTE
ENV DBBACKUP_REMOTE=${DBBACKUP_REMOTE}
ARG DBBACKUP_USERNAME
ENV DBBACKUP_USERNAME=${DBBACKUP_USERNAME}
ARG DBBACKUP_PRIVATE_KEY
ENV DBBACKUP_PRIVATE_KEY=${DBBACKUP_PRIVATE_KEY}

WORKDIR /app

# Install sqlite3 & git
RUN apt-get update && apt-get install -y --no-install-recommends sqlite3 git \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/server.jar .

COPY --from=build /app/migrations migrations

COPY --from=build /app/scripts scripts

EXPOSE 80

COPY docker-main.sh /app/docker-main.sh
RUN chmod +x /app/docker-main.sh
RUN chmod +x /app/scripts/whenserverstops/gitpush.sh
RUN chmod +x /app/scripts/whenserverstarts/gitclone.sh

CMD ["/app/docker-main.sh"]