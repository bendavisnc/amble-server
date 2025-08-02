FROM clojure:openjdk-17-lein AS build

ARG PORT 
ARG CLIENT_URL 
ARG POSTGRES_SUBNAME
ARG POSTGRES_USERNAME 
ARG POSTGRES_PASSWORD

WORKDIR /app

COPY project.clj .
COPY src src
COPY resources resources
COPY migrations/initfreshdb.pg.sql migrations/initfreshdb.pg.sql

RUN lein uberjar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*-standalone.jar app.jar
COPY --from=build /app/migrations migrations

RUN apt-get update && apt-get install -y postgresql-client && apt-get clean

EXPOSE 3000

COPY docker-main.sh /app/docker-main.sh
RUN chmod +x /app/docker-main.sh

CMD ["/app/docker-main.sh"]