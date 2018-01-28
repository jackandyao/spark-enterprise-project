#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

/home/hadoop/app/flume/bin/flume-ng agent \
--classpath ${lib_home}/nginx-flume.jar \
--conf /home/hadoop/app/flume/conf \
-f ${conf_home}/agg_agent.conf -n a1 \
-Xms256m -Xmx512m \
-Dflume.monitoring.type=http \
-Dflume.monitoring.port=5653 \
>> ${logs_home}/agg.log 2>&1 &

echo $! > ${pid_home}/agg.pid
