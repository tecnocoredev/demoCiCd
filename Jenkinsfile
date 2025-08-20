pipeline {
  agent none

  environment {
    IMAGE_NAME = "demo-ci-cd:latest"
  }

  stages {
    stage('Checkout and Build') {
      agent {
        docker {
          image 'maven:3.8.6-openjdk-17'
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
          image 'docker:latest'
          args '-v /var/run/docker.sock:/var/run/docker.sock' 
        }
      }
      steps {
        sh 'docker build -t $IMAGE_NAME .'
      }
    }
    stage('Run Container') {
      agent {
        docker {
          image 'docker:latest'
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