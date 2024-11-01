# Start from an official Maven image to build the project
FROM maven:3.8.6-openjdk-8 AS build

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the entire project to the container
COPY . .

# Run the Maven build (creates a JAR file)
RUN mvn clean package -DskipTests

# Start from an OpenJDK image for running the application
FROM openjdk:8-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage to this image
COPY --from=build /usr/src/app/target/Code-Critters-1.0.1.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
