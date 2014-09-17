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

#include "Util.h"

/********************************************************************************
***			在文件中记录跟踪信息
***			      2005/02/01
********************************************************************************/
void AscTraceMsg(int iSysCls,int iCurCls, char *pcFileName, char *pcMessage, ... )
{
   int  fd, iLen, iNameLen, iCount=0, iRename,iWeekDay=0,iMWeekDay=0;
   FILE *iFileDes;
   off_t lFileSize = 0;
   char acBuffer[65536];
   char acDateAndTime[30];
   char acBakFileName[256] ;
   char acFileName[256];
   char acTmp[256];
   char *pcTraceFilePath;
   char *pcTraceOn;
   char sYearMonth[6+1];
   struct tm *tbp;
   struct stat fileinfo;
   long ltime;
   char acWeekDay[7][4]={"sun","mon","tue","wen","thu","fri","sat"};
   int  iMonth,iDay,iOldMonth,iOldDay;

   va_list ArgList;
 

   /*assert( pcFileName  != NULL );
   assert( pcMessage   != NULL );*/
   memset( acBuffer,      0, sizeof(acBuffer));
   memset( acDateAndTime, 0, 30 );
   memset( acBakFileName, 0, 256     );
   memset( acFileName,    0, 256);
   memset( acTmp,         0, 256);
 
   time( (long *)&ltime );
   tbp=localtime( &ltime );
   iWeekDay=tbp->tm_wday;
   iMonth=tbp->tm_mon+1;
   iDay= tbp->tm_mday;

   strcpy( acFileName, pcFileName );
/*
   strcat(acFileName, ".");
   memset(sYearMonth,0,sizeof(sYearMonth));
   sprintf(sYearMonth,"%04d%02d",tbp->tm_year+1900,iMonth);
   strcat(acFileName,sYearMonth);
*/
/*
   strcat(acFileName, acWeekDay[iWeekDay]);
*/
   /*if ( ( iFileDes = fopen( acFileName, "a" ) ) == (FILE *)NULL )*/
   if ( ( iFileDes = fopen( acFileName, "a+" ) ) == (FILE *)NULL )
      return ;
 
   fd = fileno(iFileDes);  /* Add lock to control parallel processing */
   lockf(fd, F_LOCK, 0l);
/*
   fstat(fd,&fileinfo);
   tbp=localtime(&(fileinfo.st_mtime));
   iOldMonth=tbp->tm_mon;
   iOldDay= tbp->tm_mday;
*/
/*
   if(iOldMonth!=iMonth||iOldDay!=iDay)
  { 
	fclose(iFileDes);
   	if ( ( iFileDes = fopen( acFileName, "w" ) ) == (FILE *)NULL )
      		return ;
	GetCurrentDateTime2(acDateAndTime );
   }
*/
   va_start( ArgList, pcMessage );
   vsprintf( (char *)acBuffer, pcMessage, ArgList );
   va_end( ArgList );
/* 
   iCount = strlen( (char *)acBuffer );
   if ( iCount >= 8192 )
   {
       acBuffer[8192-1] = '\0';
   }
 
*/
   fprintf( iFileDes, "%s\n",acBuffer);
   fflush( iFileDes );

   lockf(fd, F_ULOCK, 0l);
   fclose( iFileDes );

   return;
}
 
/********************************************************************************
***			去除字符串尾控制字符及空格
***			      2005/02/01
********************************************************************************/

int strip(char * str)
{
	int len;
	int i;
	if(str==NULL)
		return -1;

	len=strlen(str);
	if(len>0)
	{
		for(i=len-1;i>=0;i--)
		{
			if( (str[i]=='\t') || (str[i]==32) || (str[i]==10) || (str[i]==13))
			{
				str[i]=0;
			}
			else
				break;
		}
	}
	else
	{
		return -2;
	}
	return 0;
}


/********************************************************************************
***			取配置文件中配置信息
***			      2005/02/01
********************************************************************************/
char* ReadIni(char* fname,char* section,char* tag)
{
	FILE *fp;
	char readbuf[1024];
	char strtmp[1024];
	char i;
	char len;
	char sec[256];
	char tagitem[256];
   	char * pSendBuf;
   	char * pRecvBuf;

	
	if((fname==NULL) || (section==NULL) || (tag==NULL))
	{
		return NULL;
	}
	fp=fopen(fname,"r");
	if(fp==NULL)
	{
		return NULL;
	}

	memset(sec,0,sizeof(sec));
	sprintf(sec,"[%s]",section);
	memset(tagitem,0,sizeof(tagitem));
	sprintf(tagitem,"%s=",tag);

	memset(readbuf,0,sizeof(readbuf));
	memset(strtmp,0,sizeof(strtmp));
	while(!feof(fp))
	{
		memset(readbuf,0,sizeof(readbuf));
		fgets(readbuf,1024,fp);
		if(strip(readbuf)!=0)
			continue;
		if( strlen(readbuf) < strlen(sec) )
			continue;
		
		if( !strncmp(readbuf,sec,strlen(sec)) )
		{
			while(!feof(fp))
			{
				memset(readbuf,0,sizeof(readbuf));
				fgets(readbuf,1024,fp);
				if(strip(readbuf)!=0)
					continue;
				if(strlen(readbuf)>0)
				{
					if(readbuf[0]=='[')
						break;
				}
				if( strlen(readbuf) < strlen(tagitem) )
					continue;
				if( !strncmp(readbuf,tagitem,strlen(tagitem)) )
				{
					fclose(fp);
					return (char*)readbuf+strlen(tagitem);
				
				}
				
			}
			break;
			
		}
		
	}
	fclose(fp);
	return NULL;
}


/********************************************************************************
***			根据长度iMaxLen填充字符串
***			      2005/02/01
********************************************************************************/
int fillstrR(char *str,char ch,int iMaxLen)
{
	int i;
	int iOrigLen;
	
	if(str==NULL)
		return -1;
	iOrigLen=strlen(str);
	if(iOrigLen>=iMaxLen)
		return 0;
	for(i=0;i<(iMaxLen-iOrigLen);i++)
	{
		sprintf(str,"%s%c",str,ch);
	}
	return 0;
}

/********************************************************************************
***			将进程变为守护进程
***			      2005/02/01
********************************************************************************/
int DeamonStart()
{
    int iPid;
    int iRetCode;
    int iFileHandle;
    register int iChildPid;

    iPid = getppid();
    if(iPid != 1)
    {
        #ifdef SIGTTOU
            signal(SIGTTOU, SIG_IGN);
        #endif
        #ifdef SIGTTIN
            signal(SIGTTIN, SIG_IGN);
        #endif
        #ifdef SIGTSTP
            signal(SIGTSTP, SIG_IGN);
        #endif

        iChildPid = fork();
        if(iChildPid < 0)
            return(-1);
        else if(iChildPid > 0)
            exit(0);

        setpgrp();
        signal(SIGHUP, SIG_IGN);

        iChildPid = fork();
        if(iChildPid < 0)
            return(-1);
        else if(iChildPid > 0)
            exit(0);
    }

    errno = 0;
    chdir("/");
    umask(0);
    signal(SIGCLD, SIG_IGN);

    return(0);
}


/*获取系统时间，格式YYYYMMDDHHMMSS*/
void GetCurrentDateTime(char *strDateTime)
{
        time_t t;
        struct tm *ts;
        int i;

        t = time(NULL);
        ts = localtime(&t);
        sprintf(strDateTime,"%04d%02d%02d%02d%02d%02d\0", ts->tm_year+1900,ts->tm_mon+1,ts->tm_mday,ts->tm_hour, ts->tm_min, ts->tm_sec);
}

/*获取系统时间,格式:YYYY/MM/DD HH:MM:SS*/
void GetCurrentDateTime2(char *strDateTime)
{
        time_t t;
        struct tm *ts;
        int i;

        t = time(NULL);
        ts = localtime(&t);
        sprintf(strDateTime,"%04d/%02d/%02d %02d:%02d:%02d\0", ts->tm_year+1900,ts->tm_mon+1,ts->tm_mday,ts->tm_hour, ts->tm_min, ts->tm_sec);
}


/*获取系统时间,格式:HH:MM:SS*/
void GetCurrentDateTime3(char *strDateTime)
{
        time_t t;
        struct tm *ts;
        int i;

        t = time(NULL);
        ts = localtime(&t);
        sprintf(strDateTime,"%02d:%02d:%02d\0", ts->tm_hour, ts->tm_min, ts->tm_sec);
}

void HexLog(char *sLogFile,char *sRecvBuf,int iRecvLen)
{
	int i,j;
	char strTmp[65536];
	char sTmpBuf[65536];
		
	if((sLogFile==NULL)||(sRecvBuf==NULL)||(iRecvLen<0)||(iRecvLen>65536))
		return;
	memset(sTmpBuf, 0, sizeof(sTmpBuf));
	memset(strTmp,0,sizeof(strTmp));
	strcpy(sTmpBuf,"------------------------------------------------------------------------\n");
	strcat(sTmpBuf,"       00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n");
	
	for(i=0;i<iRecvLen;i++)
	{
		if ( i%16 == 0 )
		{ 
			if(i>0)
			{
				strcat(sTmpBuf,"\t");
			}
			else
			{
				sprintf(strTmp,"(%04x) ",i);
				strcat(sTmpBuf,strTmp);
			}	
			if(i>15)
			{
				for(j=(i-16);j<i;j++)
				{
					if(*(unsigned char *)(sRecvBuf+j)<32)
						strcat(sTmpBuf,"*");
					else
					{
						sprintf(strTmp,"%c",*(char *)(sRecvBuf+j));
						strcat(sTmpBuf,strTmp);
					}
				}
			}
			if(i>0)
			{
				strcat(sTmpBuf,"\n");
				sprintf(strTmp,"(%04x) ",i);
				strcat(sTmpBuf,strTmp);
			}				
		}
		
		sprintf(strTmp,"%02x ",*(unsigned char *)(sRecvBuf+i));
		strcat(sTmpBuf,strTmp);
		if(i==(iRecvLen-1))
		{
			for(j=1;j<(16-i%16);j++)
			{
				strcat(sTmpBuf,"   ");
			}
			strcat(sTmpBuf,"\t");
			for(j=i-(i%16);j<=i;j++)
			{
				if(*(unsigned char *)(sRecvBuf+j)<32)
					strcat(sTmpBuf,"*");
				else
				{
					sprintf(strTmp,"%c",*(char *)(sRecvBuf+j));
					strcat(sTmpBuf,strTmp);
				}
			}
			strcat(sTmpBuf,"\n");
		}
	}
	strcat(sTmpBuf,"------------------------------------------------------------------------");
       	AscTraceMsg(NULL,NULL,sLogFile, "%s", sTmpBuf);
	
	return;
}


/*将16进制字符串转换为数字,如字符串2F转换成数字0x2F*/
int atox(char *str)
{
        int slen;
        int i;
        unsigned int iRes;
        int j;
        unsigned int xxx;
        unsigned int cc;
        
        if(str==NULL)
                return 0;
        slen=strlen(str);
        if(slen==0)
                return 0;
        slen=2;
        iRes=0;
        for(i=0;i<slen;i++)
        {
                cc=str[slen-i-1];
                if((cc>=48)&&(cc<=57))
                {
                        xxx=1;
                        for(j=0;j<i;j++)
                        {
                                xxx=xxx*16;
                        }
                        iRes=iRes+((cc-48)*xxx);
                }
                else if((cc>=65)&&(cc<=80))
                {
                        xxx=1;
                        for(j=0;j<i;j++)
                        {
                                xxx=xxx*16;
                        }
                        iRes=iRes+((cc-55)*xxx);
                }
                else if((cc>=97)&&(cc<=122))
                {
                        xxx=1;
                        for(j=0;j<i;j++)
                        {
                                xxx=xxx*16;
                        }
                        iRes=iRes+((cc-87)*xxx);
                }
                else
                        break;
        }
        return iRes;
}

/*
int WriteIni(char *FileName,char *Section,char *tag,char *strIni)
{
  FILE *fpProf,*fpTmp;
  char szBuf[MAXBUFLEN+1],*psz1,*psz;
  char szTmpFile[20];
  int iRet,iFunRet=-1;
 
  fpProf=fopen(FileName,"r");
  if (fpProf==NULL) 
  	return -2;
  tmpnam(szTmpFile);
  fpTmp=fopen(szTmpFile,"w");
 
  while(!feof(fpProf)) {
    if (fgets(szBuf,MAXBUFLEN,fpProf)==NULL) break;
    psz=szBuf;
    fputs(szBuf,fpTmp);
    while(*psz==' ' || *psz=='\t') psz++;
    if (*psz=='[') {  
       psz++;
       while(*psz==' ' || *psz=='\t') psz++;
       psz1=psz;
       while(*psz!=']' && *psz!='\0') psz++;
       if (*psz=='\0') continue;
       while(*psz==' ') psz--;
       *psz='\0';
       if (strcmp(psz1,Section)==0) {
	  iFunRet=profPutEntry(fpProf,fpTmp,tag,strIni);
	  if (iFunRet==0) {
	     fclose(fpProf);
	     fclose(fpTmp);
	     fpTmp=fopen(szTmpFile,"r");
	     fpProf=fopen(FileName,"w");
	     while(!feof(fpTmp)) 
	     {
               if (fgets(szBuf,MAXBUFLEN,fpTmp)==NULL) 
               		break;
	   	fputs(szBuf,fpProf);
	     }
	  }
	  break;
       }
    }
  }
  fclose(fpTmp);
  fclose(fpProf);
  unlink(szTmpFile);
  return(iFunRet);
}
*/
int profPutEntry(FILE *fpProf,FILE *fpTmp,char *pszEntry,char *pszVal)
{
	char szBuf[MAXBUFLEN+1],szBuf1[MAXBUFLEN+1],*psz1,*psz2,*psz;
	char szTmp[MAXBUFLEN+1];
	int iLen;
	
	while(!feof(fpProf)) 
	{
		if (fgets(szBuf,MAXBUFLEN,fpProf)==NULL) 
			break;
		psz=szBuf;
		strcpy(szTmp,szBuf);
	
		while(*psz==' ' || *psz=='\t') psz++;
	
		if (*psz=='*')
		{
			fputs(szTmp,fpTmp);
			continue;
		}   
		if (*psz=='/' && *(psz+1)=='/') 
		{ 
			fputs(szTmp,fpTmp);
			continue;
		}
	
		if (*psz=='\0' || *psz==0x0d || *psz==0x0a)
		{ 
			fputs(szTmp,fpTmp);
			continue;
		}
	
		if (*psz=='[') 
		{ 
			psz1=psz;
			while(*psz!=']' && *psz!='\0') psz++;
				if (*psz==']') 
					break;
			psz=psz1;
		}
		psz1=psz;
		while(*psz!='=' && *psz!='\0') 
			psz++;
		if (*psz=='\0')
		{ 
			fputs(szTmp,fpTmp);
			continue;
		}
		psz2=psz+1;
		if (psz1==psz)
		{ 
			fputs(szTmp,fpTmp);
			continue;
		}
		*psz='\0';
		psz--;
		while(*psz==' ' || *psz=='\t') 
		{
			*psz='\0';
			psz--;
		}
	
		if (strcmp(psz1,pszEntry)==0) 
		{
			strcpy(szBuf1,szBuf);
			psz=psz2;
			while(*psz==' ' || *psz=='\t') 
				psz++;
			while (*psz!='\0' && *psz!=0x0d && *psz!=0x0a) 
			{
	  			if (*psz=='/' && *(psz+1)=='/') 
	  				break;
	  			psz++;
			}
			sprintf(szBuf1,"%s=%s%s",szBuf,pszVal,psz);
			strcpy(szBuf,szBuf1);
			fputs(szBuf,fpTmp);
			while(!feof(fpProf)) 
			{
	  			if (fgets(szBuf,MAXBUFLEN,fpProf)==NULL) 
	  				break;
	  			fputs(szBuf,fpTmp);
			}
			return(0);
		}
		fputs(szTmp,fpTmp);
	}
	return(-1);
}



int	TesLog(char *LogFileName,int LogLevel ,int RunLevel ,char *pdesc,...)
{
	va_list	ap;
	FILE	*fp;
	char	timestr[24];
    time_t t;
    struct tm *ts;

	/*当调用的日志级别大于当前运行的日志级别时，不记日志*/
	/*DEBUG(3) INF(2) ERROR(1) FATAL(0)*/
	if ( LogLevel>RunLevel ) return(0);

	memset(timestr,0x00,sizeof(timestr));


    t = time(NULL);
    ts = localtime(&t);
    sprintf(timestr,"%02d:%02d:%02d\0",ts->tm_hour, ts->tm_min, ts->tm_sec);

	va_start(ap,pdesc);

	fp = fopen(LogFileName,"a+");
	if (fp == NULL)
	{
		printf("[%s] ",timestr);
		vprintf(pdesc,ap);
		printf("\n");
	}
	else
	{
		fprintf(fp,"[%s] ",timestr);
		vfprintf(fp,pdesc,ap);
		fprintf(fp,"\n");
		fclose(fp);
	}
		
	va_end(ap);

	return(0);
}



void HexLogStr(char *sRecvBuf,int iRecvLen,char *logstr)
{
	int i,j;
	char strTmp[65536];
	char sTmpBuf[65536];
		
	if((logstr==NULL)||(sRecvBuf==NULL)||(iRecvLen<0)||(iRecvLen>65536))
		return;
	memset(sTmpBuf, 0, sizeof(sTmpBuf));
	memset(strTmp,0,sizeof(strTmp));
	strcpy(sTmpBuf,"------------------------------------------------------------------------\n");
	strcat(sTmpBuf,"       00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n");
	
	for(i=0;i<iRecvLen;i++)
	{
		if ( i%16 == 0 )
		{ 
			if(i>0)
			{
				strcat(sTmpBuf,"\t");
			}
			else
			{
				sprintf(strTmp,"(%04x) ",i);
				strcat(sTmpBuf,strTmp);
			}	
			if(i>15)
			{
				for(j=(i-16);j<i;j++)
				{
					if(*(unsigned char *)(sRecvBuf+j)<32)
						strcat(sTmpBuf,"*");
					else
					{
						sprintf(strTmp,"%c",*(char *)(sRecvBuf+j));
						strcat(sTmpBuf,strTmp);
					}
				}
			}
			if(i>0)
			{
				strcat(sTmpBuf,"\n");
				sprintf(strTmp,"(%04x) ",i);
				strcat(sTmpBuf,strTmp);
			}				
		}
		
		sprintf(strTmp,"%02x ",*(unsigned char *)(sRecvBuf+i));
		strcat(sTmpBuf,strTmp);
		if(i==(iRecvLen-1))
		{
			for(j=1;j<(16-i%16);j++)
			{
				strcat(sTmpBuf,"   ");
			}
			strcat(sTmpBuf,"\t");
			for(j=i-(i%16);j<=i;j++)
			{
				if(*(unsigned char *)(sRecvBuf+j)<32)
					strcat(sTmpBuf,"*");
				else
				{
					sprintf(strTmp,"%c",*(char *)(sRecvBuf+j));
					strcat(sTmpBuf,strTmp);
				}
			}
			strcat(sTmpBuf,"\n");
		}
	}
	strcat(sTmpBuf,"------------------------------------------------------------------------");
    strcpy(logstr,sTmpBuf);
	
	return;
}
