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
                steps {
                    script {
                        def dockerize = args.dockerize
                        sh "docker rmi -f test"
                        sh "docker build -t test ."
                    }
                }
            }
            stage("Docker Run") {
                steps {
                    script {
                        def userInput = input(
                            message: 'Proceed with Docker Run?',
                            parameters: [
                                [$class: 'ChoiceParameterDefinition',
                                 choices: 'Yes\nNo',
                                 description: 'Select Yes to proceed or No to abort',
                                 name: 'Docker_RUN_CONFIRMATION']
                            ]
                        )
                        if (userInput == 'Yes') {
                            sh "docker rm -f testcontainer"
                            sh "docker run -d --name testcontainer -p 80:8080 test "
                        } else {
                            error "Docker list aborted by user"
                        }
                    }
                }
            }
        }
    }
}
