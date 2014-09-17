@echo off

set cp=.;
setlocal enabledelayedexpansion
for /f "delims=" %%i in ('dir /b /a-d *.jar') do set cp=!cp!%%i;

java "-DchannelName=TCPADAPTER" "-DtesAddr=//192.168.99.100:9999" "-DadapterConfig=./config.txt" -Dlog4j.configuration=file:log4j.properties -classpath %cp% com.dc.tes.gta.GeneralTcpAdapter

pause