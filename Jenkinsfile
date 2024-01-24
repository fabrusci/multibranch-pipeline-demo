pipeline {

    agent {label  'agent1'}

    //parameters {
    //string(name: 'AWSCLI_VERSION', defaultValue: '2.15.4', description: 'AWSCLI Version to install')
    //  }
    environment {
        AWS_DEFAULT_REGION    = 'eu-central-1'
    }
    options {
        buildDiscarder logRotator(
                    daysToKeepStr: '2',
                    numToKeepStr: '4'
            )
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
            //agent {
            //docker { image 'jenkins-dads-agent:latest'
            // args    '-u 1000:1000  --privileged'
            //reuseNode true
            // }
            //label  'agent1'
            // }
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
                                string(name: 'TERRAFORM_VERSION', defaultValue: params.TERRAFORM_VERSION ? params.TERRAFORM_VERSION : '1.4.6', description: 'TERRAFORM Version to install'),
                            //choice(name: 'ENVIRONMENT', choices: [params.CHOICE, 'One', 'Two', 'Three'], description: 'Pick something')
                            ])
                        ])
                    }
            }
        }
        stage('Setup tools') {

            // agent {
                //docker { image 'jenkins-dads-agent:latest'
                // args    '-u 1000:1000  --privileged'
                // reuseNode true
                // }
                // label  'agent1'
            // }
            // environment{
            //    name = sh(script:"echo 'ddddd' | cut -d',' -f1",  returnStdout: true).trim()
            //   }
            steps {
                script {

                        sh(
                            script: '''#!/bin/bash
                                    set -x
                                    ls -la
                                    pwd
                                    echo "Update asdf"
                                    asdf update
                                    echo "Install awscli plugin"
                                    echo "AWSCLI version : ${AWSCLI_VERSION}"
                                    asdf plugin add awscli
                                    asdf install awscli ${AWSCLI_VERSION}
                                    asdf local awscli ${AWSCLI_VERSION}
                                    echo "TERRAFORM version : ${TERRAFORM_VERSION}"
                                    asdf plugin-add terraform https://github.com/asdf-community/asdf-hashicorp.git
                                    asdf install terraform ${TERRAFORM_VERSION}
                                    asdf local terraform ${TERRAFORM_VERSION}
                                    asdf reshim
                                    env
                                    '''
                           )
                }
                
                script {
                        sh(
                            script: '''#!/bin/bash
                                    set -x
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

        stage('Manual Intervention') {

            // agent {
                 // docker { image 'jenkins-dads-agent:latest'
                 // args    '-u 1000:1000  --privileged'
                //reuseNode true
                // }
            // label  'agent1'
            // }

            when {
                branch 'develop'
                // beforeAgent true
            }

            steps {
                unstash 'pippo'  // unstash 
                script {
                    // Pause the pipeline and wait for manual input
                    def userInput = input(id: 'manual-input', message: 'Proceed with the next stage?', parameters: [string(defaultValue: '', description: 'Comments', name: 'Comments')])

                    // Check the user input
                    if (userInput == 'abort') {
                        error('Manual intervention aborted the pipeline.')
                    } else {
                        echo "User comments: ${userInput}"
                    }
                }
            }
        }

        stage('Build Deploy Code') {

             // agent {
                  //docker { image 'jenkins-dads-agent:latest'
                  // args    '-u 1000:1000  --privileged'
                  //reuseNode true
                  //}
                  // label  'agent1'
             // }
            // when {
            //    branch 'main'
            // }
            environment {
                           TF_IN_AUTOMATION    = 1
                        }
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${env.BRANCH_NAME}-aws-credential"]]) {
                    sh('env')
                    sh(
                    script: '''#!/bin/bash
                               #echo "Update asdf"
                               #asdf update
                               #echo "Install awscli plugin"
                               #echo "AWSCLI version : ${AWSCLI_VERSION}"
                               #asdf plugin add awscli
                               #asdf install awscli ${AWSCLI_VERSION}
                               #asdf local awscli ${AWSCLI_VERSION}
                               #echo "TERRAFORM version : ${TERRAFORM_VERSION}"
                               #asdf plugin-add terraform https://github.com/asdf-community/asdf-hashicorp.git
                               #asdf install terraform ${TERRAFORM_VERSION}
                               #asdf local terraform ${TERRAFORM_VERSION}
                               #asdf reshim
                               echo "Check AWS credential"
                               aws sts get-caller-identity
                            '''
                    )
                    sh(
                    script: '''#!/bin/bash
                            echo "Check terraform version"
                            terraform version
                            '''
                    )
                }
            // cleanWs()
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
