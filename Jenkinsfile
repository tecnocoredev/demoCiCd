pipeline {
  agent {
    docker {
      image 'maven:3.8.6-openjdk-17'
      args '-v /var/run/docker.sock:/var/run/docker.sock'
    }
  }
  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build & Test') {
      steps {
        sh 'mvn -B clean package'
      }
    }
    stage('Debug Reports') {
      steps {
        sh 'echo "Contenido de target/surefire-reports:"'
        sh 'ls -l target/surefire-reports || echo "No se encontró la carpeta de reportes."'
        sh 'find . -name "*.xml"'
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
      catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        junit '**/target/surefire-reports/*.xml'
      }
      archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
  }
}
