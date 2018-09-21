pipelineJob("simple-maven-app"){

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

pipelineJob("spring-playground"){

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
            
            scriptPath("pipeline-scripts/spring-playground.groovy")

        }
    }
}

