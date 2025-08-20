pipeline {
  agent any

  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
  }

  stages {
    stage('Checkout and Build') {
      steps {
        checkout scm
        sh 'docker run --rm -v $PWD:/app -w /app maven:3.8.6-openjdk-17 mvn -B clean package'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $IMAGE_NAME .'
      }
    }

    stage('Run Container') {
      steps {
        sh 'docker rm -f demo-ci-cd || true'
        sh 'docker run -d --name demo-ci-cd -p 8080:8080 $IMAGE_NAME'
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
