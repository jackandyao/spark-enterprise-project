#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

${flume_home}/bin/flume-ng agent \
--classpath ${lib_home}/behavior-flume-jar-with-dependencies.jar \
--conf ${flume_home}/conf \
-f ${conf_home}/behavior.conf -n a1 \
-Dflume.monitoring.type=http \
-Dflume.monitoring.port=5653 \
>> ${logs_home}/behavior_agent.log 2>&1 &

echo $! > ${logs_home}/behavior_agent.pid
