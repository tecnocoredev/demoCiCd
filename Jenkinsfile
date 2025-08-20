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
        sh "ls -l" // Revisa el contenido del directorio actual
        sh "cat pom.xml || echo 'pom.xml no encontrado'" // Lee el pom.xml desde el directorio actual
      }
    }

    stage('Build') {
      steps {
        script {
          // Obtiene los IDs de usuario y grupo de Jenkins para evitar problemas de permisos
          def uid = sh(script: 'id -u', returnStdout: true).trim()
          def gid = sh(script: 'id -g', returnStdout: true).trim()
          
          // Ejecuta Docker con los mismos IDs de usuario y grupo
          sh "docker run --rm -v $WORKSPACE:/app -w /app --user ${uid}:${gid} maven:3.8.5-openjdk-17 mvn -B clean package"
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
      // Estos pasos se ejecutarán solo si el stage 'Build' tiene éxito
      // y se generan los archivos de reporte y el .jar
      junit '**/target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
  }
}