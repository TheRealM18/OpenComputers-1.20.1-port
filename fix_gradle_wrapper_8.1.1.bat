@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home (using short path to avoid spaces)
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Set Gradle version
set "GRADLE_VERSION=8.1.1"
set "GRADLE_ZIP=gradle-%GRADLE_VERSION%-bin.zip"
set "GRADLE_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"

echo ====== FIXING GRADLE WRAPPER ======
echo Using Java:
"%JAVA_EXE%" -version
echo.
echo Gradle version: %GRADLE_VERSION%
echo ===================================

:: Clean up old Gradle files
echo Cleaning up old Gradle files...
if exist gradlew.bat del /q gradlew.bat
if exist gradlew del /q gradlew
if exist gradle\wrapper\gradle-wrapper.jar del /q gradle\wrapper\gradle-wrapper.jar
if exist gradle\wrapper\gradle-wrapper.properties del /q gradle\wrapper\gradle-wrapper.properties
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist %GRADLE_ZIP% del /q %GRADLE_ZIP%

:: Ensure gradle/wrapper directory exists
if not exist gradle\wrapper mkdir gradle\wrapper

:: Download Gradle
echo Downloading Gradle %GRADLE_VERSION%...
powershell -Command "if (Test-Path '%GRADLE_ZIP%') { Remove-Item '%GRADLE_ZIP%' }; (New-Object System.Net.WebClient).DownloadFile('%GRADLE_URL%', '%GRADLE_ZIP%')"
if %ERRORLEVEL% NEQ 0 (
    echo Failed to download Gradle %GRADLE_VERSION%
    exit /b 1
)

:: Extract gradle-wrapper.jar
echo Extracting gradle-wrapper.jar...
if not exist temp_%GRADLE_VERSION% mkdir temp_%GRADLE_VERSION%
unzip -q %GRADLE_ZIP% -d temp_%GRADLE_VERSION%
copy /Y "temp_%GRADLE_VERSION%\gradle-%GRADLE_VERSION%\lib\gradle-wrapper.jar" "gradle\wrapper\"
copy /Y "temp_%GRADLE_VERSION%\gradle-%GRADLE_VERSION%\bin\gradlew" "."
copy /Y "temp_%GRADLE_VERSION%\gradle-%GRADLE_VERSION%\bin\gradlew.bat" "."

:: Create/Update gradle-wrapper.properties
echo Creating/Updating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=%GRADLE_URL%>> gradle\wrapper\gradle-wrapper.properties
echo networkTimeout=10000>> gradle\wrapper\gradle-wrapper.properties
echo validateDistributionUrl=true>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Clean up
echo Cleaning up temporary files...
if exist temp_%GRADLE_VERSION% rmdir /s /q temp_%GRADLE_VERSION%
if exist %GRADLE_ZIP% del /q %GRADLE_ZIP%

:: Make gradlew executable on Unix-like systems
echo Making gradlew executable...
if exist gradlew (
    icacls gradlew /grant Everyone:RX /T /C >nul 2>&1
)

echo.
echo ====== GRADLE WRAPPER FIXED ======
echo Gradle wrapper has been fixed and configured to use version %GRADLE_VERSION%
echo Run '.\gradlew.bat build' to start the build.
echo ==================================

exit /b 0
