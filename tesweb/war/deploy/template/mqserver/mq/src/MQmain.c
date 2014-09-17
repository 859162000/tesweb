#include <string.h>
#include <unistd.h>
#include "sim.h"
#include "Adapter2Tes.h"
#include "Util.h"


#if defined(__STDC__) || defined(__cplusplus)
main(int argc, char *argv[])
#else
main(argc, argv)
int argc;
char *argv[];
#endif
{
	int		shmid,semid;
	unsigned char	*shmaddr;
	char sTmpBuf[65536];

	static struct sembuf	acquire={0,-1,SEM_UNDO};
	static struct sembuf	release={0, 1,SEM_UNDO};

	int		deadc;

	int		i,ret;
	SHMCONTENT	*pshm;
	FILE		*fp;

	/*信号屏蔽*/
	signal(SIGHUP,SIG_IGN);
	signal(SIGINT,SIG_IGN);
	signal(SIGTSTP,SIG_IGN);
	signal(SIGABRT,SIG_IGN);
	signal(SIGQUIT,SIG_IGN);

	/*文件名初始化*/
	/*取系统日期*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "../log/MQ_INF.log.%s",sTmpBuf);
	
	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter starting ,pid=[%ld]。",getpid());

	memset(EtcFile,'\0',sizeof(EtcFile));
	sprintf(EtcFile,"../etc/MQadapter.cfg");

	CurPid=getpid();
	DealNO=0;

	/*初始化全局变量*/
	ret=InitGlobalVal();
	if (ret)
	{
		printf("读取配置文件[%s]失败！\n",EtcFile);
		exit(-1);
	}

	/*注册*/
	RegBuf=messageInit();
	if(RegBuf==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"初始化注册消息失败！");
		exit(-1);
	}
	/*设置环境变量*/
	setenv("CHANNELNAME",ChannelName,1);
	setenv("TESADDR",TesAddr,1);
	setenv("ADAPTERADDR",AdapterAddr,1);
	setenv("ADAPTERCONFIG",EtcFile,1);

	ret=addContent(&RegBuf,"SIMTYPE",SimType,strlen(SimType));
	if(ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"Add content SIMTYPE error!");
		infoDel(&RegBuf);
		exit(-1);
	}
	TesLog(sLogFile,LOG_INFO,RunLevel,"RegBuf=[%s],SIMTYPE=[%s]",RegBuf,SimType);
	ret=reg2tes(&RegBuf,&ConfigBuf);
	if(ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"向核心注册失败!");
		infoDel(&RegBuf);
		infoDel(&ConfigBuf);
		exit(-1);
	}
	infoDel(&RegBuf);

	/*读取返回码*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	DataSize=0;
	ret=readContent(ConfigBuf,"RESULT",sTmpBuf,(int *)&DataSize);
	if (ret==-1)
	{
		printf("ReadContent CONFIGINFO result failed\n");
		infoDel(&ConfigBuf);
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO result failed");
		exit(-1);
	}
	/*返回码非0，标识不成功*/
	if(sTmpBuf[0]!='0')
	{
		/*读取错误信息*/
		memset(sTmpBuf,0,sizeof(sTmpBuf));
		DataSize=0;
		ret=readContent(ConfigBuf,"ERRMSG",sTmpBuf,(int *)&DataSize);
		if (ret==-1)
		{
			printf("ReadContent CONFIGINFO errmsg failed\n");
			infoDel(&ConfigBuf);
			TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO errmsg failed");
			exit(-1);
		}
		infoDel(&ConfigBuf);
		printf("向核心注册失败，%s。\n",sTmpBuf);
		exit(-1);
	}

	memset(Data,0x00,sizeof(Data));
	DataSize=0;
	ret=readContent(ConfigBuf,"CONFIGINFO",Data,(int *)&DataSize);
	infoDel(&ConfigBuf);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO failed");
		exit(-1);
	}
	/*更新配置文件*/
	if(DataSize>0)
	{
		fp=fopen(EtcFile,"w");
		if(fp==NULL)
		{
			TesLog(sLogFile,LOG_ERROR,3,"注册更新适配器配置文件失败!");
			exit(-1);
		}
		fprintf(fp,"%s",Data);
		fflush(fp);
		fclose(fp);
	}
	else
	{
		printf("注册失败，下载适配器配置失败!\n");
		TesLog(sLogFile,LOG_ERROR,3,"注册失败，下载适配器配置失败!");
		exit(-1);
	}

	TesLog(sLogFile,LOG_INFO,RunLevel,"更新配置文件完成，file len=[%d]",DataSize);
	

	/*建立共享内存，信号量*/
	semid=semget(ShmKey,1,(0777|IPC_CREAT));
        if (semid <0 )
        {
                TesLog(sLogFile,LOG_ERROR,3,"Create Semaphore Fail,ERRNO=[%d]...",errno);
                exit(-1);
        }

	if (semctl(semid,0,SETVAL,1) < 0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Init Semaphore Fail,ERRNO=[%d]...",errno);
		exit(-1);
	}
	
	if (semop(semid,&acquire,1) < 0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Get Semaphore Fail,ERRNO=[%d]...",errno);
		exit(-1);
	}
	
	shmid=shmget(ShmKey,sizeof(SHMCONTENT),(0777|IPC_CREAT));
	if (shmid <0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Create Share Memory Fail,ERRNO=[%d]...",errno);
		exit(-1);
	}
	
	shmaddr=(unsigned char  *)shmat(shmid,0,0);
	if (shmaddr==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"Attack Share memory fail...");
		exit(-1);
	}
	pshm=(SHMCONTENT *)shmaddr;

	pshm->cmd='0';
	pshm->cpc=0;	
	pshm->mpc=DealCount;
	
	/*按参数启动对应数量的业务处理子进程*/	
	ret=StartChildProc(pshm);
	if (ret < 0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Start Child Process Fail...");
		exit(-1);
	}
	
	ret=shmdt(shmaddr);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"Dettack Share memory fail...");
		exit(-1);
	}
	
	if (semop(semid,&release,1) < 0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Free Semaphore Fail,ERRNO=[%d]...",errno);
		exit(-1);
	}

	/*休眠一段时间后，检查共享内存及子进程的运行状态*/	
	while ( 1 )
	{
		sleep(ShmQryStep);

		if (semop(semid,&acquire,1) < 0 )
		{
			TesLog(sLogFile,LOG_ERROR,3,"Get Semaphore Fail,ERRNO=[%d]...",errno);
			exit(-1);
		}

		shmaddr=(unsigned char  *)shmat(shmid,0,0);
		if (shmaddr==NULL)
		{
		   	 	TesLog(sLogFile,LOG_ERROR,3,"Attack Share memory fail...");
		   	 	exit(-1);
		}

		pshm=(SHMCONTENT *)shmaddr;
		/*ShowShmContent(pshm);*/
		
		/*检查清理僵尸进程*/
		deadc=CheckDeadProc(pshm);
		if (deadc > 0)
		{
			/*清楚僵尸进程的登记信息,整理共享内存内容*/
			CleanShmContent(pshm,deadc);
		}
	
		if (ShutDownNow(pshm))
		{ /*共享内存已经登记了"系统停止命令"*/
			if (ShutDownOK(pshm))
			{ /*子进程都已经停止运行了,主进程可以退出*/
				ret=shmdt(shmaddr);
		        	if (ret==-1)
        			{
               	 			TesLog(sLogFile,LOG_ERROR,3,"Dettack Share memory fail!");
		               	 	exit(-1);
				}

			 	break;
			}
		}
		else
		{
			if (ChildMustStart(pshm) )
			{
				/*当前运行子进程数与最大进程数量不一致,需要启动子进程*/
				ret=StartChildProc(pshm);
				if (ret <0 )
				{
					ret=shmdt(shmaddr);
		        		if (ret==-1)
        				{
               	 				TesLog(sLogFile,LOG_ERROR,3,"Dettack Share memory fail...");
		               	 		exit(-1);
					}

				 	break;
				}
			}

		}

		ret=shmdt(shmaddr);
        	if (ret==-1)
        	{
               	 	TesLog(sLogFile,LOG_ERROR,3,"Dettack Share memory fail");
               	 	exit(-1);
		}

		if (semop(semid,&release,1) < 0 )
		{
			TesLog(sLogFile,LOG_ERROR,3,"Free Semaphore Fail,ERRNO=[%d]...",errno);
			exit(-1);
		}
	}

	shmctl(shmid,IPC_RMID,0);
	semctl(semid,0,IPC_RMID,0);

	exit(0);
}

int StartChildProc(SHMCONTENT *pshmc)
{
	pid_t		pid;
	int		i,ret;
	char		cmd[128],tmp[3];

	int		simulators;
	
	simulators=0;

	for (i=0 ; i<pshmc->cpc; i++)
	{
		if (pshmc->cps[i].status== '0')
		/*检查共享区子进程状态，0为正常状态，统计正常子进程总数*/
		{
			simulators ++;
		}
	}

	simulators = DealCount - simulators;

	for (i=pshmc->cpc ; i< pshmc->cpc+simulators; i++)
	{
		DealNO ++;
		if ((pid=fork())>0) 
		{ /*父进程处理过程,登记每一个启动的子进程消息*/
			pshmc->cps[i].pid=pid;
			pshmc->cps[i].cmd='0';
			pshmc->cps[i].status='0';
		}
		else if (pid==0) 
		{ /*子进程处理过程*/
			sprintf(cmd,"MQdeal");
			sprintf(tmp,"%02d",DealNO);
			ret=execl(cmd,"MQdeal",tmp,(char *)0 );
       	 	exit(0);
		}
		else 
		{
			TesLog(sLogFile,LOG_ERROR,3,"Can not fork Deal process,exited!");
       	 	return(-3);
		}
	}

	pshmc->cpc=pshmc->mpc;
	
       	return(0);
}
