@echo off
setlocal

:: Set Java 17 Home - Update this path to your Java 17 installation if different
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java version:
java -version
echo.

echo Cleaning previous build...
call gradlew clean --stacktrace

echo Running build with Java 17...
call gradlew build --stacktrace -Dorg.gradle.java.home="%JAVA_HOME%"

if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo Build completed successfully!
endlocal
