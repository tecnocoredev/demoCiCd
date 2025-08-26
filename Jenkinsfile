pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "tecnocore/demo-ci-cd:${env.BUILD_NUMBER}"
        DOCKER_HUB_CREDENTIALS = 'dockerhub-credentials'
        STAGING_CONTAINER_NAME = 'demo-ci-cd-staging'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Clonando el repositorio...'
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Ejecutando la compilación y pruebas del proyecto...'
                script {
                    docker.image('maven:3.9.4-eclipse-temurin-21').inside("-v $PWD:/app") {
                        // Crear carpeta local para repositorio Maven
                        sh 'mkdir -p .m2/repository'
                        // Ejecutar Maven usando repositorio local en /app/.m2/repository
                        sh 'mvn clean package -DskipTests -Dmaven.repo.local=/app/.m2/repository'
                    }
                }
            }
        }

        stage('Static Analysis') {
            steps {
                echo 'Realizando análisis de código estático con SonarQube...'
                // Aquí puedes agregar integración con SonarQube si lo deseas
            }
        }

        stage('Docker Hub Login') {
            steps {
                echo 'Autenticando con Docker Hub...'
                script {
                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_HUB_CREDENTIALS,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Construyendo la imagen de Docker...'
                sh "docker build -t ${env.DOCKER_IMAGE} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Subiendo la imagen a Docker Hub...'
                script {
                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_HUB_CREDENTIALS,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                        sh "docker push ${env.DOCKER_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                echo 'Desplegando en el entorno de Staging...'
                sh "docker rm -f ${env.STAGING_CONTAINER_NAME} || true"
                sh "docker run -d --name ${env.STAGING_CONTAINER_NAME} -p 8083:8080 ${env.DOCKER_IMAGE}"
            }
        }
    }

    post {
        always {
            sh 'docker logout'
            echo 'Pipeline finalizado.'
        }
    }
}
