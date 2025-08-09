@echo off
setlocal enabledelayedexpansion

echo Removing all 'pause' commands from batch files...
echo =============================================

:: Create a temporary file
set "temp_file=%TEMP%\temp_remove_pause.txt"

:: Process each batch file
for /r "c:\Development\North Western Development\Minecraft\OpenComputers-1.20.1-port" %%f in (*.bat) do (
    echo Processing: %%~nxf
    
    :: Skip this script itself
    if /i not "%%~nxf"=="%~nx0" (
        :: Create a temporary copy without pause commands
        findstr /v /i /c:"pause" "%%f" > "%temp_file%"
        
        :: Replace the original file if it's different
        fc "%%f" "%temp_file%" >nul
        if !errorlevel! equ 1 (
            echo   - Updated: %%~nxf
            copy /y "%temp_file%" "%%f" >nul
        ) else (
            echo   - No changes needed
        )
    )
)

:: Clean up
del "%temp_file%" 2>nul

echo.
echo =============================================
echo All batch files have been processed.
echo =============================================

exit /b 0
