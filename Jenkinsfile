#!groovy
try {
  node ('java-1.8'){
    stage('Checkout'){
      git 'https://github.com/Pixxis/zoufzouf.git'
    }
    stage('Build'){
      sh "./gradlew clean build"
    }
  }
} catch (ex) {

} finally {}
