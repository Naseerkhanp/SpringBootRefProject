pipeline {
    agent any
	
	tools { 
        maven 'Maven-3.3.9' 
        jdk 'jdk1.8.0_151' 
    }	
	
    stages {
        
		stage ('Build') {
            steps {
                echo 'Maven build is in progress.'
				sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
				sh 'mvn clean install -Dmaven.test.skip=true'
            }
}
		stage ('Deploy Notify') {
            steps {
            // send to email
            emailext (
            from: 'NSL-PERF-CICD@excelacom.in',
			to: 'devops@excelacom.in',
			subject: "NSL PERF msg-svc-cback Inbound Service Deployment STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
             body: '${FILE,path="Templates/Initiation.html"}',
            recipientProviders: [[$class: 'RequesterRecipientProvider']]
          )
      }
    }
	
	
       stage('Container Build') {
            steps {
			script{
                echo 'Contianer Build has been started...'
				docker.build('nsl_perf' + ':msg-svc-cback_v$BUILD_NUMBER')
            }
			}
        }
        stage('Container Upload') {
            steps {
			script{
                echo 'Image Push has been started...'
				 
				docker.withRegistry('https://197504309469.dkr.ecr.us-east-1.amazonaws.com', 'ecr:us-east-1:AWS-ECR-Upload-PERF')
				{
               docker.image('nsl_perf' + ':msg-svc-cback_v$BUILD_NUMBER').push('msg-svc-cback_v$BUILD_NUMBER')
            }
        }
		}
		}
        stage('Container Cleanup') {
            steps {
            	echo 'Image cleanup has been started...'
				sh "docker rmi nsl_perf:msg-svc-cback_v${env.BUILD_NUMBER}"
				sh "docker rmi 197504309469.dkr.ecr.us-east-1.amazonaws.com/nsl_perf:msg-svc-cback_v${env.BUILD_NUMBER}"
                  }
		}

}
post {
    success {
      emailext (
          from: 'NSL-PERF-CICD@excelacom.in',
		  to: 'devops@excelacom.in',
		  attachLog: true,
		  //attachmentsPattern: 'CodeQuality.txt',
		  subject: "NSL PERF msg-svc-cback Inbound Service Deployment SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: '${FILE,path="Templates/Completion.html"}',
          recipientProviders: [[$class: 'RequesterRecipientProvider']]
        )
    }

    failure {
      emailext (
          from: 'NSL-PERF-CICD@excelacom.in',
		  to: 'devops@excelacom.in',
		  attachLog: true,
		  //attachmentsPattern: 'CodeQuality.txt',
		  subject: "NSL PERF msg-svc-cback Inbound Deployment FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: '${FILE,path="Templates/Failure.html"}',
          recipientProviders: [[$class: 'RequesterRecipientProvider']]
        )
    }
  }
  }