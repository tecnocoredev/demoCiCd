pipeline {
  agent any

  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
    MAVEN_IMAGE = "maven:3.8.6-jdk-17" // etiqueta corregida
  }

  stages {
    stage('Checkout and Build') {
      steps {
        checkout scm
        sh "docker run --rm -v \$PWD:/app -w /app ${env.MAVEN_IMAGE} mvn -B clean package"
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "docker build -t ${env.IMAGE_NAME} ."
      }
    }

    stage('Run Container') {
      steps {
        sh "docker rm -f demo-ci-cd || true"
        sh "docker run -d --name demo-ci-cd -p 8080:8080 ${env.IMAGE_NAME}"
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
