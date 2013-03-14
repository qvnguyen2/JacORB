#!/bin/bash
#@ The purpose of this script is for launching a demo server-client pair
#@ on a tiny system such as an embedded card instead of ANT since ANT will
#@ eats up all available memory and die.  This scripts is launcehd from an
##@ demo ant build file.  Quynh N.
#
bn=${0##*/}
server_name=$1
client_name=$2
killfile=/tmp/killfile$$
host=$(hostname)
osname=$(uname -s | awk '{print tolower($0)}')
curdir=$(pwd)
pid=

if [[ -z $server_name || -z $client_name ]] ; then
    echo "Usage: $bn <server classname> <client classname>"
    exit 1
fi

trap "trap_exit" 1 2 3 15

trap_exit()
{
	if [[ ! -z $pid ]] ; then
		kill -s 15 $pid
	fi
	rm -f $killfile
	exit 1
}

PATH=${PATH}
CLASSPATH=${CLASSPATH}
export JACORB_HOME
export PATH=${PATH}:${JACORB_HOME}/bin
export CLASSPATH=${CLASSPATH}:${JACORB_HOME}/classes:${curdir}/build/classes

out_dir=${curdir}/output
rm -f server.ior killfile > /dev/null 2>&1
if [[ ! -d $out_dir ]] ; then
    if ! mkdir -p $out_dir ; then
        echo "ERROR::$bn: can't create directory $out_dir"
        exit 1
    fi
fi

log="${out_dir}/Server.log"

# This does not work on a tiny system
pid=$(ps -af | grep -v grep | grep "^.* ${server_name}" | awk '{print $1}')
if [[ ! -z $pid ]] ; then
    kill -s 15 $pid >/dev/null 2>&1
    rm -f $killfile >/dev/null 2>&1
fi

echo "$bn: starting $server_name ..."
${JACORB_HOME}/bin/jaco ${server_name} server.ior $killfile | tee ${out_dir}/Server.log 2>&1 &
pid=$!
sleep 10
if [[ -z $pid ]] ; then
    echo "ERROR $bn: failed to start $server_name"
    exit 1
fi
echo "$bn: starting $client_name ..."
${JACORB_HOME}/bin/jaco ${client_name} server.ior
touch $killfile
sleep 10
kill -s 15 $pid >/dev/null 2>&1
rm -f $killfile
exit 0
