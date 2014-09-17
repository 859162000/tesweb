package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;


import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.CascadeContentPanel;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.ResultContentPanel;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTStock;
import com.extjs.gxt.ui.client.store.ListStore; 
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.Grid;  
import com.extjs.gxt.ui.client.widget.grid.ColumnData; 
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ResultDetailPage2 extends BasePage {
	IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	ICaseServiceAsync caseService = ServiceHelper.GetDynamicService("case", ICaseService.class);
	GWTResultLog resultLog = null;

	CascadeContentPanel<GWTResultDetailLog> panel;
	ResultContentPanel<GWTResultDetailLog> detailPanel;
	ConfigToolBar configBar;
	private String caseFlowInstanceId="";
	public ResultDetailPage2(String caseFlowInstanceid)
	{
		caseFlowInstanceId = caseFlowInstanceid;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new CascadeContentPanel<GWTResultDetailLog>();
		
		RpcProxy<PagingLoadResult<GWTResultDetailLog>> proxy = new RpcProxy<PagingLoadResult<GWTResultDetailLog>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTResultDetailLog>> callback) {
				resultService.GetDetailList2(caseFlowInstanceId, panel
						.GetSearchCondition(), (PagingLoadConfig) loadConfig,
						callback);
			}
		};
		
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		final ComboBox<GWTStock> cb = new ComboBox<GWTStock>();
		cb.setEditable(false);
		cb.setDisplayField(GWTStock.N_Name);
		cb.setValueField(GWTStock.N_Pos);
		final ListStore<GWTStock> ls = new ListStore<GWTStock>();
		ls.add(new GWTStock("所有状态", "-1"));
		ls.add(new GWTStock("通过", "1"));
		ls.add(new GWTStock("失败", "0"));
		ls.add(new GWTStock("正在执行中", "2"));
		ls.add(new GWTStock("未执行", "3"));
		ls.add(new GWTStock("中断", "4"));
		ls.add(new GWTStock("超时", "5"));
		cb.setStore(ls);
		cb.setValue(ls.getAt(0));
		cb.setTriggerAction(TriggerAction.ALL);
		cb.addListener(Events.SelectionChange, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				PagingLoadConfig loadConfig = new BasePagingLoadConfig();
				loadConfig.set("flag", cb.getValue().getPos());
				loadConfig.setLimit(10);
				panel.getLoader().load(loadConfig);
			}
			
		});
		panel.getSearchBar().add(cb);
		panel.DrowGridView("", true, true);
		panel.getDataGrid().addListener(Events.Attach, new Listener<GridEvent<GWTResultLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultLog> be) {
				// TODO Auto-generated method stub
				cb.setValue(ls.getAt(0));
			}		
		});
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddButton("btnResultExport", new Button("执行结果导出"),
				MainPage.ICONS.WebDown(), CaseResultExportHandler());
		configBar.AddButton("btnCasePackage", new Button("查看报文(请求/响应报文)"),
				MainPage.ICONS.ViewPackge(), CasePackageHandler());
		configBar.AddButton("btnCompareResult", new Button("查看结果比对"),
				MainPage.ICONS.CompareResult(), CompareResultHandler());
		configBar.AddButton("btnCaseFlowInfo", new Button("查看用例信息"), ICONS.menuCase(), 
				ShowCaseFlowInfoHandler());
		configBar.AddWidget(new FillToolItem());

		// 字体颜色，以及背景色
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		detailPanel = new ResultContentPanel<GWTResultDetailLog>();
		panel.setCascadePanel(detailPanel);
		add(detailPanel);
		addDoubleClickListener(panel);
	}
	
	

	private void addDoubleClickListener(
			final CascadeContentPanel<GWTResultDetailLog> panel2) {
		// TODO Auto-generated method stub
		panel2.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTResultDetailLog>>() {

			@Override
			public void handleEvent(final GridEvent<GWTResultDetailLog> be) {
				// TODO Auto-generated method stub
				if(be.getModel().GetBreakPointFlag().equals("1")){
					MessageBox.confirm("提示", "是否继续执行中断案例?",new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent de) {
							Button msgBtn = de.getButtonClicked();
							if (msgBtn.getText().equalsIgnoreCase("Yes")) {
								caseService.GetResultCompare4BP(GetSysInfo(), be.getModel().getTransactionId(), 
										be.getModel().getID(), be.getModel().GetExecuteLogID(), new AsyncCallback<GWTCompareResult>() {

											@Override
											public void onFailure(
													Throwable caught) {
												// TODO Auto-generated method stub
												MessageBox.alert("错误提示", "执行中断案例失败，请联系管理员", null);
											}

											@Override
											public void onSuccess(
													GWTCompareResult result) {
												// TODO Auto-generated method stub
												panel2.reloadGrid();
												
											}

								});
							}
						}
					});
					
					}}
		});
		
	}

	
	
	private SelectionListener<ButtonEvent> CasePackageHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				GWTResultDetailLog gwtResultDetailLog =  panel.getDataGrid()
				                   .getSelectionModel().getSelectedItems().get(0);
				new ResultDisplayWindow().DrawResultWindow(gwtResultDetailLog);
//				
//				Window win = new Window();
//				win.setHeading("请求/响应报文");
//				BoxComponent request = new BoxComponent();
//				BoxComponent respone = new BoxComponent();
//				request.setBorders(true);
//				respone.setBorders(true);
//				
//				if(gwtResultDetailLog.getREQUESCONTENT().startsWith("<?xml")) {
//					request = new XMLEdit();
//					respone = new XMLEdit();
//					((XMLEdit)request).setValue(gwtResultDetailLog.getREQUESCONTENT());
//					((XMLEdit)respone).setValue(gwtResultDetailLog.getRESPONCONTENT()); 
//
//				} else {
//					request = new TextArea();
//					respone = new TextArea();
//					((TextArea)request).setValue(gwtResultDetailLog.getREQUESCONTENT());
//					((TextArea)respone).setValue(gwtResultDetailLog.getRESPONCONTENT());
//					((TextArea)request).setReadOnly(true);
//					((TextArea)respone).setReadOnly(true);
//				}
//				
//				BorderLayout layout = new BorderLayout();  
//				win.setLayout(layout);			
//				win.add(request,new BorderLayoutData(LayoutRegion.WEST, 450));  
//				win.add(respone,new BorderLayoutData(LayoutRegion.CENTER, 450)); 
//				win.setPlain(true);
//				win.setModal(true);
//				win.setDraggable(false);
//				win.setResizable(false);
//				win.setBlinkModal(false);
//				win.setSize(900, 540);
//				win.show();
			}
			
		};
	}
	
	private SelectionListener<ButtonEvent> CaseResultExportHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				GWTResultDetailLog gwtResultDetailLog =  panel.getDataGrid()
				.getSelectionModel().getSelectedItems()
				.get(0);
				final PostFormPanel formPanel = new PostFormPanel();
				mask("正在获取案例执行结果,请稍后...");
				formPanel
						.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);
				formPanel.setAction("CaseDetailExport?"
						+ "id="
						+ gwtResultDetailLog.GetCaseFlowInstanceID());
				formPanel.submit();
				formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

					@Override
					public void handleEvent(FormEvent be) {
						// TODO Auto-generated method stub
						unmask();
						TESWindows.ShowDownLoad(be.getResultHtml());
					}
					
				});
			}
		};
	}

	private ColumnConfig GetRenderColumn(String id, String name,
			int width) {
		GridCellRenderer<GWTResultDetailLog> gridRender = new GridCellRenderer<GWTResultDetailLog>(){
			@Override
			public Object render(GWTResultDetailLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultDetailLog> store, Grid<GWTResultDetailLog> grid) {
				// TODO Auto-generated method stub
				if(model.GetBreakPointFlag().equals("0")){
					return "<span style='color:" + "black" + "'>" + model.GetBreakPointFlagStr() + "</span>";
				}else{
					return "<span style='color:" + "red" + "'>" + model.GetBreakPointFlagStr() + "</span>";
				}
			}		
		};
		ColumnConfig columnConfig = new ColumnConfig(id, name, width);
		columnConfig.setRenderer(gridRender);
		return columnConfig;
	}
	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		
		GridCellRenderer<GWTResultDetailLog> RECEIVEDREPLAYFLAGchange = new GridCellRenderer<GWTResultDetailLog>() {   
		      public String render(GWTResultDetailLog model, String property, ColumnData config, int rowIndex, int colIndex,   
		          ListStore<GWTResultDetailLog> store, Grid<GWTResultDetailLog> grid) {   
		    	 if (model.get(property) != null)
		    	 {
		    		 if (model.get(property).toString()=="")
		    			 return "<span style='color:" + "green" + "'>" + "未执行" + "</span>";
		    		 int val = Integer.parseInt(model.get(property).toString());
		    		 String style =  "green";   
		    		 String realVal ="未执行";
		    		 switch(val)
		    		 {
		    		 	case 0:
		    			 	style = "red";
		    		 		realVal = "失败";
		    			 break;
		    		 	case 1:
		    			 	style = "green";
		    		 		realVal = "通过";
		    			 break;
		    		 	case 2:
		    			 	style = "blue";
		    		 		realVal = "正在执行中";
		    			 break;
		    		 	case 3:
		    			 	style = "blue";
		    		 		realVal = "未执行";
		    			 break;
		    		 	case 4:
		    		 		style = "red";
		    		 		realVal = "中断";
		    		 	break;
		    		 	case 5:
		    		 		style = "red";
		    		 		realVal = "超时";
		    		 	break;
		    		 	case 7:
		    		 		style = "red";
		    		 		realVal = "异常终断";
		    		 	break;
		    		 	default:
		    		 		style = "green";
		    		 		realVal = "未执行";
		    		 		break;
		    		 }
		    		 
		    		 return "<span style='color:" + style + "'>" + realVal + "</span>";   
		    	 }
		    	 else
		    	 {
		    		 return "<span style='color:" + "green" + "'>" + "未执行" + "</span>";  
		    	 }
		      }   
		    }; 
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		if(IsAdmin())
			columns.add(new ColumnConfig(GWTResultDetailLog.N_ID, "caseInstanceID", 60));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseNo, "步骤编号",100));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseName, "步骤名称",120));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_TranName, "交易类型",120));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_Sequence, "次序", 40));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseFlowNo, "测试用例编号", 100));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseFlowName, "测试用例名称", 150));
		//columns.add(new ColumnConfig(GWTResultDetailLog.N_CARDNUMBER, "卡号", 110));
		//columns.add(new ColumnConfig(GWTResultDetailLog.N_AMOUNT, "交易金额", 100));

		ColumnConfig RECEIVEDREPLAYFLAG_ColumnConfig=new ColumnConfig(GWTResultDetailLog.N_CasePassFlag, "状态", 60);	
		RECEIVEDREPLAYFLAG_ColumnConfig.setRenderer(RECEIVEDREPLAYFLAGchange);
		columns.add(RECEIVEDREPLAYFLAG_ColumnConfig); // 成功失败
		columns.add(GetRenderColumn(GWTResultDetailLog.N_BreakPointFlagStr, "断点", 40));
		return columns;
	}

	private SelectionListener<ButtonEvent> ShowCaseFlowInfoHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				CaseFlowInfoWindow caseFlowInfoWindow = new CaseFlowInfoWindow(new Window(), panel.getSelection().get(0).getGwtCaseFlow(), null);
				caseFlowInfoWindow.ShowCaseFlowInfoWindow();
			}
		};
	}

	/**
	 * 结果比对处理函数
	 * @return
	 */
	private SelectionListener<ButtonEvent> CompareResultHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				resultService.GetCompareResult(panel.getSelection().get(0), new AsyncCallback<GWTPack_Struct>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(GWTPack_Struct result) {
						// TODO Auto-generated method stub
						if(result != null)
							new ResultCompare().showCompareWindow(result);
						else{
							MessageBox.alert("提示", "无结果比对内容", null);
						}
					}
				});
			}
		};
	}
}