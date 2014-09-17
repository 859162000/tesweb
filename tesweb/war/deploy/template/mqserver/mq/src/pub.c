#include <string.h>
#include <unistd.h>
#ifdef LINUX
#include "pub.h"
#else
#include "sim.h"
#endif
#include "GI_Datatype.h"
#include "Custom_Interface.h"



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


int	GetConfig(FILE *pfp,char *pitem,char *pval)
{
	char	buf[1204];

	while (fgets(buf,1024,pfp) != NULL )
	{
		TrimAll(buf);
		if (buf[0] == '#') continue;
		else if (GetConfigByItem(buf,pitem,pval)) continue;
		else	return(0);
	}
	return(-1);
}

int	GetConfigByItem(char *pline,char *pitem,char *pval)
{
	char	*p,*p1;
	int	len=0;

	p = pline;
	p1=pval;
	while ((*p != '\n') && (*p != '\0'))
	{
		if (*p != '=') {p++;len++;}
		else
		{
			if (len != strlen(pitem)) return(-1);

			if (memcmp(pline,pitem,len)==0)
			{
				p++;
				while ((*p != '\n') && (*p != '\0'))
				{
					*p1=*p; p++; p1++;
				}
				return(0);
			}
			else	return(-1);
		}
	}
	return(-1);
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


/*以二进制方式打印字节数组*/
int GetBinStr(void* Bin,int Binlen,char* Binstr)
{
	char tmpstr[20];
	int i=0;
	Binstr[0] = '\0';
	for(i=0;i<Binlen;i++)
	{
		sprintf(tmpstr," %2X",*((unsigned char*)Bin+i));
		strcat(Binstr,tmpstr);
	}
	return(0);
}
