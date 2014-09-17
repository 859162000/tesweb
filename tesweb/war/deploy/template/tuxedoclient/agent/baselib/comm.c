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

/*设定超时标志*/
void SCCMTimeOutDeal(int iSigNum)
{
    iSCCMTimeOut = 1;       /* 设定超时标志 */
    return;
}


/*建立Socket*/
int TCPCreatSock()
{
    int iSock;

    /* 创建一个基于互连网协议的字节流套接字 */
    iSock = socket(AF_INET, SOCK_STREAM, 0);
    return(iSock);
}


/*连接Server*/
int TCPConn2Serv(int iSock, char *psHostAddr, int iCommPort)
{
    int    iTimes;
    int    iRetCode;
    struct sockaddr_in stLocal;

    bzero((char*)&stLocal, sizeof(stLocal));
    stLocal.sin_family      = AF_INET;                  /* 互连网协议 */
    stLocal.sin_addr.s_addr = inet_addr(psHostAddr);    /* 互连网地址 */
    stLocal.sin_port        = htons(iCommPort);         /* 网络节次号 */

    for(iTimes=0; iTimes<RETRY_TIMES; iTimes++)
    {
        /* 建立TCP服务的连接 */
        iRetCode = connect(iSock, (struct sockaddr*)&stLocal, sizeof(stLocal));
        if(iRetCode == 0)                               /* 连接成功   */
            return(0);
    }

    return(-1);
}


/*选择socket*/
int TCPSelectSock(int iSock, long lTimeOut)
{
    int    iRetCode;
    fd_set  stSockfd;
    struct timeval stTimeOut;

    /* 重置描述字集的值, 设定iSock对应位为1 */
    FD_ZERO(&stSockfd);
    FD_SET(iSock, &stSockfd);

    /* 设定等待的时间 */
    stTimeOut.tv_sec  = lTimeOut/1000;
    stTimeOut.tv_usec = lTimeOut%1000*1000;
    
    /* 检查套接字是否准备好 */
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

    /* 检查套接字是否准备好 */
    iRetCode = TCPSelectSock(iSock, 50);
    if(iRetCode < 0)    return(iRetCode);
    
    /* 设定系统超时时间, 激活闹铃信号 */
    iSCCMTimeOut  = 0;
    pfOldSigValue = signal(SIGALRM, SCCMTimeOutDeal);
    alarm(iTimeOut);

    /* 分包读取传输数据, 并检测是否超时 */
    while((iDataLen > 0) && (iSCCMTimeOut == 0))
    {
        /* 如果超过最大传输字节数分包 */
        if(iDataLen > MAXBUFSIZE)
            iReadLen = MAXBUFSIZE;
        else
            iReadLen = iDataLen;

        /* 读取传输数据 */
        iRetCode = read(iSock, psDataBuf+iMsgLen, iReadLen);
        if(iRetCode > 0)                        /* 读到数据 */
        {
            iMsgLen += iRetCode;
            iDataLen -= iRetCode;
            if(iRetCode != iReadLen)    break;
        }
        else                                    /* 读取失败 */
        {
            if(iMsgLen == 0)    return(-1);
            else                break;
        }
    }

    alarm(0);                               /* 撤消闹铃设定 */
    signal(SIGALRM, pfOldSigValue);         /* 恢复信号设置 */
    if(iSCCMTimeOut == 1)       return(-1);

    return(iMsgLen);
}


int TCPWriteSock(int iSock, char *psDataBuf, int iDataLen, int iTimeOut)
{
    int iMsgLen = 0;
    int iSendLen;
    int iRetCode;

    /* 检查套接字是否准备好 */
    iRetCode = TCPSelectSock(iSock, 50);
    if(iRetCode < 0)    return(iRetCode);

    /* 设定系统超时时间, 激活闹铃信号 */
    iSCCMTimeOut  = 0;
    pfOldSigValue = signal(SIGALRM, SCCMTimeOutDeal);
    alarm(iTimeOut);

    /* 分包发送传输数据, 并检测是否超时 */
    while((iDataLen > 0) && (iSCCMTimeOut == 0))
    {
        /* 如果超过最大传输字节数分包 */
        if(iDataLen > MAXBUFSIZE)
            iSendLen = MAXBUFSIZE;
        else
            iSendLen = iDataLen;

        /* 发送传输数据 */
        iRetCode = write(iSock, psDataBuf+iMsgLen, iSendLen);
        if(iRetCode <= 0)                   /* 发送数据失败 */
            return(-1);

        iDataLen -= iRetCode;
        iMsgLen += iRetCode;
    }

    alarm(0);                               /* 撤消闹铃设定 */
    signal(SIGALRM, pfOldSigValue);         /* 恢复信号设置 */
    if(iSCCMTimeOut == 1)        return(-1);

    return(iMsgLen);
}


int TCPCloseSock(int iSock)
{
    int iRetCode;

    iRetCode = close(iSock);                /* 关闭套接字 */
    if(iRetCode < 0)
        return(-1);

    return(0);
}








/*Outserver.c 需要调用的函数*/

int TCPBindSock(int iSock, int iCommPort)
{
    int    iRetCode;
    struct sockaddr_in stLocal;

    bzero((char*)&stLocal, sizeof(stLocal));
    stLocal.sin_family      = AF_INET;
    stLocal.sin_addr.s_addr = htonl(INADDR_ANY);            /* 任意地址 */
    stLocal.sin_port        = htons((unsigned short)iCommPort);

    /* 为套接字分配一个本地协议地址和一个TCP端口号 */
    iRetCode = bind(iSock, (struct sockaddr*)&stLocal, sizeof(stLocal));
    if(iRetCode == -1)
        return(-1);

    return(0);
}

int TCPListenSock(int iSock)
{
    int iRetCode;

    /* 监听套接字上的连接, 最大的同时连接数为5 */
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
    
    /* 接受外部的连接, 并生成一个新的套接字 */
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
    stLinger.l_onoff  = 1;                  /* 设定为延迟关闭模式 */
    stLinger.l_linger = iLingTime;          /* 设定关闭时延迟时间 */
    
    /* 修改套接字设定模式 */
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

    /* 获取套接字对端主机的OUT地址信息 */
    iRetCode = getpeername(iSock, (struct sockaddr*)&stSockAddr, (socklen_t *)&iLen);
    if(iRetCode < 0)    return(-1);

    /* 将互连网P地址转换为字符串形式 */
    strcpy(psIpAddr, (char*)inet_ntoa(stSockAddr.sin_addr));

    return(0);
}

