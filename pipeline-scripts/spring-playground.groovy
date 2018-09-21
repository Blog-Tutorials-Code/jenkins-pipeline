// Some global variables I have to figure out the best place to put or read them from
def GCLOUD_PROJECT = "sharp-terminal-215500"
def REGION = "us-east1-b"
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
                DOCKER_HUB_USERNAME = credentials("DOCKER_HUB_LOGIN_USR")
                DOCKER_HUB_PASSWORD = credentials("DOCKER_HUB_LOGIN_PSW")
            }

            steps {
                sh "docker login -u $DOCKER_HUB_USERNAME -p $DOCKER_HUB_PASSWORD"
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
        

        // stage ("Deploy to GCP"){
        //     steps {
        //         // Deploy. Don't forget to set project, necessary firewall rules etc. Maybe should use update-container since ALB already set on the spring-playground vm.
        //         // I believe I have two options. Update the instance unmanaged group container, or update the vm instance directly. Had to create an instance group because of the LB.
        //         // Update: The above is incorrect, I created an UNMANAGED instance group (not based on a template) so I have to update the instance directly
        //         // What to do about first case where container not deployed and should be created and afterwards should be updated?
        //         // LATER: DEPLOY all infrastructure as code (i.e ALB). Does google have something like AWS CloudFormation?

        //         //Seems like docker image must be remotely hosted. Cannot be on local machine and referenced in --container-image
        //         sh " gcloud compute instances create-with-container [INSTANCE_NAME] \
        //             --container-image [DOCKER_IMAGE]"

        //         // Add appropiate firewall tags. Add the project flag as well
        //         sh "gcloud compute instances add-tags [INSTANCE-NAME] \
        //             --zone [ZONE] \
        //             --tags [TAGS]"



        //         //Could try catch and create-with-container if get an error then use update-with-container
        //     }    
        // }

    }
}