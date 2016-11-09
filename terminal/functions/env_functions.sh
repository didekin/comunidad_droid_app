#@IgnoreInspection BashAddShebang

function assembleAndRelease() {
    # Generation of signed APK
    ./gradlew app:assembleRelease -Pkeyalias=didekindroid -Pkeypassword=didekin_00_droid_11 \
       -Pkeystore=/Users/pedro/keystores/didekindroid_release/didekindroid_jks -Pstorepassword=droid_11_jks_00
    echo "gradlew assembleRelease exit code = $?"

    mv app/build/outputs/apk/app-release.apk app/releases/${ENV}/
}

# ..................................................................................................

function checkEnvEmulatorArgs() {
    export ENV="$1"
    if ! [ ${ENV} = "local" ] && ! [ ${ENV} = "awspre" ] ; then
        echo "Wrong type of environment: $ENV" 1>&2; exit 1;
    fi
}

# ..................................................................................................

function checkParamsPro() {
    ACTION="$1"
    echo "Action: $ACTION"
    RELEASE="release"
    TEST="test"
    INSTALL="install"
    if ! [ ${ACTION} = "$RELEASE" ] && ! [ ${ACTION} = "$TEST" ] && ! [ ${ACTION} = "$INSTALL" ] ; then
        echo "Wrong type of action: $ACTION" 1>&2; exit 1;
    fi

    export ENV="pro"
    export BRANCH="master"
    GITREMOTE=didekindroid
    VERSION="$GITREMOTE-$2"
}

# ..................................................................................................

function setArgsCloseEnv() {
    checkEnvEmulatorArgs $1
    setEnvEmulatorVariables
}

export -f setArgsCloseEnv

# ..................................................................................................

function setArgsInitEnvironments() {
    checkEnvEmulatorArgs "$1"
    setEnvEmulatorVariables
    export DIDEKINSPRING_HOME=/Users/pedro/Documents/git_projects/didekinspring/releases/${LOCAL_ENV}
}

export -f setArgsInitEnvironments

# ..................................................................................................

function setEnvEmulatorVariables() {
     export LOCAL_ENV="local"
     export AWSPRE_ENV="awspre"
     export APP_PARAM_HOME=app/src/debug/res/values
     export BKS_HOME=app/src/debug/res/raw
     export GITREMOTE=didekindroid
     echo "Environment: $ENV"
}




