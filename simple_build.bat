@echo off
setlocal enabledelayedexpansion

:: Set Java home with short path to avoid spaces
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

echo ====== JAVA VERSION ======
"%JAVA_HOME%\bin\java" -version

if not exist gradle\wrapper (
    mkdir gradle\wrapper
)

echo ====== SETTING UP GRADLE ======
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Create a simple gradlew.bat
echo @echo off> gradlew.bat
echo set JAVA_HOME="C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot">> gradlew.bat
echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%>> gradlew.bat
echo "%%JAVA_HOME%%\bin\java" -jar "%%~dp0gradle\wrapper\gradle-wrapper.jar" %%*>> gradlew.bat

:: Download Gradle wrapper
echo Downloading Gradle wrapper...
powershell -Command "& { (New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.1.1/gradle/wrapper/gradle-wrapper.jar', 'gradle\wrapper\gradle-wrapper.jar'); exit $LastExitCode }"

if %ERRORLEVEL% NEQ 0 (
    echo Failed to download Gradle wrapper.
    exit /b 1
)

echo ====== RUNNING GRADLE ======
call gradlew.bat --version

if %ERRORLEVEL% NEQ 0 (
    echo Gradle setup failed.
    exit /b 1
)

echo ====== BUILDING ======
call gradlew.bat build --no-daemon --stacktrace --info

echo ====== DONE ======
