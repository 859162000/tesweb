echo   "������������......"
start startcore.bat
ping /n 10 127.1 >nul

echo "������������������......"
cd ..
if exist Send* (
cd Send*
start startAdapters.bat
ping /n 2 127.1 >nul
cd ..
)

echo "������������������......"
if exist Receive* (
cd Receive*
start startAdapters.bat
)
exit