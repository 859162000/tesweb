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
	int		shmid,semid,ret;
	unsigned char	*shmaddr;
	static struct sembuf    acquire={0,-1,SEM_UNDO};
	static struct sembuf    release={0, 1,SEM_UNDO};
	SHMCONTENT	*pshm;
	char sTmpBuf[1024];

        /*初始化全局变量*/
	memset(EtcFile,'\0',sizeof(EtcFile));
	/*取系统日期*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "../log/MQ_INF.log.%s",sTmpBuf);
	
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

	while(1)
	{
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

		ret=ShowShmContentScreen(pshm);

		shmdt(shmaddr);
		if (semop(semid,&release,1) < 0 ) 
			printf("Free Semaphore Fail,ERRNO=[%d]...\n",errno);

		if (ret=0)
		{
			printf("All Process Exit!\n"); break;
		}
		sleep(ShmQryStep);
	}
	
	exit(0);
}

int	ShowShmContentScreen(SHMCONTENT *pshmc)
{
	int	i,procs;
	char    *cmddesc []={"已执行","刷新参数","停止"};
	char    *statusdesc []={"正常运行","正常停止","异常停止"};

	procs=pshmc->cpc;

	printf("-------------Parse/Rebuild SHM Content-------------\n");
	printf("System Command Code :  %s\n", cmddesc[pshmc->cmd-48]);
	printf("Running Process Count: %d\n", pshmc->cpc);
	printf("Process Maximum Count: %d\n", pshmc->mpc);
	printf("-----------------------------------------------------\n");
	printf("  PID		CMD	\tSTATUS	\tTYPE\n");
	printf("-----------------------------------------------------\n");
	for (i=0;i<procs;i++)
	{	
		printf("[%010d]\t[%s]\t[%s]\t[%s]\n", pshmc->cps[i].pid
			, cmddesc[pshmc->cps[i].cmd-48]
			, statusdesc[pshmc->cps[i].status-48]
			, "模拟进程");
	}
	printf("-----------------------------------------------------\n");

	return(procs);
}
