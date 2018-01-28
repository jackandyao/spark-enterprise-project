#!/bin/sh

isDeleteCheckpoint=$1

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

checkpointPath=`cat ${conf_home}/my.properties | grep streaming.checkpoint.path | awk -F"=" '{print $2}'`
stopPath=`cat ${conf_home}/my.properties | grep streaming.stop.path | awk -F"=" '{print $2}'`

echo "checkpointPath=${checkpointPath}"
echo "stopPath=${stopPath}"

if [ "$isDeleteCheckpoint" == "delck" ]; then
    echo "to delete checkpointPath ${checkpointPath}"
    hdfs dfs -rm -r ${checkpointPath}
fi

echo "to delete stopPath ${stopPath}"
hdfs dfs -rm -r ${stopPath}

cmd="\
/home/hadoop/app/spark/bin/spark-submit \
--master yarn \
--deploy-mode cluster \
--name nginxlog \
--driver-memory 512m \
--executor-memory 512m \
--class com.djt.spark.streaming.NginxLogStreaming \
${lib_home}/nginx-spark-jar-with-dependencies.jar ${conf_home}/my.properties"

echo "cmd=$cmd"

echo "$cmd" >> ${startLogFile}

nohup $cmd >> ${logs_home}/log.log 2>&1 &
