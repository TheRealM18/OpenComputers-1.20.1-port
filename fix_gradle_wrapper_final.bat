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

:: Clean up old Gradle files
echo Cleaning up old Gradle files...
if exist gradlew del /q gradlew
if exist gradlew.bat del /q gradlew.bat
if exist gradle rmdir /s /q gradle
if exist .gradle rmdir /s /q .gradle
if exist gradle-*.zip del /q gradle-*.zip

:: Download Gradle 7.5.1
echo Downloading Gradle 7.5.1...
set GRADLE_VERSION=7.5.1
set GRADLE_ZIP=gradle-%GRADLE_VERSION%-bin.zip

:: Download Gradle using PowerShell
powershell -Command "if (Test-Path '%GRADLE_ZIP%') { Remove-Item '%GRADLE_ZIP%' }; (New-Object System.Net.WebClient).DownloadFile('https://services.gradle.org/distributions/gradle-7.5.1-bin.zip', '%GRADLE_ZIP%')"

:: Extract Gradle
if not exist gradle-7.5.1 mkdir gradle-7.5.1
tar -xf %GRADLE_ZIP% -C gradle-7.5.1 --strip-components=1

:: Create gradle/wrapper directory
if not exist gradle\wrapper mkdir gradle\wrapper

:: Copy required files
copy /Y gradle-7.5.1\bin\gradle.bat gradlew.bat
copy /Y gradle-7.5.1\bin\gradle gradlew
copy /Y gradle-7.5.1\lib\gradle-wrapper.jar gradle\wrapper\

:: Create gradle-wrapper.properties
echo Creating gradle-wrapper.properties...
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

:: Make gradlew executable
attrib +r gradlew.bat
attrib +r gradlew

:: Clean up
del /q %GRADLE_ZIP%
rmdir /s /q gradle-7.5.1

:: Verify the wrapper
echo Verifying Gradle wrapper...
call gradlew --version

echo.
echo Gradle wrapper has been fixed. You can now run the build using: gradlew build

