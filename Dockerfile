FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

ARG BOT_TOKEN
ENV BOT_TOKEN=${BOT_TOKEN}

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=prod -Dtelegram.bot.token=$BOT_TOKEN -Dspring.datasource.password=$POSTGRES_PASSWORD -jar app.jar"]