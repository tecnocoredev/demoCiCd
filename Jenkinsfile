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
        sh "docker build -f Dockerfile.build -t build-image-temporary ."
      }
    }
    
    stage('Build Docker Image') {
      steps {
        // En este paso se construye la imagen final de tu aplicación
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
      // Necesitas copiar los archivos del contenedor temporal al host
      // Esto es un paso adicional si necesitas los artifacts en el host
      script {
        // Obtenemos el ID del contenedor temporal
        def containerId = sh(script: "docker create build-image-temporary", returnStdout: true).trim()
        // Copiamos los archivos de reporte y el JAR del contenedor al workspace de Jenkins
        sh "docker cp ${containerId}:/app/target/surefire-reports ./"
        sh "docker cp ${containerId}:/app/target/*.jar ./"
        // Eliminamos el contenedor temporal
        sh "docker rm ${containerId}"
      }
      junit 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
  }
}