FROM eclipse-temurin:21-jdk-alpine
COPY target/cozyheavenstay_backend-0.0.1-SNAPSHOT.jar cozyheaven-backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","cozyheaven-backend.jar"]