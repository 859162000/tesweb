package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.IInterfaceService;
import com.dc.tes.ui.client.IInterfaceServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ICascadePanel;
import com.dc.tes.ui.client.model.GWTInterfaceField;
import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InterfaceFieldPanel extends ContentPanel implements ICascadePanel {

	private ListStore<GWTInterfaceField> store;
	protected BaseListLoader<ListLoadResult<GWTInterfaceField>> loader = null;
	private GWTInterfaceDef interfaceDef = null;
	private IInterfaceServiceAsync interfaceService = ServiceHelper.GetDynamicService("interface", IInterfaceService.class);
	private Grid<GWTInterfaceField> grid;
	CheckBoxSelectionModel<GWTInterfaceField> columnCK = new CheckBoxSelectionModel<GWTInterfaceField>();
	ColumnModel cm;
	private Button btnSave;
	public InterfaceFieldPanel(){
		this.setHeaderVisible(false);
		this.setWidth("100%");
		this.setScrollMode(Scroll.AUTOY);		
		
		btnSave = new Button("保存", MainPage.ICONS
				.Save(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				handleSequenceChange();
				btnSave.disable();
			}
		});
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();		
		columns.add(new ColumnConfig(GWTInterfaceField.N_FieldName, "字段名称",150));
		columns.add(new ColumnConfig(GWTInterfaceField.N_ChineseName, "中文名称", 120));
		columns.add(new ColumnConfig(GWTInterfaceField.N_FieldLen, "字段长度",100));
		columns.add(new ColumnConfig(GWTInterfaceField.N_FieldTypeExpr, "字段类型",80));
		columns.add(new ColumnConfig(GWTInterfaceField.N_Optional, "是否必填",80));
		columns.add(new ColumnConfig(GWTInterfaceField.N_DefaultValue, "默认值",120));
		columns.add(new ColumnConfig(GWTInterfaceField.N_Memo, "备注",120));
		ColumnConfig ckConfig = columnCK.getColumn();		
		columns.add(0, ckConfig);
		
		cm = new ColumnModel(columns);
		RpcProxy<List<GWTInterfaceField>> proxy = new RpcProxy<List<GWTInterfaceField>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GWTInterfaceField>> callback) {
				// TODO Auto-generated method stub
				interfaceService.GetInterfaceFields(interfaceDef, callback);
			}
		};
		loader = new BaseListLoader<ListLoadResult<GWTInterfaceField>>(proxy);
		store = new ListStore<GWTInterfaceField>(loader);
		grid = new Grid<GWTInterfaceField>(store, cm); 
	    grid.setStyleAttribute("borderTop", "none");    
	    grid.setBorders(false);  
	    grid.setAutoHeight(true);
	    grid.setStripeRows(true);   
	    grid.setSelectionModel(columnCK);
	    grid.addPlugin(columnCK);  
		grid.setView(new GridView(){
	    	protected void onBeforeDataChanged(StoreEvent<ModelData> se) {
	    	    if (grid.isLoadMask()) {
	    	      grid.mask("加载中...");
	    	    }
	    	  }
	    });
		new GridDragSource(grid);
		GridDropTarget target = new GridDropTarget(grid){
			@Override
			protected void onDragDrop(DNDEvent e) {
				// TODO Auto-generated method stub
				super.onDragDrop(e);
				store.setMonitorChanges(true);
				btnSave.enable();
			}
		};
		target.setFeedback(DND.Feedback.INSERT);
		target.setAllowSelfAsSource(true);
		target.setOperation(Operation.MOVE);		
		
		this.add(grid);
			
	}
	
	protected void handleSequenceChange(){
		List<GWTInterfaceField> list = new ArrayList<GWTInterfaceField>();
		for(int i = 0; i < store.getCount(); i++){
			GWTInterfaceField field = store.getAt(i);
			field.SetSequence(i);
			list.add(field);
		}
		interfaceService.updateFieldSequence(list, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示", "保存字段顺序失败，请联系管理员！", null);
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				store.setMonitorChanges(false);
			}
		});
	}
	
	@Override
	public void showCascadePanel(final ModelData modelData) {
		if(store.isMonitorChanges()){
			MessageBox.confirm("提示", "接口的字段次序已经改变，是否保存修改？", 
					new Listener<MessageBoxEvent>(){
				@Override
				public void handleEvent(MessageBoxEvent be) {
					// TODO Auto-generated method stub
					if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){
						handleSequenceChange();
					}else{
						store.setMonitorChanges(false);
					}
					if(modelData == null){
						hide();
					}else{
						interfaceDef = (GWTInterfaceDef)modelData;
						loader.load();
						show();
					}
				}						
			});					
		}else{
			if(modelData == null){
				this.hide();
			}else{
				this.interfaceDef = (GWTInterfaceDef)modelData;
				loader.load();
				this.show();
			}
		}
		btnSave.disable();
	}
	
	public Grid<GWTInterfaceField> getDataGrid(){
		return grid;
	}

	public void reloadGrid(){
		grid.getStore().getLoader().load();
	}
	
	public Button getSaveButton(){		
		return btnSave;
	}
	
}
