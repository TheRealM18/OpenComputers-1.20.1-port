@echo off
echo Testing Java installation...

:: Try to find Java 17
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

if not exist "%JAVA_EXE%" (
    echo Java 17 not found at: %JAVA_EXE%
    exit /b 1
)

echo Found Java at: %JAVA_EXE%
echo.
echo ====== JAVA VERSION ======
"%JAVA_EXE%" -version

echo.
echo ====== RUNNING GRADLE ======
if exist gradlew.bat (
    call gradlew.bat --version
) else (
    echo gradlew.bat not found
)

