package com.dc.tes.ui.client.control.Result;

import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.page.CasePage;
import com.dc.tes.ui.client.page.IUserLoader;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ReusltFactory {
	
	private ICaseServiceAsync caseService = ServiceHelper.GetDynamicService(CasePage.SERVERNAME, ICaseService.class);
	private boolean isInit = false;
	IUserLoader loader;
	String loadMsg = "";
	String errorMsg = "";
	String header = "";
	String xmlContent = "";

	String caseID = null;
	int isClientSimu = 1;
	String charSet = "gb2312";
	GWTPack_Struct root = null;
	GWTPackNeed needInfo = null;
	IResultWin resultWin = null;

	public void setIsClientSimu(int isClient) {
		isClientSimu = isClient;
	}
	public void setCharSet(String charSet){
		this.charSet = charSet;
	}
	public ReusltFactory(String caseID) {
		
		loadMsg = "获得案例数据中...";
		errorMsg = "数据获取失败";
		header = "案例数据";
		this.caseID = caseID;
	}

	public ReusltFactory(String caseID,GWTPack_Struct root,GWTPackNeed needInfo, IUserLoader loader) {
		loadMsg = "案例数据组包中...";
		errorMsg = "组包出现异常，请与管理员联系";
		header = "组包结果";
		this.caseID = caseID;
		this.root = root;
		this.needInfo = needInfo;
		this.loader = loader;
	}

	public void Show() {
		//errorMsg不为空，说明组包发生异常，不显示组包预览对话框，给出失败提示
		//modify by shenfx
//		if (!errorMsg.equals("")){
//			MessageBox.alert("提示", errorMsg, null);
//			return;
//		}
		if (resultWin == null) {
			resultWin = new LoadResultWin(loadMsg);
			resultWin.setIsClientSimu(isClientSimu);
			resultWin.SetCharSet(charSet);
			GetData(ShowWinCallBack());
		}
	}

	private void GetData(AsyncCallback<String> callBack) {
		if(root == null)
			caseService.GetRespData(caseID, isClientSimu, resultWin.GetCharSet(), callBack);
		else
			caseService.GetRespData(needInfo,caseID, isClientSimu, root, resultWin.GetCharSet(), callBack);
	}

	private SelectionChangedListener<SimpleComboValue<String>> GetComboListener() {
		return new SelectionChangedListener<SimpleComboValue<String>>() {
			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				if(isInit)
				{
					isInit = true;
					return;
				}
				GetData(SetValueCallBack());
			}
		};
	}

	private AsyncCallback<String> SetValueCallBack() {
		return new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("错误提示", errorMsg, null);
			}

			@Override
			public void onSuccess(String result) {
				resultWin.SetValue(result);
			}
		};
	}

	private AsyncCallback<String> ShowWinCallBack() {
		return new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("错误提示", errorMsg, null);
				resultWin.Close();
			}

			@Override
			public void onSuccess(String result) {
				resultWin.Close();
				if (result.trim().startsWith("<?xml version=")) {
					//resultWin = new XMLResultWin(header,result);
					resultWin = new ResultWin(result,caseID,needInfo,loader);
					resultWin.setIsClientSimu(isClientSimu);
				}
				else
				{
					resultWin = new HexResultWin(header,result);
					resultWin.setIsClientSimu(isClientSimu);
					resultWin.SetComboListner(GetComboListener());
				}
				
			}
		};
	}
}
