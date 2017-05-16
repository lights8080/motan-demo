#!/bin/bash
DIR=`pwd`
cd `dirname $0`
cd ../conf
CONF_DIR=`pwd`
cd ../lib
LIB_DIR=`pwd`

CONF_CORE="$CONF_DIR/core.properties"
if [ "x$3" != "x" ]
then
    CONF_CORE="$3"
fi
echo "Using coreConfig:$CONF_CORE" >&2
if [ ! -f "$CONF_CORE" ]; then
    echo "start failed, The File [$CONF_CORE] is not Exist!"
    exit 0
fi

JAVA_OPTS=$(grep "zhfz.server.java_opts" "$CONF_CORE" | sed -e 's/.*=//')
JAVA_OPTS=$(echo $JAVA_OPTS | sed 's/\r//')
SERVER_NAME=$(grep "zhfz.server.mainclass" "$CONF_CORE" | sed -e 's/.*=//')
SERVER_NAME=$(echo $SERVER_NAME | sed 's/\r//')
if [ "x$SERVER_NAME" == "x" ]; then
    echo "start failed, The Param [zhfz.server.mainclass] is Empty!"
    exit 0
fi
echo "Using serverName:$SERVER_NAME" >&2

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

PID_FILE="$CONF_DIR/../$SERVER_GROUP-$SERVER_PORT.pid"

if [ -f $PID_FILE ]; then
  if kill -0 `cat $PID_FILE` > /dev/null 2>&1; then
    echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] already running as process `cat $PID_FILE`.
    exit 0
  fi
fi

PIDS=`ps -ef | grep java | grep "$LIB_DIR" | grep "${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}" | awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] already running as process $PIDS"
    exit 1
fi

PIDS=`netstat -lnp | grep ${SERVER_PORT} | awk '{print $7}' | awk -F '/' '{print $1}'`
if [ -n "$PIDS" ]; then
    echo "server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] port is used as process $PIDS"
    exit 1
fi

cd ..
#LIB_JARS=`ls ${LIB_DIR} | grep .jar | awk '{print "'${LIB_DIR}'/"$0}' | tr "\n" ":"`
LIB_JARS=".:$LIB_DIR/*"
nohup java -server ${JAVA_OPTS} -Dlog4jpath="${SERVER_GROUP}/${SERVER_PORT}" -cp "${LIB_JARS}" ${SERVER_NAME} "${SERVER_PORT}" "${SERVER_GROUP}" "${CONF_CORE}" > /dev/null 2>&1 &

if [ $? -eq 0 ]
then
  if /bin/echo -n $! > "$PID_FILE"
  then
    sleep 1
    echo server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] started
  else
    echo FAILED TO WRITE PID.
    exit 1
  fi
else
  echo SERVER DID NOT START.
  exit 1
fi

#echo "server [${SERVER_NAME} ${SERVER_PORT} ${SERVER_GROUP}] start success!"
cd ${DIR}
