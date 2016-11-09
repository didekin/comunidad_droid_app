# It must be executed after 'cddroid' with terminal/release.sh environment
# environment('local','awspre')

#!/bin/bash

FUNC_DROID_DIR=terminal/functions

if ! [ -d ${FUNC_DROID_DIR} ]
    then
        echo "No environment functions dirs" 1>&2; exit 1
    else
        . ${FUNC_DROID_DIR}/env_functions
fi

[ $# -ne 1 ] && { echo "args count should be 1" 1>&2; exit 1;}

export ENV="$1"

if ! [ ${ENV} = "local" ] && ! [ ${ENV} = "awspre" ] ; then
    echo "Wrong type of environment: $ENV" 1>&2; exit 1;
fi

export APP_PROD_PARAM_HOME=app/src/main/res/values
export BKS_PROD_HOME=app/src/main/res/raw

rm ${APP_PROD_PARAM_HOME}/app_parameters.xml

if [ ${ENV} = "local" ] ; then
   cp terminal/app_local/local_app_parameters.xml ${APP_PROD_PARAM_HOME}/
fi

if [ ${ENV} = "awspre" ] ; then
   cp terminal/app_pre/pre_app_parameters.xml ${APP_PROD_PARAM_HOME}/
   cp terminal/app_pre/didekindroid_pre_bks ${BKS_PROD_HOME}
fi

assembleAndRelease

echo "Installing app ..."
adb -d install app/releases/${ENV}/app-release.apk

if [ ${ENV} = "local" ] ; then
   echo "borrando local_app_parameters ..."
   rm ${APP_PROD_PARAM_HOME}/local_app_parameters.xml
fi

if [ ${ENV} = "awspre" ] ; then
   echo "borrando pre_app_parameters y didekindroid_pre_bks"
   rm ${APP_PROD_PARAM_HOME}/pre_app_parameters.xml
   rm ${BKS_PROD_HOME}/didekindroid_pre_bks
fi

cp terminal/app_pro/app_parameters.xml ${APP_PROD_PARAM_HOME}/

echo "SALIENDO..."
exit 0