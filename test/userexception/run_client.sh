#!/bin/bash
bn=${0##*/}
host=$(hostname)

if [[ ! -n $JACORB_HOME ]] ; then
   $JACORB_HOME=$(cd ../../../; pwd)
   echo "$bn: setting JACORB_HOME to $JACORB_HOME"
fi
export JACORB_HOME

PATH=${PATH}
CLASSPATH=${CLASSPATH}
export PATH=${PATH}:${JACORB_HOME}/bin
export CLASSPATH=${CLASSPATH}:`pwd`/build/classes
echo "$bn: JACORB_HOME=<${JACORB_HOME}>"
echo "$bn: CLASSPATH=<${CLASSPATH}>"
echo "$bn: PATH=<${PATH}>"

out_dir="$(pwd)/output"
if [[ ! -d $out_dir ]] ; then
    mkdir -p $out_dir
fi

log_file="$out_dir/client_$$.log"
rm -f ${log_file} 2>&1

#
echo "$bn: starting client "
$JACORB_HOME/bin/jaco org.jacorb.test.userexception.one.two.three.four.Client \
	-ntimes 1 -nthreads 1 \
	-iorfile server.ior > ${log_file} 2>&1 &
    sleep 5
    cat ${log_file}

exit 0
