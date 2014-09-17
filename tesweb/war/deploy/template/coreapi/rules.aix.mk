INCS = -I. -I../include

CPPFLAGS=

CCC = xlC
CXXFLAGS=-O3 -qstrict -qinline -qfuncsect -q64 -g
CCFLAGS = $(CXXFLAGS) $(CPPFLAGS) $(INCS)

CC = cc
CFLAG = -O3 -qstrict -qinline -qinfo=pro -qfuncsect -qcpluscmt -q64 -g
CFLAGS = $(CFLAG) $(CPPFLAGS) -I. -g $(INCS)

ESQL = 
DBCFLAGS = $(CPPFLAGS)  -I. -I../include
DBLDFLAGS =  

LD = $(CCC) 
LDFLAGS = -q64

AR = ar
ARFLAGS = -rv -X64

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

