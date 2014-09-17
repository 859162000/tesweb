#--删除、创建数据库
# drop database if exists tesdb;
# create database tesdb
# CHARACTER SET 'utf8'
# COLLATE 'utf8_general_ci';
# use tesdb;

#--删除表结构
DROP TABLE IF EXISTS monitorlog;
DROP TABLE IF EXISTS realtimelog;
DROP TABLE IF EXISTS performancelog;

DROP TABLE IF EXISTS his_comm_msg_log;
DROP TABLE IF EXISTS comm_msg_log;

DROP TABLE IF EXISTS his_case_instance_field_value;
DROP TABLE IF EXISTS case_instance_field_value;
DROP TABLE IF EXISTS his_case_instance_sql_value;
DROP TABLE IF EXISTS case_instance_sql_value ;

DROP TABLE IF EXISTS his_case_instance;
DROP TABLE IF EXISTS case_instance;

DROP TABLE IF EXISTS his_caseflow_instance;
DROP TABLE IF EXISTS caseflow_instance;

DROP TABLE IF EXISTS his_execute_log;
DROP TABLE IF EXISTS execute_log;

DROP TABLE IF EXISTS executesetLogStat;
DROP TABLE IF EXISTS executeset_taskitem;
DROP TABLE IF EXISTS executeset;

DROP TABLE IF EXISTS case_parameter_expected_value;
# DROP TABLE IF EXISTS flowcases;
DROP TABLE IF EXISTS cases;
DROP TABLE IF EXISTS caseflow;

DROP TABLE IF EXISTS case_import_batch;

DROP TABLE IF EXISTS subsidiary;
DROP TABLE IF EXISTS subbank;
DROP TABLE IF EXISTS card;

DROP TABLE IF EXISTS channel;
DROP TABLE IF EXISTS transrecognizer;
DROP TABLE IF EXISTS msgpacker;
DROP TABLE IF EXISTS adapter;
DROP TABLE IF EXISTS userrsystem;
DROP TABLE IF EXISTS user;

DROP TABLE IF EXISTS transaction_dynamic_parameter;
DROP TABLE IF EXISTS transaction_catetory;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS persistentdata;
DROP TABLE IF EXISTS system_dynamic_parameter;
DROP TABLE IF EXISTS scriptflow;

DROP TABLE IF EXISTS dbhost;
DROP TABLE IF EXISTS case_directory;
DROP TABLE IF EXISTS parameter_directory;
DROP TABLE IF EXISTS executeset_directory;
DROP TABLE IF EXISTS test_round;
DROP TABLE IF EXISTS executeset_executeplan;
DROP TABLE IF EXISTS execute_plan;
DROP TABLE IF EXISTS interfacefield;
DROP TABLE IF EXISTS interfacedef; 
DROP TABLE IF EXISTS system_copy_id_map;
DROP TABLE IF EXISTS copied_system;
DROP TABLE IF EXISTS systype;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS login_log;
DROP TABLE IF EXISTS Factor_Change_UserStats; 
DROP TABLE IF EXISTS Factor_Change_Statistics;
DROP TABLE IF EXISTS Case_Run_UserStats;
DROP TABLE IF EXISTS Case_Run_Statistics;
DROP TABLE IF EXISTS recorded_case;


#--用户表
CREATE TABLE user 
(
  ID int(16) NOT NULL auto_increment,
  NAME varchar(64) NOT NULL,
  PASSWORD varchar(64) NOT NULL,
  DESCRIPTION varchar(128) default '',
  ISADMIN int(1) default 0 COMMENT '0:系统管理员 1: 测试人员，2:项目管理员',
  FLAG int(1) default 0,
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index i_user_name on user(NAME);

#--系统表
CREATE TABLE systype 
(
  SYSTEMID int(16) NOT NULL auto_increment,
#  PROJECTNAME varchar(32) binary NOT NULL COMMENT '项目名称',
  SYSTEMNO varchar(32) COMMENT '被模拟系统代号',
  SYSTEMNAME varchar(32) binary NOT NULL COMMENT '被模拟系统名称',
  DESCRIPTION varchar(256) default '' COMMENT '项目描述',
  IPADRESS varchar(32) default '',
  PORTNUM int(8) default 0,
  CHANNEL varchar(32) default '' COMMENT '通道名称(系统默认CHANNEL)',
  BASECFG mediumtext COMMENT 'base.xml配置信息(该信息由界面组织,供核心读取)',
#  STYLESTRUCT mediumtext,
  FLAG int(1) default 1,
  TRANSACTIONTIMEOUT int(4) default 20 NOT NULL COMMENT '单个交易的超时时间（秒数）',
  MAXDELAYTIME bigint(16) default 0 NOT NULL COMMENT '系统级延迟上限',
  MINDELAYTIME bigint(16) default 0 NOT NULL COMMENT '系统级延迟下限',
  DELAYTIMETYPE int(1) default 0 NOT NULL COMMENT '延迟类型:0-系统延迟 1-交易延迟 2-叠加延迟',
  ISUSED int(1) default 1 COMMENT '该系统是否被监控正在使用:0-开启(使用状态) 1-关闭',
  ISPARAMMODIFIED int(1) default 0 COMMENT '系统的参数是否发生了改变（改变了则通知后台重新载入）:0-未改变 1-改变了',
  NEEDSQLCHECK int(1) default 1 COMMENT '是否需要进行SQL语句的执行和检查：０：不需要，１：需要',
  SQLGETMETHOD int(1) default 0 COMMENT 'SQL参数获取方式：0:通过TCP连接的方式获取参数、1:通过JDBC连接方式获取参数',
  SQLGETDBADDR varchar(32) default '' COMMENT 'SQL查询的默认机器ip地址，如99.8.43.29等',
  ENCODING4REQUESTMSG varchar(32) default '' COMMENT '请求报文的编码方式',
  ENCODING4RESPONSEMSG varchar(32) default '' COMMENT '应答报文的编码方式',
  ISCLIENTSIMU int(1) default 1 COMMENT '是否为客户端模拟:0-否（为服务端模拟） 1-是',
  ISSYNCCOMM int(1) default 1 COMMENT '是否为同步通讯:0-否 1-是',
  USESAMERESPNSSTRUCT int(1) default 0 COMMENT '是否所有交易都使用同一响应模板:0-否 1-是',
  RESPONSESTRUCT mediumtext COMMENT '响应报文结构',
  RESPONSEMODE int(1) default 0 COMMENT '作为服务端的应答模式：0--使用默认案例的应答报文返回，1--根据交易应答报文解析返回，2--根据录制报文匹配返回，3--根据案例实例匹配返回报文',
  ReqMsgUnPackID  int(16) COMMENT '请求报文的拆包组件ID',
  ResMsgUnPackID  int(16) COMMENT '应答报文的拆包组件ID',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  PRIMARY KEY  (SYSTEMID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index i_systype_name on systype(SYSTEMNAME);

#--用户与系统权限表
CREATE TABLE userrsystem 
(
  ID int(16) NOT NULL auto_increment,
  USERID int(16) NOT NULL,
  SYSTEMID int(16) NOT NULL,
  KEY UserRSystem_FK_UserId (USERID),
  KEY UserRSystem_FK_SystemId (SYSTEMID),
  CONSTRAINT UserRSystem_SystemId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  CONSTRAINT UserRSystem_UserId FOREIGN KEY (USERID) REFERENCES user (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--持久化数据表
CREATE TABLE persistentdata
(
  ID int(16) NOT NULL auto_increment,
  SYSTEMID int(16) NOT NULL,
  PARAMETER varchar(64) default '',
  CURVALUE varchar(128) default '',
  TYPE int(1) default 0 COMMENT '0:字符、1:数字',
  KEY PERMENTDATA_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT permentdata_ibfk_1 FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



#--测试轮次表
CREATE TABLE test_round
(
  ROUNDID          int(16) NOT NULL AUTO_INCREMENT,
  SYSTEMID         int(16) NOT NULL,
  ROUNDNO          int COMMENT '轮次序号',
  ROUNDNAME        varchar(64) COMMENT '轮次名称',
  DESCRIPTION      varchar(1024) COMMENT '轮次说明',
  STARTDATE        date NULL,
  ENDDATE          date NULL,
  CURRENTROUNDFLAG int(1) default 0 COMMENT '是否为当前轮次',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY ROUND_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT ROUND_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY (ROUNDID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_round_no on test_round(SYSTEMID,ROUNDNO);
create unique index ui_round_name on test_round(SYSTEMID,ROUNDNAME);


#--适配器表
CREATE TABLE adapter
(
  ID int(16) NOT NULL auto_increment,
  PROTOCOLTYPE varchar(64) NOT NULL COMMENT '协议类型:TCP、HTTP',
  DESCRIPTION varchar(128) default '',
  CSTYPE int(1) NOT NULL COMMENT '0:发起方适配器、1:接收方适配器',
  PLUGINNAME varchar(128) NOT NULL COMMENT '适配器插件名称',
  CFGINFO mediumtext COMMENT '适配器配置信息,配置模版',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  PRIMARY KEY  (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

#--交易识别表
CREATE TABLE transrecognizer
(
  ID int(16) NOT NULL auto_increment,
  NAME varchar(128) NOT NULL,
  DESCRIPTION varchar(128) default '',
  TYPENAME varchar(32) COMMENT '交易识别类型:固定位置;正则;函数;脚本',
  CLASSNAME varchar(256) NOT NULL COMMENT '交易识别类名',
  CFGINFO mediumtext COMMENT '交易识别配置信息,配置模版',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  PRIMARY KEY  (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

#--拆、组包表
CREATE TABLE msgpacker
(
  ID int(16) NOT NULL auto_increment,
  STYLENAME varchar(128),
  TYPE int(1) COMMENT '0组包;1拆包',
  MESSAGETYPE varchar(64) default '' COMMENT '报文类型: XML\8583\定长等',
  CLASSNAME varchar(256) NOT NULL COMMENT '拆组包类名',
  CONTENT mediumtext COMMENT '拆组包样式文件',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#--通道表
CREATE TABLE channel
(
  ID int(16) NOT NULL auto_increment,
  NAME varchar(64) default '',
  ADAPTERID int(16) NOT NULL COMMENT '适配器ID',
  TRANSRECOGNIZERID int(16) COMMENT '交易识别ID',
  RECOGNIZERCFGINFO mediumtext COMMENT '交易识别配置信息,配置数据',
  PACKID int(16) COMMENT '组包ID',
  UNPACKID int(16) COMMENT '拆包ID',
  #--DEFAULTFLAG int(1) COMMENT '默认通道标示:0默认',
  SENDADAPTERIP varchar(32) COMMENT '发起端适配器监听核心请求IP',
  SENDADAPTERPORT int(8) COMMENT '发起端适配器监听核心请求端口',
  ADAPTERCFGINFO mediumtext COMMENT '适配器配置信息,配置数据',
  SYSTEMID int(16) NOT NULL,
  CHANNELTYPE int(1) default 0 COMMENT '通道类型 0-远程通道 1-本地通道',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY CHANNEL_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT CHANNEL_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  KEY CHANNEL_FK_ADAPTERID (ADAPTERID),
  CONSTRAINT CHANNEL_FK_ADAPTERID FOREIGN KEY (ADAPTERID) REFERENCES adapter (ID) ON DELETE CASCADE,
  KEY CHANNEL_FK_TRANSRECOGNIZERID (TRANSRECOGNIZERID),
  CONSTRAINT CHANNEL_FK_TRANSRECOGNIZERID FOREIGN KEY (TRANSRECOGNIZERID) REFERENCES transrecognizer (ID) ON DELETE CASCADE,
  KEY CHANNEL_FK_PACKID (PACKID),
  CONSTRAINT CHANNEL_FK_PACKID FOREIGN KEY (PACKID) REFERENCES msgpacker (ID) ON DELETE CASCADE,
  KEY CHANNEL_FK_UNPACKID (UNPACKID),
  CONSTRAINT CHANNEL_FK_UNPACKID FOREIGN KEY (UNPACKID) REFERENCES msgpacker (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 案例树表
create table case_directory
(
   DIRECTORYID           int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID              int(16) NOT NULL,
   PARENTDIRID           int(16) COMMENT '父目录Id',
   SORTINDEX             int(8)  COMMENT '在同一目录下的子目录的排列顺序',
   NAME                  varchar(256)  COMMENT '案例目录名称',
   PATH                  varchar(512) COMMENT '案例目录路径',
   DESCRIPTION           varchar(256) COMMENT '案例目录描述',
   KEY CASE_DIRECTORY_FK_SYSTEMID (SYSTEMID),
   CONSTRAINT CASE_DIRECTORY_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   PRIMARY KEY (DIRECTORYID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_case_directory on case_directory(PARENTDIRID,SORTINDEX);


# 执行集树表
create table executeset_directory
(
   DIRECTORYID           int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID              int(16) NOT NULL,
   PARENTDIRID           int(16) COMMENT '父目录Id',
   SORTINDEX             int(8)  COMMENT '在同一目录下的子目录的排列顺序',
   OBJTYPE               int(1) COMMENT '是目录还是执行集，0：目录，1：执行集',
   EXECUTESETID          int(16) COMMENT '所关联对象的ID，如果OBJTYPE=0，为空，如果DIRTYPE=1, 为执行集ID',
   NAME                  varchar(256)  COMMENT '执行集目录名称',
   PATH                  varchar(512) COMMENT '执行集目录路径',
   DESCRIPTION           varchar(256) COMMENT '执行集目录描述',
   KEY EXECUTESET_DIRECTORY_FK_SYSTEMID (SYSTEMID),
   CONSTRAINT EXECUTESET_DIRECTORY_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   PRIMARY KEY (DIRECTORYID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_executeset_directory on executeset_directory(PARENTDIRID,SORTINDEX);


# 参数树表
create table parameter_directory
(
   DIRECTORYID           int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID              int(16) NOT NULL,
   PARENTDIRID           int(16) COMMENT '父目录Id',
   SORTINDEX             int(8)  COMMENT '在同一目录下的子目录的排列顺序',
   NAME                  varchar(256)  COMMENT '参数目录名称',
   PATH                  varchar(512) COMMENT '参数目录路径',
   DESCRIPTION           varchar(256) COMMENT '参数目录描述',
   KEY PARAM_DIRECTORY_FK_SYSTEMID (SYSTEMID),
   CONSTRAINT PARAM_DIRECTORY_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   PRIMARY KEY (DIRECTORYID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_parameter_directory on parameter_directory(PARENTDIRID,SORTINDEX);


#--交易类别
CREATE TABLE transaction_catetory 
(
  ID int(16) NOT NULL auto_increment,
  SYSTEMID int(16) NOT NULL,
  CATEGORYNAME varchar(64) binary NOT NULL,
  DESCRIPTION varchar(128) default '', 
  KEY TRANS_CATEGORY_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT TRANS_CATEGORY_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_transaction_catetory on transaction_catetory(SYSTEMID, CATEGORYNAME);
#--insert into transaction_catetory(SYSTEMID, CATEGORYNAME,DESCRIPTION) values(1, 'POS类交易', 'POS类交易');
#--insert into transaction_catetory(SYSTEMID, CATEGORYNAME,DESCRIPTION) values(1, 'ATM类交易', 'ATM类交易');
#--insert into transaction_catetory(SYSTEMID, CATEGORYNAME,DESCRIPTION) values(1, '转账类交易', '转账类交易');


#--交易表
CREATE TABLE transaction 
(
  TRANSACTIONID int(16) NOT NULL auto_increment,
  TRANCODE varchar(32) binary NOT NULL,
  TRANNAME varchar(64) NOT NULL,
  SYSTEMID int(16) NOT NULL,
  ISCLIENTSIMU int(1) default 0 COMMENT '0:接收方交易, 1:发起方交易',
  DESCRIPTION varchar(128) default '' COMMENT '交易说明',
  CHANNEL varchar(32) default '',
  SCRIPT mediumtext,
  REQUSETSTRUCT mediumtext COMMENT '请求报文结构',
  RESPONSESTRUCT mediumtext COMMENT '响应报文结构',
  TRANSACTIONCATEGORYID int(16) COMMENT '交易类别',
  CATEGORY varchar(16) default '',
  FLAG int(1) default 0,
  MAXDELAYTIME bigint(16) default 0 NOT NULL COMMENT '系统级延迟上限',
  MINDELAYTIME bigint(16) default 0 NOT NULL COMMENT '系统级延迟下限',
  SQLDELAYTIME int(4) default 0 NOT NULL COMMENT '交易完成之后到400上查询SQL语句之前的延迟等待时间（秒数）',
  PARAMETERGETSEQUENCE varchar(1024) COMMENT '参数获取顺序',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY TRAN_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT TRAN_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY  (TRANSACTIONID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index i_transaction_sysId_tranname on transaction(SYSTEMID, ISCLIENTSIMU, TRANNAME);




#--交易脚本表
CREATE TABLE scriptflow(
   ID int(16) NOT NULL auto_increment,
   NAME varchar(64) default '',
   DESCRIPTION varchar(128) default '',
   SCRIPT mediumtext,
   SYSTEMID int(16) NOT NULL, 
   CreatedUserId int(16),
   CreatedTime datetime, 
   LastModifiedTime datetime, 
   LastModifiedUserId int(16),
   KEY SCRIPTFLOW_FK_SYSTEMID (SYSTEMID),
   CONSTRAINT SCRIPTFLOW_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
create index i_scriptflow_name on scriptflow(NAME);

#--业务流表
CREATE TABLE caseflow
(
   ID int(16) NOT NULL auto_increment,
   CASEFLOWNAME varchar(64) COMMENT '业务流名称',
   CASEFLOWNO varchar(64) NOT NULL COMMENT '业务流编号；业务流编号为空时则不是业务流,只是一般的脚本',
   CASEFLOWPATH varchar(1024) COMMENT '业务流的路径',
   DESCRIPTION varchar(1024) COMMENT '业务流的描述',
   MEMO varchar(1024) COMMENT '业务流的备注',
   CASEFLOWSTEP varchar(1024) COMMENT '步骤描述',
   PRECONDITIONS varchar(1024) COMMENT '前置条件',
   EXPECTEDRESULT varchar(1024) COMMENT '预期结果描述',
   CASETYPE varchar(32) COMMENT '用例类型',
   CASEPROPERTY varchar(32) COMMENT '用例属性',
   PRIORITY  varchar(32) COMMENT '优先级',
   STEPCOUNT int(16) default 0 NOT NULL,
   SYSTEMID int(16) NOT NULL,      
   DESIGNER varchar(32) COMMENT '用例设计人',
   DESIGNTIME varchar(32) COMMENT '用例设计时间',
   CreatedUserId int(16) COMMENT '业务创建用户',
   CreatedTime datetime  COMMENT '业务创建时间',
   LastModifiedUserId int(16),
   LastModifiedTime datetime, 
   IMPORTBATCHNO varchar(64) default '' COMMENT '业务流导入时的批次号',
   PASSFLAG int(1) default 0 COMMENT '案例是否通过（用最后一次执行结果回写），0: 未通过，1：通过',
   DISABLEDFLAG int(1) default 0 COMMENT '暂时不用，0: No，1：Yes',
   BREAKPOINTFLAG int(1) default 0 COMMENT '断点标志：0:无断点、1:有断点；只有当业务流有断点时业务流案例的断点才能生效',
   DIRECTORYID int(16) COMMENT '业务流是挂在树的那个节点上的？',
   SCRIPTFLOWID int(16) COMMENT '脚本ID',
   KEY CASEFLOW_FK_SYSTEMID (SYSTEMID),
   CONSTRAINT CASEFLOW_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   KEY CASEFLOW_FK_DIRECTORYID (DIRECTORYID),
   CONSTRAINT CASEFLOW_FK_DIRECTORYID FOREIGN KEY (DIRECTORYID) REFERENCES case_directory (DIRECTORYID) ON DELETE CASCADE,
   KEY CASEFLOW_FK_SCRIPTFLOWID (SCRIPTFLOWID),
   CONSTRAINT CASEFLOW_FK_SCRIPTFLOWID FOREIGN KEY (SCRIPTFLOWID) REFERENCES scriptflow (ID) ON DELETE CASCADE,
   PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_caseflow_batchno_flowno on caseflow(SYSTEMID, DIRECTORYID, CASEFLOWNO);


#--案例表
CREATE TABLE cases 
(
  CASEID int(16) NOT NULL auto_increment,
  CASENAME varchar(64) binary NOT NULL COMMENT '案例名称',
  CASENO varchar(64) binary NOT NULL COMMENT '案例编号',
  CASEPATH varchar(1024) binary COMMENT '案例目录',
  TRANSACTIONID int(16) NOT NULL COMMENT '交易ID',
  CASEFLOWID int(16) COMMENT '业务流ID',
  SEQUENCE int(8) COMMENT '业务流内案例执行次序号',
  CASEFLOWSTEP varchar(1024) COMMENT '步骤描述',
  PRECONDITIONS varchar(1024) COMMENT '前置条件',
  EXPECTEDRESULT varchar(1024) COMMENT '预期结果描述',
  CASETYPE varchar(32) COMMENT '用例类型',
  CASEPROPERTY varchar(32) COMMENT '用例属性',
  PRIORITY  varchar(32) COMMENT '优先级',
  DESIGNER varchar(32) COMMENT '业务流设计人',
  BREAKPOINTFLAG int(1) default 0 COMMENT '断点标志：0:无断点、1:有断点；如有断点则执行完成后会停止继续执行，等待收到界面指令后再继续执行',
  CARDID int(16) COMMENT '卡ID',
  EXPECTEDXML mediumtext COMMENT '各个字段的预期结果，XML',
  REQUESTXML mediumtext COMMENT '组包前请求报文，XML',
  RESPONSEXML mediumtext COMMENT '组包前应答报文，XML',
  REQUESTMSG mediumblob COMMENT '组包后的请求报文，二进制的',
  RESPONSEMSG mediumblob COMMENT '组包后的应答报文，二进制的',
  IMPORTBATCHNO varchar(64) default '' COMMENT '案例导入时的批次号',
  AMOUNT float(10,2) COMMENT '交易金额',
  ISPARSEABLE int(1) default 0 COMMENT '是否可解析',
  FLAG int(1) default 0 COMMENT '0: 正常, 1: 删除',
  ISDEFAULT int(1) default 0 COMMENT '是否默认案例:0-默认 1-否',
  DIRECTORYID int(16) COMMENT '案例是挂在树的那个节点上的？',
  DESCRIPTION varchar(256) default '' COMMENT '案例说明',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY CASE_FK_TRANSACTIONID (TRANSACTIONID),
  KEY CASE_FK_CASEFLOWID (CASEFLOWID),
  CONSTRAINT case_fk_transactionId FOREIGN KEY (TRANSACTIONID) REFERENCES transaction (TRANSACTIONID) ON DELETE CASCADE,
  CONSTRAINT case_fk_caseflowId FOREIGN KEY (CASEFLOWID) REFERENCES caseflow (ID) ON DELETE CASCADE,
  PRIMARY KEY  (CASEID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
# create unique index i_cases_batchno_caseno on cases(IMPORTBATCHNO, CASENO);
create unique index i_cases_flow_sequence on cases(CASEFLOWID, SEQUENCE);
create unique index i_cases_flow_caseno on cases(CASEFLOWID, CASENO);



#--业务流案例表（此表目前未用到）
#CREATE TABLE flowcases (
#  ID int(16) NOT NULL auto_increment,
#  FLOWID int(16) NOT NULL,
#  SEQUENCE int(8) NOT NULL COMMENT '业务流内案例执行次序号',
#  CASEID int(16) NOT NULL,
#  USERID int(16) NOT NULL COMMENT '把案例加入业务流的用户',
#  CREATEDTIME datetime COMMENT '把案例加入业务流的时间',
#  BREAKPOINTFLAG int(1) default 0 COMMENT '断点标志：0:无断点、1:有断点；如有断点则执行完成后会停止继续执行，等待收到界面指令后再继续执行',
#  KEY FlowCases_fk_FlowId (FLOWID),
#  KEY FlowCases_fk_CaseId (CASEID),
#  CONSTRAINT FlowCases_fk_FlowId FOREIGN KEY (FLOWID) REFERENCES caseflow (ID) ON DELETE CASCADE,
#  CONSTRAINT FlowCases_fk_CaseId FOREIGN KEY (CASEID) REFERENCES cases (CASEID) ON DELETE CASCADE,
#  PRIMARY KEY  (ID)
#) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--执行集定义
CREATE TABLE executeset
(
  ID int(16) NOT NULL auto_increment,
  NAME varchar(64) NOT NULL,
  IMPORTBATCHNO varchar(64) COMMENT '导入批次号',
  DESCRIPTION varchar(128) default '',
  SYSTEMID int(16) NOT NULL,
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY EXECUTESET_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT EXECUTESET_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_executeset_name on executeset(SYSTEMID, NAME);

#--执行集元素
CREATE TABLE executeset_taskitem
(
  ID int(16) NOT NULL auto_increment,
  EXECUTESETID int(16) NOT NULL COMMENT '执行集ID',
  TASKID int(16) NOT NULL COMMENT '案例ID\业务流ID\脚本ID',
  NAME VARCHAR(64),
  TYPE int(1) NOT NULL COMMENT '关联TASKID类型，0：关联案例; 1：关联（可执行的）交易脚本; 2：关联业务流',
  TRANSACTIONID int(16) COMMENT '交易ID',
  REPCOUNT int(8) COMMENT '自我执行次数',
  KEY EXECUTESETTASKITEM_FK_SYSTEMID (EXECUTESETID),
  CONSTRAINT EXECUTESETTASKITEM_FK_SYSTEMID FOREIGN KEY (EXECUTESETID) REFERENCES executeset (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_executeset_taskitem on executeset_taskitem(EXECUTESETID, TYPE, TASKID);
create index i_executeset_taskitem_executesetId on executeset_taskitem(EXECUTESETID);


#--日志表
CREATE TABLE monitorlog (

  ID int(16) NOT NULL auto_increment,
  DATETIME datetime  NOT NULL,
  SYSNAME varchar(32) NOT NULL,
  SYSSIGN varchar(32) NOT NULL,
  TRANCODE varchar(32) default '',
  CASENAME varchar(32) default '',
  ERRORFLAG varchar(32) default '',
  TYPE int(1) default 0,
  COMPARERESULT int(1) default 0 COMMENT '0:成功、1:失败',
  TRANNAME varchar(64) default '',
  YEARM varchar(32) default '' COMMENT '年月',
  CHANNEL varchar(32) default '',
  HASSCRIPT int(1) default 0 COMMENT '0:无脚本、1:有脚本',
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--实时日志表
CREATE TABLE realtimelog 
(
  ID int(16) NOT NULL auto_increment,
  DATETIME datetime  NOT NULL,
  SYSNAME varchar(32) NOT NULL,
  SYSSIGN varchar(32) NOT NULL,
  TRANCODE varchar(32) default '',
  CASENAME varchar(32) default '',
  ERRORFLAG varchar(32) default '',
  TYPE int(1) default 0,
  COMPARERESULT int(1) default 0 COMMENT '0:成功、1:失败',
  TRANNAME varchar(64),
  YEARM varchar(32) default '',
  CHANNEL varchar(32) default '',
  HASSCRIPT int(1) COMMENT '0:无脚本、1:有脚本',
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--性能监控日志表
CREATE TABLE performancelog(

  ID int(16) NOT NULL auto_increment,
  SYSTEMNAME varchar(32) NOT NULL COMMENT '系统名称',
  DELAYTIME  varchar(256) COMMENT '延迟场景',
  BEGINTIME datetime COMMENT '开始时间',
  ENDTIME datetime COMMENT '结束时间',
  AVGTPS decimal(10,1) default'0.0' COMMENT '平均TPS',
  MAXTPS decimal(10,1) default'0.0' COMMENT '最高TPS',
  MINTPS decimal(10,1) default'0.0' COMMENT '最低TPS',
  AVGDELAY int(8) default 0 COMMENT '平均延迟',
  MAXDELAY int(8) default 0 COMMENT '最高延迟',
  MINDELAY int(8) default 0 COMMENT '最低延迟',
  AVGCPU int(8) default 0 COMMENT '平均CPU',
  MAXCPU int(8) default 0 COMMENT '最高CPU',
  MINCPU int(8) default 0 COMMENT '最低CPU',
  SYSDATA mediumtext COMMENT '系统级别采集数据',
  TRANDATA mediumtext COMMENT '交易级别采集数据',
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--执行集执行日志统计表
CREATE TABLE executesetLogStat
(
  ID int(16) NOT NULL auto_increment,
  EXECUTESETID int(16) NOT NULL COMMENT '执行集ID',
  BEGINTIME varchar(32) COMMENT '开始时间',
  ENDTIME varchar(32) COMMENT '结束时间',
  BUSISTAT mediumtext COMMENT '业务流统计数据',
  BUSILOG mediumtext COMMENT '业务流执行日志',
  CASESTAT mediumtext COMMENT '案例统计数据',
  CASELOG mediumtext COMMENT '案例执行日志',
  KEY EXECUTESETLOGSTAT_FK_EXECUTESETID (EXECUTESETID),
  CONSTRAINT EXECUTESETLOGSTAT_FK_EXECUTESETID FOREIGN KEY (EXECUTESETID) REFERENCES executeset (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



#--批次表（案例导入时要用到）
CREATE TABLE case_import_batch 
(
  ID int(16) NOT NULL auto_increment,
  BATCHNO varchar(64) NOT NULL default '' COMMENT '导入批次号，默认为：用户名YYYYMMDDHHMMSS',
  BATCHNAME varchar(128) COMMENT '批次名称',
  DESCRIPTION varchar(128) default '' COMMENT '批次号说明',
  USERID int(16) NOT NULL COMMENT '导入用户',
  IMPORTTIME datetime NOT NULL COMMENT '导入时间',
  SYSTEMID int(16) NOT NULL COMMENT '系统ID',
  DIRECTORYID int(16) COMMENT '导入后的案例是挂在案例树的那个节点上的？',
  KEY case_import_batch_FK_UserId (USERID),
  KEY case_import_batch_FK_SystemId (SYSTEMID),
  CONSTRAINT case_import_batch_FK_UserId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
#  CONSTRAINT case_import_batch_FK_SystemId FOREIGN KEY (USERID) REFERENCES user (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--执行日志表
CREATE TABLE execute_log 
(
  ID int(16) NOT NULL auto_increment,
  SYSTEMID int(16) NOT NULL COMMENT '系统ID',
  ROUNDID int(16) COMMENT '轮次ID',
  LOGINLOGID int(16) COMMENT '登录ID',
  EXECUTESETID int(16) COMMENT '执行集ID，如果不是从执行集发起的，则为空',
  EXECUTEBATCHNO varchar(64) COMMENT '执行批次号，默认为：用户名YYYYMMDDHHMMSS',
  EXECUTESETNAME varchar(128) default '' COMMENT '执行集名称',
  DESCRIPTION varchar(128) default '' COMMENT '执行集说明',
  USERID int(16) COMMENT '执行用户',
  CREATETIME varchar(64) NOT NULL COMMENT '创建时间',
  BEGINRUNTIME datetime COMMENT '开始执行时间',
  ENDRUNTIME datetime COMMENT '结束执行时间',
  RUNDURATION varchar(64) COMMENT '执行时长',
  PASSFLAG int(1) default 3 COMMENT '执行集通过与否的基本判断：7: 异常错误, 6:终止执行，5: 超时, 4: 中断, 3:未执行, 2: 正在执行中，1：通过，0：失败',
  ERRORMSG varchar(1024) default '' COMMENT '错误信息',
  EXECUTESETDIRID int(16) COMMENT '所执行的执行集对应执行集树上的哪个节点的?',
  TYPE int(2) COMMENT '0：执行集； 1：用例；2：步骤，3：接收端收到了请求',
#  KEY ExecuteLog_FK_UserId (USERID),
  KEY ExecuteLog_FK_SystemId (SYSTEMID),
  CONSTRAINT ExecuteLog_FK_SystemId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_execute_log_crttime on execute_log(SYSTEMID, BEGINRUNTIME);


#--执行日志历史表
CREATE TABLE his_execute_log 
(
  ID int(16) NOT NULL,
  SYSTEMID int(16) NOT NULL COMMENT '系统ID',
  ROUNDID int(16) COMMENT '轮次ID',
  EXECUTESETID int(16) COMMENT '执行集ID，如果不是从执行集发起的，则为空',
  EXECUTEBATCHNO varchar(64) COMMENT '执行批次号，默认为：用户名YYYYMMDDHHMMSS',
  EXECUTESETNAME varchar(128) default '' COMMENT '执行集名称',
  DESCRIPTION varchar(128) default '' COMMENT '执行集说明',
  USERID int(16) NOT NULL COMMENT '执行用户',
  CREATETIME varchar(64) NOT NULL COMMENT '创建时间',
  BEGINRUNTIME datetime COMMENT '开始执行时间',
  ENDRUNTIME datetime COMMENT '结束执行时间',
  RUNDURATION varchar(64) COMMENT '执行时长',
  PASSFLAG int(1) default 3 COMMENT '执行集通过与否的基本判断：7: 异常错误, 6:终止执行，5: 超时, 4: 中断, 3:未执行, 2: 正在执行中，1：通过，0：失败',
  ERRORMSG varchar(1024) default '' COMMENT '错误信息',
  EXECUTESETDIRID int(16) COMMENT '所执行的执行集对应执行集树上的哪个节点的?',
  TYPE int(2) COMMENT '0：执行集； 1：用例；2：步骤',
  KEY HisExecuteLog_FK_UserId (USERID),
  KEY HisExecuteLog_FK_SystemId (SYSTEMID),
  CONSTRAINT HisExecuteLog_FK_SystemId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#--业务流实例表
CREATE TABLE caseflow_instance (
  ID int(16) NOT NULL auto_increment,
  CASEFLOWID int(16) NOT NULL COMMENT '业务流ID',
  CASEFLOWNAME varchar(64) default '' COMMENT '业务流名称',
  CASEFLOWNO varchar(64) default '' COMMENT '业务流编号',
  EXECUTELOGID int(16) COMMENT '执行日志ID',
  DIRECTORYID int(16) COMMENT '业务流是挂在树的那个节点上的？',
  CREATEDTIME datetime NOT NULL COMMENT '创建时间',
  BEGINRUNTIME datetime COMMENT '开始执行时间',
  ENDRUNTIME datetime COMMENT '结束执行时间',
  RUNDURATION varchar(64) COMMENT '执行时长',
  CASEFLOWPASSFLAG int(1) default 3 COMMENT '业务流通过与否的基本判断：7: 异常错误, 6:终止执行，5: 超时, 4: 中断, 3:未执行, 2: 正在执行中，1：通过，0：失败',
  ERRORMSG varchar(1024) default '' COMMENT '错误信息',
  SYSTEMID int(16) NOT NULL,
  ROUNDID int(16) COMMENT '轮次ID',
  KEY CaseFlowInstance_FK_ExecuteLogId (EXECUTELOGID),
  CONSTRAINT caseflowinstance_fk_execlogId FOREIGN KEY (EXECUTELOGID) REFERENCES execute_log (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_caseflow_instance on caseflow_instance(CASEFLOWID, EXECUTELOGID);


#--业务流实例历史表
CREATE TABLE his_caseflow_instance 
(
  ID int(16) NOT NULL,
  CASEFLOWID int(16) NOT NULL COMMENT '业务流ID',
  CASEFLOWNAME varchar(64) default '' COMMENT '业务流名称',
  CASEFLOWNO varchar(64) default '' COMMENT '业务流编号',
  EXECUTELOGID int(16) COMMENT '执行日志ID',
  DIRECTORYID int(16) COMMENT '业务流是挂在树的那个节点上的？',
  CREATETIME datetime NOT NULL COMMENT '创建时间',
  BEGINRUNTIME datetime COMMENT '开始执行时间',
  ENDRUNTIME datetime COMMENT '结束执行时间',
  RUNDURATION varchar(64) COMMENT '执行时长',
  CASEFLOWPASSFLAG int(1) default 3 COMMENT '业务流通过与否的基本判断：7: 异常错误, 6:终止执行，5: 超时, 4: 中断, 3:未执行, 2: 正在执行中，1：通过，0：失败',
  ERRORMSG varchar(1024) default '' COMMENT '错误信息',
  SYSTEMID int(16) NOT NULL,
  ROUNDID int(16) COMMENT '轮次ID',
  KEY HisCaseFlowInstance_FK_ExecuteLogId (EXECUTELOGID),
  CONSTRAINT Hiscaseflowinstance_fk_execlogId FOREIGN KEY (EXECUTELOGID) REFERENCES his_execute_log (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_his_caseflow_instance on his_caseflow_instance(CASEFLOWID, EXECUTELOGID);


#--案例实例表
CREATE TABLE case_instance 
(
  ID int(16) NOT NULL auto_increment,
  CASEID int(16) NOT NULL,
  DBHOST varchar(32) COMMENT '卡所在的主机',
  CASENAME varchar(32) binary NOT NULL COMMENT '案例名称',
  CASENO varchar(32) binary NOT NULL COMMENT '案例编号',
  TRANSACTIONID int(16) NOT NULL COMMENT '交易ID',
  AMOUNT float(10,2) COMMENT '交易金额',
  CARDID int(16) NOT NULL COMMENT '卡ID',
  CARDNUMBER varchar(32) COMMENT '卡号码',
  IMPORTBATCHNO varchar(64) default '' COMMENT '案例导入时的批次号',
  EXECUTELOGID int(16) COMMENT '执行日志ID',
  DIRECTORYID int(16) COMMENT '案例是挂在树的那个节点上的？',
  CASEFLOWINSTANCEID int(16) COMMENT '业务流实例ID',
  SEQUENCE int(8) COMMENT '业务流内案例执行次序号',
  BEGINRUNTIME datetime COMMENT '案例开始执行时间（上送报文的时间）',
  ENDRUNTIME datetime COMMENT '案例结束执行时间（接收并处理应答报文的时间）',
  FIELD37 varchar(64) COMMENT '实际的参考检索号，an12，12位定长的字母和数字字符',
  FIELD38 char(6) COMMENT '发卡方给予被批准交易的授权号，或CUPS在对交易进行代授权时产生的代授权号，an6，6位定长的字母和数字字符',
  RECEIVEDREPLAYFLAG int(1) COMMENT '当前案例实例是否已经收到应答报文',
  CASEPASSFLAG int(1) default 3 COMMENT '案例通过与否的基本判断：7: 异常错误, 6:终止执行，5: 超时, 4: 中断, 3:未执行, 2: 正在执行中，1：通过，0：失败',
  ERRORMSG varchar(1024) default '' COMMENT '错误信息',  
  BREAKPOINTFLAG int(1) default 0 COMMENT '断点标志：0:无断点、1:有断点；只有当业务流有断点时业务流案例的断点才能生效',
  VALUE4NEXTCASE varchar(64) default '' COMMENT '为一下关联案例生成的关联值',
  REQUESTXML longtext COMMENT '组包后请求报文，XML格式的',
  RESPONSEXML longtext COMMENT '核心接收到的应答报文，XML格式的',
  EXPECTEDXML longtext COMMENT '各个字段的预期结果，XML',
  REQUESTMSG longtext COMMENT '组包后的请求报文，二进制的',
  RESPONSEMSG longtext COMMENT '核心接收到的应答报文，二进制的',
  SCRIPTNAME varchar(32) COMMENT '来自任务队列的信息，提供打印日志',
  TAG varchar(32) COMMENT '来自任务队列的信息，提供打印日志',
  KEY CaseInstance_FK_ExecuteLogId (EXECUTELOGID),
  KEY CaseInstance_FK_CaseId (CASEID),
  CONSTRAINT caseinstance_fk_execlogId FOREIGN KEY (EXECUTELOGID) REFERENCES execute_log (ID) ON DELETE CASCADE,
#  CONSTRAINT caseinstance_fk_caseId FOREIGN KEY (CASEID) REFERENCES cases (CASEID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_case_instance on case_instance(CASEID, EXECUTELOGID);
create index i_case_instance_FIELD37 on case_instance(FIELD37, RECEIVEDREPLAYFLAG);
create index i_case_instance_CASEFLOWINSTANCEID on case_instance(CASEFLOWINSTANCEID);
create index i_case_instance_REQUESTMSG on case_instance(REQUESTMSG);



#--案例实例历史表
CREATE TABLE his_case_instance 
(
  ID int(16) NOT NULL COMMENT '拷贝case_instance的ID',
  CASEID int(16) NOT NULL,
  DBHOST varchar(32) COMMENT '卡所在的主机',
  CASENAME varchar(32) binary NOT NULL COMMENT '案例名称',
  CASENO varchar(32) binary NOT NULL COMMENT '案例编号',
  TRANSACTIONID int(16) NOT NULL COMMENT '交易ID',
  AMOUNT float(10,2) COMMENT '交易金额',
  CARDID int(16) NOT NULL COMMENT '卡ID',
  CARDNUMBER varchar(32) COMMENT '卡号码',
  IMPORTBATCHNO varchar(64) default '' COMMENT '案例导入时的批次号',
  EXECUTELOGID int(16) COMMENT '执行日志ID',
  DIRECTORYID int(16) COMMENT '案例是挂在树的那个节点上的？',
  CASEFLOWINSTANCEID int(16) COMMENT '业务流实例ID',
  SEQUENCE int(8) COMMENT '业务流内案例执行次序号',
  BEGINRUNTIME datetime COMMENT '案例开始执行时间（上送报文的时间）',
  ENDRUNTIME datetime COMMENT '案例结束执行时间（接收并处理应答报文的时间）',
  FIELD37 varchar(64) COMMENT '实际的参考检索号，an12，12位定长的字母和数字字符',
  FIELD38 char(6) COMMENT '发卡方给予被批准交易的授权号，或CUPS在对交易进行代授权时产生的代授权号，an6，6位定长的字母和数字字符',
  RECEIVEDREPLAYFLAG int(1) COMMENT '当前案例实例是否已经收到应答报文',
  CASEPASSFLAG int(1) default 3 COMMENT '案例通过与否的基本判断：7: 异常错误, 6:终止执行，5: 超时, 4: 中断, 3:未执行, 2: 正在执行中，1：通过，0：失败',
  ERRORMSG varchar(1024) default '' COMMENT '错误信息',
  BREAKPOINTFLAG int(1) default 0 COMMENT '断点标志：0:无断点、1:有断点；只有当业务流有断点时业务流案例的断点才能生效',
  VALUE4NEXTCASE varchar(64) default '' COMMENT '为一下关联案例生成的关联值',
  REQUESTXML longtext COMMENT '组包前请求报文，XML',
  RESPONSEXML longtext COMMENT '核心接收到的应答报文，XML格式的',
  EXPECTEDXML longtext COMMENT '各个字段的预期结果，XML',
  REQUESTMSG longtext COMMENT '组包后的请求报文，二进制的',
  RESPONSEMSG longtext COMMENT '核心接收到的应答报文，二进制的',
  SCRIPTNAME varchar(32) COMMENT '来自任务队列的信息，提供打印日志',
  TAG varchar(32) COMMENT '来自任务队列的信息，提供打印日志',
  KEY HisCaseInstance_FK_ExecuteLogId (EXECUTELOGID),
  KEY HisCaseInstance_FK_CaseId (CASEID),
  CONSTRAINT hiscaseinstance_fk_execlogId FOREIGN KEY (EXECUTELOGID) REFERENCES his_execute_log (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_his_case_instance on his_case_instance(CASEID, EXECUTELOGID);
create index i_his_case_instance_FIELD37 on his_case_instance(FIELD37);
create index i_his_case_instance_CASEFLOWINSTANCEID on his_case_instance(CASEFLOWINSTANCEID);




#--被测系统的动态参数表
CREATE TABLE system_dynamic_parameter
(
  ID int(16) NOT NULL auto_increment,
  SYSTEMID int(16) NOT NULL,
  PARAMETERNAME varchar(64) COMMENT '参数名称',
  PARAMETERDESC varchar(64) COMMENT '参数说明',
  PARAMETEREXPRESSION varchar(2048) COMMENT '参数表达式，一般为包含有变量的SQL语句',
  DEFAULTEXPECTEDVALUE varchar(128) default '' COMMENT '参数默认的预期值',
  PARAMETERTYPE int(1) default 0 COMMENT '0：报文类参数，1：SQL参数，2：交易数据类参数，3：函数处理类参数，4：条件分支类参数',
  PARAMFROMMSGSRC int(1) default 0 COMMENT '1：从上传报文中获取，2：从下传报文中获取（仅当PARAMETERTYPE=0时有效）',
  COMPARECONDITION int(1) default 0 COMMENT '参数默认预期值比对条件：0: 完全一样,1: 实际值中包含有预期值, 2: 预期值中包含有实际值',
  MULTIDATAFLAG int(1) default 0 COMMENT '是否为多笔的参数，0: NO, 1: Yes',
  ISVALID int(1) default 1 COMMENT '参数状态：1:有效, 0:无效',
#  ISKEYMSGFIELD int(1) default 0 COMMENT '是否为关键报文字段：1:是, 0:否，这个字段用来作为匹配上传和下传报文对应关系的依据，仅当PARAMETERTYPE=0时有意义',
  PARAMETERHOSTTYPE int(1) default 0 COMMENT '参数到哪个主机上去查找：0：当前机器，1：指定机器，2：由所使用的卡信息来指定，3：默认机器',
  PARAMETERHOSTID int(16) COMMENT '当PARAMETERHOSTTYPE=1，指明参数所在主机ID，即dbhost.hostId',
  DISPLAYFLAG int(1) default 1 COMMENT '是否为需要在界面上显示的参数：1:需要显示, 0:不需要显示，一般为过渡性的参数，如交易流水号',
  REFETCHFLAG int(1) default 0 COMMENT '参数是否需要进行回溯获取：1:需要回溯获取, 0:不需要回溯获取',
  REFETCHMETHOD int(1) default 0 COMMENT '回溯获取的方式：0:使用原来的老参数进行查询, 1:使用新参数进行查询, 2: 使用老参数和新参数各查一遍',
  DIRECTORYID int(16) COMMENT '参数是挂在树的那个节点上的？',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY DYNAMICPARAM_FK_SYSTEMID (SYSTEMID),
  CONSTRAINT DYNAMICPARAM_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
  KEY DYNAMICPARAM_FK_DIRECTORYID (DIRECTORYID),
  CONSTRAINT DYNAMICPARAM_FK_DIRECTORYID FOREIGN KEY (DIRECTORYID) REFERENCES parameter_directory (DIRECTORYID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_dynamic_parameter on system_dynamic_parameter(SYSTEMID, PARAMETERNAME, DIRECTORYID);


#--交易的动态参数表
CREATE TABLE transaction_dynamic_parameter
(
  ID int(16) NOT NULL auto_increment,
  TRANSACTIONID int(16) NOT NULL COMMENT '交易ID',
  SYSTEMPARAMETERID int(16) NOT NULL COMMENT '系统动态参数ID',
  USERID int(16) NOT NULL COMMENT '交易动态参数最后修改用户',
  MODIFYTIME datetime COMMENT '交易动态参数最后修改时间',
  KEY TRANSACPARAM_FK_TRANSACTIONID (TRANSACTIONID),
  KEY TRANSACPARAM_FK_PARAMETERID (SYSTEMPARAMETERID),
  CONSTRAINT TRANSACPARAM_FK_TRANSACTIONID FOREIGN KEY (TRANSACTIONID) REFERENCES transaction (TRANSACTIONID) ON DELETE CASCADE,
  CONSTRAINT TRANSACPARAM_FK_PARAMETERID FOREIGN KEY (SYSTEMPARAMETERID) REFERENCES system_dynamic_parameter (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_transaction_dynamic_parameter on transaction_dynamic_parameter(TRANSACTIONID,SYSTEMPARAMETERID);



#--案例的动态参数预期值设置表
CREATE TABLE case_parameter_expected_value
(
  ID int(16) NOT NULL auto_increment,
  CASEID int(16) NOT NULL COMMENT '案例ID',
  TRANSACTIONPARAMETERID int(16) NOT NULL COMMENT '交易动态参数ID',
  EXPECTEDVALUETYPE int(1) default 0 COMMENT '参数预期值类型: 0：常量, 1:表达式',
  EXPECTEDVALUE varchar(1024) COMMENT '参数预期值',
  CreatedUserId int(16),
  CreatedTime datetime, 
  LastModifiedTime datetime, 
  LastModifiedUserId int(16),
  KEY CASEPARAM_FK_CASEID (CASEID),
  KEY CASEPARAM_FK_TRANSACTIONPARAMETERIDD (TRANSACTIONPARAMETERID),
  CONSTRAINT CASEPARAM_FK_CASEID FOREIGN KEY(CASEID) REFERENCES cases (CASEID) ON DELETE CASCADE,
  CONSTRAINT CASEPARAM_FK_TRANSACTIONPARAMETERIDD FOREIGN KEY(TRANSACTIONPARAMETERID) REFERENCES transaction_dynamic_parameter (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_case_parameter_expected_value on case_parameter_expected_value(CASEID,TRANSACTIONPARAMETERID);


#--案例实例SQL参数表
CREATE TABLE case_instance_sql_value 
(
  ID int(16) NOT NULL auto_increment,
  CASEINSTANCEID int(16) NOT NULL COMMENT '案例实例ID',
  CASEFLOWSTEP int(2) COMMENT '业务流执行到了第几步、第几个案例，如果不案例不属于业务流，为单独案例，则该字段为空',
  ISCURRENTSTEP int(1) COMMENT '是否为当前步骤：0:No, 1:Yes; 对于非业务流的单独案例而言，该字段没有意义',
  SEQUENCE int(8) COMMENT '参数序号',
  TRANSACTIONPARAMETERID int(16) NOT NULL COMMENT '交易动态参数ID',
  PARAMETERNAME varchar(64) COMMENT '参数名称',
  REALSQL varchar(2048) COMMENT '获取参数值的具体SQL语句（正常情况下已经不包含变量了）',
  REALVALUE mediumtext COMMENT '参数实际值',
  EXPECTEDVALUE mediumtext COMMENT '参数预期值',
  KEY CASEINSTSQLPARAM_CaseInstanceId (CASEINSTANCEID),
  CONSTRAINT CASEINSTSQLPARAM_CaseInstanceId FOREIGN KEY (CASEINSTANCEID) REFERENCES case_instance (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_case_instance_sql_value on case_instance_sql_value(CASEINSTANCEID,CASEFLOWSTEP,TRANSACTIONPARAMETERID); 


#--案例实例SQL参数历史表
CREATE TABLE his_case_instance_sql_value 
(
  ID int(16) NOT NULL COMMENT '拷贝case_instance_sql_value的ID',
  CASEINSTANCEID int(16) NOT NULL COMMENT '案例实例ID',
  CASEFLOWSTEP int(2) COMMENT '业务流执行到了第几步、第几个案例，如果不案例不属于业务流，为单独案例，则该字段为空',
  ISCURRENTSTEP int(1) COMMENT '是否为当前步骤：0:No, 1:Yes; 对于非业务流的单独案例而言，该字段没有意义',
  SEQUENCE int(8) COMMENT '参数序号',
  TRANSACTIONPARAMETERID int(16) NOT NULL COMMENT '交易动态参数ID',
  PARAMETERNAME varchar(64) COMMENT '参数名称',
  REALSQL varchar(2048) COMMENT '获取参数值的具体SQL语句（正常情况下已经不包含变量了）',
  REALVALUE mediumtext COMMENT '参数实际值',
  EXPECTEDVALUE mediumtext COMMENT '参数预期值',
  KEY HISCASEINSTSQLPARAM_CaseInstanceId (CASEINSTANCEID),
  CONSTRAINT HISCASEINSTSQLPARAM_CaseInstanceId FOREIGN KEY (CASEINSTANCEID) REFERENCES his_case_instance (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--案例实例报文参数表
CREATE TABLE case_instance_field_value 
(
  ID int(16) NOT NULL auto_increment,
  CASEINSTANCEID int(16) NOT NULL COMMENT '案例实例ID',
  TRANSACTIONPARAMETERID int(16) NOT NULL COMMENT '交易动态参数ID',
  MSGFIELDNAME varchar(64) COMMENT '报文字段',
  MSGFIELDVALUE mediumtext COMMENT '报文字段实际内容',
  EXPECTEDVALUE mediumtext COMMENT '字段参数预期值',
  PARAMETERTYPE int(1) default 0 COMMENT '0: 字段参数，2：交易数据类参数，3：函数处理类参数，4：条件分支类参数',
  KEY CASEINSTFIELDPARAM_CaseInstanceId (CASEINSTANCEID),
  CONSTRAINT CASEINSTFIELDPARAM_CaseInstanceId FOREIGN KEY (CASEINSTANCEID) REFERENCES case_instance (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_case_instance_field_value on case_instance_field_value(CASEINSTANCEID,TRANSACTIONPARAMETERID); 


#--案例实例报文参数历史表
CREATE TABLE his_case_instance_field_value (

  ID int(16) NOT NULL COMMENT '拷贝case_instance_field_value的ID',
  CASEINSTANCEID int(16) NOT NULL COMMENT '案例实例ID',
  TRANSACTIONPARAMETERID int(16) NOT NULL COMMENT '交易动态参数ID',
  MSGFIELDNAME varchar(64) COMMENT '报文字段',
  MSGFIELDVALUE mediumtext COMMENT '报文字段实际内容',
  EXPECTEDVALUE mediumtext COMMENT '字段参数预期值',
  PARAMETERTYPE int(1) default 0 COMMENT '0: 字段参数，2：交易数据类参数，3：函数处理类参数，4：条件分支类参数',
  KEY HISCASEINSTFIELDPARAM_CaseInstanceId (CASEINSTANCEID),
  CONSTRAINT hisCASEINSTFIELDPARAM_CaseInstanceId FOREIGN KEY (CASEINSTANCEID) REFERENCES his_case_instance (ID) ON DELETE CASCADE,
  PRIMARY KEY  (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--当天收发日志
CREATE TABLE comm_msg_log
(
  ID                  int(16) NOT NULL AUTO_INCREMENT,
  TRANSACTIONNAME     varchar(64) COMMENT '交易名称',
  CASENAME            varchar(64) COMMENT '案例名称',
  EXECUTELOGID        int COMMENT '执行日志ID',
  CASEINSTANCEID      int COMMENT '案例实例ID',
  MSG_CONTENT         mediumblob COMMENT '报文内容',
  HEAD10              char(5) COMMENT '拒绝码, ：n5，5 位定长数字字符',
  SENDTIME            datetime,
  SENDSTATUS          char(1) COMMENT '0:未发送，1: 发送/接收成功，2：发送/接收失败',
  SENDREPORT          varchar(256) COMMENT '发送报告，如发送失败，写入失败提示信息',
  DIRECTION           char(1) NOT NULL COMMENT '1:发送，2:接收',
  KEY CommMsgLog_CaseInstanceId (CASEINSTANCEID),
  CONSTRAINT commmsglog_fk_caseInstId FOREIGN KEY (CASEINSTANCEID) REFERENCES case_instance (ID) ON DELETE CASCADE,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#--历史收发日志
CREATE TABLE his_comm_msg_log
(
  ID                  int(16) COMMENT '拷贝comm_msg_log的ID',
  TRANSACTIONNAME     varchar(64) COMMENT '交易名称',
  CASENAME            varchar(64) COMMENT '案例名称',
  EXECUTELOGID        int COMMENT '执行日志ID',
  CASEINSTANCEID      int COMMENT '案例实例ID',
  MSG_CONTENT         mediumblob COMMENT '报文内容',
  HEAD10              char(5) COMMENT '拒绝码, ：n5，5 位定长数字字符',
  SENDTIME            datetime,
  SENDSTATUS          char(1) COMMENT '0:未发送，1: 发送/接收成功，2：发送/接收失败',
  SENDREPORT          varchar(256) COMMENT '发送报告，如发送失败，写入失败提示信息',
  DIRECTION           char(1) NOT NULL COMMENT '1:发送，2:接收',
  CREATETIME          datetime COMMENT '记录创建时间',
  KEY HisCommMsgLog_CaseInstanceId (CASEINSTANCEID),
  CONSTRAINT hiscommmsglog_fk_caseInstId FOREIGN KEY (CASEINSTANCEID) REFERENCES his_case_instance (ID) ON DELETE CASCADE,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



#--分行表
CREATE TABLE subbank
(
   SUBBANKNO        char(6)     COMMENT '分行号',
   SUBBANKNAME      varchar(64) COMMENT '分行名称',
   FULLNAME         varchar(64) COMMENT '分行全名',
   PRIMARY KEY  (SUBBANKNO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#--delete from subbank;
insert into subbank(SUBBANKNO,SUBBANKNAME,FULLNAME) values('755','深圳分行', '755 : 深圳分行');
insert into subbank(SUBBANKNO,SUBBANKNAME,FULLNAME) values('571','杭州分行', '571 : 杭州分行');
insert into subbank(SUBBANKNO,SUBBANKNAME,FULLNAME) values('351','太原分行', '351 : 太原分行');

#--机构表
CREATE TABLE subsidiary
(
   SUBSIDIARYNO     char(6)     COMMENT '机构号',
   SUBSIDIARYNAME   varchar(64) COMMENT '机构名称',
   DEPARTMENTTYPE   varchar(6)  COMMENT '部门类型',
   SUBBANKNO        char(3)     COMMENT '分行号',
   PRIMARY KEY  (SUBSIDIARYNO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#--delete from subsidiary;
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755598','深圳分行营业部','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755595','深圳分行深纺支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755512','深圳分行蛇口支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755519','深圳分行高新园支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755530','深圳分行南山支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755538','深圳分行车公庙支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755540','深圳分行宝安支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755545','深圳分行上步支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('755550','深圳分行罗湖支行','755');
insert into subsidiary(SUBSIDIARYNO,SUBSIDIARYNAME,SUBBANKNO) values('351501','太原分行营业部','351');


#--卡信息表
CREATE TABLE card
(
   ID               int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID         int(16) NOT NULL,
   DBHOST           varchar(32) COMMENT '卡所在的主机',
   SUBBANKNO        varchar(32) COMMENT '卡所属分行号',
   SUBSIDIARYNO     char(6) COMMENT '卡所属机构号',
   IMPORTBATCHNO    varchar(64) default '' COMMENT '案例导入时的批次号',
   SEQUENCE         int(8) COMMENT '编号',
   CARDNUMBER       varchar(32) COMMENT '卡号码',
   CARDTYPE         char(4) COMMENT '卡类型',
   CARDPWD          char(6) COMMENT '卡密码',
   CARDSTATUS       varchar(32) COMMENT '卡状态',
   VALIDUNTIL       varchar(16) COMMENT '卡有效期',
   USERNAME         varchar(32) COMMENT '卡用户名',
   IDTYPE           int(2) COMMENT '证件类型',
   IDNO             varchar(32) COMMENT '证件号码',
   MOBILEPHONE      varchar(32) COMMENT '手机号码',
   TRACK2           char(37)  COMMENT '二磁',
   TRACK3           char(104) COMMENT '三磁',
   MAGNETICSTRIPE   char(152) COMMENT '磁条信息',
   CVCCOD           varchar(16) COMMENT 'CVC码',
   DESCRIPTION      varchar(64) COMMENT '说明',
   KEY Card_FK_SystemId (SYSTEMID),
   CONSTRAINT Card_FK_SystemId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,   
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_card_khm on card(CARDNUMBER);
create unique index i_card_pch on card(IMPORTBATCHNO,CARDNUMBER);


#--主机信息表
CREATE TABLE dbhost
(
   HOSTID           int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID         int(16) NOT NULL,
   DBHOST           varchar(32) NOT NULL COMMENT '主机名称',
   IPADDRESS        varchar(32) NOT NULL COMMENT 'IP地址',
#   CONNECTMETHOD    int(1) default 0 COMMENT '连接方式：0:通过TCP连接的方式获取参数、1:通过JDBC连接方式获取参数',
   PORTNUM          int(16) default 0 COMMENT '端口号, 当连接方式为TCP连接时指TCP Socket的端口号，当连接方式为JDBC连接时指访问数据库服务的端口号',
   ISLONGCONN       int(1) default 1 COMMENT '是否为长连接：1:长连接, 0:短连接, 仅当连接方式为TCP连接时才有意义',
   DBTYPE           varchar(32) default '' COMMENT 'SQL查询的数据库类型，如DB2, MYSQL, SQLSERVER, ORALCE等',
   OSTYPE           varchar(32) default '' COMMENT '操作系统平台，如WINDOWS, AS400, RS6000, LINUX等',
   DBNAME           varchar(32) default '' COMMENT 'SQL查询的数据库名称，如tesdb',
   DBUSER           varchar(32) default '' COMMENT 'SQL查询的默认数据库用户名，如PGQRY2',
   DBPWD            varchar(32) default '' COMMENT 'SQL查询的默认数据库密码，如PGQRY2',
   DESCRIPTION      varchar(64) default '' COMMENT '说明', 
   CreatedUserId int(16),
   CreatedTime datetime, 
   LastModifiedTime datetime, 
   LastModifiedUserId int(16),
   KEY DBHOST_FK_SystemId (SYSTEMID),
   CONSTRAINT DBHOST_FK_SystemId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   PRIMARY KEY (HOSTID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_dbhost on dbhost(SYSTEMID, DBHOST);
#--insert into dbhost(dbhost,IPADDRESS) values('CMB01','99.8.46.19');
#--insert into dbhost(dbhost,IPADDRESS) values('CMB03','99.8.46.18');
#--insert into dbhost(dbhost,IPADDRESS) values('CMB05','99.8.46.17');



# 执行计划表
CREATE TABLE execute_plan
(
   ID                     int(16) NOT NULL AUTO_INCREMENT,
   NAME                   varchar(64) COMMENT '执行计划名称',
   DESCRIPTION            varchar(1024) COMMENT '执行计划说明',
   SYSTEMID               int(16) NOT NULL,
   CreatedUserId int(16),
   CreatedTime datetime,
   LastModifiedTime datetime, 
   LastModifiedUserId int(16),
   STATUS                 int(1) default 1 NOT NULL COMMENT '执行计划是否有效 0:无效, 1:有效',
   SCHEDULEDRUNMODE       int(1) default 0 COMMENT '定时执行模式: 1:只定时执行一次, 2:每日执行一次, 3: 每周执行一次，4: 每月执行一次',
   SCHEDULEDRUNWEEKDAY    varchar(10) COMMENT '1: 20110121, 2: 无意义, 3: 星期0~6, 4: 1~31日',
   SCHEDULEDRUNHOUR       varchar(8) COMMENT '24小时制 0~23',
   KEY execute_plan_FK_SystemId (SYSTEMID),
   CONSTRAINT execute_plan_FK_SystemId FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_execute_plan_systemId on execute_plan(SYSTEMID, SCHEDULEDRUNMODE);


# 执行集的执行计划表
CREATE TABLE executeset_executeplan
(
   ID                     int(16) NOT NULL AUTO_INCREMENT,
   EXECUTEPLANID          int(16) NOT NULL COMMENT '执行计划ID',
   EXECUTESETDIRID        int(16) NOT NULL COMMENT '执行集树目录ID',
   SYSTEMID               int(16) NOT NULL,
   ADDUSERID              int(16) NOT NULL,
   ADDTIME                datetime COMMENT '创建时间',
   SCHEDULEDRUNSTATUS     int(1) default 0 COMMENT '调度执行状态 0:未调度执行, 2:调度执行完成, -1: 正在调度执行中',
   BEGINRUNTIME           datetime COMMENT '最后一次执行的开始执行时间',
   ENDRUNTIME             datetime COMMENT '最后一次执行的结束执行时间',
   SCHEDULEDRUNMACHINE    varchar(64) COMMENT '预定执行机器IP，空则不限制' ,
   SCHEDULEDRUNMACNAME    varchar(64) COMMENT '预定执行机器名称，空则不限制' ,
   CONSTRAINT executeset_executeplan_fk_executePlanId FOREIGN KEY (EXECUTEPLANID) REFERENCES execute_plan (ID) ON DELETE CASCADE,
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_executeset_executeplan on executeset_executeplan(EXECUTEPLANID, EXECUTESETDIRID);


# 接口定义表
CREATE TABLE interfacedef
(
   INTERFACEID            int(16) PRIMARY KEY,
   SYSTEMID               int(16) NOT NULL,
   INTERFACENAME          varchar(64),
   CHINESENAME            varchar(64),
   INTERFACELEN           int(4),
   FIELDCOUNT             int(4),
   IMPORTUSERID           int(16) NOT NULL COMMENT '导入用户',
   IMPORTTIME             datetime NOT NULL COMMENT '导入时间',
   MEMO                   varchar(256),
   KEY INTERFACEDEF_FK_SYSTEMID (SYSTEMID),
   CONSTRAINT INTERFACEDEF_FK_SYSTEMID FOREIGN KEY (SYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_interfacedef on interfacedef(SYSTEMID, INTERFACENAME);


# 接口字段表
CREATE TABLE interfacefield
(
   FIELDID                int(16) PRIMARY KEY,
   INTERFACEID            int(16),
   SEQUENCE               int(2),
   FIELDNAME              varchar(64),
   CHINESENAME            varchar(64),
   FIELDTYPE              char(1) COMMENT 'A: 字符, S: 数值, D: 日期, T: 时间',
   FIELDLEN               int(4),
   DECIMALDIGITS          int(4),
   FIELDTYPEEXPR          varchar(16),
   OPTIONAL               char(1) COMMENT 'Optional or Compulsory',
   DEFAULTVALUE           varchar(128),
   MEMO                   varchar(256),
   KEY INTERFACEFILED_FK_INTERFACEID (INTERFACEID),
   CONSTRAINT INTERFACEFILED_FK_INTERFACEID FOREIGN KEY (INTERFACEID) REFERENCES interfacedef (INTERFACEID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create Index i_interfacefield_fieldname on interfacefield(INTERFACEID,FIELDNAME);


CREATE TABLE system_copy_id_map
(
   ID          int(16) PRIMARY KEY,
   OLDSYSTEMID int(16) NOT NULL,
   NEWSYSTEMID int(16) NOT NULL,
   TYPE        int(1)  NOT NULL COMMENT '0: 交易类别，1：主机，2: 系统参数，3：交易，4：（业务流）案例, 5：执行集，6: ScriptFlow, 7: 交易参数',
   OLDID       int(16) NOT NULL,
   NEWID       int(16) NOT NULL, 
   CONSTRAINT system_copy_id_map_FK_OLDSYSTEMID FOREIGN KEY (OLDSYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE,
   CONSTRAINT system_copy_id_map_FK_NEWSYSTEMID FOREIGN KEY (NEWSYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create Index i_system_copy_id_map on system_copy_id_map(OLDSYSTEMID,NEWSYSTEMID,TYPE,OLDID);


DROP TABLE IF EXISTS copied_system;
#--拷贝的系统表
CREATE TABLE copied_system
(
   ID          int(16) auto_increment PRIMARY KEY,
   SYSTEMNAME  varchar(32) binary NOT NULL COMMENT '被模拟系统名称',
   SYSTEMNO    varchar(32) COMMENT '被模拟系统代号',
   OLDSYSTEMID int(16) NOT NULL,
   NEWSYSTEMID int(16),
   COPYSTATUS  int(1) default 0 NOT NULL COMMENT '执行计划是否有效, 0:记录刚生成, 1:正在复制, 2: 复制成功完成，3: 复制失败',
   COPIEDUSERID int(16),
   COPIEDTIME TIMESTAMP, 
   CONSTRAINT copied_system_FK_OLDSYSTEMID FOREIGN KEY (OLDSYSTEMID) REFERENCES systype (SYSTEMID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index i_copied_system_name on copied_system(SYSTEMNAME);


#--界面登录历史表
create table login_log
(
   ID int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID int(16) NOT NULL,
   USERID int(16) NOT NULL,
   IPADDRESS varchar(64),
   MACHINENAME varchar(64),
   LOGINCOUNT int default 0 COMMENT 'User在当前IP的总登录次数',
   LOGINTIME datetime,
   LOGOUTTIME datetime, 
   MEMO varchar(1024) COMMENT '备注',
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_login_log on login_log(SYSTEMID,USERID);

#--操作日志表
create table operation_log
(
   ID int(16) NOT NULL AUTO_INCREMENT,
   SYSTEMID int(16) NOT NULL,
   USERID int(16) NOT NULL,
   LOGINLOGID int(16),
   OBJID int(16) COMMENT '被操作对象的ID',
   OBJNAME varchar(64) COMMENT '被操作对象的名称',
   IDUTYPE int(1) COMMENT '增删改类型: 1: 新增，2：删除, ３：修改，4：导入',
   OPTYPE int(16) COMMENT '操作类型: 1: 系统, 2: 用户, 3: 授权，4: 取消授权，5:主机，6:接口，7:系统参数，8:轮次，9:执行计划，11:交易，12:交易请求报文，13:交易应答报文，14:交易参数，21:测试用例，22:用例步骤，23:案例请求报文，24:案例预期应答报文，25:案例参数预期值，26:执行集，27:执行集元素，28:可执行脚本，31:适配器，32:组包组件，33:拆包组件，34:交易识别码，35:通道',
   OPFIELD varchar(256) COMMENT '修改字段',
   OLDVALUE varchar(1024) COMMENT '旧值',
   NEWVALUE varchar(1024) COMMENT '新值',
   MEMO varchar(1024) COMMENT '备注',
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_operation_log on operation_log(SYSTEMID,USERID);



#--用例执行统计汇总表（按时间段汇总）
create table Case_Run_Statistics
(
   CaseRunStatisticsId                  int(16) not null auto_increment PRIMARY KEY,
   SYSTEMID                             int(16) NOT NULL,
   StatMonth                            varchar(10) COMMENT '统计月份，如201401',
   StatStartDay                         varchar(8) COMMENT '统计区间开始时间，如20140101',
   StatEndDay                           varchar(8) COMMENT '统计区间截止时间，如20140131',
   TotalRunCaseFlowCount                int default 0 COMMENT '本月执行的总用例个数',
   TotalPassedCaseFlowCount             int default 0 COMMENT '本月执行通过的总用例个数',
   TotalRunCaseCount                    int default 0 COMMENT '本月执行的总案例个数',
   CaseFlowPassRate                     varchar(64) default '' COMMENT '本月执行的用例通过率',
   TotalRunUserCount                    int default 0 COMMENT '本月执行的总用户数',
   CreatedTransactionCount              int default 0 COMMENT '新创建的交易个数',
   CreatedCaseFlowCount                 int default 0 COMMENT '新创建的用例个数',
   CreatedCaseCount                     int default 0 COMMENT '新创建的案例个数',
   CreatedSysParamCount                 int default 0 COMMENT '新创建的参数个数',
   ModifiedTransactionCount             int default 0 COMMENT '修改的交易个数',
   ModifiedCaseFlowCount                int default 0 COMMENT '修改的用例个数',
   ModifiedCaseCount                    int default 0 COMMENT '修改的案例个数',
   ModifiedSysParamCount                int default 0 COMMENT '修改的参数个数',
   StatIpAddress                        varchar(64) COMMENT '统计者所在的IP地址',
   StatHostName                         varchar(64) COMMENT '统计者所在的主机名称',
   StatUserId                           int(16) COMMENT '统计者',
   StatTime                             datetime COMMENT '统计时刻',
   StatStatus                           int default 0 COMMENT '状态, 0: 初始状态，-1: 正在执行，1: 执行情况统计已经完成，2：要素变更统计也已经完成，3：统计未完成或异常中断',
   FirstRunTime                         datetime COMMENT '最早开始执行时间',
   LastRunTime                          datetime COMMENT '最晚开始执行时间',
   Memo                                 varchar(256) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_Case_Run_Statistics on Case_Run_Statistics(SYSTEMID, StatMonth);

#--用例执行统计汇总表（按 时间段+用户 汇总）
create table Case_Run_UserStats
(
   CaseRunUserStatId                    int(16) not null auto_increment primary key,
   CaseRunStatisticsId                  int(16),
   RunUserId                            int(16) COMMENT '执行用户（被统计用户）',
   TotalRunCaseFlowCount                int default 0 COMMENT '本月执行的总用例个数',
   TotalPassedCaseFlowCount             int default 0 COMMENT '本月执行通过的总用例个数',
   TotalRunCaseCount                    int default 0 COMMENT '本月执行的总案例个数',
   CaseFlowPassRate                     varchar(64) default '' COMMENT '本月执行的用例通过率',
   FirstRunTime                         datetime COMMENT '最早开始执行时间',
   LastRunTime                          datetime COMMENT '最晚开始执行时间',
   CreatedTransactionCount              int default 0 COMMENT '新创建的交易个数',
   CreatedCaseFlowCount                 int default 0 COMMENT '新创建的用例个数',
   CreatedCaseCount                     int default 0 COMMENT '新创建的案例个数',
   CreatedSysParamCount                 int default 0 COMMENT '新创建的参数个数',
   CreatedParExpValCount                int default 0 COMMENT '新创建的参数预期值个数',
   ModifiedTransactionCount             int default 0 COMMENT '修改的交易个数',
   ModifiedCaseFlowCount                int default 0 COMMENT '修改的用例个数',
   ModifiedCaseCount                    int default 0 COMMENT '修改的案例个数',
   ModifiedSysParamCount                int default 0 COMMENT '修改的参数个数',
   ModifiedParExpValCount               int default 0 COMMENT '修改的参数预期值个数',
   Memo                                 varchar(256) COMMENT '备注',
   KEY CASERUNUSERSTAT_CASERUNSTATID(CaseRunStatisticsId),
   CONSTRAINT CASERUNUSERSTAT_CASERUNSTATID FOREIGN KEY (CaseRunStatisticsId) REFERENCES Case_Run_Statistics (CaseRunStatisticsId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_Case_Run_UserStats on Case_Run_UserStats(CaseRunStatisticsId, RunUserId);


#--要素变更统计表（快照表，涵盖统计期间内所有新建或者修改过的交易、案例、参数等）
create table Factor_Change_Statistics
(
   ID                                   int(16) not null auto_increment primary key,
   SYSTEMID                             int(16) NOT NULL,
   StatMonth                            varchar(10) COMMENT '统计月份，如201401',
   StatStartDay                         varchar(8) COMMENT '统计区间开始时间，如20140101',
   StatEndDay                           varchar(8) COMMENT '统计区间截止时间，如20140131',
   CreatedTransactionCount              int default 0 COMMENT '新创建的交易个数',
   CreatedCaseFlowCount                 int default 0 COMMENT '新创建的用例个数',
   CreatedCaseCount                     int default 0 COMMENT '新创建的案例个数',
   CreatedSysParamCount                 int default 0 COMMENT '新创建的参数个数',
   ModifiedTransactionCount             int default 0 COMMENT '修改的交易个数',
   ModifiedCaseFlowCount                int default 0 COMMENT '修改的用例个数',
   ModifiedCaseCount                    int default 0 COMMENT '修改的案例个数',
   ModifiedSysParamCount                int default 0 COMMENT '修改的参数个数',
   StatUserId                           int(16) COMMENT '统计者',
   StatStatus                           int COMMENT '状态, -1: 正在执行，1: 执行情况统计已经完成，2：要素变更统计也已经完成，3：统计未完成或异常中断',
   StatTime                             datetime COMMENT '统计时刻',
   Memo                                 varchar(256) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_Factor_Change_Statistics on Factor_Change_Statistics(SYSTEMID, StatMonth);


#--要素变更统计表（快照表，涵盖统计期间内所有新建或者修改过的交易、案例、参数等）
create table Factor_Change_UserStats
(
   ID                                   int(16) not null auto_increment primary key,
   FactorChangeStatId                   int(16) NOT NULL,
   OpUserId                             int(16) COMMENT '操作用户（被统计用户）',
   CreatedTransactionCount              int default 0 COMMENT '新创建的交易个数',
   CreatedCaseFlowCount                 int default 0 COMMENT '新创建的用例个数',
   CreatedCaseCount                     int default 0 COMMENT '新创建的案例个数',
   CreatedSysParamCount                 int default 0 COMMENT '新创建的参数个数',
   CreatedParExpValCount                int default 0 COMMENT '新创建的参数预期值个数',
   ModifiedTransactionCount             int default 0 COMMENT '修改的交易个数',
   ModifiedCaseFlowCount                int default 0 COMMENT '修改的用例个数',
   ModifiedCaseCount                    int default 0 COMMENT '修改的案例个数',
   ModifiedSysParamCount                int default 0 COMMENT '修改的参数个数',
   ModifiedParExpValCount               int default 0 COMMENT '修改的参数预期值个数',
   Memo                                 varchar(256) COMMENT '备注',
   KEY Factor_Change_UserStats_FID(FactorChangeStatId),
   CONSTRAINT Factor_Change_UserStats_FID FOREIGN KEY (FactorChangeStatId) REFERENCES Factor_Change_Statistics (ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index ui_Factor_Change_UserStats on Factor_Change_UserStats(FactorChangeStatId, OpUserId);


#--报文录制信息表
create table recorded_case
(
   ID int(16) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
   SYSTEMID int(16) NOT NULL,
   REQUESTMSG longtext COMMENT '请求报文，二进制的',
   RESPONSEMSG longtext COMMENT '应答报文，二进制的',
   RESPONSEFLAG int(1) NOT NULL default 0,
   RECORDUSERID int(16),
   RECORDTIME TIMESTAMP, 
   MEMO varchar(1024) default '' COMMENT '备注',
   CASEID int(16),
   ISCASED int(1) NOT NULL default 0 COMMENT '是否做过案例化',
   CREATETIME varchar(64) NOT NULL COMMENT '创建时间',
   LastModifiedTime datetime, 
   LastModifiedUserId int(16)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index i_recorded_case on recorded_case(SYSTEMID, RESPONSEFLAG);
