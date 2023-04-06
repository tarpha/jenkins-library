def call(String name) {
    node {
        stage('Clone repository') {
            checkout scm
        }
        stage('Build image') {
            app = docker.build("ghcr.io/tarpha/${env.JOB_NAME}")
        }
        stage('Push image') {
            docker.withRegistry('https://ghcr.io/tarpha', 'ghcr') {
            app.push("${env.BUILD_NUMBER}")
            app.push("0.0.1")
            }
        }
        stage('Clean') {
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
            echo "End ${name}"
        }
    }
}