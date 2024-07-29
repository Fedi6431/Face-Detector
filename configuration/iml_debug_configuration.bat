@echo off 
:menu
title IML DEBUG CONFIGURATION PROGRAM

echo   /#######################################/
echo  /### IML DEBUG CONFIGURATION PROGRAM ###/
echo /#######################################/
echo.
echo Select the structure of your system
echo 1) x64     
echo 2) x86
echo.

set /p structure="Please select an option (1 or 2): "

:selection
if "%structure%"=="1" goto x64
if "%structure%"=="2" goto x86
echo Invalid selection. Please try again.
cls
goto menu

:x64
echo Adding x64 data structure to IML configuration file 
echo.
copy "x64.iml" "../face-detector.iml"
echo x64 data added to IML configuration file
goto exit

:x86
echo Adding x86 data structure to IML configuration file 
echo.
copy "x86.iml" "../face-detector.iml"
echo x86 data added to IML configuration file
goto exit

:exit 
echo.
echo Leaving...
timeout /t 5 /nobreak >nul
exit
