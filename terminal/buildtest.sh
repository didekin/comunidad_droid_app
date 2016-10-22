# It must be executed after 'cddroid' with ./terminal/buildtest.sh  environment emulator suite
# environment('local','dbpre','awspre') emulator ('geny','google') suite('cm','in','us','all')

#!/bin/bash
[ $# -ne 3 ] && { echo "args count should be 3" 1>&2; exit 1;}

ENV="$1"
EMULATOR="$2"
SUITE="$3"
GITREMOTE=didekinspring

echo "Suite:" $SUITE

source ./terminal/env_init.sh  $ENV $EMULATOR

./gradlew clean

if [ $ENV == "local" ] || [ $ENV == "dbpre" ] ; then
    git add .
    git commit -m "version $VERSION"
    git push $GITREMOTE localdev
fi

if [ $ENV = "awspre" ] ; then
    git merge localdev  -m "version $VERSION"
    git push $GITREMOTE aws_pre
fi

case "$SUITE" in
    cm) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite ;;
    in) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.suite.IncidSupportSuite, \
                                                                             com.didekindroid.incidencia.suite.IncidFunctionalSuite;;
    us) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite, \
                                                                             com.didekindroid.usuario.suite.UserFunctionalSuite, \
                                                                             com.didekindroid.usuario.suite.UserFunctionalSlowSuite;;
    all) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.AppFullSuite;;
    *) echo "Invalid suite";;
esac

echo " gradlew exit = $?"

source ./terminal/env_close.sh