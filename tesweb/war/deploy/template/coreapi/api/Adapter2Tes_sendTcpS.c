/*
 * Adapter2Tes_sendTcpS.c
 *
 *  Created on: 2009-12-1
 *      Author: Conan
 */

#include "Adapter2Tes.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <strings.h>

/**
 * 向核心进行注册并接收配置信息
 * @param regC
 * 			构建好的注册消息体
 * @param configC
 * 			返回的配置文件消息
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int reg2tes(char **regC, char **configC)
{
	int result = 0;
	/*
	FILE *fp = NULL;
	char buffer[128*1024];
	char ch; 
	int i = 0;
	char filename[512];
	*/
	fprintf(stdout,"Start reg to core!\n");
	
	/*读取配置文件*/
	/*20100313 by liuzj 陈勇测试用，现不需要*/
	/*
	{
		if(NULL==getenv(CONFIGNAMEENV))
		{
			fprintf(stderr, "0x0D14：<reg2tes>环境变量[%s]未设置！\n", CONFIGNAMEENV);
			return -1;
		}
		strcpy(filename, getenv(CONFIGNAMEENV));
		if ((fp = fopen(filename, "r")) == NULL) 
		{
	  	fprintf(stderr, "0x0D15：<reg2tes>读取文件[%s]时出现异常！\n", filename);
	  	return -1;
	  }
	  while(i<(128*1024))
	 	{
	  	ch = fgetc(fp);
		  if (ch != EOF)
		  {
		  	buffer[i++] = ch;
		  }
		  else
		  {
		  	break; 
		  }
	 	}
	  fclose(fp);
	}
	*/

	/*设置消息头*/
	memcpy(*regC, "       REG", 10);
	/*进行注册*/
	if (-1==(result=sendTcpS(regC, configC)))
	{
		fprintf(stderr, "0x0D17：<reg2tes>适配器注册失败！\n");
		return -1;
	}
	printf("Reg to core success!\n");
/*	if (-1==(result=addContent(configC, "CONFIGINFO", buffer, i))){
		return -1;
	}
*/	
	return result;
}

/**
 * 向核心进行注销
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int unreg2tes()
{
	int result = 0;
	char* unregC = messageInit();
	char* backmessage = NULL;
	
	/*设置消息头*/
	memcpy(unregC, "     UNREG", 10);
	
	/*进行注册*/
	if (-1==(result=sendTcpS(&unregC, &backmessage))){
		fprintf(stderr, "0x0D17：<reg2tes>适配器注销失败！\n");
		return -1;
	}
	
	infoDel(&unregC);
	infoDel(&backmessage);
	
	return result;
}

/**
 * 与核心进行交互
 * @param sendC
 * 			构建好的请求消息体
 * @param recvC
 * 			核心返回的响应消息体
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int sendContent(char **sendC, char **recvC)
{
	int result = 0;
	
	/*设置消息头标记位*/
	memcpy(*sendC, "   MESSAGE", 10);
	if (-1==(result=sendTcpS(sendC, recvC))){
		fprintf(stderr, "0x0D18：<sendContent>请求报文发送失败！\n");
		return -1;
	}
	return result;
}


int sendContentMul(char **sendC, char ***recvC)
{
	int result = 0;
	
	/*设置消息头标记位*/
	memcpy(*sendC, "   MESSAGE", 10);
	result=sendTcpSMul(sendC, recvC);
	if (result<0)
	{
		fprintf(stderr, "0x0D18：<sendContent>请求报文发送失败！\n");
		return result;
	}
	return result;
}
