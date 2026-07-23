# Stage 1: Build project bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

COPY docker/context.azure.xml ./src/main/webapp/META-INF/context.xml

RUN mvn -B clean package -DskipTests


# Stage 2: Chạy app bằng Tomcat
FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

ENV CATALINA_OPTS="-Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource"

COPY --from=build /app/target/*.war /tmp/app.war

RUN mkdir -p /usr/local/tomcat/webapps/ROOT \
    && cd /usr/local/tomcat/webapps/ROOT \
    && jar -xf /tmp/app.war \
    && mkdir -p /usr/local/tomcat/webapps/ROOT/uploads \
    && rm -f /tmp/app.war

EXPOSE 8080

CMD ["catalina.sh", "run"]