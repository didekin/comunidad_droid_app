# It must be executed after 'cddroid' with terminal/release.sh environment
# environment('local','awspre','pro')

#!/bin/bash

FUNC_DROID_DIR=terminal/functions

if ! [ -d $FUNC_DROID_DIR ]
    then
        echo "No environment functions dirs" 1>&2; exit 1
    else
        . $FUNC_DROID_DIR/env_functions
fi

[ $# -ne 1 ] && { echo "args count should be 1" 1>&2; exit 1;}

export ENV="$1"

if ! [ $ENV = "local" ] && ! [ $ENV = "awspre" ] ; then
        echo "Wrong type of environment: $ENV" 1>&2; exit 1;
fi

assembleAndRelease

echo "SALIENDO..."
exit 0