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

int TesAdptSecFunc_In(char *RecvBuf,int iRecLen,char *SecBuf,int *iSecLen)
{

	return 0;
}


int TesAdptSecFunc_Out(char *RecvBuf,int iRecLen,char *SecBuf,int *iSecLen)
{
	return 0;

}
