#ifndef _COMM_H_
#define _COMM_H_
#endif
#define READ_TIMEOUT		30
#define WRITE_TIMEOUT		30
#define COMM_TIMEOUT     30
#define MAXBUFSIZE       8192        /* MAX buffer size define */
#define RETRY_TIMES      5           /* Retry times if fail    */

extern void SCCMTimeOutDeal(int iSigNum);
extern int  TCPCreatSock();
extern int  TCPConn2Serv(int iSock, char *psHostAddr, int iCommPort);
extern int  TCPSelectSock(int iSock, long lTimeOut);
extern int  TCPReadSock(int iSock, char *psDataBuf, int iDataLen, int iTimeOut);
extern int  TCPWriteSock(int iSock, char *psDataBuf, int iDataLen, int iTimeOut);
extern int  TCPCloseSock(int iSock);
extern int  TCPBindSock(int iSock, int iCommPort);
extern int  TCPListenSock(int iSock);
extern int  TCPAcceptSock(int iSock);
extern int  TcpSetLinger(int iSock, int iLingTime);
extern int  TCPGetPeerAddr(int iSock, char *pcIpAddr);
