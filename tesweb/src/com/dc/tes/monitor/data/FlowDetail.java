package com.dc.tes.monitor.data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dc.tes.ui.client.model.GWTScriptFlowLog;

/**
 * 每条日志的详细情况
 */
public class FlowDetail {
	//所属业务流ID
	private String  FLOWID ;
	//本次执行的日志ID
	private String LOGID ;
	private int SCRIPTROW ;
	private String LOGCONTANT;
	private int  STATE;
	private boolean ISERROR ;
	private Date time;
	public String getFLOWID() {
		return FLOWID;
	}
	public void setFLOWID(String flowid) {
		FLOWID = flowid;
	}
	public String getLOGID() {
		return LOGID;
	}
	public void setLOGID(String signid) {
		LOGID = signid;
	}
	public int getSCRIPTROW() {
		return SCRIPTROW;
	}
	public void setSCRIPTROW(String scriptrow) {
		SCRIPTROW = Integer.parseInt(scriptrow);
	}
	public String getLOGCONTANT() {
		return LOGCONTANT;
	}
	public void setLOGCONTANT(String logcontant) {
		LOGCONTANT = logcontant;
	}
	public int getSTATE() {
		return STATE;
	}
	public void setSTATE(String state) {
		STATE = Integer.parseInt(state);
	}
	public boolean getISERROR() {
		return ISERROR;
	}
	public void setISERROR(String iserror) {
		ISERROR = iserror.equals("true");//0表示成功
	}
	public Date getTime() {
		return time;
	}
	public void setTime(String t) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			time = sdf.parse(t);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
        }
	}
	
}
