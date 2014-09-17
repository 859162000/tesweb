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

#include <cmqc.h>
#include <sys/time.h>

#define	MAXXMLTAGLEN	32
#define	MAXXMLVALLEN	16
#define	TRACENOLEN	16
#define	REQUESTERIDLEN	4
#define	NODEIDLEN	9
#define	SECIDLEN	4
#define	MAXEAIBCOUNT	38

#define TRAN_CODE_LEN 14

char	Msg[128];			/*��������д�����Ϣ*/
char	Ver[3]; /*�汾��*/


/*Ӧ�ñ��ģ��Ӵ����ģ������������ݳ���*/
#define	MAXDATASIZE	65536
#define	MAXFILENAMELEN	128	/*log�ļ�����󳤶�*/

/*---------------------------ȫ�ֱ���------------------------------------------------------------------- */



int	CurPid;			/*��ǰ���̵�PID��*/
int	Count;			/*��ǰ���̴����׼���*/
int	DealNO;			/*ģ�����˳���*/		

long	DataSize;					/*�������ݴ�С*/
int	XmlDataSize;			/*Ӧ�ñ���(����)��С*/
char	XmlData[MAXDATASIZE];
char	Data[MAXDATASIZE];

/*��־�ļ�����������*/
#define	MAXFILENAMELEN	128
char	EtcFile[MAXFILENAMELEN+1];	/*�����ļ�����*/
char	ErrFile[MAXFILENAMELEN+1];	/*��������־�ļ�����*/
char	WrnFile[MAXFILENAMELEN+1];	/*��������־�ļ�����*/
char	InfFile[MAXFILENAMELEN+1];	/*��������־�ļ�����*/


/* ^..^---------------------�����ڴ����ݸ�ʽ������---------------------- ^..^ */
#define MAXPROCS	200
typedef struct  procslist
{
	long	pid;		/*���̺�*/
	char	cmd;		/*����,0--��ִ��;  1--ˢ�²���;2--ֹͣ*/
	char	status;		/*״̬,0--��������;1--����ֹͣ;2--�쳣ֹͣ*/
} PROCSLIST;
	
typedef struct  shmcontent
{
	char		cmd;		/*����,0--��ִ��;  1--ˢ�²���;2--ֹͣ*/
	int		cpc;		/*��ǰ������*/
	int		mpc;		/*��������*/
	PROCSLIST	cps[MAXPROCS];	/*���ӽ���״̬*/
} SHMCONTENT;
/* ^..^---------------------�����ڴ����ݸ�ʽ������---------------------- ^..^ */


/*-------------�����ļ�ȫ�ֱ���----------------------------*/
#define	MAXMQNAMELEN	64			 
char	SimQM[MAXMQNAMELEN];		/*Queue Manage ����*/
char	SimReqQ[MAXMQNAMELEN];		/*MQ�����������*/
char	SimResQ[MAXMQNAMELEN];		/*MQ��Ӧ��������*/
int	MQFetchTimeOut;			/*ȡMQ��Ϣ��ʱʱ��(��λ:��)*/
int	DealCount;			/*��ǰ�����ӽ�������*/
int	ShmQryStep;			/*��鹲���ڴ��ʱ����(��λ:��)*/
int	ShmKey;				/*�����ڴ�Key*/
int	RunLevel;			/*ϵͳ���м��� 0-����,1-����*/
int	MsgType;			/*��������     0-Xml ,1-Bin */
char sysid[32+1];		/*ϵͳID*/
char taskid[32+1];	/*����ID*/
char	APP_ID[5];	/*MACӦ��ϵͳ����--4λ*/
char	PKG_APP_ID[9];	/*���ܷ��ͷ����շ�ϵͳ����-ǰ4λ���ͷ� ��4λ���շ�*/
char	ChannelName[256];
char	SimType[10];
char	TesAddr[256];
char	AdapterAddr[256];
char	AdapterConfig[256];

char	dealno[20];					/*������*/
char	state[10];					/*״̬*/

/*ʱ�������*/
struct timeval Tpbegin,Tpend;
char	dtime[20];
long	delaytime;

/*����API�ռ�ָ��*/
/*char *sendbuf,*rcv,*log;  */
char *sendbuf,*rcv;  
char *RegBuf;
char *ConfigBuf;  
/*����API��������*/
int len_slog;

/*��ȫ����*/
char	MacType[16];


int InitGlobalVal();
int	ApiDeal();
int TimeDeal();

void gettimestr(char *timestr);
void gettimestr_res(char *timestr);
int	GetConfig(FILE *pfp,char *pitem,char *pval);
int	GetConfigByItem(char *pline,char *pitem,char *pval);
int	TrimRight(char *pstr);
int	TrimLeft(char *pstr);
int	TrimAll(char *pstr);
int	ErrLog(char *pdesc,...);
int	WrnLog(char *pdesc,...);
int	InfLog(char *pdesc,...);

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
int	ConnectQueue(MQHCONN *pqm,MQHOBJ *pinq,MQHOBJ *poutq);
int	DisConnectQueue(MQHCONN *pqm,MQHOBJ *pinq,MQHOBJ *poutq);
int	RcvMsgFromMQ(MQHCONN *pqm,MQHOBJ *pinq,MQMD *pmdg);
int	SndMsgToMQ(MQHCONN *pqm,MQHOBJ *poutq,MQMD *pmdp);
int LoadSecLib();
int UnloadSecLib();

char DynamicIn[20];
char DynamicName[256];
char DynamicOut[20];

char sLogFile[256];
