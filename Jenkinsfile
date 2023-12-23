pipeline {

    agent {
          
            docker { image 'aws-pulumi-tools-image:latest' 
                    args    '-u root'
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

        stage('Unit Testing') {
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
                whoami
                git clone --branch v0.13.1 --depth 1 https://github.com/asdf-vm/asdf.git "${HOME}/.asdf"
                echo -e '\nsource $HOME/.asdf/asdf.sh' >> "${HOME}/.bashrc"
                echo -e '\nsource $HOME/.asdf/asdf.sh' >> "${HOME}/.profile"
                source "${HOME}/.asdf/asdf.sh"
                rm -rf /var/tmp/* /tmp/* /var/tmp/.???* /tmp/.???*

                echo ${HOME}
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
