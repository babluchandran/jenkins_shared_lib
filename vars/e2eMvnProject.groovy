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
			stage("Docker Build") {
				steps {
					input {
						message "Proceed with Docker build?"
						parameters {
							choice(
								choices: 'Yes\nNo',
								description: 'Select Yes to proceed or No to abort',
								name: 'Docker_BUILD_CONFIRMATION'
							)
						}
					}
					script {
						if (params.Docker_BUILD_CONFIRMATION == 'Yes') {
							sh "docker ls" // Replace with your Docker build commands
						} else {
							error "Docker build aborted by user"
						}
					}
				}
			}		   
       }
   }
}
