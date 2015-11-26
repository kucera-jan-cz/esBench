@echo off
set BENCH_CLASSPATH="lib\elastic-module-0.0.2-SNAPSHOT.jar"
if DEFINED JAVA_HOME GOTO home
set JAVA_EXE="java"
goto exec

:home
set JAVA_EXE="%JAVA_HOME%\bin\java"

:exec
%JAVA_EXE% -cp %BENCH_CLASSPATH%  "org.esbench.cmd.EsBenchCommandLine" 
pause
