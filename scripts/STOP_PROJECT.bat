@echo off
echo Stopping project processes...
taskkill /FI "WINDOWTITLE eq AI Ticket Classifier - Python ML" /T /F >nul 2>nul
taskkill /FI "WINDOWTITLE eq AI Ticket Classifier - Spring Boot" /T /F >nul 2>nul
echo Done. If a process remains, close its terminal window manually.
pause
