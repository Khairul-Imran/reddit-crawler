pipeline {
    agent any

    environment {
        // Credentials
        // VITE_TELEGRAM_BOT_USERNAME = credentials('vite-telegram-bot-username')
        MYSQL_URL = credentials('mysql-url')
        MYSQL_USERNAME = credentials('mysql-username')
        MYSQL_PASSWORD = credentials('mysql-password')

        REDDIT_CLIENT_ID = credentials('reddit-client-id')
        REDDIT_CLIENT_SECRET = credentials('reddit-client-secret')
        REDDIT_USERNAME = credentials('reddit-username')
        REDDIT_PASSWORD = credentials('reddit-password')
        REDDIT_USER_AGENT = credentials('reddit-user-agent')

        TELEGRAM_BOT_TOKEN = credentials('telegram-bot-token')
        TELEGRAM_BOT_USERNAME = credentials('telegram-bot-username')

        // Docker Hub configs
        DOCKER_HUB_REGISTRY = "docker.io"
        DOCKER_HUB_USERNAME = "khairulimran"
        DOCKER_IMAGE = "${DOCKER_HUB_USERNAME}/reddit-crawler"
        DOCKER_TAG = "latest"
        // Docker Hub credentials
        DOCKER_CREDENTIALS = credentials('dockerhub-credentials')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image using the multi-stage Dockerfile
                    // Recall we installed the Docker pipeline plugin
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", "--build-arg VITE_TELEGRAM_BOT_USERNAME=${TELEGRAM_BOT_USERNAME} .")
                }
            }
        }

        stage('Push image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        // Login to Docker Hub
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"

                        // Push the image
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    }
                }
            }
        }

        stage('Deploy to Ubuntu Server') {
            steps {
                script {
                    try {
                        // Using the SSH credentials
                        sshagent(['ubuntu-server-ssh']) {
                            withCredentials([
                                // Might just do the same for all the variables, not just the sensitive ones. TODO
                                string(credentialsId: 'mysql-password', variable: 'DB_PASS'),
                                string(credentialsId: 'reddit-client-secret', variable: 'REDDIT_SECRET'),
                                string(credentialsId: 'reddit-password', variable: 'REDDIT_PASS'),
                                string(credentialsId: 'telegram-bot-token', variable: 'BOT_TOKEN')
                            ]) {
                                // SSH into Ubuntu server and run the container
                                sh """
                                    ssh -o StrictHostKeyChecking=no -p 2222 deploy@localhost '
                                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                                        docker stop reddit-crawler || true
                                        docker rm reddit-crawler || true
                                        docker run -d \\
                                            --name reddit-crawler \\
                                            --network jenkins \\
                                            -p 8080:8080 \\
                                            -e MYSQL_URL=${MYSQL_URL} \\
                                            -e MYSQL_USERNAME=${MYSQL_USERNAME} \\
                                            -e MYSQL_PASSWORD=${DB_PASS} \\
                                            -e REDDIT_CLIENT_ID=${REDDIT_CLIENT_ID} \\
                                            -e REDDIT_CLIENT_SECRET=${REDDIT_SECRET} \\
                                            -e REDDIT_USERNAME=${REDDIT_USERNAME} \\
                                            -e REDDIT_PASSWORD=${REDDIT_PASS} \\
                                            -e REDDIT_USER_AGENT=${REDDIT_USER_AGENT} \\
                                            -e TELEGRAM_BOT_TOKEN=${BOT_TOKEN} \\
                                            -e TELEGRAM_BOT_USERNAME=${TELEGRAM_BOT_USERNAME} \\
                                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                                    '
                                """
                            }
                        }
                    } catch (err) {
                        echo "Deployment failed: ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    sleep(10) // Give the container time to start
                    sh 'curl -f http://localhost:8080 || exit 1'
                }
            }
        }
    }

    post {
        always {
            // Logout from Docker Hub
            sh 'docker logout'
        }
    }
}
