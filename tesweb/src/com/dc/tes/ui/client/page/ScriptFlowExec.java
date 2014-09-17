package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.IScriptFlowServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.JSEdit;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;
import com.dc.tes.ui.client.model.GWTScriptFlowLogDetail;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 业务流脚本定义、执行页面
 * @author scckobe
 *
 */
public class ScriptFlowExec extends BasePage_Register {
	private IScriptFlowServiceAsync scriptFlowService = ServiceHelper.GetDynamicService("scriptFlow", IScriptFlowService.class);
	private boolean fireExec = false;
	private GWTScriptFlow scriptFlow = null;
	private GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
	private String executeLogId = "";
	
	private JSEdit jsEdit = new JSEdit();
	
	private Grid<GWTScriptFlowLogDetail> logContainer;
	
	public ScriptFlowExec(GWTScriptFlow scriptFlow)
	{
		this(scriptFlow,false);
	}
	
	public ScriptFlowExec(GWTScriptFlow scriptFlow,boolean fire)
	{
		this.scriptFlow = scriptFlow;
		this.fireExec = fire;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		this.setLayout(new FlowLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.setHeight(25);
		// 保存按钮
		Button btnSave = new Button("保存", ICONS.Save(),
				SaveHandler());
		btnSave.setEnabled(false);
		btnList.add(btnSave);
		toolBar.add(btnSave);
		
		//执行按钮
		Button btnExec = new Button("执行", MainPage.ICONS.Exec(),
				ExecHandler());
		btnExec.setEnabled(false);
		btnList.add(btnExec);
		toolBar.add(btnExec);
		
		add(toolBar);
		ContentPanel cp = new ContentPanel();
		
		int itemHeight = Math.abs(AppContext.getTabPanel().getSelectedItem().getHeight() -50);
		int jsHeight = (int)(itemHeight * 0.6);
		int logHeight =  itemHeight - jsHeight;
		jsEdit.setIFrameHeight(jsHeight);
		jsEdit.setValue(scriptFlow.getScript());
		cp.add(jsEdit);
		
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setHeight(jsHeight);

		add(cp);
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
//		GridCellRenderer<GWTScriptFlowLogDetail> typeRender = new GridCellRenderer<GWTScriptFlowLogDetail>() {
//			@Override
//			public Object render(final GWTScriptFlowLogDetail model, String property,
//					ColumnData config, int rowIndex, int colIndex,
//					ListStore<GWTScriptFlowLogDetail> store, Grid<GWTScriptFlowLogDetail> grid) {
//				return model.getTime();
//			}
//		};
//		ColumnConfig typeColumn = new ColumnConfig("test","类型",120);
//		typeColumn.setRenderer(typeRender);
//		columns.add(typeColumn);
		
		GridCellRenderer<GWTScriptFlowLogDetail> timeRender = new GridCellRenderer<GWTScriptFlowLogDetail>() {
			@Override
			public Object render(final GWTScriptFlowLogDetail model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTScriptFlowLogDetail> store, Grid<GWTScriptFlowLogDetail> grid) {
				return model.getTime();
			}
		};
		ColumnConfig timeColumn = new ColumnConfig("test","时间",120);
		timeColumn.setRenderer(timeRender);
		columns.add(timeColumn);
		
		GridCellRenderer<GWTScriptFlowLogDetail> logRender = new GridCellRenderer<GWTScriptFlowLogDetail>() {
			@Override
			public Object render(final GWTScriptFlowLogDetail model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTScriptFlowLogDetail> store, Grid<GWTScriptFlowLogDetail> grid) {
				return "<span style = \"color= " + (model.getIsSucess() ? "black" : "red") + "\" >" 
					+ model.getLogInfo() + "</span>" ;
			}
		};
		
		ColumnConfig logColumn = new ColumnConfig("test","日志信息",500);
		logColumn.setRenderer(logRender);
		columns.add(logColumn);
		
		ColumnModel cm = new ColumnModel(columns);
		ListStore<GWTScriptFlowLogDetail> store = new ListStore<GWTScriptFlowLogDetail>();
		logContainer = new Grid<GWTScriptFlowLogDetail>(store,cm);
		
//		logContainer.setHideHeaders(true);
		logContainer.getView().setForceFit(true);
		logContainer.getView().setEmptyText("脚本尚未执行，执行中本区域将记录执行的日志的信息");
		logContainer.setHeight(logHeight);
		
		ContentPanel cpLog = new ContentPanel();
		cpLog.setHeading("执行日志");
		cpLog.add(logContainer);
		add(cpLog);
		
		if(fireExec && !logInfo.isRunning())
		{
			Exec(scriptFlow.getScript());
		}
		else
		{
			PageControlEnabled(true);
		}
	}
	
	@Override
	protected boolean NeedRegister()
	{
		return logInfo.isRunning();
	}
	
	private String getLogTagID()
	{
		return GetUserID() + "" + scriptFlow.getID();
	}
	
	@Override
	public void Exec()
	{

		Exec(jsEdit.getValue());
	}
	
	private void Exec(final String script)
	{
		
		scriptFlowService.Insert2DataBase(scriptFlow, GetUserID(),  new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				
				String[] splits = result.split(";");
				executeLogId = splits[0];
				
				final int delayTime = Integer.parseInt(splits[1]) + 2000;
				
				if(logInfo.isRunning())
				{
					return;
				}
				
				logInfo = new GWTScriptFlowLog();
				logContainer.getStore().removeAll();
				
				if(script.trim() == "")
				{
					logContainer.getStore().add(new GWTScriptFlowLogDetail(0,"脚本为空，无法执行",true));
					return;
				}
				
				PageControlEnabled(false);
				final int scheduleTime = 1000;
				final Timer time = new Timer() {
					int i = 0;
					
					@Override
					public void run() {
						final Timer self = this;
						i++;
						scriptFlowService.GetExecLog(getLogTagID(), scriptFlow.getID(), new AsyncCallback<ModelData>(){
							private void Refresh(GWTScriptFlowLog returnLog)
							{
								logInfo = returnLog;
								logContainer.getStore().add(logInfo.getLogDetail());
								ScrollGrid();
								
								if(!logInfo.isRunning())
								{
									PageControlEnabled(true);
									self.cancel();
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								Refresh(GWTScriptFlowLog.CreateExecptionLog("执行日志获取失败"));
								Refresh(GWTScriptFlowLog.CreateExecptionLog(caught.getMessage()));
							}

							@Override
							public void onSuccess(ModelData result) {
								if(result != null && result instanceof GWTScriptFlowLog)
									Refresh((GWTScriptFlowLog)result);
								else if( i * scheduleTime == delayTime)
									Refresh(GWTScriptFlowLog.CreateDualLog());
								
							}});
						
							
					}
				};
				
				scriptFlowService.ExecScript(GetSysInfo(),getLogTagID(), scriptFlow.getID(), jsEdit.getValue(), executeLogId, new AsyncCallback<Void>(){
					@Override
					public void onFailure(Throwable caught) {
						logContainer.getStore().add(new GWTScriptFlowLogDetail(0,"脚本执行失败",false));
						PageControlEnabled(true);
					}

					@Override
					public void onSuccess(Void result) {
						logInfo = new GWTScriptFlowLog();
						logContainer.getStore().add(new GWTScriptFlowLogDetail(0,"业务流脚本已往核心发送，等待监控回馈信息",true));
						time.scheduleRepeating(scheduleTime);
					}});
				
			}
			
		});
		

	}
	
	private void ScrollGrid()
	{
		logContainer.getView().getScrollState();
		GWTScriptFlowLogDetail model = new GWTScriptFlowLogDetail(0,"",false);
		logContainer.getStore().add(model);
		logContainer.getStore().remove(model);
	}
	
	private void PageControlEnabled(boolean enabled)
	{
		for(int i = 0; i < btnList.size(); i++)
			btnList.get(i).setEnabled(enabled);
	}
	
	/**
	 * 脚本执行控制函数
	 * 
	 * @return 脚本执行控制函数
	 */
	private SelectionListener<ButtonEvent> ExecHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Exec(jsEdit.getValue());
			}
		};
	}
	
	/**
	 * 脚本保存控制函数
	 * 
	 * @return 脚本保存控制函数
	 */
	private SelectionListener<ButtonEvent> SaveHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				scriptFlowService.UpdateScript(scriptFlow.getID(), jsEdit.getValue(), new AsyncCallback<Void>()
						{
							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("错误提示", "保存失败", null);
							}

							@Override
							public void onSuccess(Void result) {
								MessageBox.info("", "保存成功", null);
							}
						});
			}
		};
	}
}
