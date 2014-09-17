#ifndef _CLTTUX_H_
#define _CLTTUX_H_

#define TES_COMM_TIMEOUT 30

char cfgFile[256];
char sLogFile[256];
int iPort;
int iTransferPort;
char sTransferIP[60];

int	RunLevel;			/*系统运行级别 DEBUG(3) INF(2) ERROR(1) FATAL(0)*/
struct timeval Tpbegin,Tpend;

char    AgentTuxService[256];
char    WsnAddr[256];
char    CommType[20];
char    ChannelName[256];
char    SimType[10];
char    TesAddr[256];
char    AdapterAddr[256];
char    AdapterConfig[256];

char DynamicIn[20];
char DynamicName[256];
char DynamicOut[20];

int GetConfig();
int LoadSecLib();
int UnloadSecLib();
#endif
