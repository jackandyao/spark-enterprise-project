#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

/home/hadoop/app/flume/bin/flume-ng agent \
--conf /home/hadoop/app/flume/conf \
-f ${conf_home}/s1_agent.conf -n a1 \
-Xms256m -Xmx512m \
-Dflume.monitoring.type=http \
-Dflume.monitoring.port=5654 \
>> ${logs_home}/s1.log 2>&1 &

echo $! > ${pid_home}/s1.pid
