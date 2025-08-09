@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home with proper escaping
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE="%JAVA_HOME%\bin\java.exe""

:: Check if Java exists
if not exist %JAVA_EXE% (
    echo Java not found at: %JAVA_EXE%
    exit /b 1
)

echo ====== VERIFYING JAVA ======
%JAVA_EXE% -version
if %ERRORLEVEL% NEQ 0 (
    echo Failed to run Java. Please check JAVA_HOME.
    exit /b 1
)

:: Set up Gradle wrapper if it doesn't exist
if not exist gradle\wrapper (
    mkdir gradle\wrapper
)

if not exist gradle\wrapper\gradle-wrapper.properties (
    echo Creating gradle-wrapper.properties...
    (
        echo distributionBase=GRADLE_USER_HOME
        echo distributionPath=wrapper/dists
        echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
        echo zipStoreBase=GRADLE_USER_HOME
        echo zipStorePath=wrapper/dists
    ) > gradle\wrapper\gradle-wrapper.properties
)

if not exist gradle\wrapper\gradle-wrapper.jar (
    echo Downloading Gradle wrapper...
    powershell -Command "& { (New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.1.1/gradle/wrapper/gradle-wrapper.jar', 'gradle\wrapper\gradle-wrapper.jar'); exit $LastExitCode }"
    if %ERRORLEVEL% NEQ 0 (
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
        echo set "GRADLE_OPTS=-Dorg.gradle.java.home=%%JAVA_HOME%% -Dfile.encoding=UTF-8"
        echo "%%JAVA_HOME%%\bin\java" %%GRADLE_OPTS%% -jar "%%~dp0gradle\wrapper\gradle-wrapper.jar" %%*
    ) > gradlew.bat
)

echo ====== RUNNING GRADLE ======
call gradlew.bat --version
if %ERRORLEVEL% NEQ 0 (
    echo Gradle setup failed.
    exit /b 1
)

echo ====== STARTING BUILD ======
call gradlew.bat build --no-daemon --stacktrace --info

if %ERRORLEVEL% EQU 0 (
    echo ====== BUILD SUCCESSFUL ======
) else (
    echo ====== BUILD FAILED ======
)

