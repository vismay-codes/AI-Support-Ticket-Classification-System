@echo off
cd /d "%~dp0.."
if not exist "python-ml-service\.venv\Scripts\python.exe" (
    python -m venv "python-ml-service\.venv"
)
"python-ml-service\.venv\Scripts\python.exe" -m pip install -r "python-ml-service\requirements.txt"
cd /d "python-ml-service"
.venv\Scripts\python.exe app.py
