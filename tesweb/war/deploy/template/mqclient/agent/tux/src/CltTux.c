/******************************************************************************
  模块名称：CltTux.c
  模块功能：客户端Tuxedo适配器诶
  相关模块: 
  作者姓名：刘志军
  编写日期：20010/01/06
  维护记录：
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

	
	/*取系统日期*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "%s/agent/log/CltTux.log.%s",getenv("HOME"),sTmpBuf);

	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter starting ,pid=[%ld]。",getpid());
	
	
	/*读取配置文件，获得核心参数*/
	iRetCode=GetConfig();
	if(iRetCode!=0)
	{
		printf("GetConfig error![%d]\n",iRetCode);
		TesLog(sLogFile,LOG_ERROR,3,"GetConfig error![%d]\n",iRetCode);
		exit(-1);
	}

	/*设置环境变量*/
	setenv("CHANNELNAME",ChannelName,1);
	setenv("TESADDR",TesAddr,1);
	setenv("ADAPTERCONFIG",cfgFile,1);


	/*注册*/
	RegBuf=messageInit();
	if(RegBuf==NULL)
	{
			TesLog(sLogFile,LOG_ERROR,3,"初始化注册消息失败！\n");
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

	
	/*向核心注册*/
	gettimeofday (&Tpbegin, NULL);
	iRetCode=reg2tes(&RegBuf,&ConfigBuf);
	if(iRetCode<0)
	{
		printf("向核心注册失败,iRetCode=[%d]!\n",iRetCode);
		TesLog(sLogFile,LOG_ERROR,3,"向核心注册失败,iRetCode=[%d]!\n",iRetCode);
		infoDel(&RegBuf);
		infoDel(&ConfigBuf);
		exit(-1);
	}
	infoDel(&RegBuf);

	/*读取返回码*/
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
	/*返回码非0，标识不成功*/
	if(sTmpBuf[0]!='0')
	{
		/*读取错误信息*/
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
		printf("向核心注册失败，%s。\n",sTmpBuf);
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
	/*更新配置文件*/
	if(DataSize>0)
	{
		fp=fopen(cfgFile,"w");
		if(fp==NULL)
		{
			printf("注册更新适配器配置文件失败!\n");
			TesLog(sLogFile,LOG_ERROR,3,"注册更新适配器配置文件失败!\n");
			exit(-1);
		}
		fprintf(fp,"%s",sTmpBuf);
		fflush(fp);
		fclose(fp);
	}
	else
	{
		printf("注册失败，下载适配器配置失败!\n");
		TesLog(sLogFile,LOG_ERROR,3,"注册失败，下载适配器配置失败!\n");
		exit(-1);
	}

	/*重新读取更新后的配置*/
	iRetCode=GetConfig();
	if(iRetCode!=0)
	{
		printf("Get new Config error![%d]\n",iRetCode);
		TesLog(sLogFile,LOG_ERROR,3,"Get new Config error![%d]\n",iRetCode);
		exit(-1);
	}

	setenv("WSNADDR",WsnAddr,1);

	/*启动守护进程*/	
	DeamonStart();
    			
	
	memset(sCurTime,0,sizeof(sCurTime));
	GetCurrentDateTime2(sCurTime);
	

	
	/*启动TCP服务*/
	/*创建socket*/
	iSock = TCPCreatSock();
	if(iSock < 0)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPCreatSock Fail, Errno: [%d]!\n", iSock);
		exit(iSock);
	}
	/*绑定端口*/
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
	
	/*加载安全库*/
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
        	/*父进程continue继续监听*/
        	else if(iPid > 0)
        	{
            		TCPCloseSock(iNewSock);
            		continue;
        	}
			/*iPid=0则为子进程，往下进行转发处理*/
			        
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



			/*接收原始请求报文*/        
        	memset(sRecvBuf, 0, sizeof(sRecvBuf));
        	iRecvLen = TCPReadSock(iNewSock, sRecvBuf,sizeof(sRecvBuf), TES_COMM_TIMEOUT);
       		if(iRecvLen < 0)
        	{
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"Read from core fail, Errno: [%d]!", iRecvLen);
            		TCPCloseSock(iNewSock);
            		exit(iRetCode);
        	}

			TesLog(sLogFile,LOG_INFO,RunLevel,"Recieve msg from core,len=[%d]:",iRecvLen);	        	
		  	/*记通讯报文16进制日志*/
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

			/*调用用户自定义安全处理函数*/
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
			以下为报文转发
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
			        TesLog(sLogFile,LOG_ERROR,RunLevel,"TuxSendBuf申请内存空间失败!");
            		TCPCloseSock(iNewSock);
			        exit(-1);
			}
			
			TuxRcvBuf=(char*)tpalloc("CARRAY", NULL, 65536);
			if(TuxRcvBuf == NULL)
			{
			        tpfree(TuxSendBuf);
			        TesLog(sLogFile,LOG_ERROR,RunLevel,"TuxRcvBuf申请内存空间失败!");
            		TCPCloseSock(iNewSock);
			        exit(-1);
			}
			memset(TuxSendBuf,0,sizeof(TuxSendBuf));
			memcpy(TuxSendBuf, sTmpBuf, iSendLen);
			
			iRecvLen=0;
			iRetCode = tpcall(AgentTuxService, (char *)TuxSendBuf, (long)(iSendLen), (char **)&TuxRcvBuf, (long *)&iRecvLen, (long)0);
			/*转发出错，生成错误报文*/
			if(iRetCode<0)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"tpcall error [%d]!",tperrno);
				memset(sResult,0,sizeof(sResult));
				memset(sErrMsg,0,sizeof(sErrMsg));
				strcpy(sResult,"TPCERR");
				sprintf(sErrMsg,"tpcall error [%d]",tperrno);
				/*错误消息体设为空*/
				iRecvLen=0;
				memset(sTmpBuf,0,sizeof(sTmpBuf));				
				
			}
			else
			{
				TesLog(sLogFile,LOG_INFO,RunLevel,"tuxedo转发消息完成，返回应答长度[%d]:",iRecvLen);
				memset(sResult,0,sizeof(sResult));
				strcpy(sResult,"000000");
				memset(sErrMsg,0,sizeof(sErrMsg));
				memset(sTmpBuf,0,sizeof(sTmpBuf));
				memcpy(sTmpBuf,TuxRcvBuf,iRecvLen);
			  	/*记录应答16进制报文*/
			  	memset(sLogStr,0,sizeof(sLogStr));
				HexLogStr(sTmpBuf,iRecvLen,sLogStr);
				TesLog(sLogFile,LOG_DEBUG,RunLevel,sLogStr);
				
			}

			tpfree(TuxSendBuf);
			tpfree(TuxRcvBuf);
		


/*************************************************************************
			转发请求报文完毕，开始返回应答报文给核心
*************************************************************************/

			/*准备相应报文*/
			sRspBuf=messageInit();
			if(sRspBuf==NULL)
			{
				TesLog(sLogFile,LOG_ERROR,3,"初始化相应报文失败！\n");
           		TCPCloseSock(iNewSock);
				exit(-1);
			}
			/*错误码*/
			iRetCode=addContent(&sRspBuf,"RESULT",sResult,strlen(sResult));
			if(iRetCode==-1)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"Add content RESULT error!\n");
				infoDel(&sRspBuf);
           		TCPCloseSock(iNewSock);
				exit(-1);
			}
			/*错误信息*/
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
			/*置应答消息体*/		
			/*如果应答消息长度为0或者返回码不是000000，则应答消息固定为10个空格*/			
			if((iRecvLen<=0)||(strcmp(sResult,"000000")!=0))					
			{
				memset(sTmpBuf,0,sizeof(sTmpBuf));
				strcpy(sTmpBuf,"          ");
				iRecvLen=strlen(sTmpBuf);
			}
			else
			{
				/*调用用户自定义安全处理函数*/
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
            		TesLog(sLogFile,LOG_ERROR,RunLevel,"TCPWriteSock返回最终应答报文出错: [%d]!", iRetCode);
            		TCPCloseSock(iNewSock);
            		infoDel(&sRspBuf);
            		exit(iRetCode);
        	}
			infoDel(&sRspBuf);

			TesLog(sLogFile,LOG_INFO,RunLevel,"组装最终应答报文，长度=[%d]，错误码=[%s]，错误信息=[%s]",iSendLen,sResult,sErrMsg);
			
	        TCPCloseSock(iNewSock);

	        TesLog(sLogFile,LOG_INFO,RunLevel, "返回最终应答报文成功，返回长度[%d]",iRetCode);
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

	/*****安全配置信息*******/
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
	/*加载安全库*/
	if((DynamicIn[0]=='1') || (DynamicOut[0]=='1'))
	{
		TesSecLib=dlopen(DynamicName,RTLD_LAZY);
		dlError=dlerror();
		if(dlError!=NULL)
		return -12110;
	}
	else
		return 0;
	/*请求报文处理标志0：无安全处理，1：需安全处理*/
	if(DynamicIn[0]=='1')
	{
		TesSecFuncIn=dlsym(TesSecLib,"TesAdptSecFunc_In");
		dlError=dlerror();
		if(dlError!=NULL)
		return -12112;
	}

	/*请求报文处理标志0：无安全处理，1：需安全处理*/
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
