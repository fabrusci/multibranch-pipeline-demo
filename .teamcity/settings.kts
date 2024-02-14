import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.provideAwsCredentials
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    vcsRoot(HttpsGithubComMkjetbrainsSimpleMavenSampleRefsHeadsMaster)

    buildType(Build)

    features {
        awsConnection {
            id = "AwsAbruscidemo"
            name = "AWS abruscidemo"
            regionName = "eu-central-1"
            credentialsType = static {
                accessKeyId = "AKIAYQMCOLTCMPYCHTFF"
                secretAccessKey = "zxxb3608879e5ee97eb53c52c3e0ee01960ada0272535656c5095544cd0d9fd9850813f2c40b913c9d6775d03cbe80d301b"
                stsEndpoint = "https://sts.eu-central-1.amazonaws.com"
            }
            allowInSubProjects = true
            allowInBuilds = true
        }
    }
}

object Build : BuildType({
    name = "Build"

    params {
        text("env.pippo", "pluto", label = "pippo", display = ParameterDisplay.PROMPT, allowEmpty = true)
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Directory"
            id = "Directory"
            scriptContent = """
                asdf update
                # env
                # aws sts get-caller-identity
                env | grep '^TEAM'
            """.trimIndent()
            dockerImage = "fabrusci/ssh-agent:jdk21-asdf"
            dockerRunParameters = "-u jenkins"
        }
        script {
            name = "Lis paramters"
            id = "Lis_paramters"
            scriptContent = """
                echo "All TeamCity Parameters:"
                echo "------------------------"
                echo "%teamcity.agent.home.dir%"
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = "+:feature"
        }
    }

    features {
        perfmon {
        }
        provideAwsCredentials {
            awsConnectionId = "AwsAbruscidemo"
        }
    }
})

object HttpsGithubComMkjetbrainsSimpleMavenSampleRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/mkjetbrains/SimpleMavenSample#refs/heads/master"
    url = "https://github.com/mkjetbrains/SimpleMavenSample"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
})
