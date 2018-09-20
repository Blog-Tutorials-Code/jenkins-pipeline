pipeline {
    agent {
        docker {
            // NOTE: May need a "docker login" to access dockerhub images
            image "ferdinandyeboah/jenkins-pipeline-tooling"
            // NOTE: The docker sock host location depends on host. Verify location in google vm
            args "-v /var/run/docker.sock:/var/run/docker.sock"
        }
    }

    stages {
        stage ("Verify Tooling"){
            steps {
                sh "java -version"
                sh "mvn -version"
                sh "gcloud version"
                sh "docker version"
            }
        }

    }
}