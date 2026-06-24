# Create .tools folder if it doesn't exist
if (-not (Test-Path ".tools")) {
    New-Item -ItemType Directory -Force -Path ".tools" | Out-Null
}

$toolsPath = Resolve-Path ".tools"

# Download and Setup JDK 17
$jdkFolder = Join-Path $toolsPath "jdk-17"
if (-not (Test-Path $jdkFolder)) {
    Write-Host "--- Downloading OpenJDK 17 ---"
    $jdkZip = Join-Path $toolsPath "jdk.zip"
    curl.exe -L -o $jdkZip "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse"
    
    Write-Host "--- Extracting OpenJDK 17 ---"
    Expand-Archive -Path $jdkZip -DestinationPath $toolsPath
    
    # Locate the extracted directory (e.g. jdk-17.0.19+10) and rename to jdk-17
    $extracted = Get-ChildItem -Path $toolsPath -Directory | Where-Object { $_.Name -like "jdk-17*" }
    if ($extracted) {
        Rename-Item -Path $extracted.FullName -NewName "jdk-17"
    }
    
    # Clean up the zip file
    if (Test-Path $jdkZip) {
        Remove-Item -Path $jdkZip -Force
    }
    Write-Host "OpenJDK 17 setup complete!"
} else {
    Write-Host "OpenJDK 17 already exists in .tools/jdk-17"
}

# Download and Setup Maven 3.9.6
$mavenFolder = Join-Path $toolsPath "maven"
if (-not (Test-Path $mavenFolder)) {
    Write-Host "--- Downloading Apache Maven 3.9.6 ---"
    $mavenZip = Join-Path $toolsPath "maven.zip"
    curl.exe -L -o $mavenZip "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
    
    Write-Host "--- Extracting Apache Maven ---"
    Expand-Archive -Path $mavenZip -DestinationPath $toolsPath
    
    # Locate the extracted directory (e.g. apache-maven-3.9.6) and rename to maven
    $extracted = Get-ChildItem -Path $toolsPath -Directory | Where-Object { $_.Name -like "apache-maven*" }
    if ($extracted) {
        Rename-Item -Path $extracted.FullName -NewName "maven"
    }
    
    # Clean up the zip file
    if (Test-Path $mavenZip) {
        Remove-Item -Path $mavenZip -Force
    }
    Write-Host "Apache Maven setup complete!"
} else {
    Write-Host "Apache Maven already exists in .tools/maven"
}

# Create monitored_directory if it doesn't exist
if (-not (Test-Path "monitored_directory")) {
    New-Item -ItemType Directory -Force -Path "monitored_directory" | Out-Null
    # Put a dummy file in it to monitor
    Set-Content -Path "monitored_directory/welcome.txt" -Value "Welcome to the Ransomware Detection and Prevention System. This directory is monitored in real-time."
    Write-Host "Created 'monitored_directory' folder."
}

# Verification
$env:JAVA_HOME = Resolve-Path ".tools/jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
$mavenBin = Join-Path (Resolve-Path ".tools/maven") "bin"
$env:PATH = "$mavenBin;" + $env:PATH

Write-Host "`n--- Verification ---"
java -version
mvn -version
Write-Host "Environment Setup Successful!"
