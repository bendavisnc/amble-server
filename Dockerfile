
FROM clojure:openjdk-17-lein AS build

WORKDIR /app

COPY project.clj .
COPY src src
COPY resources resources

RUN lein uberjar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*-standalone.jar app.jar

EXPOSE 3000

RUN echo $CLIENT_URL
CMD ["java", "-jar", "app.jar"]
