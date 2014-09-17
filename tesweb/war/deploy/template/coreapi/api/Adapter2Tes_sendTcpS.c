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
 * ����Ľ���ע�Ტ����������Ϣ
 * @param regC
 * 			�����õ�ע����Ϣ��
 * @param configC
 * 			���ص������ļ���Ϣ
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
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
	
	/*��ȡ�����ļ�*/
	/*20100313 by liuzj ���²����ã��ֲ���Ҫ*/
	/*
	{
		if(NULL==getenv(CONFIGNAMEENV))
		{
			fprintf(stderr, "0x0D14��<reg2tes>��������[%s]δ���ã�\n", CONFIGNAMEENV);
			return -1;
		}
		strcpy(filename, getenv(CONFIGNAMEENV));
		if ((fp = fopen(filename, "r")) == NULL) 
		{
	  	fprintf(stderr, "0x0D15��<reg2tes>��ȡ�ļ�[%s]ʱ�����쳣��\n", filename);
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

	/*������Ϣͷ*/
	memcpy(*regC, "       REG", 10);
	/*����ע��*/
	if (-1==(result=sendTcpS(regC, configC)))
	{
		fprintf(stderr, "0x0D17��<reg2tes>������ע��ʧ�ܣ�\n");
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
 * ����Ľ���ע��
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
 */
int unreg2tes()
{
	int result = 0;
	char* unregC = messageInit();
	char* backmessage = NULL;
	
	/*������Ϣͷ*/
	memcpy(unregC, "     UNREG", 10);
	
	/*����ע��*/
	if (-1==(result=sendTcpS(&unregC, &backmessage))){
		fprintf(stderr, "0x0D17��<reg2tes>������ע��ʧ�ܣ�\n");
		return -1;
	}
	
	infoDel(&unregC);
	infoDel(&backmessage);
	
	return result;
}

/**
 * ����Ľ��н���
 * @param sendC
 * 			�����õ�������Ϣ��
 * @param recvC
 * 			���ķ��ص���Ӧ��Ϣ��
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
 */
int sendContent(char **sendC, char **recvC)
{
	int result = 0;
	
	/*������Ϣͷ���λ*/
	memcpy(*sendC, "   MESSAGE", 10);
	if (-1==(result=sendTcpS(sendC, recvC))){
		fprintf(stderr, "0x0D18��<sendContent>�����ķ���ʧ�ܣ�\n");
		return -1;
	}
	return result;
}


int sendContentMul(char **sendC, char ***recvC)
{
	int result = 0;
	
	/*������Ϣͷ���λ*/
	memcpy(*sendC, "   MESSAGE", 10);
	result=sendTcpSMul(sendC, recvC);
	if (result<0)
	{
		fprintf(stderr, "0x0D18��<sendContent>�����ķ���ʧ�ܣ�\n");
		return result;
	}
	return result;
}
