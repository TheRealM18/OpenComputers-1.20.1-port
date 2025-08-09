@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home with proper escaping
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE="%JAVA_HOME%\bin\java.exe""
set "PATH="%JAVA_HOME%\bin";%PATH%""
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

echo ====== VERIFYING JAVA ======
%JAVA_EXE% -version
if %ERRORLEVEL% NEQ 0 (
    echo Failed to run Java. Please check JAVA_HOME.
    exit /b 1
)

echo ====== SETTING UP GRADLE ======
if not exist gradle\wrapper mkdir gradle\wrapper

echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Create a simple gradlew.bat
(
echo @echo off
echo setlocal
echo set JAVA_HOME=^"C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot^"
echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%
echo set GRADLE_OPTS=-Dorg.gradle.java.home=%%JAVA_HOME%% -Dfile.encoding=UTF-8
echo "%%JAVA_HOME%%\bin\java" %%GRADLE_OPTS%% -jar "%%~dp0gradle\wrapper\gradle-wrapper.jar" %%*
) > gradlew.bat

echo ====== DOWNLOADING GRADLE WRAPPER ======
powershell -Command "& { (New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.1.1/gradle/wrapper/gradle-wrapper.jar', 'gradle\wrapper\gradle-wrapper.jar'); exit $LastExitCode }"
if %ERRORLEVEL% NEQ 0 (
    echo Failed to download Gradle wrapper.
    exit /b 1
)

echo ====== VERIFYING GRADLE ======
call gradlew.bat --version
if %ERRORLEVEL% NEQ 0 (
    echo Failed to run Gradle wrapper.
    exit /b 1
)

echo ====== SETUP COMPLETE ======
echo.
echo 1. Run: call gradlew.bat build
echo 2. If it fails, try: call gradlew.bat clean build

endlocal
