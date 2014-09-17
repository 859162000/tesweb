package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.ITestRoundService;
import com.dc.tes.ui.client.ITestRoundServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.PostFormPanel;

import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTTestRound;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CaseFlowResultPage extends BasePage {

	IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	GWTResultLog resultLog = null;

	GridContentPanel<GWTResultDetailLog> panel;
	FormContentPanel<GWTResultDetailLog> detailPanel;
	ConfigToolBar configBar;
	private String caseFlowID = "";
	private TabPanel tabPanel;
	PagingLoadConfig loadConfig;
	
	public CaseFlowResultPage(String caseFlowId, TabPanel panel) {
		caseFlowID = caseFlowId;
		tabPanel = panel;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new GridContentPanel<GWTResultDetailLog>();
		loadConfig = new BasePagingLoadConfig();
		loadConfig.setLimit(10);
		RpcProxy<PagingLoadResult<GWTResultDetailLog>> proxy = new RpcProxy<PagingLoadResult<GWTResultDetailLog>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTResultDetailLog>> callback) {
				resultService.GetResultListByCaseFlow(caseFlowID, panel
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
				loadConfig.set("flag", cb.getValue().getPos());
				panel.getLoader().load(loadConfig);
			}
			
		});
		panel.getSearchBar().add(cb);
		final ComboBox<GWTTestRound> cbTestRound = new ComboBox<GWTTestRound>();
		cbTestRound.setHideLabel(true);
		cbTestRound.setWidth(100);
		cbTestRound.setEditable(false);
		cbTestRound.setValueField(GWTTestRound.N_RoundID);
		cbTestRound.setDisplayField(GWTTestRound.N_RoundName);
		final ListStore<GWTTestRound> roundStore = new ListStore<GWTTestRound>();
		GWTTestRound gwtTestRound = new GWTTestRound(-1, GetSystemID());
		gwtTestRound.set(GWTTestRound.N_RoundName, "所有轮次");
		roundStore.add(gwtTestRound);
		ITestRoundServiceAsync testRoundService = ServiceHelper.GetDynamicService("testRound", ITestRoundService.class);	
		testRoundService.GetTestRounds(GetSystemID(), new AsyncCallback<List<GWTTestRound>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示",
						"获取轮次列表失败！", null);
			}

			@Override
			public void onSuccess(List<GWTTestRound> result) {
				// TODO Auto-generated method stub
				for(GWTTestRound tr : result){
					if(tr.GetCurrentRoundFlag() == 1){
						GWTTestRound t = new GWTTestRound(tr.GetRoundID(), GetSystemID());
						t.set(GWTTestRound.N_RoundName, "当前轮次");
						roundStore.add(t);
						break;
					}
				}
				roundStore.add(result);
			}
		});
		cbTestRound.setStore(roundStore);
		cbTestRound.setValue(roundStore.getAt(0));
		cbTestRound.setTriggerAction(TriggerAction.ALL);
		cbTestRound.addSelectionChangedListener(new SelectionChangedListener<GWTTestRound>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<GWTTestRound> se) {
				// TODO Auto-generated method stub
				loadConfig.set("roundId", cbTestRound.getValue().GetRoundID());
				panel.getLoader().load(loadConfig);
			}
		});
		panel.getSearchBar().add(cbTestRound);
		panel.DrowGridView("", true, true);
		panel.getDataGrid().addListener(Events.Attach, new Listener<GridEvent<GWTResultLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultLog> be) {
				// TODO Auto-generated method stub
				cb.setValue(ls.getAt(0));
				cbTestRound.setValue(roundStore.getAt(0));
			}		
		});
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddButton("btnResultExport", new Button("执行结果导出"),
				MainPage.ICONS.WebDown(), CaseResultExportHandler());
		
		configBar.AddWidget(new FillToolItem());
		
		configBar.AddDelBtn("btnDel", DelHandler());
		// 字体颜色，以及背景色
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTResultDetailLog>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
		
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTResultDetailLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultDetailLog> be) {
				// TODO Auto-generated method stub
				if(be.getModel().GetCaseFlowInstanceID()!=null && !be.getModel().GetCaseFlowInstanceID().isEmpty()){
					String tabId = "caseInstance" + be.getModel().GetCaseFlowInstanceID();
					String tabTitle ="[" + be.getModel().GetCaseFlowName() + "]执行结果";
					TabItem tabItem = new TabItem(tabTitle);
					tabItem.setId(tabId);
					tabItem.setClosable(true);
					tabItem.setLayout(new FitLayout());
					tabItem.setBorders(false);
					tabItem.setScrollMode(Scroll.AUTO);
					BasePage page = new ResultDetailPage2(be.getModel().GetCaseFlowInstanceID());
					tabItem.add(page);
					tabPanel.add(tabItem);
					tabPanel.repaint();
					tabPanel.setSelection(tabItem);
				}
			}
		});
	}
	


	

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				// TODO Auto-generated method stub
				List<GWTResultDetailLog> lst = panel.getDataGrid().getSelectionModel().getSelectedItems();
				resultService.DeleteCaseFlowInstance(lst, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
						MessageBox.alert("错误提示", "删除失败！", null);
					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
						panel.reloadGrid();
					}
				});
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
	
	private ColumnConfig GetRenderColumn(final String iconType,
			final boolean fireExec, String title, int width) {
		GridCellRenderer<GWTResultDetailLog> gridRender = new GridCellRenderer<GWTResultDetailLog>() {
			@Override
			public Object render(final GWTResultDetailLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultDetailLog> store, Grid<GWTResultDetailLog> grid) {
				String iconID = "icon_" + iconType + model.getID();

				boolean isSet = true;// model.getIsSet();
				String iconName = iconType + (isSet ? "" : "_No");
				IconButton b = new IconButton(iconName);
				if (fireExec && !isSet) {
					b.setEnabled(false);
					b.setStyleAttribute("cursor", "default");
				}

				HtmlContainer html = new HtmlContainer(
						"<span style = 'margin:0px;padding:0px;' id = '"
								+ iconID + "' ></span>");
				html.add(b, "#" + iconID);
				b.addSelectionListener(new SelectionListener<IconButtonEvent>() {
					@Override
					public void componentSelected(IconButtonEvent ce) {
						
						String tabId = "caseInstance" + model.GetCaseFlowInstanceID();
						String tabTitle ="[" + model.GetCaseFlowName() + "]执行结果";
						TabItem tabItem = new TabItem(tabTitle);
						tabItem.setId(tabId);
						tabItem.setClosable(true);
						tabItem.setLayout(new FitLayout());
						tabItem.setBorders(false);
						tabItem.setScrollMode(Scroll.AUTO);
						BasePage page = new ResultDetailPage2(model.GetCaseFlowInstanceID());
						tabItem.add(page);
						tabPanel.add(tabItem);
						tabPanel.repaint();
						tabPanel.setSelection(tabItem);
					}
				});
				return html;
			}
		};
		ColumnConfig column = new ColumnConfig("", title, width);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setSortable(false);
		column.setResizable(false);
		column.setRenderer(gridRender);

		return column;
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
		        //double val = (Double) model.get(property); 
		    	 if (model.get(property) != null)
		    	 {
		    		 if (model.get(property).toString()=="")
		    			 return "<span style='color:" + "green" + "'>" + "未执行" + "</span>";
		    			 //return "";
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
		ColumnConfig RECEIVEDREPLAYFLAG_ColumnConfig;
		
		columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseFlowNo, "测试用例编号", 120));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseFlowName, "测试用例名称", 150));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_BeginRunTime, "执行时间", 120));
		columns.add(new ColumnConfig(GWTResultDetailLog.N_RoundName, "执行轮次", 100));
		RECEIVEDREPLAYFLAG_ColumnConfig=new ColumnConfig(GWTResultDetailLog.N_CaseFlowPassFlag, "状态", 60);

		
		//columns.add(new ColumnConfig(GWTResultDetailLog.N_CARDNUMBER, "卡号", 110));
	//	columns.add(new ColumnConfig(GWTResultDetailLog.N_AMOUNT, "交易金额", 100));
		
		RECEIVEDREPLAYFLAG_ColumnConfig.setRenderer(RECEIVEDREPLAYFLAGchange);
		columns.add(RECEIVEDREPLAYFLAG_ColumnConfig); // 成功失败
		
		columns.add(GetRenderColumn("taskDetail", false, "详情", 40));
		

		return columns;
	}

	
	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	private Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTResultDetailLog.N_CaseFlowNo, "测试用例编号");
		detailMap.put(GWTResultDetailLog.N_CaseFlowName, "测试用例名称");
		detailMap.put(GWTResultDetailLog.N_BeginRunTime, "开始执行时间");
		detailMap.put(GWTResultDetailLog.N_EndRuntime, "结束执行时间");
		detailMap.put(GWTResultDetailLog.N_RoundName, "执行轮次");
		return detailMap;
	}
	
	
}
