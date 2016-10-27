# It must be executed after 'cddroid' with ./terminal/buildtest.sh  environment emulator suite  version
# environment('local','dbpre','awspre') emulator ('geny','google', 'physical') suite('cm','in','us','all')

#!/bin/bash
[ $# -ne 4 ] && { echo "args count should be 4" 1>&2; exit 1;}
ENV="$1"
EMULATOR="$2"
SUITE="$3"
VERSION="didekindroid-$4"
GITREMOTE=didekindroid

echo "Suite:" $SUITE

source ./terminal/env_init.sh  $ENV $EMULATOR

./gradlew clean

if [ $ENV == "local" ] || [ $ENV == "dbpre" ] ; then
    echo "Git: add/commit/push local ..."
    git add .
    git commit -m "version $VERSION"
    git push $GITREMOTE localdev
fi

if [ $ENV = "awspre" ] ; then
    echo "Git: merge local-awspre / push ..."
    git merge localdev  -m "version $VERSION"
    git push $GITREMOTE aws_pre
fi

case "$SUITE" in
    cm) ./gradlew --debug app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite ;;
    in) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.suite.IncidSupportSuite,
                                                                             com.didekindroid.incidencia.suite.IncidFunctionalSuite;;
    us) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite,com.didekindroid.usuario.suite.UserFunctionalSuite,com.didekindroid.usuario.suite.UserFunctionalSlowSuite;;
    all) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.AppFullSuite;;
    *) echo "Invalid suite";;
esac

echo " gradlew exit = $?"

source ./terminal/env_close.sh