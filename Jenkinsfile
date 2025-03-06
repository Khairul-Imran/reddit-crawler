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

        // Application configs
        APP_PORT = "8090" // To avoid conflicts with 8080
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

        stage('Check Environment') {
            steps {
                script {
                    sshagent(['ubuntu-server-ssh']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no -p 22 deploy@ubuntu-server '
                                echo "=== System Information ==="
                                uname -a
                                
                                echo "=== Docker Info ==="
                                docker info
                                
                                echo "=== Docker Networks ==="
                                docker network ls
                                
                                echo "=== Port Usage ==="
                                ss -tulpn || netstat -tulpn || echo "No tool available to check ports"
                                
                                echo "=== Checking Port ${APP_PORT} ==="
                                lsof -i :${APP_PORT} || echo "Port ${APP_PORT} is free or lsof not installed"
                                
                                echo "=== Existing Containers ==="
                                docker ps -a
                            '
                        """
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
                                // Create deployment script with proper docker run command
                                def deployScript = """
                                    # Ensure network exists
                                    docker network inspect jenkins >/dev/null 2>&1 || docker network create jenkins

                                    # Pull latest image
                                    docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                                    
                                    # Stop and remove existing container
                                    docker stop reddit-crawler || true
                                    docker rm reddit-crawler || true

                                    # Check if port ${APP_PORT} is in use
                                    if ss -tulpn | grep :${APP_PORT} || netstat -tulpn | grep :${APP_PORT}; then
                                        echo "Port ${APP_PORT} is already in use. Finding any containers using this port..."
                                        docker ps | grep ${APP_PORT} || echo "No Docker containers using port ${APP_PORT}"
                                        echo "Attempting to find and kill processes using port ${APP_PORT}..."
                                        lsof -ti:${APP_PORT} | xargs kill -9 || echo "No processes found or unable to kill"
                                    fi
                                    
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
                                    
                                    docker run -d --name reddit-crawler \\
                                        --network jenkins \\
                                        -p ${APP_PORT}:${APP_PORT} \\
                                        -e "SERVER_PORT=${APP_PORT}" \\
                                        --env-file /tmp/reddit-crawler.env \\
                                        ${DOCKER_IMAGE}:${DOCKER_TAG}

                                    echo "Container started with ID: \$(docker ps -q --filter name=reddit-crawler || echo 'FAILED')"
                                    
                                    if [ "\$(docker ps -q --filter name=reddit-crawler)" != "" ]; then
                                        echo "Container started successfully!"
                                        docker logs reddit-crawler
                                    else
                                        echo "Container failed to start!"
                                        docker ps -a | grep reddit-crawler
                                    fi
                                        
                                    echo "Removing sensitive env file..."
                                    rm /tmp/reddit-crawler.env
                                """

                                // SSH into Ubuntu server and run the deployment script
                                sh "ssh -o StrictHostKeyChecking=no -p 22 deploy@ubuntu-server '${deployScript}'"
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
                    sleep(20) // Give the container time to start

                    
                    sshagent(['ubuntu-server-ssh']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no deploy@ubuntu-server '
                                echo "=== Container Status ==="
                                docker ps -a | grep reddit-crawler || echo "Container not found"

                                echo "=== Container Details ==="
                                docker inspect reddit-crawler || echo "Cannot inspect container"

                                echo "=== Container Logs ==="
                                docker logs reddit-crawler || echo "Cannot access logs"

                                echo "=== Network Status ==="
                                docker network inspect jenkins || echo "Jenkins network not found"
                                
                                echo "=== Container Network ==="
                                docker exec reddit-crawler ip addr || echo "Cannot check container network"

                                echo "=== Port Bindings ==="
                                docker port reddit-crawler || echo "No port bindings found"

                                echo "=== System Port Status ==="
                                apt-get update -qq && apt-get install -y net-tools > /dev/null || true
                                ss -tulpn | grep ${APP_PORT} || netstat -tulpn | grep ${APP_PORT} || echo "No process listening on ${APP_PORT}"
                                
                                echo "=== Application Connectivity Test ==="
                                curl -v http://localhost:${APP_PORT} || echo "Application not responding on localhost"
                                curl -v http://127.0.0.1:${APP_PORT} || echo "Application not responding on 127.0.0.1"
                                
                                CONTAINER_IP=\$(docker inspect -f "{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}" reddit-crawler)
                                if [ -n "\$CONTAINER_IP" ]; then
                                    echo "=== Testing direct container IP ==="
                                    curl -v http://\$CONTAINER_IP:${APP_PORT} || echo "Application not responding on container IP"
                                fi
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
