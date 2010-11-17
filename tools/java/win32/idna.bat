@echo off

rem ---- check if JAVA_HOME is set ----
if "%JAVA_HOME%" == "" goto nojavahome

set LIB_DIR=..\..\..\lib

set MYCLASSPATH=%JAVA_HOME%\lib\tools.jar
set MYCLASSPATH=%MYCLASSPATH%;%LIB_DIR%\vrsnIdna-4.0.jar

rem ---- invoke the java class ----
%JAVA_HOME%\bin\java -classpath %MYCLASSPATH% com.vgrs.xcode.cmdline.idna.IdnaCmdLine %*
goto done

:nojavahome
echo ERROR: JAVA_HOME environment variable not set

:done
