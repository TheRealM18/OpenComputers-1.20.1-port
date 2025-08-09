@echo off
setlocal enabledelayedexpansion

:: Set Java 17 home
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home=%JAVA_HOME% -Dfile.encoding=UTF-8"

echo ====== UPDATING GRADLE WRAPPER TO 8.1.1 ======
echo Java Version:
java -version
echo.

:: Clean up old Gradle files
if exist .gradle rmdir /s /q .gradle
if exist gradle\wrapper rmdir /s /q gradle\wrapper
if exist gradlew del /f /q gradlew
if exist gradlew.bat del /f /q gradlew.bat

:: Create new Gradle wrapper directory
if not exist gradle\wrapper mkdir gradle\wrapper

:: Create gradle-wrapper.properties
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Download Gradle wrapper JAR
echo Downloading Gradle wrapper...
powershell -Command "& { (New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.1.1/gradle/wrapper/gradle-wrapper.jar', 'gradle\wrapper\gradle-wrapper.jar')}"

:: Create gradlew.bat
echo @echo off> gradlew.bat
echo setlocal>> gradlew.bat
echo set DIRNAME=%%~dp0>> gradlew.bat
echo if "%%DIRNAME%%"=="" set DIRNAME=.>> gradlew.bat
echo set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot">> gradlew.bat
echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%>> gradlew.bat
echo set APP_HOME=%%DIRNAME%%>> gradlew.bat
echo set APP_BASE_NAME=%%~n0>> gradlew.bat
echo set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m">> gradlew.bat
echo set CLASSPATH=%%APP_HOME%%\gradle\wrapper\gradle-wrapper.jar>> gradlew.bat
echo set JAVA_EXE=java.exe>> gradlew.bat
echo if defined JAVA_HOME (>> gradlew.bat
echo   set JAVA_EXE=%%JAVA_HOME%%\bin\java.exe>> gradlew.bat
echo )>> gradlew.bat
echo set CLASSPATH=%%APP_HOME%%\gradle\wrapper\gradle-wrapper.jar>> gradlew.bat
echo set DEFAULT_JVM_OPTS=%%DEFAULT_JVM_OPTS: = %%>> gradlew.bat
echo set CLASSPATH=%%CLASSPATH: =;%%>> gradlew.bat
echo if "x%%JAVA_HOME%%" == "x" (>> gradlew.bat
echo   set JAVA_EXE=java>> gradlew.bat
echo ) else (>> gradlew.bat
echo   set JAVA_HOME=%%JAVA_HOME:"=%%>> gradlew.bat
echo   set JAVA_EXE=%%JAVA_HOME%%\bin\java.exe>> gradlew.bat
echo )>> gradlew.bat
echo if not exist "%%JAVA_EXE%%" (>> gradlew.bat
echo   set JAVA_EXE=java>> gradlew.bat
echo )>> gradlew.bat
echo set JAVA_OPTS=%%JAVA_OPTS: =%%>> gradlew.bat
echo for /f "tokens=*" %%%%i in ('"%%JAVA_EXE%%" -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%%%i>> gradlew.bat
echo set JAVA_VERSION=%%JAVA_VERSION:java version=%%>> gradlew.bat
echo set JAVA_VERSION=%%JAVA_VERSION:"=%%>> gradlew.bat
echo set JAVA_VERSION=%%JAVA_VERSION: =%%>> gradlew.bat
echo if "%%JAVA_VERSION:~0,2%%" GEQ "17" (>> gradlew.bat
echo   set DEFAULT_JVM_OPTS=%%DEFAULT_JVM_OPTS%% --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.prefs/java.util.prefs=ALL-UNNAMED --add-opens=java.base/java.nio.charset=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED>> gradlew.bat
echo )>> gradlew.bat
echo set CMD_LINE_ARGS=>> gradlew.bat
echo :setArgs>> gradlew.bat
echo if "%%1"=="" goto execute>> gradlew.bat
echo set CMD_LINE_ARGS=%%CMD_LINE_ARGS%% %%1>> gradlew.bat
echo shift>> gradlew.bat
echo goto setArgs>> gradlew.bat
echo :execute>> gradlew.bat
echo set CLASSPATH=%%CLASSPATH: =;%%>> gradlew.bat
echo "%%JAVA_EXE%%" %%DEFAULT_JVM_OPTS%% %%JAVA_OPTS%% -classpath "%%CLASSPATH%%" org.gradle.wrapper.GradleWrapperMain %%CMD_LINE_ARGS%%>> gradlew.bat
echo exit /b %%ERRORLEVEL%%>> gradlew.bat

echo ====== GRADLE WRAPPER UPDATED ======
echo.
echo 1. Run: .\gradlew.bat --version
echo 2. Then: .\gradlew.bat build
echo.

endlocal
