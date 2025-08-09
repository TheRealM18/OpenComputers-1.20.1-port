@echo off
setlocal

:: Set Java 17 Home
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Updating Gradle wrapper to version 8.1.1...

echo Stopping any running Gradle daemons...
call gradlew --stop

echo Setting Gradle wrapper version to 8.1.1...
call gradlew wrapper --gradle-version=8.1.1 --distribution-type=bin

echo Verifying Gradle wrapper update...
call gradlew --version

if %ERRORLEVEL% NEQ 0 (
    echo Failed to update Gradle wrapper
    exit /b %ERRORLEVEL%
)

echo Gradle wrapper updated successfully to version 8.1.1
endlocal
