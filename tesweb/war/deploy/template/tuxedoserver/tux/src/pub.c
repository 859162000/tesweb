#include        <sys/time.h>
#include        <time.h>
#include "pub.h"
#include "GI_Datatype.h"
#include "Custom_Interface.h"
#include <string.h>


void gettimestr(char *timestr)
{
	struct timeval Tp;
	struct tm* ltime;
	int usec;

	gettimeofday (&Tp, NULL);

	usec = Tp.tv_usec/1000;
	ltime = localtime(&(Tp.tv_sec));
	
	sprintf( timestr, "%04d%02d%02d %02d:%02d:%02d.%03d",
		ltime->tm_year+1900, ltime->tm_mon+1, ltime->tm_mday,
		ltime->tm_hour, ltime->tm_min, ltime->tm_sec,
		usec);
}

void gettimestr_res(char *timestr)
{
	struct timeval Tp;
	struct tm* ltime;
	int usec;
	
	gettimeofday (&Tp, NULL);

	usec = Tp.tv_usec/1000;
	ltime = localtime(&(Tp.tv_sec));
	
	sprintf(timestr, "%04d-%02d-%02d %02d:%02d:%02d.%03d",
		ltime->tm_year+1900, ltime->tm_mon+1, ltime->tm_mday,
		ltime->tm_hour, ltime->tm_min, ltime->tm_sec,
		usec);
}



int	TrimRight(char *pstr)
{
	char	*p;

	if ( strlen(pstr)<2 ) return(0);

	p = pstr + strlen(pstr);

	while (p != pstr)
	{
		p--;
		if ((*p==' ') || (*p=='\t')) *p='\0'; 
		else break;
	}

	return(0);
	
}
	

int	TrimLeft(char *pstr)
{
	char	*p,*p1;

	if ( strlen(pstr)<2 ) return(0);

	p = pstr;
	p1= pstr;
	
	while ((*p != '\n') && (*p != '\0'))
	{
		if ((*p==' ') || (*p=='\t')) p++;
		else break;
	}

	while ((*p != '\n') && (*p != '\0'))
	{
		*p1=*p; p++; p1++;
	}

	while ((*p1 != '\n') && (*p1 != '\0'))
	{
		*p1='\0'; p1++;
	}

	return(0);
}

int	TrimAll(char *pstr)
{
	TrimLeft(pstr); TrimRight(pstr); return(0);
}

