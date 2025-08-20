pipeline {
  agent none // No global agent for the entire pipeline

  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
  }

  stages {
    stage('Checkout and Build') {
      agent {
        docker {
          image 'maven:3.8.6-openjdk-17' // This agent is great for building Java apps
        }
      }
      steps {
        checkout scm
        sh 'mvn -B clean package'
      }
    }
    stage('Debug Reports') {
      agent {
        docker {
          image 'maven:3.8.6-openjdk-17' // Still using Maven agent for reports
        }
      }
      steps {
        sh 'echo "Contenido de target/surefire-reports:"'
        sh 'ls -l target/surefire-reports || echo "No se encontró la carpeta de reportes."'
        sh 'find . -name "*.xml"'
      }
    }
    stage('Build Docker Image') {
      agent {
        docker {
          image 'docker:latest' // Use an agent that *has* the Docker client
          args '-v /var/run/docker.sock:/var/run/docker.sock' // Mount the Docker socket
        }
      }
      steps {
        sh 'docker build -t $IMAGE_NAME .'
      }
    }
    stage('Run Container') {
      agent {
        docker {
          image 'docker:latest' // Continue using the Docker agent
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
        node { // This `node` block typically runs on the main Jenkins agent
          catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            junit '**/target/surefire-reports/*.xml'
          }
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }
  }
}