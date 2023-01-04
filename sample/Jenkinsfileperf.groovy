pipeline {
    agent any
	
	
	
    stages {
	
	       stage('Image Fetch') {
            steps {
            script {
            properties([
            parameters([
            string(
            name: 'Image_Name', 
            trim: true
            )
            ])
            ])
            }
            }
            }
		stage ('Deploy Notify') {
            steps {
            // send to email
            emailext (
            from: 'NSL-perf-CICD@excelacom.in',
			to: 'devops@excelacom.in',
			subject: "NSL perf msg-svc-cback Deployment STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
             body: '${FILE,path="Templates/Initiation.html"}',
            recipientProviders: [[$class: 'RequesterRecipientProvider']]
          )
      }
    }
        
		stage('NSL perf - Configmap Inject') {
			steps {
			echo 'Applying perf Properties and Datasource Configurations...'
			sh 'kubectl create configmap perf-msg-svc-cback-properties --from-file=propertiesperf -n default -o yaml --dry-run | kubectl apply -f -'
			sh 'kubectl get configmaps perf-msg-svc-cback-properties -o yaml -n default'
                  }
        }
		stage('NSL perf - EKS POD Deploy') {
			steps {
			echo 'msg-svc-cback POD Deployment has been started...'
			sh 'echo "pwd = $(pwd)"'
			sh 'sed -i "s/imagename/${Image_Name}/g" perf-msg-svc-cback-deployment.yaml'
            sh 'kubectl apply -f perf-msg-svc-cback-deployment.yaml'
			sh 'kubectl apply -f perf-msg-svc-cback-service.yaml'
			sh 'kubectl apply -f perf-msg-svc-cback-apm.yaml'
                  }
        }
		stage('NSL perf- EKS POD Status') {
		    steps {
			echo 'msg-svc-cback POD Status is being monitored...'
			sleep(time: 60, unit: "SECONDS")
			sh 'kubectl get pods -A | grep msg-svc-cback-deployment'
			
			      }
		}
}
post {
    success {
      emailext (
          from: 'NSL-perf-CICD@excelacom.in',
		  to: 'devops@excelacom.in',
		  attachLog: true,
		 // attachmentsPattern: 'CodeQuality.txt',
		  subject: "NSL perf msg-svc-cback Deployment SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: '${FILE,path="Templates/Completion.html"}',
          recipientProviders: [[$class: 'RequesterRecipientProvider']]
        )
    }

    failure {
      emailext (
          from: 'NSL-perf-CICD@excelacom.in',
		  to: 'devops@excelacom.in',
		  attachLog: true,
		 // attachmentsPattern: 'CodeQuality.txt',
		  subject: "NSL perf msg-svc-cback Deployment FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: '${FILE,path="Templates/Failure.html"}',
          recipientProviders: [[$class: 'RequesterRecipientProvider']]
        )
    }
  }
  }