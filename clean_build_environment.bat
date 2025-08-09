@echo off
setlocal enabledelayedexpansion

echo [INFO] Cleaning build environment...

:: Stop any running Java processes
echo [INFO] Stopping Java processes...
taskkill /F /IM java.exe >nul 2>&1

:: Clean Gradle caches
echo [INFO] Cleaning Gradle caches...
if exist "%USERPROFILE%\.gradle" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches" >nul 2>&1
    rmdir /s /q "%USERPROFILE%\.gradle\daemon" >nul 2>&1
    rmdir /s /q "%USERPROFILE%\.gradle\workers" >nul 2>&1
)

:: Clean project build directories
echo [INFO] Cleaning project build directories...
if exist "build" rmdir /s /q build
if exist ".gradle" rmdir /s /q .gradle
if exist "run" rmdir /s /q run

:: Set Java 17
echo [INFO] Setting Java 17...
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify Java version
echo [INFO] Verifying Java version...
java -version 2>&1 | findstr "version"

echo [INFO] Environment cleanup complete!
