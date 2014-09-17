#include <string.h>
#include <unistd.h>
#include <dlfcn.h>
#include "sim.h"
#include "Adapter2Tes.h"
#include "Util.h"

MQHCONN  hcon;                    /* connection handle         */
MQHOBJ   hobjg;                   /* object handle             */
MQHOBJ   hobjp;                   /* object handle             */
MQMD     mdg = {MQMD_DEFAULT};    /* Message Descriptor        */
MQMD     mdp = {MQMD_DEFAULT};    /* Message Descriptor        */

void  *TesSecLib;
int   (*TesSecFuncIn)();
int   (*TesSecFuncOut)();
const char *dlError;


/*Ӧ������������*/
char **RspMsg;
int iPackNum;

#if defined(__STDC__) || defined(__cplusplus)
main(int argc, char *argv[])
#else
main(argc, argv)
int argc;
char *argv[];
#endif
{
	int	ret;
	char	msgidstr[20];
	
	/*��ʼ��*/
	int len_ss=0,len_sta=0,len_sd=0,len_slog=0;
	int	len_rd=0,len_rdt=0,len_rs=0;
	char SecBuf[65536];
	int iSecBufLen;
	int iRetCode;
	int i,j;
	int iMulMsgFlag;
	char sTmpBuf[1024];


	signal(SIGHUP,SIG_IGN);
	signal(SIGINT,SIG_IGN);
	signal(SIGTSTP,SIG_IGN);
	signal(SIGABRT,SIG_IGN);
	signal(SIGQUIT,SIG_IGN);

	DealNO=atoi(argv[1]);
	
	memset(EtcFile,0x00,sizeof(EtcFile));


	/*ȡϵͳ����*/
	memset(sTmpBuf,0,sizeof(sTmpBuf));
	GetCurrentDateTime(sTmpBuf);
	sTmpBuf[8]='\0';
	memset(sLogFile,0,sizeof(sLogFile));
	sprintf(sLogFile, "../log/MQ%02d.log.%s",DealNO,sTmpBuf);
		
	sprintf(EtcFile,"../etc/MQadapter.cfg");

	CurPid=getpid();

	ret=InitGlobalVal();
	if (ret <0 ) return(-1);
	
	len_ss=strlen(sysid);
	len_sta=strlen(taskid);
	
	/*���ӵ�MQ*/
	ret=ConnectQueue(&hcon,&hobjg,&hobjp);
	if (ret < 0) return(-1);

	/*���ذ�ȫ��*/
	iRetCode=LoadSecLib();
	if(iRetCode<0)
	{
		printf("Load Sec library error [%d],errmgs=[%s]", iRetCode,dlError);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Load Sec library error [%d],errmgs=[%s]", iRetCode,dlError);
		return(iRetCode);
	}
	else
	{
		if( (DynamicIn[0]=='1') || (DynamicOut[0]=='1') )
			TesLog(sLogFile,LOG_INFO,RunLevel,"Load Sec library [%s] OK", DynamicName);
	}
	
	
	TesLog(sLogFile,LOG_INFO,RunLevel,"Waiting Request Data Now....");	
	while(1)
	{
		DataSize=0;
		XmlDataSize=0;
    

		memset(Data,0x00,sizeof(Data));

		/*��鹲���ڴ�,�ж��Ǽ������л���ˢ�²��������л��˳�*/
		ret=CheckShmCmd();
		if ( ret == 1 )  break;
		else if ( ret == 2 )
		{
			/*��Ϊˢ����ȫ�ֱ���,������Ҫ��������MQ*/
			DisConnectQueue(&hcon,&hobjg,&hobjp);
			ret=ConnectQueue(&hcon,&hobjg,&hobjp);
			if (ret < 0) break;
		}
		else if ( ret < 0 ) break;
		
	
		/*��ȡ��������,���ݴ洢��Data,��СΪDataSize*/
		ret=RcvMsgFromMQ(&hcon,&hobjg,&mdg);
		if ( ret != 0) continue;

		/*��¼ʱ���*/
		gettimeofday(&Tpbegin,NULL);
		
		memset(XmlData,0x00,sizeof(XmlData));
		XmlDataSize=DataSize;
		memcpy(XmlData,Data,sizeof(XmlData));

		GetBinStr(mdg.MsgId,sizeof(mdg.MsgId),msgidstr);
	  /*InfLog("Message:[%s] DealWith Begin",msgidstr);*/

/*
		InfLog("Get From MQ  Data[%s]",EAIBData+4);
*/

	
		/*�����û��Զ��尲ȫ������*/
		if(DynamicIn[0]=='1')
		{
			memset(SecBuf,0,sizeof(SecBuf));
			iSecBufLen=0;
			iRetCode=(*TesSecFuncIn)(XmlData,XmlDataSize,SecBuf,&iSecBufLen);
			if(iRetCode!=0)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"Enc or dec request message error[%d]",iRetCode);
				continue;
			}
           	else
           	{
				TesLog(sLogFile,LOG_INFO,RunLevel,"Enc or dec request message OK,original msg len=[%d] and now[%d]",XmlDataSize,iSecBufLen);	
				memset(XmlData,0,sizeof(XmlData));
				memcpy(XmlData,SecBuf,iSecBufLen);
				XmlDataSize=iSecBufLen;
			}
		}

		/*����API�ͺ��Ĳ㽻��*/
		ret=ApiDeal();
		if (ret < 0 ) 
		{
			TesLog(sLogFile,LOG_ERROR,RunLevel,"�ͺ��Ľ�������[%d]",ret);
			continue;
		}
		TesLog(sLogFile,LOG_INFO,RunLevel,"�ͺ��Ĳ㽻���ɹ�");

		/*ʱ�������+ģ����ʱ*/
		
		ret=TimeDeal();
		if (ret <0)
				TesLog(sLogFile,LOG_ERROR,RunLevel,"ʱ�䴦�����");
		
				
		/*�ͷ���Դ*/
		infoDel(&sendbuf);
		iMulMsgFlag=0;
		for(i=0;i<iPackNum;i++)
		{	
			XmlDataSize=0;
			memset(XmlData,0x00,sizeof(XmlData));
			ret=readContent(RspMsg[i],"RESMESSAGE",XmlData,&XmlDataSize);
			if (ret==-1)
			{
				TesLog(sLogFile,LOG_ERROR,RunLevel,"ReadContent XMLData failed");
				iMulMsgFlag=-1;
				break;
			}
			TesLog(sLogFile,LOG_INFO,RunLevel,"Read from core msg is [%s]",XmlData);	

			/*�����û��Զ��尲ȫ������*/
			if(DynamicOut[0]=='1')
			{
				memset(SecBuf,0,sizeof(SecBuf));
				iSecBufLen=0;
				iRetCode=(*TesSecFuncOut)(XmlData,XmlDataSize,SecBuf,&iSecBufLen);
				if(iRetCode!=0)
				{
						TesLog(sLogFile,LOG_ERROR,RunLevel,"Enc or dec response message error[%d]",iRetCode);
						iMulMsgFlag=-1;	           	
			           	break;
				}
				else
				{
					TesLog(sLogFile,LOG_INFO,RunLevel,"Enc or dec request message OK,original msg len=[%d] and now[%d]",XmlDataSize,iSecBufLen);	
					memset(XmlData,0,sizeof(XmlData));
					memcpy(XmlData,SecBuf,iSecBufLen);
					XmlDataSize=iSecBufLen;
				}
			}/*end for*/
			
			if(iMulMsgFlag<0)
			{
				for(j=0;j<iPackNum;j++)
					free(RspMsg[j]);
				free(RspMsg);	           	
				break;
			}
			
			/*�������ݵ�MQ*/
			
		
			memcpy(mdp.MsgId, mdg.MsgId, sizeof(mdp.MsgId) );
			memcpy(mdp.CorrelId, mdg.MsgId, sizeof(mdp.CorrelId) );
			ret=SndMsgToMQ(&hcon,&hobjp,&mdp);
			if (ret <0)
				TesLog(sLogFile,LOG_ERROR,RunLevel,"Send Msg To MQ FAILED");
	
			TesLog(sLogFile,LOG_INFO,RunLevel,"Send Msg to MQ OK");
		}

	}
	for(j=0;j<iPackNum;j++)
		free(RspMsg[j]);
	free(RspMsg);
	
	UnloadSecLib();

	return(0);
}


/*����MQ*/
int	ConnectQueue(MQHCONN *pqm,MQHOBJ *pinq,MQHOBJ *poutq)
{
	MQOD     odg = {MQOD_DEFAULT};
	MQOD     odp = {MQOD_DEFAULT};

	MQLONG   o_options;
	MQLONG   compcode;
	MQLONG   reason;

	strcpy(odg.ObjectName, SimReqQ);
	strcpy(odp.ObjectName, SimResQ);

	//MQCONN(	SimQM, pqm, &compcode, &reason); 
	MQCONN(	SimQM, pqm, &compcode, &reason); 
	if (compcode == MQCC_FAILED)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"MQCONN ended with reason code %ld", reason);
		return(-1);
	}

	o_options = MQOO_INPUT_AS_Q_DEF + MQOO_FAIL_IF_QUIESCING;

	MQOPEN(	*pqm, &odg, o_options, pinq, &compcode, &reason);
	if (compcode == MQCC_FAILED)   
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"MQOPEN Ended With reason Code %ld", reason);
     		TesLog(sLogFile,LOG_ERROR,RunLevel,"Open Queue %s For Input Fail", odg.ObjectName);
		MQDISC(pqm, &compcode, &reason);
		return(-1);
	}

	o_options=MQOO_OUTPUT+MQOO_BIND_NOT_FIXED+MQOO_FAIL_IF_QUIESCING;

	MQOPEN(	*pqm, &odp, o_options, poutq, &compcode, &reason);
	if (compcode == MQCC_FAILED)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"MQOPEN Ended With reason Code %ld", reason);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Open Queue %s For Front Output Fail",odp.ObjectName);
		MQCLOSE(*pqm,pinq, MQCO_NONE, &compcode,&reason);
		MQDISC(pqm, &compcode, &reason);
		return(-1);
	}

	return(0);
}

/*�Ͽ���MQ������*/
int	DisConnectQueue(MQHCONN *pqm,MQHOBJ *pinq,MQHOBJ *poutq)
{
	MQLONG   compcode;
	MQLONG   reason;
	MQCLOSE(*pqm,pinq, MQCO_NONE, &compcode,&reason);
	MQCLOSE(*pqm,poutq, MQCO_NONE, &compcode,&reason);
	MQDISC(pqm, &compcode, &reason);
	return(0);
}

/*��MQ�������ȡ��Ϣ*/
int	RcvMsgFromMQ(MQHCONN *pqm,MQHOBJ *pinq,MQMD *pmdg)
{
	MQLONG   compcode;
	MQLONG   reason;
	MQGMO    gmo = {MQGMO_DEFAULT};

	int	ret=0;


	DataSize=0;

	memcpy(pmdg->MsgId, MQMI_NONE, sizeof(pmdg->MsgId));
	memcpy(pmdg->CorrelId, MQCI_NONE, sizeof(pmdg->CorrelId));

	gmo.Options = MQGMO_WAIT + MQGMO_FAIL_IF_QUIESCING;
/*
        gmo.WaitInterval = MQWI_UNLIMITED;
*/

        gmo.WaitInterval = MQFetchTimeOut*1000;

	MQGET(*pqm,*pinq,pmdg, &gmo, 
		MAXDATASIZE, Data, (MQLONG *)&DataSize, 
		&compcode, &reason);
        if(reason == 2071 ){
                sprintf(Msg,"��MQ��ȡ���ݳ��� reason code:2071!");
                TesLog(sLogFile,LOG_ERROR,RunLevel,"Get Data From MQ Fail,RC= %d",reason);
                usleep(500*1000);
                return(1);
        }
	if (compcode == MQCC_FAILED)
	{
		 if (reason == 2033 )
                {
			/*
			InfLog("There is nothing received from MQ in %d seconds!",MQFetchTimeOut); 
			InfLog("Check share memory command following!"); 
			*/
                	XmlDataSize= 0;
                        return(1);
                } else if(reason == 2009)
                {
                       usleep(500*1000);
                       DisConnectQueue(&hcon,&hobjg,&hobjp);
                       TesLog(sLogFile,LOG_ERROR,RunLevel,"Get Data From MQ Fail RE CONNECT ,RC=%d",reason);
                       ret=ConnectQueue(&hcon,&hobjg,&hobjp);
                }
                else
		{
			sprintf(Msg,"��MQ��ȡ���ݳ���!");
			TesLog(sLogFile,LOG_ERROR,RunLevel,"Get Data From MQ Fail,RC=%d",reason);
			return(-1);
		}
	}
	if (DataSize == 0) return(1);
	
	return(0);
}

/*������Ϣ��MQ������*/
int	SndMsgToMQ(MQHCONN *pqm,MQHOBJ *poutq,MQMD *pmdp)
{
	MQLONG	compcode;
	MQLONG	reason;
	MQPMO	pmo = {MQPMO_DEFAULT};

	pmdp->Expiry = 20000;
	pmdp->Version = MQMD_VERSION_1;
/*
	memcpy(pmdp->MsgId, MQMI_NONE, sizeof(pmdp->MsgId) );
*/
	MQPUT(*pqm,*poutq,pmdp,&pmo,XmlDataSize,XmlData,&compcode,&reason);
	if (compcode == MQCC_FAILED)
	{
		sprintf(Msg,"��MQ��д���ݳ���!");
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Put Data To MQ Fail,RC=%d",reason);
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Data Size=[%d],Data=[%s]",DataSize,Data);
		return(-1);
	}

	return(0);
}


/*��¼ʱ���+ģ����ʱ*/	
int TimeDeal()
{
	long sleeptime;
	long timecost;
	gettimeofday(&Tpend,NULL);
	timecost=(Tpend.tv_sec - Tpbegin.tv_sec)*1000  + (Tpend.tv_usec - Tpbegin.tv_usec)/1000;
    

	sleeptime = (delaytime-timecost)*1000;

	if ( sleeptime<=0 )	return(0);
	usleep(sleeptime);
	
	return(0);	
}

/*API�ͺ��Ĳ㽻��*/
int	ApiDeal()
{
	/*��ʼ��API����--����*/
	char smsg[] = "REQMESSAGE";
	/*��ʼ��API����--����*/
	char rmsg[]="RESMESSAGE";
	int ret,len;

	sendbuf = messageInit();
	if (sendbuf==NULL)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"send �ռ䴴��ʧ��");
		return(-1);
	}
	
	ret=addContent(&sendbuf,smsg,XmlData,XmlDataSize);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"AddContent REQMESSAGE failed");
		return(-1);
	}
	ret=sendContentMul(&sendbuf,&RspMsg);
	if (ret<0)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"Send msg to core failed[%d]",ret);
		return(ret);
	}
	else
		/*ȫ�ֱ����ְ���*/
		iPackNum=ret;
  
	len=0;
/*	
	XmlDataSize=0;
	memset(XmlData,0x00,sizeof(XmlData));
	ret=readContent(rcv,rmsg,XmlData,&XmlDataSize);
	if (ret==-1)
	{
		ErrLog("ReadContent XMLData failed");
		return(-1);
	}
	InfLog("Read from core msg is [%s]",XmlData);	
*/
	memset(dtime,0x00,sizeof(dtime));
	ret=readContent(RspMsg[0],"DELAYTIME",dtime,&len);
	if (ret==-1)
	{
		TesLog(sLogFile,LOG_ERROR,RunLevel,"ReadContent delaytime failed[%s]",RspMsg[0]);
		return(-1);
	}
        TesLog(sLogFile,LOG_INFO,RunLevel,"dtime is [%s]",dtime);
        delaytime=atoi(dtime);
	
	
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
