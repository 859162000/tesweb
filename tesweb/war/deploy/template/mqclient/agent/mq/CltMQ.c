/******************************************************************************
  ģ�����ƣ�CltMQ.c
  ģ�鹦�ܣ������MQ��������
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
#include <cmqc.h>

#include "CltMQ.h"
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
	sprintf(sLogFile, "%s/agent/log/CltMQ.log.%s",getenv("HOME"),sTmpBuf);

	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"MQ Adapter starting ,pid=[%ld]��",getpid());
	
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

	memset(MQConnStr,0,sizeof(MQConnStr));
	sprintf(MQConnStr,"%s/%s/%s(%d)",MQChannel,MQCommType,MQSvrIP,iMQPort);
	setenv("MQSERVER",MQConnStr,1);
	setenv("CCSID",MQCcsid,1);
		
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
	
	TesLog(sLogFile,LOG_INFO,3,"MQ Adapter start OK!");
	TesLog(sLogFile,LOG_INFO,RunLevel,"MQ Connect env=[%s]!",MQConnStr);
	
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
			/*���ӵ�MQ*/
			iRetCode=ConnectQueue(&hcon,&hobj_req,&hobj_rsp);
			if (iRetCode !=0)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"connect to mq error![%d]",iRetCode);
				memset(sResult,0,sizeof(sResult));
				strcpy(sResult,"COMQER");
				if(strlen(sErrMsg)<1)
				{
					memset(sErrMsg,0,sizeof(sErrMsg));
					sprintf(sErrMsg,"connect to mq error![%d]",iRetCode);
				}
				/*������Ϣ����Ϊ��*/
				memset(sTmpBuf,0,sizeof(sTmpBuf));				
			}
			else
			{
				TesLog(sLogFile,LOG_INFO,RunLevel,"connect to mq OK!");	        	

				iRetCode=SndMsgToMQ(&hcon,&hobj_req,&mdp,sTmpBuf,iSendLen);
				if (iRetCode!=0)
				{
					TesLog(sLogFile,LOG_ERROR,RunLevel,"Send Message to MQ error! [%d]",iRetCode);
					memset(sResult,0,sizeof(sResult));
					strcpy(sResult,"WRMQER");
					if(strlen(sErrMsg)<1)
					{
						memset(sErrMsg,0,sizeof(sErrMsg));
						sprintf(sErrMsg,"Send Message to MQ error! [%d]",iRetCode);
					}
					/*������Ϣ����Ϊ��*/
					memset(sTmpBuf,0,sizeof(sTmpBuf));				
				}
				else
				{
					TesLog(sLogFile,LOG_INFO,RunLevel,"Send Message to MQ OK!");
					memset(sTmpBuf,0,sizeof(sTmpBuf));
					iRecvLen=0;
					iRetCode=RcvMsgFromMQ(&hcon,&hobj_rsp,&mdg,sTmpBuf,&iRecvLen);
					if (iRetCode !=0)
					{
						TesLog(sLogFile,LOG_ERROR,RunLevel,"Read Message from MQ error! [%d]",iRetCode);
						memset(sResult,0,sizeof(sResult));
						strcpy(sResult,"RDMQER");
						if(strlen(sErrMsg)<1)
						{
							memset(sErrMsg,0,sizeof(sErrMsg));
							sprintf(sErrMsg,"Read Message from MQ error! [%d]",iRetCode);
						}
						/*������Ϣ����Ϊ��*/
						memset(sTmpBuf,0,sizeof(sTmpBuf));				
						iRecvLen=0;
					}
					/*��ȡӦ����Ϣ�ɹ�*/
					else
					{
						TesLog(sLogFile,LOG_INFO,RunLevel,"Read Message from MQ OK,recieve msg len=[%d]:",iRecvLen);
						memset(sResult,0,sizeof(sResult));
						strcpy(sResult,"000000");
						memset(sErrMsg,0,sizeof(sErrMsg));
					  	/*��¼Ӧ��16���Ʊ���*/
					  	memset(sLogStr,0,sizeof(sLogStr));
						HexLogStr(sTmpBuf,iRecvLen,sLogStr);
						TesLog(sLogFile,LOG_DEBUG,RunLevel,sLogStr);
					
					}				        	
				
				}
			}

			DisConnectQueue(&hcon,&hobj_req,&hobj_rsp);

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
           		TCPCloseSock(iNewSock);	
				infoDel(&sRspBuf);
				exit(-1);
			}
			/*������Ϣ*/
			if(strlen(sErrMsg)>0)
			{
				iRetCode=addContent(&sRspBuf,"ERRMSG",sErrMsg,strlen(sErrMsg));
				if(iRetCode==-1)
				{
					TesLog(sLogFile,LOG_ERROR,RunLevel,"Add content ERRMSG error!\n");
	           		TCPCloseSock(iNewSock);
					infoDel(&sRspBuf);
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

			iRetCode=addContent(&sRspBuf,"RESMESSAGE",sTmpBuf,iRecvLen);
			if(iRetCode==-1)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"Add content RESMESSAGE error!\n");
				infoDel(&sRspBuf);
           		TCPCloseSock(iNewSock);
				exit(-1);
			}

			memset(sTmpBuf,0,sizeof(sTmpBuf));
			memcpy(sTmpBuf, sRspBuf+10, 10);
			iSendLen = atoi(sTmpBuf);
			TesLog(sLogFile,LOG_INFO,RunLevel,"��װ����Ӧ���ģ�����=[%d]��������=[%s]��������Ϣ=[%s]",iSendLen,sResult,sErrMsg);
    		iRetCode = TCPWriteSock(iNewSock, sRspBuf, iSendLen, TES_COMM_TIMEOUT);
        	if(iRetCode < 0)
        	{
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPWriteSock��������Ӧ���ĳ���: [%d]!", iRetCode);
            		TCPCloseSock(iNewSock);
            		infoDel(&sRspBuf);
            		exit(iRetCode);
        	}
			infoDel(&sRspBuf);

	        TCPCloseSock(iNewSock);
	        TesLog(sLogFile,LOG_INFO,RunLevel, "��������Ӧ���ĳɹ���ʵ�ʷ��س���[%d]",iRetCode);
			memset(sLogStr,0,sizeof(sLogStr));
			HexLogStr(sRspBuf,iSendLen,sLogStr);
			TesLog(sLogFile,LOG_DEBUG,RunLevel,sLogStr);
			UnloadSecLib();
	        exit(0);
	        
	}/*end for*/

}		/*end main*/


int GetConfig()
{
	char *p;
	memset(cfgFile,0,sizeof(cfgFile));
	sprintf(cfgFile,"%s/agent/etc/CltMQ.cfg",getenv("HOME"));
	
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
		if(RunLevel<0)
			return(-12101);			
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

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQMNAME");
	if(p==NULL)
		return(-12003);			
	else
	{
		strip(p);
		memset(MQName,0,sizeof(MQName));
		strcpy(MQName,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQCHANNEL");
	if(p==NULL)
		return(-12004);			
	else
	{
		strip(p);
		memset(MQChannel,0,sizeof(MQChannel));
		strcpy(MQChannel,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQREQ");
	if(p==NULL)
		return(-12005);			
	else
	{
		strip(p);
		memset(MQReq,0,sizeof(MQReq));
		strcpy(MQReq,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQRSP");
	if(p==NULL)
		return(-12010);			
	else
	{
		strip(p);
		memset(MQRsp,0,sizeof(MQRsp));
		strcpy(MQRsp,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","CCSID");
	if(p==NULL)
		return(-12011);			
	else
	{
		strip(p);
		memset(MQCcsid,0,sizeof(MQCcsid));
		strcpy(MQCcsid,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQSVRIP");
	if(p==NULL)
		return(-12012);			
	else
	{
		strip(p);
		memset(MQSvrIP,0,sizeof(MQSvrIP));
		strcpy(MQSvrIP,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQCOMMTYPE");
	if(p==NULL)
		return(-12013);			
	else
	{
		strip(p);
		memset(MQCommType,0,sizeof(MQCommType));
		strcpy(MQCommType,p);
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQLSRPORT");
	if(p==NULL)
		return(-12014);			
	else
	{
		strip(p);
		iMQPort=atoi(p);
		if(iMQPort<100)
			return(-12015);			
	}

	p=NULL;
	p=ReadIni(cfgFile,"MQ","MQTIMEOUT");
	if(p==NULL)
		return(-12016);			
	else
	{
		strip(p);
		iMQTimeOut=atoi(p);
		if(iMQTimeOut<=0)
			return(-12017);			
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


/*����MQ*/
int	ConnectQueue(MQHCONN *pqm,MQHOBJ *preq,MQHOBJ *prsp)
{
	MQOD     odg = {MQOD_DEFAULT};
	MQOD     odp = {MQOD_DEFAULT};

	MQLONG   o_options;
	MQLONG   compcode;
	MQLONG   reason;

	TesLog(sLogFile,LOG_INFO,RunLevel,"Begin connect mq,MQSERVER=[%s],CCSID=[%s]",getenv("MQSERVER"),getenv("CCSID"));
	/*��Ӧ����*/
	strcpy(odg.ObjectName, MQRsp);
	/*�������*/
	strcpy(odp.ObjectName, MQReq);

	MQCONN(MQName, pqm, &compcode, &reason); 
	if (compcode == MQCC_FAILED)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"MQCONN [%s] ended with reason code %ld", MQName,reason);
		return reason;
	}

	o_options = MQOO_INPUT_AS_Q_DEF + MQOO_FAIL_IF_QUIESCING;

	MQOPEN(	*pqm, &odg, o_options, prsp, &compcode, &reason);
	if (compcode == MQCC_FAILED)   
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"MQOPEN Ended With reason Code %ld", reason);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Open Queue %s For Input Fail", odg.ObjectName);
		MQDISC(pqm, &compcode, &reason);
		return reason;
	}

	o_options=MQOO_OUTPUT+MQOO_BIND_NOT_FIXED+MQOO_FAIL_IF_QUIESCING;

	MQOPEN(	*pqm, &odp, o_options, preq, &compcode, &reason);
	if (compcode == MQCC_FAILED)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"MQOPEN Ended With reason Code %ld", reason);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Open Queue %s For Front Output Fail",odp.ObjectName);
		MQCLOSE(*pqm,preq, MQCO_NONE, &compcode,&reason);
		MQDISC(pqm, &compcode, &reason);
		return reason;
	}

	return(0);
}

/*�Ͽ���MQ������*/
int	DisConnectQueue(MQHCONN *pqm,MQHOBJ *preq,MQHOBJ *prsp)
{
	MQLONG   compcode;
	MQLONG   reason;
	MQCLOSE(*pqm,preq, MQCO_NONE, &compcode,&reason);
	MQCLOSE(*pqm,prsp, MQCO_NONE, &compcode,&reason);
	MQDISC(pqm, &compcode, &reason);
	return(0);
}

/*��MQ�������ȡ��Ϣ*/
int	RcvMsgFromMQ(MQHCONN *pqm,MQHOBJ *prsp,MQMD *pmdg,char *MQData,int *MQDataSize)
{
	MQLONG   compcode;
	MQLONG   reason;
	MQGMO    gmo = {MQGMO_DEFAULT};

	int	iRetCode;

	memcpy(pmdg->MsgId, MQMI_NONE, sizeof(pmdg->MsgId));
	memcpy(pmdg->CorrelId, MQCI_NONE, sizeof(pmdg->CorrelId));

	gmo.Options = MQGMO_WAIT + MQGMO_FAIL_IF_QUIESCING;
/*
        gmo.WaitInterval = MQWI_UNLIMITED;
*/

	gmo.WaitInterval = iMQTimeOut*1000;

	MQGET(*pqm,*prsp,pmdg, &gmo,TES_MQ_MAX_SIZE, MQData, (MQLONG *)MQDataSize,&compcode,&reason);
	if(reason == 2071 )
	{
		return reason;
	}
	if (compcode == MQCC_FAILED)
	{
		return reason;
	}
	if (*MQDataSize == 0) 
		return -1;
	
	return(0);
}

/*������Ϣ��MQ������*/
int	SndMsgToMQ(MQHCONN *pqm,MQHOBJ *preq,MQMD *pmdp,char *MQData,int MQDataSize)
{
	MQLONG	compcode;
	MQLONG	reason;
	MQPMO	pmo = {MQPMO_DEFAULT};

	pmdp->Expiry = 20000;
	pmdp->Version = MQMD_VERSION_1;
/*
	memcpy(pmdp->MsgId, MQMI_NONE, sizeof(pmdp->MsgId) );
*/
	MQPUT(*pqm,*preq,pmdp,&pmo,MQDataSize,MQData,&compcode,&reason);
	if (compcode == MQCC_FAILED)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Put Data To MQ Fail,RC=%d",reason);
		return reason;
	}

	return 0;
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
