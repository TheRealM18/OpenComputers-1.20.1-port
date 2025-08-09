@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home explicitly
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

:: Clean up previous build
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle

:: Run the build with Java 17
echo =============================================
echo   BUILDING WITH JAVA 17
echo =============================================
java -version
echo =============================================

call gradlew build --no-daemon --stacktrace --info

if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo Build completed successfully!

endlocal
