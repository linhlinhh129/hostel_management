# Stage 1: Build project bằng Maven trong Docker
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Sửa DB config chỉ trong môi trường Docker
# Source code trên máy bạn vẫn giữ localhost để chạy local bình thường
RUN sed -i 's|jdbc:sqlserver://localhost:1433|jdbc:sqlserver://db:1433|g' src/main/webapp/META-INF/context.xml && \
    sed -i 's|password="123"|password="YourStrong@Passw0rd"|g' src/main/webapp/META-INF/context.xml && \
    sed -i 's|encrypt=false|encrypt=true|g' src/main/webapp/META-INF/context.xml

RUN mvn -B clean package -DskipTests


# Stage 2: Chạy app bằng Tomcat
FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]