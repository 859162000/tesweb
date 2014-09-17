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

	/*�ź�����*/
	signal(SIGHUP,SIG_IGN);
	signal(SIGINT,SIG_IGN);
	signal(SIGTSTP,SIG_IGN);
	signal(SIGABRT,SIG_IGN);
	signal(SIGQUIT,SIG_IGN);

	/*�ļ�����ʼ��*/
	/*ȡϵͳ����*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "../log/MQ_INF.log.%s",sTmpBuf);
	
	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter starting ,pid=[%ld]��",getpid());

	memset(EtcFile,'\0',sizeof(EtcFile));
	sprintf(EtcFile,"../etc/MQadapter.cfg");

	CurPid=getpid();
	DealNO=0;

	/*��ʼ��ȫ�ֱ���*/
	ret=InitGlobalVal();
	if (ret)
	{
		printf("��ȡ�����ļ�[%s]ʧ�ܣ�\n",EtcFile);
		exit(-1);
	}

	/*ע��*/
	RegBuf=messageInit();
	if(RegBuf==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"��ʼ��ע����Ϣʧ�ܣ�");
		exit(-1);
	}
	/*���û�������*/
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
		TesLog(sLogFile,LOG_ERROR,3,"�����ע��ʧ��!");
		infoDel(&RegBuf);
		infoDel(&ConfigBuf);
		exit(-1);
	}
	infoDel(&RegBuf);

	/*��ȡ������*/
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
	/*�������0����ʶ���ɹ�*/
	if(sTmpBuf[0]!='0')
	{
		/*��ȡ������Ϣ*/
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
		printf("�����ע��ʧ�ܣ�%s��\n",sTmpBuf);
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
	/*���������ļ�*/
	if(DataSize>0)
	{
		fp=fopen(EtcFile,"w");
		if(fp==NULL)
		{
			TesLog(sLogFile,LOG_ERROR,3,"ע����������������ļ�ʧ��!");
			exit(-1);
		}
		fprintf(fp,"%s",Data);
		fflush(fp);
		fclose(fp);
	}
	else
	{
		printf("ע��ʧ�ܣ���������������ʧ��!\n");
		TesLog(sLogFile,LOG_ERROR,3,"ע��ʧ�ܣ���������������ʧ��!");
		exit(-1);
	}

	TesLog(sLogFile,LOG_INFO,RunLevel,"���������ļ���ɣ�file len=[%d]",DataSize);
	

	/*���������ڴ棬�ź���*/
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
	
	/*������������Ӧ������ҵ�����ӽ���*/	
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

	/*����һ��ʱ��󣬼�鹲���ڴ漰�ӽ��̵�����״̬*/	
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
		
		/*�������ʬ����*/
		deadc=CheckDeadProc(pshm);
		if (deadc > 0)
		{
			/*�����ʬ���̵ĵǼ���Ϣ,�������ڴ�����*/
			CleanShmContent(pshm,deadc);
		}
	
		if (ShutDownNow(pshm))
		{ /*�����ڴ��Ѿ��Ǽ���"ϵͳֹͣ����"*/
			if (ShutDownOK(pshm))
			{ /*�ӽ��̶��Ѿ�ֹͣ������,�����̿����˳�*/
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
				/*��ǰ�����ӽ�������������������һ��,��Ҫ�����ӽ���*/
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
		/*��鹲�����ӽ���״̬��0Ϊ����״̬��ͳ�������ӽ�������*/
		{
			simulators ++;
		}
	}

	simulators = DealCount - simulators;

	for (i=pshmc->cpc ; i< pshmc->cpc+simulators; i++)
	{
		DealNO ++;
		if ((pid=fork())>0) 
		{ /*�����̴������,�Ǽ�ÿһ���������ӽ�����Ϣ*/
			pshmc->cps[i].pid=pid;
			pshmc->cps[i].cmd='0';
			pshmc->cps[i].status='0';
		}
		else if (pid==0) 
		{ /*�ӽ��̴������*/
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
