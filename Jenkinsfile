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

    stage('Build and Package') {
      steps {
        sh "docker build -t $IMAGE_NAME ."
      }
    }

    stage('Run Container') {
      steps {
        sh "docker rm -f demo-ci-cd || true"
        sh "docker run -d --name demo-ci-cd -p 8081:8080 $IMAGE_NAME"
      }
    }
  }

  post {
    always {
      echo 'La fase de compilación se realizó correctamente dentro del Dockerfile.'
    }
  }
}