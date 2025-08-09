@echo off
REM Java 8 Wrapper for update_gradle_wrapper.bat
setlocal enabledelayedexpansion

REM Set Java 8 paths
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.452.9-hotspot"
set "JAVA_BIN=%JAVA_HOME%\bin"

REM Add Java 8 to PATH
set "PATH=%JAVA_BIN%;%PATH%"

REM Display build information
echo =============================================
echo   Updating Gradle Wrapper with Java 8
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

REM Run the update script
echo.
echo Running Gradle wrapper update...
call "%~dp0update_gradle_wrapper.bat"

REM Check result
if %ERRORLEVEL% EQU 0 (
    echo.
    echo =============================================
    echo   Gradle wrapper update completed successfully!
    echo   You can now run build_java8.bat to build the project
    echo =============================================
) else (
    echo.
    echo =============================================
    echo   Gradle wrapper update failed with error code %ERRORLEVEL%
    echo =============================================
    exit /b %ERRORLEVEL%
)

endlocal
