@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home explicitly with proper quoting
set "JAVA17_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_HOME=%JAVA17_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify Java version before proceeding
for /f "tokens=3" %%j in ('""%JAVA_HOME%\bin\java" -version 2^>^&1 | findstr /i "version""') do (
    set "JAVA_VERSION=%%j"
    set "JAVA_VERSION=!JAVA_VERSION:"=!"
)

echo ====== Java Environment ======
echo JAVA_HOME: %JAVA_HOME%
echo Java version: %JAVA_VERSION%
echo =============================
echo.

:: Check if Java 17 is being used
if not "%JAVA_VERSION:17=%"=="%JAVA_VERSION%" (
    echo Using Java 17: %JAVA_VERSION%
) else (
    echo ERROR: Java 17 is required but found %JAVA_VERSION%
    echo Please ensure Java 17 is installed and JAVA_HOME is set correctly.
    exit /b 1
)

:: Clean Gradle and build directories
echo Cleaning build directories...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist run rmdir /s /q run

:: Create a fresh Gradle wrapper with Java 17
echo Setting up Gradle wrapper with Java 17...
if not exist gradle\wrapper mkdir gradle\wrapper

echo Creating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.0.2-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Set Gradle user home to ensure clean environment
set "GRADLE_USER_HOME=%CD%\.gradle"

:: Run the build with Java 17
echo ====== Starting Build ======
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en"
call "%JAVA_HOME%\bin\java" -version
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
