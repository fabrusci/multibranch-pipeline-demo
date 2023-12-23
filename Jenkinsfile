pipeline {

    agent {
          
            docker { image 'dadsgarage/dadsgarage:latest' 
                     
                   }
          }
environment {
        AWS_DEFAULT_REGION    = "eu-central-1"
        
    }
    options {
        buildDiscarder logRotator( 
                    daysToKeepStr: '16', 
                    numToKeepStr: '10'
            )
    }

    stages {
        
        stage('Cleanup Workspace') {
            steps {
                //cleanWs()
                sh """
                echo "Cleaned Up Workspace For Project"
                """
            }
        }

        stage('Code Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM', 
                    branches: [[name: '*/main']], 
                    userRemoteConfigs: [[url: 'https://github.com/fabrusci/multibranch-pipeline-demo.git']]
                ])
            }
        }

        stage(' Unit Testing') {
            steps {
                sh """
                ls
                echo "Running Unit Tests"
                """
            }
        }
        
        stage(' Setup tools') {
            steps {
                sh """
                # whoami
                pwd
                ls -la
                echo "Update asdf"
                asdf update
                echo "Install awscli plugin"
                asdf plugin add awscli
                asdf install awscli latest
                asdf reshim awscli
                """
            }
        }

        stage('Code Analysis') {
            steps {
                sh """
                echo "Running Code Analysis"
                """
            }
        }

        stage('Build Deploy Code') {
            when {
                branch 'develop'
            }
            steps {

                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',credentialsId: "aws-credential-abruscidemo"]]) {
               
                sh('env')
                sh """
                echo "Building Artifact"
                aws sts get-caller-identity
                """
                
                sh """
                echo "Deploying Code"
                """
                }
            }
        }

    }   
}
