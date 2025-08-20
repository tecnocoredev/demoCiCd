stage('Build') {
  steps {
    script {
      sh 'echo "WORKSPACE: $WORKSPACE"'
      sh "docker run --rm -v $WORKSPACE:/app -w /app maven:3.8.5-openjdk-17 ls -l"
      sh "docker run --rm -v $WORKSPACE:/app -w /app maven:3.8.5-openjdk-17 mvn -B clean package"
    }
  }
}
