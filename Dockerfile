# Build frontend
FROM node:20-alpine as frontend-build


# WORKDIR /reddit-crawler-frontend
WORKDIR /reactapp

# Frontend env var can be passed during build
ARG VITE_TELEGRAM_BOT_USERNAME
ENV VITE_TELEGRAM_BOT_USERNAME=${VITE_TELEGRAM_BOT_USERNAME}


# Copy frontend files
# COPY reddit-crawler-frontend/package*.json ./
# COPY reddit-crawler-frontend/src src
# COPY reddit-crawler-frontend/tsconfig.* .
# COPY reddit-crawler-frontend/vite.config.* .
# COPY reddit-crawler-frontend/tailwind.config* .
# COPY reddit-crawler-frontend/postcss.config.* .
# COPY reddit-crawler-frontend/eslint.config.* .

# Copy frontend source - this might be repeating the above****
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
# COPY --from=frontend-build /reddit-crawler-frontend/dist app/src/main/resources/static
# COPY --from=frontend-build /reactapp/dist app/src/main/resources/static
COPY --from=frontend-build /reactapp/dist src/main/resources/static

# Package the application
RUN ./mvnw package -DskipTests

# Final stage
FROM eclipse-temurin:22-jdk-alpine

WORKDIR /app

# Copy backend jar
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

# KEEP IN MIND THESE BELOW DO NOT HAVE THE NEW ENV VARIABLES!!!
# Use environment variables from host
# ENTRYPOINT ["java", \
#     "-Dspring.datasource.url=${MYSQL_URL}", \
#     "-Dspring.datasource.username=${MYSQL_USERNAME}", \
#     "-Dspring.datasource.password=${MYSQL_PASSWORD}", \
#     "-Dreddit.client-id=${REDDIT_CLIENT_ID}", \
#     "-Dreddit.user-agent=${REDDIT_USER_AGENT}", \
#     "-Dtelegram.bot-token=${TELEGRAM_BOT_TOKEN}", \
#     "-Dtelegram.bot-username=${TELEGRAM_BOT_USERNAME}", \
#     "-jar", "app.jar"]

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar