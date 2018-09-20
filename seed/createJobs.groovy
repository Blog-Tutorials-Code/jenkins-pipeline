pipelineJob("simple-maven-app"){
    
    definition {
        cpsScm {
            scm {
                git("/home/Desktop/Projects/jenkins-pipeline", "master")
            }
            
            scriptPath("pipeline-scripts/simple-maven-app.groovy")
        }
    }
}
