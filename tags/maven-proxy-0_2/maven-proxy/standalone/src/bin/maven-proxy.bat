@ECHO OFF

rem Guess MAVEN_PROXY_HOME if not defined
set CURRENT_DIR=%cd%
if x%MAVEN_PROXY_HOME% == x goto gotHome
set MAVEN_PROXY_HOME=%CURRENT_DIR%
if exist "%MAVEN_PROXY_HOME%\bin\maven-proxy.bat" goto okHome

cd ..
set MAVEN_PROXY_HOME=%cd%
cd %CURRENT_DIR%
:gotHome
if exist "%MAVEN_PROXY_HOME%\bin\maven-proxy.bat" goto okHome
echo The MAVEN_PROXY_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome
rem set MAVEN_PROXY_HOME=..

set CP=
FOR %%i IN (%MAVEN_PROXY_HOME%\lib\*.jar) DO CALL ADDENV %%i 


rem ECHO %CP%
echo on
java -classpath %CP% org.apache.maven.proxy.standalone.Standalone "%1"

:end