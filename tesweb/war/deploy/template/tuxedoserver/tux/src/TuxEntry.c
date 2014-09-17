#include 	<atmi.h>
#include 	<fml32.h>
#include 	<sys/time.h>
#include 	<time.h>
#include 	<stdio.h>
#include 	<ctype.h>
#include 	<stdlib.h>
#include 	<errno.h>
#include 	<sys/types.h>
#include 	<sys/ipc.h>
#include 	<sys/shm.h>
#include	<string.h>
#include	<unistd.h>
#include <dlfcn.h>

#include	"Adapter2Tes.h"
#include	"sim.h"
#include	"Util.h"
extern int 	frerrno;
extern int 	errno;

void  *TesSecLib;
int   (*TesSecFuncIn)();
int   (*TesSecFuncOut)();
const char *dlError;

char sLogFile[256];

#if defined(__STDC__) || defined(__cplusplus)
int tpsvrinit(int argc, char *argv[])
#else
int tpsvrinit(argc, argv)
int argc;
char **argv;
#endif
{
	int ret;
	FILE *fp;
	int iRetCode;
	char sTmpBuf[65536];
	
	memset(EtcFile,0x00,sizeof(EtcFile));
	

	/*取系统日期*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "%s/tux/log/TUXadapter.log.%s",getenv("HOME"),sTmpBuf);
	
	TesLog(sLogFile,LOG_INFO,3,"##############################################################################");
	TesLog(sLogFile,LOG_INFO,3,"TUXEDO Adapter starting ,pid=[%ld]。",getpid());
		
	sprintf(EtcFile,"%s/tux/etc/TUXadapter.cfg",getenv("HOME"));

	ret=InitGlobalVal();
	if (ret<0)
	{
		TesLog(sLogFile,LOG_ERROR,3,"初始化失败[%d]",ret);
		return(-1);	
	}	
	/*注册*/
	RegBuf=messageInit();
	if(RegBuf==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"初始化注册消息失败！");
		return(-1);	
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
		return(-1);	
	}
	
	ret=reg2tes(&RegBuf,&ConfigBuf);
	if(ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"向核心注册失败!");
		infoDel(&RegBuf);
		infoDel(&ConfigBuf);
		return(-1);	
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
		return(-1);	
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
			return(-1);	
		}
		infoDel(&ConfigBuf);
		printf("向核心注册失败，%s。\n",sTmpBuf);
		return(-1);
	}        	
	
	
	memset(Data,0x00,sizeof(Data));
	DataSize=0;
	ret=readContent(ConfigBuf,"CONFIGINFO",Data,(int *)&DataSize);
	infoDel(&ConfigBuf);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent CONFIGINFO failed");
		return(-1);	
	}
	if(DataSize>0)
	{
		/*更新配置文件*/
		fp=fopen(EtcFile,"w+");
		if(fp==NULL)
		{
			TesLog(sLogFile,LOG_ERROR,3,"注册更新适配器配置文件失败!");
			return(-1);	
		}
		fprintf(fp,"%s",Data);
		fflush(fp);
		fclose(fp);
	}
	else
	{
		printf("注册失败，下载适配器配置失败!\n");
		TesLog(sLogFile,LOG_ERROR,3,"注册失败，下载适配器配置失败!");
		return(-1);
	}
	TesLog(sLogFile,LOG_INFO,RunLevel,"更新配置文件完成，file len=[%d]",DataSize);
	
	/*加载安全库*/
	iRetCode=LoadSecLib();
	if(iRetCode<0)
	{
		printf("Load Sec library error [%d],errmgs=[%s]", iRetCode,dlError);
		TesLog(sLogFile,LOG_ERROR,3,"Load Sec library error [%d],errmgs=[%s]", iRetCode,dlError);
		return(-1);	
	}
	else
	{
	if((DynamicIn[0]=='1') || (DynamicOut[0]=='1'))
		TesLog(sLogFile,LOG_INFO,RunLevel,"Load Sec library [%s] OK", DynamicName);
	}        
	

}

void tpsvrdone(void)
{
	UnloadSecLib();
	return;

}

#ifdef __cplusplus
extern "C"
#endif
void
#if defined(__STDC__) || defined(__cplusplus)
TuxEntry(TPSVCINFO *rqst)
#else
TuxEntry(rqst)
TPSVCINFO *rqst;
#endif
{
	char *buf;
	int ret;
	char *p;
	FILE *fp;
	
	char SecBuf[65536];
	int iSecBufLen;
	int iRetCode;

	
	/*记录时间戳*/
	gettimeofday (&Tpbegin, NULL);
	
	
	/*Allocate memory for CARRAY type-buffer*/
	buf = tpalloc("CARRAY", NULL, 65535);
	if (buf == NULL)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Server:Tpalloc Tuxedo Buf fail!");
		tpreturn(TPFAIL, -1, NULL, 0L, 0L);
	}
	
	XmlDataSize=0;
    
	memset(XmlData,0x00,sizeof(XmlData));
	memcpy(XmlData,rqst->data,rqst->len);
	XmlDataSize=rqst->len;	
	

	
	TesLog(sLogFile,LOG_INFO,RunLevel,"Recieve len=[%d], message [%s]",XmlDataSize,XmlData);
	
	/*调用用户自定义安全处理函数*/
	if(DynamicIn[0]=='1')
	{
		memset(SecBuf,0,sizeof(SecBuf));
		iSecBufLen=0;
		iRetCode=(*TesSecFuncIn)(XmlData,XmlDataSize,SecBuf,&iSecBufLen);
		if(iRetCode!=0)
		{
			TesLog(sLogFile,LOG_ERROR,RunLevel,"Enc or dec request message error[%d]",iRetCode);
			tpreturn(TPFAIL,-1,NULL,0L,0L);
		}
		else
		{
			TesLog(sLogFile,LOG_INFO,RunLevel,"Enc or dec request message OK,original msg len=[%d] and now[%d]",XmlDataSize,iSecBufLen);	
			memset(XmlData,0,sizeof(XmlData));
			memcpy(XmlData,SecBuf,iSecBufLen);
			XmlDataSize=iSecBufLen;
		}
	}
		
	/*调用API和核心层交互*/
	ret=ApiDeal();
	if (ret < 0 ) 
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"和核心交互错误");
		tpreturn(TPFAIL,-1,NULL,0L,0L);
	}
	TesLog(sLogFile,LOG_INFO,RunLevel,"和核心层交互成功");
	/*调用用户自定义安全处理函数*/
	if(DynamicOut[0]=='1')
	{
		memset(SecBuf,0,sizeof(SecBuf));
		iSecBufLen=0;
		iRetCode=(*TesSecFuncOut)(XmlData,XmlDataSize,SecBuf,&iSecBufLen);
		if(iRetCode!=0)
		{
			TesLog(sLogFile,LOG_ERROR,RunLevel,"Enc or dec response message error[%d]",iRetCode);
			tpreturn(TPFAIL,-1,NULL,0L,0L);
		}
		else
		{
			TesLog(sLogFile,LOG_INFO,RunLevel,"Enc or dec request message OK,original msg len=[%d] and now[%d]",XmlDataSize,iSecBufLen);	
			memset(XmlData,0,sizeof(XmlData));
			memcpy(XmlData,SecBuf,iSecBufLen);
			XmlDataSize=iSecBufLen;
		}
	}
  
	
	/*记录时间戳+模拟延时*/
	ret=TimeDeal();
	if (ret <0)			
		TesLog(sLogFile,LOG_ERROR,RunLevel,"模拟延时处理失败");
	
	/*释放资源*/
	infoDel(&sendbuf);
	infoDel(&rcv);	
			
	memset(buf,0x00,sizeof(buf));		
	memcpy(buf,XmlData,XmlDataSize);
	TesLog(sLogFile,LOG_INFO,RunLevel,"Response len=[%d],message=[%s]",XmlData);
	tpreturn(TPSUCCESS, 0, buf, XmlDataSize, 0);
}


int	InitGlobalVal()
{
	char *p;
	

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

/*时间戳处理+模拟延时*/	
int TimeDeal()
{
	long sleeptime;
	long timecost;
	
	gettimeofday(&Tpend,NULL);
	timecost=(Tpend.tv_sec - Tpbegin.tv_sec)*1000  + (Tpend.tv_usec - Tpbegin.tv_usec)/1000;
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"timecost is [%d]",timecost);
	
	sleeptime = (delaytime-timecost)*1000;
	TesLog(sLogFile,LOG_DEBUG,RunLevel,"sleeptime is [%d]",sleeptime);
	if ( sleeptime<=0 )	return(0);
	usleep(sleeptime);
	
	return(0);

}

/*API和核心层交互*/
int	ApiDeal()
{
	/*初始化API参数--发送*/
	char smsg[] = "REQMESSAGE";
	/*初始化API参数--接收*/
	char rmsg[]="RESMESSAGE",rdt[]="DELAYTIME";
	int ret,len;
	
	sendbuf=messageInit();
	if (sendbuf==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,3,"sendbuf 空间创建失败");
		return(-1);
	}
	
	
	
	ret=addContent(&sendbuf,smsg,XmlData,XmlDataSize);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"AddContent XmlData failed");
		return(-1);
	}


	ret=sendContent(&sendbuf,&rcv);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"Send XmlData to core failed");
		return(-1);
	}
  
	
	XmlDataSize=0;
	memset(XmlData,0x00,sizeof(XmlData));
	ret=readContent(rcv,rmsg,XmlData,&XmlDataSize);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent XmlData failed");
		return(-1);
	}
	
	len=0;
	memset(dtime,0x00,sizeof(dtime));
	ret=readContent(rcv,rdt,dtime,&len);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,3,"ReadContent delaytime failed");
		return(-1);
	}
	TesLog(sLogFile,LOG_INFO,RunLevel,"dtime is [%s]",dtime);
	delaytime=atoi(dtime);

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
