@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home
echo ====== VERIFYING JAVA ENVIRONMENT ======
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

:: Verify Gradle version
echo ====== VERIFYING GRADLE ======
call gradlew --version
echo =========================
echo.

:: Check if build.gradle exists
if not exist build.gradle (
    echo Error: build.gradle not found in the current directory.
    exit /b 1
)

:: Check if settings.gradle exists
if not exist settings.gradle (
    echo Warning: settings.gradle not found. Creating a default one.
    echo rootProject.name = "OpenComputers" > settings.gradle
)

echo ====== VERIFICATION COMPLETE ======
echo Environment appears to be set up for Java 17.
echo You can now run the build using: gradlew clean build
endlocal
