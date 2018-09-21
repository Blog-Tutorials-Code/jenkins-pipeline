// Some global variables I have to figure out the best place to put or read them from
def GCLOUD_PROJECT = "[SUPPLY_PROJECT_NAME]"
def REGION = "[SUPPLY_REGION]"
def VM_INSTANCE = "spring-playground"

pipeline {

    // Pass the job these variables
    parameters {
        string(name: "GIT_REPO_URL", defaultValue: "https://github.com/Blog-Tutorials-Code/spring-playground", description: "URL of git repo to checkout")
        // Improvement: There really should not be an option to configure this. This should be extracted as the repo name from the git repo url.
        string(name: "ARTIFACT_NAME", defaultValue: "spring-playground", description: "Name of docker image that will be created and versioned")
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
        stage ("Verify & Configure Tooling"){

            environment {
                GCLOUD_SERVICE_ACCOUNT_KEY = credentials("GCLOUD_SERVICE_KEY")
            }

            steps {
                sh "java -version"
                sh "mvn -version"
                sh "gcloud version"
                sh "docker version"

                // Configure the GCLOUD CLI
                sh "gcloud auth activate-service-account --key-file $GCLOUD_SERVICE_ACCOUNT_KEY"
                sh "gcloud config list" 
                // sh "gcloud config set project ${REGION}" //
            }
        }

        stage ("Checkout Source Code"){
            steps {
                echo "GIT REPO URL: ${params.GIT_REPO_URL}"
                git url: "${params.GIT_REPO_URL}", branch: "master", changelog: false, poll: false
            }
        }

        stage ("Docker Build"){
            steps {
                sh "docker build . -t ${params.ARTIFACT_NAME}"
            }
        }

        // TODO: Push docker image to nexus repo.
        // stage ("Push to Artifact Repo"){
        //     steps {
        //         sh "docker build . -t ${params.ARTIFACT_NAME}"
        //     }
        // }
        

        // stage ("Deploy to GCP"){
        //     steps {
                // Whatever GCP deploy command with the latest image
        //     }    
        // }

    }
}