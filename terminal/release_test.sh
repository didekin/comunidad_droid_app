# It must be executed after 'cddroid' with terminal/release.sh buildType
# buildTypes: local, pre

#!/bin/bash

FUNC_DROID_DIR=terminal/functions

if ! [ -d ${FUNC_DROID_DIR} ]
    then
        echo "No environment functions dirs" 1>&2; exit 1
    else
        . ${FUNC_DROID_DIR}/env_functions.sh
fi

[ $# -ne 1 ] && { echo "args count should be 1" 1>&2; exit 1;}

export BUILD_TYPE="$1"

if ! [ ${BUILD_TYPE} = "local" ] && ! [ ${BUILD_TYPE} = "pre" ] ; then
    echo "Wrong buildType: $BUILD_TYPE" 1>&2; exit 1;
fi

assembleBuildType ${BUILD_TYPE}

echo "Uninstalling com.didekindroid ..."
adb uninstall com.didekindroid

echo "Installing apk ..."
adb  install app/releases/${BUILD_TYPE}/app-${BUILD_TYPE}.apk

echo "SALIENDO..."
exit 0