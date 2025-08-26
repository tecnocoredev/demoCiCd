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

                    // Aseguramos que existan las carpetas necesarias
                    sh "mkdir -p ${workspace}"
                    sh "mkdir -p ${m2repo}"

                    docker.image('maven:3.9.4-eclipse-temurin-21').inside("-v ${workspace}:/app -v ${m2repo}:/root/.m2") {
                        sh 'mvn clean package -DskipTests'
                    }
                }
            }
        }

        // Resto del pipeline igual ...
    }

    post {
        always {
            sh 'docker logout'
            echo 'Pipeline finalizado.'
        }
    }
}
