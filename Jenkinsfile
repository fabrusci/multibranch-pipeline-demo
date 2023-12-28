pipeline {

    agent {
          
            docker { image 'jenkins-dads-agent:latest' 
                     // args    '-u 1000:1000  --privileged'
                   }
          }

          parameters {
        string(name: 'AWSCLI_VERSION', defaultValue: '2.15.4', description: 'AWSCLI Version to install')

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
        
       //stage('Cleanup Workspace') {
       //    steps {
       //        //cleanWs()
       //        sh """
       //        #!/bin/bash
       //        echo "Cleaned Up Workspace For Project"
       //        """
       //    }
       //}

        //stage('Code Checkout') {
        //    steps {
        //        checkout([
        //            $class: 'GitSCM', 
        //            branches: [[name: '*/main']], 
        //            userRemoteConfigs: [[url: 'https://github.com/fabrusci/multibranch-pipeline-demo.git']]
        //        ])
        //    }
        //}

        //stage('Unit Testing') {
        //    steps {
        //        sh """#!/bin/bash
        //        ls
        //        echo "Running Unit Tests"
        //        """
        //    }
        //}
        
        stage('Setup tools') {
            
            steps {               
                script {
                
                
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
