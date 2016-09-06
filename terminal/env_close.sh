# It must be executed after 'cddroid' with ./terminal/env_close.sh  environment emulator

#!/bin/bash

ENV="$1"
EMULATOR="$2"

APP_PARAM_HOME=app/src/debug/res/values

if [ $ENV = "awspre" ] ; then
    rm  $APP_PARAM_HOME/aws_app_parameters.xml
fi

if [ $ENV = "dbpre" ] || [ $ENV = "local" ] ; then

    kill $(ps -l | grep '[d]idekinspring.jar' | awk '{print $2}')
    nginx -s quit  -c /usr/local/etc/nginx/nginx_didekinspring_dev.conf
    rm  $APP_PARAM_HOME/local_app_parameters.xml

    if [ $EMULATOR = "geny" ] ; then
        rm  $APP_PARAM_HOME/geny_url.xml
    else
        rm  $APP_PARAM_HOME/google_url.xml
    fi
fi

if [ $ENV = "local" ] ; then
    mysql.server stop
fi

echo "SALIENDO"
exit 0