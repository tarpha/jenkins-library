def call(String name) {
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
            cleanup {
                // cleanWs()
                // echo "END ${name}"
                /* clean up our workspace */
                deleteDir()
                /* clean up tmp directory */
                dir("${workspace}@tmp") {
                    deleteDir()
                }
                /* clean up script directory */
                dir("${workspace}@script") {
                    deleteDir()
                }
                dir("${workspace}@libs") {
                    deleteDir()
                }
            }
        }
    }
}