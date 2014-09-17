##-------------------------------------------------

include ../rules.linux.mk

##-------------------------------------------------

libapitcps_a_SRCS = Adapter2Tes_func.c Adapter2Tes_sendTcpS.c
libapitcps_a_OBJS = Adapter2Tes_func.o Adapter2Tes_sendTcpS.o
libapitcps_a_TARGET = ../lib/libapitcps.a

libapi_a_SRCS = Adapter2Tes_func.c
libapi_a_OBJS = Adapter2Tes_func.o
libapi_a_TARGET = ../lib/libapi.a

##-------------------------------------------------

all: $(libapitcps_a_TARGET) $(libapi_a_TARGET)

$(libapitcps_a_TARGET) : $(libapitcps_a_OBJS)
	@rm -f $(libapitcps_a_TARGET)
	$(AR) $(ARFLAGS) $@ $(libapitcps_a_OBJS) 
	$(AR) -x $(libapitcps_a_TARGET)
	cp $(libapitcps_a_TARGET) /home/tesadpt/lib/liblinux/libapitcps32.a

$(libapi_a_TARGET) : $(libapi_a_OBJS)
	$(AR) $(ARFLAGS) $@ $(libapi_a_OBJS)
	../CreateExport $(libapi_a_EXPORT) $(libapi_a_OBJS)

export:
	../CreateExport $(libapi_a_EXPORT) $(libapi_a_OBJS)


##-------------------------------------------------

clean:
	@rm -f $(libapitcps_a_OBJS)
	@rm -f $(libapitcps_a_TARGET)
	@rm -f $(libapi_a_OBJS)
	@rm -f $(libapi_a_TARGET)
	@echo "	make clean OK!"

##-------------------------------------------------

