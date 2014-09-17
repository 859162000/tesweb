package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.ITestRoundService;
import com.dc.tes.ui.client.ITestRoundServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTTestRound;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TestRoundPage extends BasePage {
	private GridContentPanel<GWTTestRound> panel;
	private GWTTestRound EditTestRound;
	private FormContentPanel<GWTTestRound> detailPanel;
	private ConfigToolBar configBar;
	private ITestRoundServiceAsync testRoundService;
	
	public TestRoundPage(){
	}
	
	@Override
	protected void onRender(Element parent, int index){
		super.onRender(parent, index);
		testRoundService = ServiceHelper.GetDynamicService("testRound", ITestRoundService.class);
		panel = new GridContentPanel<GWTTestRound>();
		RpcProxy<PagingLoadResult<GWTTestRound>> proxy = new RpcProxy<PagingLoadResult<GWTTestRound>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTTestRound>> callback) {
				// TODO Auto-generated method stub
				testRoundService.GetTestRoundList(GetSystemID(), panel.GetSearchCondition(), 
						(PagingLoadConfig)loadConfig, callback);
			}
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView();
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddWidget(new FillToolItem());
		configBar.AddNewBtn("btnAdd", AddHandler());
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);
		
		detailPanel = new FormContentPanel<GWTTestRound>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
		
		
	}

	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTTestRound.N_RoundNo, "轮次号");
		detailMap.put(GWTTestRound.N_RoundName, "轮次名");
		detailMap.put(GWTTestRound.N_Desc, "描述");
		detailMap.put(GWTTestRound.N_StartDate, "开始日期");
		detailMap.put(GWTTestRound.N_EndDate, "结束日期");
		detailMap.put(GWTTestRound.N_CurrentRoundFlagStr, "当前轮次");
		return detailMap;
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTTestRound.N_RoundNo, "轮次号", 100));
		columns.add(new ColumnConfig(GWTTestRound.N_RoundName, "轮次名", 150));
		columns.add(new ColumnConfig(GWTTestRound.N_Desc, "描述", 200));
		columns.add(new ColumnConfig(GWTTestRound.N_StartDate, "开始日期", 120));
		columns.add(new ColumnConfig(GWTTestRound.N_EndDate, "结束日期", 120));
		columns.add(new ColumnConfig(GWTTestRound.N_CurrentRoundFlagStr, "当前轮次", 60));
		return columns;
	}

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					MessageBox.confirm("删除提示", "删除轮次将级联删除该轮次下的所有执行结果，是否继续？", 
							new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){
										testRoundService.DeleteTestRound(panel.getSelection(),
												new AsyncCallback<Boolean>() {
													public void onFailure(Throwable caught) {
														caught.printStackTrace();
														MessageBox.alert("错误提示", "删除失败", null);
													}
	
													public void onSuccess(Boolean obj) {
														panel.reloadGrid();
													}
												});
									}
								}
							});
					
				}
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<GWTTestRound> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditTestRound = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}
	
	private SelectionListener<ButtonEvent> AddHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditTestRound = new GWTTestRound(null, GetSystemID());
				EditTestRound.set(GWTTestRound.N_RoundName, "");
				CreateEditForm();
			}
		};
	}
	
	protected void CreateEditForm() {
		// TODO Auto-generated method stub
		final Window window = new Window();

		window.setSize(340, 290);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		formPanel.setPadding(5);
		
		FormData formData = new FormData("90%");	
		String labelStyle = "width:70px;";
		
		final TextField<String> roundNo = new TextField<String>();
		roundNo.setName(GWTTestRound.N_RoundNo);
		roundNo.setAllowBlank(false);
		roundNo.setRegex("^[0-9]+$");		
		roundNo.getMessages().setBlankText("轮次号不能为空");
		roundNo.getMessages().setRegexText("轮次号须为整数值");
		roundNo.setFieldLabel("轮次号");
		formPanel.add(roundNo,formData);
		
		final DistTextField roundName = new DistTextField(EditTestRound, 
				EditTestRound.GetRoundName(), "轮次名", "已存在该名称的轮次，请重新输入");
		roundName.setLabelStyle(labelStyle);
		roundName.setMaxLength(32);
		formPanel.add(roundName, formData);
		
		final DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy年MM月dd日");
		
		final DateField startdate = new DateField();
		startdate.setFieldLabel("开始时间");
	    startdate.setPropertyEditor(new DateTimePropertyEditor("yyyy年MM月dd日"));  
	    //startdate.setWidth(115);	       
	    formPanel.add(startdate, formData);
	    
	    final DateField enddate = new DateField();  
	    enddate.setFieldLabel("结束时间");
	    enddate.setPropertyEditor(new DateTimePropertyEditor("yyyy年MM月dd日"));
	   // enddate.setWidth(115);
	    formPanel.add(enddate, formData);
	    
	    final ComboBox<GWTStock> isCurrentRound = new ComboBox<GWTStock>();
	    isCurrentRound.setFieldLabel("当前轮次");
	    isCurrentRound.setValueField(GWTStock.N_Pos);
	    isCurrentRound.setDisplayField(GWTStock.N_Name);
	    isCurrentRound.setEditable(false);
	    ListStore<GWTStock> store = new ListStore<GWTStock>();
	    store.add(new GWTStock("否", "0"));
	    store.add(new GWTStock("是", "1"));
	    isCurrentRound.setStore(store);
	    isCurrentRound.setTriggerAction(TriggerAction.ALL);
	    formPanel.add(isCurrentRound, formData);
	    
	    final TextArea taDesc = new TextArea();
	    taDesc.setFieldLabel("描述");
	    formPanel.add(taDesc, formData);
	    
	    Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				if (!formPanel.isValid())
					return;
				EditTestRound.SetValue(Integer.parseInt(roundNo.getValue()), roundName.getValue(), taDesc.getValue(), 
						dtf.format(startdate.getValue()), dtf.format(enddate.getValue()), Integer.parseInt(isCurrentRound.getValue().getPos()));
				testRoundService.SaveOrUpdateTestRound(EditTestRound, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						MessageBox.alert("错误信息", "保存失败，轮次号已存在", null);
					}

					@Override
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						
						if(result){
							panel.loaderReLoad(EditTestRound.isNew());
							window.hide();
						}
						else{
							
							roundName.focus();
							roundName.EnforceValidate();
							
							MessageBox.alert("错误信息", "保存失败!", null);
						}
					}
				});
			}
		});
	    window.addButton(btnOK);
	    window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		}));
		window.add(formPanel);
		
		if(EditTestRound.isNew()){
			window.setHeading("新增轮次信息");
		}else{
			window.setHeading("编辑轮次信息");
			roundNo.setValue(EditTestRound.GetRoundNo().toString());
			startdate.setValue(EditTestRound.GetStartDate()==null?null: dtf.parse(EditTestRound.GetStartDate()));
			enddate.setValue(EditTestRound.GetEndDate()==null?null: dtf.parse(EditTestRound.GetEndDate()));
		    taDesc.setValue(EditTestRound.GetDesc());
		    roundName.setValue(EditTestRound.GetRoundName());
		    isCurrentRound.setValue(EditTestRound.isNew()?store.getAt(0):
		    	(EditTestRound.GetCurrentRoundFlag()==0?store.getAt(0):store.getAt(1)));
		}
	    window.show();
	    
	}
	
	
}
