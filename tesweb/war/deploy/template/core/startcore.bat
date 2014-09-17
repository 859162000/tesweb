@echo off

set cp=.
setlocal enabledelayedexpansion
for /f "delims=" %%i in ('dir /b /a-d lib\*.jar') do set cp=!cp!;lib\%%i

java -cp %cp% com.dc.tes.fcore.FCore

pause