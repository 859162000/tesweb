#include "comm.h"
#include <time.h>
#include <stdio.h>
#include <errno.h>
#include <fcntl.h>
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
#include<sys/socket.h>
#include<arpa/inet.h>
#ifdef LINUX
#include <sys/select.h>
#endif

int  iSCCMTimeOut;
void (*pfOldSigValue)(int);

/*�趨��ʱ��־*/
void SCCMTimeOutDeal(int iSigNum)
{
    iSCCMTimeOut = 1;       /* �趨��ʱ��־ */
    return;
}


/*����Socket*/
int TCPCreatSock()
{
    int iSock;

    /* ����һ�����ڻ�����Э����ֽ����׽��� */
    iSock = socket(AF_INET, SOCK_STREAM, 0);
    return(iSock);
}


/*����Server*/
int TCPConn2Serv(int iSock, char *psHostAddr, int iCommPort)
{
    int    iTimes;
    int    iRetCode;
    struct sockaddr_in stLocal;

    bzero((char*)&stLocal, sizeof(stLocal));
    stLocal.sin_family      = AF_INET;                  /* ������Э�� */
    stLocal.sin_addr.s_addr = inet_addr(psHostAddr);    /* ��������ַ */
    stLocal.sin_port        = htons(iCommPort);         /* ����ڴκ� */

    for(iTimes=0; iTimes<RETRY_TIMES; iTimes++)
    {
        /* ����TCP��������� */
        iRetCode = connect(iSock, (struct sockaddr*)&stLocal, sizeof(stLocal));
        if(iRetCode == 0)                               /* ���ӳɹ�   */
            return(0);
    }

    return(-1);
}


/*ѡ��socket*/
int TCPSelectSock(int iSock, long lTimeOut)
{
    int    iRetCode;
    fd_set  stSockfd;
    struct timeval stTimeOut;

    /* ���������ּ���ֵ, �趨iSock��ӦλΪ1 */
    FD_ZERO(&stSockfd);
    FD_SET(iSock, &stSockfd);

    /* �趨�ȴ���ʱ�� */
    stTimeOut.tv_sec  = lTimeOut/1000;
    stTimeOut.tv_usec = lTimeOut%1000*1000;
    
    /* ����׽����Ƿ�׼���� */
    iRetCode = select(iSock+1, &stSockfd, (fd_set*)NULL, (fd_set*)NULL,
                      &stTimeOut);
    if(iRetCode < 0)
        return(-1);
    else
        return(iRetCode);
}


int TCPReadSock(int iSock, char *psDataBuf, int iDataLen, int iTimeOut)
{
    int iRetCode;
    int iMsgLen = 0;
    int iReadLen;

    /* ����׽����Ƿ�׼���� */
    iRetCode = TCPSelectSock(iSock, 50);
    if(iRetCode < 0)    return(iRetCode);
    
    /* �趨ϵͳ��ʱʱ��, ���������ź� */
    iSCCMTimeOut  = 0;
    pfOldSigValue = signal(SIGALRM, SCCMTimeOutDeal);
    alarm(iTimeOut);

    /* �ְ���ȡ��������, ������Ƿ�ʱ */
    while((iDataLen > 0) && (iSCCMTimeOut == 0))
    {
        /* �������������ֽ����ְ� */
        if(iDataLen > MAXBUFSIZE)
            iReadLen = MAXBUFSIZE;
        else
            iReadLen = iDataLen;

        /* ��ȡ�������� */
        iRetCode = read(iSock, psDataBuf+iMsgLen, iReadLen);
        if(iRetCode > 0)                        /* �������� */
        {
            iMsgLen += iRetCode;
            iDataLen -= iRetCode;
            if(iRetCode != iReadLen)    break;
        }
        else                                    /* ��ȡʧ�� */
        {
            if(iMsgLen == 0)    return(-1);
            else                break;
        }
    }

    alarm(0);                               /* ���������趨 */
    signal(SIGALRM, pfOldSigValue);         /* �ָ��ź����� */
    if(iSCCMTimeOut == 1)       return(-1);

    return(iMsgLen);
}


int TCPWriteSock(int iSock, char *psDataBuf, int iDataLen, int iTimeOut)
{
    int iMsgLen = 0;
    int iSendLen;
    int iRetCode;

    /* ����׽����Ƿ�׼���� */
    iRetCode = TCPSelectSock(iSock, 50);
    if(iRetCode < 0)    return(iRetCode);

    /* �趨ϵͳ��ʱʱ��, ���������ź� */
    iSCCMTimeOut  = 0;
    pfOldSigValue = signal(SIGALRM, SCCMTimeOutDeal);
    alarm(iTimeOut);

    /* �ְ����ʹ�������, ������Ƿ�ʱ */
    while((iDataLen > 0) && (iSCCMTimeOut == 0))
    {
        /* �������������ֽ����ְ� */
        if(iDataLen > MAXBUFSIZE)
            iSendLen = MAXBUFSIZE;
        else
            iSendLen = iDataLen;

        /* ���ʹ������� */
        iRetCode = write(iSock, psDataBuf+iMsgLen, iSendLen);
        if(iRetCode <= 0)                   /* ��������ʧ�� */
            return(-1);

        iDataLen -= iRetCode;
        iMsgLen += iRetCode;
    }

    alarm(0);                               /* ���������趨 */
    signal(SIGALRM, pfOldSigValue);         /* �ָ��ź����� */
    if(iSCCMTimeOut == 1)        return(-1);

    return(iMsgLen);
}


int TCPCloseSock(int iSock)
{
    int iRetCode;

    iRetCode = close(iSock);                /* �ر��׽��� */
    if(iRetCode < 0)
        return(-1);

    return(0);
}








/*Outserver.c ��Ҫ���õĺ���*/

int TCPBindSock(int iSock, int iCommPort)
{
    int    iRetCode;
    struct sockaddr_in stLocal;

    bzero((char*)&stLocal, sizeof(stLocal));
    stLocal.sin_family      = AF_INET;
    stLocal.sin_addr.s_addr = htonl(INADDR_ANY);            /* �����ַ */
    stLocal.sin_port        = htons((unsigned short)iCommPort);

    /* Ϊ�׽��ַ���һ������Э���ַ��һ��TCP�˿ں� */
    iRetCode = bind(iSock, (struct sockaddr*)&stLocal, sizeof(stLocal));
    if(iRetCode == -1)
        return(-1);

    return(0);
}

int TCPListenSock(int iSock)
{
    int iRetCode;

    /* �����׽����ϵ�����, ����ͬʱ������Ϊ5 */
    iRetCode = listen(iSock, 5);
    if(iRetCode == -1)
        return(-1);

    return(0);
}

int TCPAcceptSock(int iSock)
{
    int    iNewSock;
    int    iAddrLen;
    struct sockaddr_in stLocal;

    bzero((char*)&stLocal, sizeof(stLocal));
    iAddrLen = sizeof(struct sockaddr_in);
    
    /* �����ⲿ������, ������һ���µ��׽��� */
    iNewSock = accept(iSock, (struct sockaddr*)&stLocal, (socklen_t *)&iAddrLen);
    if(iNewSock == -1)
        return(-1);

    return(iNewSock);
}

int TcpSetLinger(int iSock, int iLingTime)
{
    int    iLen;
    int    iRetCode;
    struct linger stLinger;

    iLen = sizeof(struct linger);
    stLinger.l_onoff  = 1;                  /* �趨Ϊ�ӳٹر�ģʽ */
    stLinger.l_linger = iLingTime;          /* �趨�ر�ʱ�ӳ�ʱ�� */
    
    /* �޸��׽����趨ģʽ */
    iRetCode = setsockopt(iSock, SOL_SOCKET, SO_LINGER, &stLinger, iLen);
    if(iRetCode < 0)    return(-1);

    return(0);
}

int TCPGetPeerAddr(int iSock, char *psIpAddr)
{
    struct sockaddr_in stSockAddr;
    char   abyid[23];
    int    iLen;
    int    iRetCode;

    iLen = sizeof(stSockAddr);
    if(iSock < 0)       return(-1);

    /* ��ȡ�׽��ֶԶ�������OUT��ַ��Ϣ */
    iRetCode = getpeername(iSock, (struct sockaddr*)&stSockAddr, (socklen_t *)&iLen);
    if(iRetCode < 0)    return(-1);

    /* ��������P��ַת��Ϊ�ַ�����ʽ */
    strcpy(psIpAddr, (char*)inet_ntoa(stSockAddr.sin_addr));

    return(0);
}

