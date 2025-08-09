@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home
echo Setting Java 17 environment...
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

echo ====== VERIFYING JAVA VERSION ======
java -version
echo.
echo JAVA_HOME: %JAVA_HOME%
echo.

echo ====== CLEANING PREVIOUS BUILDS ======
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle

:: Create a simple build.gradle if it doesn't exist
if not exist build.gradle (
    echo Creating minimal build.gradle...
    echo plugins { id 'java' } > build.gradle
    echo "repositories { mavenCentral() }" >> build.gradle
    echo "dependencies { testImplementation 'junit:junit:4.13.2' }" >> build.gradle
)

echo ====== RUNNING GRADLE BUILD ======
set GRADLE_CMD=gradle
if exist gradlew.bat set GRADLE_CMD=call gradlew.bat

%GRADLE_CMD% --version
echo.

echo Starting build with timeout (5 minutes)...
%GRADLE_CMD% build --no-daemon --stacktrace --info

if %ERRORLEVEL% EQU 0 (
    echo Build completed successfully!
) else (
    echo Build failed with error code %ERRORLEVEL%
)

endlocal
