# fuentes: 
# - https://spring.io/guides/gs/spring-boot-docker/
# - https://www.javanicaragua.org/2020/03/29/aplicacion-de-spring-boot-con-mysql-y-docker/

# IMAGEN DE PARTIDA
FROM openjdk:8-jdk-alpine

# run the app as a non-root user:
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# PUERTO QUE EXPONEMOS
EXPOSE 8080

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]

# ENTRYPOINT 
# ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", 
# "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]

# pasos para la ejecución
# ...............................................................................................
# 1. construir el jar de la app sin pasar los tests
# ./mvnw clean install -DskipTests

# 2. construir la imagen
# docker build -t fj2m/apikey-server:0.3 .

# 3. correr el contenedor
# docker run -d -p 8081:8081 -t --name=apikey-server --network=apikey-network fj2m/apikey-server:0.3

# 4. ver los logs
# docker logs CONTAINER ID