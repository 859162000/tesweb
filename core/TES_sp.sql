drop TRIGGER if exists t_afterinsert_on_copied_system;
CREATE TRIGGER t_afterinsert_on_copied_system AFTER INSERT ON copied_system for each row
BEGIN
  declare iNewSystemId int;
  if new.SYSTEMNAME is not null and new.OLDSYSTEMID is not null then
      call sp_copy_system(new.SYSTEMNAME, new.SYSTEMNO, new.OLDSYSTEMID);
  end if;
END;


drop procedure if exists sp_copy_system;
create procedure sp_copy_system
(
     SYSNAME varchar(32),
     SYSNO varchar(32),
     OldSystemID int(16)
)
begin
    declare iNewSystemId int(16);
    declare strIPADRESS varchar(64);
    declare iPORTNUM int(8);
    declare strCHANNEL varchar(32);
    declare strBASECFG mediumtext;
    declare strSTYLESTRUCT mediumtext;
    declare iNEEDSQLCHECK int(1);
    declare strSQLGETMETHOD int(1);
    declare strSQLGETDBADDR varchar(32);
    declare strENCODING4REQUESTMSG varchar(32);
    declare strENCODING4RESPONSEMSG varchar(32);


    if SYSNO is null then
      set SYSNO = '';
    end if;

    /*插入系统表*/
    insert into systype(SYSTEMNAME, SYSTEMNO) values(SYSNAME, SYSNO);
    set iNewSystemId = last_insert_id();


    /*更新刚刚新插入的系统表记录*/

    select IPADRESS into strIPADRESS from systype where SYSTEMID=OldSystemID;
    update systype set IPADRESS=strIPADRESS where SYSTEMID=iNewSystemId;

    select PORTNUM into iPORTNUM from systype where SYSTEMID=OldSystemID;
    update systype set PORTNUM=iPORTNUM where SYSTEMID=iNewSystemId;

    select CHANNEL into strCHANNEL from systype where SYSTEMID=OldSystemID;
    update systype set CHANNEL=strCHANNEL where SYSTEMID=iNewSystemId;

    select BASECFG into strBASECFG from systype where SYSTEMID=OldSystemID;
    update systype set BASECFG=strBASECFG where SYSTEMID=iNewSystemId;

    select STYLESTRUCT into strSTYLESTRUCT from systype where SYSTEMID=OldSystemID;
    update systype set STYLESTRUCT=strSTYLESTRUCT where SYSTEMID=iNewSystemId;

    select NEEDSQLCHECK into iNEEDSQLCHECK from systype where SYSTEMID=OldSystemID;
    update systype set NEEDSQLCHECK=iNEEDSQLCHECK where SYSTEMID=iNewSystemId;

    select SQLGETMETHOD into strSQLGETMETHOD from systype where SYSTEMID=OldSystemID;
    update systype set SQLGETMETHOD=strSQLGETMETHOD where SYSTEMID=iNewSystemId;


    select SQLGETDBADDR into strSQLGETDBADDR  from systype where SYSTEMID=OldSystemID;
    update systype set SQLGETDBADDR=strSQLGETDBADDR where SYSTEMID=iNewSystemId;

    select ENCODING4REQUESTMSG into strENCODING4REQUESTMSG from systype where SYSTEMID=OldSystemID;
    update systype set ENCODING4REQUESTMSG=strENCODING4REQUESTMSG where SYSTEMID=iNewSystemId;

    select ENCODING4RESPONSEMSG into strENCODING4RESPONSEMSG from systype where SYSTEMID=OldSystemID;
    update systype set ENCODING4RESPONSEMSG=strENCODING4RESPONSEMSG where SYSTEMID=iNewSystemId;

    /* update copied_system set NEWSYSTEMID=iNewSystemId, COPYSTATUS=1 where SYSTEMNAME=SYSNAME; --此操作执行不了 */

    /* 拷贝 userrsystem 表 */
    insert into userrsystem(USERID, SYSTEMID) select USERID, iNewSystemId from userrsystem where SYSTEMID=OldSystemID;
    
    /* 拷贝 test_round 表 */
    insert into test_round(SYSTEMID, ROUNDNO, ROUNDNAME, DESCRIPTION, STARTDATE, ENDDATE, CURRENTROUNDFLAG) select iNewSystemId, ROUNDNO, ROUNDNAME, DESCRIPTION, STARTDATE, ENDDATE, CURRENTROUNDFLAG from test_round where SYSTEMID=OldSystemID;

		/* 拷贝 channel 表 */
		insert into channel(NAME, ADAPTERID, TRANSRECOGNIZERID, RECOGNIZERCFGINFO, PACKID, UNPACKID, SENDADAPTERIP, SENDADAPTERPORT, ADAPTERCFGINFO, SYSTEMID, CHANNELTYPE) select  NAME, ADAPTERID, TRANSRECOGNIZERID, RECOGNIZERCFGINFO, PACKID, UNPACKID, SENDADAPTERIP, SENDADAPTERPORT, ADAPTERCFGINFO, iNewSystemId, CHANNELTYPE from channel where SYSTEMID=OldSystemID;
    
    /* 拷贝 execute_plan 表 */
    insert into execute_plan(NAME, DESCRIPTION, SYSTEMID, CreatedUserId, CreatedTime, STATUS, SCHEDULEDRUNMODE, SCHEDULEDRUNWEEKDAY, SCHEDULEDRUNHOUR) select NAME, DESCRIPTION, iNewSystemId, CreatedUserId, CreatedTime, STATUS, SCHEDULEDRUNMODE, SCHEDULEDRUNWEEKDAY, SCHEDULEDRUNHOUR from execute_plan where SYSTEMID=OldSystemID;

    SET @@max_sp_recursion_depth = 20;

    /* 拷贝 dbhost 表 */
    call sp_copy_dbhosts(OldSystemID, iNewSystemId);
    
    /* 拷贝 scriptflow 表 */
    call sp_copy_scriptflows(OldSystemID, iNewSystemId);

    /* 拷贝 transaction_catetory 表 */
    /* call sp_copy_transaction_catetory(OldSystemID, iNewSystemId); */

    /* 拷贝 parameter_directory 和 system_dynamic_parameter 表 */
    call sp_copy_parameter_tree(OldSystemID, iNewSystemId);

    /* 拷贝 transaction 表 */
    call sp_copy_transactions(OldSystemID, iNewSystemId);
    
    /* 拷贝 case_directory 和 caseflow 及 case 表 */
    call sp_copy_caseflow_tree(OldSystemID, iNewSystemId);
    
    /* 拷贝 executeset 和 executeset_taskitem 表 */
    call sp_copy_executeset_tree(OldSystemID, iNewSystemId);
    
    /* 拷贝 interfacedef 和 interfacefield 表 */
    call sp_copy_interfaces(OldSystemID, iNewSystemId);
    
    delete from system_copy_id_map where OLDSYSTEMID=OldSystemID and NEWSYSTEMID=iNewSystemId;
    /* delete from copied_system where SYSTEMNAME=SYSNAME and SYSTEMNO=SYSNO and OLDSYSTEMID=OldSystemID; */
end;


/* 拷贝 db_host 表的内容 */
drop procedure if exists sp_copy_dbhosts;
create procedure sp_copy_dbhosts
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare iHOSTID               int(16);
    declare strDBHOST             varchar(32);
    declare strIPADDRESS          varchar(32);
    declare iPORTNUM              int(16);
    declare iISLONGCONN           int(1);
    declare strDBTYPE             varchar(32);
    declare strOSTYPE             varchar(32);
    declare strDBNAME             varchar(32);
    declare strDBUSER             varchar(32);
    declare strDBPWD              varchar(32);
    declare strDESCRIPTION        varchar(64);
    declare iNewInsertedHostDbId  int(16);

    declare record_not_found int DEFAULT 0;
    declare cur_dbhost CURSOR for select HOSTID, DBHOST, IPADDRESS, PORTNUM, ISLONGCONN, DBTYPE, OSTYPE, DBNAME, DBUSER, DBPWD, DESCRIPTION from dbhost where SYSTEMID=iOldSystemId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_dbhost;
    allRecords: LOOP
        fetch cur_dbhost into iHOSTID, strDBHOST, strIPADDRESS, iPORTNUM, iISLONGCONN, strDBTYPE, strOSTYPE, strDBNAME, strDBUSER, strDBPWD, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          insert into dbhost(SYSTEMID, DBHOST, IPADDRESS, PORTNUM, ISLONGCONN, DBTYPE, OSTYPE, DBNAME,  DBUSER, DBPWD, DESCRIPTION) values(iNewSystemId, strDBHOST, strIPADDRESS, iPORTNUM, iISLONGCONN, strDBTYPE, strOSTYPE, strDBNAME, strDBUSER, strDBPWD, strDESCRIPTION);
          set iNewInsertedHostDbId = last_insert_id();
          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 1, iHOSTID, iNewInsertedHostDbId);
        end;
    END LOOP allRecords;
    close cur_dbhost;

end;


/* 拷贝 scriptflow 表的内容 */
drop procedure if exists sp_copy_scriptflows;
create procedure sp_copy_scriptflows
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
   	declare sNAME varchar(64) default '';
   	declare sDESCRIPTION varchar(128) default '';
   	declare sSCRIPT mediumtext;
   
    declare iScriptFlowId  int(16);
    declare iNewInsertedScriptFlowId  int(16);

    declare record_not_found int DEFAULT 0;
    declare cur_scriptflow CURSOR for select ID, NAME, DESCRIPTION, SCRIPT from scriptflow where SYSTEMID=iOldSystemId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_scriptflow;
    allRecords: LOOP
        fetch cur_scriptflow into iScriptFlowId, sNAME, sDESCRIPTION, sSCRIPT;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          insert into scriptflow(SYSTEMID, NAME, DESCRIPTION, SCRIPT) values(iNewSystemId, sNAME, sDESCRIPTION, sSCRIPT);
          set iNewInsertedScriptFlowId = last_insert_id();
          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 6, iScriptFlowId, iNewInsertedScriptFlowId);
        end;
    END LOOP allRecords;
    close cur_scriptflow;

end;



/* 拷贝 transaction_catetory 表的内容 */
drop procedure if exists sp_copy_transaction_catetory;
create procedure sp_copy_transaction_catetory
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare iCATEGORYID           int(16);
    declare strCATEGORYNAME       varchar(64);
    declare strDESCRIPTION        varchar(128);
    declare iNewCATEGORYID        int(16);

    declare record_not_found int DEFAULT 0;
    declare cur_transaction_catetory CURSOR for select ID, CATEGORYNAME, DESCRIPTION from transaction_catetory where SYSTEMID=iOldSystemId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_transaction_catetory;
    allRecords: LOOP
        fetch cur_transaction_catetory into iCATEGORYID, strCATEGORYNAME, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          insert into transaction_catetory(SYSTEMID, CATEGORYNAME, DESCRIPTION) values(iNewSystemId, strCATEGORYNAME, strDESCRIPTION);
          set iNewCATEGORYID = last_insert_id();
          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 0, iCATEGORYID, iNewCATEGORYID);
        end;
    END LOOP allRecords;
    close cur_transaction_catetory;

end;


/* 拷贝 transaction 表的内容 */
drop procedure if exists sp_copy_transactions;
create procedure sp_copy_transactions
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare strTRANCODE varchar(32) binary;
    declare strTRANNAME varchar(64);
    declare iISMODE int(1) default 0 ;
    declare strDESCRIPTION varchar(128) default '';
    declare strCHANNEL varchar(32) default '';
    declare strSCRIPT mediumtext;
    declare strREQUSETSTRUCT mediumtext ;
    declare strRESPONSESTRUCT mediumtext;
    declare iTRANSACTIONCATEGORYID int(16);
    declare strCATEGORY varchar(16) default '';
    declare iFLAG int(1) default 0;
    declare iMAXDELAYTIME bigint(16) default 0;
    declare iMINDELAYTIME bigint(16) default 0;
    declare iSQLDELAYTIME int(4) default 0 ;
    declare iPARAMETERGETSEQUENCE varchar(1024) default '';

    declare iNewTRANSACTIONCATEGORYID int(16);
    declare iTransactionId int(16);
    declare iNewTransactionId int(16);


    declare record_not_found int DEFAULT 0;

    declare cur_transaction CURSOR for select TRANSACTIONID, TRANCODE, TRANNAME, ISMODE, DESCRIPTION, CHANNEL, SCRIPT, REQUSETSTRUCT, RESPONSESTRUCT, TRANSACTIONCATEGORYID, CATEGORY, FLAG, MAXDELAYTIME, MINDELAYTIME, SQLDELAYTIME, PARAMETERGETSEQUENCE from transaction where SYSTEMID=iOldSystemId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_transaction;
    allRecords: LOOP
        fetch cur_transaction into iTransactionId, strTRANCODE, strTRANNAME, iISMODE, strDESCRIPTION, strCHANNEL, strSCRIPT, strREQUSETSTRUCT, strRESPONSESTRUCT, iTRANSACTIONCATEGORYID, strCATEGORY, iFLAG, iMAXDELAYTIME, iMINDELAYTIME, iSQLDELAYTIME, iPARAMETERGETSEQUENCE;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          insert into transaction(TRANCODE, TRANNAME, SYSTEMID, ISMODE, DESCRIPTION, CHANNEL, SCRIPT, REQUSETSTRUCT, RESPONSESTRUCT, TRANSACTIONCATEGORYID, CATEGORY, FLAG, MAXDELAYTIME, MINDELAYTIME, SQLDELAYTIME, PARAMETERGETSEQUENCE) values(strTRANCODE, strTRANNAME, iNewSystemId, iISMODE, strDESCRIPTION, strCHANNEL, strSCRIPT, strREQUSETSTRUCT, strRESPONSESTRUCT, iTRANSACTIONCATEGORYID, strCATEGORY, iFLAG, iMAXDELAYTIME, iMINDELAYTIME, iSQLDELAYTIME, iPARAMETERGETSEQUENCE);
          set iNewTransactionId = last_insert_id();
          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 3, iTransactionId, iNewTransactionId);
          call sp_copy_transaction_parameters(iOldSystemId, iNewSystemId, iTransactionId, iNewTransactionId);
        end;
    END LOOP allRecords;
    close cur_transaction;

end;


/* 拷贝 第一层级的 parameter_directory */
drop procedure if exists sp_copy_parameter_tree;
create procedure sp_copy_parameter_tree
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare iDIRECTORYID          int(16);
    declare iSORTINDEX            int(8);
    declare strNAME               varchar(256);
    declare strPATH               varchar(512);
    declare strDESCRIPTION        varchar(256);
    declare iNewInsertedParamDirId int(16);

    /* 遍历所有的根目录 */
    declare record_not_found int DEFAULT 0;
    declare cur_param_dir CURSOR for select DIRECTORYID, SORTINDEX, NAME, PATH, DESCRIPTION from parameter_directory where SYSTEMID=iOldSystemId and PARENTDIRID=0;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_param_dir;
    allRecords: LOOP
        /* 逐个获取根目录 */
        fetch cur_param_dir into iDIRECTORYID, iSORTINDEX, strNAME, strPATH, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          /* 拷贝一个根目录 */
          insert into parameter_directory(SYSTEMID, PARENTDIRID, SORTINDEX, NAME, PATH, DESCRIPTION) values(iNewSystemId, 0, iSORTINDEX, strNAME, strPATH, strDESCRIPTION);
          set iNewInsertedParamDirId = last_insert_id();
          /* 处理一个根目录 */
          call sp_copy_one_parameter_directory(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedParamDirId);
          /* 暂时不存在一上来就是参数的情况，参数都一定是在目录下的 */
        end;
    END LOOP allRecords;
    close cur_param_dir;

end;


/* 处理 parameter_directory 的一个目录 */
drop procedure if exists sp_copy_one_parameter_directory;
create procedure sp_copy_one_parameter_directory
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldParentDirectoryId int(16),
  iNewParentDirectoryId int(16)
)
begin
    declare iDIRECTORYID          int(16);
    declare iSORTINDEX            int(8) ;
    declare strNAME               varchar(256);
    declare strPATH               varchar(512);
    declare strDESCRIPTION        varchar(256);
    declare iNewInsertedParamDirId int(16);

    /* 遍历当前目录的所有子目录 */
    declare record_not_found int DEFAULT 0;
    declare cur_param_dir1 CURSOR for select DIRECTORYID, SORTINDEX, NAME, PATH, DESCRIPTION from parameter_directory where SYSTEMID=iOldSystemId and PARENTDIRID=iOldParentDirectoryId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_param_dir1;
    allRecords: LOOP
        fetch cur_param_dir1 into iDIRECTORYID, iSORTINDEX, strNAME, strPATH, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          /* 拷贝一个子目录 */
          insert into parameter_directory(SYSTEMID, PARENTDIRID, SORTINDEX, NAME, PATH, DESCRIPTION) values(iNewSystemId, iNewParentDirectoryId, iSORTINDEX, strNAME, strPATH, strDESCRIPTION);
          set iNewInsertedParamDirId = last_insert_id();
          call sp_copy_one_parameter_directory(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedParamDirId);
        end;
    END LOOP allRecords;
    close cur_param_dir1;

    /* 当前目录下的直接参数（叶子节点） */
    call sp_copy_directory_system_parameters(iOldSystemId, iNewSystemId, iOldParentDirectoryId, iNewParentDirectoryId);

end;


/* 拷贝 parameter_directory的一个目录节点下的所有参数 */
drop procedure if exists sp_copy_directory_system_parameters;
create procedure sp_copy_directory_system_parameters
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldDirectoryId int(16),
  iNewDirectoryId int(16)
)
begin
    declare iOldSystemParameterId int(16);
    declare strPARAMETERNAME varchar(64);
    declare strPARAMETERDESC varchar(64);
    declare strPARAMETEREXPRESSION varchar(2048);
    declare strDEFAULTEXPECTEDVALUE varchar(128);
    declare iPARAMETERTYPE int(1);
    declare iPARAMFROMMSGSRC int(1);
    declare iCOMPARECONDITION int(1);
    declare iISVALID int(1);
    declare iPARAMETERHOSTTYPE int(1);
    declare iPARAMETERHOSTID int(16);
    declare iDISPLAYFLAG int(1);
    declare iREFETCHFLAG int(1);
    declare iREFETCHMETHOD int(1);
    declare iNewSysParamId int(16);

    declare record_not_found int DEFAULT 0;
    declare cur_system_param CURSOR for select ID, PARAMETERNAME, PARAMETERDESC, PARAMETEREXPRESSION, DEFAULTEXPECTEDVALUE, PARAMETERTYPE, PARAMFROMMSGSRC, COMPARECONDITION, ISVALID, PARAMETERHOSTTYPE, PARAMETERHOSTID, DISPLAYFLAG, REFETCHFLAG, REFETCHMETHOD from system_dynamic_parameter where SYSTEMID=iOldSystemId and DIRECTORYID=iOldDirectoryId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_system_param;
    allRecords: LOOP
        fetch cur_system_param into iOldSystemParameterId, strPARAMETERNAME, strPARAMETERDESC, strPARAMETEREXPRESSION, strDEFAULTEXPECTEDVALUE, iPARAMETERTYPE, iPARAMFROMMSGSRC, iCOMPARECONDITION, iISVALID, iPARAMETERHOSTTYPE, iPARAMETERHOSTID, iDISPLAYFLAG, iREFETCHFLAG, iREFETCHMETHOD;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          insert into system_dynamic_parameter(SYSTEMID, PARAMETERNAME, PARAMETERDESC, PARAMETEREXPRESSION, DEFAULTEXPECTEDVALUE, PARAMETERTYPE, PARAMFROMMSGSRC, COMPARECONDITION, ISVALID, PARAMETERHOSTTYPE, PARAMETERHOSTID, DISPLAYFLAG, REFETCHFLAG, REFETCHMETHOD, DIRECTORYID) values(iNewSystemId, strPARAMETERNAME, strPARAMETERDESC, strPARAMETEREXPRESSION, strDEFAULTEXPECTEDVALUE, iPARAMETERTYPE, iPARAMFROMMSGSRC, iCOMPARECONDITION, iISVALID, iPARAMETERHOSTTYPE, iPARAMETERHOSTID, iDISPLAYFLAG, iREFETCHFLAG, iREFETCHMETHOD, iNewDirectoryId);
          set iNewSysParamId = last_insert_id();
          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 2, iOldSystemParameterId, iNewSysParamId);
        end;
    END LOOP allRecords;
    close cur_system_param;

end;


/* 拷贝 transaction_dynamic_parameter 的内容 */
drop procedure if exists sp_copy_transaction_parameters;
create procedure sp_copy_transaction_parameters
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldTransactionId int(16),
  iNewTransactionId int(16)
)
begin
    declare iSystemParameterId int(16);
    declare iUserId int(16);
    declare dtMODIFYTIME datetime;

    declare iOldTransactionParameterId int(16);
    declare iNewSystemParameterId int(16);
    
    declare iNewInsertedTransParamId int(16);
    
    declare record_not_found int DEFAULT 0;

    declare cur_transaction_parameter CURSOR for select ID, SYSTEMPARAMETERID, USERID, MODIFYTIME from transaction_dynamic_parameter where TRANSACTIONID=iOldTransactionId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_transaction_parameter;
    allRecords: LOOP
        fetch cur_transaction_parameter into iOldTransactionParameterId, iSystemParameterId, iUserId, dtMODIFYTIME;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          select NEWID into iNewSystemParameterId from system_copy_id_map where OLDSYSTEMID=iOldSystemId and NEWSYSTEMID=iNewSystemId and type=2 and OLDID=iSystemParameterId;
          insert into transaction_dynamic_parameter(TRANSACTIONID, SYSTEMPARAMETERID, USERID, MODIFYTIME) values(iNewTransactionId, iNewSystemParameterId, iUserId, dtMODIFYTIME);
          set iNewInsertedTransParamId = last_insert_id();
          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 7, iOldTransactionParameterId, iNewInsertedTransParamId);
        end;
    END LOOP allRecords;
    close cur_transaction_parameter;

end;



/* 拷贝 第一层级的 case_directory */
drop procedure if exists sp_copy_caseflow_tree;
create procedure sp_copy_caseflow_tree
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare iDIRECTORYID          int(16);
    declare iSORTINDEX            int(8);
    declare strNAME               varchar(256);
    declare strPATH               varchar(512);
    declare strDESCRIPTION        varchar(256);
    declare iNewInsertedCaseDirId int(16);

    /* 遍历所有的根目录 */
    declare record_not_found int DEFAULT 0;
    declare cur_case_dir CURSOR for select DIRECTORYID, SORTINDEX, NAME, PATH, DESCRIPTION from case_directory where SYSTEMID=iOldSystemId and PARENTDIRID=0;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;
    
    open cur_case_dir;
    allRecords: LOOP
        /* 逐个获取根目录 */
        fetch cur_case_dir into iDIRECTORYID, iSORTINDEX, strNAME, strPATH, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          /* 拷贝一个根目录 */
          insert into case_directory(SYSTEMID, PARENTDIRID, SORTINDEX, NAME, PATH, DESCRIPTION) values(iNewSystemId, 0, iSORTINDEX, strNAME, strPATH, strDESCRIPTION);
          set iNewInsertedCaseDirId = last_insert_id();
          
          /* 根目录下本身就是案例的 */
          call sp_copy_caseflows(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedCaseDirId);
          
          /* 递归处理一个根目录 */
          call sp_copy_one_caseflow_directory(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedCaseDirId);
        end;
    END LOOP allRecords;
    close cur_case_dir;
    
end;


/* 处理 case_directory 的一个目录 */
drop procedure if exists sp_copy_one_caseflow_directory;
create procedure sp_copy_one_caseflow_directory
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldParentDirectoryId int(16),
  iNewParentDirectoryId int(16)
)
begin
    declare iDIRECTORYID          int(16);
    declare iSORTINDEX            int(8) ;
    declare strNAME               varchar(256);
    declare strPATH               varchar(512);
    declare strDESCRIPTION        varchar(256);
    declare iNewInsertedCaseDirId int(16);

    /* 遍历当前目录的所有子目录 */
    declare record_not_found int DEFAULT 0;
    declare cur_case_dir1 CURSOR for select DIRECTORYID, SORTINDEX, NAME, PATH, DESCRIPTION from case_directory where SYSTEMID=iOldSystemId and PARENTDIRID=iOldParentDirectoryId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;
  
    open cur_case_dir1;
    allRecords: LOOP
        fetch cur_case_dir1 into iDIRECTORYID, iSORTINDEX, strNAME, strPATH, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          /* 拷贝一个子目录 */
          insert into case_directory(SYSTEMID, PARENTDIRID, SORTINDEX, NAME, PATH, DESCRIPTION) values(iNewSystemId, iNewParentDirectoryId, iSORTINDEX, strNAME, strPATH, strDESCRIPTION);
          set iNewInsertedCaseDirId = last_insert_id();
          call sp_copy_one_caseflow_directory(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedCaseDirId);
        end;
    END LOOP allRecords;
    close cur_case_dir1;
     
    /* 当前目录下的直接参数（叶子节点） */
    call sp_copy_caseflows(iOldSystemId, iNewSystemId, iOldParentDirectoryId, iNewParentDirectoryId);
end;


/* 拷贝 caseflow 表的内容 */
drop procedure if exists sp_copy_caseflows;
create procedure sp_copy_caseflows
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldDirectoryId int(16),
  iNewDirectoryId int(16)
)
begin
    declare sCASEFLOWNAME varchar(64);
    declare sCASEFLOWNO varchar(64);
    declare sCASEFLOWPATH varchar(1024);
    declare sDESCRIPTION varchar(1024);
    declare sCASEFLOWSTEP varchar(1024);
    declare sPRECONDITIONS varchar(1024);
    declare sEXPECTEDRESULT varchar(1024);
    declare sCASETYPE varchar(32);
    declare sCASEPROPERTY varchar(32);
    declare sPRIORITY varchar(32);
    declare sDESIGNER varchar(32);
    declare sSTEPCOUNT int(16) default 0;
    declare sSYSTEMID int(16);
    declare sUSERID int(16);
    declare sCREATETIME datetime;
    declare sIMPORTBATCHNO varchar(64) default '';
    declare sPASSFLAG int(1) default 0;
    declare sDISABLEDFLAG int(1) default 0;
    declare sBREAKPOINTFLAG int(1) default 0;
    declare sDIRECTORYID int(16);
    declare sSCRIPTFLOWID int(16);
    declare iNewScriptFlowID int(16);

    declare iCaseFlowId int(16);
    declare iNewCaseFlowId int(16);
    declare iCount int(8);

    declare record_not_found int DEFAULT 0;

    declare cur_caseflow CURSOR for select ID, CASEFLOWNAME, CASEFLOWNO, CASEFLOWPATH, DESCRIPTION, CASEFLOWSTEP, PRECONDITIONS, EXPECTEDRESULT, CASETYPE, CASEPROPERTY, PRIORITY, DESIGNER, STEPCOUNT, CreatedUserId, CreatedTime, IMPORTBATCHNO, PASSFLAG, DISABLEDFLAG, BREAKPOINTFLAG, SCRIPTFLOWID from caseflow where SYSTEMID=iOldSystemId and DIRECTORYID=iOldDirectoryId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_caseflow;
    allRecords: LOOP
        fetch cur_caseflow into iCaseFlowId, sCASEFLOWNAME, sCASEFLOWNO, sCASEFLOWPATH, sDESCRIPTION, sCASEFLOWSTEP, sPRECONDITIONS, sEXPECTEDRESULT, sCASETYPE, sCASEPROPERTY, sPRIORITY, sDESIGNER, sSTEPCOUNT, sUSERID, sCREATETIME, sIMPORTBATCHNO, sPASSFLAG, sDISABLEDFLAG, sBREAKPOINTFLAG, sSCRIPTFLOWID;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin       	
        	select NEWID into iNewScriptFlowID from system_copy_id_map where OLDSYSTEMID=iOldSystemId and NEWSYSTEMID=iNewSystemId and type=6 and OLDID=sSCRIPTFLOWID;
        	select count(*) into iCount from caseflow where SYSTEMID=iNewSystemId and DirectoryId=iNewDirectoryId and CASEFLOWNAME=sCASEFLOWNAME and CASEFLOWNO=sCASEFLOWNO;
        	if iCount <= 0 then
	          insert into caseflow(SYSTEMID, DirectoryId, CASEFLOWNAME, CASEFLOWNO, CASEFLOWPATH, DESCRIPTION, CASEFLOWSTEP, PRECONDITIONS, EXPECTEDRESULT, CASETYPE, CASEPROPERTY, PRIORITY, DESIGNER, STEPCOUNT, CreatedUserId, CreatedTime, IMPORTBATCHNO, PASSFLAG, DISABLEDFLAG, BREAKPOINTFLAG, SCRIPTFLOWID) values(iNewSystemId, iNewDirectoryId, sCASEFLOWNAME, sCASEFLOWNO, sCASEFLOWPATH, sDESCRIPTION, sCASEFLOWSTEP, sPRECONDITIONS, sEXPECTEDRESULT, sCASETYPE, sCASEPROPERTY, sPRIORITY, sDESIGNER, sSTEPCOUNT, sUSERID, sCREATETIME, sIMPORTBATCHNO, sPASSFLAG, sDISABLEDFLAG, sBREAKPOINTFLAG, iNewScriptFlowID);
	          set iNewCaseFlowId = last_insert_id();
	          insert into system_copy_id_map(OldSystemID, NEWSYSTEMID, TYPE, OLDID, NEWID) values (iOldSystemId, iNewSystemId, 4, iCaseFlowId, iNewCaseFlowId);
	          call sp_copy_cases(iOldSystemId, iNewSystemId, iCaseFlowId, iNewCaseFlowId);
          end if;
        end;
    END LOOP allRecords;
    close cur_caseflow;

end;



/* 拷贝 cases 表的内容 */
drop procedure if exists sp_copy_cases;
create procedure sp_copy_cases
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldCaseFlowId int(16),
  iNewCaseFlowId int(16)
)
begin
    declare sCASENAME varchar(64);
		declare sCASENO varchar(64);
		declare sCASEPATH varchar(1024);
		declare sTRANSACTIONID int(16);
		declare sCASEFLOWID int(16);
		declare sSEQUENCE int(8) ;
		declare sBREAKPOINTFLAG int(1) default 0;
		declare sCARDID int(16);
		declare sEXPECTEDXML mediumtext;
		declare sREQUESTXML mediumtext;
		declare sRESPONSEXML mediumtext;
		declare sREQUESTMSG blob;
		declare sRESPONSEMSG blob;
		declare sIMPORTBATCHNO varchar(64) default '';
		declare sAMOUNT float(10,2);
		declare sISPARSEABLE int(1) default 0;
		declare sFLAG int(1) default 0;
		declare sISDEFAULT int(1) default 0;
		declare sDIRECTORYID int(16) ;
		declare sDESCRIPTION varchar(256) default '';
    
    declare iNewTransactionId int(16);
    
    declare iCaseId int(16);
    declare iNewInsertedCaseId int(16);
    
    declare record_not_found int DEFAULT 0;

    declare cur_cases CURSOR for select CASEID,CASENAME,CASENO,CASEPATH,TRANSACTIONID,SEQUENCE,BREAKPOINTFLAG,CARDID,EXPECTEDXML,REQUESTXML,RESPONSEXML,REQUESTMSG,RESPONSEMSG,IMPORTBATCHNO,AMOUNT,ISPARSEABLE,FLAG,ISDEFAULT,DESCRIPTION from cases where CaseFlowId=iOldCaseFlowId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_cases;
    allRecords: LOOP
        fetch cur_cases into iCaseId,sCASENAME,sCASENO,sCASEPATH,sTRANSACTIONID,sSEQUENCE,sBREAKPOINTFLAG,sCARDID,sEXPECTEDXML,sREQUESTXML,sRESPONSEXML,sREQUESTMSG,sRESPONSEMSG,sIMPORTBATCHNO,sAMOUNT,sISPARSEABLE,sFLAG,sISDEFAULT,sDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          select NEWID into iNewTransactionId from system_copy_id_map where OLDSYSTEMID=iOldSystemId and NEWSYSTEMID=iNewSystemId and type=3 and OLDID=sTRANSACTIONID;
          insert into cases(CASENAME,CASENO,CASEPATH,TRANSACTIONID,CASEFLOWID,SEQUENCE,BREAKPOINTFLAG,CARDID,EXPECTEDXML,REQUESTXML,RESPONSEXML,REQUESTMSG,RESPONSEMSG,IMPORTBATCHNO,AMOUNT,ISPARSEABLE,FLAG,ISDEFAULT,DESCRIPTION) values(sCASENAME,sCASENO,sCASEPATH,iNewTransactionId,iNewCaseFlowId,sSEQUENCE,sBREAKPOINTFLAG,sCARDID,sEXPECTEDXML,sREQUESTXML,sRESPONSEXML,sREQUESTMSG,sRESPONSEMSG,sIMPORTBATCHNO,sAMOUNT,sISPARSEABLE,sFLAG,sISDEFAULT,sDESCRIPTION);
          set iNewInsertedCaseId = last_insert_id();
        end;
    END LOOP allRecords;
    close cur_cases;

end;


/* 拷贝 case_parameter_expected_value 表的内容 */
drop procedure if exists sp_copy_case_parameter_expected_value;
create procedure sp_copy_case_parameter_expected_value
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldCaseId int(16),
  iNewCaseId int(16)
)
begin
    declare iTRANSACTIONPARAMETERID int(16);
    declare iEXPECTEDVALUETYPE int(1) default 0;
    declare sEXPECTEDVALUE varchar(1024);
    
    declare iNewTRANSACTIONPARAMETERID int(16);
    
    declare record_not_found int DEFAULT 0;

    declare cur_case_parameter_expected_value CURSOR for select TRANSACTIONPARAMETERID,EXPECTEDVALUETYPE,EXPECTEDVALUE from case_parameter_expected_value where CaseId=iOldCaseId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_case_parameter_expected_value;
    allRecords: LOOP
        fetch cur_case_parameter_expected_value into iTRANSACTIONPARAMETERID, iEXPECTEDVALUETYPE, sEXPECTEDVALUE;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          select NEWID into iNewTRANSACTIONPARAMETERID from system_copy_id_map where OLDSYSTEMID=iOldSystemId and NEWSYSTEMID=iNewSystemId and type=7 and OLDID=iTRANSACTIONPARAMETERID;
          insert into case_parameter_expected_value(CASEID,TRANSACTIONPARAMETERID,EXPECTEDVALUETYPE,EXPECTEDVALUE) values(iNewCaseId, iNewTRANSACTIONPARAMETERID, iEXPECTEDVALUETYPE, sEXPECTEDVALUE);
        end;
    END LOOP allRecords;
    close cur_case_parameter_expected_value;

end;



/* 拷贝 第一层级的 executeset_directory */
drop procedure if exists sp_copy_executeset_tree;
create procedure sp_copy_executeset_tree
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare iDIRECTORYID          int(16);
    declare iOBJTYPE              int(8);
    declare iEXECUTESETID         int(16);
    declare iSORTINDEX            int(8);
    declare strNAME               varchar(256);
    declare strPATH               varchar(512);
    declare strDESCRIPTION        varchar(256);
    
    declare iNewInsertedExecuteSetDirId int(16);
    declare iNewInsertedExecuteSetId int(16);

    /* 遍历所有的根目录 */
    declare record_not_found int DEFAULT 0;
    declare cur_executeset_dir CURSOR for select DIRECTORYID, SORTINDEX, OBJTYPE, EXECUTESETID, NAME, PATH, DESCRIPTION from executeset_directory where SYSTEMID=iOldSystemId and PARENTDIRID=0;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;
    
    open cur_executeset_dir;
    allRecords: LOOP
        /* 逐个获取根目录 */
        fetch cur_executeset_dir into iDIRECTORYID, iSORTINDEX, iOBJTYPE, iEXECUTESETID, strNAME, strPATH, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          /* 拷贝一个根目录 */
          insert into executeset_directory(SYSTEMID, PARENTDIRID, SORTINDEX, OBJTYPE, EXECUTESETID, NAME, PATH, DESCRIPTION) values(iNewSystemId, 0, iSORTINDEX, iOBJTYPE, iEXECUTESETID, strNAME, strPATH, strDESCRIPTION);
          set iNewInsertedExecuteSetDirId = last_insert_id();
          
          if iOBJTYPE = 1 then
		          /* 根目录下本身就是执行集的 */
		          select func_copy_executeset(iOldSystemId, iNewSystemId, iEXECUTESETID) into iNewInsertedExecuteSetId;
		          /* 回写 EXECUTESETID */
		          update executeset_directory set EXECUTESETID = iNewInsertedExecuteSetId where DIRECTORYID = iNewInsertedExecuteSetDirId;
		      else
		          /* 递归处理一个根目录 */
          		call sp_copy_one_executeset_directory(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedExecuteSetDirId);
          end if;
          

        end;
    END LOOP allRecords;
    close cur_executeset_dir;
    
end;



/* 处理 executeset_directory 的一个目录 */
drop procedure if exists sp_copy_one_executeset_directory;
create procedure sp_copy_one_executeset_directory
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldParentDirectoryId int(16),
  iNewParentDirectoryId int(16)
)
begin
    declare iDIRECTORYID          int(16);
    declare iOBJTYPE              int(8);
    declare iEXECUTESETID         int(16);
    declare iSORTINDEX            int(8);
    declare strNAME               varchar(256);
    declare strPATH               varchar(512);
    declare strDESCRIPTION        varchar(256);
      
    declare iNewInsertedExecuteSetDirId int(16);
    declare iNewInsertedExecuteSetId int(16);

    /* 遍历当前目录的所有子目录 */
    declare record_not_found int DEFAULT 0;
    declare cur_executeset_dir CURSOR for select DIRECTORYID, SORTINDEX, OBJTYPE, EXECUTESETID, NAME, PATH, DESCRIPTION from executeset_directory where SYSTEMID=iOldSystemId and PARENTDIRID=iOldParentDirectoryId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;
  
    open cur_executeset_dir;
    allRecords: LOOP
        fetch cur_executeset_dir into iDIRECTORYID, iSORTINDEX, iOBJTYPE, iEXECUTESETID, strNAME, strPATH, strDESCRIPTION;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          /* 拷贝一个子目录 */
          insert into executeset_directory(SYSTEMID, PARENTDIRID, SORTINDEX, OBJTYPE, EXECUTESETID, NAME, PATH, DESCRIPTION) values(iNewSystemId, iNewParentDirectoryId, iSORTINDEX, iOBJTYPE, iEXECUTESETID, strNAME, strPATH, strDESCRIPTION);
          set iNewInsertedExecuteSetDirId = last_insert_id();
          
          if iOBJTYPE = 1 then
		          /* 根目录下本身就是执行集的 */
		          select func_copy_executeset(iOldSystemId, iNewSystemId, iEXECUTESETID) into iNewInsertedExecuteSetId;
		          /* 回写 EXECUTESETID */
		          update executeset_directory set EXECUTESETID = iNewInsertedExecuteSetId where DIRECTORYID = iNewInsertedExecuteSetDirId;
		      else
		          /* 递归处理一个根目录 */
          		call sp_copy_one_executeset_directory(iOldSystemId, iNewSystemId, iDIRECTORYID, iNewInsertedExecuteSetDirId);
          end if;    

        end;
    END LOOP allRecords;
    close cur_executeset_dir;
     
end;



/* 拷贝 executeset 表的内容 */
drop function if exists func_copy_executeset;
create function func_copy_executeset
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldExecuteSetId int(16)
)
returns int
begin
    declare iNewInsertedExecuteSetId int(16);

    insert into executeset(SYSTEMID, NAME, IMPORTBATCHNO, DESCRIPTION) select iNewSystemId, NAME, IMPORTBATCHNO, DESCRIPTION from executeset where ID=iOldExecuteSetId and SYSTEMID=iOldSystemId;
    set iNewInsertedExecuteSetId = last_insert_id();
   
    call sp_copy_executeset_taskitems(iOldSystemId, iNewSystemId, iOldExecuteSetId, iNewInsertedExecuteSetId);
 
    return iNewInsertedExecuteSetId;
end;


/* 拷贝 executeset_taskitem 表的内容 */
drop procedure if exists sp_copy_executeset_taskitems;
create procedure sp_copy_executeset_taskitems
(
  iOldSystemId int(16),
  iNewSystemId int(16),
  iOldExecuteSetId int(16),
  iNewExecuteSetId int(16)
)
begin
		declare iTASKID int(16);
		declare sNAME VARCHAR(64);
		declare iTYPE int(1);
		declare iTransactionID int(16);
		declare iREPCOUNT int(8);
		
		declare iNewTransactionId int(16);
		declare iNewTASKID int(16);
		declare iCount int(8);

    declare record_not_found int DEFAULT 0;
    declare cur_executeset_taskitem CURSOR for select TASKID, NAME, TYPE, TRANSACTIONID, REPCOUNT from executeset_taskitem where EXECUTESETID=iOldExecuteSetId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_executeset_taskitem;
    allRecords: LOOP
        fetch cur_executeset_taskitem into iTASKID, sNAME, iTYPE, iTransactionID, iREPCOUNT;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
        	 select NEWID into iNewTASKID from system_copy_id_map where OLDSYSTEMID=iOldSystemId and NEWSYSTEMID=iNewSystemId and type=6 and OLDID=iTASKID;
        	 select count(*) into iCount from executeset_taskitem where EXECUTESETID=iNewExecuteSetId and TASKID=iNewTASKID and TYPE=iTYPE;
        	 if iCount <= 0 and iNewTASKID > 0 then
        	 insert into executeset_taskitem(EXECUTESETID, TASKID, TYPE, REPCOUNT) values (iNewExecuteSetId, iNewTASKID, iTYPE, iREPCOUNT);
           /*select NEWID into iNewTransactionId from system_copy_id_map where OLDSYSTEMID=iOldSystemId and NEWSYSTEMID=iNewSystemId and type=3 and OLDID=iTransactionID;
           insert into executeset_taskitem(EXECUTESETID, TASKID, NAME, TYPE, TRANSACTIONID, REPCOUNT) values (iNewExecuteSetId, iNewTASKID, sNAME, iTYPE, iNewTransactionId, iREPCOUNT);*/
           end if;
        end;
    END LOOP allRecords;
    close cur_executeset_taskitem;

end;


/* 拷贝 interfacedef 表的内容 */
drop procedure if exists sp_copy_interfaces;
create procedure sp_copy_interfaces
(
  iOldSystemId int(16),
  iNewSystemId int(16)
)
begin
    declare iInterfaceDefId        int(16);
    declare sINTERFACENAME          varchar(64);
    declare sCHINESENAME            varchar(64);
    declare sINTERFACELEN           int(4);
    declare sFIELDCOUNT             int(4);
    declare sIMPORTUSERID           int(16);
    declare sIMPORTTIME             datetime;
    declare sMEMO                   varchar(256);

    declare iNewInsertedInterfaceDefId  int(16);

    declare record_not_found int DEFAULT 0;
    declare cur_interfacedef CURSOR for select INTERFACEID,INTERFACENAME,CHINESENAME,INTERFACELEN,FIELDCOUNT,IMPORTUSERID,IMPORTTIME,MEMO from interfacedef where SYSTEMID=iOldSystemId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    open cur_interfacedef;
    allRecords: LOOP
        fetch cur_interfacedef into iInterfaceDefId,sINTERFACENAME,sCHINESENAME,sINTERFACELEN,sFIELDCOUNT,sIMPORTUSERID,sIMPORTTIME,sMEMO;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
          insert into interfacedef(SYSTEMID,INTERFACENAME,CHINESENAME,INTERFACELEN,FIELDCOUNT,IMPORTUSERID,IMPORTTIME,MEMO) values(iNewSystemId,sINTERFACENAME,sCHINESENAME,sINTERFACELEN,sFIELDCOUNT,sIMPORTUSERID,sIMPORTTIME,sMEMO);
          set iNewInsertedInterfaceDefId = last_insert_id();
          insert into interfacefield(INTERFACEID,SEQUENCE,FIELDNAME,CHINESENAME,FIELDTYPE,FIELDLEN,DECIMALDIGITS,FIELDTYPEEXPR,OPTIONAL,DEFAULTVALUE) select iNewInsertedInterfaceDefId,SEQUENCE,FIELDNAME,CHINESENAME,FIELDTYPE,FIELDLEN,DECIMALDIGITS,FIELDTYPEEXPR,OPTIONAL,DEFAULTVALUE from interfacefield where INTERFACEID=iInterfaceDefId;
        end;
    END LOOP allRecords;
    close cur_interfacedef;

end;












drop procedure if exists sp_copy_case_inst;
create procedure sp_copy_case_inst
(
  iSystemId int(16),
  iUserId int(16)
)
begin

    declare iTransactionID        int(16);
    declare strDBPWD              varchar(32);
    declare strDESCRIPTION        varchar(64);

    declare record_not_found int DEFAULT 0;
    declare cur_transactions CURSOR for select TRANSACTIONID from transaction where SYSTEMID=iSystemId;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

		delete from recorded_case where SYSTEMID=iSystemId;
		
    open cur_transactions;
    allRecords: LOOP
        fetch cur_transactions into iTransactionID;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        begin
        	call sp_copy_trans_default_case(iSystemId, iTransactionID, iUserId);
        end;
    END LOOP allRecords;
    close cur_transactions;

end;


drop procedure if exists sp_copy_trans_default_case;
create procedure sp_copy_trans_default_case
(
  iSystemId int(16),
  iTransactionID int(16),
  iUserId int(16)
)
begin
    declare iCaseId        int(16);
    declare strDBPWD       varchar(32);
    declare strDESCRIPTION varchar(64);

    declare iCaseInstId    int(16);
    declare strREQUESTXML  longtext;
    declare strRESPONSEXML longtext;
    declare strREQUESTMSG  longtext;
    declare strRESPONSEMSG longtext;
  
    declare record_not_found int DEFAULT 0;
    declare cur_trans_cases CURSOR for select CASEID from cases where TRANSACTIONID=iTransactionID;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

		delete from recorded_case where SYSTEMID=iSystemId;
		
    open cur_trans_cases;
    allRecords: LOOP
        fetch cur_trans_cases into iCaseId;

        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
                      
        SET iCaseInstId = 0;
                
        call sp_get_case_inst1(iSystemId, iTransactionID, iCaseId, iCaseInstId, strREQUESTXML, strRESPONSEXML, strREQUESTMSG, strRESPONSEMSG);
        
        IF iCaseInstId > 0 THEN
	        begin
	          insert into recorded_case(SYSTEMID, REQUESTMSG, RESPONSEMSG, RECORDUSERID, CASEID, ISCASED) values(iSystemId, strREQUESTMSG, strRESPONSEMSG, iUserId, iCaseId, 2);
	          update cases set ISDEFAULT=1, RESPONSEMSG=strRESPONSEMSG, RESPONSEXML=strRESPONSEXML where CASEID=iCaseId;
	        end;
	        LEAVE allRecords;
        END IF;
        
    END LOOP allRecords;
    close cur_trans_cases;

end;




drop procedure if exists sp_get_case_inst1;
create procedure sp_get_case_inst1
(
  in iSystemId int(16),
  in iTransactionID int(16),
  in iCaseId int(16),
  out iCaseInstId    int(16),
  out strREQUESTXML  longtext,
  out strRESPONSEXML longtext,
  out strREQUESTMSG  longtext,
  out strRESPONSEMSG longtext
)
begin
    declare iMaxCaseInstId    int(16);

    declare record_not_found int DEFAULT 0;
    declare cur_trans_case_insts CURSOR for select ID, REQUESTXML, RESPONSEXML, REQUESTMSG, RESPONSEMSG from case_instance where CASEID=iCaseId and CASEPASSFLAG=1;
    declare continue handler FOR NOT FOUND SET record_not_found = 1;

    SET iMaxCaseInstId = 0;

    open cur_trans_case_insts;
    allRecords: LOOP
        fetch cur_trans_case_insts into iCaseInstId, strREQUESTXML, strRESPONSEXML, strREQUESTMSG, strRESPONSEMSG;
        IF record_not_found THEN
           LEAVE allRecords;
        END IF;
        
        IF iCaseInstId > iMaxCaseInstId THEN
          SET iMaxCaseInstId = iCaseInstId;
        END IF;

    END LOOP allRecords;
    close cur_trans_case_insts;

end;
