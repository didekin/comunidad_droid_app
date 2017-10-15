# It must be executed after 'cddroid' with terminal/release.sh buildType version
# buildTypes: local, pre

#!/bin/bash

FUNC_DROID_DIR=terminal/functions

if ! [ -d ${FUNC_DROID_DIR} ]
    then
        echo "No environment functions dirs" 1>&2; exit 1
    else
        . ${FUNC_DROID_DIR}/env_functions.sh
fi

[ $# -ne 2 ] && { echo "args count should be 2" 1>&2; exit 1;}

export BUILD_TYPE="$1"
export VERSION="$2"

if ! [ ${BUILD_TYPE} = "local" ] && ! [ ${BUILD_TYPE} = "pre" ] ; then
    echo "Wrong buildType: $BUILD_TYPE" 1>&2; exit 1;
fi

assembleBuildType ${BUILD_TYPE} ${VERSION}
echo "==== AssembleBuildType exit code = $?"

adb uninstall com.didekindroid
echo "==== Uninstalling com.didekindroid exit code = $?"

adb  install app/releases/${BUILD_TYPE}/app-${VERSION}-${BUILD_TYPE}.apk
echo "==== Installing new APK exit code = $?"

echo "SALIENDO..."
exit 0