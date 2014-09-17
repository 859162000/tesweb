package com.dc.tes.ui.client.control;

import java.util.ArrayList;
import java.util.List;


import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.IScriptFlowServiceAsync;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;
import com.dc.tes.ui.client.model.GWTScriptFlowLogDetail;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.page.BasePage;
import com.dc.tes.ui.client.page.ResultDetailPage;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExecuteCaseFlow extends BasePage{
	GWTScriptFlow gwtScriptFlow;
	String batchNo;
	private String executeLogId = "";
	private String js = "";
	private GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
	private Grid<GWTScriptFlowLogDetail> logContainer;
	//private GridContentPanel<GWTCaseFlow> panel;
	private GWTCaseFlow caseFlow;
	private IScriptFlowServiceAsync scriptFlowService = ServiceHelper.GetDynamicService("scriptFlow", IScriptFlowService.class);
	private IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	
	
	public ExecuteCaseFlow(GWTCaseFlow gwtCaseFlow){
		this.caseFlow = gwtCaseFlow;
	//	this.batchNo = gwtCaseFlow.GetImportBatchNo();
	}
	public void ExecCaseFlow() {
		// TODO Auto-generated method stub
		
				CreateExecForm();
//				gwtScriptFlow = caseFlow.getScriptFlow();
//				js = "run_caseFlow(\"" + batchNo +
//					"\", \"" + caseFlow.GetCaseFlowNo() + "\");";
				Exec();
	}
	
	private void CreateExecForm() {
		// TODO Auto-generated method stub
		Window window = new Window();
		window.setSize(400, 400);
		window.setHeading("业务流执行日志");
		//window.setPlain(true);
		window.setModal(false);
		//window.setBlinkModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
//				GridCellRenderer<GWTScriptFlowLogDetail> typeRender = new GridCellRenderer<GWTScriptFlowLogDetail>() {
//					@Override
//					public Object render(final GWTScriptFlowLogDetail model, String property,
//							ColumnData config, int rowIndex, int colIndex,
//							ListStore<GWTScriptFlowLogDetail> store, Grid<GWTScriptFlowLogDetail> grid) {
//						return model.getTime();
//					}
//				};
//				ColumnConfig typeColumn = new ColumnConfig("test","类型",120);
//				typeColumn.setRenderer(typeRender);
//				columns.add(typeColumn);
		
		GridCellRenderer<GWTScriptFlowLogDetail> timeRender = new GridCellRenderer<GWTScriptFlowLogDetail>() {
			@Override
			public Object render(final GWTScriptFlowLogDetail model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTScriptFlowLogDetail> store, Grid<GWTScriptFlowLogDetail> grid) {
				return model.getTime();
			}
		};
		ColumnConfig timeColumn = new ColumnConfig("test","时间",100);
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
		
		ColumnConfig logColumn = new ColumnConfig("test","日志信息",400);
		logColumn.setRenderer(logRender);
		columns.add(logColumn);
		
		ColumnModel cm = new ColumnModel(columns);
		ListStore<GWTScriptFlowLogDetail> store = new ListStore<GWTScriptFlowLogDetail>();
		logContainer = new Grid<GWTScriptFlowLogDetail>(store,cm);
		
		logContainer.getView().setForceFit(true);
		logContainer.getView().setEmptyText("任务尚未执行，执行中本区域将记录执行的日志的信息");
		logContainer.setHeight(400);
		
		ContentPanel cpLog = new ContentPanel();
		cpLog.setHeading("执行日志");
		cpLog.add(logContainer);
		window.add(cpLog);
		window.show();
		
	}
	
	private void Exec()
	{
		
		scriptFlowService.GetExecIDAndDelayTime(caseFlow, GetUserID(),  new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				MessageBox.alert("错误", "新建日志失败", null);
			}

			@Override
			public void onSuccess(final String result) {
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
				
//				if(script.trim() == "")
//				{
//					logContainer.getStore().add(new GWTScriptFlowLogDetail(0,"脚本为空，无法执行",true));
//					return;
//				}
				
				final int scheduleTime = 1000;
				final Timer time = new Timer() {
					int i = 0;				
					
					@Override
					public void run() {
						final Timer self = this;
						i++;
						scriptFlowService.GetExecLog(executeLogId, caseFlow.GetID(), new AsyncCallback<ModelData>(){
							private void Refresh(GWTScriptFlowLog returnLog)
							{
								logInfo = returnLog;
								logContainer.getStore().add(logInfo.getLogDetail());
								ScrollGrid();
								
								if(!logInfo.isRunning())
								{
									self.cancel();
									MessageBox.confirm("提示", "是否立即查看执行结果？", new Listener<MessageBoxEvent>() {
										
										@Override
										public void handleEvent(MessageBoxEvent be) {
											// TODO Auto-generated method stub
											if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){						
												resultService.GetCaseFlowInstance(executeLogId, caseFlow.GetID(), new AsyncCallback<GWTResultDetailLog>() {

													@Override
													public void onFailure(
															Throwable caught) {
														// TODO Auto-generated method stub
														caught.printStackTrace();														
													}

													@Override
													public void onSuccess(
															GWTResultDetailLog result) {
														// TODO Auto-generated method stub
														if(result == null){
															MessageBox.alert("提示", "未找到本次执行结果，请检查核心是否启动", null);
														}else
															ResultDetailPage.openDetailPage(result);														
													}
												});
											}
										}
									});
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
				
				
				scriptFlowService.ExecCaseFlow(GetSysInfo(), caseFlow.GetID(), executeLogId, new AsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						logContainer.getStore().add(new GWTScriptFlowLogDetail(0,"业务流执行失败",false));
					}

					@Override
					public void onSuccess(Boolean result) {
						logInfo = new GWTScriptFlowLog();
						logContainer.getStore().add(new GWTScriptFlowLogDetail(0,"业务流已往核心发送，等待监控回馈信息",true));
						time.scheduleRepeating(scheduleTime);
					}
				});
				
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
	
}
