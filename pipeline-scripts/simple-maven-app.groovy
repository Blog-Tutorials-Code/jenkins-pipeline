pipeline {

    // Pass the job these variables
    parameters {
        string(name: "GIT_REPO_URL", defaultValue: "https://github.com/FerdinandYeboah/simple-java-maven-app", description: "URL of git repo to checkout")
    }

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

        stage ("Checkout Source Code"){
            steps {
                echo "GIT REPO URL: ${params.GIT_REPO_URL}"
            }
        }

        // stage ("Docker Build"){
        //     steps {

        //     }
        // }

        // stage ("Deploy to GCP"){
        //     steps {

        //     }
        // }

    }
}