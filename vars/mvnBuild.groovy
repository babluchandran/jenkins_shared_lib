def call() {

  dir('spring-boot-hello-world-master'){
       sh "mvn install -DskipTests"
     }
  }


