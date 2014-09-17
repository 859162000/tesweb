package com.dc.tes.ui.client.page;

import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CaseFlowInfoWindow extends AbstractUseCaseForm{
	private Window window = new Window();
	private GWTCaseFlow EditCaseFlow;
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCaseFlow> panel;
	TabPanel tabPanel;
	
	public CaseFlowInfoWindow(Window _window, GWTCaseFlow gwtCaseFlow, GridContentPanel<GWTCaseFlow> _panel){
		this.window = _window;
		this.EditCaseFlow = gwtCaseFlow;
		this.panel = _panel;
	}
	public void ShowCaseFlowInfoWindow() {
		// TODO Auto-generated method stub
		window = new Window();
		window.setHeading(EditCaseFlow.GetCaseFlowNo()+"用例详情");
		window.setWidth(850);
		window.setModal(true);
		window.setPlain(true);
		window.setLayout(new FitLayout());
		window.setHeight(670);
		window.setMaximizable(true);
		tabPanel = new TabPanel();
		tabPanel.setBorders(false);
		tabPanel.setBodyBorder(false);
		tabPanel.setTabPosition(TabPosition.TOP);
		
		TabItem tabItem1 = BaseUseCaseInfo(tabPanel, EditCaseFlow);
		tabPanel.add(tabItem1);
		TabItem tabItem = new TabItem("用例步骤");
		tabItem.setId("0");
		tabItem.setClosable(false);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		tabItem.setScrollMode(Scroll.AUTO);

		BasePage page = new FlowCasesPage4YQ(tabPanel, EditCaseFlow);
		tabItem.add(page);
		tabPanel.add(tabItem);
		tabPanel.setSelection(tabItem1);
		
		window.add(tabPanel);
		window.show();
	}
	@Override
	public void EditUseCaseSuccHandler(GWTCaseFlow editResult) {
		// TODO Auto-generated method stub
		MessageBox.alert("提示", "保存成功", null);
		if(panel != null)
			panel.getDataGrid().getStore().getLoader().load();

	}
	@Override
	public void DeleteUseCaseSuccHandler(Boolean deleteResult) {
		// TODO Auto-generated method stub
		if(panel != null)
			panel.getDataGrid().getStore().getLoader().load();

	}
	
	public void CreateEditForm(){
		CreateEditForm(EditCaseFlow);
	}
}
