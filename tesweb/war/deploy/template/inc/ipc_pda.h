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
                          
#define IPC_SEM_INIT_SEMVALUE_ERR	-51001  /*  初始化信号灯出错    */
#define IPC_SHM_ERROR_CANNOT_GET_SHM	-51002  /*  分配共享内存出错    */
#define IPC_SHM_ERROR_CANNOT_DESTROY_SHM	-51003  /*  删除共享内存出错    */
#define IPC_SHM_ERROR_CANNOT_ATTACH_SHM	-51004  /*  连结共享内存出错    */
#define IPC_SHM_ERROR_CANNOT_DETACH_SHM	-51005  /*  删除共享内存出错    */
#define IPC_SEM_RECSIG	-51006  /*  信号灯操作时收到中断信号    */
#define IPC_PDA_ERROR_SEMOP_ERR	-51011  /*  操作PDA时信号灯出错    */
#define IPC_PDA_ERROR_NO_DATA	-51012  /*  读PDA时无数据    */
#define IPC_PDA_ERROR_BLOCKED	-51013  /*  写PDA时PDA数据满    */
#define IPC_PDA_ERROR_SEMOP_RECSIG	-51014  /*  操作PDA时收到中断信号    */
#define IPC_PDA_ERROR_TIMEOUT	-51015  /*  读或写PDA时超时    */
#define TSA_ERROR_SEMOP_ERR	-51016  /*  读或写TSA时信号灯错    */
#define TSA_ERROR_NO_DATA	-51017  /*  TSA中无数据    */
#define TSA_ERROR_BLOCKED	-51018  /*  写TSA时，TSA数据满    */
#define TSA_ERROR_SEMOP_RECSIG	-51019  /*  TSA操作时收到中断信号    */
#define TSA_ERROR_TIMEOUT	-51020  /*  读或写TSA超时    */

#endif
