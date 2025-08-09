@echo off
setlocal enabledelayedexpansion

echo ====== CLEANING JAVA AND GRADLE CACHES ======

:: Stop any running Java processes
echo Stopping any running Java processes...
taskkill /F /IM java.exe /T >nul 2>&1

:: Set Java 17 home (using short path to avoid spaces)
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

:: Clean Gradle caches
echo Cleaning Gradle caches...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist .idea rmdir /s /q .idea

:: Clean Maven local repository (optional)
if exist "%USERPROFILE%\.m2\repository\net\minecraftforge" (
    echo Cleaning Forge artifacts from Maven local repository...
    rmdir /s /q "%USERPROFILE%\.m2\repository\net\minecraftforge"
)

:: Clean temporary files
echo Cleaning temporary files...
del /q /s *.log 2>nul
del /q /s *.log.* 2>nul

:: Verify Java
echo.
echo ====== VERIFYING JAVA ======
"%JAVA_EXE%" -version
if !ERRORLEVEL! NEQ 0 (
    echo Failed to run Java. Please check JAVA_HOME.
    exit /b 1
)

echo.
echo JAVA_HOME is set to: %JAVA_HOME%
echo.

echo ====== CLEANUP COMPLETE ======
echo Please run the build script again.

