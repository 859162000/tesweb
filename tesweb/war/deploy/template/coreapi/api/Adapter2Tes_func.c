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
 * ��ʼ����Ϣ��ռ�
 * @return	��ʼ����ɵ���Ϣ��ռ䣬ʧ�ܷ���NULL
 */
char* messageInit()
{
	char flag[] = "   MESSAGE";
	return allInit(1024, flag);
}

/**
 * ��ʼ��ָ����־λ����Ϣ��
 * @param len
 * 			Ҫ��ʼ���Ŀռ��С
 * @param flag
 * 			Ҫָ������Ϣ���־��C��ʽ�ַ���
 * @return	��ɳ�ʼ������Ϣ�壬ʧ�ܷ���NULL
 */
char* allInit(int len, char *flag)
{
	char lenTotal[] = "0000000000", lenNow[] = "0000000030";
	char *p = NULL;
	
	/*��֤��������Ϸ���*/
	if (len < 0){
		fprintf(stderr,"0x0D00��<allInit>��ʼ����Ϣ�峤��[%d]����С���㣡\n", len);
		return NULL;
	}
	if (len > 64*1024){
		fprintf(stderr,"0x0D01��<allInit>��ʼ����Ϣ�峤��[%d]����ϵͳ֧������64K��\n", len);
		return NULL;
	}
	if (NULL==flag||strlen(flag)>10){
		fprintf(stderr,"0x0D02��<allInit>��������Ϣ���־λΪ�ջ����10�ֽڣ�\n");
		return NULL;
	}

	/*����ռ�*/
	p = (char *)malloc(len+30);
	if (p==NULL)
	{
		fprintf(stderr,"0x0D32��<allInit>�ڴ�����ʧ�ܣ�\n");
		return NULL;
	}

	/*��ʼ����Ϣͷ*/
	sprintf(lenTotal, "%010d", len+30);
	memcpy(p, flag, 10);
	memcpy(p + 10 , lenTotal, 10);
	memcpy(p + 20, lenNow, 10);

	return p;
}

/**
 * �ͷ���Ϣ��ռ�
 * @param target
 * 			Ҫ�ͷſռ����Ϣ��
 * @param flag
 * 			Ҫָ������Ϣ���־
 * @return	Ŀǰ�̶�����1
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
 * ����Ϣ��������ֶ�����
 * @param message
 * 			�Ѿ���ʼ������Ϣ��
 * @param name
 * 			�ֶ����ƣ�C��ʽ�ַ���
 * @param content
 * 			�ֶ�����
 * @param lenC
 * 			���ݳ���
 * @return	��ӳɹ����ص�ǰ����Ϣ�峤�ȣ�ʧ�ܷ���-1
 */
int addContent(char **message, char *name, char *content, int lenC)
{
	int lenN = -1, needSpace = -1, lenNow = -1, lenTotal = -1;
	char lenN_s[] = "0000000000", lenC_s[] = "0000000000", lenNow_s[] = "0000000000", trans[] = "0000000000";
	
	/*��֤��������Ϸ���*/
	if (NULL==*message){
		fprintf(stderr,"0x0D03������Ϣ������ֶ�����ʱ��������Ϣ��δ��ָ���ӿڳ�ʼ����\n");
		return -1;
	}
	if (NULL==name){
		fprintf(stderr,"0x0D04��<addContent>����Ϣ������ֶ�����ʱ�������ֶ����Ʋ���Ϊ�գ�\n");
		return -1;
	}
	if (NULL==content){
		fprintf(stderr,"0x0D05��<addContent>����Ϣ������ֶ�����ʱ�������ֶ����ݲ���Ϊ�գ�\n");
		return -1;
	}
	if (lenC<=0){
		fprintf(stderr,"0x0D05��<addContent>����Ϣ������ֶ�����ʱ�������ֶ����ݳ���[%d]����Ϊ�գ�\n",lenC);
		return -1;
	}

	/*�ж�������Ϣ��ռ��Ƿ����*/
	lenN = strlen(name);	/*��ȡ�ֶ����Ƴ���*/
	needSpace = isEnough(*message, (10+lenN+10+lenC));
	if(-1==needSpace)
	{
		return -1;
	}
	else if(needSpace)
	{
		*message = increMessage(*message, needSpace);	/*������Ϣ��ռ�*/
	}

	sprintf(lenN_s, "%010d", lenN);
	sprintf(lenC_s, "%010d", lenC);

	/*��ȡ��Ϣ�峤����Ϣ*/
	memcpy(trans, *message+20, 10);
	lenNow = atoi(trans);

	/*����Ϣ��������ֶ�����*/
	memcpy(*message+lenNow, lenN_s, 10);
	lenNow += 10;
	memcpy(*message+lenNow, name, lenN);
	lenNow += lenN;
	memcpy(*message+lenNow, lenC_s, 10);
	lenNow += 10;
	memcpy(*message+lenNow, content, lenC);
	lenNow += lenC;

	/*ά���ռ䳤����Ϣ*/
	sprintf(lenNow_s, "%010d", lenNow);
	memcpy(*message+20, lenNow_s, 10);
	memcpy(trans, *message+20, 10);
	lenTotal = atoi(trans);

	return lenTotal;
}

/**
 * ����Ϣ���ж�ȡ�ֶ�����
 * @param recv
 * 			����ȡ����Ϣ��
 * @param name
 * 			�ֶ����ƣ�C��ʽ�ַ���
 * @param target
 *			��ȡ���ֶ����ݵĴ洢�ռ�
 * @param len
 * 			�洢�ռ�Ŀ��ó���
 * @return	�ɹ�����1��ʧ�ܷ���-1
 */
int readContent(char *recv, char *name, char *target, int *len)
{
	int step = -1, lenNow = -1, count = 30, maxlen = -1;
	char trans[64];
	
	/*��֤��������Ϸ���*/
	if (NULL==recv){
		fprintf(stderr,"0x0D03��<readContent>��ȡ�ֶ�����ʱ��������Ϣ��δ��ָ���ӿڳ�ʼ����\n");
		return -1;
	}
	if (NULL==name){
		fprintf(stderr,"0x0D04��<readContent>��ȡ�ֶ�����ʱ�������ֶ����Ʋ���Ϊ�գ�\n");
		return -1;
	}
	if (NULL==target){
		fprintf(stderr,"0x0D33��<readContent>������Ŀ��ռ䲻��Ϊ�գ�\n");
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

	/*��ȡ��Ϣ�峤����Ϣ*/
	memcpy(trans, recv+20, 10);
	lenNow = atoi(trans);

	while(count<lenNow)
	{
		/*��ȡ�ֶ�������*/
		memcpy(trans, recv+count, 10);
		step = atoi(trans);
		count += 10;

		/*��ȡ�ֶ������бȽ�*/
		memcpy(trans, recv + count, step);
		count += step;

		/*�ֶ���ƥ��*/
		if(!(memcmp(trans, name, step)))
		{
			memcpy(trans, recv+count, 10);
			step = atoi(trans);
			count += 10;

			/*��Ҫ���ȴ��ڸ����ռ䳤�ȣ�����-1���˳�*/
			if(maxlen<step)
			{
				fprintf(stderr,"0x0D33��<readContent>������Ŀ��ռ�[%d][%d]���㣡\n", step, maxlen);
				return -1;
			}

			memcpy(target, recv+count, step);
			count += step;
			*len = step;
			return 1;
		}

		/*�������������*/
		memcpy(trans, recv+count, 10);
		step = atoi(trans);
		count += 10;
		count += step;
	}

	fprintf(stderr,"0x0D11��<readContent>Ŀ���ֶ�[%s]�ڸ�����Ϣ���в����ڣ�\n", name);
	return -1;
}

/**
 * �ж���Ϣ��ռ��Ƿ����
 * @param message
 * 			Ŀ����Ϣ��ռ�
 * @param increment
 * 			��Ҫ�洢���ֶοռ�
 * @return	Ҫ����ֶδ洢��Ϣ����Ҫ����Ŀռ䣬������-1
 */
int isEnough(char *message, int increment)
{
	int lenTotal = -1, lenNow = -1, result = -1;
	char trans[] = "0000000000";

	/*��ȡ��Ϣ��ĳ�����Ϣ*/
	memcpy(trans, message + 10, 10);
	lenTotal = atoi(trans);
	memcpy(trans, message + 20, 10);
	lenNow = atoi(trans);

	if(lenTotal==-1 || lenNow==-1)
	{
		fprintf(stderr,"0x0D09��<isEnough>��Ϣ������ĳ�����Ϣ����\n[%s]\n",message);
		return -1;
	}

	/*�ж�������Ϣ��ռ��Ƿ����*/
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
 * ����Ϣ��ռ��������
 * @param message
 * 			Ŀ����Ϣ��ռ�
 * @param len
 * 			��Ҫ����Ϣ��ռ�����
 * @return	�ɹ������µ���Ϣ��ռ䣬ʧ�ܷ���NULL
 */
char* increMessage(char *message, int len)
{
	int lenTotal = -1;
	char trans[] = "0000000000";
	char *newSpace = NULL;

	/*��ȡ��Ϣ�峤����Ϣ*/
	memcpy(trans, message+10, 10);
	lenTotal = atoi(trans);
	if(lenTotal == -1)
	{
		fprintf(stderr,"0x0D09��<increMessage>��Ϣ������ĳ�����Ϣ����\n[%s]\n", message);
		return NULL;
	}

	/*�����µ���Ϣ��ռ�*/
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
 * ������Ϣ��
 * @param from
 * 			Դ��Ϣ��
 * @param to
 * 			Ŀ����Ϣ��
 * @return	�ɹ�����1��ʧ�ܷ���-1
 */
int copyMessage(char *from, char *to)
{
	int f_lenNow = -1, t_lenTotal = -1;
	char trans[] = "0000000000";

	/*��ȡ��Ϣ�峤����Ϣ*/
	memcpy(trans, from+20, 10);
	f_lenNow = atoi(trans);
	memcpy(trans, to+10, 10);
	t_lenTotal = atoi(trans);

	if(f_lenNow == -1 || t_lenTotal == -1)
	{
		fprintf(stderr,"0x0D09��<copyMessage>��Ϣ������ĳ�����Ϣ����\n[%s]\n",from);
		return -1;
	}

	if(t_lenTotal < f_lenNow)
	{
		fprintf(stderr,"0x0D09��<copyMessage>��Ϣ������ĳ�����Ϣ����\n[%s]\n",from);
		return -1;
	}

	/*�����Ϣ�忽��*/
	memcpy(to+20, from+20, f_lenNow);
	memcpy(to, from, 10);

	return 1;
}

/**
 * �ӻ��������л�ȡIP�˿���Ϣ
 * @param env
 * 			������������
 * @param ip
 * 			���IP��ַ�Ŀռ�
 * @param iplen
 *			���IP��ַ�ռ�Ĵ�С
 * @param port
 * 			��Ŷ˿���Ϣ�Ŀռ�
 * @param portlen
 * 			��Ŷ˿���Ϣ�ռ�ĳ���
 * @return	�ɹ�����1��ʧ�ܷ���-1
 */
int env2ip (char *env, char *ip, int iplen, char *port, int portlen)
{
	char addr[30], tempip[20], tempport[10];

	/*��ȡָ������������ֵ*/	
	if(NULL==getenv(env))
	{
		fprintf(stderr, "0x0D14��<env2ip>��������[%s]δ���ã�\n", env);
		return -1;
	}
	strcpy(addr, getenv(env));

	/*��黷��������ʽ*/
	if(strncmp(addr, "//",2))
	{
		fprintf(stderr, "0x0D19��<env2ip>���ĵ�ַ�Ļ�������[%s]��ʽ���ô���\n", addr);
		return -1;
	}

	/*��ȡIP�Ͷ˿ڵ�ֵ*/
	strcpy(tempip, strtok(addr, ":"));
	if (tempip==NULL)
	{
		fprintf(stderr, "0x0D06��<env2ip>�ӻ��������л�ȡ����IP��ַ����\n");
		return -1;
	}

	if((iplen + 2)<20)
	{
		fprintf(stderr, "0x0D33��<env2ip>IP��Ϣ�洢�ռ䲻�㣡\n");
		return -1;
	}
	strcpy(tempport, strtok(NULL, ":"));
	if (tempport==NULL)
	{
		fprintf(stderr, "0x0D09��<env2ip>�ӻ��������л�ȡ���Ķ˿�ʧ�ܣ�\n");
		return -1;
	}
	if((portlen+2) < 10)
	{
		fprintf(stderr, "0x0D33��<env2ip>�˿���Ϣ�洢�ռ䲻�㣡\n");
		return -1;
	}

	strcpy(ip, tempip+2);
	strcpy(port, tempport);

	return 1;
}



/**
 * ʹ��TCP�����ӷ�ʽ����Ľ��н���
 * @param psend
 * 			������Ϣ��
 * @param precv
 * 			������Ϣ��
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
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

	/*��ȡͨ������*/
	{
		if(NULL==getenv(CHANNELNAMEENV))
		{
			fprintf(stderr, "0x0D14��<sendTcpS>��������[%s]δ���ã�\n", CHANNELNAMEENV);
			return -1;
		}
		strcpy(channelName, getenv(CHANNELNAMEENV));
	}
	
	/*�����Ϣ�ֶ�*/
	if (-1==addContent(psend, "CHANNELNAME", channelName, strlen(channelName))){
		return -1;
	}

	/*TCP��������*/
	/*�ӻ��������л�ȡ����IP�Ͷ˿�*/
	if ((env2ip(TCPADDRENV, coreip, 20, coreport, 20)) < 0)
	{
		return -1;
	}

	if((host=gethostbyname(coreip))==NULL)
	{
		herror("0x0D20��<sendTcpS>Զ�̵�ַ����ʧ�ܣ�\n");
		return -1;
	}

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("0x0D20��<sendTcpS>�׽��ִ���ʧ�ܣ�\n");
		return -1;
	}

	serv_addr.sin_family=AF_INET;
	serv_addr.sin_port=htons(atoi(coreport));
	serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
	bzero( &(serv_addr.sin_zero),8);
	if (connect(sockfd, (struct sockaddr *) &serv_addr,sizeof(struct sockaddr)) == -1)
	{
		fprintf(stderr, "0x0D21��<sendTcpS>Զ�������޷�����[%s][%s]\n", coreip,coreport);
		return -1;
	}
	
	/*��ȡ������Ϣ��ĳ���*/
	memcpy(trans, *psend+10, 10);
	sendLen = atoi(trans);

	/*��������*/
	if(send(sockfd,(void*)*psend,sendLen,0) != sendLen)
	{
		perror("0x0D22��<sendTcpS>���ķ���ʧ�ܣ�\n");
		return -1;
	}

	/*��������*/
	nsize = 0;
	while (nsize<30)
	{/*��ȡ��Ϣͷ*/
		if ((recvLen = recv(sockfd, recvbuf, 30-nsize, 0)) ==-1)
		{
			perror("0x0D22��<sendTcpS>���Ľ���ʧ�ܣ�\n");
			close(sockfd);
			return -1;
		}
		memcpy(tranmessage+nsize, recvbuf, recvLen);
		nsize += recvLen;
		if (recvLen==0)
		{
			fprintf(stderr,"0x0D22��<sendTcpS>�ͻ������ӹرգ�\n");
			close(sockfd);
			return -1;
		}
	}
	//��ȡ������Ϣ�ĳ�����Ϣ
	memcpy(slen, tranmessage+10, 10);
	tsize = atoi(slen);
	
	if (tsize>TCPRECVSIZE)
	{
		fprintf(stderr,"0x0D33��<sendTcpS>���ջ������ռ䲻�㣡\n");
		close(sockfd);
		return -1;
	}
	
	while(nsize<tsize)
	{
		if ((recvLen = recv(sockfd, recvbuf, TCPRECVSIZE, 0)) ==-1)
		{
			fprintf(stderr,"0x0D22��<sendTcpS>���Ľ��ճ���\n");
			close(sockfd);
			return -1;
		}
		memcpy(tranmessage+nsize, recvbuf, recvLen);
		nsize += recvLen;
	}
	/*��ȡ��������*/
	*precv = (char*)malloc(tsize);
	memcpy(*precv, tranmessage, tsize);

	/*�ͷ���Դ*/
	close(sockfd);

	return recvLen;
}


/**
 * ʹ��TCP�����ӷ�ʽ����Ľ��н���
 * @param psend
 * 			������Ϣ��
 * @param precv
 * 			������Ϣ��
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
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

	/*��ȡͨ������*/
	{
		if(NULL==getenv(CHANNELNAMEENV))
		{
			fprintf(stderr, "0x0D14��<sendTcpS>��������[%s]δ���ã�\n", CHANNELNAMEENV);
			return -1;
		}
		strcpy(channelName, getenv(CHANNELNAMEENV));
	}
	
	/*�����Ϣ�ֶ�*/
	if (-1==addContent(psend, "CHANNELNAME", channelName, strlen(channelName))){
		return -1;
	}

	/*TCP��������*/
	/*�ӻ��������л�ȡ����IP�Ͷ˿�*/
	if ((env2ip(TCPADDRENV, coreip, 20, coreport, 20)) < 0)
	{
		return -1;
	}

	if((host=gethostbyname(coreip))==NULL)
	{
		herror("0x0D20��<sendTcpS>Զ�̵�ַ����ʧ�ܣ�\n");
		return -1;
	}

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("0x0D20��<sendTcpS>�׽��ִ���ʧ�ܣ�\n");
		return -1;
	}

	serv_addr.sin_family=AF_INET;
	serv_addr.sin_port=htons(atoi(coreport));
	serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
	bzero( &(serv_addr.sin_zero),8);
	if (connect(sockfd, (struct sockaddr *) &serv_addr,sizeof(struct sockaddr)) == -1)
	{
		fprintf(stderr, "0x0D21��<sendTcpS>Զ�������޷�����[%s][%s]\n", coreip,coreport);
		return -1;
	}
	
	/*��ȡ������Ϣ��ĳ���*/
	memcpy(trans, *psend+10, 10);
	sendLen = atoi(trans);
	/*���֧��1024���ְ�*/
	precvTmp=(char **)malloc(1024*sizeof(char *));
	if(precvTmp==NULL)
	{
		fprintf(stderr, "����Ӧ�����ڴ�ռ�ʧ��\n");
		close(sockfd);		
		return -1;
	}	

	/*��������*/
	if(send(sockfd,(void*)*psend,sendLen,0) != sendLen)
	{
		perror("0x0D22��<sendTcpS>���ķ���ʧ�ܣ�\n");
		close(sockfd);		
		free(precvTmp);
		return -1;
	}
	/*��Ӧ�������*/
	
	iPackNum=0;
	iPackSeq=0;
	for(i=0;;i++)
	{
		/*printf("TestMQ:start read msg pack NO[%d]\n",i+1);*/
		/*��������*/
		nsize = 0;
		while (nsize<30)
		{/*��ȡ��Ϣͷ*/
			if ((recvLen = recv(sockfd, recvbuf, 30-nsize, 0)) ==-1)
			{
				perror("0x0D22��<sendTcpS>recv����ͷ����ʧ�ܣ�\n");
				iFlag=-1;
				break;
			}
			/*printf("TestMQ:recvLen=[%ld]\n",recvLen);*/
			memcpy(tranmessage+nsize, recvbuf, recvLen);
			nsize += recvLen;
			if (recvLen==0)
			{
				fprintf(stderr,"0x0D22��<sendTcpS>�ͻ������ӹرգ�\n");
				iFlag=-2;
				break;
			}
		}
		if(iFlag<0)
			break;
		//��ȡ������Ϣ�ĳ�����Ϣ
		memcpy(slen, tranmessage+10, 10);
		tsize = atoi(slen);
		
		if (tsize>TCPRECVSIZE)
		{
			fprintf(stderr,"0x0D33��<sendTcpS>���ջ������ռ䲻�㣡\n");
			iFlag=-3;
			break;
		}
		
		while(nsize<tsize)
		{
			/*printf("TestMQ recv msgbody tsize=[%d] body=[%d]\n",tsize,tsize-nsize);*/
			if ((recvLen = recv(sockfd, recvbuf, tsize-nsize, 0)) ==-1)
			{
				fprintf(stderr,"0x0D22��<sendTcpS>recv���Ľ��ճ���\n");
				iFlag=-4;
				break;
			}
			/*printf("TestMQ recv msgbody recvLen=[%ld]\n",recvLen);*/
			memcpy(tranmessage+nsize, recvbuf, recvLen);
			nsize += recvLen;
		}
		if(iFlag<0)
			break;
		/*��ȡ��������*/
		precvTmp[i] = (char*)malloc(65536);
		if (precvTmp[i]==NULL)
		{
			fprintf(stderr,"����ְ��ڴ�ռ�ʧ��Pack[%d]��\n",i);
			iFlag=-5;
			break;
		}
		memset(precvTmp[i],0,65536);
		memcpy(precvTmp[i], tranmessage, tsize);
		
		memset(strPackSeq,0,sizeof(strPackSeq));
		ret=readContent(precvTmp[i],"PACKSEQ",strPackSeq,&iFieldLen);
		if (ret==-1)
		{
			fprintf(stderr,"readContent ��ȡPACKSEQ����\n");
			/*+1��ʧ�ܺ��ͷſռ���*/
			iPackNum++;
			iFlag=-6;
			break;
		}
		/*printf("TestMQ:msg seq[%s],[%s]\n",strPackSeq,precvTmp[i]);*/
		iPackSeq=atoi(strPackSeq);
		iPackNum++;
		/*�������Ϊ0���Ľ���*/
		if(iPackSeq==0)
			break;
		/*�������ְ���*/
		if(iPackNum>1024)
			break;
		/*printf("TestMQ:end read msg pack[%d]\n",i+1);*/
			
		
	}
	/*printf("TestMQ read msg OK\n");*/
	/*�ͷ���Դ*/
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


