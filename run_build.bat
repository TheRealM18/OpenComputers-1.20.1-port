@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home (using short path to avoid spaces)
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify Java
echo ====== VERIFYING JAVA ======
"%JAVA_EXE%" -version
if !ERRORLEVEL! NEQ 0 (
    echo Failed to run Java. Please check JAVA_HOME.
    exit /b 1
)
echo.
echo JAVA_HOME is set to: %JAVA_HOME%
echo.

:: Clean previous build
echo ====== CLEANING PREVIOUS BUILDS ======
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle

:: Set up Gradle wrapper
echo ====== SETTING UP GRADLE WRAPPER ======
if not exist gradle\wrapper mkdir gradle\wrapper

(
    echo distributionBase=GRADLE_USER_HOME
    echo distributionPath=wrapper/dists
    echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
    echo zipStoreBase=GRADLE_USER_HOME
    echo zipStorePath=wrapper/dists
) > gradle\wrapper\gradle-wrapper.properties

if not exist gradle\wrapper\gradle-wrapper.jar (
    echo Downloading Gradle wrapper...
    powershell -Command "& { (New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.1.1/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar'); exit $LastExitCode }"
    if !ERRORLEVEL! NEQ 0 (
        echo Failed to download Gradle wrapper.
        exit /b 1
    )
)

if not exist gradlew.bat (
    echo Creating gradlew.bat...
    (
        echo @echo off
        echo setlocal
        echo set "JAVA_HOME=%JAVA_HOME%"
        echo set "PATH=%%JAVA_HOME%%\bin;%%PATH%%"
        echo "%%JAVA_HOME%%\bin\java" -jar "%%~dp0gradle\wrapper\gradle-wrapper.jar" %%*
    ) > gradlew.bat
)

:: Run the build
echo.
echo ====== STARTING BUILD ======
call gradlew.bat clean build --no-daemon --stacktrace --info

echo.
if !ERRORLEVEL! EQU 0 (
    echo ====== BUILD SUCCESSFUL ======
) else (
    echo ====== BUILD FAILED (Error: !ERRORLEVEL!) ======
)

exit /b %ERRORLEVEL%
