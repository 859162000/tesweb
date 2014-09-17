#include <string.h>
#include <unistd.h>

#ifdef LINUX
#include "MQproc.h"
#else
#include "sim.h"
#endif

#include "Util.h"

int	ShutDownNow(SHMCONTENT *pshmc)
{
	if (pshmc->cmd == '2' ) return(1);
	else	return(0);
}

int	InitGlobalNow(SHMCONTENT *pshmc)
{
	pid_t	pid;
	int	i;

	if (pshmc->cmd == '1' )
	{
		pid=getpid();
		for (i=0; i< pshmc->cpc; i++)
		{
			if (pshmc->cps[i].pid == pid)
			{
				if (pshmc->cps[i].cmd == '1') return(1);
				else return(0);
			}
		}
	}
	else	return(0);
}

int	ChildMustStart(SHMCONTENT *pshmc)
{
	int	i,k;

	if (pshmc->cpc == pshmc->mpc ) return(0);
	else  return(pshmc->mpc - pshmc->cpc);
}

int	ShutDownOK(SHMCONTENT *pshmc)
{
	int	i,procs;

	procs=pshmc->cpc;
	for (i=0;i<procs;i++)
		if (pshmc->cps[i].status == '0' ) return(0);

	return(1);
}

int	ShowShmContent(SHMCONTENT *pshmc)
{
	int	i,procs;
	char    *cmddesc []={"已执行","刷新参数","停止"};
	char    *statusdesc []={"正常运行","正常停止","异常停止"};

	procs=pshmc->cpc;

	TesLog(sLogFile,LOG_DEBUG,RunLevel,"---------------MQ Client SHM Content--------------");
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"System Command Code :  %s", cmddesc[pshmc->cmd-48]);
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"Running Process Count: %d", pshmc->cpc);
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"Process Maximum Count: %d", pshmc->mpc);
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"-----------------------------------------------------");
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"  PID		CMD	STATUS	TYPE");
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"-----------------------------------------------------");
	for (i=0;i<procs;i++)
	{	
		TesLog(sLogFile,LOG_DEBUG,RunLevel,"[%010d]\t[%s]\t[%s]\t[%s]", pshmc->cps[i].pid
			, cmddesc[pshmc->cps[i].cmd-48]
			, statusdesc[pshmc->cps[i].status-48]
			, "模拟进程");
	}
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"-----------------------------------------------------");
	return(0);
}

int	CheckDeadProc(SHMCONTENT *pshmc)
{
	int	i,j,procs,ret;
	
	procs=pshmc->cpc;
	j=0;

	for (i=0;i<procs;i++)
	{
		ret=waitpid(pshmc->cps[i].pid,NULL,WUNTRACED|WNOHANG);
		if ( ret > 0 ) 
		{
			pshmc->cps[i].status='2';
			j++;
			DealNO --;
		}
	}

	return(j);
}

int	CleanShmContent(SHMCONTENT *pshmc,int pdead)
{
	int		i,j,procs;
	SHMCONTENT	content;

	procs=pshmc->cpc;
	pshmc->cpc = pshmc->cpc - pdead;
	j=0;

	for (i=0;i<procs;i++)
	{
		if (pshmc->cps[i].status == '0' )
		{
			memcpy(&content.cps[j],&pshmc->cps[i],sizeof(PROCSLIST));
			j++;
		}
	}
	for (i=0;i<j;i++)
	{
		memcpy(&pshmc->cps[i],&content.cps[i],sizeof(PROCSLIST));
	}

	for (i=j;i<j+pdead;i++)
	{
		memset(&pshmc->cps[i],'\0',sizeof(PROCSLIST));
	}

	return(0);
}

int	CheckShmCmd()
{
	unsigned char	*shmaddr;
	SHMCONTENT 	*pshm;

	pid_t		pid;
	int		ret,i,flag;
	int		shmid,semid;
	static struct sembuf    acquire={0,-1,SEM_UNDO};
	static struct sembuf    release={0, 1,SEM_UNDO};


	pid=getpid();
	flag = 0;
	
	semid=semget(ShmKey,1,0);
	if (semid <0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Attack Semaphore Fail,ERRNO=[%d]...",errno);
		return(-1);
	}

	if (semop(semid,&acquire,1) < 0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Get Semaphore Fail,ERRNO=[%d]...",errno);
		return(-1);
	}

	shmid=shmget(ShmKey,sizeof(SHMCONTENT),0777);
        if (shmid <0 )
        {
            TesLog(sLogFile,LOG_ERROR,3,"Get  Share memory Fail,ERRNO=[%d]...",errno);
						if (semop(semid,&release,1) < 0 )
								TesLog(sLogFile,LOG_ERROR,3,"Release Semaphore Fail,ERRNO=[%d]...",errno);

                return(-1);
        }

	shmaddr=(unsigned char *)shmat(shmid,0,0);
        if (shmaddr==NULL)
        {
           TesLog(sLogFile,LOG_ERROR,3,"Attack Share memory Fail,ERRNO=[%d]...",errno);
						if (semop(semid,&release,1) < 0 )
								TesLog(sLogFile,LOG_ERROR,3,"Release Semaphore Fail,ERRNO=[%d]...",errno);
            return(-2);
        }
	pshm=(SHMCONTENT *)shmaddr;

	if (ShutDownNow(pshm))
	{
		for (i=0; i< pshm->cpc; i++)
		{
			if (pshm->cps[i].pid == pid)
			{
				pshm->cps[i].status = '1'; break;
			}
		}
		flag=1;
	}
	else
	{
		if (InitGlobalNow(pshm))
		{
			ret=InitGlobalVal();
			if ( ret < 0 )
        		{
				TesLog(sLogFile,LOG_ERROR,3,"Init Global Value fail...");
				if (semop(semid,&release,1) < 0 )
					TesLog(sLogFile,LOG_ERROR,3,"Release Semaphore Fail,ERRNO=[%d]...",errno);
				return(-3);
        		}
			for (i=0; i< pshm->cpc; i++)
			{
				if (pshm->cps[i].pid == pid)
				{
					pshm->cps[i].cmd = '0'; break;
				}
			}
			flag=2;
		}
	}
	
	ret=shmdt(shmaddr);
       	if (ret==-1)
       	{
		printf("Dettack Share memory fail...\n");
		if (semop(semid,&release,1) < 0 )
			TesLog(sLogFile,LOG_ERROR,3,"Release Semaphore Fail,ERRNO=[%d]...",errno);
		return(-4);
	}
	if (semop(semid,&release,1) < 0 )
	{
		TesLog(sLogFile,LOG_ERROR,3,"Release Semaphore Fail,ERRNO=[%d]...",errno);
		return(-1);
	}

	return(flag);
}

int	InitGlobalVal()
{
    char *p;

	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.QM");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.QM设置不正确");		
		return(-12201);
	}
	else
	{
		strip(p);
		memset(SimQM,0x00,sizeof(SimQM));
		strcpy(SimQM,p);
	}
	
	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.REQUEST.QUEUE");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.REQUEST.QUEUE设置不正确");		
		return(-12202);
	}
	else
	{
		strip(p);
		memset(SimReqQ,0x00,sizeof(SimReqQ));
		strcpy(SimReqQ,p);
	}

	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.RESPONSE.QUEUE");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.RESPONSE.QUEUE设置不正确");		
		return(-12203);
	}
	else
	{
		strip(p);
		memset(SimResQ,0x00,sizeof(SimResQ));
		strcpy(SimResQ,p);
	}


	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.MQ.TIMEOUT");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.MQ.TIMEOUT设置不正确");		
		return(-12204);
	}
	else
	{
		strip(p);
		MQFetchTimeOut=atoi(p);
	}

	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.DEALER.COUNT");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.DEALER.COUNT设置不正确");		
		return(-12205);
	}
	else
	{
		strip(p);
		DealCount=atoi(p);
	}

	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.IDLE.TIMEOUT");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.IDLE.TIMEOUT设置不正确");		
		return(-12206);
	}
	else
	{
		strip(p);
		ShmQryStep=atoi(p);
	}

	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.SHMKEY");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.SHMKEY设置不正确");		
		return(-12207);
	}
	else
	{
		strip(p);
		ShmKey=atoi(p);
	}
         
	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.RUNLEVEL");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.RUNLEVEL设置不正确");		
		return(-12208);
	}
	else
	{
		strip(p);
		RunLevel=atoi(p);
	}


	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.MESSAGE.TYPE");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.MESSAGE.TYPE设置不正确");		
		return(-12209);
	}
	else
	{
		strip(p);
		MsgType=atoi(p);
	}


  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.SYSID");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.SYSID设置不正确");		
		return(-12210);
	}
	else
	{
		strip(p);
		memset(sysid,0x00,sizeof(sysid));
		strcpy(sysid,p);
	}

  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.TASKID");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.TASKID设置不正确");		
		return(-12211);
	}
	else
	{
		strip(p);
		memset(taskid,0x00,sizeof(taskid));
		strcpy(taskid,p);
	}


  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.APPID");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.APPID设置不正确");		
		return(-12212);
	}
	else
	{
		strip(p);
		memset(APP_ID,0x00,sizeof(APP_ID));
		strcpy(APP_ID,p);
	}

  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIM.PKG_APP_ID");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIM.PKG_APP_ID设置不正确");		
		return(-12213);
	}
	else
	{
		strip(p);
		memset(PKG_APP_ID,0x00,sizeof(PKG_APP_ID));
		strcpy(PKG_APP_ID,p);
	}
	
  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","CHANNELNAME");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中CHANNELNAME设置不正确");		
		return(-12214);
	}
	else
	{
		strip(p);
		memset(ChannelName,0x00,sizeof(ChannelName));
		strcpy(ChannelName,p);
	}

	

  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","SIMTYPE");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中SIMTYPE设置不正确");		
		return(-12215);
	}
	else
	{
		strip(p);
		memset(SimType,0x00,sizeof(SimType));
		strcpy(SimType,p);
	}	

	
  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","TESADDR");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中TESADDR设置不正确");		
		return(-12216);
	}
	else
	{
		strip(p);
		memset(TesAddr,0x00,sizeof(TesAddr));
		strcpy(TesAddr,p);
	}	


  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","ADAPTERADDR");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中ADAPTERADDR设置不正确");		
		return(-12217);
	}
	else
	{
		strip(p);
		memset(AdapterAddr,0x00,sizeof(AdapterAddr));
		strcpy(AdapterAddr,p);
	}	





	/*****安全配置信息*******/
	
  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","DYNAMIC_IN");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中DYNAMIC_IN设置不正确");		
		return(-12218);
	}
	else
	{
		strip(p);
		memset(DynamicIn,0x00,sizeof(DynamicIn));
		strcpy(DynamicIn,p);
	}	
	
		
  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","DYNAMIC_NAME");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中DYNAMIC_NAME设置不正确");		
		return(-12219);
	}
	else
	{
		strip(p);
		memset(DynamicName,0x00,sizeof(DynamicName));
		strcpy(DynamicName,p);
	}	
	
  	p=NULL;
	p=ReadIni(EtcFile,"OPTION","DYNAMIC_OUT");
	if(p==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"配置文件中DYNAMIC_OUT设置不正确");		
		return(-12220);
	}
	else
	{
		strip(p);
		memset(DynamicOut,0x00,sizeof(DynamicOut));
		strcpy(DynamicOut,p);
	}	



	return(0);
}
