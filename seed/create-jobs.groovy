pipelineJob("simple-maven-app"){
    
    definition {
        cpsScm {
            scm {
            	github("FerdinandYeboah/simple-java-maven-app")
            }
            
            scriptPath("jenkins/Jenkinsfile")
        }
    }
}
