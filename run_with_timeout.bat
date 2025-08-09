@echo off
setlocal enabledelayedexpansion

:: Default timeout of 3 minutes (180000 milliseconds)
set "TIMEOUT_MS=180000"
set "TIMEOUT_SECONDS=180"

:: Check if command is provided
if "%~1"=="" (
    echo Error: No command specified.
    echo Usage: %~nx0 "command [args]" [timeout_ms]
    exit /b 1
)

:: Check if custom timeout is provided
if not "%~2"=="" (
    set "TIMEOUT_MS=%~2"
    set /a "TIMEOUT_SECONDS=%TIMEOUT_MS%/1000"
)

echo [%TIME%] Running command with %TIMEOUT_SECONDS% second timeout: %*

:: Run the command with timeout
powershell -Command "$process = Start-Process -NoNewWindow -PassThru -FilePath 'cmd.exe' -ArgumentList '/c', '%*'; if (-not $process.WaitForExit(%TIMEOUT_MS%)) { $process.Kill(); Write-Error 'Command timed out after %TIMEOUT_SECONDS% seconds'; exit 1 } else { exit $process.ExitCode }"
set "EXIT_CODE=!ERRORLEVEL!"

echo [%TIME%] Command completed with exit code: !EXIT_CODE!

:: Return the exit code from the command
exit /b !EXIT_CODE!
