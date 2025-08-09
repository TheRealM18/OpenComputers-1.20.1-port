@echo off 
setlocal enabledelayedexpansion 
set JAVA_HOME=%~dp0jdk-17.0.15.6-hotspot 
if not exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot" 
set PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot\bin;%PATH% 
set GRADLE_OPTS=-Dorg.gradle.java.home=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot -Dfile.encoding=UTF-8 
"C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot\bin\java" -Dorg.gradle.appname=gradlew -classpath "%~dp0gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %* 
