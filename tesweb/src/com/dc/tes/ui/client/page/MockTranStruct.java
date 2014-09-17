package com.dc.tes.ui.client.page;

import com.dc.tes.ui.client.model.GWTPack_Field;
import com.dc.tes.ui.client.model.GWTPack_Struct;

public class MockTranStruct {

	public static GWTPack_Struct getTreeModel() {

		GWTPack_Struct[] str = new GWTPack_Struct[] { new GWTPack_Struct(
				"交易报文结构:706100",
				new GWTPack_Struct[] {
						new GWTPack_Struct("head",
								new GWTPack_Field[] {
										new GWTPack_Field("transaction_sn",
												"请求方交易流水号", "15", "false",
												"String"),
										new GWTPack_Field("transaction_id",
												"EAIH交易码", "18", "false",
												"String"),
										new GWTPack_Field("requester_id",
												"请求方系统代码", "8", "false",
												"String"),
										new GWTPack_Field("branch_id",
												"原始请求方机构代号", "14", "false",
												"String"),
										new GWTPack_Field("channel_id",
												"交易渠道代号", "21", "false",
												"String"),
										new GWTPack_Field("transaction_date",
												"请求方交易日期（周期）", "10", "false",
												"String"),
										new GWTPack_Field("transaction_time",
												"请求方交易时间", "10", "false",
												"String"),
										new GWTPack_Field("version_id", "版本号",
												"13", "false", "String") }),
						new GWTPack_Struct("ext", new GWTPack_Field[] {
								new GWTPack_Field("OPM_MSG_TYPE", "交易类型", "21",
										"false", "String"),
								new GWTPack_Field("INM_SVR_TYPE", "服务类型", "16",
										"false", "String"),
								new GWTPack_Field("INM_PROCESS_CODE", "交易码",
										"9", "false", "String"),
								new GWTPack_Field("OPM_TX_STATUS", "交易处理状态",
										"9", "false", "String"),
								new GWTPack_Field("OPM_HOST_BUS_DT",
										"服务方交易营业日期", "7", "false", "String"),
								new GWTPack_Field("OPM_HOST_CPU_DT",
										"服务方交易处理日期", "6", "false", "String"),
								new GWTPack_Field("OPM_HOST_PROC_TIME",
										"服务方交易处理时间", "11", "false", "String"),
								new GWTPack_Field("OPM_TX_LOG_NO", "服务方交易流水编号",
										"15", "false", "String"),
								new GWTPack_Field("OPM_FRT_BRANCH_ID",
										"前置虚拟机构号", "32", "false", "String"),
								new GWTPack_Field("OPM_FRT_CYCLE", "前置系统交易周期",
										"21", "false", "String"),
								new GWTPack_Field("OPM_FRT_LOG_NO",
										"前置系统交易流水号", "4", "false", "String") }),
						new GWTPack_Struct("status", new GWTPack_Field[] {
								new GWTPack_Field("status", "交易状态", "7",
										"false", "String"),
								new GWTPack_Field("code", "错误码", "9", "false",
										"String"),
								new GWTPack_Field("desc", "错误描述", "8", "false",
										"String") }),
						new GWTPack_Struct("body", new GWTPack_Field[] {
								new GWTPack_Field("RespCode", "响应代码", "10",
										"false", "String"),
								new GWTPack_Field("RespMsg", "响应信息", "22",
										"false", "String"),
								new GWTPack_Field("SecAcct", "保证金账号", "2",
										"false", "String"),
								new GWTPack_Field("SavAcctId", "个人银行账号", "11",
										"false", "String"),
								new GWTPack_Field("InvName", "投资人名称", "6",
										"false", "String") }) }) };

		GWTPack_Struct root = new GWTPack_Struct("root");
		for (int i = 0; i < str.length; i++) {
			root.add((GWTPack_Struct) str[i]);
		}

		return root;
	}
}
