@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home=%JAVA_HOME% -Dfile.encoding=UTF-8"

echo ====== CLEANING ENVIRONMENT ======

:: Stop any running Gradle daemons
call gradlew --stop >nul 2>&1

:: Kill any remaining Java processes
taskkill /F /IM java.exe /T >nul 2>&1

:: Clean Gradle caches
if exist "%USERPROFILE%\.gradle\caches" rmdir /s /q "%USERPROFILE%\.gradle\caches"
if exist "%USERPROFILE%\.gradle\daemon" rmdir /s /q "%USERPROFILE%\.gradle\daemon"
if exist "%USERPROFILE%\.gradle\workers" rmdir /s /q "%USERPROFILE%\.gradle\workers"

:: Clean project directories
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist run rmdir /s /q run

:: Create fresh Gradle wrapper
echo ====== SETTING UP GRADLE ======
if exist gradlew (
    call gradlew wrapper --gradle-version 7.5.1 --no-daemon
) else (
    echo Downloading Gradle wrapper...
    powershell -Command "& { (New-Object Net.WebClient).DownloadFile('https://services.gradle.org/distributions/gradle-7.5.1-bin.zip', 'gradle.zip') }"
    if exist gradle.zip (
        powershell -Command "Expand-Archive -Path gradle.zip -DestinationPath . -Force"
        del /f /q gradle.zip
        move /y gradle-7.5.1\* .
        rmdir /s /q gradle-7.5.1
    )
)

echo ====== VERIFYING JAVA VERSION ======
echo Java Version:
java -version
echo.
echo JAVA_HOME: %JAVA_HOME%
echo.

:: Run the build
echo ====== STARTING BUILD ======
call gradlew clean build --no-daemon --info --stacktrace > build_output.log 2>&1

:: Check result
echo ====== BUILD RESULT ======
if %ERRORLEVEL% EQU 0 (
    echo Build completed successfully!
    echo.
    echo ====== GENERATED FILES ======
    dir /s /b build\libs\*.jar
) else (
    echo Build failed with error code %ERRORLEVEL%
    echo Check build_output.log for details
)

echo ==========================
echo Build log: %CD%\build_output.log
endlocal
