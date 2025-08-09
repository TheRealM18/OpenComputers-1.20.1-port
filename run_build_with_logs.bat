@echo off
setlocal

echo Starting build with detailed logging...
echo Build started at: %DATE% %TIME% > build.log
echo Java version: >> build.log
java -version 2>> build.log
echo. >> build.log
echo JAVA_HOME: %JAVA_HOME% >> build.log
echo. >> build.log

echo Running Gradle build with full output...
call gradlew build --stacktrace --info --no-daemon >> build.log 2>&1

if %ERRORLEVEL% EQU 0 (
    echo Build completed successfully! Check build.log for details.
    exit /b 0
) else (
    echo Build failed with error code %ERRORLEVEL%. Check build.log for details.
    exit /b %ERRORLEVEL%
)
