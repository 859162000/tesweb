#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <sys/errno.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <sys/signal.h>
#include <sys/wait.h>

char	Msg[128];			/*处理过程中错误信息*/
char	Ver[3]; /*版本号*/

#define	MAXDATASIZE	65536
#define	MAXFILENAMELEN	128	/*log文件名最大长度*/

/*---------------------------全局变量------------------------------------------------------------------- */



int	CurPid;			/*当前进程的PID号*/
int	Count;			/*当前进程处理交易计数*/
int	DealNO;			/*模拟进程顺序号*/		

//int	DataSize;					/*报文数据大小*/
long	DataSize;					/*报文数据大小*/
int	XmlDataSize;			/*应用报文(明文)大小*/

char	Data[MAXDATASIZE];	/*报文数据接口缓冲区*/
char	XmlData[MAXDATASIZE];	/*应用报文体Xml（明文）*/

/*日志文件名变量定义*/
#define	MAXFILENAMELEN	128
char	EtcFile[MAXFILENAMELEN+1];	/*配置文件名称*/


/* ^..^---------------------共享内存数据格式定义区---------------------- ^..^ */
#define MAXPROCS	200
typedef struct  procslist
{
	long	pid;		/*进程号*/
	char	cmd;		/*命令,0--已执行;  1--刷新参数;2--停止*/
	char	status;		/*状态,0--正常运行;1--正常停止;2--异常停止*/
} PROCSLIST;
	
typedef struct  shmcontent
{
	char		cmd;		/*命令,0--已执行;  1--刷新参数;2--停止*/
	int		cpc;		/*当前进程数*/
	int		mpc;		/*最大进程数*/
	PROCSLIST	cps[MAXPROCS];	/*各子进程状态*/
} SHMCONTENT;
/* ^..^---------------------共享内存数据格式定义区---------------------- ^..^ */


/*-------------配置文件全局变量----------------------------*/
int	DealCount;			/*当前启动子进程数量*/
int	ShmQryStep;			/*检查共享内存的时间间隔(单位:秒)*/
int	ShmKey;				/*共享内存Key*/
int	RunLevel;			/*系统运行级别 0-正常,1-调试*/
int	MsgType;			/*报文类型     0-Xml ,1-Bin */
char sysid[32+1];		/*系统ID*/
char taskid[32+1];	/*任务ID*/
//unsigned char	APP_ID[5];	/*MAC应用系统代码--4位*/
char	APP_ID[5];	/*MAC应用系统代码--4位*/
//unsigned char	PKG_APP_ID[9];	/*加密发送方接收方系统代码-前4位发送方 后4位接收方*/
char	PKG_APP_ID[9];	/*加密发送方接收方系统代码-前4位发送方 后4位接收方*/
char    ChannelName[256];
char    SimType[10];
char    TesAddr[256];
char    AdapterAddr[256];
char    AdapterConfig[256];

char	dealno[20];					/*交易码*/
char	state[10];					/*状态*/

/*时间戳参数*/
struct timeval Tpbegin,Tpend;
char	dtime[20];
long	delaytime;

/*声明API空间指针*/
char *sendbuf,*rcv;  
char *RegBuf;
char *ConfigBuf;
/*声明API参数长度*/
int len_slog;

int InitGlobalVal();
int	ApiDeal();
int TimeDeal();

void gettimestr(char *timestr);
void gettimestr_res(char *timestr);
int	TrimRight(char *pstr);
int	TrimLeft(char *pstr);
int	TrimAll(char *pstr);

int StartChildProc(SHMCONTENT *pshmc);
int	ShutDownNow(SHMCONTENT *pshmc);
int	InitGlobalNow(SHMCONTENT *pshmc);
int	ChildMustStart(SHMCONTENT *pshmc);
int	ShutDownOK(SHMCONTENT *pshmc);
int	ShowShmContent(SHMCONTENT *pshmc);
int	CheckDeadProc(SHMCONTENT *pshmc);
int	CleanShmContent(SHMCONTENT *pshmc,int pdead);
int	CheckShmCmd();
int	ShowShmContentScreen(SHMCONTENT *pshmc);

int LoadSecLib();
int UnloadSecLib();

char DynamicIn[20];
char DynamicName[256];
char DynamicOut[20];
