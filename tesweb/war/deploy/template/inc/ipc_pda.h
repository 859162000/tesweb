#ifndef _IPC_PDA_H_
#define _IPC_PDA_H_
#define IPC_PDA_TXT_SIZE 16384
#define IPC_PDA_LENGTH   100

#define IPC_PDA_CREAT    0
#define IPC_PDA_REFER    1

#define IPC_PDA_WAIT    0
#define IPC_PDA_NO_WAIT 1



#define IPC_SEM_P			-1
#define IPC_SEM_V			1
#define IPC_SEM_SIGDISABLE		1
#define IPC_SEM_SIGENABLE		0
#define IPC_SEM_SEMNUM			1     /* The number of semaphore */
#define IPC_SEMWAIT      		1
#define IPC_SEMTHROUGHT  		0
#define   IPC_SEM_OK              	0
#define   IPC_SEM_ERR             	-999



typedef	struct 
{
   long lMsgType;
   char abyMsgTxt[IPC_PDA_TXT_SIZE];
   int  iNextRecord;
} MsgStruct;

typedef	struct 
{
   MsgStruct    amsgMsgStructArray[IPC_PDA_LENGTH+2];
   int iCurEmptyRecordRear;
   int iCurFullRecordRear;
   unsigned int iRecordCount;
} MsgQueue;

typedef	struct
{
   MsgQueue msgqMsgQueue;
   int      iMaxPdaBuffer;
   int      iShmID;
   int      iSemID;
} Pda;

Pda * IPC_PDA_InitPda(int iShmKeyID,int iSemKeyID,int iCreateMod);
int IPC_PDA_FreePda(Pda *pPda);
int IPC_PDA_DeLinkPda(Pda *pPda);
int IPC_PDA_PutMsgToPda(Pda *pPda,MsgStruct * pmsgInputMsg,int iFlag,int iTimeOut);

int IPC_PDA_GetMsgFromPda(Pda *pPda,MsgStruct * pmsgOutputMsg,long lMsgType,int iFlag,int iTimeOut);

int IPC_PDA_GetNotMsgFromPda(Pda *pPda,MsgStruct * pmsgOutputMsg,long lMsgType,int iFlag,int iTimeOut);
                          
#define IPC_SEM_INIT_SEMVALUE_ERR	-51001  /*  ��ʼ���źŵƳ���    */
#define IPC_SHM_ERROR_CANNOT_GET_SHM	-51002  /*  ���乲���ڴ����    */
#define IPC_SHM_ERROR_CANNOT_DESTROY_SHM	-51003  /*  ɾ�������ڴ����    */
#define IPC_SHM_ERROR_CANNOT_ATTACH_SHM	-51004  /*  ���Ṳ���ڴ����    */
#define IPC_SHM_ERROR_CANNOT_DETACH_SHM	-51005  /*  ɾ�������ڴ����    */
#define IPC_SEM_RECSIG	-51006  /*  �źŵƲ���ʱ�յ��ж��ź�    */
#define IPC_PDA_ERROR_SEMOP_ERR	-51011  /*  ����PDAʱ�źŵƳ���    */
#define IPC_PDA_ERROR_NO_DATA	-51012  /*  ��PDAʱ������    */
#define IPC_PDA_ERROR_BLOCKED	-51013  /*  дPDAʱPDA������    */
#define IPC_PDA_ERROR_SEMOP_RECSIG	-51014  /*  ����PDAʱ�յ��ж��ź�    */
#define IPC_PDA_ERROR_TIMEOUT	-51015  /*  ����дPDAʱ��ʱ    */
#define TSA_ERROR_SEMOP_ERR	-51016  /*  ����дTSAʱ�źŵƴ�    */
#define TSA_ERROR_NO_DATA	-51017  /*  TSA��������    */
#define TSA_ERROR_BLOCKED	-51018  /*  дTSAʱ��TSA������    */
#define TSA_ERROR_SEMOP_RECSIG	-51019  /*  TSA����ʱ�յ��ж��ź�    */
#define TSA_ERROR_TIMEOUT	-51020  /*  ����дTSA��ʱ    */

#endif
