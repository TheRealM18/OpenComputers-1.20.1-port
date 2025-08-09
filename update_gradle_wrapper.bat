@echo off
REM Script to update Gradle wrapper to version 6.8.3
setlocal enabledelayedexpansion

set GRADLE_VERSION=6.8.3
set GRADLE_URL=https\://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip

echo Updating Gradle wrapper to version %GRADLE_VERSION%...

REM Create the wrapper directory if it doesn't exist
if not exist "gradle\wrapper" mkdir "gradle\wrapper"

REM Create or update gradle wrapper properties with proper line endings
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=!GRADLE_URL!
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "gradle\wrapper\gradle-wrapper.properties"

echo.
echo Gradle wrapper has been updated to version %GRADLE_VERSION%
echo Run 'build_java8.bat' to build the project

endlocal
