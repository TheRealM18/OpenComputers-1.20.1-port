@echo off
echo Setting up OpenComputers development environment for Minecraft 1.20.1...
echo.

:: Set Java home to Java 17 if not already set
if "%JAVA_HOME%"=="" (
    echo JAVA_HOME is not set. Attempting to find Java 17...
    for /f "tokens=*" %%a in ('where /r "C:\Program Files\Java" java.exe 2^>nul') do (
        "%%~dpa\java.exe" -version 2>&1 | find "17" >nul
        if !errorlevel!==0 (
            set "JAVA_HOME=%%~dpa.."
            setx JAVA_HOME "%%~dpa.." /M
            echo Set JAVA_HOME to %JAVA_HOME%
            goto :java_home_set
        )
    )
    echo ERROR: Could not find Java 17. Please install JDK 17 and set JAVA_HOME.
    exit /b 1
) else (
    echo Java is already configured at %JAVA_HOME%
)

:java_home_set

:: Update Gradle wrapper
echo.
echo Updating Gradle wrapper...
call gradlew wrapper --gradle-version 8.1.1

:: Download assets
echo.
echo Downloading assets...
call gradlew --stop
call gradlew --refresh-dependencies

:: Generate IDE files
echo.
echo Generating IDE files...
call gradlew genEclipseRuns --refresh-dependencies
call gradlew genIntellijRuns --refresh-dependencies

echo.
echo Development environment setup complete!
echo You can now open the project in your preferred IDE.
echo.
exit /b 0
