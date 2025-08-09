@echo off
setlocal enabledelayedexpansion

:: Configuration
set "MAX_RETRIES=3"
set "RETRY_DELAY=10"  :: seconds between retries
set "TIMEOUT_SECONDS=180"
set "TIMEOUT_MS=180000"

:: Set Java 17 home (using short path to avoid spaces)
set "JAVA_HOME=C:\Progra~1\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "GRADLE_OPTS=-Dorg.gradle.java.home="%JAVA_HOME%" -Dfile.encoding=UTF-8"

:: Verify Java
echo ====== VERIFYING JAVA ======
"%JAVA_EXE%" -version
if !ERRORLEVEL! NEQ 0 (
    echo Failed to run Java. Please check JAVA_HOME.
    exit /b 1
)
echo.
echo JAVA_HOME is set to: %JAVA_HOME%
echo =============================================

echo ====== BUILD WITH RETRY ======
echo Max retries: %MAX_RETRIES%
echo Timeout: %TIMEOUT_SECONDS% seconds per attempt
echo =============================================

set ATTEMPT=1
:BUILD_ATTEMPT

echo.
echo ====== ATTEMPT !ATTEMPT! OF %MAX_RETRIES% ======
echo [%TIME%] Starting build...

:: Run the build with timeout
set "POWERSHELL_CMD=$process = Start-Process -NoNewWindow -PassThru -FilePath 'cmd.exe' -ArgumentList '/c', 'gradlew.bat build --no-daemon --stacktrace --info'; if (-not $process.WaitForExit(%TIMEOUT_MS%)) { Write-Host 'Build timed out after %TIMEOUT_SECONDS% seconds!'; Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue; exit 1 } else { exit $process.ExitCode }"
powershell -Command "%POWERSHELL_CMD%"

set "EXIT_CODE=!ERRORLEVEL!"

if !EXIT_CODE! EQU 0 (
    echo [%TIME%] ====== BUILD SUCCESSFUL ======
    exit /b 0
) else (
    echo [%TIME%] ====== BUILD FAILED (Error: !EXIT_CODE!) ======
    
    if !ATTEMPT! LSS %MAX_RETRIES% (
        set /a NEXT_ATTEMPT=!ATTEMPT!+1
        echo [%TIME%] Retrying in %RETRY_DELAY% seconds... (Attempt !NEXT_ATTEMPT! of %MAX_RETRIES%)
        
        :: Clean up before retry
        if exist build rmdir /s /q build
        if exist .gradle rmdir /s /q .gradle
        
        :: Wait before retry
        timeout /t %RETRY_DELAY% /nobreak >nul
        
        set /a ATTEMPT+=1
        goto BUILD_ATTEMPT
    ) else (
        echo [%TIME%] ====== MAXIMUM RETRIES REACHED ======
        exit /b 1
    )
)

exit /b 0
