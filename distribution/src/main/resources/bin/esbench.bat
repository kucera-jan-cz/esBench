@echo off

set SCRIPT_PATH=%~dp0
for %%I in ("%SCRIPT_PATH%..") do set BENCH_HOME=%%~dpfI

set BENCH_CLASSPATH="%BENCH_HOME%\lib\elastic-module.jar"

if DEFINED JAVA_HOME GOTO home
set JAVA_EXE="java"
goto exec

:home
set JAVA_EXE="%JAVA_HOME%\bin\java"

:exec
%JAVA_EXE% -cp %BENCH_CLASSPATH%  "org.esbench.cmd.EsBenchCommandLine" %* 
pause
