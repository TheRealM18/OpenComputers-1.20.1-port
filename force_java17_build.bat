@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home with proper quoting
set "JAVA17_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_HOME=%JAVA17_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Stop any running Java processes that might interfere
echo Stopping any running Java processes...
taskkill /F /IM java.exe >nul 2>&1

:: Clean up any existing Gradle daemons
echo Stopping any running Gradle daemons...
for /f "tokens=1" %%i in ('jps -l ^| findstr GradleDaemon') do taskkill /F /PID %%i >nul 2>&1

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
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en"

:: Verify Java version before proceeding
echo ====== Verifying Java Version ======
"%JAVA_HOME%\bin\java" -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to verify Java version
    exit /b 1
)

echo ====== Java Environment ======
echo JAVA_HOME: %JAVA_HOME%
echo PATH: %PATH%
"%JAVA_HOME%\bin\java" -version
echo =============================

:: Run the build with Java 17
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
