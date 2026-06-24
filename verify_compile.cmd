@echo off
set "JAVA_HOME=%~dp0.tools\jdk-17"
set "PATH=%~dp0.tools\jdk-17\bin;%~dp0.tools\maven\bin;%PATH%"
echo Java Home: %JAVA_HOME%
echo Path: %PATH%
echo Running MVN Compile...
cd backend
call mvn clean compile
if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)
echo Compilation successful!
