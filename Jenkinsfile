pipeline {
  agent none // No hay un agente global para todo el pipeline

  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
  }

  stages {
    stage('Checkout and Build') {
      agent {
        docker {
          image 'maven:3.8.6-openjdk-17' // Usa el agente de Maven para la compilación
        }
      }
      steps {
        checkout scm
        sh 'mvn -B clean package'
      }
    }
    stage('Build Docker Image') {
      agent {
        docker {
          image 'docker:latest' // Usa un agente con el cliente de Docker
          args '-v /var/run/docker.sock:/var/run/docker.sock' // Monta el socket de Docker
        }
      }
      steps {
        sh 'docker build -t $IMAGE_NAME .'
      }
    }
    stage('Run Container') {
      agent {
        docker {
          image 'docker:latest' // Usa el mismo agente de Docker
          args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
      }
      steps {
        sh 'docker rm -f demo-ci-cd || true'
        sh 'docker run -d --name demo-ci-cd -p 8080:8080 $IMAGE_NAME'
      }
    }
  }

  post {
    always {
      script {
        node {
          catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            junit '**/target/surefire-reports/*.xml'
          }
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }
  }
}