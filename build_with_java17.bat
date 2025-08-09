@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home explicitly
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_BIN=%JAVA_HOME%\bin"
set "PATH=%JAVA_BIN%;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home=%JAVA_HOME% -Dfile.encoding=UTF-8"

echo =============================================
echo   BUILDING WITH JAVA 17
echo =============================================
echo Java Home: %JAVA_HOME%
echo Java Version:
java -version
echo =============================================
echo.

:: Clean Gradle and build directories
echo Cleaning build directories...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist run rmdir /s /q run
if exist gradle\wrapper\gradle-wrapper.properties (
    echo Removing existing Gradle wrapper...
    del /q gradle\wrapper\gradle-wrapper.jar
    del /q gradle\wrapper\gradle-wrapper.properties
)

:: Set up Gradle wrapper if it doesn't exist
if not exist gradle\wrapper\gradle-wrapper.jar (
    echo Setting up Gradle wrapper...
    if not exist gradle\wrapper mkdir gradle\wrapper
    
    echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
    echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
    echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
    echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
    echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
    
    echo Downloading Gradle wrapper...
    powershell -Command "(New-Object System.Net.WebClient).DownloadFile('https://github.com/gradle/gradle/raw/v7.5.1/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar')"
)

:: Create a simple gradlew.bat if it doesn't exist
if not exist gradlew.bat (
    echo @echo off> gradlew.bat
    echo setlocal>> gradlew.bat
    echo set JAVA_HOME=%%~dp0jdk-17.0.15.6-hotspot>> gradlew.bat
    echo if not exist "%%JAVA_HOME%%" set JAVA_HOME=!JAVA_HOME!>> gradlew.bat
    echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%>> gradlew.bat
    echo set GRADLE_OPTS=-Dorg.gradle.java.home=%%JAVA_HOME%% -Dfile.encoding=UTF-8>> gradlew.bat
    echo if exist "gradle\wrapper\gradle-wrapper.jar" (>> gradlew.bat
    echo     java -Dorg.gradle.java.home=%%JAVA_HOME%% -Dfile.encoding=UTF-8 -jar "gradle\wrapper\gradle-wrapper.jar" %%*>> gradlew.bat
    echo ) else (>> gradlew.bat
    echo     echo Error: Gradle wrapper not found.>> gradlew.bat
    echo     exit /b 1>> gradlew.bat
    echo )>> gradlew.bat
)

echo.
echo =============================================
echo   STARTING GRADLE BUILD
echo =============================================
echo.
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.0.2-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

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
