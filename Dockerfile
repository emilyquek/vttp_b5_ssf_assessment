FROM eclipse-temurin:23-noble AS builder

WORKDIR /src

COPY mvnw .
COPY pom.xml .

COPY src src
COPY .mvn .mvn

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

FROM eclipse-temurin:23-jre-noble

WORKDIR /app

COPY --from=builder /src/target/noticeboard-0.0.1-SNAPSHOT.jar vttp-ssf-assessment.jar

ENV PORT=8080

ENV NOTICE_PUBLISHING_SERVER="" 

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar vttp-ssf-assessment.jar

HEALTHCHECK --interval=60s --start-period=120s \ 
    CMD curl -s -f http://localhost:3030/status || exit 1
