@echo off
setlocal

:: Set Java 17 home
set JAVA_HOME="C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.15.6-hotspot"
set PATH=%JAVA_HOME%\bin;%PATH%

echo Java version set to:
java -version
echo.
echo JAVA_HOME is now set to %JAVA_HOME%
echo.

:: Run the original command if any arguments were passed
if not "%1"=="" (
    echo Running: %*
    %*
    exit /b %ERRORLEVEL%
) else (
    echo Java 17 environment is set up.
    echo To use this environment in the current command prompt, run:
    echo   call set_java17.bat
    exit /b 0
)
