package com.dc.tes.ui.client.control.Result;

import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.XMLEdit;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.ui.client.page.CasePage;
import com.dc.tes.ui.client.page.IUserLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ResultWin implements IResultWin {
	
	private Window resultWin;
	private XMLEdit xmlEdit;
	private TextArea txtArea;
	private ICaseServiceAsync caseService = ServiceHelper.GetDynamicService(CasePage.SERVERNAME, ICaseService.class);
	private boolean change = false;
	private String charSet = "gb2312";
	public int isClientSimu = 1; //debug_hzx
	

	public ResultWin(String result, final String caseID, final GWTPackNeed needInfo,final IUserLoader loader) {
		TabPanel tabPanel = new TabPanel();
		tabPanel.setBorders(false);
		tabPanel.setTabPosition(TabPosition.BOTTOM);
		final TabItem privewModel = new TabItem("预览模式");
// 		if (result.trim().startsWith(
// 		"<?xml version=")) {
 			xmlEdit = new XMLEdit();
 			xmlEdit.setValue(result);
 			privewModel.setLayout(new FitLayout());
 			privewModel.add(xmlEdit);
// 		}
		
 		ContentPanel cp = new ContentPanel();
 		
 		txtArea = new TextArea();
 		caseService.GetXmlContent(caseID, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
						}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				txtArea.setValue(result);
			}
 			
 		});
 		
 		cp.setHeaderVisible(false);
 		cp.setCollapsible(false);
 		cp.setLayout(new FitLayout());
 		cp.setBorders(false);
 		cp.add(txtArea);
 		cp.addButton(new Button("保存",new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				caseService.SaveXmlContent(txtArea.getValue(), caseID, isClientSimu, needInfo, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						MessageBox.alert("错误", "保存失败", null);
					}

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						if(result.startsWith("error")) {
							MessageBox.alert("尝试组包失败", "格式有误，请重新确认", null);
						} else {
							MessageBox.alert("成功", "保存成功", null);
							if(xmlEdit != null) {
								privewModel.remove(xmlEdit);
					 			xmlEdit = new XMLEdit();
					 			xmlEdit.setValue(result);
					 			privewModel.setLayout(new FitLayout());
					 			privewModel.add(xmlEdit);
					 			change = true;
							}						
						}
					}
					
				});
			}
 			
 		}));
 		
 		cp.addButton(new Button("取消",new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				resultWin.close();
			}

 		}));
 		
		TabItem editModel = new TabItem("编辑模式");
		editModel.setLayout(new FitLayout());
		editModel.add(cp);
		
		tabPanel.add(privewModel);
		tabPanel.add(editModel);
				
		resultWin = new Window();
		resultWin.addListener(Events.Hide, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(change) {
					loader.load();
					change = false;
				}
			}
			
		});
		
		resultWin.setSize(600, 540);
		resultWin.setLayout(new FitLayout());
		resultWin.add(tabPanel);
		resultWin.show();
	}
	
	@Override
	public void setIsClientSimu(int isClient) {
		this.isClientSimu = isClient;
	}
	
	@Override
	public void SetValue(String value) {
		// TODO Auto-generated method stub
		if(txtArea != null) {
			//txtArea.setValue(value);
		}
	}

	@Override
	public String GetCharSet() {
		// TODO Auto-generated method stub
		return charSet;
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		resultWin.show();
	}

	@Override
	public void Close() {
		// TODO Auto-generated method stub
		resultWin.close();
	}

	@Override
	public void SetComboListner(
			SelectionChangedListener<SimpleComboValue<String>> listner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SetCharSet(String charSet) {
		// TODO Auto-generated method stub
		this.charSet = charSet;
	}

}
