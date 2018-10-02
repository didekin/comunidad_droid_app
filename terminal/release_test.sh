# It must be executed after 'cddroid' with terminal/release.sh buildType version device
# buildTypes: local, pre
# device: taken from adb devices

#!/bin/bash

FUNC_DROID_DIR=terminal/functions

if ! [ -d ${FUNC_DROID_DIR} ]
    then
        echo "No environment functions dirs" 1>&2; exit 1
    else
        . ${FUNC_DROID_DIR}/env_functions.sh
fi

[ $# -ne 3 ] && { echo "args count should be 3" 1>&2; exit 1;}

export BUILD_TYPE="$1"
export VERSION="$2"
export DEVICE="$3"

if ! [ ${BUILD_TYPE} = "local" ] && ! [ ${BUILD_TYPE} = "pre" ] ; then
    echo "Wrong buildType: $BUILD_TYPE" 1>&2; exit 1;
fi

assembleBuildType ${BUILD_TYPE}
echo "==== AssembleBuildType exit code = $?"

<module-name>/build/outputs/mapping/release/

mv app/build/outputs/apk/${BUILD_TYPE}/app-${BUILD_TYPE}.apk app/releases/${BUILD_TYPE}/app-${VERSION}-${BUILD_TYPE}.apk

adb -s ${DEVICE} uninstall com.didekindroid
echo "==== Uninstalling com.didekindroid exit code = $?"

adb -s ${DEVICE} install -r app/releases/${BUILD_TYPE}/app-${VERSION}-${BUILD_TYPE}.apk
echo "==== Installing new APK exit code = $?"

echo "SALIENDO..."
exit 0