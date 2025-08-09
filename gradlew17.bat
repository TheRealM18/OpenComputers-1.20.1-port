@echo off
setlocal

:: Set Java 17 home
set JAVA_HOME="C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.15.6-hotspot"
set PATH=%JAVA_HOME%\bin;%PATH%

:: Run the gradlew script with the same arguments
call gradlew %*
