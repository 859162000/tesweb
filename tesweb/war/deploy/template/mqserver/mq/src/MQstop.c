#include <string.h>
#include <unistd.h>
#include "sim.h"
#include "Util.h"
#if defined(__STDC__) || defined(__cplusplus)
main(int argc, char *argv[])
#else
main(argc, argv)
int argc;
char *argv[];
#endif
{
	int		shmid,semid,ret,i;
	unsigned char	*shmaddr;
	static struct sembuf    acquire={0,-1,SEM_UNDO};
	static struct sembuf    release={0, 1,SEM_UNDO};
	SHMCONTENT	*pshm;
	char sTmpBuf[1024];


	/*取系统日期*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "../log/MQ_INF.log.%s",sTmpBuf);
	
	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter stop ,pid=[%ld]。",getpid());

	/*初始化全局变量*/
	memset(EtcFile,'\0',sizeof(EtcFile));
	sprintf(EtcFile,"../etc/MQadapter.cfg");

	CurPid=getpid();

	ret=InitGlobalVal();
	if (ret < 0)
	{
		printf("Get Etc Information Error,Exit!\n");
		exit(-1);
	}

	semid=semget(ShmKey,1,0);
	if (semid <0 )
	{
		printf("Attach Semaphore Fail,ERRNO=[%d]...\n",errno);
		exit(-1);
	}

	if (semop(semid,&acquire,1) < 0 )
	{
		printf("Get Semaphore Fail,ERRNO=[%d]...\n",errno);
		exit(-1);
	}
	
	shmid=shmget(ShmKey,sizeof(SHMCONTENT),0777);
	if (shmid <0 )
	{
		printf("Get Share memory Fail,ERRNO=[%d]...\n",errno);
		if (semop(semid,&release,1) < 0 )
				printf("Free Semaphore Fail,ERRNO=[%d]...\n",errno);
				exit(-1);
	}

	shmaddr=(unsigned char *)shmat(shmid,0,0);
	if (shmaddr==NULL)
	{
		printf("Attack Share memory Fail,ERRNO=[%d]...\n",errno);
		if (semop(semid,&release,1) < 0 )
			printf("Free Semaphore Fail,ERRNO=[%d]...\n",errno);
		exit(-1);
	}
	pshm=(SHMCONTENT *)shmaddr;

	pshm->cmd='2';
	
	for ( i=0; i<pshm->cpc; i++) pshm->cps[i].cmd='2';
	
	shmdt(shmaddr);
	if (semop(semid,&release,1) < 0 ) 
		printf("Free Semaphore Fail,ERRNO=[%d]...\n",errno);
	
	exit(0);
}

