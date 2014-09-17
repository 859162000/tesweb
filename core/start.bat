@echo off
setlocal enabledelayedexpansion

set cp=.
for /f "delims=" %%i in ('dir /b /s /a-d lib\*.jar') do set cp=!cp!;lib\%%i

java -cp %cp% com.dc.tes.fcore.FCore