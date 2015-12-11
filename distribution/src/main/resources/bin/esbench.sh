#!/bin/sh

SCRIPT_PATH="$0"
SCRIPT_PATH=`readlink -f "$SCRIPT_PATH"`
BENCH_HOME=`dirname "$SCRIPT_PATH"`/..
BENCH_HOME=`cd "$BENCH_HOME"; pwd`
BENCH_CLASSPATH="$BENCH_HOME/lib/*"

#Choose JAVA
if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=`which java`
fi

exec "$JAVA" -cp $BENCH_CLASSPATH "org.esbench.cmd.EsBenchCommandLine" "$@"