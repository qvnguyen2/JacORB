#!/bin/bash
#!@ The purpose of this script is for launching a selected regression test
#!@ on a tiny system such as an embedded card instead of ANT since ANT may
#!@ consume all available memory and die.  Quynh N.
#
#!@ Usage: ./launch_test.sh <test classname, ex: AlternateIIOPAddress2Test>
#
bn=${0##*/}
umask 002
host=$(hostname)
OSNAME=$(uname -s)  # What OS is this?
TESTHOME="$JACORB_HOME/test/regression"
export BASH_ENV="/etc/profile"

echo "$bn: got host: $host"
echo "$bn: got OSNAME: $OSNAME"
if [[ $OSNAME != "LynxOS" ]] ; then
   echo "$bn:  Please launch regression tests using ant instead"
   exit 1
fi

testname=$1
if [[ -z $testname ]] ; then
    echo "Usage: $bn <test classname, ex: AlternateIIOPAddress2Test>"
    exit 1
fi

test_classname=$(find classes/org/jacorb -type f -name "${testname}.class")
echo "$bn: got test_classname of <$test_classname>"
if [[ -z $test_classname || ! -r $test_classname ]] ; then
    echo "ERROR::$bn: class file for $testname does not exist or is not accessible."
    exit 1
fi
test_classname=$(echo $test_classname | sed -e 's/^classes\///' | sed -e 's/\//\./g' | sed -e 's/\.class//g')

TESTCLASSNAME=$test_classname
echo "$bn: got TESTCLASSNAME: $TESTCLASSNAME"

export ANT_HOME="/opt/app/apache-ant/apache-ant.lnk"
export JAVA_HOME="/opt/app/apogee/cjre-rt"
ANTSHAREHOME=$ANT_HOME
JAVASHAREHOME=$JAVA_HOME

ANTSHAREHOME_LIB="$ANTSHAREHOME/lib"
JAVASHAREHOME_LIB="$JAVASHAREHOME/lib"
echo "$bn: got ANTSHAREHOME_LIB: $ANTSHAREHOME_LIB"
echo "$bn: got JAVASHAREHOME_LIB: $JAVASHAREHOME_LIB"

OUTDIR="${TESTHOME}/output"
if [[ ! -d $OUTDIR ]] ; then
    if ! mkdir -p $OUTDIR ; then
        echo "ERROR::failed to create directory $OUTDIR"
    fi
fi
if [[ ! -d $OUTDIR/pid ]] ; then
    if ! mkdir -p $OUTDIR/pid; then
        echo "ERROR::failed to create directory $OUTDIR/pid"
    fi
fi
LOGFILETXT="${OUTDIR}/TEST-${TESTCLASSNAME}.txt"
LOGFILEXML="${OUTDIR}/TEST-${TESTCLASSNAME}.xml"
if ! echo > $LOGFILETXT ; then
    echo "ERROR::failed to create $LOGFILETXT"
fi
if ! echo > $LOGFILEXML ; then
    echo "ERROR::failed to create $LOGFILEXML"
fi

debug_port=60574
DEBUG_OPTS="-Xmx1024M -Xdebug -Xrunjdwp:transport=dt_socket,address=localhost:${debug_port}"
if [[ $OSNAME == "LynxOS" ]] ; then
    DEBUG_OPTS=
fi
echo "$bn: got DEBUG_OPTS: $DEBUG_OPTS"

#====================
# start the test
#==================+=
log=zzzz-jacorb-${testname}-$$.log
java \
 -Xmx512m -Xms512m \
-Xbootclasspath/p:$JACORB_HOME/classes:$JACORB_HOME/lib/antlr-2.7.2.jar:$JACORB_HOME/lib/idl.jar:$JACORB_HOME/lib/jacorb-services.jar:$JACORB_HOME/lib/jacorb-sources.jar:$JACORB_HOME/lib/jacorb.jar:$JACORB_HOME/lib/picocontainer-1.2.jar:$JACORB_HOME/lib/slf4j-api-1.6.4.jar:$JACORB_HOME/lib/slf4j-jdk14-1.6.4.jar:$JACORB_HOME/lib/wrapper-3.1.0.jar \
${DEBUG_OPTS} \
-Djacorb.test.pkg=org/jacorb/test/** \
-Djacorb.test.piddir=${OUTDIR}/pid \
-Djacorb.test.home=$TESTHOME \
-Djacorb.test.client.version=cvs \
-Djacorb.test.maxmemory=1024M \
-Djacorb.test.pattern=org/jacorb/test/**/*Test.class \
-Djacorb.test.client.jvminfo="X" \
-Djacorb.test.timeout.medium=10000 \
-Djacorb.home=$JACORB_HOME \
-Djacorb.test.testbanner="X" \
-Djacorb.test.outdir=$OUTDIR \
-Djacorb.test.timeout.global=480000 \
-Djacorb.test.server.version=cvs \
-Djacorb.test.launcherconfigfile=$TESTHOME/resources/test.properties \
-Djacorb.test.timeout.server=120000 \
-Djacorb.test.verbose=true \
-classpath $TESTHOME/classes:$TESTHOME/src:$TESTHOME/resources:$TESTHOME/lib/ant-testutil.jar:$TESTHOME/lib/easymock-1.1.jar:$TESTHOME/lib/jakarta-regexp-1.4.jar:$TESTHOME/lib/junit.jar:$JACORB_HOME/classes:$JACORB_HOME/lib/antlr-2.7.2.jar:$JACORB_HOME/lib/jacorb-services.jar:$JACORB_HOME/lib/jacorb-sources.jar:$JACORB_HOME/lib/picocontainer-1.2.jar:$JACORB_HOME/lib/slf4j-api-1.6.4.jar:$JACORB_HOME/lib/slf4j-jdk14-1.6.4.jar:$JACORB_HOME/lib/wrapper-3.1.0.jar:$ANTSHAREHOME_LIB/ant-launcher.jar:$ANTSHAREHOME_LIB/ant-antlr.jar:$ANTSHAREHOME_LIB/ant-apache-bcel.jar:$ANTSHAREHOME_LIB/ant-apache-bsf.jar:$ANTSHAREHOME_LIB/ant-apache-log4j.jar:$ANTSHAREHOME_LIB/ant-apache-oro.jar:$ANTSHAREHOME_LIB/ant-apache-regexp.jar:$ANTSHAREHOME_LIB/ant-apache-resolver.jar:$ANTSHAREHOME_LIB/ant-apache-xalan2.jar:$ANTSHAREHOME_LIB/ant-commons-logging.jar:$ANTSHAREHOME_LIB/ant-commons-net.jar:$ANTSHAREHOME_LIB/ant-jai.jar:$ANTSHAREHOME_LIB/ant-javamail.jar:$ANTSHAREHOME_LIB/ant-jdepend.jar:$ANTSHAREHOME_LIB/ant-jmf.jar:$ANTSHAREHOME_LIB/ant-jsch.jar:$ANTSHAREHOME_LIB/ant-junit.jar:$ANTSHAREHOME_LIB/ant-junit4.jar:$ANTSHAREHOME_LIB/ant-netrexx.jar:$ANTSHAREHOME_LIB/ant-swing.jar:$ANTSHAREHOME_LIB/ant-testutil.jar:$ANTSHAREHOME_LIB/ant.jar \
org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner ${TESTCLASSNAME} \
    filtertrace=true \
    haltOnError=false \
    haltOnFailure=false \
    showoutput=true \
    outputtoformatters=true \
    logfailedtests=true \
    logtestlistenerevents=false \
formatter=org.apache.tools.ant.taskdefs.optional.junit.BriefJUnitResultFormatter \
formatter=org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter,$LOGFILETXT \
formatter=org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter,$LOGFILEXML | tee $log
echo "$bn: please see log file $log"
exit 0
