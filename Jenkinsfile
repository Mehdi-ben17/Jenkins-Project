pipeline {
    agent {
        docker {
            image 'maven:3.8.5-openjdk-17-slim'  // Utilisation de l'image Maven avec OpenJDK 17
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
                // Récupérer le code source à partir de Git
                git branch: 'main', url: 'https://github.com/Mehdi-ben17/Jenkins-Project.git'
            }
        }

        stage('Build') {
            steps {
                // Construire l'application avec Maven
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                // Exécuter les tests unitaires avec Maven
                sh 'mvn test'
            }
            post {
                always {
                    // Publier les résultats des tests (Junit)
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Test Docker') {
            steps {
                // Vérification de la version de Docker
                sh 'docker --version'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',  // Identifiants Docker Hub dans Jenkins
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        // Se connecter à DockerHub et pousser l'image
                        sh """
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                            docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .  // Construction de l'image Docker
                            docker push ${DOCKER_IMAGE}:${DOCKER_TAG}  // Push de l'image sur Docker Hub
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
                    // Déployer l'image Docker sur le serveur distant
                    sh """
                        ssh -i ${SSH_KEY} user@remote-server 'docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}'  // Télécharger l'image sur le serveur distant
                        ssh -i ${SSH_KEY} user@remote-server 'docker stop banking-app || true'  // Arrêter l'ancien conteneur
                        ssh -i ${SSH_KEY} user@remote-server 'docker rm banking-app || true'  // Supprimer l'ancien conteneur
                        ssh -i ${SSH_KEY} user@remote-server 'docker run -d --name banking-app ${DOCKER_IMAGE}:${DOCKER_TAG}'  // Lancer le nouveau conteneur
                    """
                }
            }
        }
    }

    post {
        always {
            // Nettoyer le répertoire de travail après chaque exécution
            deleteDir()
        }
    }
}
