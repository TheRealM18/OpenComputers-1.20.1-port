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

echo ====== SETTING UP GRADLE WRAPPER ======
echo Using Java:
"%JAVA_EXE%" -version
echo.
echo Gradle version: %GRADLE_VERSION%
echo =======================================

:: Clean up old Gradle files
echo Cleaning up old Gradle files...
if exist gradlew.bat del /q gradlew.bat
if exist gradlew del /q gradlew
if exist gradle\wrapper rmdir /s /q gradle\wrapper
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist %GRADLE_ZIP% del /q %GRADLE_ZIP%

:: Create gradle/wrapper directory
if not exist gradle\wrapper mkdir gradle\wrapper

:: Download Gradle
echo Downloading Gradle %GRADLE_VERSION%...
powershell -Command "if (Test-Path '%GRADLE_ZIP%') { Remove-Item '%GRADLE_ZIP%' }; (New-Object System.Net.WebClient).DownloadFile('%GRADLE_URL%', '%GRADLE_ZIP%')"
if %ERRORLEVEL% NEQ 0 (
    echo Failed to download Gradle %GRADLE_VERSION%
    exit /b 1
)

:: Extract Gradle
echo Extracting Gradle...
if not exist gradle-temp mkdir gradle-temp
powershell -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::ExtractToDirectory('%GRADLE_ZIP%', 'gradle-temp')"
if %ERRORLEVEL% NEQ 0 (
    echo Failed to extract Gradle
    exit /b 1
)

:: Copy wrapper files
echo Setting up Gradle wrapper...
copy /Y gradle-temp\gradle-%GRADLE_VERSION%\gradlew .
copy /Y gradle-temp\gradle-%GRADLE_VERSION%\gradlew.bat .
copy /Y gradle-temp\gradle-%GRADLE_VERSION%\gradle\wrapper\gradle-wrapper.jar gradle\wrapper\

:: Create gradle-wrapper.properties
echo Creating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=%GRADLE_URL%>> gradle\wrapper\gradle-wrapper.properties
echo networkTimeout=10000>> gradle\wrapper\gradle-wrapper.properties
echo validateDistributionUrl=true>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Make gradlew executable
echo Setting execute permissions...
icacls gradlew /grant Everyone:RX /T /C >nul 2>&1

:: Clean up
echo Cleaning up temporary files...
if exist gradle-temp rmdir /s /q gradle-temp
if exist %GRADLE_ZIP% del /q %GRADLE_ZIP%

echo.
echo ====== GRADLE WRAPPER SETUP COMPLETE ======
echo Run the following command to build the project:
echo .\gradlew.bat build
echo =========================================

exit /b 0
