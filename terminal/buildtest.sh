# It must be executed after 'cddroid' with ./terminal/buildtest.sh  environment  suite  version
# environment('local','awspre') suite('co','in','us', 'usco', 'all')

#!/bin/bash
[ $# -ne 3 ] && { echo "args count should be 3" 1>&2; exit 1;}

# env_init initialized ENV, BRANCH, GITREMOTE.
source ./terminal/env_init.sh  $1
SUITE="$2"
VERSION="$GITREMOTE-$3"
echo "Suite:" ${SUITE}

./gradlew clean

if [ ${ENV} == "local" ] ; then
    echo "Git: add, commit, push local ..."
    git add .
    git commit -m "version $VERSION"
fi

if [ ${ENV} = "awspre" ] ; then
    echo "Git: merge with local, push aws_pre ..."
    git merge localdev  -m "version $VERSION"
fi

echo "Uninstalling com.didekindroid ..."
adb uninstall com.didekindroid

case "$SUITE" in
    cm) ./gradlew --info app:cAT \
        -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite
        ;;
    in) ./gradlew --info app:cAT \
        -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.IncidSupportSuite,com.didekindroid.incidencia.IncidFunctionalSuite
        ;;
    us) ./gradlew --info app:cAT \
        -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite,com.didekindroid.usuario.suite.UserFunctionalSuite
        ;;
    all) ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.AppFullSuite
        ;;
    *) echo "Invalid suite"
        ;;
esac
echo "gradlew exit = $?"

# Branch is initialized in env_init.sh.
echo "pushing code to $BRANCH"
git push ${GITREMOTE} ${BRANCH}

source ./terminal/env_close.sh  ${ENV}