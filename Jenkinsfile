pipeline {

    agent {
          
            docker { image 'jenkins-jnlp-agent:latest' 
                     args    '-u 108:117'
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
                #!/bin/bash
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
                #!/bin/bash
                ls
                echo "Running Unit Tests"
                """
            }
        }
        
        stage('Setup tools') {
            environment { 
                ASDF_DIR = '/home/jenkins/.asdf'
                ASDF_PATH = '/home/jenkins/.asdf'
                ASDF_BIN = "${ASDF_PATH}/bin"
                ASDF_DATA_DIR = '/home/jenkins/.asdf'
                ASDF_DEFAULT_TOOL_VERSIONS_FILENAME = '/home/jenkins/.tool-versions'
            }
            steps {               
                script {
                
                withEnv([
                        "ASDF_DIR=${ASDF_DIR}",
                        "ASDF_PATH=${ASDF_PATH}",
                        "ASDF_BIN=${ASDF_BIN}",
                        "ASDF_DATA_DIR=${ASDF_DATA_DIR}",
                        "ASDF_DEFAULT_TOOL_VERSIONS_FILENAME=${ASDF_DEFAULT_TOOL_VERSIONS_FILENAME}"
                    ]) {
                        sh """
                           #!/bin/bash        
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
        }

        stage('Code Analysis') {
            steps {
                sh """
                #!/bin/bash
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
                #!/bin/bash
                echo "Building Artifact"
                aws sts get-caller-identity
                """
                
                sh """
                #!/bin/bash
                echo "Deploying Code"
                """
                }
            }
        }

    }   
}
