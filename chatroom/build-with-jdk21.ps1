# build-with-jdk21.ps1
# Usage: run this script in PowerShell to build the project using a JDK 21 installation.
# It will try common JDK21 install locations and ask you to input a path if none found.

param(
    [string]$Jdk21Path
)

function Find-Jdk21 {
    $candidates = @(
        "C:\\Program Files\\Microsoft\\jdk-21*",
        "C:\\Program Files\\Java\\jdk-21*",
        "C:\\Program Files (x86)\\Java\\jdk-21*",
        "D:\\Program Files\\Microsoft\\jdk-21*",
        "D:\\Program Files\\Java\\jdk-21*"
    )
    foreach ($pattern in $candidates) {
        $dirs = Get-ChildItem -Path $pattern -Directory -ErrorAction SilentlyContinue
        if ($dirs) { return $dirs[0].FullName }
    }
    return $null
}

if (-not $Jdk21Path) {
    $found = Find-Jdk21
    if ($found) { $Jdk21Path = $found }
}

if (-not $Jdk21Path) {
    Write-Host "JDK 21 not found automatically. Please enter the full path to your JDK 21 (e.g. C:\\Program Files\\Java\\jdk-21.0.0):" -ForegroundColor Yellow
    $Jdk21Path = Read-Host "JDK21 path"
}

if (-not (Test-Path "$Jdk21Path\bin\java.exe")) {
    Write-Error "No java.exe found under $Jdk21Path. Aborting."
    exit 1
}

Write-Host "Using JDK at: $Jdk21Path" -ForegroundColor Green

# Temporarily set JAVA_HOME and PATH for this session
$oldJavaHome = $env:JAVA_HOME
$oldPath = $env:PATH
$env:JAVA_HOME = $Jdk21Path
$env:PATH = "$Jdk21Path\bin;" + $env:PATH

try {
    mvn -DskipTests clean package
    $exit = $LASTEXITCODE
    if ($exit -eq 0) { Write-Host "Build succeeded." -ForegroundColor Green } else { Write-Host "Build failed with exit code $exit." -ForegroundColor Red }
    exit $exit
} finally {
    # restore
    $env:JAVA_HOME = $oldJavaHome
    $env:PATH = $oldPath
}

