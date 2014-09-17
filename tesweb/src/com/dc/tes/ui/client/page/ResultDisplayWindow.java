package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.XMLEdit;
import com.dc.tes.ui.client.model.GWTMsgAttribute;
import com.dc.tes.ui.client.model.GWTPack_Base;
import com.dc.tes.ui.client.model.GWTPack_Field;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class ResultDisplayWindow extends BasePage {
	IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	private TreeStore<ModelData> reqStore = new TreeStore<ModelData>();
	private TreeStore<ModelData> resStore = new TreeStore<ModelData>();
	private TreeGrid<ModelData> reqTree = null;
	private TreeGrid<ModelData> resTree = null;
	private ArrayList<GWTMsgAttribute> reqSAttrs = null;
	private ArrayList<GWTMsgAttribute> reqFAttrs = null;
	private ArrayList<GWTMsgAttribute> resSAttrs = null;
	private ArrayList<GWTMsgAttribute> resFAttrs = null;
	
	public void DrawResultWindow(final GWTResultDetailLog gwtResultDetailLog) {
		// TODO Auto-generated method stub	
		Window win = new Window();
		win.setSize(1000, 540);
		win.setHeading("请求/响应报文");
		BoxComponent request = new BoxComponent();
		BoxComponent respone = new BoxComponent();
		request.setBorders(true);
		respone.setBorders(true);
		
		if(gwtResultDetailLog.getREQUESCONTENT().startsWith("<?xml")) {  //XML报文显示
			request = new XMLEdit();
			respone = new XMLEdit();
			((XMLEdit)request).setValue(gwtResultDetailLog.getREQUESCONTENT());
			((XMLEdit)respone).setValue(gwtResultDetailLog.getRESPONCONTENT()); 
			BorderLayout layout = new BorderLayout();  
			win.setLayout(layout);			
			win.add(request,new BorderLayoutData(LayoutRegion.WEST, 500));  
			win.add(respone,new BorderLayoutData(LayoutRegion.CENTER, 500)); 
		}else if(GetSysInfo().GetSystemName().equals("二代支付前置机")){  // 二代支付项目较为特殊，特别处理
			win.setHeight(600);
			BorderLayout layout = new BorderLayout();  
			win.setLayout(layout);
			int flag = gwtResultDetailLog.getREQUESCONTENT().indexOf("<");
			String head = "";
			String content = "";
			if(flag != -1){
				head = gwtResultDetailLog.getREQUESCONTENT().substring(0, flag);
				content = gwtResultDetailLog.getREQUESCONTENT().substring(flag);
			}
			win.add(setRequestDataForMBFE(head, content), new BorderLayoutData(LayoutRegion.WEST, 500));							  			
			win.add(setResponseDataForMBFE(gwtResultDetailLog), new BorderLayoutData(LayoutRegion.CENTER, 500));
		}else {  // 其它报文显示    
			TabPanel tabPanel = new TabPanel();
			tabPanel.setBodyBorder(false);
			tabPanel.setBorders(false);
			tabPanel.setTabPosition(TabPosition.BOTTOM);
			tabPanel.add(originalContentItem(gwtResultDetailLog)); //原始报文
			tabPanel.add(decoratedContentItem(gwtResultDetailLog)); //加工报文
			win.setLayout(new FitLayout());
			win.add(tabPanel);
		}
		win.setPlain(true);
		win.setModal(true);
		win.setBlinkModal(false);
		
		win.show();
	}

	///////////////////////////////////////////////////////
	//二代支付特殊处理部分
	///////////////////////////////////////////////////////	
	private Widget setResponseDataForMBFE(GWTResultDetailLog gwtResultDetailLog) {
		// TODO Auto-generated method stub		
		TabPanel tabPanel = new TabPanel();
		tabPanel.setBodyBorder(false);
		tabPanel.setBorders(false);
		tabPanel.setTabPosition(TabPosition.BOTTOM);
		
		TextArea response = new TextArea();			
		response.setValue(gwtResultDetailLog.getRESPONCONTENT());			
		response.setReadOnly(true);
		
		TabItem ocItem = new TabItem();
		ocItem.setText("原始报文");
		ocItem.setClosable(false);
		ocItem.setId("1");
		ocItem.setLayout(new FitLayout());
		ocItem.add(response);
		tabPanel.add(ocItem);
		
		TabItem dcItem = new TabItem();
		dcItem.setText("加工报文");
		dcItem.setClosable(false);
		dcItem.setId("2");
		dcItem.setLayout(new FitLayout());
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeaderVisible(false);
		contentPanel.setBodyBorder(false);
		contentPanel.setBorders(false);
		contentPanel.setLayout(new FitLayout());
		LoadData(gwtResultDetailLog);
		contentPanel.add(drawResTree());
		dcItem.add(contentPanel);
		tabPanel.add(dcItem);
		return tabPanel;			
	}

	private Widget setRequestDataForMBFE(String head, String content) {
		// TODO Auto-generated method stub
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setBorders(false);
		panel.setLayout(new BorderLayout());
		TextArea headArea = new TextArea();
		headArea.setSize(500, 60);
		headArea.setValue(head);
		panel.add(headArea, new BorderLayoutData(LayoutRegion.NORTH, 60));
		XMLEdit xmlEdit = new XMLEdit();
		xmlEdit.setValue(content);
		panel.add(xmlEdit, new BorderLayoutData(LayoutRegion.CENTER));
		
		return panel;
	}
	///////////////////////////////////////////////////////////////////
	
	private TabItem decoratedContentItem(GWTResultDetailLog gwtResultDetailLog) {
		// TODO Auto-generated method stub
		TabItem dcItem = new TabItem();
		dcItem.setText("加工报文");
		dcItem.setClosable(false);
		dcItem.setId("2");
		dcItem.setLayout(new FitLayout());
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeaderVisible(false);
		contentPanel.setBodyBorder(false);
		contentPanel.setBorders(false);
		BorderLayout layout = new BorderLayout();  
		contentPanel.setLayout(layout);
		LoadData(gwtResultDetailLog);
		contentPanel.add(drawReqTree(),new BorderLayoutData(LayoutRegion.WEST, 500));  
		contentPanel.add(drawResTree(),new BorderLayoutData(LayoutRegion.CENTER, 500)); 
		dcItem.add(contentPanel);
		return dcItem;
	}

	
	private Widget drawReqTree() {
		// TODO Auto-generated method stub
		reqTree = new TreeGrid<ModelData>(reqStore, DefineColumnModel()){
			@Override
			public void reconfigure(ListStore<ModelData> store, ColumnModel cm) {
				viewReady = true;
				rendered = true;
				afterRender();
				super.reconfigure(store, cm);
			}	
		};
		reqTree.setSelectionModel(new TreeGridSelectionModel<ModelData>());

		reqTree.setBorders(false);
		reqTree.setAutoExpandColumn(GWTPack_Base.m_name);
		reqTree.setAutoExpand(true);
		
		reqTree.getStyle().setLeafIcon(MainPage.ICONS.EmptyField());
		reqTree.setIconProvider(new ModelIconProvider<ModelData>() {

			@Override
			public AbstractImagePrototype getIcon(ModelData item) {
				if (item instanceof GWTPack_Field) {
					String data = ((GWTPack_Field) item).getData();
					if (data == null || data.isEmpty()) {
						return MainPage.ICONS.EmptyField();
					}
					return MainPage.ICONS.RightField();
					
				} else
					return MainPage.EXTICONS.tree_folder();
			}
		});
		
		
		return reqTree;
	}
	

	private Widget drawResTree() {
		// TODO Auto-generated method stub
		resTree = new TreeGrid<ModelData>(resStore, DefineColumnModel()){
			@Override
			public void reconfigure(ListStore<ModelData> store, ColumnModel cm) {
				viewReady = true;
				rendered = true;
				afterRender();
				super.reconfigure(store, cm);
			}	
		};
		resTree.setSelectionModel(new TreeGridSelectionModel<ModelData>());

		resTree.setBorders(false);
		resTree.setAutoExpandColumn(GWTPack_Base.m_data);
		resTree.setAutoExpand(true);
		
		resTree.getStyle().setLeafIcon(MainPage.ICONS.EmptyField());
		resTree.setIconProvider(new ModelIconProvider<ModelData>() {

			@Override
			public AbstractImagePrototype getIcon(ModelData item) {
				if (item instanceof GWTPack_Field) {
					String data = ((GWTPack_Field) item).getData();
					if (data == null || data.isEmpty()) {
						return MainPage.ICONS.EmptyField();
					}
					return MainPage.ICONS.RightField();
					
				} else
					return MainPage.EXTICONS.tree_folder();
			}
		});
		
		
		return resTree;
	
	}

	

	private TabItem originalContentItem(GWTResultDetailLog gwtResultDetailLog) {
		// TODO Auto-generated method stub
		TabItem ocItem = new TabItem();
		ocItem.setText("原始报文");
		ocItem.setClosable(false);
		ocItem.setId("1");
		ocItem.setLayout(new FitLayout());
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeaderVisible(false);
		contentPanel.setBodyBorder(false);
		contentPanel.setBorders(false);
		BorderLayout layout = new BorderLayout();  
		contentPanel.setLayout(layout);
		TextArea request = new TextArea();
		TextArea respone = new TextArea();
		request.setValue(gwtResultDetailLog.getREQUESCONTENT());
		respone.setValue(gwtResultDetailLog.getRESPONCONTENT());
		request.setReadOnly(true);
		respone.setReadOnly(true);
		contentPanel.add(request,new BorderLayoutData(LayoutRegion.WEST, 500));  
		contentPanel.add(respone,new BorderLayoutData(LayoutRegion.CENTER, 500)); 
		ocItem.add(contentPanel);
		return ocItem;
	}

	
	
	
	/**
	 * 定义TreeGrid默认列
	 * 
	 * @return TreeGrid列模型
	 */
	private ColumnModel DefineColumnModel() {

		ColumnConfig name = new ColumnConfig("name", "字段名称", 190);
		name.setRenderer(new TreeGridCellRenderer<ModelData>());

		ColumnConfig desc = new ColumnConfig("desc", "描述", 90);
		ColumnConfig data = new ColumnConfig("data", "值", 180);
		ColumnModel cm = new ColumnModel(Arrays.asList(name, desc, data));

		return cm;
	}
	
	private void LoadData(GWTResultDetailLog gwtResultDetailLog) {
		// TODO Auto-generated method stub
		resultService.GetResultContent(gwtResultDetailLog, new AsyncCallback<List<GWTPack_Struct>>() {
			
			@Override
			public void onSuccess(List<GWTPack_Struct> result) {
				// TODO Auto-generated method stub
				if(result.get(0)!=null && reqTree!=null){
					GWTPack_Struct root = result.get(0);
					reqFAttrs = new ArrayList<GWTMsgAttribute>();
					reqSAttrs = new ArrayList<GWTMsgAttribute>();
					for (GWTMsgAttribute attr : root.getFieldAttrList())
						reqFAttrs.add(attr);
					for (GWTMsgAttribute attr : root.getStructAttrList())
						reqSAttrs.add(attr);

					reqTree.getStore().removeAll();
					reqStore.removeAll();
					reqTree.setLazyRowRender(0);
					reqTree.getView().setForceFit(true);
					reqStore.add(root.getChildren(), true);						
					reqTree.reconfigure(reqStore, DefineColumnModel());
					reqTree.setAutoExpandColumn("name");
					reqTree.setExpanded(root.getChild(0), true, true);
				}
				if(result.get(1)!=null && resTree!=null){
					GWTPack_Struct root = result.get(1);
					resFAttrs = new ArrayList<GWTMsgAttribute>();
					resSAttrs = new ArrayList<GWTMsgAttribute>();
					for (GWTMsgAttribute attr : root.getFieldAttrList())
						resFAttrs.add(attr);
					for (GWTMsgAttribute attr : root.getStructAttrList())
						resSAttrs.add(attr);

					resTree.getStore().removeAll();
					resStore.removeAll();
					resTree.setLazyRowRender(0);
					resTree.getView().setForceFit(true);
					resStore.add(root.getChildren(), true);						
					resTree.reconfigure(resStore, DefineColumnModel());
					resTree.setAutoExpandColumn("name");
					resTree.setExpanded(root.getChild(0), true, true);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
			}
		});
		
	}
}
