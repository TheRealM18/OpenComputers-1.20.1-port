@echo off
setlocal

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo ====== CLEANING ======
call gradlew clean

if %ERRORLEVEL% NEQ 0 (
    echo Clean failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo.
echo ====== BUILDING ======
call gradlew build --info --stacktrace > build_output.log 2>&1

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====== BUILD SUCCESSFUL ======
    echo.
    echo Generated files:
    if exist build\libs (
        dir /s /b build\libs\*.jar
    ) else (
        echo No build artifacts found in build\libs
    )
) else (
    echo.
    echo ====== BUILD FAILED ======
    echo Check build_output.log for details
)

endlocal
