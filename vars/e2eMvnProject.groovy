def call(body) {
    def params= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()	
    node {
        stage("Checkout") {
            checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'git_credentials', url: '${params.url}']])
        }

        stage("Compile") {
            sh "cd demo && mvn install -DskipTests"
        }
		
        stage("Docker Build") {
            sh "docker build -t test ."
        }		

    }
}
