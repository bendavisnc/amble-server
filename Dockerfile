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

RUN lein uberjar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*-standalone.jar app.jar

EXPOSE 3000

CMD ["java", "-jar", "app.jar"]
