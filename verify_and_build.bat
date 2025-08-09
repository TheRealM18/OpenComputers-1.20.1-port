@echo off
setlocal

:: Set Java 17 home
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Show Java version
echo Java Version:
java -version

:: Show environment
echo.
echo Environment:
echo JAVA_HOME=%JAVA_HOME%
echo PATH=%PATH%

:: Clean and build
echo.
echo Starting build...
gradlew clean build --stacktrace --info

if %ERRORLEVEL% EQU 0 (
    echo Build succeeded!
) else (
    echo Build failed with error code %ERRORLEVEL%
)


