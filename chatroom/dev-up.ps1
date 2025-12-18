param(
    [string]$envFile = ".env",
    [switch]$UseDocker
)

function Test-JavaVersionIs21 {
    param([string]$javaCmd = "java")
    try {
        $out = & $javaCmd -version 2>&1
        if ($out -match 'version "?21') { return $true }
    } catch {
        return $false
    }
    return $false
}

function Find-Jdk21 {
    $patterns = @(
        "C:\\Program Files\\Microsoft\\jdk-21*",
        "C:\\Program Files\\Java\\jdk-21*",
        "D:\\Program Files\\Microsoft\\jdk-21*",
        "D:\\Program Files\\Java\\jdk-21*",
        "C:\\Program Files (x86)\\Java\\jdk-21*"
    )
    foreach ($pattern in $patterns) {
        $dirs = Get-ChildItem -Path $pattern -Directory -ErrorAction SilentlyContinue
        if ($dirs -and $dirs.Count -gt 0) { return $dirs[0].FullName }
    }
    return $null
}

# Load env file if exists
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match "^\s*([^#=]+)=(.*)$") {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim('"')
            Write-Host "Setting environment: $name"
            Set-Item -Path "Env:$name" -Value $value
        }
    }
}

if ($UseDocker) {
    Write-Host "Starting development environment using docker-compose.dev.yml (Docker mode)..."
    docker-compose -f docker-compose.dev.yml up --build
    exit $LASTEXITCODE
}

Write-Host "Starting development environment locally (no Docker)."

# Ensure JAVA_HOME and JDK version - try to ensure JDK21 for this session
$javaOk = $false
if ($env:JAVA_HOME) {
    $javaCmd = Join-Path $env:JAVA_HOME 'bin\java.exe'
    if (Test-Path $javaCmd) { $javaOk = Test-JavaVersionIs21 -javaCmd $javaCmd }
}

if (-not $javaOk) {
    Write-Host "Current JAVA_HOME is not Java 21 or not set. Searching common JDK21 install locations..." -ForegroundColor Yellow
    $found = Find-Jdk21
    if ($found) {
        Write-Host "Found JDK21 at: $found" -ForegroundColor Green
        $env:JAVA_HOME = $found
        $env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
        $javaOk = Test-JavaVersionIs21 -javaCmd (Join-Path $env:JAVA_HOME 'bin\java.exe')
    }
}

if (-not $javaOk) {
    Write-Host "ERROR: Java 21 not found. Please install JDK 21 or run build-with-jdk21.ps1 to help set it up." -ForegroundColor Red
    exit 1
} else {
    Write-Host "Using Java at: $env:JAVA_HOME"
    & "$env:JAVA_HOME\bin\java.exe" -version 2>&1 | ForEach-Object { Write-Host $_ }
}

# Build the project
Write-Host "Building project with Maven (skip tests)..."
& mvn -DskipTests clean package
$mvnExit = $LASTEXITCODE
if ($mvnExit -ne 0) {
    Write-Host "Maven build failed with exit code $mvnExit" -ForegroundColor Red
    exit $mvnExit
}

# Run the application (jar)
$jar = Get-ChildItem -Path target -Filter "*-1.0.0.jar" | Select-Object -First 1
if ($null -eq $jar) {
    Write-Host "Jar not found in target/. Did build succeed?" -ForegroundColor Red
    exit 1
}

Write-Host "Running application: $($jar.FullName)"
Start-Process -FilePath "java" -ArgumentList "-jar", $jar.FullName -WorkingDirectory (Get-Location)
Write-Host "Application started (background). Check logs in the console output or use 'Get-Process java' to find the PID."
