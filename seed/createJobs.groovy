pipelineJob("simple-maven-app"){
    
    definition {
        cpsScm {
            scm {
            	github("Blog-Tutorials-Code/jenkins-pipeline", "master")
            }
            
            scriptPath("pipeline-scripts/simple-maven-app.groovy")
        }
    }
}
