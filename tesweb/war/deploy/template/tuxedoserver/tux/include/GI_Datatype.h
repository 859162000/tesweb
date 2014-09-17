/***************************************************************/
/* ͨ����������ӿڵ��������Ͷ���ͷ�ļ�                    */
/*                                                             */
/* �ļ���:      GI_Datatype.h                                  */
/*                                                             */
/* ����Ա:      ���ٽ�                                         */
/*                                                             */
/* ��������:    2005.04.19                                     */
/***************************************************************/




#ifndef _GI_DATATYPE_H_
#define _GI_DATATYPE_H_

#ifdef __cplusplus
extern "C" {
#endif

/***********************��������*************************/
//�����ɹ��ķ���ֵ
#define GI_OK                     0

//������
#define GI_ERR_BASE                 0X02240000

#define GI_ERR_GENERAL              GI_ERR_BASE+0X0001
#define GI_ERR_RSA_DATA_LEN         GI_ERR_BASE+0X0002   //����RSA���㴦����������ݳ��Ȳ��Ϸ�
#define GI_ERR_BUFFER_TOO_SMALL     GI_ERR_BASE+0X0003   //���������̫С�������Դ�Ž��
#define GI_ERR_ARG_NULL             GI_ERR_BASE+0X0005   //ָ�����ΪNULL
#define GI_ERR_RSA_PAD              GI_ERR_BASE+0X0006   //RSA���ܳ�����Pad����
#define GI_ERR_MALLOC_MEMORY        GI_ERR_BASE+0X0007   //�����ڴ����
#define GI_ERR_INVALID_SIGNATURE    GI_ERR_BASE+0X0008   //ǩ�����Ϸ�
#define GI_ERR_ARG_OUTOF_RANGE      GI_ERR_BASE+0X0009   //��ֵ����С����Сֵ��������ֵ
#define GI_ERR_RSA_MODULUS_LEN      GI_ERR_BASE+0X000a   //RSA��Կ/˽Կģ�����Ϸ�
#define GI_ERR_KEYTYPE              GI_ERR_BASE+0X000b   //��Կ���ʹ����޴���Կ���ͻ��ߺ��㷨���Ͳ�ƥ��
#define GI_ERR_ALGTYPE              GI_ERR_BASE+0X000c   //�㷨���ʹ����޴��㷨���ͻ��ߺͽӿڲ�ƥ��
#define GI_ERR_SYM_PAD              GI_ERR_BASE+0X000d   //�Գƽ��ܳ�����Pad����
#define GI_ERR_SYM_DATA_LEN         GI_ERR_BASE+0X000e   //���жԳƼӽ��ܡ�MAC����/��֤��hash������������ݳ��Ȳ��Ϸ�
#define GI_ERR_SIGNATURE_LEN        GI_ERR_BASE+0X000f   //ǩ�����ȴ���

#define GI_ERR_IV_LEN               GI_ERR_BASE+0X0022   //��ʼ���������Ⱥ��㷨��ƥ��
#define GI_ERR_KEY_LEN              GI_ERR_BASE+0X0032   //��Կ���Ȳ�����Ҫ��

#define GI_ERR_HMAC_LEN             GI_ERR_BASE+0X0040   //HMAC��֤�з���HMAC���ȴ���
#define GI_ERR_HMAC                 GI_ERR_BASE+0X0041   //HMAC��֤�з���HMACֵ��ƥ��
#define GI_ERR_MAC_LEN              GI_ERR_BASE+0X0042   //MAC��֤�з���MAC���ȴ���
#define GI_ERR_MAC                  GI_ERR_BASE+0X0043   //MAC��֤�з���MACֵ��ƥ��

#define GI_ERR_FILE_OPEN            GI_ERR_BASE+0X0050   //���ļ�����
#define GI_ERR_FILE_CREATE          GI_ERR_BASE+0X0051   //��������ļ�����
#define GI_ERR_FILE_EXIST           GI_ERR_BASE+0X0052   //����ļ��Ѿ�����
#define GI_ERR_FILE_READ            GI_ERR_BASE+0X0053   //���ļ�����
#define GI_ERR_FILE_WRITE           GI_ERR_BASE+0X0054   //д�ļ�����
#define GI_ERR_KEY_OR_MAC_OR_HMAC   GI_ERR_BASE+0X0060

#define GI_ERR_MYID_NOT_SATISFY     GI_ERR_BASE+0X0070   //Э��������õ�ʵ���ź��ϵİ汾������
#define GI_ERR_BASE64CIPHER_FORMAT  GI_ERR_BASE+0X0071   //base64���������ĸ�ʽ����ȷ
#define GI_ERR_BASE64DATA_LEN       GI_ERR_BASE+0X0072   //base64���볤�Ȳ���ȷ��������4��������
//��������С
#define GI_MAX_DATA_LEN            16384 //16*1024
#define GI_MAX_RANDOM_LEN          4096
#define GI_MAX_RSA_MODULUS_LEN     256
#define GI_MAX_RSA_PRIME_LEN       GI_MAX_RSA_MODULUS_LEN/2

//�Գ���Կ����
#define GI_MAX_HMAC_KEY_LEN       256
#define GI_MAX_SYMMETRIC_KEY_LEN  32
#define GI_DES_KEY_LEN            8
#define GI_3DES112_KEY_LEN        16
#define GI_3DES168_KEY_LEN        24
#define GI_IDEA_KEY_LEN           16
#define GI_SSF33_KEY_LEN          16
#define GI_AES128_KEY_LEN         16

//�Գ��㷨���鳤�ȳ���
#define GI_DES_BLOCK_LEN          8
#define GI_3DES_BLOCK_LEN         8
#define GI_IDEA_BLOCK_LEN         8
#define GI_SSF33_BLOCK_LEN        16
#define GI_AES128_BLOCK_LEN       16

/**��Կ���Ͷ��壬���libCSSP.h�ж����ֵһ��**/   /**ע�͵�����Կ���Ͷ�������libCSSP.h**/

#define GI_KEY_BASE         0X0000D000      //#define CSSPK_BASE            0X0000D000

#define GI_SYMMETRIC_KEY    GI_KEY_BASE+0   //#define CSSPK_SYMMETRIC_KEY   CSSPK_BASE+0
#define GI_PUBLIC_KEY       GI_KEY_BASE+1   //#define CSSPK_PUBLIC_KEY      CSSPK_BASE+1

#define GI_DES_KEY          GI_KEY_BASE+10  //#define CSSPK_DES_KEY         CSSPK_BASE+10
#define GI_3DES112_KEY      GI_KEY_BASE+11  //#define CSSPK_DES2_KEY        CSSPK_BASE+11
#define GI_3DES168_KEY      GI_KEY_BASE+12  //#define CSSPK_DES3_KEY        CSSPK_BASE+12
#define GI_IDEA_KEY         GI_KEY_BASE+13  //#define CSSPK_IDEA_KEY        CSSPK_BASE+13
#define GI_AES128_KEY       GI_KEY_BASE+14  //#define CSSPK_AES128_KEY      CSSPK_BASE+14
#define GI_SSF33_KEY        GI_KEY_BASE+17  //#define CSSPK_SSF33_KEY       CSSPK_BASE+17

#define GI_RSA_PUBLIC_KEY   GI_KEY_BASE+32  //#define CSSPK_RSA_PUBLIC_KEY_RAW  CSSPK_BASE+32
#define GI_RSA_PRIVATE_KEY  GI_KEY_BASE+33  //#define CSSPK_RSA_PRIVATE_KEY_RAW CSSPK_BASE+33

#define GI_MAC_MD5_KEY      GI_KEY_BASE+36  //#define CSSPK_MAC_MD5_KEY         CSSPK_BASE+36
#define GI_MAC_SHA1_KEY     GI_KEY_BASE+37  //#define CSSPK_MAC_SHA1_KEY        CSSPK_BASE+37


/********************�㷨���ͣ����ƣ�����*****/
//�Գ��㷨
enum
{
    GI_ALGO_DES_ECB       = 0x00000001,
    GI_ALGO_DES_CBC       = 0x00000002,
    GI_ALGO_3DES_ECB      = 0x00000010,
    GI_ALGO_3DES_CBC      = 0x00000020,
    GI_ALGO_AES_ECB       = 0x00000100,
    GI_ALGO_AES_CBC       = 0x00000200,
    GI_ALGO_SSF33_ECB     = 0x00001000,
    GI_ALGO_SSF33_CBC     = 0x00002000,
    GI_ALGO_IDEA_ECB      = 0x00010000,
    GI_ALGO_IDEA_CBC      = 0x00020000,
    GI_ALGO_DES_ECB_PAD   = 0x10000001,
    GI_ALGO_DES_CBC_PAD   = 0x10000002,
    GI_ALGO_3DES_ECB_PAD  = 0x10000010,
    GI_ALGO_3DES_CBC_PAD  = 0x10000020,
    GI_ALGO_AES_ECB_PAD   = 0x10000100,
    GI_ALGO_AES_CBC_PAD   = 0x10000200,
    GI_ALGO_SSF33_ECB_PAD    = 0x10001000,
    GI_ALGO_SSF33_CBC_PAD    = 0x10002000,
    GI_ALGO_IDEA_ECB_PAD     = 0x10010000,
    GI_ALGO_IDEA_CBC_PAD     = 0x10020000
};

//�ǶԳ��㷨
enum
{
    GI_ALGO_RSA_PUBLIC_KEY_RAW  = 0x00000001,
    GI_ALGO_RSA_PRIVATE_KEY_RAW = 0x00000002,

    GI_ALGO_RSA_PUBLIC_KEY_ENC_PKCS1  = 0x00000010,
    GI_ALGO_RSA_PUBLIC_KEY_DEC_PKCS1  = 0x00000020,
    GI_ALGO_RSA_PRIVATE_KEY_ENC_PKCS1 = 0x00000040,
    GI_ALGO_RSA_PRIVATE_KEY_DEC_PKCS1 = 0x00000080,

    GI_ALGO_RSA_PRIVATE_KEY_SIGN_WITH_MD5   = 0x00000100,
    GI_ALGO_RSA_PRIVATE_KEY_SIGN_WITH_SHA1  = 0x00000200,
    GI_ALGO_RSA_PUBLIC_KEY_VERIFY_WITH_MD5  = 0x00000400,
    GI_ALGO_RSA_PUBLIC_KEY_VERIFY_WITH_SHA1 = 0x00000800
};

//��ϢժҪ
enum
{
    GI_ALGO_HASH_MD5  = 0x00000001,
    GI_ALGO_HASH_SHA1 = 0x00000002
};

//��Ϣ������
enum
{
    GI_ALGO_MAC_MD5    = 0x00000001,
    GI_ALGO_MAC_SHA1   = 0x00000002,
    GI_ALGO_MAC_DES_CBC   = 0x00000004,
    GI_ALGO_MAC_3DES_CBC  = 0x00000008,
    GI_ALGO_MAC_AES_CBC   = 0x00000010,
    GI_ALGO_MAC_SSF33_CBC = 0x00000020,
    GI_ALGO_MAC_IDEA_CBC  = 0x00000040
};

/*********************�������Ͷ���*********************/



typedef struct{
  unsigned int  nKeyLength;
  unsigned int  nKeyType;
  unsigned char pbKeyBuffer[GI_MAX_SYMMETRIC_KEY_LEN];
}SYMMETRIC_KEY;//�Գ���Կ�ṹ,�����libCSSP.h�жԳ���Կ�ṹһģһ��

typedef struct{
   unsigned int    modulusLength;
   unsigned char   modulus[GI_MAX_RSA_MODULUS_LEN];
   unsigned char   publicExponent[GI_MAX_RSA_MODULUS_LEN];
}RSA_PUBLIC_KEY;//��Կ�ṹ,�����libCSSP.h�й�Կ�ṹһģһ��

typedef struct{
   unsigned int   modulusLength ;
   unsigned char  modulus[GI_MAX_RSA_MODULUS_LEN];
   unsigned char  publicExponent[GI_MAX_RSA_MODULUS_LEN];
   unsigned char  privateExponent[GI_MAX_RSA_MODULUS_LEN];
   unsigned char  prime[2][GI_MAX_RSA_PRIME_LEN];
   unsigned char  primeExponent[2][GI_MAX_RSA_PRIME_LEN];
   unsigned char  crtCoefficient[GI_MAX_RSA_PRIME_LEN];
}RSA_PRIVATE_KEY;//˽Կ�ṹ,�����libCSSP.h��˽Կ�ṹһģһ��


typedef struct{
    unsigned int    nAlgType;       //�Գ��㷨������MAC�㷨�����ͣ����ƣ�
    SYMMETRIC_KEY    SymKey;         //�Գ���Կ�ṹ
    unsigned char    IV[24];         //��ʼ������
    unsigned int    nIVLen;         //��ʼ����������
}SYM_CONTEX;         //�Գ����㣨�ԳƼӽ��ܡ�MAC���㼰��֤��������

typedef struct{
    unsigned int    nAlgType;       //HMAC�㷨���ͣ����ƣ�
    unsigned int    nKeyLength;     //����HMAC����Կ����
    unsigned char    pbKeyBuffer[GI_MAX_HMAC_KEY_LEN];   //HMAC��Կ
}HMAC_CONTEX;        //HMAC���㼰��֤��������

typedef struct{
    unsigned int    nAlgType;       //�ǶԳ��㷨���ͣ����ƣ�
    RSA_PUBLIC_KEY   PubKey;     //��Կ�ṹ
}PUB_CONTEX;         //�ǶԳƹ�Կ���㣨�ǶԳƼӽ��ܡ���֤ǩ����������

typedef struct{
    unsigned int     nAlgType;       //�ǶԳ��㷨���ͣ����ƣ�
    RSA_PRIVATE_KEY   PriKey;         //˽Կ�ṹ
}PRI_CONTEX;         //�ǶԳ�˽Կ���㣨�ǶԳƼӽ��ܡ�����ǩ����������



#ifdef __cplusplus
}
#endif

#endif


