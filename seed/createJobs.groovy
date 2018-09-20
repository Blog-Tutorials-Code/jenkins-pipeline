pipelineJob("simple-maven-app"){
    
    definition {
        cpsScm {
            scm {
                git("https://github.com/Blog-Tutorials-Code/jenkins-pipeline", "master")
            }
            
            scriptPath("pipeline-scripts/simple-maven-app.groovy")
        }
    }
}
