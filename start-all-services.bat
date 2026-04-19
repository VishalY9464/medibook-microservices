@echo off
title MediBook Microservices Manager
color 0A

echo ========================================
echo   STOPPING ALL RUNNING SERVICES...
echo ========================================

REM Kill all Java processes
taskkill /F /IM java.exe /T 2>nul
echo [OK] Killed Java processes.

REM Kill by specific ports (just to be safe)
echo Freeing ports...
for %%P in (8761 8080 8081 8082 8083 8084 8085 8086 8087 8088) do (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":%%P " 2^>nul') do (
        taskkill /F /PID %%i 2>nul
    )
)

echo Waiting for ports to fully release...
timeout /t 8 /nobreak >nul

echo.
echo ========================================
echo   STARTING ALL SERVICES...
echo ========================================

echo [1/10] Starting Eureka Server...
cd /d "D:\Desktop\medibook-microservices\eureka-server"
start "Eureka Server" cmd /k "mvn spring-boot:run"
timeout /t 25 /nobreak >nul

echo [2/10] Starting API Gateway...
cd /d "D:\Desktop\medibook-microservices\api-gateway"
start "API Gateway" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/10] Starting Auth Service...
cd /d "D:\Desktop\medibook-microservices\auth-service"
start "Auth Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [4/10] Starting Provider Service...
cd /d "D:\Desktop\medibook-microservices\provider-service"
start "Provider Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [5/10] Starting Appointment Service...
cd /d "D:\Desktop\medibook-microservices\appointment-service"
start "Appointment Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [6/10] Starting Slot Service...
cd /d "D:\Desktop\medibook-microservices\slot-service"
start "Slot Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [7/10] Starting Payment Service...
cd /d "D:\Desktop\medibook-microservices\payment-service"
start "Payment Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [8/10] Starting Notification Service...
cd /d "D:\Desktop\medibook-microservices\notification-service"
start "Notification Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [9/10] Starting Record Service...
cd /d "D:\Desktop\medibook-microservices\record-service"
start "Record Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [10/10] Starting Review Service...
cd /d "D:\Desktop\medibook-microservices\review-service"
start "Review Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo   ALL SERVICES STARTED!
echo ========================================
echo  Eureka Dashboard : http://localhost:8761
echo  API Gateway      : http://localhost:8080
echo.
pause