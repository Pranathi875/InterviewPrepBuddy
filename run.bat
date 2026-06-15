@echo off

REM Load .env file if it exists
if exist .env (
    for /f "tokens=* delims=" %%a in ('type .env ^| findstr /v "^#"') do set %%a
)

echo Compiling...
mkdir out 2>nul
javac -cp "lib\*" -d out src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo.
echo Starting Interview Prep Buddy...
echo.
java -cp "out;lib\*" Main
pause
