@echo off
setlocal EnableExtensions
cd /d "%~dp0"
set "ROOT=%~dp0"

echo ============================================================
echo   AI Support Ticket Classification System
echo ============================================================
echo.

where python >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Python was not found in PATH.
    echo Install Python 3.11+ and make sure "python" works in Command Prompt.
    pause
    exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java was not found in PATH.
    echo Install Java 17+ and make sure "java -version" works.
    pause
    exit /b 1
)

if not exist "%ROOT%python-ml-service\.venv\Scripts\python.exe" (
    echo [1/4] Creating Python virtual environment...
    python -m venv "%ROOT%python-ml-service\.venv"
    if errorlevel 1 goto :fail
)

echo [2/4] Installing/verifying Python dependencies...
"%ROOT%python-ml-service\.venv\Scripts\python.exe" -m pip install -r "%ROOT%python-ml-service\requirements.txt"
if errorlevel 1 (
    echo.
    echo [ERROR] Python dependencies could not be installed.
    echo Check your internet connection and try again.
    pause
    exit /b 1
)

set "MVN_CMD="
set "MAVEN_VERSION=3.9.11"
set "MAVEN_HOME=%ROOT%.tools\apache-maven-%MAVEN_VERSION%"
where mvn >nul 2>nul
if not errorlevel 1 set "MVN_CMD=mvn"

if not defined MVN_CMD (
    if not exist "%ROOT%.tools\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd" (
        echo [3/4] Maven not found. Downloading Maven %MAVEN_VERSION%...
        if not exist "%ROOT%.tools" mkdir "%ROOT%.tools"
        powershell -NoProfile -ExecutionPolicy Bypass -Command "$u='https://archive.apache.org/dist/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.zip'; $z='%ROOT%.tools\maven.zip'; Invoke-WebRequest -UseBasicParsing -Uri $u -OutFile $z; Expand-Archive -Path $z -DestinationPath '%ROOT%.tools' -Force; Remove-Item $z -Force"
        if errorlevel 1 (
            echo [ERROR] Maven download failed. Install Maven 3.9+ and rerun this file.
            pause
            exit /b 1
        )
    )
    set "MVN_CMD=%ROOT%.tools\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd"
) else (
    echo [3/4] Maven found.
)

echo [4/4] Starting Python ML service and Spring Boot...
start "AI Ticket Classifier - Python ML" /D "%ROOT%python-ml-service" cmd /k ".venv\Scripts\python.exe app.py"

timeout /t 3 /nobreak >nul
start "AI Ticket Classifier - Spring Boot" /D "%ROOT%spring-api" cmd /k ""%ROOT%scripts\RUN_SPRING.bat""

echo.
echo Waiting for Spring Boot to start. The first build may take a few minutes.
timeout /t 12 /nobreak >nul
start "" "http://localhost:8080"
echo.
echo ============================================================
echo   Project started.
echo   Browser: http://localhost:8080
echo   Python ML: http://127.0.0.1:5000/health
echo ============================================================
echo.
echo Keep both opened terminal windows running while using the project.
pause
exit /b 0

:fail
echo [ERROR] Project startup failed.
pause
exit /b 1
