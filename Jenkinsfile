pipeline {

    agent {
          
            docker { image 'jenkins/jnlp-slave:latest' 
                    //args    '-u root'
                   }
          }

          parameters {
        string(name: 'AWSCLI_VERSION', defaultValue: 'latest', description: 'AWSCLI Version to install')

        // text(name: 'AWSCLI_VERSION', defaultValue: 'latest', description: 'Enter some information about the person')
        // booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Toggle this value')
        // choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')
        //password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
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
        
        stage('Setup tools') {
            steps {
                environment { 
                PATH='$PATH:/home/jenkins/.asdf/bin'
                ASDF_DATA_DIR='/home/jenkins/.asdf'
                ASDF_DEFAULT_TOOL_VERSIONS_FILENAME='/home/jenkins/.tool-versions'
            }
                script {
                def asdfPath = '/home/jenkins/.asdf/asdf.sh'
                sh """         
                env
                ls -la
                pwd
                echo "Update asdf"
                asdf update
                echo "Install awscli plugin"
                echo "AWSCLI version : ${AWSCLI_VERSION}"
                asdf plugin add awscli 
                asdf install awscli ${AWSCLI_VERSION}
                asdf local awscli ${AWSCLI_VERSION}
                asdf reshim awscli
                """
                }
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
