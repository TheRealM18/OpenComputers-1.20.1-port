@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home
echo ====== ENVIRONMENT ======
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home=%JAVA_HOME% -Dfile.encoding=UTF-8"

echo Java Home: %JAVA_HOME%
echo Java Version:
java -version
echo.
echo PATH: %PATH%
echo =========================
echo.

:: Clean previous build
echo ====== CLEANING ======
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist run rmdir /s /q run
echo ======================
echo.

:: Run Gradle with debug output
echo ====== STARTING GRADLE BUILD ======
call gradlew clean build --no-daemon --info --stacktrace > build_debug.log 2>&1

:: Check result
echo ====== BUILD RESULT ======
if %ERRORLEVEL% EQU 0 (
    echo Build completed successfully!
    echo.
    echo ====== GENERATED FILES ======
    dir /s /b build\libs\*.jar
) else (
    echo Build failed with error code %ERRORLEVEL%
    echo Check build_debug.log for details
)

echo ==========================
echo Build log: %CD%\build_debug.log
endlocal
