pipelineJob("simple-maven-app"){
    
    definition {
        cpsScm {
            scm {
            	github("FerdinandYeboah/simple-java-maven-app", "master")
            }
            
            scriptPath("jenkins/Jenkinsfile")
        }
    }
}
