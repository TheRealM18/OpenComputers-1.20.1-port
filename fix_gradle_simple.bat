@echo off
setlocal

:: Set Java 17 home
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set PATH=%JAVA_HOME%\bin;%PATH%

echo Java version:
java -version
echo.
echo JAVA_HOME is set to %JAVA_HOME%
echo.

:: Create gradle/wrapper directory if it doesn't exist
if not exist gradle\wrapper mkdir gradle\wrapper

:: Create gradle-wrapper.properties
echo Creating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Download gradle-wrapper.jar
echo Downloading gradle-wrapper.jar...
powershell -Command "(New-Object System.Net.WebClient).DownloadFile('https://github.com/gradle/gradle/raw/v7.5.1/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar')"

:: Create a simple gradlew.bat
echo Creating gradlew.bat...
echo @echo off> gradlew.bat
echo setlocal>> gradlew.bat
echo set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot">> gradlew.bat
echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%>> gradlew.bat
echo java -Dorg.gradle.appname=gradlew -classpath "%~dp0gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %%*>> gradlew.bat

:: Verify the wrapper
echo Verifying Gradle wrapper...
call gradlew --version

echo.
echo If you see the Gradle version above, the wrapper is working.
echo You can now run: gradlew setupDecompWorkspace

echo.
echo Running setupDecompWorkspace...
call gradlew setupDecompWorkspace --stacktrace --info

echo.
echo If you see no errors above, you can now run: gradlew build

