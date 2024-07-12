FROM gradle:8.7-jdk17 AS build


# Set the working directory in the image to /app
WORKDIR /app

# Copy the gradle configuration files to our app directory
# COPY gradle gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
# COPY settings.gradle .
# Download all needed dependencies for building offline
RUN gradle wrapper
RUN chmod +x ./gradlew
# RUN ls -la ./
RUN ./gradlew 
# build --no-daemon

# Copy the rest of the project
COPY . .
RUN chmod -R 777 ./
# Download all needed dependencies for building offline
RUN gradle wrapper

# Build the application
RUN ./gradlew bootJar

# Use the official openjdk image for a lean production stage of our multi-stage build
FROM openjdk:17-jdk-slim

# Set the working directory in the image to /app
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar xeex_collab.jar
COPY --from=build /app/.env .env
COPY --from=build /app/keystore.p12 keystore.p12
# Set the required environment variables
ENV APP_TILTE="Collab Application"
ENV APP_VERSION="1.0.0"
# ... add the rest of your environment variables here ...

# Run the application
EXPOSE 8080
CMD ["java", "-jar", "xeex_collab.jar"]