# It must be executed after 'cddroid' with terminal/build_dbpre.sh

#!/bin/bash

nginx -c /usr/local/etc/nginx/nginx_didekindroid_dev.conf

export RDS_DB_NAME=didekin
export RDS_HOSTNAME=frankfurt-mysql-one.c2ojt9azfyy4.eu-central-1.rds.amazonaws.com
export RDS_USERNAME=frank_1_root
export RDS_PASSWORD=xAt-WDS-7sT-YSb

gradle assembleDebugAndroidTest

export DIDEKINSPRING_HOME=/Users/pedro/Documents/git_projects/didekinspring
java -jar $DIDEKINSPRING_HOME/services/build/libs/didekinspring.jar

# Common tests
   ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite
# Incidencias tests
   ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.suite.IncidSupportSuite,com.didekindroid.incidencia.suite.IncidFunctionalSuite
# Usuario tests
   ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite,com.didekindroid.usuario.suite.UserFunctionalSuite,com.didekindroid.usuario.suite.UserFunctionalSlowSuite

nginx -s quit  -c /usr/local/etc/nginx/nginx_didekindroid_dev.conf