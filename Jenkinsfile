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
        // Usa el contenedor de Maven para compilar el proyecto en el workspace de Jenkins
        sh "docker run --rm -v $WORKSPACE:/app -v $HOME/.m2:/root/.m2 -w /app maven:3.8.5-openjdk-17 mvn -B clean package"
      }
    }

    stage('Build Docker Image') {
      steps {
        // Ahora que el JAR existe, Docker puede construir la imagen
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
      // Ahora que el JAR existe en el workspace, JUnit y archiveArtifacts funcionarán
      junit 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
  }
}