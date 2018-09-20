pipelineJob("simple-maven-app"){

    // Pass the job these variables
    parameters {
        stringParam(name = "GIT_REPO_URL", defaultValue = "https://github.com/FerdinandYeboah/simple-java-maven-app", description = "URL of git repo to checkout") 
    }

    //Run the job on github changes
    triggers {

    }
    
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                    	url("https://github.com/Blog-Tutorials-Code/jenkins-pipeline")
                    }
                    
                    branch('master')
                    //Blank configuration. Removes default git tagging.
                    extensions { }
                    
                    //Look into cloneWorkspace to possibly be able to clone the app source code here.
                }
            }
            
            scriptPath("pipeline-scripts/simple-maven-app.groovy")

        }
    }
}
