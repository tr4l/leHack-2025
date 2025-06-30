FROM maven:3.9-amazoncorretto-21 AS builder
RUN mkdir -p /build/poor/
WORKDIR /build/poor/
COPY src src
COPY pom.xml .
RUN mvn clean package

FROM amazoncorretto:21-alpine

RUN mkdir -p /app

COPY --from=builder /build/poor/target/*.jar /app/app.jar
COPY app.sh /app/app.sh
RUN chmod +x /app/app.sh

ENV LH_SERVER_PORT=1234
ENV LH_SERVER_ADDRESS=0.0.0.0
ENV LH_SERVER_CONTEXT_PATH=/
ENV LH_FLAG="DummyDockerFlag"


ENTRYPOINT ["/app/app.sh"]