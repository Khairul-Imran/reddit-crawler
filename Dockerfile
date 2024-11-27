# Build frontend
FROM node:20-alpine as frontend-build

# WORKDIR /reddit-crawler-frontend
WORKDIR /reactapp

# Frontend env var can be passed during build
ARG VITE_TELEGRAM_BOT_USERNAME
ENV VITE_TELEGRAM_BOT_USERNAME=${VITE_TELEGRAM_BOT_USERNAME}

# Copy frontend source
COPY reddit-crawler-frontend/ .

# Install dependencies
RUN npm install

# Build frontend
RUN npm run build

# Build backend
FROM eclipse-temurin:22-jdk-alpine as backend-build

# WORKDIR /reddit-crawler-backend
WORKDIR /sbapp

# Copy maven files
COPY reddit-crawler-backend/mvnw .
COPY reddit-crawler-backend/.mvn .mvn
COPY reddit-crawler-backend/pom.xml .

# Copy backend source
COPY reddit-crawler-backend/src src

# Copy frontend build to backend static resources
COPY --from=frontend-build /reactapp/dist src/main/resources/static

# Package the application
RUN ./mvnw package -DskipTests

# Final stage (Final Runtime)
FROM eclipse-temurin:22-jdk-alpine

WORKDIR /app

# Copy backend jar
# Copies the built JAR from the backend build stage to the final image
# Naming it app.jar - You could change this name in the future
COPY --from=backend-build /sbapp/target/*.jar app.jar

ENV PORT=8080

ENV MYSQL_URL=
ENV MYSQL_USERNAME=
ENV MYSQL_PASSWORD=

ENV REDDIT_CLIENT_ID=
ENV REDDIT_CLIENT_SECRET=
ENV REDDIT_USERNAME=
ENV REDDIT_PASSWORD=
ENV REDDIT_USER_AGENT=

ENV TELEGRAM_BOT_TOKEN=
ENV TELEGRAM_BOT_USERNAME=

# Starts the spring-boot application, 
# Sets the server port from the environment variable ${PORT}
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar