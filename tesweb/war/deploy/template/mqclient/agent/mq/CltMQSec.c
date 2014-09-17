#include <sys/time.h>
#include <stdio.h>
#include <errno.h>
#include <fcntl.h>
#include <dlfcn.h>
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

#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "Util.h"
#include "comm.h"

int TesAdptSecFunc_In(char *RecvBuf,int iRecLen,char *SecBuf,int *iSecLen)
{
	int i;
	*iSecLen=iRecLen;
	if(iRecLen>10)
		for(i=0;i<10;i++)
			SecBuf[i]=RecvBuf[i]+2;
	else
		return -1;
	memcpy(SecBuf+10,RecvBuf+10,iRecLen-10);
	return 0;
}


int TesAdptSecFunc_Out(char *RecvBuf,int iRecLen,char *SecBuf,int *iSecLen)
{
	int i;
	*iSecLen=iRecLen;
	if(iRecLen>10)
		for(i=0;i<10;i++)
			SecBuf[i]=RecvBuf[i]-2;
	else
		return -1;
	memcpy(SecBuf+10,RecvBuf+10,iRecLen-10);
	return 0;

}
