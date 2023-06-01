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
                 def dockerize = args.dockerize
                 sh "docker build -t test ."
               }
             }
           }
       }
   }
}
