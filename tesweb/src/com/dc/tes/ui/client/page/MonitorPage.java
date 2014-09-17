package com.dc.tes.ui.client.page;

import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IMonitorService;
import com.dc.tes.ui.client.IMonitorServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.LogInfoPopPanel;
import com.dc.tes.ui.client.model.GWTMoniLogDetail;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MonitorPage extends BasePage {

	private String data = "";
	private TextArea ta = null;
	private IMonitorServiceAsync moniService;
//	private boolean allowScroll = true;
//	private boolean allowNotify = true;
	
	private Boolean isSysNameVisible = true;
	private Boolean isTranTimeVisible = true;
	private Boolean isTranCodeVisible = true;
	private Boolean isCaseNameVisible = true;
	private Boolean isMsgInVisible = false;
	private Boolean isMsgOutVisible = false;
	private Boolean isDataInVisible = false;
	private Boolean isDataOutVisible = false;
	private int lastId = 0;
	
	public MonitorPage(){
		moniService = ServiceHelper.GetDynamicService("moniserver", IMonitorService.class);
		
		ta = new TextArea();
		ta.setReadOnly(true);
		ta.setHeight("100%");
		ta.setWidth("100%");
	}
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new FitLayout());
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAutoHeight(true);
		
	    Button item1 = new Button("监控数据视图");
	    item1.setIcon(MainPage.ICONS.MoniView());

	    Menu menu = new Menu();
	    
	    CheckMenuItem menuItem = new CheckMenuItem("系统名、交易码");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);

	    menu.add(menuItem);
	    
	    menuItem = new CheckMenuItem("发起/响应时间");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);

	    menu.add(menuItem);

	    menuItem = new CheckMenuItem("发起/响应案例名称");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);

	    menu.add(menuItem);
	    
	    menuItem = new CheckMenuItem("原始报文数据");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);

	    menu.add(menuItem);
	    
	    menuItem = new CheckMenuItem("拆包后报文数据");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);
 
	    menu.add(menuItem);
	    
	    menuItem = new CheckMenuItem("响应案例数据");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);

	    menu.add(menuItem);
	    
	    menuItem = new CheckMenuItem("组包后报文数据");
	    menuItem.setChecked(true);
	    menuItem.setEnabled(false);

	    menu.add(menuItem);
	    
	    item1.setMenu(menu);
	    toolBar.add(item1);
	    
	    Button btnClear = new Button("清空");
	    btnClear.setIcon(MainPage.ICONS.menuTreeLeaf());
	    btnClear.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(ta != null){
					data = "";
					ta.setValue("");
				}
			}});
	    toolBar.add(btnClear);
	    
	    toolBar.add(new FillToolItem());
	    //自动滚屏
	    final CheckBox cbAllowScroll = new CheckBox();
	    cbAllowScroll.setValue(AppContext.getAllowScroll());
	    cbAllowScroll.setAutoWidth(true);
	    cbAllowScroll.addListener(Events.Change, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				boolean allowScroll = cbAllowScroll.getValue();
				AppContext.setAllowScroll(allowScroll);
				if(allowScroll)
					ta.setCursorPos(ta.getCursorPos());
			}});
	    toolBar.add(cbAllowScroll);
	   
	    LabelField lf = new LabelField();
	    lf.setText("自动滚屏");
	    toolBar.add(lf);
	    
	    toolBar.add(new SeparatorToolItem());
	    //消息提示
	    final CheckBox cbAllowNotify = new CheckBox();
	    cbAllowNotify.setValue(AppContext.getAllowNotify());
	    cbAllowNotify.setAutoWidth(true);
	    cbAllowNotify.addListener(Events.Change, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				boolean allowNotify = cbAllowNotify.getValue();
				AppContext.setAllowNotify(allowNotify);
			}});
	    toolBar.add(cbAllowNotify);
	   
	    lf = new LabelField();
	    lf.setText("消息提示");
	    toolBar.add(lf);
		
	    ContentPanel cpMoniter = new ContentPanel();
	    cpMoniter.setHeaderVisible(false);
	    cpMoniter.setTopComponent(toolBar);
		cpMoniter.add(ta, new FitData());

		add(cpMoniter);
	}
	
	public void CollectLogInfo(){
		moniService.GetMoniLogDetail(GetSystemName(), lastId, new AsyncCallback<List<GWTMoniLogDetail>>(){

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<GWTMoniLogDetail> result) {

				if(result == null || result.size() == 0)return;
				GWTMoniLogDetail item0 = result.get(0);
//				if(item0.get(GWTMoniLogDetail.N_REGINFO) != null){
//					//LogInfoPopPanel.display(new InfoConfig("", "", null), item0);
//					return;
//				}
				if(item0.get(GWTMoniLogDetail.N_SYSNAME) == null){
					lastId = item0.<Integer>get(GWTMoniLogDetail.N_LASTID);
					return;
				}
				data = data + GetLogInfoStr(result);
				if(AppContext.getAllowNotify())
					for(int i = 0; i < result.size() && i < 2; i++){
						InfoConfig config = new InfoConfig("", "", null);
						config.display = 5000;
						LogInfoPopPanel.display(config, result.get(i));
					}
				if(data.length() >= 30000){
					data = data.substring(15000);
				}
				ta.setValue(data);
				if(AppContext.getAllowScroll())
					ta.setCursorPos(ta.getCursorPos());
			}
		});
	}

	private String GetLogInfoStr(List<GWTMoniLogDetail> result){
		StringBuffer logBuffer = new StringBuffer();
		
		//ArrayList<GWTMoniLogDetail> details = log.get(GWTMoniLog.N_LOGDETAIL);
		for(GWTMoniLogDetail item : result){
			lastId = item.<Integer>get(GWTMoniLogDetail.N_LASTID);
			
			logBuffer.append("\n\n");
			
			//接收方
			if(item.get(GWTMoniLogDetail.N_TRANSTATE).toString().equals("1")){
				if(isTranTimeVisible)
					logBuffer.append("模拟器交易响应时间： " + item.get(GWTMoniLogDetail.N_TRANTIME) + "\n");
				if(isSysNameVisible)
					logBuffer.append("系统名称： " + GetSystemName() + "\n");
				
				logBuffer.append("消息类型： 接收方\n");
				
				logBuffer.append("通道名称： "+ item.get(GWTMoniLogDetail.N_CHANNEL) + "\n");
				
				if(isTranCodeVisible)
					logBuffer.append("交易码： " + item.get(GWTMoniLogDetail.N_TRANCODE) + "\n");
				if(isCaseNameVisible)
					logBuffer.append("返回案例名称： " + item.get(GWTMoniLogDetail.N_CASENAME) + "\n");
				if(isMsgInVisible)
					logBuffer.append("原始接收报文： " + item.get(GWTMoniLogDetail.N_MSGIN) + "\n");
				if(isDataInVisible)
					logBuffer.append("拆包后接收报文： " + item.get(GWTMoniLogDetail.N_DATAIN) + "\n");
				if(isDataOutVisible)
					logBuffer.append("组包前发送报文： " + item.get(GWTMoniLogDetail.N_DATAOUT) + "\n");
				if(isMsgOutVisible)
					logBuffer.append("实际发送报文： " + item.get(GWTMoniLogDetail.N_MSGOUT) + "\n");
			}else{
				if(isTranTimeVisible)
					logBuffer.append("模拟器交易发起时间： " + item.get(GWTMoniLogDetail.N_TRANTIME) + "\n");
				if(isSysNameVisible)
					logBuffer.append("系统名称： " + GetSystemName() + "\n");
				
				logBuffer.append("消息类型： 发起方\n");
							
				logBuffer.append("实际接收报文： " + item.get(GWTMoniLogDetail.N_MSGIN) + "\n");
				logBuffer.append("拆包后接收报文： " + item.get(GWTMoniLogDetail.N_DATAIN) + "\n");
			}
			
			String errMsg = item.get(GWTMoniLogDetail.N_ERRMSG);
			
			if(errMsg != null && !errMsg.trim().equals(""))
				logBuffer.append("错误信息： " + errMsg + "\n");
		}
		return logBuffer.toString();
	}
}
