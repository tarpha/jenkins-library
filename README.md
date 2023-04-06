# jenkins-library

pipeline {
  environment { 
      repository = "ghcr.io/tarpha/$JOB_NAME"  //docker hub id와 repository 이름
      dockerImage = '' 
  }
  agent any
  stages {
    stage('Clone repository') {
      steps {
        checkout scm
      }
    }
    stage('Build image') {
      steps {
        script { 
          dockerImage = docker.build repository + ":$BUILD_NUMBER" 
        }
      }
    }
    stage('Push image') {
      steps {
        script {
          docker.withRegistry('https://ghcr.io/tarpha', 'ghcr') {
            dockerImage.push("${env.BUILD_NUMBER}")
            dockerImage.push("0.0.1")
          }
        }
      }
    }
  }
  post {
    always {
      cleanWs()
    }
  }
}