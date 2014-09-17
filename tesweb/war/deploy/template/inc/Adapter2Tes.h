/*
 * Adapter2Tes4C.h
 *
 *  Created on: 2009-12-1
 *      Author: Conan
 */

#ifndef ADAPTER2TES_H_
#define ADAPTER2TES_H_

#define TCPRECVSIZE 65536				/*TCP���ջ�������С*/
#define TCPADDRENV "TESADDR"				/*TCPͨѶģʽ�ĵ�ַ��������*/
#define CONFIGNAMEENV "ADAPTERCONFIG"	/*�����ļ�������������*/
#define CHANNELNAMEENV "CHANNELNAME"	/*ͨ�����ƻ�������*/

#ifdef __cplusplus
extern "C" {
#endif

/**
 * ��ʼ����Ϣ��ռ�
 * @return	��ʼ����ɵ���Ϣ��ռ䣬ʧ�ܷ���NULL
 */
char* messageInit();

/**
 * ��ʼ��ָ����־λ����Ϣ��
 * @param len
 * 			Ҫ��ʼ���Ŀռ��С
 * @param flag
 * 			Ҫָ������Ϣ���־��C��ʽ�ַ���
 * @return	��ɳ�ʼ������Ϣ�壬ʧ�ܷ���NULL
 */	
char* allInit(int, char*);	

/**
 * �ͷ���Ϣ��ռ�
 * @param target
 * 			Ҫ�ͷſռ����Ϣ��
 * @param flag
 * 			Ҫָ������Ϣ���־
 * @return	Ŀǰ�̶�����1
 */
int infoDel(char**);		

/**
 * ����Ϣ��������ֶ�����
 * @param message
 * 			�Ѿ���ʼ������Ϣ��
 * @param name
 * 			�ֶ����ƣ�C��ʽ�ַ���
 * @param content
 * 			�ֶ�����
 * @param lenC
 * 			���ݳ���
 * @return	��ӳɹ����ص�ǰ����Ϣ�峤�ȣ�ʧ�ܷ���-1
 */
int addContent(char**, char*, char*, int);	

/**
 * ����Ϣ���ж�ȡ�ֶ�����
 * @param recv
 * 			����ȡ����Ϣ��
 * @param name
 * 			�ֶ����ƣ�C��ʽ�ַ���
 * @param target
 *			��ȡ���ֶ����ݵĴ洢�ռ�
 * @param len
 * 			�洢�ռ�Ŀ��ó���
 * @return	�ɹ�����1��ʧ�ܷ���-1
 */
int readContent(char*, char*, char*, int*);	

/**
 * ʹ��TCP�����ӷ�ʽ����Ľ��н���
 * @param psend
 * 			������Ϣ��
 * @param precv
 * 			������Ϣ��
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
 */
int sendTcpS(char**, char**);			
int sendTcpSMul(char**, char***);			

/**
 * �ж���Ϣ��ռ��Ƿ����
 * @param message
 * 			Ŀ����Ϣ��ռ�
 * @param increment
 * 			��Ҫ�洢���ֶοռ�
 * @return	Ҫ����ֶδ洢��Ϣ����Ҫ����Ŀռ䣬������-1
 */
int isEnough(char*, int);	

/**
 * ����Ϣ��ռ��������
 * @param message
 * 			Ŀ����Ϣ��ռ�
 * @param len
 * 			��Ҫ����Ϣ��ռ�����
 * @return	�ɹ������µ���Ϣ��ռ䣬ʧ�ܷ���NULL
 */				
char* increMessage(char*, int);		

/**
 * ������Ϣ��
 * @param from
 * 			Դ��Ϣ��
 * @param to
 * 			Ŀ����Ϣ��
 * @return	�ɹ�����1��ʧ�ܷ���-1
 */		
int copyMessage(char*, char*);	

/**
 * �ӻ��������л�ȡIP�˿���Ϣ
 * @param env
 * 			������������
 * @param ip
 * 			���IP��ַ�Ŀռ�
 * @param iplen
 *			���IP��ַ�ռ�Ĵ�С
 * @param port
 * 			��Ŷ˿���Ϣ�Ŀռ�
 * @param portlen
 * 			��Ŷ˿���Ϣ�ռ�ĳ���
 * @return	�ɹ�����1��ʧ�ܷ���-1
 */		
int env2ip (char*, char*, int, char*, int);	

/**
 * ����Ľ���ע�Ტ����������Ϣ
 * @param regC
 * 			�����õ�ע����Ϣ��
 * @param configC
 * 			���ص������ļ���Ϣ
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
 */
int reg2tes(char **, char **);	

/**
 * ����Ľ��н���
 * @param sendC
 * 			�����õ�������Ϣ��
 * @param recvC
 * 			���ķ��ص���Ӧ��Ϣ��
 * @return	�ɹ����ؽ��յ���Ϣ��ĳ��ȣ�ʧ�ܷ���-1
 */		
int sendContent(char**, char**);		
int sendContentMul(char**, char***);		

#ifdef __cplusplus
}
#endif

#endif /* ADAPTER2TES4C_H_ */
