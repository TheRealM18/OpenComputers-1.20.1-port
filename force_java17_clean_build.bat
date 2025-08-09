@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Display Java version
echo =============================================
echo   Starting Clean Build with Java 17
echo   Java Home: %JAVA_HOME%
echo   Java Version:
java -version
echo =============================================
echo.

:: Stop any running Java processes
echo Stopping any running Java processes...
taskkill /F /IM java.exe /T >nul 2>&1

:: Clean Gradle caches
echo Cleaning Gradle caches...
if exist "%USERPROFILE%\.gradle" (
    echo Removing Gradle caches...
    rmdir /s /q "%USERPROFILE%\.gradle\caches"
    rmdir /s /q "%USERPROFILE%\.gradle\daemon"
    rmdir /s /q "%USERPROFILE%\.gradle\workers"
)

:: Clean project build directories
echo Cleaning project build directories...
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle
if exist run rmdir /s /q run

:: Run Gradle with Java 17
echo Starting Gradle build with Java 17...
set GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8

:: Run the build
call gradlew clean build --no-daemon --stacktrace --info

:: Check if build was successful
if %ERRORLEVEL% EQU 0 (
    echo.
    echo =============================================
    echo   BUILD SUCCESSFUL
    echo =============================================
    exit /b 0
) else (
    echo.
    echo =============================================
    echo   BUILD FAILED
    echo =============================================
    exit /b 1
)
