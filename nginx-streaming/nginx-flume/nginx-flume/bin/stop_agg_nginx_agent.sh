#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

pid=`cat ${pid_home}/agg.pid | head -1`

kill ${pid}
