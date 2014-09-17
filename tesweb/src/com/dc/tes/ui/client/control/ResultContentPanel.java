package com.dc.tes.ui.client.control;

import java.util.ArrayList;
import java.util.List;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTResultCompare;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.XmlLoadResultReader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 结果比对Panel
 */
public class ResultContentPanel<T extends ModelData> extends ContentPanel
		implements ICascadePanel{
	private ListStore<BeanModel> store;
	private GWTResultDetailLog record = null;
	private Grid<BeanModel> grid;
	ColumnModel cm;
	IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	protected BaseListLoader<ListLoadResult<BeanModel>> loader = null;
	public ResultContentPanel(){
		this.setHeaderVisible(false);
		//this.setBorders(true);
		//this.setBodyBorder(true);
		this.setWidth("100%");
		//this.setHeight("50%");
		this.setLayout(new FitLayout());   
		this.setScrollMode(Scroll.AUTOY);
		this.hide();
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
//		column.setId("caseName");
//		column.setHeader("案例名称");
//		column.setWidth(150);
//		column.setResizable(true);
//		configs.add(column);
//		
//		column = new ColumnConfig();
		column.setId("paramName");
		column.setHeader("参数名称");
		column.setWidth(90);
		column.setResizable(true);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("paramDesc");
		column.setHeader("参数描述");
		column.setWidth(110);
		column.setResizable(true);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("paramType");
		column.setHeader("参数类型");
		column.setWidth(80);
		column.setResizable(true);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("realVal");
		column.setHeader("实际值");
		column.setWidth(200);
		column.setResizable(true);
		//column.setToolTip("1111");
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("expVal");
		column.setHeader("预期值");
		column.setWidth(200);
		column.setResizable(true);
		configs.add(column);
		
		GridCellRenderer<GWTResultCompare> gridRender = new GridCellRenderer<GWTResultCompare>(){

			@Override
			public Object render(GWTResultCompare model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultCompare> store, Grid<GWTResultCompare> grid) {
				// TODO Auto-generated method stub
				if(model.GetIsEqual()==null || model.GetIsEqual().isEmpty()){
					return "";
				}
				if(model.GetIsEqual().equals("是"))
					return "<span style='color:" + "green" + "'>" + "是" + "</span>";
				else {
					 return "<span style='color:" + "red" + "'>" + "否" + "</span>";
				}
			}		
		};
		column = new ColumnConfig();
		column.setId("isEqual");
		column.setHeader("是否一致");
		column.setWidth(60);
		column.setResizable(true);
		column.setRenderer(gridRender);
		configs.add(column);
		RowNumberer columnNum = new RowNumberer();
		configs.add(0, columnNum);
		cm = new ColumnModel(configs); 
		
		RpcProxy<List<GWTResultCompare>> proxy = new RpcProxy<List<GWTResultCompare>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GWTResultCompare>> callback) {
				// TODO Auto-generated method stub
				resultService.GetCompareList(record, callback);
			}
		}; 
		  
	    // loader and store  
	    loader = new BaseListLoader<ListLoadResult<BeanModel>>(proxy);  
	    store = new ListStore<BeanModel>(loader);
	   
		grid = new Grid<BeanModel>(store, cm);  
	    grid.setBorders(true);  
	    grid.setStyleAttribute("borderTop", "none");    
	    grid.setBorders(false);  
	    grid.setAutoHeight(true);
	    grid.setStripeRows(true);   

	    grid.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<T>>() {
			public void handleEvent(
					SelectionChangedEvent<T> be) {
				String title = "";
				GWTResultCompare gt = (GWTResultCompare)be.getSelection().get(0);
				MessageBox.alert(title,gt.GetRealVal() , null);
				
			}
		});
	    grid.addListener(Events.OnDoubleClick,new Listener<GridEvent<T>>()
	    		{

					@Override
					public void handleEvent(GridEvent<T> be) {
						// TODO Auto-generated method stub
						final Window window = new Window();
						// 取得被点击的对象
						GWTResultCompare g = (GWTResultCompare)be.getModel();
						window.setModal(true);
						window.setBlinkModal(false);
						window.setLayout(new BorderLayout());
						window.setResizable(true);
						window.setWidth(500);
						window.setHeading("SQL查询结果");
						window.setMinHeight(300);
						String sql = g.GetRealSql();
						String sqlResult = g.GetRealVal();
						showWindow(window, sql, sqlResult, g.GetExpVal());
					}
	    	
	    		}
	    );
	    grid.setView(new GridView(){
	    	protected void onBeforeDataChanged(StoreEvent<ModelData> se) {
	    	    if (grid.isLoadMask()) {
	    	      grid.mask("加载中...");
	    	    }
	    	  }
	    });
	    
	    
	 //   loader.load();
	    this.add(grid);
		
	}
	
	
	private void showWindow(Window window, String sql, String sqlResult, String expVal){
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 120);		
		northData.setMargins(new Margins(5, 5, 5, 5));
		
		FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		//formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		formPanel.setScrollMode(Scroll.AUTOX);
		FormData formdata = new FormData("90%");
		formPanel.setHeight(100);
		TextArea taSql = new TextArea();
		taSql.setFieldLabel("查询语句");
		//taSql.setFieldLabel("预期值");
		taSql.setHeight(80);
		taSql.setValue(sql);
		taSql.setReadOnly(true);
		formPanel.add(taSql, formdata);
		
		
		TextField<String> tfExpVal = new TextField<String>();
		tfExpVal.setValue(expVal);
		tfExpVal.setReadOnly(true);
		tfExpVal.setFieldLabel("预期值");
		formPanel.add(tfExpVal, formdata);
		
		window.add(formPanel, northData);
		
		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.CENTER, 130);
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBorders(true);
		panel.setBodyBorder(true);
		panel.setWidth(450);
		panel.setHeight(200);
		//panel.setAutoHeight(true);
		panel.setScrollMode(Scroll.AUTOX);
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		if (sqlResult== null)
		{
			MessageBox.alert("实际值为空", "实际值为空", null);
			return;
		}
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
				if(colNum>5)
					column.setWidth(100);
				else {
					column.setWidth(480/colNum);
				}
				configs.add(column);
			}
		}else{
			for(int i = 0, j = 1; i<colNum; i++){
				ColumnConfig column = new ColumnConfig();
				column.setId("c"+String.valueOf(j));
				column.setHeader("实际值"+String.valueOf(j++));
				column.setResizable(true);
				if(colNum>5)
					column.setWidth(100);
				else {
					column.setWidth(480/colNum);
				}
				configs.add(column);
			}
		}
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
	    grid.setHeight(130);
	    grid.setStripeRows(true);   
	    //grid.
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
		
	}
	
	public ListStore<BeanModel> getStore() {
		return store;
	}
	public void setStore(ListStore<BeanModel> store) {
		this.store = store;
	}
	
	@Override
	public void showCascadePanel(final ModelData record){
		if(record == null || ((GWTResultDetailLog)record).GetCaseName().isEmpty()){
			this.hide();
		}else{
			this.record = (GWTResultDetailLog)record;  
			loader.load();
			this.show();
		}
	}
	
	public Grid<BeanModel> getDataGrid(){
		return grid;
	}

	
	 
}

