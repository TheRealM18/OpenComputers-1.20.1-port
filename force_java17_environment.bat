@echo off
setlocal enabledelayedexpansion

echo =============================================
echo   FORCING JAVA 17 ENVIRONMENT
echo =============================================
echo.

:: Set Java 17 home
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_BIN=%JAVA_HOME%\bin"

:: Add Java 17 to PATH
set "PATH=%JAVA_BIN%;%PATH%"

:: Stop all Java processes
echo Stopping all Java processes...
taskkill /F /IM java.exe /T >nul 2>&1

:: Clean Gradle caches
echo Cleaning Gradle caches...
if exist "%USERPROFILE%\.gradle" (
    echo Removing Gradle caches...
    rmdir /s /q "%USERPROFILE%\.gradle\caches"
    rmdir /s /q "%USERPROFILE%\.gradle\daemon"
    rmdir /s /q "%USERPROFILE%\.gradle\workers"
    rmdir /s /q "%USERPROFILE%\.gradle\native"
)

:: Clean project build directories
echo Cleaning project build directories...
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle
if exist run rmdir /s /q run
if exist gradle rmdir /s /q gradle
if exist gradlew del /q gradlew
if exist gradlew.bat del /q gradlew.bat

:: Set environment variables for this session
echo Setting environment variables...
set GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8
set ORG_GRADLE_JAVA_HOME=%JAVA_HOME%
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

:: Verify Java version
echo.
echo =============================================
echo   VERIFYING JAVA VERSION
echo =============================================
java -version

:: Create a new gradle wrapper with Java 17
echo.
echo =============================================
echo   SETTING UP GRADLE WRAPPER WITH JAVA 17
echo =============================================
set GRADLE_VERSION=7.5.1
set GRADLE_ZIP=gradle-%GRADLE_VERSION%-bin.zip

:: Download Gradle using PowerShell
echo Downloading Gradle %GRADLE_VERSION%...
powershell -Command "if (Test-Path '%GRADLE_ZIP%') { Remove-Item '%GRADLE_ZIP%' }; (New-Object System.Net.WebClient).DownloadFile('https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip', '%GRADLE_ZIP%')"

:: Extract Gradle
if exist gradle-* rmdir /s /q gradle-*
if not exist gradle-* mkdir gradle-temp
powershell -Command "Expand-Archive -Path '%GRADLE_ZIP%' -DestinationPath .\gradle-temp"
for /d %%d in (gradle-temp\*) do (
    if not exist gradle-* (
        ren "%%d" gradle-%GRADLE_VERSION%
    )
)
rmdir /s /q gradle-temp

:: Create gradle wrapper
echo Creating Gradle wrapper...
if not exist gradle mkdir gradle
if not exist gradle\wrapper mkdir gradle\wrapper

echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Create a simple gradlew.bat
echo @echo off> gradlew.bat
echo setlocal>> gradlew.bat
echo set JAVA_HOME=%JAVA_HOME%>> gradlew.bat
echo set PATH=%%JAVA_HOME%%in;%%PATH%%>> gradlew.bat
echo set GRADLE_OPTS=-Dorg.gradle.java.home=%%JAVA_HOME%% -Dfile.encoding=UTF-8>> gradlew.bat
echo if exist "gradle\wrapper\gradle-wrapper.jar" (>> gradlew.bat
echo     java -Dorg.gradle.java.home=%%JAVA_HOME%% -Dfile.encoding=UTF-8 -jar "gradle\wrapper\gradle-wrapper.jar" %%*>> gradlew.bat
echo ) else (>> gradlew.bat
echo     echo Error: Gradle wrapper not found. Please run 'gradle wrapper' first.>> gradlew.bat
echo     exit /b 1>> gradlew.bat
echo )>> gradlew.bat

:: Download gradle-wrapper.jar
echo Downloading gradle-wrapper.jar...
powershell -Command "(New-Object System.Net.WebClient).DownloadFile('https://github.com/gradle/gradle/raw/v7.5.1/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar')"

:: Verify setup
echo.
echo =============================================
echo   VERIFYING GRADLE WRAPPER
echo =============================================
if exist gradle\wrapper\gradle-wrapper.jar (
    echo Gradle wrapper setup complete.
) else (
    echo Error: Failed to set up Gradle wrapper.
    exit /b 1
)

echo.
echo =============================================
echo   JAVA 17 ENVIRONMENT SETUP COMPLETE
echo =============================================
echo.
echo To build the project, run:
echo   .\gradlew clean build

exit /b 0
