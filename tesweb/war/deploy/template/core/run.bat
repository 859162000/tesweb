echo   "正在启动核心......"
start startcore.bat
ping /n 10 127.1 >nul

echo "正在启动发送适配器......"
cd ..
if exist Send* (
cd Send*
start startAdapters.bat
ping /n 2 127.1 >nul
cd ..
)

echo "正在启动接收适配器......"
if exist Receive* (
cd Receive*
start startAdapters.bat
)
exit