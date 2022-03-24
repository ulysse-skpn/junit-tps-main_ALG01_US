pipeline {
   agent any
    stages {
	
		stage('Checkout') {
			steps {
				checkout scm
			}
		}
		stage('Clean Install Project') {
			steps {			
                       withMaven(maven : 'maven-3.5.4') {
                        sh 'mvn clean install -Dmaven.test.skip=true'
                        }
			}
		}
		stage('Analyze with SonarQube') {
			steps {
	       withSonarQubeEnv(installationName:'indyli-sonarqube') {
		       withMaven(maven : 'maven-3.5.4') {
	                        sh 'mvn -Dmaven.test.skip=true -Dsonar.login=fb183f8c8bda32ec791d762a3e8fe3b4acd91690 sonar:sonar'
	                }	
                }
		    }
	     }
	}
}