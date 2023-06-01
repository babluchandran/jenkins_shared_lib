def call(Map args) {
  pipeline {
       agent any
       stages {
           stage("Tools initialization") {
               steps {
                   sh "mvn --version"
                   sh "java -version"
                   sh "echo ${args.name}"
               }
           }
           stage("Checkout Code") {
               steps {
                   git branch: 'master',
                       url: "${args.repoUrl}"
               }
           }
           stage("Maven Build") {
               steps {
                   sh "cd demo && mvn clean install -DskipTests"
               }
           }
           stage("Docker Build") {
              when {
                expression { args.dockerize == "True" }
              }             
             steps{
               script {
                 input {
                    message 'Proceed with docker build?'
                    parameters {
                        choice(
                            choices: 'Yes\nNo',
                            description: 'Select Yes to proceed or No to abort',
                            name: 'DOCKER_BUILD_CONFIRMATION'
                        )
                    }
                }
                 def dockerize = args.dockerize
                 sh "docker build -t test ."
               }
             }
           }
       }
   }
}
