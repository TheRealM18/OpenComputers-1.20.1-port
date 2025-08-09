@echo off
setlocal enabledelayedexpansion

:: Set timeout to 3 minutes (180 seconds = 180,000 milliseconds)
set "TIMEOUT_MS=180000"
set "TIMEOUT_SECONDS=180"

:: Set Java 17 home explicitly (using short path to avoid spaces)
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

:: Verify Java
echo ====== VERIFYING JAVA ======
"%JAVA_EXE%" -version
if !ERRORLEVEL! NEQ 0 (
    echo Failed to run Java. Please check JAVA_HOME.
    exit /b 1
)
echo.
echo JAVA_HOME is set to: %JAVA_HOME%
echo =============================================

:: Clean up previous build
echo ====== CLEANING PREVIOUS BUILDS ======
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle

:: Ensure Gradle wrapper exists
if not exist gradlew.bat (
    echo Gradle wrapper not found. Please run reset_environment.bat first.
    exit /b 1
)

:: Run the build with timeout
echo =============================================
echo   BUILDING WITH JAVA 17 (%TIMEOUT_SECONDS% SECOND TIMEOUT)
echo =============================================

echo Starting build with %TIMEOUT_SECONDS% second timeout...

:: Use PowerShell to run the build with a proper timeout
powershell -Command "
    $process = Start-Process -NoNewWindow -PassThru -FilePath 'cmd.exe' -ArgumentList '/c', 'gradlew.bat build --no-daemon --stacktrace --info';
    if (-not $process.WaitForExit(%TIMEOUT_MS%)) {
        Write-Host 'Build timed out after %TIMEOUT_SECONDS% seconds!';
        Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue;
        exit 1;
    } else {
        exit $process.ExitCode;
    }"

set "EXIT_CODE=!ERRORLEVEL!"

if !EXIT_CODE! EQU 0 (
    echo ====== BUILD SUCCESSFUL ======
) else (
    echo ====== BUILD FAILED (Error: !EXIT_CODE!) ======
)

exit /b !EXIT_CODE!

exit /b 0
