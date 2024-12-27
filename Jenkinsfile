pipeline {
    agent any

    tools {
        maven 'Maven_3.9.9'
    }

    environment {
        DOCKER_IMAGE = "mehdi/banking-app"
        DOCKER_TAG = "latest"
        // Add environment variables for Docker installation
        DOCKER_INSTALL_DIR = "/var/jenkins_home/docker"
    }

    stages {
        stage('Setup Docker') {
            steps {
                script {
                    // Install Docker without sudo
                    sh '''
                        if ! command -v docker &> /dev/null; then
                            mkdir -p ${DOCKER_INSTALL_DIR}
                            curl -fsSL https://get.docker.com -o ${DOCKER_INSTALL_DIR}/get-docker.sh
                            chmod +x ${DOCKER_INSTALL_DIR}/get-docker.sh
                            sh ${DOCKER_INSTALL_DIR}/get-docker.sh --dry-run
                            # Install Docker using alternative method
                            curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-20.10.9.tgz -o ${DOCKER_INSTALL_DIR}/docker.tgz
                            tar xzvf ${DOCKER_INSTALL_DIR}/docker.tgz -C ${DOCKER_INSTALL_DIR}
                            export PATH=${DOCKER_INSTALL_DIR}/docker:$PATH
                            # Start Docker daemon if not running
                            if ! pgrep dockerd > /dev/null; then
                                ${DOCKER_INSTALL_DIR}/docker/dockerd &
                                sleep 10  # Wait for Docker daemon to start
                            fi
                        fi
                        # Verify Docker installation
                        docker --version || true
                    '''
                }
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
                    junit '*/target/surefire-reports/.xml'
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
                            export PATH=${DOCKER_INSTALL_DIR}/docker:\$PATH
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
                        export PATH=${DOCKER_INSTALL_DIR}/docker:\$PATH
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
            cleanWs()
        }
    }
}