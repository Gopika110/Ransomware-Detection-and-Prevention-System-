@echo off
set "JAVA_HOME=%~dp0.tools\jdk-17"
set "PATH=%~dp0.tools\jdk-17\bin;%~dp0.tools\maven\bin;%PATH%"
echo Java Home: %JAVA_HOME%
echo Starting Spring Boot backend...
cd backend
call mvn spring-boot:run
