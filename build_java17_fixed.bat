@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home with proper quoting
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify Java version
"%JAVA_HOME%\bin\java" -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to run Java from %JAVA_HOME%\bin\java
    exit /b 1
)

:: Clean up any existing Gradle daemons
tasklist /FI "IMAGENAME eq java.exe" 2>nul | find /i "GradleDaemon" >nul
if %ERRORLEVEL% EQU 0 (
    echo Stopping existing Gradle daemons...
    taskkill /F /FI "WINDOWTITLE eq Gradle Daemon" >nul 2>&1
)

:: Clean build directories
echo Cleaning build directories...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist run rmdir /s /q run

:: Create a fresh Gradle wrapper
echo Setting up Gradle wrapper...
if not exist gradle\wrapper mkdir gradle\wrapper

echo Creating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.0.2-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Set Gradle options to ensure Java 17 is used
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

:: Run the build
echo ====== Java Environment ======
echo JAVA_HOME: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo =============================
echo.
echo ====== Starting Build ======
call gradlew build --stacktrace --info --no-daemon -Dorg.gradle.java.home="%JAVA_HOME%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====== Build Succeeded ======
    echo Build artifacts should be in the build/libs directory.
    exit /b 0
) else (
    echo.
    echo ====== Build Failed ======
    echo Check the output above for error details.
    exit /b %ERRORLEVEL%
)
