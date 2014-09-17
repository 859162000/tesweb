##-------------------------------------------------
INCS = -I. -I../src/include
CPPFLAGS= 
CCC = g++ 
CXXFLAGS=-g -O0 -Wall -W
CCFLAGS = $(CXXFLAGS) $(CPPFLAGS) $(INCS)

CC = gcc
CFLAG = -g -O0 -Wall -W
CFLAGS = $(CFLAG) $(CPPFLAGS) $(INCS)
LIBS = -L../src/lib -lgtest4linux -lapitcps

LD = $(CCC) 
LDFLAGS =

AR = ar
ARFLAGS = -r
##-------------------------------------------------
