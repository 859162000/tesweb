package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.CascadeContentPanel;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.PostFormPanel;

import com.dc.tes.ui.client.control.ResultContentPanel;


import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTStock;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore; 
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.Grid;  
import com.extjs.gxt.ui.client.widget.grid.ColumnData; 

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
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;



import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ResultDetailPage extends BasePage{
	IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	GWTResultLog resultLog = null;

	CascadeContentPanel<GWTResultDetailLog> panel;
	ResultContentPanel<GWTResultDetailLog> detailPanel;
	ConfigToolBar configBar;
	private boolean isCase;
	private Integer passFlag = -1;
	private String executelogid = "";
	
	/**
	 * 执行日志的案例执行结果详情
	 * @param executelogId  执行日志ID
	 * @param isCase       是否为单个步骤
	 * @param passFlag     执行结果状态
	 */
	public ResultDetailPage(String executelogId, boolean isCase, Integer passFlag) {
		this.executelogid = executelogId;
		this.isCase = isCase;
		this.passFlag = passFlag;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new CascadeContentPanel<GWTResultDetailLog>();
		
		RpcProxy<PagingLoadResult<GWTResultDetailLog>> proxy = new RpcProxy<PagingLoadResult<GWTResultDetailLog>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTResultDetailLog>> callback) {
				resultService.GetDetailList(executelogid, panel
						.GetSearchCondition(), (PagingLoadConfig) loadConfig, passFlag,
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
		if(passFlag == 10 || passFlag == -1){
			cb.setValue(ls.getAt(0));
		}else{
			cb.setValue(ls.findModel(GWTStock.N_Pos, passFlag.toString()));
		}
		cb.setTriggerAction(TriggerAction.ALL);
		cb.addListener(Events.SelectionChange, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				//PagingLoadConfig loadConfig = new BasePagingLoadConfig();
				//loadConfig.set("flag", cb.getValue().getPos());
				//loadConfig.setLimit(10);
				passFlag = Integer.parseInt(cb.getValue().getPos());
				panel.getLoader().load();
			}
			
		});
		panel.getSearchBar().add(cb);
		panel.DrowGridView("", true, true);
		panel.getDataGrid().addListener(Events.Attach, new Listener<GridEvent<GWTResultLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultLog> be) {
				// TODO Auto-generated method stub
				if(passFlag == 10 || passFlag == -1){
					cb.setValue(ls.getAt(0));
				}else{
					cb.setValue(ls.findModel(GWTStock.N_Pos, passFlag.toString()));
				}
			}		
		});
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddButton("btnResultExport", new Button("执行结果导出"),
				MainPage.ICONS.WebDown(), CaseResultExportHandler());
		if(isCase){
			configBar.AddButton("btnCasePackage", new Button("查看报文(请求/响应报文)"),
					MainPage.ICONS.ViewPackge(), CasePackageHandler());
			configBar.AddButton("btnCompareResult", new Button("查看结果比对"),
					MainPage.ICONS.CompareResult(), CompareResultHandler());
		}
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
		
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTResultDetailLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultDetailLog> be) {
				// TODO Auto-generated method stub
				if(be.getModel().GetCaseFlowInstanceID()!=null && !be.getModel().GetCaseFlowInstanceID().isEmpty()){
					openDetailPage(be.getModel());
				}
			}
		});
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

	private SelectionListener<ButtonEvent> CasePackageHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				GWTResultDetailLog gwtResultDetailLog =  panel.getDataGrid()
                .getSelectionModel().getSelectedItems().get(0);
				new ResultDisplayWindow().DrawResultWindow(gwtResultDetailLog);
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
						openDetailPage(model);
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
		if(IsAdmin())
			columns.add(new ColumnConfig(GWTResultDetailLog.N_ID, "caseInstanceID", 60));
		if(isCase){
			columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseNo, "编号",200));
			columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseName, "步骤名称",200));
			RECEIVEDREPLAYFLAG_ColumnConfig=new ColumnConfig(GWTResultDetailLog.N_CasePassFlag, "状态", 60);

		}else{
			columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseFlowNo, "测试用例编号", 200));
			columns.add(new ColumnConfig(GWTResultDetailLog.N_CaseFlowName, "测试用例名称", 200));
			RECEIVEDREPLAYFLAG_ColumnConfig=new ColumnConfig(GWTResultDetailLog.N_CaseFlowPassFlag, "状态", 60);

		}
		//columns.add(new ColumnConfig(GWTResultDetailLog.N_CARDNUMBER, "卡号", 110));
	//	columns.add(new ColumnConfig(GWTResultDetailLog.N_AMOUNT, "交易金额", 100));
		
		RECEIVEDREPLAYFLAG_ColumnConfig.setRenderer(RECEIVEDREPLAYFLAGchange);
		columns.add(RECEIVEDREPLAYFLAG_ColumnConfig); // 成功失败
		if(!isCase){
			columns.add(GetRenderColumn("taskDetail", false, "详情", 40));
		}

		return columns;
	}

	
	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();

		return detailMap;
	}
	
	
	
	/**
	 * 打开执行结果详情页面
	 * @param model GWTResultDetailLog
	 */
	public static void openDetailPage(GWTResultDetailLog model) {
		// TODO Auto-generated method stub
		String tabId = "caseInstance" + model.GetCaseFlowInstanceID();
		String tabTitle ="[" + model.GetCaseFlowName() + "]执行结果";
		BasePage page = AppContext.GetRegisterPage(tabId);
		if(page == null){
			page = new ResultDetailPage2(model.GetCaseFlowInstanceID());
			AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
		}else{
			AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
		}
	}
	
	/**
	 * 获得 脚本定义、执行列
	 * @param iconType	按钮样式名称
	 * @param fireExec	true:执行列 false:定义列
	 * @param title		列表头名称
	 * @param width		列宽
	 * @return			脚本定义、执行列
	 */
/*	private ColumnConfig GetRenderColumn(final String iconType,final boolean fireExec,String title,int width)
	{
		GridCellRenderer<GWTResultDetailLog> gridRender = new GridCellRenderer<GWTResultDetailLog>() {
			@Override
			public Object render(final GWTResultDetailLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultDetailLog> store, Grid<GWTResultDetailLog> grid) {
				String iconID = "icon_" + iconType + model.getID();
				
				boolean isSet = true;//model.getIsSet();
				String iconName = iconType + (isSet ? "" : "_No");
				// 如果需要绑定的结果集内容为空 则绑定空图标
				/*if (iconType == "SPDetail") // 结果展示
				{
			        if (model.getSPSQLRESULT()== null)
			        {
			        	iconName = "";
			        }
			        else
			        {
			        	if (model.getSPSQLRESULT().toString()== "")
			        	{
			        		iconName = "";
			        	}
			        }
				}
				else if (iconType == "RPDetail") // 结果展示
				{
					if (model.getRPSQLRESULT()== null)
			        {
						iconName = "";
			        }
			        else
			        {
			        	if (model.getRPSQLRESULT().toString()== "")
			        	{
			        		iconName = "";
			        	}
			        }
				}
				else if (iconType == "AC1Detail") // 结果展示
				{
					if (model.getACSQL1RESULT()== null)
			        {
						iconName = "";
			        }
			        else
			        {
			        	if (model.getACSQL1RESULT().toString()== "")
			        	{
			        		iconName = "";
			        	}
			        }
				}
				else if (iconType == "AC2Detail") // 结果展示
				{
					if (model.getACSQL2RESULT()== null)
			        {
						iconName = "";
			        }
			        else
			        {
			        	if (model.getACSQL2RESULT().toString()== "")
			        	{
			        		iconName = "";
			        	}
			        }
				}
				IconButton b = new IconButton(iconName);
				if(fireExec && !isSet)
				{
					b.setEnabled(false);
					b.setStyleAttribute("cursor", "default");
				}
				
				if(iconName == "")
				{
					b.setEnabled(true);
				}
				
				HtmlContainer html = new HtmlContainer( "<span style = 'margin:0px;padding:0px;' id = '"
						+ iconID + "' ></span>" );
				html.add(b, "#" + iconID);
				b.addSelectionListener(new SelectionListener<IconButtonEvent>() {
							@Override
							public void componentSelected(IconButtonEvent ce) {

								final Window window = new Window();
							
								window.setModal(true);
								window.setBlinkModal(false);
								window.setLayout(new BorderLayout());
								window.setResizable(true);
								window.setWidth(500);
								window.setMinHeight(300);																								
								
								if (iconType == "SPDetail") // 结果展示
								{
									if(model.getSPSQL()==null || model.getSPSQL().isEmpty()){
										MessageBox.alert("SP结果", "SP Sql 为空！", null);
									}else if(model.getSPSQLRESULT()==null ||model.getSPSQLRESULT().isEmpty()){
										MessageBox.alert("SP结果", "SP查询结果 为空！", null);
									}else{
										window.setHeading("SP结果显示");		
										showWindow(window, model.getSPSQL(),model.getSPSQLRESULT());									
									}
								}
								else if (iconType == "RPDetail") // 结果展示
								{
									if(model.getRPSQL()==null || model.getRPSQL().isEmpty()){
										MessageBox.alert("RP结果", "RP Sql 为空！", null);
									}else if(model.getRPSQLRESULT()==null || model.getRPSQLRESULT().isEmpty()){
										MessageBox.alert("RP结果", "RP查询结果 为空！", null);
									}else{ 
										window.setHeading("RP结果显示");
										showWindow(window, model.getRPSQL(),model.getRPSQLRESULT());										
									}
								}
								else if (iconType == "AC1Detail") // 结果展示
								{
									if(model.getACSQL1()==null || model.getACSQL1().isEmpty()){
										MessageBox.alert("AC1结果", "AC1 Sql 为空！", null);
									}else if(model.getACSQL1RESULT()==null || model.getACSQL1RESULT().isEmpty()){
										MessageBox.alert("AC1结果", "AC1查询结果 为空！", null);
									}else{
										window.setHeading("AC1结果显示");										
										showWindow(window, model.getACSQL1(),model.getACSQL1RESULT());

									}
								}
								else if (iconType == "AC2Detail") // 结果展示
								{
									if(model.getACSQL2()==null || model.getACSQL2().isEmpty()){
										MessageBox.alert("AC2结果", "AC2 Sql 为空！", null);
									}else if(model.getACSQL2RESULT()==null || model.getACSQL2RESULT().isEmpty()){
										MessageBox.alert("AC2结果", "AC2查询结果 为空！", null);
									}else{
										window.setHeading("AC2结果显示");
										showWindow(window, model.getACSQL2(),model.getACSQL2RESULT());
									}
								}
								
							}
						});
				return html;
			}
		};
		ColumnConfig column = new ColumnConfig("", title, width);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setSortable(false);
		column.setRenderer(gridRender);
		
		return column;
	}
*/

	/*private void showWindow(Window window, String sql, String sqlResult){
				
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 100);		
		northData.setMargins(new Margins(5, 5, 5, 5));
		
		FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		//formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		formPanel.setScrollMode(Scroll.AUTOY);
		FormData formdata = new FormData("90%");
		formPanel.setHeight(100);
		TextArea taSql = new TextArea();
		taSql.setFieldLabel("查询语句");
		taSql.setHeight(80);
		taSql.setValue(sql);
		taSql.setReadOnly(true);
		formPanel.add(taSql, formdata);
		window.add(formPanel, northData);
	
		
		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.CENTER, 150);
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBorders(true);
		panel.setBodyBorder(true);
		panel.setWidth(450);
		panel.setAutoHeight(true);
		panel.setScrollMode(Scroll.AUTOY);
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		if (!sql.toUpperCase().contains("SELECT"))
			window.mask("解析出错：sql 语句当中没有 select \r\n sql语句为"+sql);
		if (!sql.toUpperCase().contains("FROM"))
			window.mask("解析出错：sql 语句当中没有 from \r\n sql语句为"+sql);
		int indexSelect = sql.toUpperCase().indexOf("SELECT");
		int indexFrom = sql.toUpperCase().indexOf("FROM");
		String fields = sql.substring(indexSelect+6,indexFrom);
		String[]cols =sqlResult.split("}");
		int colNum = cols[0].split("\\|").length;
		if(!fields.trim().equals("*")){
			String [] colTitles = fields.split(",");
			int i = 1;
			for(String col : colTitles){
				ColumnConfig column = new ColumnConfig();
				column.setId("c"+String.valueOf(i++));
				column.setHeader(col);
				column.setResizable(true);
				column.setWidth(500/colNum);
				configs.add(column);
			}
		}else{
			for(int i = 0, j = 1; i<colNum; i++){
				ColumnConfig column = new ColumnConfig();
				column.setId("c"+String.valueOf(j));
				column.setHeader("column"+String.valueOf(j++));
				column.setResizable(true);
				column.setWidth(500/colNum);
				configs.add(column);
			}
		}
		
	    // create the column model  
	    ColumnModel cm = new ColumnModel(configs);  
		ModelType type = new ModelType();  
	    type.setRoot("results");  
	    type.setRecordName("result");
	    for(int i = 1; i <= colNum; i++){
	    	type.addField("c"+String.valueOf(i));
	    }
	   
	    String sqlResultXml = buildXml(sqlResult);
	    
	    
	    XmlLoadResultReader<ListLoadResult<ModelData>> reader = new XmlLoadResultReader<ListLoadResult<ModelData>>(type);  
	    ListLoadResult<ModelData> lst = reader.read(null, sqlResultXml);
	    ListStore<ModelData> store = new ListStore<ModelData>();
	    store.add(lst.getData());
		
	    final Grid<ModelData> grid = new Grid<ModelData>(store, cm);  
	    grid.setBorders(true);  
	    grid.setAutoExpandColumn("c1");
	    grid.setStyleAttribute("borderTop", "none");    
	    grid.setBorders(false);  
	    grid.setStripeRows(true);   

	    grid.setLoadMask(true);

		panel.add(grid);
		//panel.setAutoHeight(true);
		window.add(panel, southData);
		window.show();
		
		
	}
	private String buildXml(String sqlResult) {
		// TODO Auto-generated method stub
		String[] rows = sqlResult.split("}");
		for(int i = 0; i < rows.length; i++){
			rows[i] = rows[i].trim();
			if(rows[i].startsWith("{")){
				rows[i] = rows[i].substring(1);
			}
		}
		StringBuffer xml = new StringBuffer();
		
		xml.append("<results>");				
		for(int i=0; i<rows.length; i++){
			xml.append("<result>");
			String[] cols = rows[i].split("\\|");
			for(int j=0; j<cols.length; j++){
				cols[j] = cols[j].trim();
				xml.append("<c"+String.valueOf(j+1)+">");
				xml.append(cols[j]);
				xml.append("</c"+String.valueOf(j+1)+">");
			}
			xml.append("</result>");
		}
		xml.append("</results>");
		return xml.toString();
		
	}*/

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