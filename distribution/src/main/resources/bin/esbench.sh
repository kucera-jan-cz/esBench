#!/bin/sh

BENCH_CLASSPATH="lib\elastic-module-0.0.2-SNAPSHOT.jar"
if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=`which java`
fi

exec "$JAVA" -cp %BENCH_CLASSPATH% "org.esbench.cmd.EsBenchCommandLine" 