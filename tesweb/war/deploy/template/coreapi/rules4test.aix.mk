##-------------------------------------------------
INCS = -I. -I../src/include
CPPFLAGS= 
CCC = xlC
CXXFLAGS=-O3 -qstrict -qinline -qfuncsect -q64 -g $(INCS)
CCFLAGS = $(CXXFLAGS) $(CPPFLAGS) $(INCS)

CC = cc
CFLAG = -O3 -qstrict -qinline -qinfo=pro -qfuncsect -qcpluscmt -q64 -g
CFLAGS = $(CFLAG) $(CPPFLAGS) -I. -g $(INCS)
LIBS = -L../src/lib -lgtest4aix -lapitcps

LD = $(CCC) 
LDFLAGS = -q64

AR = ar
ARFLAGS = -rv -X64
##-------------------------------------------------
