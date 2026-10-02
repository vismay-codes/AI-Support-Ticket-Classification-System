@echo off
setlocal
cd /d "%~dp0.."
set "ROOT=%CD%"
set "MAVEN_VERSION=3.9.11"
set "MVN_CMD=%ROOT%\.tools\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd"
where mvn >nul 2>nul
if not errorlevel 1 set "MVN_CMD=mvn"
cd /d "%ROOT%\spring-api"
call "%MVN_CMD%" spring-boot:run
pause
