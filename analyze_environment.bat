@echo off
setlocal enabledelayedexpansion

echo =============================================
echo   BUILD ENVIRONMENT ANALYSIS
echo =============================================

echo [SYSTEM INFO]
systeminfo | findstr /B /C:"OS Name" /C:"OS Version"
echo.

echo [JAVA VERSIONS]
where /r "C:\Program Files\Java" java.exe 2>nul
where /r "C:\Program Files\Eclipse Adoptium" java.exe 2>nul
echo.

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [CURRENT JAVA VERSION]
java -version 2>&1
echo.

echo [GRADLE VERSION]
where gradlew.bat 2>nul
if exist gradlew.bat (
    call gradlew.bat --version 2>&1 | findstr /B /C:"Gradle" /C:"Kotlin" /C:"Groovy"
) else (
    echo Gradle wrapper not found
)
echo.

echo [GRADLE PROPERTIES]
if exist gradle.properties (
    type gradle.properties
) else (
    echo gradle.properties not found
)
echo.

echo [GRADLE WRAPPER PROPERTIES]
if exist gradle\wrapper\gradle-wrapper.properties (
    type gradle\wrapper\gradle-wrapper.properties
) else (
    echo gradle-wrapper.properties not found
)
echo.

echo [ENVIRONMENT VARIABLES]
echo JAVA_HOME=%JAVA_HOME%
echo GRADLE_HOME=%GRADLE_HOME%
echo PATH=!PATH!
echo.

echo [BUILD.GRADLE CHECKSUM]
if exist build.gradle (
    certutil -hashfile build.gradle MD5
) else (
    echo build.gradle not found
)
echo.

echo =============================================
echo   ANALYSIS COMPLETE
echo =============================================

endlocal
