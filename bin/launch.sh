#!/bin/bash

# Since this script is launched by a non-interactive bash shell from the JVM,
# the environment variable LD_LIBRARY_PATH and others must be
# initialized on a lynxos-ppc card in order for Apogee to find its
# libraries.  Quynh N.
#
echo "$0: OSTYPE=<$OSTYPE> HOSTTYPE=<$HOSTTYPE>"
if [[ $OSTYPE == "lynxos" && $HOSTTYPE == "ppc" ]] ; then
    if [[ -f /etc/profile ]] ; then
        . /etc/profile
    elif [[ -f /etc/bash_profile ]] ; then
        . /etc/bash_profile
    fi

    # brute force it if need to
    if [[ -z $LD_LIBRARY_PATH ]] ; then
        echo "$0: found that LD_LIBRARY_PATH is not set"
        export LD_LIBRARY_PATH=/opt/app/apogee/cjre-rt/bin
        echo "$0: set LD_LIBRARY_PATH to /opt/app/apogee/cjre-rt/bin"
    fi
	echo "$0: LD_LIBRARY_PATH=<${LD_LIBRARY_PATH}>"
fi

"$@" &
_PID=$!
echo "$@" > $JACUNIT_PID_DIR/$_PID

trap "kill $_PID; wait $_PID" TERM QUIT INT
wait $_PID

