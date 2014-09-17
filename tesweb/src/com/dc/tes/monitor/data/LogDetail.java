package com.dc.tes.monitor.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DataException;

public class LogDetail {
	
	private int TRANSTATE;	//客户端\服务段标志
	private Date TRANTIME;//执行时间
	private String TRANCODE;//交易码
	private String TRANNAME;//交易名称
	private String CASENAME;//案例名称
	private int    COMPARE; //比对结果
	private String channelname;//适配器名称
	private int    script;  //是否有比对结果
	private byte[] MSGIN ;  //输入报文
	private byte[] MSGOUT;  //输出报文
	private String DATAIN;  //输入报文结构
	private String DATAOUT; //输出报文结构
	private String ERRMSG;  //出错信息
	
	/**
	 * 根据系统名称，从数据库中取交易码对应的交易名称及是否有脚本
	 * @param sysname
	 */
	public void puttranname(String sysname){
		script = 0;
		Op[] op = new Op[1];
		IDAL<SysType> sysDao = DALFactory.GetBeanDAL(SysType.class);
		op[0] = Op.EQ("systemName", sysname); 
		String sysid="";
		try {
			SysType systype = sysDao.Get(op);
			sysid = systype.getSystemId();
		}
		catch(Exception e){
			throw new DataException("从数据库查询系统出现异常"+sysname,e);
		}
		
		Op[] opt = new Op[3];
		IDAL<Transaction> trans = DALFactory.GetBeanDAL(Transaction.class);
		opt[0] = Op.EQ(Transaction.N_SystemID, sysid); 
		opt[1] = Op.EQ(Transaction.N_TranCode, TRANCODE);
		opt[2] = Op.EQ(Transaction.N_IsClientSimu, TRANSTATE);
		try {
			Transaction tran = trans.Get(opt);
			TRANNAME = tran.getTranName();
			if(tran.getScript()!=null ){
				if(tran.getScript().length()>0)
					script = 1;
			}
		}
		catch(Exception e){
			throw new DataException("从数据库查询交易信息出现异常"+TRANCODE,e);
		}
		
	}
	public String getChannelname() {
		return channelname;
	}
	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
	public void setTRANTIME(Date trantime) {
		TRANTIME = trantime;
	}
	public void setCOMPARE(int compare) {
		COMPARE = compare;
	}
	public String getTRANNAME() {
		return TRANNAME;
	}
	public void setTRANNAME(String tranname) {
		TRANNAME = tranname;
	}
	public int getCOMPARE() {
		return COMPARE;
	}
	public void setCOMPARE(String compare) {
		COMPARE = Integer.parseInt(compare);
	}
	public int getScript() {
		return script;
	}
	public void setScript(int script) {
		this.script = script;
	}
	public int getTRANSTATE() {
		return TRANSTATE;
	}
	public void setTRANSTATE(int transtate) {
		TRANSTATE = transtate;
	}
	public Date getTRANTIME() {
		return TRANTIME;
	}
	private Date replaceDate(String dates){
		Date data=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			data = sdf.parse(dates);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
        }
        return data;
	}
	public void setTRANTIME(String trantime) {
		TRANTIME = this.replaceDate(trantime);
	}
	public String getTRANCODE() {
		return TRANCODE;
	}
	public void setTRANCODE(String trancode) {
		TRANCODE = trancode;
	}
	public String getCASENAME() {
		return CASENAME;
	}
	public void setCASENAME(String casename) {
		CASENAME = casename;
	}
	public byte[] getMSGIN() {
		return MSGIN;
	}
	public void setMSGIN(byte[] msgin) {
		MSGIN = msgin;
	}
	public byte[] getMSGOUT() {
		return MSGOUT;
	}
	public void setMSGOUT(byte[] msgout) {
		MSGOUT = msgout;
	}
	public String getDATAIN() {
		return DATAIN;
	}
	public void setDATAIN(String datain) {
		DATAIN = datain;
	}
	public String getDATAOUT() {
		return DATAOUT;
	}
	public void setDATAOUT(String dataout) {
		DATAOUT = dataout;
	}
	public String getERRMSG() {
		return ERRMSG;
	}
	public void setERRMSG(String errmsg) {
		ERRMSG = errmsg;
	}
	
}
