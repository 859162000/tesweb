/*
 * Adapter2Tes4C_func.c
 *
 *  Created on: 2009-12-1
 *      Author: Conan
 */

#include "Adapter2Tes.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <strings.h>
#include <errno.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

/**
 * 初始化消息体空间
 * @return	初始化完成的消息体空间，失败返回NULL
 */
char* messageInit()
{
	char flag[] = "   MESSAGE";
	return allInit(1024, flag);
}

/**
 * 初始化指定标志位的消息体
 * @param len
 * 			要初始化的空间大小
 * @param flag
 * 			要指定的消息体标志，C格式字符串
 * @return	完成初始化的消息体，失败返回NULL
 */
char* allInit(int len, char *flag)
{
	char lenTotal[] = "0000000000", lenNow[] = "0000000030";
	char *p = NULL;
	
	/*验证传入参数合法性*/
	if (len < 0){
		fprintf(stderr,"0x0D00：<allInit>初始化消息体长度[%d]不能小于零！\n", len);
		return NULL;
	}
	if (len > 64*1024){
		fprintf(stderr,"0x0D01：<allInit>初始化消息体长度[%d]大于系统支持上限64K！\n", len);
		return NULL;
	}
	if (NULL==flag||strlen(flag)>10){
		fprintf(stderr,"0x0D02：<allInit>给定的消息体标志位为空或大于10字节！\n");
		return NULL;
	}

	/*分配空间*/
	p = (char *)malloc(len+30);
	if (p==NULL)
	{
		fprintf(stderr,"0x0D32：<allInit>内存申请失败！\n");
		return NULL;
	}

	/*初始化消息头*/
	sprintf(lenTotal, "%010d", len+30);
	memcpy(p, flag, 10);
	memcpy(p + 10 , lenTotal, 10);
	memcpy(p + 20, lenNow, 10);

	return p;
}

/**
 * 释放消息体空间
 * @param target
 * 			要释放空间的消息体
 * @param flag
 * 			要指定的消息体标志
 * @return	目前固定返回1
 */
int infoDel(char **target)
{
	if (NULL == *target)
		return 1;
	free(*target);
	*target = NULL;
	return 1;
}

/**
 * 向消息体中添加字段内容
 * @param message
 * 			已经初始化的消息体
 * @param name
 * 			字段名称，C格式字符串
 * @param content
 * 			字段内容
 * @param lenC
 * 			内容长度
 * @return	添加成功返回当前的消息体长度，失败返回-1
 */
int addContent(char **message, char *name, char *content, int lenC)
{
	int lenN = -1, needSpace = -1, lenNow = -1, lenTotal = -1;
	char lenN_s[] = "0000000000", lenC_s[] = "0000000000", lenNow_s[] = "0000000000", trans[] = "0000000000";
	
	/*验证传入参数合法性*/
	if (NULL==*message){
		fprintf(stderr,"0x0D03：向消息体添加字段内容时，传入消息体未用指定接口初始化！\n");
		return -1;
	}
	if (NULL==name){
		fprintf(stderr,"0x0D04：<addContent>向消息体添加字段内容时，传入字段名称不能为空！\n");
		return -1;
	}
	if (NULL==content){
		fprintf(stderr,"0x0D05：<addContent>向消息体添加字段内容时，传入字段内容不能为空！\n");
		return -1;
	}
	if (lenC<=0){
		fprintf(stderr,"0x0D05：<addContent>向消息体添加字段内容时，传入字段内容长度[%d]不能为空！\n",lenC);
		return -1;
	}

	/*判断现有消息体空间是否充足*/
	lenN = strlen(name);	/*获取字段名称长度*/
	needSpace = isEnough(*message, (10+lenN+10+lenC));
	if(-1==needSpace)
	{
		return -1;
	}
	else if(needSpace)
	{
		*message = increMessage(*message, needSpace);	/*扩充消息体空间*/
	}

	sprintf(lenN_s, "%010d", lenN);
	sprintf(lenC_s, "%010d", lenC);

	/*提取消息体长度信息*/
	memcpy(trans, *message+20, 10);
	lenNow = atoi(trans);

	/*向消息体中添加字段内容*/
	memcpy(*message+lenNow, lenN_s, 10);
	lenNow += 10;
	memcpy(*message+lenNow, name, lenN);
	lenNow += lenN;
	memcpy(*message+lenNow, lenC_s, 10);
	lenNow += 10;
	memcpy(*message+lenNow, content, lenC);
	lenNow += lenC;

	/*维护空间长度信息*/
	sprintf(lenNow_s, "%010d", lenNow);
	memcpy(*message+20, lenNow_s, 10);
	memcpy(trans, *message+20, 10);
	lenTotal = atoi(trans);

	return lenTotal;
}

/**
 * 从消息体中读取字段内容
 * @param recv
 * 			待读取的消息体
 * @param name
 * 			字段名称，C格式字符串
 * @param target
 *			读取出字段内容的存储空间
 * @param len
 * 			存储空间的可用长度
 * @return	成功返回1，失败返回-1
 */
int readContent(char *recv, char *name, char *target, int *len)
{
	int step = -1, lenNow = -1, count = 30, maxlen = -1;
	char trans[64];
	
	/*验证传入参数合法性*/
	if (NULL==recv){
		fprintf(stderr,"0x0D03：<readContent>读取字段内容时，传入消息体未用指定接口初始化！\n");
		return -1;
	}
	if (NULL==name){
		fprintf(stderr,"0x0D04：<readContent>读取字段内容时，传入字段名称不能为空！\n");
		return -1;
	}
	if (NULL==target){
		fprintf(stderr,"0x0D33：<readContent>给定的目标空间不能为空！\n");
		return -1;
	}

	if(*len>0)
	{
		maxlen = *len;
	}
	else
	{
		maxlen = 1024*1024;
	}

	/*提取消息体长度信息*/
	memcpy(trans, recv+20, 10);
	lenNow = atoi(trans);

	while(count<lenNow)
	{
		/*提取字段名长度*/
		memcpy(trans, recv+count, 10);
		step = atoi(trans);
		count += 10;

		/*提取字段名进行比较*/
		memcpy(trans, recv + count, step);
		count += step;

		/*字段名匹配*/
		if(!(memcmp(trans, name, step)))
		{
			memcpy(trans, recv+count, 10);
			step = atoi(trans);
			count += 10;

			/*需要长度大于给定空间长度，返回-1，退出*/
			if(maxlen<step)
			{
				fprintf(stderr,"0x0D33：<readContent>给定的目标空间[%d][%d]不足！\n", step, maxlen);
				return -1;
			}

			memcpy(target, recv+count, step);
			count += step;
			*len = step;
			return 1;
		}

		/*跳过后面的内容*/
		memcpy(trans, recv+count, 10);
		step = atoi(trans);
		count += 10;
		count += step;
	}

	fprintf(stderr,"0x0D11：<readContent>目标字段[%s]在给定消息体中不存在！\n", name);
	return -1;
}

/**
 * 判断消息体空间是否充足
 * @param message
 * 			目标消息体空间
 * @param increment
 * 			需要存储的字段空间
 * @return	要完成字段存储消息体需要扩充的空间，出错返回-1
 */
int isEnough(char *message, int increment)
{
	int lenTotal = -1, lenNow = -1, result = -1;
	char trans[] = "0000000000";

	/*提取消息体的长度信息*/
	memcpy(trans, message + 10, 10);
	lenTotal = atoi(trans);
	memcpy(trans, message + 20, 10);
	lenNow = atoi(trans);

	if(lenTotal==-1 || lenNow==-1)
	{
		fprintf(stderr,"0x0D09：<isEnough>消息体包含的长度信息有误！\n[%s]\n",message);
		return -1;
	}

	/*判断现有消息体空间是否充足*/
	result = (lenNow+increment) - lenTotal;

	if (result>0)
	{
		return result;
	}
	else
	{
		return 0;
	}
}

/**
 * 对消息体空间进行扩充
 * @param message
 * 			目标消息体空间
 * @param len
 * 			需要的消息体空间增量
 * @return	成功返回新的消息体空间，失败返回NULL
 */
char* increMessage(char *message, int len)
{
	int lenTotal = -1;
	char trans[] = "0000000000";
	char *newSpace = NULL;

	/*提取消息体长度信息*/
	memcpy(trans, message+10, 10);
	lenTotal = atoi(trans);
	if(lenTotal == -1)
	{
		fprintf(stderr,"0x0D09：<increMessage>消息体包含的长度信息有误！\n[%s]\n", message);
		return NULL;
	}

	/*分配新的消息体空间*/
	newSpace = allInit(lenTotal+len-30, "MESSAGE");
	if(newSpace == NULL)
	{
		return NULL;
	}

	if(copyMessage(message, newSpace)==1)
	{
		infoDel(&message);
		return newSpace;
	}
	else
	{
		infoDel(&newSpace);
		return NULL;
	}
}

/**
 * 复制消息体
 * @param from
 * 			源消息体
 * @param to
 * 			目标消息体
 * @return	成功返回1，失败返回-1
 */
int copyMessage(char *from, char *to)
{
	int f_lenNow = -1, t_lenTotal = -1;
	char trans[] = "0000000000";

	/*提取消息体长度信息*/
	memcpy(trans, from+20, 10);
	f_lenNow = atoi(trans);
	memcpy(trans, to+10, 10);
	t_lenTotal = atoi(trans);

	if(f_lenNow == -1 || t_lenTotal == -1)
	{
		fprintf(stderr,"0x0D09：<copyMessage>消息体包含的长度信息有误！\n[%s]\n",from);
		return -1;
	}

	if(t_lenTotal < f_lenNow)
	{
		fprintf(stderr,"0x0D09：<copyMessage>消息体包含的长度信息有误！\n[%s]\n",from);
		return -1;
	}

	/*完成消息体拷贝*/
	memcpy(to+20, from+20, f_lenNow);
	memcpy(to, from, 10);

	return 1;
}

/**
 * 从环境变量中获取IP端口信息
 * @param env
 * 			环境变量名称
 * @param ip
 * 			存放IP地址的空间
 * @param iplen
 *			存放IP地址空间的大小
 * @param port
 * 			存放端口信息的空间
 * @param portlen
 * 			存放端口信息空间的长度
 * @return	成功返回1，失败返回-1
 */
int env2ip (char *env, char *ip, int iplen, char *port, int portlen)
{
	char addr[30], tempip[20], tempport[10];

	/*获取指定环境变量的值*/	
	if(NULL==getenv(env))
	{
		fprintf(stderr, "0x0D14：<env2ip>环境变量[%s]未设置！\n", env);
		return -1;
	}
	strcpy(addr, getenv(env));

	/*检查环境变量格式*/
	if(strncmp(addr, "//",2))
	{
		fprintf(stderr, "0x0D19：<env2ip>核心地址的环境变量[%s]格式设置错误！\n", addr);
		return -1;
	}

	/*获取IP和端口的值*/
	strcpy(tempip, strtok(addr, ":"));
	if (tempip==NULL)
	{
		fprintf(stderr, "0x0D06：<env2ip>从环境变量中获取核心IP地址错误！\n");
		return -1;
	}

	if((iplen + 2)<20)
	{
		fprintf(stderr, "0x0D33：<env2ip>IP信息存储空间不足！\n");
		return -1;
	}
	strcpy(tempport, strtok(NULL, ":"));
	if (tempport==NULL)
	{
		fprintf(stderr, "0x0D09：<env2ip>从环境变量中获取核心端口失败！\n");
		return -1;
	}
	if((portlen+2) < 10)
	{
		fprintf(stderr, "0x0D33：<env2ip>端口信息存储空间不足！\n");
		return -1;
	}

	strcpy(ip, tempip+2);
	strcpy(port, tempport);

	return 1;
}



/**
 * 使用TCP短连接方式与核心进行交互
 * @param psend
 * 			请求消息体
 * @param precv
 * 			返回消息体
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int sendTcpS(char **psend, char **precv)
{
	char coreip[20], coreport[20];
	struct hostent *host;
	int sockfd;
	struct sockaddr_in serv_addr;
	char recvbuf[TCPRECVSIZE];
	long recvLen = -1;

	int tsize,nsize,sendLen;
	char slen[] = "0000000000";
	char trans[] = "0000000000";
	char tranmessage[TCPRECVSIZE];
	char channelName[100];

	/*获取通道名称*/
	{
		if(NULL==getenv(CHANNELNAMEENV))
		{
			fprintf(stderr, "0x0D14：<sendTcpS>环境变量[%s]未设置！\n", CHANNELNAMEENV);
			return -1;
		}
		strcpy(channelName, getenv(CHANNELNAMEENV));
	}
	
	/*添加消息字段*/
	if (-1==addContent(psend, "CHANNELNAME", channelName, strlen(channelName))){
		return -1;
	}

	/*TCP处理流程*/
	/*从环境变量中获取核心IP和端口*/
	if ((env2ip(TCPADDRENV, coreip, 20, coreport, 20)) < 0)
	{
		return -1;
	}

	if((host=gethostbyname(coreip))==NULL)
	{
		herror("0x0D20：<sendTcpS>远程地址解析失败！\n");
		return -1;
	}

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("0x0D20：<sendTcpS>套接字创建失败！\n");
		return -1;
	}

	serv_addr.sin_family=AF_INET;
	serv_addr.sin_port=htons(atoi(coreport));
	serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
	bzero( &(serv_addr.sin_zero),8);
	if (connect(sockfd, (struct sockaddr *) &serv_addr,sizeof(struct sockaddr)) == -1)
	{
		fprintf(stderr, "0x0D21：<sendTcpS>远程主机无法连接[%s][%s]\n", coreip,coreport);
		return -1;
	}
	
	/*获取发送消息体的长度*/
	memcpy(trans, *psend+10, 10);
	sendLen = atoi(trans);

	/*发送数据*/
	if(send(sockfd,(void*)*psend,sendLen,0) != sendLen)
	{
		perror("0x0D22：<sendTcpS>报文发送失败！\n");
		return -1;
	}

	/*接收数据*/
	nsize = 0;
	while (nsize<30)
	{/*收取消息头*/
		if ((recvLen = recv(sockfd, recvbuf, 30-nsize, 0)) ==-1)
		{
			perror("0x0D22：<sendTcpS>报文接收失败！\n");
			close(sockfd);
			return -1;
		}
		memcpy(tranmessage+nsize, recvbuf, recvLen);
		nsize += recvLen;
		if (recvLen==0)
		{
			fprintf(stderr,"0x0D22：<sendTcpS>客户机连接关闭！\n");
			close(sockfd);
			return -1;
		}
	}
	//获取接收消息的长度信息
	memcpy(slen, tranmessage+10, 10);
	tsize = atoi(slen);
	
	if (tsize>TCPRECVSIZE)
	{
		fprintf(stderr,"0x0D33：<sendTcpS>接收缓冲区空间不足！\n");
		close(sockfd);
		return -1;
	}
	
	while(nsize<tsize)
	{
		if ((recvLen = recv(sockfd, recvbuf, TCPRECVSIZE, 0)) ==-1)
		{
			fprintf(stderr,"0x0D22：<sendTcpS>报文接收出错！\n");
			close(sockfd);
			return -1;
		}
		memcpy(tranmessage+nsize, recvbuf, recvLen);
		nsize += recvLen;
	}
	/*获取接收数据*/
	*precv = (char*)malloc(tsize);
	memcpy(*precv, tranmessage, tsize);

	/*释放资源*/
	close(sockfd);

	return recvLen;
}


/**
 * 使用TCP短连接方式与核心进行交互
 * @param psend
 * 			请求消息体
 * @param precv
 * 			返回消息体
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int sendTcpSMul(char **psend, char ***precv)
{
	char coreip[20], coreport[20];
	struct hostent *host;
	int sockfd;
	struct sockaddr_in serv_addr;
	char recvbuf[TCPRECVSIZE];
	long recvLen = -1;

	int tsize,nsize,sendLen;
	char slen[] = "0000000000";
	char trans[] = "0000000000";
	char tranmessage[TCPRECVSIZE];
	char channelName[100];
	int i,iPackNum,iPackSeq;
	char strPackSeq[64];
	int iFieldLen=0;
	int ret;
	int iFlag=0;
	char **precvTmp;

	/*获取通道名称*/
	{
		if(NULL==getenv(CHANNELNAMEENV))
		{
			fprintf(stderr, "0x0D14：<sendTcpS>环境变量[%s]未设置！\n", CHANNELNAMEENV);
			return -1;
		}
		strcpy(channelName, getenv(CHANNELNAMEENV));
	}
	
	/*添加消息字段*/
	if (-1==addContent(psend, "CHANNELNAME", channelName, strlen(channelName))){
		return -1;
	}

	/*TCP处理流程*/
	/*从环境变量中获取核心IP和端口*/
	if ((env2ip(TCPADDRENV, coreip, 20, coreport, 20)) < 0)
	{
		return -1;
	}

	if((host=gethostbyname(coreip))==NULL)
	{
		herror("0x0D20：<sendTcpS>远程地址解析失败！\n");
		return -1;
	}

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("0x0D20：<sendTcpS>套接字创建失败！\n");
		return -1;
	}

	serv_addr.sin_family=AF_INET;
	serv_addr.sin_port=htons(atoi(coreport));
	serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
	bzero( &(serv_addr.sin_zero),8);
	if (connect(sockfd, (struct sockaddr *) &serv_addr,sizeof(struct sockaddr)) == -1)
	{
		fprintf(stderr, "0x0D21：<sendTcpS>远程主机无法连接[%s][%s]\n", coreip,coreport);
		return -1;
	}
	
	/*获取发送消息体的长度*/
	memcpy(trans, *psend+10, 10);
	sendLen = atoi(trans);
	/*最多支持1024个分包*/
	precvTmp=(char **)malloc(1024*sizeof(char *));
	if(precvTmp==NULL)
	{
		fprintf(stderr, "分配应答报文内存空间失败\n");
		close(sockfd);		
		return -1;
	}	

	/*发送数据*/
	if(send(sockfd,(void*)*psend,sendLen,0) != sendLen)
	{
		perror("0x0D22：<sendTcpS>报文发送失败！\n");
		close(sockfd);		
		free(precvTmp);
		return -1;
	}
	/*多应答包处理*/
	
	iPackNum=0;
	iPackSeq=0;
	for(i=0;;i++)
	{
		/*printf("TestMQ:start read msg pack NO[%d]\n",i+1);*/
		/*接收数据*/
		nsize = 0;
		while (nsize<30)
		{/*收取消息头*/
			if ((recvLen = recv(sockfd, recvbuf, 30-nsize, 0)) ==-1)
			{
				perror("0x0D22：<sendTcpS>recv报文头接收失败！\n");
				iFlag=-1;
				break;
			}
			/*printf("TestMQ:recvLen=[%ld]\n",recvLen);*/
			memcpy(tranmessage+nsize, recvbuf, recvLen);
			nsize += recvLen;
			if (recvLen==0)
			{
				fprintf(stderr,"0x0D22：<sendTcpS>客户机连接关闭！\n");
				iFlag=-2;
				break;
			}
		}
		if(iFlag<0)
			break;
		//获取接收消息的长度信息
		memcpy(slen, tranmessage+10, 10);
		tsize = atoi(slen);
		
		if (tsize>TCPRECVSIZE)
		{
			fprintf(stderr,"0x0D33：<sendTcpS>接收缓冲区空间不足！\n");
			iFlag=-3;
			break;
		}
		
		while(nsize<tsize)
		{
			/*printf("TestMQ recv msgbody tsize=[%d] body=[%d]\n",tsize,tsize-nsize);*/
			if ((recvLen = recv(sockfd, recvbuf, tsize-nsize, 0)) ==-1)
			{
				fprintf(stderr,"0x0D22：<sendTcpS>recv报文接收出错！\n");
				iFlag=-4;
				break;
			}
			/*printf("TestMQ recv msgbody recvLen=[%ld]\n",recvLen);*/
			memcpy(tranmessage+nsize, recvbuf, recvLen);
			nsize += recvLen;
		}
		if(iFlag<0)
			break;
		/*获取接收数据*/
		precvTmp[i] = (char*)malloc(65536);
		if (precvTmp[i]==NULL)
		{
			fprintf(stderr,"分配分包内存空间失败Pack[%d]！\n",i);
			iFlag=-5;
			break;
		}
		memset(precvTmp[i],0,65536);
		memcpy(precvTmp[i], tranmessage, tsize);
		
		memset(strPackSeq,0,sizeof(strPackSeq));
		ret=readContent(precvTmp[i],"PACKSEQ",strPackSeq,&iFieldLen);
		if (ret==-1)
		{
			fprintf(stderr,"readContent 读取PACKSEQ出错！\n");
			/*+1，失败后释放空间用*/
			iPackNum++;
			iFlag=-6;
			break;
		}
		/*printf("TestMQ:msg seq[%s],[%s]\n",strPackSeq,precvTmp[i]);*/
		iPackSeq=atoi(strPackSeq);
		iPackNum++;
		/*报文序号为0则报文结束*/
		if(iPackSeq==0)
			break;
		/*超过最大分包数*/
		if(iPackNum>1024)
			break;
		/*printf("TestMQ:end read msg pack[%d]\n",i+1);*/
			
		
	}
	/*printf("TestMQ read msg OK\n");*/
	/*释放资源*/
	close(sockfd);
	if(iFlag<0)
	{
		for(i=0;i<iPackNum;i++)
			free(precvTmp[i]);
		free(precvTmp);
		return -iFlag;
	}
	*precv=precvTmp;
	/*printf("TestMQ:Total packet num=[%d]\n",iPackNum);*/
	return iPackNum;
}


