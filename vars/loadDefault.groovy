def call(String name) {
    pipeline {
        environment {
            projectName = "${JOB_NAME.tokenize('/')[0]}"
            repository = "ghcr.io/tarpha/${projectName}"  //docker hub id와 repository 이름
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
                        echo "${job_name}"
                        echo "${BRANCH_NAME}"
                        echo "${workspace}"
                        echo "${projectName}"
                        dockerImage = docker.build repository + ":$BUILD_NUMBER" 
                    }
                }
            }
            stage('Push image') {
                steps {
                    script {
                        docker.withRegistry('https://ghcr.io/tarpha', 'ghcr') {
                            dockerImage.push("${BRANCH_NAME}-${BUILD_NUMBER}")
                            // dockerImage.push("0.0.1")
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