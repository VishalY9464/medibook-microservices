@echo off
echo ========================================
echo   Restarting Auth Service only...
echo ========================================

:: Kill only the process running on port 8081 (auth-service port)
echo Stopping auth-service on port 8081...
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8081" ^| find "LISTENING"') do (
    taskkill /PID %%a /F >nul 2>&1
)

timeout /t 2 /nobreak >nul

:: Go to auth-service directory and start it
:: CHANGE THIS PATH to wherever your auth-service folder is
echo Starting auth-service...
start "auth-service" cmd /k "cd /d "D:\Desktop\medibook-microservices\auth-service" && mvn spring-boot:run"

echo ========================================
echo   Auth Service restarting...
echo   Wait for "Started" message in the
echo   new window before testing.
echo ========================================