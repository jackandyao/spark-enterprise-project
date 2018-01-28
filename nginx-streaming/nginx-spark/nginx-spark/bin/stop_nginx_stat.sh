#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

stopPath=`cat ${conf_home}/my.properties | grep streaming.stop.path | awk -F"=" '{print $2}'`

echo "to create stopPath ${stopPath}"

hdfs dfs -mkdir -p ${stopPath}
