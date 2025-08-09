@echo off
setlocal

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo ====== JAVA VERSION ======
java -version
echo =========================

set GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8

call gradlew clean build --stacktrace --info --debug > debug_build.log 2>&1

if %ERRORLEVEL% EQU 0 (
    echo Build completed successfully!
    echo.
    echo ====== GENERATED FILES ======
    dir /s /b build\libs\*.jar
) else (
    echo Build failed with error code %ERRORLEVEL%
    echo Check debug_build.log for details
)

echo =========================
echo Build log: %CD%\debug_build.log
endlocal
