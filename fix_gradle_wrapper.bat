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

:: Clean up old Gradle wrapper files
echo Cleaning up old Gradle wrapper...
if exist gradlew del /q gradlew
if exist gradlew.bat del /q gradlew.bat
if exist gradle rmdir /s /q gradle

:: Download and set up Gradle 7.5.1
echo Downloading Gradle 7.5.1...
set GRADLE_VERSION=7.5.1
set GRADLE_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip
set GRADLE_ZIP=gradle-%GRADLE_VERSION%-bin.zip

:: Download Gradle
powershell -Command "(New-Object System.Net.WebClient).DownloadFile('%GRADLE_URL%', '%GRADLE_ZIP%')"

:: Extract Gradle
echo Extracting Gradle...
if not exist gradle-temp mkdir gradle-temp
tar -xf %GRADLE_ZIP% -C gradle-temp --strip-components=1

:: Create Gradle wrapper
echo Creating Gradle wrapper...
if not exist gradle\wrapper mkdir gradle\wrapper
copy /Y gradle-temp\bin\gradle.bat gradlew.bat
copy /Y gradle-temp\bin\gradle gradlew
copy /Y gradle-temp\lib\gradle-wrapper.jar gradle\wrapper\

:: Create gradle-wrapper.properties
echo Creating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Clean up
echo Cleaning up...
del /q %GRADLE_ZIP%
rmdir /s /q gradle-temp

:: Make wrapper executable
echo Making wrapper executable...
attrib +r gradlew.bat
attrib +r gradlew

:: Test the wrapper
echo Testing Gradle wrapper...
call gradlew --version

echo.
echo Gradle wrapper has been fixed. You can now run the build using: gradlew build

