@echo off
setlocal

:: Check Java version
echo Java version:
java -version

echo.
echo JAVA_HOME is set to %JAVA_HOME%

echo.
echo Checking Gradle Java version...
call gradlew --version

exit /b %ERRORLEVEL%
