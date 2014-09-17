/*
 * Adapter2Tes4C.h
 *
 *  Created on: 2009-12-1
 *      Author: Conan
 */

#ifndef ADAPTER2TES_H_
#define ADAPTER2TES_H_

#define TCPRECVSIZE 65536				/*TCP接收缓冲区大小*/
#define TCPADDRENV "TESADDR"				/*TCP通讯模式的地址环境变量*/
#define CONFIGNAMEENV "ADAPTERCONFIG"	/*配置文件环境变量名称*/
#define CHANNELNAMEENV "CHANNELNAME"	/*通道名称环境变量*/

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 初始化消息体空间
 * @return	初始化完成的消息体空间，失败返回NULL
 */
char* messageInit();

/**
 * 初始化指定标志位的消息体
 * @param len
 * 			要初始化的空间大小
 * @param flag
 * 			要指定的消息体标志，C格式字符串
 * @return	完成初始化的消息体，失败返回NULL
 */	
char* allInit(int, char*);	

/**
 * 释放消息体空间
 * @param target
 * 			要释放空间的消息体
 * @param flag
 * 			要指定的消息体标志
 * @return	目前固定返回1
 */
int infoDel(char**);		

/**
 * 向消息体中添加字段内容
 * @param message
 * 			已经初始化的消息体
 * @param name
 * 			字段名称，C格式字符串
 * @param content
 * 			字段内容
 * @param lenC
 * 			内容长度
 * @return	添加成功返回当前的消息体长度，失败返回-1
 */
int addContent(char**, char*, char*, int);	

/**
 * 从消息体中读取字段内容
 * @param recv
 * 			待读取的消息体
 * @param name
 * 			字段名称，C格式字符串
 * @param target
 *			读取出字段内容的存储空间
 * @param len
 * 			存储空间的可用长度
 * @return	成功返回1，失败返回-1
 */
int readContent(char*, char*, char*, int*);	

/**
 * 使用TCP短连接方式与核心进行交互
 * @param psend
 * 			请求消息体
 * @param precv
 * 			返回消息体
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int sendTcpS(char**, char**);			
int sendTcpSMul(char**, char***);			

/**
 * 判断消息体空间是否充足
 * @param message
 * 			目标消息体空间
 * @param increment
 * 			需要存储的字段空间
 * @return	要完成字段存储消息体需要扩充的空间，出错返回-1
 */
int isEnough(char*, int);	

/**
 * 对消息体空间进行扩充
 * @param message
 * 			目标消息体空间
 * @param len
 * 			需要的消息体空间增量
 * @return	成功返回新的消息体空间，失败返回NULL
 */				
char* increMessage(char*, int);		

/**
 * 复制消息体
 * @param from
 * 			源消息体
 * @param to
 * 			目标消息体
 * @return	成功返回1，失败返回-1
 */		
int copyMessage(char*, char*);	

/**
 * 从环境变量中获取IP端口信息
 * @param env
 * 			环境变量名称
 * @param ip
 * 			存放IP地址的空间
 * @param iplen
 *			存放IP地址空间的大小
 * @param port
 * 			存放端口信息的空间
 * @param portlen
 * 			存放端口信息空间的长度
 * @return	成功返回1，失败返回-1
 */		
int env2ip (char*, char*, int, char*, int);	

/**
 * 向核心进行注册并接收配置信息
 * @param regC
 * 			构建好的注册消息体
 * @param configC
 * 			返回的配置文件消息
 * @return	成功返回接收到消息体的长度，失败返回-1
 */
int reg2tes(char **, char **);	

/**
 * 与核心进行交互
 * @param sendC
 * 			构建好的请求消息体
 * @param recvC
 * 			核心返回的响应消息体
 * @return	成功返回接收到消息体的长度，失败返回-1
 */		
int sendContent(char**, char**);		
int sendContentMul(char**, char***);		

#ifdef __cplusplus
}
#endif

#endif /* ADAPTER2TES4C_H_ */
