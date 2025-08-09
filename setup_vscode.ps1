# Create .vscode directory if it doesn't exist
$vscodeDir = ".vscode"
if (-not (Test-Path -Path $vscodeDir)) {
    New-Item -ItemType Directory -Path $vscodeDir | Out-Null
}

# Create launch.json
$launchJson = @"
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Minecraft Client",
            "request": "launch",
            "mainClass": "net.minecraft.launchwrapper.Launch",
            "projectName": "OpenComputers",
            "vmArgs": [
                "-Dfml.coreMods.load=li.cil.oc.common.launch.TransformerLoader",
                "-Dmixin.env.remapRefMap=true",
                "-Dmixin.env.refMapRemappingFile=./build/createSrgToMcp/output.srg",
                "-Dforge.logging.console.level=debug",
                "-Dforge.logging.markers=REGISTRIES,REGISTRYDUMP"
            ],
            "args": [
                "--tweakClass", "org.spongepowered.asm.launch.MixinTweaker",
                "--mixin", "mixins.opencomputers.json",
                "--username", "Dev",
                "--version", "1.20.1",
                "--gameDir", "${workspaceFolder}",
                "--assetsDir", "${workspaceFolder}/run/assets",
                "--assetIndex", "1.20.1",
                "--uuid", "00000000-0000-0000-0000-000000000000",
                "--accessToken", "0",
                "--userType", "mojang",
                "--tweakClass", "net.minecraftforge.fml.common.launcher.FMLTweaker",
                "--tweakClass", "net.minecraftforge.gradle.tweakers.CoremodTweaker"
            ],
            "cwd": "${workspaceFolder}/run",
            "console": "integratedTerminal",
            "stopOnEntry": false
        },
        {
            "type": "java",
            "name": "Minecraft Server",
            "request": "launch",
            "mainClass": "net.minecraft.server.Main",
            "projectName": "OpenComputers",
            "vmArgs": [
                "-Dfml.coreMods.load=li.cil.oc.common.launch.TransformerLoader",
                "-Dmixin.env.remapRefMap=true",
                "-Dmixin.env.refMapRemappingFile=./build/createSrgToMcp/output.srg",
                "-Dforge.logging.console.level=debug"
            ],
            "args": [
                "nogui"
            ],
            "cwd": "${workspaceFolder}/run",
            "console": "integratedTerminal",
            "stopOnEntry": false
        }
    ]
}
"@

# Create settings.json
$settingsJson = @"
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.jdt.ls.vmargs": "-XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -Dsun.zip.disableMemoryMapping=true -Xmx4G -Xms100m -javaagent:\\"${workspaceFolder}/.vscode/lombok.jar\\"",
    "java.completion.importOrder": [
        "java",
        "javax",
        "com",
        "org",
        "net",
        "li"
    ],
    "java.format.settings.url": "${workspaceFolder}/.vscode/eclipse-formatter.xml",
    "java.format.settings.profile": "GoogleStyle",
    "java.format.enabled": true,
    "java.autobuild.enabled": true,
    "java.import.gradle.home": "",
    "java.import.gradle.wrapper.enabled": true,
    "java.import.gradle.version": "",
    "java.import.gradle.arguments": "--refresh-dependencies",
    "java.import.gradle.autoImport": true,
    "java.import.gradle.nestedProjects": true,
    "java.test.config": [
        {
            "name": "OpenComputers",
            "workingDirectory": "${workspaceFolder}",
            "vmargs": [
                "-ea"
            ]
        }
    ],
    "java.test.defaultConfig": "OpenComputers",
    "maven.terminal.customEnv": [
        {
            "environmentVariable": "JAVA_HOME",
            "value": "C:\\Program Files\\Java\\jdk-17"
        }
    ]
}
"@

# Create tasks.json
$tasksJson = @"
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Run Client",
            "type": "shell",
            "command": "./gradlew runClient",
            "group": {
                "kind": "build",
                "isDefault": true
            },
            "problemMatcher": []
        },
        {
            "label": "Run Server",
            "type": "shell",
            "command": "./gradlew runServer",
            "group": "build",
            "problemMatcher": []
        },
        {
            "label": "Build",
            "type": "shell",
            "command": "./gradlew build",
            "group": "build",
            "problemMatcher": []
        },
        {
            "label": "Clean",
            "type": "shell",
            "command": "./gradlew clean",
            "group": "build",
            "problemMatcher": []
        },
        {
            "label": "Refresh Dependencies",
            "type": "shell",
            "command": "./gradlew --refresh-dependencies",
            "group": "build",
            "problemMatcher": []
        }
    ]
}
"@

# Write files
Set-Content -Path "$vscodeDir/launch.json" -Value $launchJson
Set-Content -Path "$vscodeDir/settings.json" -Value $settingsJson
Set-Content -Path "$vscodeDir/tasks.json" -Value $tasksJson

Write-Host "VS Code configuration has been set up successfully!"
Write-Host "1. Open the project in VS Code"
Write-Host "2. Install the following extensions if not already installed:"
Write-Host "   - Extension Pack for Java"
Write-Host "   - Gradle for Java"
Write-Host "   - Lombok Annotations Support for VS Code"
Write-Host "3. Reload VS Code window after installing extensions"
Write-Host "4. Use the Run and Debug view to start the Minecraft client or server"
