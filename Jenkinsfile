pipeline {
  agent any

  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Debug') {
      steps {
        sh "ls -l"
        sh "cat pom.xml"
      }
    }

    stage('Build') {
      steps {
        script {
          def uid = sh(script: 'id -u', returnStdout: true).trim()
          def gid = sh(script: 'id -g', returnStdout: true).trim()
          
          // Se monta el volumen del proyecto y se indica a Maven que use una carpeta diferente para el caché
          sh "docker run --rm -v $WORKSPACE:/app -w /app --user ${uid}:${gid} maven:3.8.5-openjdk-17 mvn -B -Dmaven.repo.local=/app/.m2 clean package"
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "docker build -t $IMAGE_NAME ."
      }
    }

    stage('Run Container') {
      steps {
        sh "docker rm -f demo-ci-cd || true"
        sh "docker run -d --name demo-ci-cd -p 8080:8080 $IMAGE_NAME"
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
  }
}