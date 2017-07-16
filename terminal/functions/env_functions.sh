#@IgnoreInspection BashAddShebang

function assembleBuildType() {
    BUILD_TYPE="$1"
    suffix=`echo ${BUILD_TYPE:0:1} | tr '[:lower:]' '[:upper:]'`${BUILD_TYPE:1}
    # Generation of signed APK
    ./gradlew app:assemble${suffix} -Pkeyalias=didekindroid -Pkeypassword=didekin_00_droid_11 \
               -Pkeystore=/Users/pedro/keystores/didekindroid_release/didekindroid_jks -Pstorepassword=droid_11_jks_00
    echo "gradlew assemble${suffix} exit code = $?"
    mv app/build/outputs/apk/app-${BUILD_TYPE}.apk app/releases/${BUILD_TYPE}/
}

# ..................................................................................................

function checkEnvironmentArgs() {
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
    checkEnvironmentArgs $1
    export GITREMOTE=didekindroid
}

export -f setArgsCloseEnv

# ..................................................................................................

function setArgsInitEnvironments() {
    checkEnvironmentArgs "$1"
    export GITREMOTE=didekindroid
    export DIDEKIN_WEB_HOME=/Users/pedro/Documents/git_projects/didekin_web/releases/local
}

export -f setArgsInitEnvironments

# ..................................................................................................





