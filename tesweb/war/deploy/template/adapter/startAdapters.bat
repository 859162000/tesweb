@echo off
title "ÊÊÅäÆ÷Æô¶¯"
set cp=.;
setlocal enabledelayedexpansion
for /f "delims=" %%i in ('dir /b /a-d *.jar') do set cp=!cp!%%i;

java -Dlog4j.configuration=file:log4j.properties  -classpath %cp% com.dc.tes.adapter.startup.StartUpEntry

pause