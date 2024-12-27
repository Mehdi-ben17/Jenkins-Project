pipeline {
    agent {
        docker {
            image 'maven:3.8.5-openjdk-17-slim'  // Utilisation de l'image Maven
            args '''
                --privileged
                -v /var/run/docker.sock:/var/run/docker.sock  // Montage du socket Docker pour permettre l'exécution de Docker à l'intérieur du conteneur
            '''
        }
    }

    tools {
        maven 'Maven_3.9.9'  // Utilisation de Maven 3.9.9 comme outil
    }

    environment {
        DOCKER_IMAGE = "Mehdi/banking-app"  // Nom de l'image Docker
        DOCKER_TAG = "latest"  // Tag de l'image Docker
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Mehdi-ben17/Jenkins-Project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'  // Construction de l'application avec Maven
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'  // Exécution des tests unitaires avec Maven
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'  // Publication des résultats des tests
                }
            }
        }

        stage('Test Docker') {
            steps {
                sh 'docker --version'  // Vérification de la version de Docker pour s'assurer que Docker est installé
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',  // Identification des credentials DockerHub dans Jenkins
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        sh """
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin  // Connexion à DockerHub
                            docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .  // Construction de l'image Docker
                            docker push ${DOCKER_IMAGE}:${DOCKER_TAG}  // Push de l'image Docker sur DockerHub
                        """
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'ssh-key',  // Accès SSH au serveur distant
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                        ssh -i ${SSH_KEY} user@remote-server 'docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}'  // Pull de l'image Docker sur le serveur distant
                        ssh -i ${SSH_KEY} user@remote-server 'docker stop banking-app || true'  // Arrêt de l'ancien conteneur
                        ssh -i ${SSH_KEY} user@remote-server 'docker rm banking-app || true'  // Suppression de l'ancien conteneur
                        ssh -i ${SSH_KEY} user@remote-server 'docker run -d --name banking-app ${DOCKER_IMAGE}:${DOCKER_TAG}'  // Exécution du nouveau conteneur
                    """
                }
            }
        }
    }

    post {
        always {
            deleteDir()  // Suppression du répertoire de travail dans Jenkins après chaque exécution
        }
    }
}
