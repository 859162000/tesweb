#ifndef _UTIL_H_
#define _UTIL_H_
#define MAXBUFLEN 128

#define LOG_DEBUG	3
#define LOG_INFO	2
#define LOG_ERROR	1
#define LOG_FATAL	0

char* ReadIni(char* fname,char* section,char* tag);
int strip(char * str);
void AscTraceMsg(int iSysCls,int iCurCls, char *pcFileName, char *pcMessage, ...);
int fillstrR(char *str,char ch,int iMaxLen);
int DeamonStart();
void GetCurrentDateTime(char *strDateTime);
void GetCurrentDateTime2(char *strDateTime);
void GetCurrentDateTime3(char *strDateTime);
void HexLog(char *sLogFile,char *sRecvBuf,int iRecvLen);
int atox(char *str);
int WriteIni(char *FileName,char *Section,char *tag,char *strIni);
int profPutEntry(FILE *fpProf,FILE *fpTmp,char *pszEntry,char *pszVal);
void HexLogStr(char *sRecvBuf,int iRecvLen,char *logstr);
int TesLog(char *LogFileName,int LogLevel ,int RunLevel ,char *pdesc,...);
#endif
