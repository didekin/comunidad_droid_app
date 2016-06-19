# It must be executed after 'cddroid' with ./terminal/build_localdev.sh

#!/bin/bash

mysql.server start
nginx -c /usr/local/etc/nginx/nginx_didekindroid_dev.conf

export RDS_DB_NAME=didekin
export RDS_HOSTNAME=localhost
export RDS_USERNAME=pedro
export RDS_PASSWORD=pedro
export DIDEKINSPRING_HOME=/Users/pedro/Documents/git_projects/didekinspring

rm terminal/*log

java -jar $DIDEKINSPRING_HOME/services/build/libs/didekinspring.jar > terminal/server.log 2> terminal/server_error.log &

# Common tests
    ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.common.suite.CommonSuite
# Incidencias tests
   # ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.incidencia.suite.IncidSupportSuite,com.didekindroid.incidencia.suite.IncidFunctionalSuite
# Usuario tests
   # ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.usuario.suite.UserSupportSuite,com.didekindroid.usuario.suite.UserFunctionalSuite,com.didekindroid.usuario.suite.UserFunctionalSlowSuite
# Common + incidencias + usuario tests
  # ./gradlew app:cAT -Pandroid.testInstrumentationRunnerArguments.class=com.didekindroid.AppFullSuite

echo " gradlew exit = $?"

kill -s KILL $!
nginx -s quit  -c /usr/local/etc/nginx/nginx_didekindroid_dev.conf
mysql.server stop

exit