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
        // Usa `ls -l` para verificar el contenido del directorio
        sh "ls -l"
        // Asegúrate de que `cat pom.xml` funcione
        sh "cat pom.xml" 
      }
    }
    stage('Build') {
      steps {
        // Usa la variable $WORKSPACE para montar el directorio del proyecto
        sh "docker run --rm -v $WORKSPACE:/app -w /app maven:3.8.5-openjdk-17 mvn -B clean package"
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