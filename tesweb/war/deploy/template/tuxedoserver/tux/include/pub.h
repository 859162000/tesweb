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


extern char	Msg[128];			/*��������д�����Ϣ*/
extern char	Ver[3]; /*�汾��*/


/*Ӧ�ñ��ģ��Ӵ����ģ������������ݳ���*/
#define	MAXDATASIZE	65536
#define	MAXFILENAMELEN	128	/*log�ļ�����󳤶�*/

/*---------------------------ȫ�ֱ���------------------------------------------------------------------- */



extern int	CurPid;			/*��ǰ���̵�PID��*/
extern int	Count;			/*��ǰ���̴����׼���*/
extern int	DealNO;			/*ģ�����˳���*/		

//extern int	DataSize;					/*�������ݴ�С*/
extern long	DataSize;					/*�������ݴ�С*/
extern int	XhttpHeadSize;		/*��HTTPͷ����ϵͳͷ��С*/
extern int	XmlDataSize;			/*Ӧ�ñ���(����)��С*/

extern char	Data[MAXDATASIZE];	/*�������ݽӿڻ�����*/
extern char	XmlData[MAXDATASIZE];	/*Ӧ�ñ�����Xml�����ģ�*/

/*��־�ļ�����������*/
#define	MAXFILENAMELEN	128
extern char	EtcFile[MAXFILENAMELEN+1];	/*�����ļ�����*/
extern char	ErrFile[MAXFILENAMELEN+1];	/*��������־�ļ�����*/
extern char	WrnFile[MAXFILENAMELEN+1];	/*��������־�ļ�����*/
extern char	InfFile[MAXFILENAMELEN+1];	/*��������־�ļ�����*/


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
extern int	DealCount;			/*��ǰ�����ӽ�������*/
extern int	ShmQryStep;			/*��鹲���ڴ��ʱ����(��λ:��)*/
extern int	ShmKey;				/*�����ڴ�Key*/
extern int	RunLevel;			/*ϵͳ���м��� 0-����,1-����*/
extern int	MsgType;			/*��������     0-Xml ,1-Bin */
extern char sysid[32+1];		/*ϵͳID*/
extern char taskid[32+1];	/*����ID*/
//extern unsigned char	APP_ID[5];	/*MACӦ��ϵͳ����--4λ*/
extern char	APP_ID[5];	/*MACӦ��ϵͳ����--4λ*/
//extern unsigned char	PKG_APP_ID[9];	/*���ܷ��ͷ����շ�ϵͳ����-ǰ4λ���ͷ� ��4λ���շ�*/
extern char	PKG_APP_ID[9];	/*���ܷ��ͷ����շ�ϵͳ����-ǰ4λ���ͷ� ��4λ���շ�*/

extern char	dealno[20];					/*������*/
extern char	state[10];					/*״̬*/

/*ʱ�������*/
extern struct timeval Tpbegin,Tpend;
extern char	dtime[20];
extern long	delaytime;

/*����API�ռ�ָ��*/
extern char *sendbuf,*rcv;  
/*����API��������*/
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
