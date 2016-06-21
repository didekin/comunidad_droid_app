# It must be executed after 'cddroid' with ./terminal/env_init.sh environment emulator
# environment('local','dbpre','awspre')  emulator ('geny','google')

#!/bin/bash

ENV="$1"
EMULATOR="$2"

LOCAL="local"
DBPRE="dbpre"
AWSPRE="awspre"

GENY="geny"

APP_PARAM_HOME=app/src/debug/res/values

echo $ENV  $EMULATOR

if [ $ENV == $DBPRE ] ; then
    echo "DB_PRE TESTS"
    export RDS_DB_NAME=didekin
    export RDS_HOSTNAME=frankfurt-mysql-one.c2ojt9azfyy4.eu-central-1.rds.amazonaws.com
    export RDS_USERNAME=frank_1_root
    export RDS_PASSWORD=xAt-WDS-7sT-YSb

elif [ $ENV = $AWSPRE ] ; then
    echo "AWS_PRE TESTS"
    cp terminal/resources/aws_app_parameters.xml $APP_PARAM_HOME

elif [ $ENV = "$LOCAL" ] ; then
    echo "LOCAL_TESTS"
    export RDS_DB_NAME=didekin
    export RDS_HOSTNAME=localhost
    export RDS_USERNAME=pedro
    export RDS_PASSWORD=pedro
    mysql.server start || { echo "mysql.server not started" 1>&2; exit 2;}

else
    echo "WRONG CATEGORY TEST" 1>&2
fi

if [ $ENV == $DBPRE ] || [ $ENV == "$LOCAL" ] ; then
    cp terminal/resources/local_app_parameters.xml $APP_PARAM_HOME
    nginx -c /usr/local/etc/nginx/nginx_didekindroid_dev.conf
    rm terminal/*log
    export DIDEKINSPRING_HOME=/Users/pedro/Documents/git_projects/didekinspring
    java -jar $DIDEKINSPRING_HOME/services/build/libs/didekinspring.jar > terminal/server.log 2> terminal/server.log &
fi

if [ $EMULATOR = $GENY ] ; then
    cp terminal/resources/geny_url.xml $APP_PARAM_HOME
else
    cp terminal/resources/google_url.xml $APP_PARAM_HOME
fi