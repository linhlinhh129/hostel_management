# Stage 1: Build project bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Dùng context riêng để connect Azure SQL
COPY docker/context.azure.xml ./src/main/webapp/META-INF/context.xml

RUN mvn -B clean package -DskipTests


# Stage 2: Chạy app bằng Tomcat
FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

# Cho Tomcat đọc biến môi trường trong context.xml
ENV CATALINA_OPTS="-Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource"

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]