#!/bin/bash
DIR=`pwd`
cd `dirname $0`
cd ../conf
CONF_DIR=`pwd`

CONF_CORE="$CONF_DIR/core.properties"

SERVER_NAME=$(grep "zhfz.server.mainclass" "$CONF_CORE" | sed -e 's/.*=//')
SERVER_NAME=$(echo $SERVER_NAME | sed 's/\r//')

SERVER_GROUP=$(grep "zhfz.server.group" "$CONF_CORE" | sed -e 's/.*=//')
SERVER_GROUP=$(echo $SERVER_GROUP | sed 's/\r//')

if [ "x$2" != "x" ]
then
    SERVER_GROUP="$2"
fi
echo "Using group:$SERVER_GROUP" >&2

SERVER_PORT=$(grep "zhfz.server.port" "$CONF_CORE" | sed -e 's/.*=//')
SERVER_PORT=$(echo $SERVER_PORT | sed 's/\r//')
if [ "x$1" != "x" ]
then
    SERVER_PORT="$1"
fi
echo "Using port:$SERVER_PORT" >&2

if [ "x$SERVER_GROUP" = "x" ] || [ "x$SERVER_PORT" = "x" ]
then
    echo "Usage $0 SERVER_PORT [SERVER_GROUP]"
    exit 0
fi

PID_FILE="$CONF_DIR/../$SERVER_GROUP-$SERVER_PORT.pid"
if [ -f $PID_FILE ]
then
	PID=`cat $PID_FILE`
	echo -e "process info:\n<<< `ps -ef | grep java | awk -v _pid="${PID}" '{if($2==_pid) {print $0}}'` >>>\nserver [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stop [y/n]:"
	read zhfz_command
	if [ "y" = "$zhfz_command" ] || [ "Y" = "$zhfz_command" ]
	then
		kill ${PID} > /dev/null 2>&1
		rm "$PID_FILE"
		echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stopped as process ${PID}
		exit 0
	else
		echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] running as process ${PID}
		exit 0
	fi
else
	PID=`ps -ef | grep java | grep "${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}" | awk '{print $2}'`
	if [ -n "${PID}" ]
	then
		#echo -e "process info:\n<<< `ps -ef | grep java | grep "${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}"` >>>\nserver [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stop [y/n]:"
		echo -e "process info:\n<<< `ps -ef | grep java | awk -v _pid="${PID}" '{if($2==_pid) {print $0}}'` >>>\nserver [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stop [y/n]:"
		read zhfz_command
		if [ "y" = "$zhfz_command" ] || [ "Y" = "$zhfz_command" ]
		then
			kill ${PID} > /dev/null 2>&1
			echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stopped as process ${PID}
			exit 0
		else
			echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] running as process ${PID}
			exit 0
		fi
	else
		echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] not start
		exit 0
	fi
fi

#if [ "x$SERVER_GROUP" = "x" ]
#then
#   PID=`netstat -lnp | grep ${SERVER_PORT} | awk '{print $7}' | awk -F '/' '{print $1}'`
#	if [ -n "${PID}" ]; then
#		echo -e "process info:\n<<< `ps -ef | grep java | awk -v _pid="${PID}" '{if($2==_pid) {print $0}}'` >>>\nserver [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stop [y/n]:"
#		read zhfz_command
#		if [ "y" = "$zhfz_command" ] || [ "Y" = "$zhfz_command" ]
#		then
#			kill ${PID} > /dev/null 2>&1
#			echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] stopped as process ${PID}
#			exit 0
#		else
#			echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] running as process ${PID}
#			exit 0
#		fi
#	fi
#fi

cd ${DIR}

