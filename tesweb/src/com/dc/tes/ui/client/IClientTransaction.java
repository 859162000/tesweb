package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IClientTransaction extends RemoteService {
	/**
	 * 获得交易信息列表
	 * @param sysId			系统标识
	 * @param isClient		是否是客户端
	 * @param searchKey		模糊查询字符
	 * @param config		分页配置信息
	 * @return				交易信息列表
	 */
	PagingLoadResult<GWTTransaction> GetList(String sysId, int isClient, String searchKey, PagingLoadConfig config);

	/**
	 * 保存交易信息（新增、修改）
	 * @param sysInfo	当前系统信息
	 * @param tran		交易信息
	 * @return			true:保存成功 false：保存失败
	 */
	Boolean SaveTran(GWTSimuSystem sysInfo,GWTTransaction tran, Integer loginLogId);

	/**
	 * 删除交易信息
	 * @param sysInfo	当前系统信息
	 * @param tranList	交易信息列表
	 */
	void DeleteTran(GWTSimuSystem sysInfo,List<GWTTransaction> tranList, Integer loginLogId);
	
	/**
	 * 获得交易脚本信息
	 * @param tranID	交易标识
	 * @return			交易脚本
	 */
	String GetScript(String tranID);
	
	/**
	 * 保存交易脚本信息
	 * @param tranID	交易标识
	 * @param Script	交易脚本
	 */
	void UpdateScript(String tranID,String Script);
	
	/**
	 * 获得交易报文结构
	 * @param tranId	交易标识
	 * @param isRes		是否是响应报文 true：响应报文 false：请求报文
	 * @return			交易报文结构
	 */
	GWTPack_Struct GetTreeRoot(String tranId, boolean isRes);
	
	/**
	 * 保存交易报文结构
	 * @param tranId	交易标识
	 * @param isRes		是否是响应报文 true：响应报文 false：请求报文
	 * @param root		交易报文结构
	 */
	void SaveTreeRoot(String tranId, boolean isRes, GWTPack_Struct root, Integer loginLogId);
}
