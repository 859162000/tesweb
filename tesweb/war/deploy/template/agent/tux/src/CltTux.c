/******************************************************************************
  ģ�����ƣ�CltTux.c
  ģ�鹦�ܣ��ͻ���Tuxedo��������
  ���ģ��: 
  ������������־��
  ��д���ڣ�20010/01/06
  ά����¼��
 ******************************************************************************/
#include <atmi.h>
#include <sys/time.h>
#include <stdio.h>
#include <errno.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <memory.h>
#include <unistd.h>
#include <signal.h>
#include <stdarg.h>
#include <string.h>
#include <strings.h>

#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/types.h>

#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "CltTux.h"
#include "Util.h"
#include "comm.h"
#include "Adapter2Tes.h"

void  *TesSecLib;
int   (*TesSecFuncIn)();
int   (*TesSecFuncOut)();
const char *dlError;

int main(int argc,char **argv)
{
	
	int  iRetCode;
	int  iPid;
	int  iTranCode;
	int  iSock, iNewSock,iTransferSock;
	int  iSendLen, iRecvLen;
    
	char sTmpBuf[65536];
	char sRecvBuf[65536];
	char SecBuf[65536];
	int iSecBufLen;
	char *TuxSendBuf,*TuxRcvBuf,*sRspBuf;
	char sLogStr[65536];

	char sResult[20];
	char sErrMsg[512];
	char sCurTime[128];
	int i,j;
	int DataSize;
	FILE *fp;
	char *RegBuf,*ConfigBuf;
	
	

	struct tm *tbp;
	long ltime;

	
	/*ȡϵͳ����*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "%s/agent/log/CltTux.log.%s",getenv("HOME"),sTmpBuf);

	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter starting ,pid=[%ld]��",getpid());
	
	
	/*��ȡ�����ļ�����ú��Ĳ���*/
	iRetCode=GetConfig();
	if(iRetCode!=0)
	{
		printf("GetConfig error![%d]\n",iRetCode);
		TesLog(sLogFile,LOG_ERROR,3,"GetConfig error![%d]\n",iRetCode);
		exit(-1);
	}

	/*���û�������*/
	setenv("CHANNELNAME",ChannelName,1);
	setenv("TESADDR",TesAddr,1);
	setenv("ADAPTERCONFIG",cfgFile,1);


	/*ע��*/
	RegBuf=messageInit();
	if(RegBuf==NULL)
	{
			TesLog(sLogFile,LOG_ERROR,3,"��ʼ��ע����Ϣʧ�ܣ�\n");
			exit(-1);
	}
	
	iRetCode=addContent(&RegBuf,"SIMTYPE",SimType,strlen(SimType));
	if(iRetCode==-1)
	{
	        TesLog(sLogFile,LOG_ERROR,3,"Add content SIMTYPE error!\n");
	        infoDel(&RegBuf);
		exit(-1);	
	}

	memset(sTmpBuf,0,sizeof(sTmpBuf));
	sprintf(sTmpBuf,"%d",iPort);
	iRetCode=addContent(&RegBuf,"PORT",sTmpBuf,strlen(sTmpBuf));
	if(iRetCode==-1)
	{
	        TesLog(sLogFile,LOG_ERROR,3,"Add content PORT error!\n");
	        infoDel(&RegBuf);
		exit(-1);	
	}

	
	/*�����ע��*/
	gettimeofday (&Tpbegin, NULL);
	iRetCode=reg2tes(&RegBuf,&ConfigBuf);
	if(iRetCode<0)
	{
		printf("�����ע��ʧ��,iRetCode=[%d]!\n",iRetCode);
		TesLog(sLogFile,LOG_ERROR,3,"�����ע��ʧ��,iRetCode=[%d]!\n",iRetCode);
		infoDel(&RegBuf);
		infoDel(&ConfigBuf);
		exit(-1);
	}
	infoDel(&RegBuf);

	/*��ȡ������*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	DataSize=0;
	iRetCode=readContent(ConfigBuf,"RESULT",sTmpBuf,(int *)&DataSize);
	if (iRetCode==-1)
	{
		printf("ReadContent CONFIGINFO result failed\n");
		infoDel(&ConfigBuf);
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO result failed\n");
		exit(-1);
	}
	/*�������0����ʶ���ɹ�*/
	if(sTmpBuf[0]!='0')
	{
		/*��ȡ������Ϣ*/
		memset(sTmpBuf,0,sizeof(sTmpBuf));
		DataSize=0;
		iRetCode=readContent(ConfigBuf,"ERRMSG",sTmpBuf,(int *)&DataSize);
		if (iRetCode==-1)
		{
			printf("ReadContent CONFIGINFO errmsg failed\n");
			infoDel(&ConfigBuf);
			TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO errmsg failed\n");
			exit(-1);
		}
		infoDel(&ConfigBuf);
		printf("�����ע��ʧ�ܣ�%s��\n",sTmpBuf);
		exit(-1);
	}

	memset(sTmpBuf,0x00,sizeof(sTmpBuf));
	DataSize=0;
	iRetCode=readContent(ConfigBuf,"CONFIGINFO",sTmpBuf,(int *)&DataSize);
	infoDel(&ConfigBuf);
	if (iRetCode==-1)
	{
		printf("ReadContent CONFIGINFO failed\n");
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO failed\n");
		exit(-1);
	}
	/*���������ļ�*/
	if(DataSize>0)
	{
		fp=fopen(cfgFile,"w");
		if(fp==NULL)
		{
			printf("ע����������������ļ�ʧ��!\n");
			TesLog(sLogFile,LOG_ERROR,3,"ע����������������ļ�ʧ��!\n");
			exit(-1);
		}
		fprintf(fp,"%s",sTmpBuf);
		fflush(fp);
		fclose(fp);
	}
	else
	{
		printf("ע��ʧ�ܣ���������������ʧ��!\n");
		TesLog(sLogFile,LOG_ERROR,3,"ע��ʧ�ܣ���������������ʧ��!\n");
		exit(-1);
	}

	/*���¶�ȡ���º������*/
	iRetCode=GetConfig();
	if(iRetCode!=0)
	{
		printf("Get new Config error![%d]\n",iRetCode);
		TesLog(sLogFile,LOG_ERROR,3,"Get new Config error![%d]\n",iRetCode);
		exit(-1);
	}

	setenv("WSNADDR",WsnAddr,1);

	/*�����ػ�����*/	
	DeamonStart();
    			
	
	memset(sCurTime,0,sizeof(sCurTime));
	GetCurrentDateTime2(sCurTime);
	

	
	/*����TCP����*/
	/*����socket*/
	iSock = TCPCreatSock();
	if(iSock < 0)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPCreatSock Fail, Errno: [%d]!\n", iSock);
		exit(iSock);
	}
	/*�󶨶˿�*/
	iRetCode = TCPBindSock(iSock, iPort);
	if(iRetCode < 0)
	{
		printf("Bind socket error ,port=[%d]",iPort);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPBindSock Fail, Errno: [%d]!\n", iRetCode);
		TCPCloseSock(iSock);
		exit(iRetCode);
  }

	iRetCode = TCPListenSock(iSock);
	if(iRetCode < 0)
	{
    		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPListenSock Fail, Errno: [%d]!\n", iRetCode);
    		TCPCloseSock(iSock);
    		exit(iRetCode);
	}
	
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter start OK!");
	
	/*���ذ�ȫ��*/
	iRetCode=LoadSecLib();
	if(iRetCode<0)
	{
		printf("Load Sec library error [%d],errmgs=[%s]", iRetCode,dlError);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Load Sec library error [%d],errmgs=[%s]", iRetCode,dlError);
   		TCPCloseSock(iSock);
		exit(iRetCode);
	}
	else
	{
		if( (DynamicIn[0]=='1') || (DynamicOut[0]=='1') )
			TesLog(sLogFile,LOG_INFO,RunLevel,"Load Sec library [%s] OK", DynamicName);
	}

	
	for(;;)
	{
	        iNewSock =  TCPAcceptSock(iSock);
        	if(iNewSock < 0)
        	{
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPAcceptSock Fail, Errno: [%d]!\n", iNewSock);
            		continue;
        	}

        	iPid = fork();
        	if(iPid < 0)
        	{
            		TCPCloseSock(iNewSock);
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"fork Fail, Errno: [%d]!\n", iPid);
            		continue;
        	}
        	/*������continue��������*/
        	else if(iPid > 0)
        	{
            		TCPCloseSock(iNewSock);
            		continue;
        	}
			/*iPid=0��Ϊ�ӽ��̣����½���ת������*/
			        
        	TCPCloseSock(iSock);
        	memset(sTmpBuf, 0, sizeof(sTmpBuf));
        	iRetCode = TCPGetPeerAddr(iNewSock, sTmpBuf);
        	if(iRetCode < 0)
        	{
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPGetPeerAddr Fail, Errno: [%d]!", iRetCode);
            		TCPCloseSock(iNewSock);
            		exit(iRetCode);
        	}
        	TesLog(sLogFile,LOG_INFO,RunLevel,"\n\n\n##############################################################################");
        	TesLog(sLogFile,LOG_INFO,RunLevel,"New request from core, core ip: [%s]", sTmpBuf);



			/*����ԭʼ������*/        
        	memset(sRecvBuf, 0, sizeof(sRecvBuf));
        	iRecvLen = TCPReadSock(iNewSock, sRecvBuf,sizeof(sRecvBuf), TES_COMM_TIMEOUT);
       		if(iRecvLen < 0)
        	{
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"Read from core fail, Errno: [%d]!", iRecvLen);
            		TCPCloseSock(iNewSock);
            		exit(iRetCode);
        	}

			TesLog(sLogFile,LOG_INFO,RunLevel,"Recieve msg from core,len=[%d]:",iRecvLen);	        	
		  	/*��ͨѶ����16������־*/
		  	memset(sLogStr,0,sizeof(sLogStr));
			HexLogStr(sRecvBuf,iRecvLen,sLogStr);
			TesLog(sLogFile,LOG_DEBUG,RunLevel,sLogStr);
			
			iRetCode=readContent(sRecvBuf,"REQMESSAGE",sTmpBuf,&iSendLen);
			if (iRetCode<0)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"ReadContent REQMESSAGE failed[%d]",iRetCode);
	    		TCPCloseSock(iNewSock);
           		exit(iRetCode);
			}

			/*�����û��Զ��尲ȫ������*/
			if(DynamicIn[0]=='1')
			{
				memset(SecBuf,0,sizeof(SecBuf));
				iSecBufLen=0;
				iRetCode=(*TesSecFuncIn)(sTmpBuf,iSendLen,SecBuf,&iSecBufLen);
				if(iRetCode!=0)
				{
					TesLog(sLogFile,LOG_ERROR,RunLevel,"Enc or dec request message error[%d]",iRetCode);
            		TCPCloseSock(iNewSock);
	           		exit(iRetCode);
	           	}
	           	else
	           	{
					TesLog(sLogFile,LOG_INFO,RunLevel,"Enc or dec request message OK,original msg len=[%d] and now[%d]",iSendLen,iSecBufLen);	
					memset(sTmpBuf,0,sizeof(sTmpBuf));
					memcpy(sTmpBuf,SecBuf,iSecBufLen);
					iSendLen=iSecBufLen;
	        	}
			}
			
			memset(sResult,0,sizeof(sResult));
			memset(sErrMsg,0,sizeof(sErrMsg));
			
        	
    		
/*************************************************************************
			����Ϊ����ת��
*************************************************************************/	
			if(!strcmp(CommType,"WSL"))
			{
				if (tpinit((TPINIT *) NULL) == -1) 
				{
					TesLog(sLogFile,LOG_ERROR,RunLevel,"WSNADDR=[%s]",getenv("WSNADDR"));
					TesLog(sLogFile,LOG_ERROR,RunLevel,"tpinit error!tperrno=[%d]",tperrno);
            		TCPCloseSock(iNewSock);
					exit(-1);
				}

			}
			TuxSendBuf = (char*)tpalloc("CARRAY",NULL,(long)(iSendLen+1));
			if(TuxSendBuf==NULL)
			{
			        TesLog(sLogFile,LOG_ERROR,RunLevel,"TuxSendBuf�����ڴ�ռ�ʧ��!");
            		TCPCloseSock(iNewSock);
			        exit(-1);
			}
			
			TuxRcvBuf=(char*)tpalloc("CARRAY", NULL, 65536);
			if(TuxRcvBuf == NULL)
			{
			        tpfree(TuxSendBuf);
			        TesLog(sLogFile,LOG_ERROR,RunLevel,"TuxRcvBuf�����ڴ�ռ�ʧ��!");
            		TCPCloseSock(iNewSock);
			        exit(-1);
			}
			memset(TuxSendBuf,0,sizeof(TuxSendBuf));
			memcpy(TuxSendBuf, sTmpBuf, iSendLen);
			
			iRecvLen=0;
			iRetCode = tpcall(AgentTuxService, (char *)TuxSendBuf, (long)(iSendLen), (char **)&TuxRcvBuf, (long *)&iRecvLen, (long)0);
			/*ת���������ɴ�����*/
			if(iRetCode<0)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"tpcall error [%d]!",tperrno);
				memset(sResult,0,sizeof(sResult));
				memset(sErrMsg,0,sizeof(sErrMsg));
				strcpy(sResult,"TPCERR");
				sprintf(sErrMsg,"tpcall error [%d]",tperrno);
				/*������Ϣ����Ϊ��*/
				iRecvLen=0;
				memset(sTmpBuf,0,sizeof(sTmpBuf));				
				
			}
			else
			{
				TesLog(sLogFile,LOG_INFO,RunLevel,"tuxedoת����Ϣ��ɣ�����Ӧ�𳤶�[%d]:",iRecvLen);
				memset(sResult,0,sizeof(sResult));
				strcpy(sResult,"000000");
				memset(sErrMsg,0,sizeof(sErrMsg));
				memset(sTmpBuf,0,sizeof(sTmpBuf));
				memcpy(sTmpBuf,TuxRcvBuf,iRecvLen);
			  	/*��¼Ӧ��16���Ʊ���*/
			  	memset(sLogStr,0,sizeof(sLogStr));
				HexLogStr(sTmpBuf,iRecvLen,sLogStr);
				TesLog(sLogFile,LOG_DEBUG,RunLevel,sLogStr);
				
			}

			tpfree(TuxSendBuf);
			tpfree(TuxRcvBuf);
		


/*************************************************************************
			ת����������ϣ���ʼ����Ӧ���ĸ�����
*************************************************************************/

			/*׼����Ӧ����*/
			sRspBuf=messageInit();
			if(sRspBuf==NULL)
			{
				TesLog(sLogFile,LOG_ERROR,3,"��ʼ����Ӧ����ʧ�ܣ�\n");
           		TCPCloseSock(iNewSock);
				exit(-1);
			}
			/*������*/
			iRetCode=addContent(&sRspBuf,"RESULT",sResult,strlen(sResult));
			if(iRetCode==-1)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"Add content RESULT error!\n");
				infoDel(&sRspBuf);
           		TCPCloseSock(iNewSock);
				exit(-1);
			}
			/*������Ϣ*/
			if(strlen(sErrMsg)>0)
			{
				iRetCode=addContent(&sRspBuf,"ERRMSG",sErrMsg,strlen(sErrMsg));
				if(iRetCode==-1)
				{
					TesLog(sLogFile,LOG_ERROR,RunLevel,"Add content ERRMSG error!\n");
					infoDel(&sRspBuf);
            		TCPCloseSock(iNewSock);
					exit(-1);
				}
			}
			/*��Ӧ����Ϣ��*/		
			/*���Ӧ����Ϣ����Ϊ0���߷����벻��000000����Ӧ����Ϣ�̶�Ϊ10���ո�*/			
			if((iRecvLen<=0)||(strcmp(sResult,"000000")!=0))					
			{
				memset(sTmpBuf,0,sizeof(sTmpBuf));
				strcpy(sTmpBuf,"          ");
				iRecvLen=strlen(sTmpBuf);
			}
			else
			{
				/*�����û��Զ��尲ȫ������*/
				if(DynamicOut[0]=='1')
				{
					memset(SecBuf,0,sizeof(SecBuf));
					iSecBufLen=0;
					iRetCode=(*TesSecFuncOut)(sTmpBuf,iRecvLen,SecBuf,&iSecBufLen);
					if(iRetCode!=0)
					{
						TesLog(sLogFile,LOG_ERROR,RunLevel,"Enc or dec response message error[%d]",iRetCode);
	            		TCPCloseSock(iNewSock);
		           		exit(iRetCode);
		           	}
		           	else
		           	{
						TesLog(sLogFile,LOG_INFO,RunLevel,"Enc or dec request message OK,original msg len=[%d] and now[%d]",iRecvLen,iSecBufLen);	
						memset(sTmpBuf,0,sizeof(sTmpBuf));
						memcpy(sTmpBuf,SecBuf,iSecBufLen);
						iRecvLen=iSecBufLen;
		        	}
				}
			
			}

			UnloadSecLib();
			
			iRetCode=addContent(&sRspBuf,"RESMESSAGE",sTmpBuf,iRecvLen);
			if(iRetCode==-1)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"Add content RESMESSAGE len=[%d],[%s] error!\n",iRecvLen,sTmpBuf);
				infoDel(&sRspBuf);
           		TCPCloseSock(iNewSock);
				exit(-1);
			}

			memset(sTmpBuf,0,sizeof(sTmpBuf));
			memcpy(sTmpBuf, sRspBuf+10, 10);
			iSendLen = atoi(sTmpBuf);
    		iRetCode = TCPWriteSock(iNewSock, sRspBuf, iSendLen, TES_COMM_TIMEOUT);
        	if(iRetCode < 0)
        	{
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPWriteSock��������Ӧ���ĳ���: [%d]!", iRetCode);
            		TCPCloseSock(iNewSock);
            		infoDel(&sRspBuf);
            		exit(iRetCode);
        	}
			infoDel(&sRspBuf);

			TesLog(sLogFile,LOG_INFO,RunLevel,"��װ����Ӧ���ģ�����=[%d]��������=[%s]��������Ϣ=[%s]",iSendLen,sResult,sErrMsg);
			
	        TCPCloseSock(iNewSock);

	        TesLog(sLogFile,LOG_INFO,RunLevel, "��������Ӧ���ĳɹ������س���[%d]",iRetCode);
			memset(sLogStr,0,sizeof(sLogStr));
			HexLogStr(sRspBuf,iSendLen,sLogStr);
			TesLog(sLogFile,LOG_DEBUG,RunLevel,sLogStr);

	        exit(0);
	        
	}	/*end for*/

}		/*end main*/


int GetConfig()
{
	char *p;
	memset(cfgFile,0,sizeof(cfgFile));
	sprintf(cfgFile,"%s/agent/etc/CltTux.cfg",getenv("HOME"));
	
	p=NULL;
	p=ReadIni(cfgFile,"OPTION","PORT");
	if(p==NULL)
		return(-12001);			
	else
	{
		strip(p);
		iPort=atoi(p);
		if(iPort<100)
			return(-12100);			
	}

	p=NULL;
	p=ReadIni(cfgFile,"OPTION","RUNLEVEL");
	if(p==NULL)
		return(-12002);			
	else
	{
		strip(p);
		RunLevel=atoi(p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"TUXEDO","COMMTYPE");
	if(p==NULL)
		return(-12003);			
	else
	{
		strip(p);
		memset(CommType,0,sizeof(CommType));
		strcpy(CommType,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"TUXEDO","TUXSERVICE");
	if(p==NULL)
		return(-12004);			
	else
	{
		strip(p);
		memset(AgentTuxService,0,sizeof(AgentTuxService));
		strcpy(AgentTuxService,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"TUXEDO","WSNADDR");
	if(p==NULL)
		return(-12005);			
	else
	{
		strip(p);
		memset(WsnAddr,0,sizeof(WsnAddr));
		strcpy(WsnAddr,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"OPTION","CHANNELNAME");
	if(p==NULL)
		return(-12006);			
	else
	{
		strip(p);
		memset(ChannelName,0,sizeof(ChannelName));
		strcpy(ChannelName,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"OPTION","SIMTYPE");
	if(p==NULL)
		return(-12007);			
	else
	{
		strip(p);
		memset(SimType,0,sizeof(SimType));
		strcpy(SimType,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"OPTION","TESADDR");
	if(p==NULL)
		return(-12008);			
	else
	{
		strip(p);
		memset(TesAddr,0,sizeof(TesAddr));
		strcpy(TesAddr,p);
	}

	/*****��ȫ������Ϣ*******/
	p=NULL;
	p=ReadIni(cfgFile,"OPTION","DYNAMIC_IN");
	if(p==NULL)
		return(-12018);			
	else
	{
		strip(p);
		memset(DynamicIn,0,sizeof(DynamicIn));
		strcpy(DynamicIn,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"OPTION","DYNAMIC_OUT");
	if(p==NULL)
		return(-12020);			
	else
	{
		strip(p);
		memset(DynamicOut,0,sizeof(DynamicOut));
		strcpy(DynamicOut,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"OPTION","DYNAMIC_NAME");
	if(p==NULL)
		return(-12013);			
	else
	{
		strip(p);
		memset(DynamicName,0,sizeof(DynamicName));
		strcpy(DynamicName,p);
	}

	return(0);
	
}


int LoadSecLib()
{
	/*���ذ�ȫ��*/
	if((DynamicIn[0]=='1') || (DynamicOut[0]=='1'))
	{
		TesSecLib=dlopen(DynamicName,RTLD_LAZY);
		dlError=dlerror();
		if(dlError!=NULL)
		return -12110;
	}
	else
		return 0;
	/*�����Ĵ����־0���ް�ȫ����1���谲ȫ����*/
	if(DynamicIn[0]=='1')
	{
		TesSecFuncIn=dlsym(TesSecLib,"TesAdptSecFunc_In");
		dlError=dlerror();
		if(dlError!=NULL)
		return -12112;
	}

	/*�����Ĵ����־0���ް�ȫ����1���谲ȫ����*/
	if(DynamicOut[0]=='1')
	{
		TesSecFuncOut=dlsym(TesSecLib,"TesAdptSecFunc_Out");
		dlError=dlerror();
		if(dlError!=NULL)
		return -12114;
	}	
		
	return 0;
}


int UnloadSecLib()
{
	int iRetCode;
	if((DynamicIn[0]=='1') || (DynamicOut[0]=='1'))
	{
		iRetCode=dlclose(TesSecLib);
		if(iRetCode!=0)
		return -12120;
	}
	return 0;
}
