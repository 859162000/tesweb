package com.dc.tes.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * 执行日志统计信息
 * @author xuat
 *
 */
public class GWTResultLogMsg extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4220728953032303952L;
	/**
	 * 执行日志ID
	 */
	public static String N_ExecuteLogID = "executeLogID";
	/**
	 * 执行案例数
	 */
	public static String N_CaseCount = "caseCount";
	/**
	 * 通过案例数
	 */
	public static String N_PassCaseCount = "passCaseCount";
	/**
	 * 失败案例数
	 */
	public static String N_FailedCaseCount = "failedCaseCount";
	/**
	 * 超时案例数
	 */
	public static String N_TimeOutCaseCount = "timeOutCaseCount";
	/**
	 * 其它案例数
	 */
	public static String N_OtherCaseCount = "otherCaseCount";
	/**
	 * 通过率
	 */
	public static String N_PassRate = "passRate";
	
	public GWTResultLogMsg(){}
	
	public GWTResultLogMsg(Integer executeLogID, int caseCount, int passCaseCount,
			int failedCaseCount, int timeOutCaseCount, int otherCaseCount,
			String passRate){
		this.set(N_ExecuteLogID, executeLogID);
		this.set(N_CaseCount, caseCount);
		this.set(N_PassCaseCount, passCaseCount);
		this.set(N_FailedCaseCount, failedCaseCount);
		this.set(N_TimeOutCaseCount, timeOutCaseCount);
		this.set(N_OtherCaseCount, otherCaseCount);
		this.set(N_PassRate, passRate);
	}
	
	public Integer GetExecuteLogID(){
		return this.get(N_ExecuteLogID);
	}
	
	public Integer GetCaseCount(){
		return Integer.parseInt(this.get(N_CaseCount).toString());
	}
	
	public Integer GetPassCaseCount(){
		return Integer.parseInt(this.get(N_PassCaseCount).toString());
	}
	
	public Integer GetFailedCaseCount(){
		return Integer.parseInt(this.get(N_FailedCaseCount).toString());
	}
	
	public Integer GetTimeOutCaseCount(){
		return Integer.parseInt(this.get(N_TimeOutCaseCount).toString());
	}
	
	public Integer GetOtherCaseCount(){
		return Integer.parseInt(this.get(N_OtherCaseCount).toString());
	}
	
	public String GetPassRate(){
		return this.get(N_PassRate);
	}
	
	public void SetCaseCount(Integer caseCount){
		this.set(N_CaseCount, caseCount);
	}
	
	public void SetPassCaseCount(Integer caseCount){
		this.set(N_PassCaseCount, caseCount);
	}
	
	public void SetFailedCaseCount(Integer caseCount){
		this.set(N_FailedCaseCount, caseCount);
	}
	
	public void SetTimeOutCaseCount(Integer caseCount){
		this.set(N_TimeOutCaseCount, caseCount);
	}
	
	public void SetOtherCaseCount(Integer caseCount){
		this.set(N_OtherCaseCount, caseCount);
	}
	
	public void SetPassRate(String passRate){
		this.set(N_PassRate, passRate);
	}
}
