pipeline {
   agent any

   tools {
       maven 'Maven_3.9.9'
   }

   environment {
       DOCKER_IMAGE = "rimsdk/banking-app"
       DOCKER_TAG = "latest"
   }

   stages {
       stage('Checkout') {
           steps {
               git branch: 'main',
                   url: 'https://github.com/Mehdi-ben17/Jenkins-Project.git'
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
                   junit '**/target/surefire-reports/*.xml'
               }
           }
       }

       stage('Docker Build & Push') {
           steps {
               script {
                   withCredentials([usernamePassword(
                       credentialsId: 'dockerhub-credentials',
                       usernameVariable: 'mehdi2001',
                       passwordVariable: 'Stage2025'
                   )]) {
                       sh """
                           echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                           docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                           docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
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
                       ssh -i ${SSH_KEY} user@remote-server 'docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}'
                       ssh -i ${SSH_KEY} user@remote-server 'docker stop banking-app || true'
                       ssh -i ${SSH_KEY} user@remote-server 'docker rm banking-app || true'
                       ssh -i ${SSH_KEY} user@remote-server 'docker run -d --name banking-app ${DOCKER_IMAGE}:${DOCKER_TAG}'
                   """
               }
           }
       }
   }

   post {
       always {
           deleteDir()
       }
   }
}