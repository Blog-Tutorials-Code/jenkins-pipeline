// Some global variables I have to figure out the best place to put or read them from
def GCLOUD_PROJECT = "sharp-terminal-215500"
def ZONE = "us-east1-b"
def MACHINE_TYPE = "f1-micro"
def ALLOW_TCP_8080_TAG = "allow-http-8080"

pipeline {

    // Pass the job these variables
    parameters {
        string(name: "GIT_REPO_URL", defaultValue: "https://github.com/Blog-Tutorials-Code/spring-playground", description: "URL of git repo to checkout")
        // Improvement: There really should not be an option to configure this. This should be extracted as the repo name from the git repo url.
        string(name: "ARTIFACT_NAME", defaultValue: "spring-playground", description: "Name of docker image and vm instance that will be created and versioned")
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

                // Configure the GCLOUD CLI. Setting project and region should be okay.
                sh "gcloud auth activate-service-account --key-file $GCLOUD_SERVICE_ACCOUNT_KEY"
                sh "gcloud config list" 
                // sh "gcloud config set project ${GCLOUD_PROJECT}" //
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
                // Remove old application docker image. NOTE: Can remove all images using: docker rmi $(docker images -q)
                sh "docker image rm ${params.ARTIFACT_NAME}"
                // Build the application docker image
                sh "docker build . -t ${params.ARTIFACT_NAME}"
            }
        }

        stage ("Push to Dockerhub"){
            environment {
                DOCKER_HUB = credentials("DOCKER_HUB_LOGIN")
            }

            steps {
                sh "docker login -u $DOCKER_HUB_USR -p $DOCKER_HUB_PSW"
                sh "docker tag ${params.ARTIFACT_NAME} ferdinandyeboah/${params.ARTIFACT_NAME}" 
                sh "docker push ferdinandyeboah/${params.ARTIFACT_NAME}" 
            }
        }

        // TODO: Instead of pushing to dockerhub, push to nexus
        // stage ("Push to Nexus Repo"){
        //     steps {
                // docker push...
        //     }
        // }
        

        stage ("Deploy to GCP"){
            steps {
                //Try to deploy container. Update if it is already present.
                script {
                    try {
                        sh """gcloud compute instances create-with-container ${params.ARTIFACT_NAME} \
                        --container-image=ferdinandyeboah/${params.ARTIFACT_NAME} \
                        --machine-type=${MACHINE_TYPE} \
                        --zone=${ZONE} \
                        --project=${GCLOUD_PROJECT}"""

                        //Add the correct firewall tags.
                        sh """gcloud compute instances add-tags ${params.ARTIFACT_NAME} \
                            --zone ${ZONE} \
                            --project=${GCLOUD_PROJECT} \
                            --tags ${ALLOW_TCP_8080_TAG}"""
                    } 
                    catch (Exception e) { // Container already present, try update
                        sh """gcloud compute instances update-container ${params.ARTIFACT_NAME} \
                        --container-image=ferdinandyeboah/${params.ARTIFACT_NAME} \
                        --zone=${ZONE} \
                        --project=${GCLOUD_PROJECT}"""
                    }
                }

            }
                
        }

    }
}