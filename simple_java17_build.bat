@echo off
setlocal

:: Set Java 17 home with proper quoting
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify Java version
"%JAVA_HOME%\bin\java" -version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to run Java from %JAVA_HOME%\bin\java
    exit /b 1
)

echo ====== Java Environment ======
echo JAVA_HOME: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo =============================
echo.

:: Clean build directories
echo Cleaning build directories...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist run rmdir /s /q run

:: Run the build with Java 17
echo ====== Starting Build ======
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en"
call gradlew build --stacktrace --info --no-daemon -Dorg.gradle.java.home="%JAVA_HOME%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====== Build Succeeded ======
    echo Build artifacts should be in the build/libs directory.
    exit /b 0
) else (
    echo.
    echo ====== Build Failed ======
    echo Check the output above for error details.
    exit /b %ERRORLEVEL%
)
