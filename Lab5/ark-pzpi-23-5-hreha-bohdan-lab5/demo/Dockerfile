FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=postgres:18 /usr/lib/postgresql/18 /usr/lib/postgresql/18
COPY --from=postgres:18 /usr/lib/x86_64-linux-gnu/libpq.so.5 /usr/lib/
COPY --from=postgres:18 /usr/lib/x86_64-linux-gnu/libpq.so.5.18 /usr/lib/

ENV PATH="/usr/lib/postgresql/18/bin:$PATH"
ENV LD_LIBRARY_PATH="/usr/lib:$LD_LIBRARY_PATH"

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
