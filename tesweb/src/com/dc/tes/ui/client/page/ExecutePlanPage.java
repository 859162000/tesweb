package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IExecutePlanService;
import com.dc.tes.ui.client.IExecutePlanServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExecutePlanPage extends BasePage {
	private GridContentPanel<GWTExecutePlan> panel;
	private GWTExecutePlan EditExecutePlan;
	private FormContentPanel<GWTExecutePlan> detailPanel;
	private ConfigToolBar configBar;
	private IExecutePlanServiceAsync executePlanService;
	
	public ExecutePlanPage(){
	}
	
	@Override
	protected void onRender(Element parent, int index){
		super.onRender(parent, index);
		executePlanService = ServiceHelper.GetDynamicService("executePlan", IExecutePlanService.class);
		panel = new GridContentPanel<GWTExecutePlan>();
		RpcProxy<PagingLoadResult<GWTExecutePlan>> proxy = new RpcProxy<PagingLoadResult<GWTExecutePlan>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTExecutePlan>> callback) {
				// TODO Auto-generated method stub
				executePlanService.GetExecutePlanList(GetSystemID(), panel.GetSearchCondition(), 
						(PagingLoadConfig)loadConfig, callback);
			}
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView();
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddButton("btnExecSetExecPlan", new Button("查看执行集计划任务"), 
				ICONS.ExecSetExecPlan(), execSetExecPlanHandler());
		configBar.AddWidget(new FillToolItem());
		configBar.AddNewBtn("btnAdd", AddHandler());
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);
		
		detailPanel = new FormContentPanel<GWTExecutePlan>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
		
		
	}

	
	private SelectionListener<ButtonEvent> execSetExecPlanHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				String tabId = "execSetExecPlan";
				String tabTitle ="执行集计划任务列表";
				String execPlanID = "";
				if(panel.getDataGrid().getSelectionModel().getSelectedItems().size()!=0){
					execPlanID = panel.getDataGrid().getSelectionModel().getSelectedItems().get(0).GetID();
				}
				BasePage page = new ExecuteSetExecPlanPage(execPlanID);
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
			}
		 };
	}
	
	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTExecutePlan.N_Name, "执行计划名称");
		detailMap.put(GWTExecutePlan.N_Desc, "描述");
		detailMap.put(GWTExecutePlan.N_CreateTime, "创建时间");
		detailMap.put(GWTExecutePlan.N_ScheduleRunModeStr, "计划模式");
		detailMap.put(GWTExecutePlan.N_ScheduleRunWeekDay, "执行日期");
		detailMap.put(GWTExecutePlan.N_ScheduleRunHour, "执行时间");
		detailMap.put(GWTExecutePlan.N_StatusStr, "是否有效");
		return detailMap;
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTExecutePlan.N_Name, "执行计划名称", 100));
		columns.add(new ColumnConfig(GWTExecutePlan.N_Desc, "描述", 150));
		columns.add(new ColumnConfig(GWTExecutePlan.N_CreateTime, "创建时间", 120));
		columns.add(new ColumnConfig(GWTExecutePlan.N_ScheduleRunModeStr, "计划模式", 100));
		columns.add(new ColumnConfig(GWTExecutePlan.N_ScheduleRunWeekDay, "执行日期", 80));
		columns.add(new ColumnConfig(GWTExecutePlan.N_ScheduleRunHour, "执行时间", 80));
		columns.add(new ColumnConfig(GWTExecutePlan.N_StatusStr, "是否有效", 70));
		return columns;
	}

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					executePlanService.DeleteExecutePlan(panel.getSelection(),
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
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<GWTExecutePlan> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditExecutePlan = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}
	
	private SelectionListener<ButtonEvent> AddHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditExecutePlan = new GWTExecutePlan("", GetSystemID(), "");
				CreateEditForm();
			}
		};
	}
	
	protected void CreateEditForm() {
		// TODO Auto-generated method stub
		final Window window = new Window();

		window.setSize(340, 280);
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
		
		final DistTextField tfName = new DistTextField(EditExecutePlan, 
				EditExecutePlan.GetName(), "名称", "已存在相同名称的执行计划，请重新输入");
		tfName.setName(GWTExecutePlan.N_Name);
		tfName.setMaxLength(16);
		tfName.setAllowBlank(false);		
		formPanel.add(tfName,formData);
		
		final TextArea taDesc = new TextArea();
		taDesc.setName(GWTExecutePlan.N_Desc);
		taDesc.setAllowBlank(false);		
		taDesc.setFieldLabel("描述");
		formPanel.add(taDesc,formData);
		
	    final ComboBox<GWTStock> cbRunMode = new ComboBox<GWTStock>();
	    cbRunMode.setFieldLabel("计划模式");
	    cbRunMode.setValueField(GWTStock.N_Pos);
	    cbRunMode.setDisplayField(GWTStock.N_Name);
	    cbRunMode.setEditable(false);
	    ListStore<GWTStock> store = new ListStore<GWTStock>();
	  //  store.add(new GWTStock("不执行", "-1"));
	   // store.add(new GWTStock("核心启动时", "0"));
	    store.add(new GWTStock("一次性", "1"));
	    store.add(new GWTStock("每天", "2"));
	    store.add(new GWTStock("每周", "3"));
	    store.add(new GWTStock("每月", "4"));
	    cbRunMode.setStore(store);
	    cbRunMode.setTriggerAction(TriggerAction.ALL);
	    formPanel.add(cbRunMode, formData);
	    
	    final TimeField startTime = new TimeField();
	    startTime.setFieldLabel("执行时间");
	    startTime.setTriggerAction(TriggerAction.ALL);
	    startTime.setAllowBlank(false);
	    startTime.setFormat(DateTimeFormat.getFormat("HH:mm"));
	    startTime.setEditable(false);
	    startTime.getMessages().setBlankText("执行时间不能为空");
	    formPanel.add(startTime, formData);
	    
	    final DateField tfStartdDay = new DateField();
	    tfStartdDay.setFieldLabel("执行日期");
	    tfStartdDay.setPropertyEditor(new DateTimePropertyEditor("yyyy年MM月dd日"));
	    tfStartdDay.setEditable(false);
	    formPanel.add(tfStartdDay, formData);
	    
	    Map<String, CheckBox> cbMap = new HashMap<String, CheckBox>();
	    final CheckBox mon = new CheckBox();
	    mon.setBoxLabel("星期一");
	    mon.setData("weekday", "1");
	    cbMap.put("1", mon);
	    final CheckBox tues = new CheckBox();
	    tues.setBoxLabel("星期二");
	    tues.setData("weekday", "2");
	    cbMap.put("2", tues);
	    final CheckBox wedn = new CheckBox();
	    wedn.setBoxLabel("星期三");
	    wedn.setData("weekday", "3");
	    cbMap.put("3", wedn);
	    final CheckBox thur = new CheckBox();
	    thur.setBoxLabel("星期四");
	    thur.setData("weekday", "4");	
	    cbMap.put("4", thur);
	    final CheckBox fri = new CheckBox();
	    fri.setBoxLabel("星期五");
	    fri.setData("weekday", "5");
	    cbMap.put("5", fri);
	    final CheckBox sat = new CheckBox();
	    sat.setBoxLabel("星期六");
	    sat.setData("weekday", "6");
	    cbMap.put("6", sat);
	    final CheckBox sun = new CheckBox();
	    sun.setBoxLabel("星期日");
	    sun.setData("weekday", "7");
	    cbMap.put("7", sun);
	    final CheckBoxGroup week = new CheckBoxGroup();
	    week.add(mon);
	    week.add(tues);
	    week.add(wedn);
	    week.add(thur);
	    week.add(fri);
	    week.add(sat);
	    week.add(sun);
	    week.setOrientation(Orientation.VERTICAL);
	    week.setFieldLabel("执行日期");
	    formPanel.add(week, formData);
	    
	    final ComboBox<GWTStock> cbDate = new ComboBox<GWTStock>();
	    cbDate.setFieldLabel("执行日期");
	    cbDate.setValueField(GWTStock.N_Pos);
	    cbDate.setDisplayField(GWTStock.N_Name);
	    cbDate.setEditable(false);
	    ListStore<GWTStock> dstore = new ListStore<GWTStock>();
	    for(int i = 1; i<=31; i++){
	    	dstore.add(new GWTStock("每月"+i+"号", String.valueOf(i)));
	    }
	    cbDate.setStore(dstore);
	    cbDate.setTriggerAction(TriggerAction.ALL);
	    formPanel.add(cbDate, formData);
	    
	    Radio radioYes = new Radio();
		radioYes.setData("data", "1");
		radioYes.setValue(true);
		radioYes.setBoxLabel("是");
		Radio radioNo = new Radio();
		radioNo.setData("data", "0");
		radioNo.setBoxLabel("否");
		final RadioGroup rgStatus = new RadioGroup();
		rgStatus.setFieldLabel("是否有效");
		rgStatus.add(radioYes);
		rgStatus.add(radioNo);
		rgStatus.setSpacing(10);
		formPanel.add(rgStatus, formData);
	    
	    cbRunMode.addSelectionChangedListener(new SelectionChangedListener<GWTStock>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<GWTStock> se) {
				// TODO Auto-generated method stub
//				if(cbRunMode.getValue().getPos().equals("-1")){
//					startTime.setVisible(false);
//					tfStartdDay.setVisible(false);
//					week.setVisible(false);
//					cbDate.setVisible(false);
//					window.setHeight(220);
//				}else if (cbRunMode.getValue().getPos().equals("0")) {
//					startTime.setVisible(false);
//					tfStartdDay.setVisible(false);
//					week.setVisible(false);
//					cbDate.setVisible(false);
//					window.setHeight(220);
//				}else
				if (cbRunMode.getValue().getPos().equals("1")) {
					startTime.setVisible(true);
					tfStartdDay.setVisible(true);
					week.setVisible(false);
					cbDate.setVisible(false);
					window.setHeight(280);
				}else if (cbRunMode.getValue().getPos().equals("2")) {
					startTime.setVisible(true);
					tfStartdDay.setVisible(false);
					week.setVisible(false);
					cbDate.setVisible(false);
					window.setHeight(270);
				}else if (cbRunMode.getValue().getPos().equals("3")) {
					startTime.setVisible(true);
					tfStartdDay.setVisible(false);
					week.setVisible(true);
					cbDate.setVisible(false);
					window.setHeight(445);
				}else if (cbRunMode.getValue().getPos().equals("4")) {
					startTime.setVisible(true);
					tfStartdDay.setVisible(false);
					week.setVisible(false);
					cbDate.setVisible(true);
					window.setHeight(280);
				}
			}
		});
	    
	    Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				if(!formPanel.isValid()){
					return;
				}
				String weekday = null;
				String hour = null;
				DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd");
//				if(cbRunMode.getValue().getPos().equals("-1")){
//					weekday = null;
//					hour = null;
//				}else if(cbRunMode.getValue().getPos().equals("0")){
//					weekday = null;
//					hour = null;
//				}else 
				if(cbRunMode.getValue().getPos().equals("1")){
					weekday = dtf.format(tfStartdDay.getValue());
					hour = startTime.getValue().getText();
				}else if(cbRunMode.getValue().getPos().equals("2")){
					hour = startTime.getValue().getText();
					weekday = null;
				}else if(cbRunMode.getValue().getPos().equals("3")){
					hour = startTime.getValue().getText();
					weekday = "";
					for(CheckBox cBox : week.getValues()){
						weekday += cBox.getData("weekday");
					}											
				}else if(cbRunMode.getValue().getPos().equals("4")){
					hour = startTime.getValue().getText();
					weekday = cbDate.getValue().getPos();
				}
				EditExecutePlan.SetValue(tfName.getValue(), taDesc.getValue(), GetUserID(), cbRunMode.getValue().getPos(),
						weekday, hour, rgStatus.getValue().getData("data").toString());
				executePlanService.SaveOrUpdateExecutePlan(EditExecutePlan, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						MessageBox.alert("错误信息", "保存失败", null);
					}

					@Override
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub						
						if(result){
							panel.loaderReLoad(EditExecutePlan.isNew());
							window.hide();
						}else{
							tfName.EnforceValidate();
							tfName.focus();
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
		
		if(EditExecutePlan.isNew()){
			window.setHeading("新增执行计划");
			cbRunMode.setValue(store.getAt(0));
		}else{
			window.setHeading("编辑执行计划");
			tfName.setValue(EditExecutePlan.GetName());
			taDesc.setValue(EditExecutePlan.GetDesc());
			int mode = Integer.parseInt(EditExecutePlan.GetScheduleRunMode());
			cbRunMode.setValue(store.getAt(mode-1));
			if(mode == 1){
				DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd");
				tfStartdDay.setValue(dtf.parse(EditExecutePlan.GetScheduleRunWeekday()));
				DateTimeFormat dtf2 = DateTimeFormat.getFormat("HH:mm");
				startTime.setDateValue(dtf2.parse(EditExecutePlan.GetScheduleRunHour()));
			}else if(mode == 2){
				DateTimeFormat dtf2 = DateTimeFormat.getFormat("HH:mm");
				startTime.setDateValue(dtf2.parse(EditExecutePlan.GetScheduleRunHour()));
			}else if(mode == 3){
				DateTimeFormat dtf2 = DateTimeFormat.getFormat("HH:mm");
				startTime.setDateValue(dtf2.parse(EditExecutePlan.GetScheduleRunHour()));
				
				for(int i = 0; i<EditExecutePlan.GetScheduleRunWeekday().length(); i++){
					String s = EditExecutePlan.GetScheduleRunWeekday().substring(i, i+1);
					cbMap.get(s).setValue(true);
				}
			}else if(mode == 4){
				DateTimeFormat dtf2 = DateTimeFormat.getFormat("HH:mm");
				startTime.setDateValue(dtf2.parse(EditExecutePlan.GetScheduleRunHour()));
				
				cbDate.setValue(dstore.getAt(Integer.parseInt(EditExecutePlan.GetScheduleRunWeekday())-1));
			}
			
		}
	    window.show();
	    
	}
	
}
