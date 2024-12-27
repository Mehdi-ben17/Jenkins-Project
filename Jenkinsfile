pipeline {
    agent any  // Changed from docker agent to allow Docker installation

    tools {
        maven 'Maven_3.9.9'
    }

    environment {
        DOCKER_IMAGE = "mehdi/banking-app"  // Changed to lowercase as per Docker naming conventions
        DOCKER_TAG = "latest"
    }

    stages {
        stage('Setup Docker') {
            steps {
                // Install Docker if not present
                sh '''
                    if ! command -v docker &> /dev/null; then
                        curl -fsSL https://get.docker.com -o get-docker.sh
                        sudo sh get-docker.sh
                        sudo usermod -aG docker jenkins
                    fi
                '''
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Mehdi-ben17/Jenkins-Project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '*/target/surefire-reports/.xml'  // Fixed path pattern
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        sh """
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                            docker build -t \$DOCKER_USERNAME/${DOCKER_IMAGE}:${DOCKER_TAG} .
                            docker push \$DOCKER_USERNAME/${DOCKER_IMAGE}:${DOCKER_TAG}
                        """
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'ssh-key',
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                        ssh -i \$SSH_KEY -o StrictHostKeyChecking=no user@remote-server '
                            docker pull \$DOCKER_USERNAME/${DOCKER_IMAGE}:${DOCKER_TAG} &&
                            docker stop banking-app || true &&
                            docker rm banking-app || true &&
                            docker run -d --name banking-app \$DOCKER_USERNAME/${DOCKER_IMAGE}:${DOCKER_TAG}
                        '
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // Using cleanWs instead of deleteDir for better cleanup
        }
    }
}