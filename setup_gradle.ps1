# Set Java 17 home
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH

Write-Host "Java version:"
java -version
Write-Host "`nJAVA_HOME is set to $env:JAVA_HOME`n"

# Create gradle/wrapper directory if it doesn't exist
if (-not (Test-Path "gradle\wrapper")) {
    New-Item -ItemType Directory -Path "gradle\wrapper" -Force | Out-Null
}

# Create gradle-wrapper.properties
@"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@ | Out-File -FilePath "gradle\wrapper\gradle-wrapper.properties" -Encoding ASCII

# Download gradle-wrapper.jar
$webClient = New-Object System.Net.WebClient
$webClient.DownloadFile("https://github.com/gradle/gradle/raw/v7.5.1/gradle/wrapper/gradle-wrapper.jar", "gradle\wrapper\gradle-wrapper.jar")

# Create a simple gradlew.bat
@"
@echo off
setlocal
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%%JAVA_HOME%%\bin;%%PATH%%

if exist "%JAVA_HOME%\bin\java.exe" (
    "%JAVA_HOME%\bin\java.exe" -Dorg.gradle.appname=gradlew -classpath "%~dp0gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
) else (
    echo Error: Could not find Java at %JAVA_HOME%
    exit /b 1
)
"@ | Out-File -FilePath "gradlew.bat" -Encoding ASCII

# Create a simple gradlew (Unix script)
@"
#!/bin/sh
JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.15.6-hotspot"
PATH="$JAVA_HOME/bin:$PATH"

exec "$JAVA_HOME/bin/java" -Dorg.gradle.appname=gradlew -classpath "`dirname \$0`/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
"@ | Out-File -FilePath "gradlew" -Encoding ASCII

# Make gradlew executable on Unix-like systems
if ($IsLinux -or $IsMacOS) {
    chmod +x ./gradlew
}

# Verify the wrapper
Write-Host "`nVerifying Gradle wrapper..."
.\gradlew --version

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nGradle wrapper is working. Running setupDecompWorkspace..."
    .\gradlew setupDecompWorkspace --stacktrace --info
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`nSetup completed successfully! You can now run: .\gradlew build"
    } else {
        Write-Host "`nError during setupDecompWorkspace. Please check the output above for errors."
    }
} else {
    Write-Host "`nError: Failed to verify Gradle wrapper. Please check the output above for errors."
}

Write-Host "`nPress any key to continue..."
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
