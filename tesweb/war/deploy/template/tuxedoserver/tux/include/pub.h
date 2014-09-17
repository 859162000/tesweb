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


extern char	Msg[128];			/*处理过程中错误信息*/
extern char	Ver[3]; /*版本号*/


/*应用报文，捎带报文，附件报文数据长度*/
#define	MAXDATASIZE	65536
#define	MAXFILENAMELEN	128	/*log文件名最大长度*/

/*---------------------------全局变量------------------------------------------------------------------- */



extern int	CurPid;			/*当前进程的PID号*/
extern int	Count;			/*当前进程处理交易计数*/
extern int	DealNO;			/*模拟进程顺序号*/		

//extern int	DataSize;					/*报文数据大小*/
extern long	DataSize;					/*报文数据大小*/
extern int	XhttpHeadSize;		/*类HTTP头报文系统头大小*/
extern int	XmlDataSize;			/*应用报文(明文)大小*/

extern char	Data[MAXDATASIZE];	/*报文数据接口缓冲区*/
extern char	XmlData[MAXDATASIZE];	/*应用报文体Xml（明文）*/

/*日志文件名变量定义*/
#define	MAXFILENAMELEN	128
extern char	EtcFile[MAXFILENAMELEN+1];	/*配置文件名称*/
extern char	ErrFile[MAXFILENAMELEN+1];	/*错误性日志文件名称*/
extern char	WrnFile[MAXFILENAMELEN+1];	/*警告性日志文件名称*/
extern char	InfFile[MAXFILENAMELEN+1];	/*调制性日志文件名称*/


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
extern int	DealCount;			/*当前启动子进程数量*/
extern int	ShmQryStep;			/*检查共享内存的时间间隔(单位:秒)*/
extern int	ShmKey;				/*共享内存Key*/
extern int	RunLevel;			/*系统运行级别 0-正常,1-调试*/
extern int	MsgType;			/*报文类型     0-Xml ,1-Bin */
extern char sysid[32+1];		/*系统ID*/
extern char taskid[32+1];	/*任务ID*/
//extern unsigned char	APP_ID[5];	/*MAC应用系统代码--4位*/
extern char	APP_ID[5];	/*MAC应用系统代码--4位*/
//extern unsigned char	PKG_APP_ID[9];	/*加密发送方接收方系统代码-前4位发送方 后4位接收方*/
extern char	PKG_APP_ID[9];	/*加密发送方接收方系统代码-前4位发送方 后4位接收方*/

extern char	dealno[20];					/*交易码*/
extern char	state[10];					/*状态*/

/*时间戳参数*/
extern struct timeval Tpbegin,Tpend;
extern char	dtime[20];
extern long	delaytime;

/*声明API空间指针*/
extern char *sendbuf,*rcv;  
/*声明API参数长度*/
extern int len_slog;


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
