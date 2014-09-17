#ifndef _CLTMQ_H_
#define _CLTMQ_H_

#define TES_COMM_TIMEOUT 30
#define	TES_MQ_MAX_SIZE 65536

char cfgFile[256];
char sLogFile[256];
int iPort;
int iTransferPort;
char sTransferIP[60];
char sResult[20];
char sErrMsg[512];

int	RunLevel;			/*系统运行级别 DEBUG(3) INF(2) ERROR(1) FATAL(0)*/
struct timeval Tpbegin,Tpend;

char    AgentTuxService[256];
char    WsnAddr[256];
char    CommType[20];
char    ChannelName[256];
char    SimType[10];
char    TesAddr[256];
char    AdapterAddr[256];
char    AdapterConfig[256];

char DynamicIn[20];
char DynamicOut[20];
char DynamicName[256];

char MQSvrIP[256];
int iMQPort;
int iMQTimeOut;
char MQChannel[256];
char MQReq[256];
char MQRsp[256];
char MQName[256];
char MQCcsid[16];
char MQCommType[32];

char MQConnStr[256];

/*MQ global var*/
MQHCONN  hcon;                    /* connection handle         */
MQHOBJ   hobj_req;                   /* object handle             */
MQHOBJ   hobj_rsp;                   /* object handle             */
MQMD     mdg = {MQMD_DEFAULT};    /* Message Descriptor        */
MQMD     mdp = {MQMD_DEFAULT};    /* Message Descriptor        */

int GetConfig();
int ConnectQueue(MQHCONN *pqm,MQHOBJ *pinq,MQHOBJ *poutq);
int DisConnectQueue(MQHCONN *pqm,MQHOBJ *pinq,MQHOBJ *poutq);
int RcvMsgFromMQ(MQHCONN *pqm,MQHOBJ *pinq,MQMD *pmdg,char *MQData,int *MQDataSize);
int SndMsgToMQ(MQHCONN *pqm,MQHOBJ *poutq,MQMD *pmdp,char *MQData,int MQDataSize);
int LoadSecLib();
int UnloadSecLib();
#endif
