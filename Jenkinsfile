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
                    docker.image('maven:3.9.4-eclipse-temurin-21').inside("-u root -v ${workspace}:/app -v ${m2repo}:/usr/src/app/.m2") {
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
                    sh '''
                        if docker ps -a --format '{{.Names}}' | grep -Eq '^demo-ci-cd-staging$'; then
                            echo "Eliminando contenedor existente..."
                            docker rm -f demo-ci-cd-staging

                            echo "Esperando a que el puerto 8080 se libere..."
                            while lsof -Pi :8096 -sTCP:LISTEN -t >/dev/null ; do
                                echo "Puerto 8080 aún en uso. Esperando..."
                                sleep 1
                            done
                        fi
                        echo "Desplegando nueva imagen en puerto 8096..."
                        docker run -d --name demo-ci-cd-staging -p 8096:8080 tecnocore/demo-ci-cd:${BUILD_NUMBER}
                    '''
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
