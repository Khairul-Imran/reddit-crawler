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
                                string(credentialsId: 'telegram-bot-token', variable: 'BOT_TOKEN'),
                                string(credentialsId: 'reddit-user-agent', variable: 'REDDIT_AGENT') // Added this
                            ]) {
                                // SSH into Ubuntu server and run the container
                                sh """
                                    ssh -o StrictHostKeyChecking=no -p 22 deploy@ubuntu-server '
                                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                                        docker stop reddit-crawler || true
                                        docker rm reddit-crawler || true
                                        
                                        echo "Creating environment file for sensitive variables..."
                                        cat > /tmp/reddit-crawler.env << EOF
                                        MYSQL_URL=${MYSQL_URL}
                                        MYSQL_USERNAME=${MYSQL_USERNAME}
                                        MYSQL_PASSWORD=${DB_PASS}
                                        REDDIT_CLIENT_ID=${REDDIT_CLIENT_ID}
                                        REDDIT_CLIENT_SECRET=${REDDIT_SECRET}
                                        REDDIT_USERNAME=${REDDIT_USERNAME}
                                        REDDIT_PASSWORD=${REDDIT_PASS}
                                        REDDIT_USER_AGENT=${REDDIT_AGENT}
                                        TELEGRAM_BOT_TOKEN=${BOT_TOKEN}
                                        TELEGRAM_BOT_USERNAME=${TELEGRAM_BOT_USERNAME}
EOF
                                
                                        echo "Starting container..."
                                        docker run -d \\
                                            --name reddit-crawler \\
                                            --network jenkins \\
                                            -p 8080:8080 \\
                                            --env-file /tmp/reddit-crawler.env \\
                                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                                            
                                        echo "Container started with ID: $(docker ps -q --filter name=reddit-crawler)"
                                        
                                        # Remove sensitive env file
                                        echo "Removing sensitive env file..."
                                        rm /tmp/reddit-crawler.env

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
                    sleep(15) // Give the container time to start
                    // sh 'curl -f http://localhost:8080 || exit 1'
                    
                    sshagent(['ubuntu-server-ssh']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no deploy@ubuntu-server '
                                echo "Checking container status..."
                                docker ps -a | grep reddit-crawler

                                echo "Checking container logs..."
                                docker logs reddit-crawler || echo "Cannot access logs"

                                echo "Checking network connectivity..."
                                netstat -tulpn | grep 8080 || echo "No process listening on 8080"
                                
                                echo "Attempting to access application..."
                                curl -v http://localhost:8080 || echo "Application not responding"
                            '
                        """
                    }
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
