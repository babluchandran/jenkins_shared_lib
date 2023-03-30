def call(Map args) {
    node {
        stage("Checkout") {
            checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'git_credentials', url: 'args.repo']])
        }

        stage("Compile") {
            sh "mvn install -DskipTests"
        }
		
        stage("Docker Build") {
            sh "docker build -t test ."
        }		

    }
}
