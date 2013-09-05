#!/bin/bash
bn=${0##*/}

if [[ ! -n $JACORB_HOME ]] ; then
   $JACORB_HOME=$(cd ../../../; pwd)
   echo "$bn: setting JACORB_HOME to $JACORB_HOME"
fi

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

log="$out_dir/Server_$$.log"
rm -f $log 2>&1
server_name="org.jacorb.test.userexception.one.two.three.four.Server"
echo "$bn: starting server  ..."
${JACORB_HOME}/bin/jaco ${server_name} \
 -iorfile server.ior  > $log 2>&1 &
pid=$!
echo "$bn: $pid $log"
cat $log
if [[ ! -z $pid ]] ; then
(( cnt = 2 ))
while (( cnt > 0 )) ; do
    echo "."
    sleep 10
    if ps -p $pid ; then
        tail -5 $log
        echo "SUCCESS::$bn: ${server_name} is running"
        exit 0
    fi
    (( cnt = cnt - 1 ))
done
fi
cat $log
echo "WARNING::$bn: ${server_name} may not be running! Please check the log file"
exit 0
