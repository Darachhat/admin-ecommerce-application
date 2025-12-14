@echo off
echo ========================================
echo   E-Commerce Admin App
echo   Opening in Android Studio
echo ========================================
echo.

REM Try common Android Studio installation paths
set STUDIO_PATH=""

if exist "C:\Program Files\Android\Android Studio\bin\studio64.exe" (
    set STUDIO_PATH="C:\Program Files\Android\Android Studio\bin\studio64.exe"
) else if exist "%LOCALAPPDATA%\Programs\Android Studio\bin\studio64.exe" (
    set STUDIO_PATH="%LOCALAPPDATA%\Programs\Android Studio\bin\studio64.exe"
) else (
    echo Android Studio not found in common locations.
    echo Please open Android Studio manually and select this folder.
    pause
    exit
)

echo Starting Android Studio...
start "" %STUDIO_PATH% "%~dp0"

echo.
echo Android Studio is starting...
echo This window will close in 3 seconds.
timeout /t 3
