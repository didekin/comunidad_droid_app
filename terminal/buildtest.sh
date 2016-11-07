# It must be executed after 'cddroid' with ./terminal/buildtest.sh  environment emulator suite version
# environment('local','awspre') emulator ('geny','google', 'physical') suite('cm','in','us','all')

#!/bin/bash
[ $# -ne 4 ] && { echo "args count should be 4" 1>&2; exit 1;}

source ./terminal/env_init.sh  $1 $2

SUITE="$3"
VERSION="$GITREMOTE-$4"
echo "Suite:" $SUITE

./gradlew clean

if [ $ENV == "$LOCAL_ENV" ] ; then
    echo "Git: add, commit, push local ..."
    git add .
    git commit -m "version $VERSION"
fi

if [ $ENV = "$AWSPRE_ENV" ] ; then
    echo "Git: merge with local, push aws_pre ..."
    git merge localdev  -m "version $VERSION"
fi

case "$SUITE" in
    cm) ./gradlew --debug app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite ;;
    in) ./gradlew --debug app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.suite.IncidSupportSuite,com.didekindroid.incidencia.suite.IncidFunctionalSuite;;
    us) ./gradlew --debug app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite,com.didekindroid.usuario.suite.UserFunctionalSuite,com.didekindroid.usuario.suite.UserFunctionalSlowSuite;;
    all) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.AppFullSuite;;
    *) echo "Invalid suite";;
esac
echo "gradlew exit = $?"

echo "pushing code to $BRANCH"
git push $GITREMOTE $BRANCH

assembleAndRelease

source ./terminal/env_close.sh  $ENV $EMULATOR