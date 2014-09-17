INCS = -I. -I../include

CPPFLAGS=

CCC = g++ 
CXXFLAGS=-g -O0 -Wall -W
CCFLAGS = $(CXXFLAGS) $(CPPFLAGS) $(INCS)

CC = g++
CFLAG =-g -O0 -Wall -W
CFLAGS = $(CFLAG) $(CPPFLAGS) $(INCS)

ESQL = 
DBCFLAGS = $(CPPFLAGS)  -I. -I../include
DBLDFLAGS =  

LD = $(CCC) 
LDFLAGS = 

AR = ar
ARFLAGS = -r

##-------------------------------------------------

libapi_a_EXPORT = $(HOME)/lib/libapi.exp

EXPFLAGS =  -bE:$(libapi_a_EXPORT)

IMPFLAGS =  -bI:$(libapi_a_EXPORT)

DSOLDFLAGS=-brtl $(EXPFLAGS)
#DSOLDFLAGS=-brtl
DSOLIBCFLAGS=
DSOLIBCXXFLAGS=
DSOLIBLDFLAGS=-G -Wl,-G $(IMPFLAGS)
OSFLAG=
TMLDFLAGS=

##-------------------------------------------------

.SUFFIXES: .ecpp .C .cpp .ec .c .hh .h .so .o

.c.o          :
	$(CC) $(CFLAGS) -c $<

.cpp.o          :
	$(CCC) $(CCFLAGS) -c $<

.C.o          :
	$(CCC) $(CCFLAGS) -c $<

.o.so           :
	$(LD) $(DSOLIBLDFLAGS) $(LDFLAGS) $< -o $@

##-------------------------------------------------

