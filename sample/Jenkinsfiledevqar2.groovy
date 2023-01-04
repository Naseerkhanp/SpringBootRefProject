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
            from: 'NSL-DEVQAR2-CICD@excelacom.in',
			to: 'nsl_devops@excelacom.in',
			subject: "NSL DEVQAR2 sb-ne-apollo-v2 Deployment STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            body: '${FILE,path="Templates/Initiation.html"}',
            recipientProviders: [[$class: 'RequesterRecipientProvider']]
          )
      }
    }
	stage('SonarQube Analysis') {
            steps {
              withSonarQubeEnv('SonarQube') {
              sh "/app/sonar-runner-2.4/bin/sonar-runner -D.sonar.projectBaseDir=/app/Jenkins/jenkins-home-devqar2/workspace/sb-ne-apollo-v2 -Dsonar.sourceEncoding=UTF-8 -Dsonar.dynamicAnalysis=reuseReports -Dsonar.sources=/app/Jenkins/jenkins-home-devqar2/workspace/sb-ne-apollo-v2 -Dsonar.language=java -Dsonar.projectName=NSL_DEVQAR2_sb-ne-apollo-v2 -Dsonar.projectKey=NSL_DEVQAR2_sb-ne-apollo-v2 -Dsonar.projectVersion=7.1"
        }
     }
    }
	stage ("Sonar fetch"){
          steps {
            sh '''
			>/app/Outbound/sb-ne-apollo-v2/summary.txt
			/app/SQ.sh NSL_DEVQAR2_nb-sb-ne-apollo-v2 > /app/Outbound/sb-ne-apollo-v2/summary.txt
			cp -r /app/Outbound/sb-ne-apollo-v2/summary.txt ${WORKSPACE}/CodeQuality.txt
			
			'''
            
			}
			}
	 stage ("Quality Gate"){
          steps {
            sleep(time: 120, unit: "SECONDS")
             waitForQualityGate abortPipeline:true
          }
        }
        stage('Container Build') {
            steps {
			script{
                echo 'Container Build has been started...'
				docker.build('nsl-devqar2' + ':sb-ne-apollo-v2_v$BUILD_NUMBER')
            }
			}
        }
        stage('Container Upload') {
            steps {
			script{
                echo 'Image Push has been started...'
				 
				docker.withRegistry('https://852563228231.dkr.ecr.us-east-1.amazonaws.com', 'ecr:us-east-1:AWS-ECR-Upload')
				{
               docker.image('nsl-devqar2' + ':sb-ne-apollo-v2_v$BUILD_NUMBER').push('sb-ne-apollo-v2_v$BUILD_NUMBER')
            }
        }
		}
		}
        stage('Container Cleanup') {
            steps {
            	echo 'Image cleanup has been started...'
				sh "docker rmi nsl-devqar2:sb-ne-apollo-v2_v${env.BUILD_NUMBER}"
				sh "docker rmi 852563228231.dkr.ecr.us-east-1.amazonaws.com/nsl-devqar2:sb-ne-apollo-v2_v${env.BUILD_NUMBER}"
                  }
		}
		stage('NSL DEVQAR2 - Configmap Inject') {
			steps {
			echo 'Applying QAR1 Properties and Datasource Configurations...'
			sh 'kubectl create configmap devqar2-sb-ne-apollo-v2-properties --from-file=propertiesdevqar2 -n default -o yaml --dry-run | kubectl apply -f -'
			sh 'kubectl get configmaps devqar2-sb-ne-apollo-v2-properties -o yaml -n default'
                  }
        }
		stage('NSL DEVQAR2 - EKS POD Deploy') {
			steps {
			echo 'sb-ne-apollo-v2 POD Deployment has been started...'
			sh 'cat devqar2-sb-ne-apollo-v2-deployment.yaml | sed "s/{{BUILD_NUMBER}}/$BUILD_NUMBER/g" | kubectl apply -f -'
			sh 'kubectl apply -f devqar2-sb-ne-apollo-v2-service.yaml'
			//sh 'kubectl apply -f devqar2-validate-service-apm.yaml'
                  }
        }
		stage('NSL DEVQAR2 - EKS POD Status') {
		    steps {
			echo 'sb-ne-apollo-v2 POD Status is being monitored...'
			sleep(time: 60, unit: "SECONDS")
			sh 'kubectl get pods -A | grep sb-ne-apollo-v2-deployment'
			
			      }
		}
}
post {
    success {
      emailext (
          from: 'NSL-DEVQAR2-CICD@excelacom.in',
		  to: 'nsl_devops@excelacom.in',
		  attachLog: true,
		  attachmentsPattern: 'CodeQuality.txt',
		  subject: "NSL DEVQAR2 sb-ne-apollo-v2 Deployment SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: '${FILE,path="Templates/Completion.html"}',
          recipientProviders: [[$class: 'RequesterRecipientProvider']]
        )
    }

    failure {
      emailext (
          from: 'NSL-DEVQAR2-CICD@excelacom.in',
		  to: 'nsl_devops@excelacom.in',
		  attachLog: true,
		  attachmentsPattern: 'CodeQuality.txt',
		  subject: "NSL DEVQAR2 sb-ne-apollo-v2 Deployment FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: '${FILE,path="Templates/Failure.html"}',
          recipientProviders: [[$class: 'RequesterRecipientProvider']]
        )
    }
  }
  }