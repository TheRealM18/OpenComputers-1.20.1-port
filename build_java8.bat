@echo off
REM Batch file to build OpenComputers using Java 8
setlocal enabledelayedexpansion

REM Set Java 8 paths
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.452.9-hotspot"
set "JAVA_BIN=%JAVA_HOME%\bin"
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%""

REM Add Java 8 to PATH
set "PATH=%JAVA_BIN%;%PATH%"

REM Display build information
echo =============================================
echo   Building OpenComputers with Java 8
echo   Java Home: %JAVA_HOME%
echo   Java Bin:  %JAVA_BIN%
echo =============================================

REM Verify Java version
echo.
echo Verifying Java version...
"%JAVA_BIN%\java.exe" -version
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java 8 is not properly installed or configured.
    echo Please ensure Java 8 is installed at: %JAVA_HOME%
    exit /b 1
)

REM Clean and build with Gradle
echo.
echo Starting Gradle build...
cd /D "%~dp0"
call "gradlew.bat" clean build --refresh-dependencies --no-daemon --stacktrace

REM Check build result
if %ERRORLEVEL% EQU 0 (
    echo.
    echo =============================================
    echo   Build completed successfully!
    echo =============================================
) else (
    echo.
    echo =============================================
    echo   Build failed with error code %ERRORLEVEL%
    echo =============================================
    exit /b %ERRORLEVEL%
)

endlocal
