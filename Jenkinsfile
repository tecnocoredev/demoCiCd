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
                    def workspace = env.WORKSPACE
                    def m2repo = "${workspace}/.m2"
                    sh "mkdir -p ${workspace}"
                    sh "mkdir -p ${m2repo}"
                    sh "sudo chown -R 1000:1000 ${m2repo}"
                    docker.image('maven:3.9.4-eclipse-temurin-21').inside("-v ${workspace}:/app -v ${m2repo}:/usr/src/app/.m2") {
                        sh 'mvn clean package -DskipTests -Dmaven.repo.local=/usr/src/app/.m2'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Creando la imagen de Docker...'
                script {
                    def dockerImage = docker.build("${DOCKER_IMAGE}")
                    echo "Imagen de Docker creada: ${dockerImage.id}"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo 'Empujando la imagen a Docker Hub...'
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_HUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                        docker.image("${DOCKER_IMAGE}").push()
                        echo "Imagen ${DOCKER_IMAGE} empujada a Docker Hub."
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                echo 'Desplegando la aplicación en el entorno de Staging...'
                script {
                    sh "docker rm -f ${STAGING_CONTAINER_NAME} || true"
                    sh "docker run -d --name ${STAGING_CONTAINER_NAME} -p 8080:8080 ${DOCKER_IMAGE}"
                    echo "Contenedor ${STAGING_CONTAINER_NAME} desplegado y escuchando en el puerto 8080."
                }
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