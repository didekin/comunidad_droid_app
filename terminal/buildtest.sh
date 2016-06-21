# It must be executed after 'cddroid' with ./terminal/buildtest.sh  environment emulator suite
# environment('local','dbpre','awspre') emulator ('geny','google') suite('cm','in','us','all')

#!/bin/bash
[ $# -ne 3 ] && { echo "args count should be 3" 1>&2; exit 1;}

source ./terminal/env_init.sh

# ./gradlew clean

SUITE="$3"
echo "Suite:" $SUITE

case "$SUITE" in
    cm) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite ;;
    in) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.suite.IncidSupportSuite,com.didekindroid.incidencia.suite.IncidFunctionalSuite;;
    us) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite,com.didekindroid.usuario.suite.UserFunctionalSuite,com.didekindroid.usuario.suite.UserFunctionalSlowSuite;;
    all) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.AppFullSuite;;
    *) echo "Invalid suite";;
esac

echo " gradlew exit = $?"

source ./terminal/env_close.sh