pipeline {

    agent {label  ('agent1')}

    //parameters {
    //string(name: 'AWSCLI_VERSION', defaultValue: '2.15.4', description: 'AWSCLI Version to install')
    //  }
    environment {
        AWS_DEFAULT_REGION    = "${params.AWS_REGION}"
        AWS_REGION            = "${params.AWS_REGION}"
        ACTION                = "${params.ACTION}"
        TF_IN_AUTOMATION      = 1
        STACK                 = ""
    }
    options {
        buildDiscarder logRotator(
                    daysToKeepStr: '2',
                    numToKeepStr: '4'
            )
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        // newContainerPerStage()
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

        stage('Setup parameters')
        {
            
            steps {
                    script {
                    properties([
                            parameters([
                                //$class: 'ChoiceParameter',
                                //   choiceType: 'PT_SINGLE_SELECT',
                                //   description: 'Select the Environemnt from the Dropdown List',
                                //   filterLength: 1,
                                //   filterable: false,
                                //   name: 'Env',
                                //   script: [
                                //       $class: 'GroovyScript',
                                //       fallbackScript: [
                                //           classpath: [],
                                //           sandbox: false,
                                //           script:
                                //               "return['Could not get The environemnts']"
                                //       ],
                                //       script: [
                                //           classpath: [],
                                //           sandbox: false,
                                //           script:
                                //               "return['dev','stage','prod']"
                                //       ]
                                //   ]
                                //,
                                string(name: 'AWSCLI_VERSION', defaultValue: params.AWSCLI_VERSION ? params.AWSCLI_VERSION : '2.15.13', description: 'AWSCLI Version to install'),
                                string(name: 'AWS_REGION', defaultValue: params.AWS_REGION ? params.AWS_REGION : 'eu-central-1', description: 'AWS region'),
                                string(name: 'TERRAFORM_VERSION', defaultValue: params.TERRAFORM_VERSION ? params.TERRAFORM_VERSION : '1.4.6', description: 'TERRAFORM Version to install'),
                                choice (name: 'ACTION',
				                             choices: [ 'plan', 'apply', 'destroy'],
				                             description: 'Run terraform plan / apply / destroy')
                            ])
                        ])


                       // Set the STACK variable based on the Git branch
                            if ( env.BRANCH_NAME == 'feature' || env.BRANCH_NAME == 'develop') {
                                 STACK = 'dev'
                                } else if ( env.BRANCH_NAME == 'main') {
                                STACK = 'prod'
                               } else {
                                      echo "Unsupported Git branch: ${env.BRANCH_NAME}"
                                      error "Unsupported Git branch"
                                      }
                    }
            }
        }
        stage('Setup tools') {

            // environment{
            //    name = sh(script:"echo 'ddddd' | cut -d',' -f1",  returnStdout: true).trim()
            //   }
            steps {
                echo "Deploying to ${STACK} environment..."
                script {

                        sh(
                            script: '''#!/bin/bash
                                    set -x
                                    source ~/.profile
                                    echo "${STACK}"
                                    ls -la
                                    pwd
                                    echo "asdf version :"
                                    asdf version
                                    echo "Install awscli plugin"
                                    echo "AWSCLI version : ${AWSCLI_VERSION}"
                                    asdf plugin add awscli
                                    asdf install awscli ${AWSCLI_VERSION}
                                    asdf set -u awscli ${AWSCLI_VERSION}
                                    echo "TERRAFORM version : ${TERRAFORM_VERSION}"
                                    asdf plugin add terraform https://github.com/asdf-community/asdf-hashicorp.git
                                    asdf install terraform ${TERRAFORM_VERSION}
                                    asdf set -u terraform ${TERRAFORM_VERSION}
                                    asdf reshim
                                    echo "Install AWS SSM Plugin"
                                    curl "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o "session-manager-plugin.deb"
                                    sudo dpkg -i session-manager-plugin.deb
                                    env
                                    '''
                           )
                }
                
                script {
                        sh(
                            script: '''#!/bin/bash
                                    set -x
                                    source ~/.profile
                                    echo " Second script"
                                    touch pippo.txt
                                    pwd
                                    ls
                                    '''
                           )
                }
                stash name: 'pippo', includes: '*.txt'  // stash all *.txt file
            }
        }

        // stage('Manual Intervention') {
// 
        //     when {
        //         branch 'feature'
        //         beforeAgent true
        //     }
// 
        //     steps {
        //         unstash 'pippo'  // unstash 
        //         script {
        //             // Pause the pipeline and wait for manual input
        //             def userInput = input(id: 'manual-input', message: 'Proceed with the next stage?', parameters: [string(defaultValue: '', description: 'Comments', name: 'Comments')])
// 
        //             // Check the user input
        //             if (userInput == 'abort') {
        //                 error('Manual intervention aborted the pipeline.')
        //             } else {
        //                 echo "User comments: ${userInput}"
        //             }
        //         }
        //     }
        // }

        stage('Terraform init') {

             when { 
                    anyOf { 
                            branch 'main'; branch 'develop'; branch 'feature' 
                          } 
                  }
            steps {
                dir('ci') 
                {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${env.BRANCH_NAME}-aws-credential"]]) {
                    unstash 'pippo'  // unstash 
                    sh(
                    script: '''#!/bin/bash
                               echo "Check AWS credential"
                               aws sts get-caller-identity
                            '''
                    )
                    sh(
                    script: '''#!/bin/bash
                            echo "Check terraform version"
                            terraform version
                            echo "Check current directory"
                            pwd
                            '''
                    )

                    withEnv(["TF_CLI_ARGS_init=-backend-config='./backend-configs/${BRANCH_NAME}-backend-config.hcl'"]) {

                          sh(
                            script: '''#!/bin/bash
                            set -x
                            source ~/.profile
                            echo $TF_CLI_ARGS_init
                            echo "Terraform init"
                            # terraform init -backend-config=./backend-configs/${BRANCH_NAME}-backend-config.hcl -no-color --reconfigure
                            terraform init -no-color --reconfigure
                            '''
                            )
                    }
                }
                  // cleanWs()
               }
            }
        } 

        stage('Terraform plan') {

            when { 
                    allOf {
                    expression { env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'feature'}
                    expression { params.ACTION != 'destroy' }
                          }
                  }
            steps {
                dir('ci') 
                {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${env.BRANCH_NAME}-aws-credential"]]) {
                    sh('env')
                    sh(
                    script: '''#!/bin/bash
                               source ~/.profile
                               echo "Check AWS credential"
                               aws sts get-caller-identity
                            '''
                    )
                    
                    withEnv(["STACK=${STACK}"]) 
                    {
                        sh(
                         script: '''#!/bin/bash
                                    set -x
                                    source ~/.profile
                                    echo "${STACK}"
                                    terraform state pull
                                    echo "Terraform plan"
                                    terraform plan -target="module.vpc" -var "stackname=${STACK}" -out=plan.tfplan -no-color 
                                  '''
                          )
                    }
                }
                  // cleanWs()
               }
            }
        }

        stage('Terraform apply') {

            when { 
                    allOf {
                    expression { env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop' }
                    expression { params.ACTION != 'destroy' }
                          }
                  }
            steps {
                dir('ci') 
                {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${env.BRANCH_NAME}-aws-credential"]]) {
                    sh('env')
                    sh(
                    script: '''#!/bin/bash
                               source ~/.profile
                               echo "Check AWS credential"
                               aws sts get-caller-identity
                            '''
                    )

                    script {
                                    // Pause the pipeline and wait for manual input
                                    def userInput = input(id: 'manual-input', message: 'Proceed with apply ?', parameters: [string(defaultValue: '', description: 'Comments', name: 'Comments')])
                
                                    // Check the user input
                                    if (userInput == 'abort') {
                                        error('Apply aborted')
                                    } else {
                                        echo "User comments: ${userInput}"
                                    }
                                }

                     withEnv(["STACK=${STACK}"])
                         {    sh(
                              script: '''#!/bin/bash
                                         set -x
                                         source ~/.profile
                                         echo "Terraform apply"
                                         # terraform apply -target="module.vpc" -input=false -no-color -auto-approve plan.tfplan
                            '''
                            )
                    }
                }
                  // cleanWs()
               }
            }
        }
        stage('Terraform destroy') {

            when { 
                    allOf {
                    expression { env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop' }
                    expression { params.ACTION == 'destroy' }
                          }
                  }
            steps {
                dir('ci') 
                {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${env.BRANCH_NAME}-aws-credential"]]) {
                    sh('env')
                    sh(
                    script: '''#!/bin/bash
                               set -x
                               source ~/.profile
                               echo "Check AWS credential"
                               aws sts get-caller-identity
                            '''
                    )
                    sh(
                    script: '''#!/bin/bash
                            set -x
                            source ~/.profile
                            echo "Terraform state pull"
                            terraform state pull
                            echo "Terraform destroy"
                            #terraform destroy -var "stackname=${STACK}" -auto-approve -no-color -target="module.vpc"
                            '''
                    )
                }
                  // cleanWs()
               }
            }
        }
    }
    post {

        always {
            cleanWs()
        }

        failure {
            echo 'Pipeline failed'
            cleanWs()
        }
    }
}
