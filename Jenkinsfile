pipeline {

    agent {
          
            docker { image 'jenkins-dads-agent:latest' 
                     // args    '-u 1000:1000  --privileged'
                   }
          }

        //parameters {
        //string(name: 'AWSCLI_VERSION', defaultValue: '2.15.4', description: 'AWSCLI Version to install')
        //  }
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
    

        stage('Parameter')
        {
            steps{
                
                    script {
                    properties([
                            parameters([
                                [$class: 'ChoiceParameter', 
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select the Environemnt from the Dropdown List', 
                                    filterLength: 1, 
                                    filterable: false, 
                                    name: 'Env', 
                                    script: [
                                        $class: 'GroovyScript', 
                                        fallbackScript: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['Could not get The environemnts']"
                                        ], 
                                        script: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['dev','stage','prod']"
                                        ]
                                    ]
                                ],
                                string(name: 'AWSCLI_VERSION' ? params.AWSCLI_VERSION : '2.15.14'  , description: 'AWSCLI Version to install')
                            ])
                        ])
                    }
                
            }
        }
        stage('Setup tools') {
             environment{
                name = sh(script:"echo 'ddddd' | cut -d',' -f1",  returnStdout: true).trim()
            }
            steps {               
                script {
                
                
                        sh (
                            script: """#!/bin/bash        
                                    set -x
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
                                    asdf plugin-add terraform https://github.com/asdf-community/asdf-hashicorp.git
                                    asdf install terraform 1.4.6
                                    asdf local terraform 1.4.6
                                    asdf reshim
                                    """
                           )               
                }

                script {

                        sh (
                            script: """#!/bin/bash        
                                    echo " Second script"
                                    """
                           )               
                }
            }
        }


        stage('Build Deploy Code') {
            when {
                branch 'develop'
            }
            steps {

                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',credentialsId: "aws-credential-abruscidemo"]]) {
               
                sh('env')
                sh ( 
                    script: """#!/bin/bash
                            echo "Check AWS credential"
                            aws sts get-caller-identity
                            """
                    )               
                sh (
                    script: """#!/bin/bash
                            echo "Check terraform version"
                            terraform version
                            """
                    )
                }
            }
        }

    }   
}
