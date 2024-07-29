@echo off 
title IML DEBUG CONFIGURATION PROGRAM

:menu
echo   /#######################################/
echo  /### IML DEBUG CONFIGURATION PROGRAM ###/
echo /#######################################/
echo.
echo Select the structure of your system
echo 1) x64     
echo 2) x86
echo.

set /p structure="Please select an option (1 or 2): "

if "%structure%"=="1" (
    set "data_structure=x64"
    set "file_name=x64.iml"
) else if "%structure%"=="2" (
    set "data_structure=x86"
    set "file_name=x86.iml"
) else (
    cls
    goto menu
)

echo Adding %data_structure% data structure to IML configuration file 
echo.
copy "%file_name%" "../face-detector.iml" >nul
echo %data_structure% data added to IML configuration file

:exit 
echo.
echo Leaving...
timeout /t 5 /nobreak >nul
exit
