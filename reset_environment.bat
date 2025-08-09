@echo off
setlocal enabledelayedexpansion

:: Set timeout to 3 minutes (180 seconds)
set "TIMEOUT_SECONDS=180"
set "TIMEOUT_MS=180000"

echo ====== RESETTING BUILD ENVIRONMENT ======

:: Set Java 17 home (using short path to avoid spaces)
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"

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

:: Clean up
echo ====== CLEANING UP ======
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist .idea rmdir /s /q .idea
del /q *.log 2>nul

echo.
echo ====== SETTING UP GRADLE WRAPPER ======
if not exist gradle\wrapper mkdir gradle\wrapper

(
    echo distributionBase=GRADLE_USER_HOME
    echo distributionPath=wrapper/dists
    echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
    echo zipStoreBase=GRADLE_USER_HOME
    echo zipStorePath=wrapper/dists
) > gradle\wrapper\gradle-wrapper.properties

:: Download Gradle wrapper with timeout
echo Downloading Gradle wrapper (timeout: %TIMEOUT_SECONDS% seconds)...
powershell -Command "$webClient = New-Object System.Net.WebClient; $downloadTask = $webClient.DownloadFileTaskAsync('https://raw.githubusercontent.com/gradle/gradle/v8.1.1/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar'); if (-not $downloadTask.Wait(%TIMEOUT_MS%)) { Write-Error 'Download timed out'; exit 1 } elseif ($downloadTask.Exception) { Write-Error $downloadTask.Exception; exit 1 } else { exit 0 }"
if !ERRORLEVEL! NEQ 0 (
    echo Failed to download Gradle wrapper within %TIMEOUT_SECONDS% seconds or encountered an error.
    exit /b 1
)

:: Create a simple gradlew.bat
echo @echo off > gradlew.bat
echo set "JAVA_HOME=%JAVA_HOME%" >> gradlew.bat
echo set "PATH=%%JAVA_HOME%%\bin;%%PATH%%" >> gradlew.bat
echo "%%JAVA_HOME%%\bin\java" -jar "%%~dp0gradle\wrapper\gradle-wrapper.jar" %%* >> gradlew.bat

echo.
echo ====== ENVIRONMENT RESET COMPLETE ======
echo Run '.\gradlew.bat build' to start the build.

