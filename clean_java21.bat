@echo off
setlocal

:: Set Java 17 home
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ====== Cleaning Java 21 Artifacts ======

:: Clean Gradle caches
echo Cleaning Gradle caches...
if exist %USERPROFILE%\.gradle\caches rmdir /s /q %USERPROFILE%\.gradle\caches
if exist .gradle rmdir /s /q .gradle

:: Clean build directories
echo Cleaning build directories...
if exist build rmdir /s /q build
if exist run rmdir /s /q run

:: Stop any running Java processes
echo Stopping Java processes...
taskkill /F /IM java.exe >nul 2>&1

:: Verify Java version
echo.
echo ====== Verifying Java Version ======
java -version

:: Run Gradle with Java 17
echo.
echo ====== Running Build with Java 17 ======
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"
call gradlew clean build --stacktrace --info --no-daemon -Dorg.gradle.java.home="%JAVA_HOME%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====== Build Succeeded ======
    echo Build artifacts should be in the build/libs directory.
) else (
    echo.
    echo ====== Build Failed ======
    echo Check the output above for error details.
)


