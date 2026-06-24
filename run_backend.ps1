# Set up JAVA_HOME and PATH locally using tools folder
$cwd = Resolve-Path "."
$jdkPath = Join-Path $cwd ".tools\jdk-17"
$mvnPath = Join-Path $cwd ".tools\maven"

if (-not (Test-Path $jdkPath)) {
    Write-Error "JDK 17 not found in .tools/jdk-17. Please run .\setup_env.ps1 first."
    exit 1
}

if (-not (Test-Path $mvnPath)) {
    Write-Error "Maven not found in .tools/maven. Please run .\setup_env.ps1 first."
    exit 1
}

# Set environment variables for this process only
$env:JAVA_HOME = Resolve-Path $jdkPath
$env:PATH = "$(Resolve-Path $jdkPath)\bin;" + $env:PATH
$env:PATH = "$(Resolve-Path $mvnPath)\bin;" + $env:PATH

Write-Host "Starting Ransomware Detection and Prevention System Backend..."
Write-Host "Java version: " -NoNewline
java -version
Write-Host "Maven version: " -NoNewline
mvn -v

# Run the backend
Set-Location -Path "backend"
mvn spring-boot:run
