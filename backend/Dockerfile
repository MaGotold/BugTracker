### Stage 1: Build the application

# 1. Specify the base image
FROM gradle:7.5-jdk17-alpine AS build

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Install necessary utilities
RUN apk add --no-cache git curl postgresql-client

# 4. Copy the files from the host to the container
COPY . .

# 5. Set the command to build the Spring Boot application
RUN ./gradlew build --no-daemon

### Stage 2: Run the application

# 1. Specify the base image
FROM openjdk:17-jdk-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the built JAR file from the build stage to the current directory
COPY --from=build /app/build/libs/*.jar /app/build/libs/app.jar

# 5. Specify the port
EXPOSE 8080

# 6. Set the command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/build/libs/app.jar", "--spring.profiles.active=dev"]
