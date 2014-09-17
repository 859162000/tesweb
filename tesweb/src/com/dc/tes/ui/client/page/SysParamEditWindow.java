package com.dc.tes.ui.client.page;

import java.util.List;

import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.ISysDynamicParameterAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.PathSelectField;
import com.dc.tes.ui.client.control.TreeGridContentPanel;
import com.dc.tes.ui.client.model.GWTHost;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SysParamEditWindow{

	private static ISysDynamicParameterAsync sysParaService =  ServiceHelper.GetDynamicService("sysPara", ISysDynamicParameter.class);
	private static PathSelectField<GWTParameterDirectory> pathSelectField = null;
	
	public static void CreateEditForm(final GWTSysDynamicPara EditPara,	final TreeGridContentPanel<BaseTreeModel> panel, 
		final String systemID, final Integer loginLogId, final boolean allowPathSelect) 
	{
		final Integer dir = EditPara.GetDirectoryID();
		final Window window = new Window();
		window.setSize(400, 500);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		formPanel.setPadding(5);
		
		FormData formdata = new FormData("90%");
		
		final DistTextField paraName = new DistTextField(EditPara,EditPara.getParameterName(),"参数名","已经存在该参数名");
		paraName.setName("parameterName");
		paraName.setMaxLength(32);
		paraName.setValue(EditPara.getParameterName());
		formPanel.add(paraName,formdata);
		
		if(allowPathSelect){
			pathSelectField = new PathSelectField<GWTParameterDirectory>("参数路径",
							SelectionMode.SINGLE, false){
				@Override
				protected boolean HasChildren(
						GWTParameterDirectory parent) {
					// TODO Auto-generated method stub
					return true;
				}
			};
			pathSelectField.setAllowBlank(false);
			pathSelectField.setReadOnly(true);
			RpcProxy<List<GWTParameterDirectory>> proxy = new RpcProxy<List<GWTParameterDirectory>>() {

				@Override
				protected void load(Object loadConfig,
						AsyncCallback<List<GWTParameterDirectory>> callback) {
					// TODO Auto-generated method stub
					sysParaService.GetParamDirTree((GWTParameterDirectory)loadConfig, systemID, callback);
				}
			};
			pathSelectField.setTextFieldWidth(200);
			pathSelectField.setProxy(proxy);
			formPanel.add(pathSelectField, formdata);
		}
		
		final TextField<String> paraDesc = new TextField<String>();
		paraDesc.setName(GWTSysDynamicPara.N_ParameterDesc);
		paraDesc.setAllowBlank(true);
		paraDesc.setFieldLabel("参数描述");
		paraDesc.setValue(EditPara.getParameterDesc());
		formPanel.add(paraDesc,formdata);
		
		final ComboBox<GWTStock> paraTypeBox = new ComboBox<GWTStock>();
		paraTypeBox.setEditable(false);
		paraTypeBox.setName("ParaType");
		paraTypeBox.setFieldLabel("参数类型");
		paraTypeBox.setValueField(GWTStock.N_Name);
		paraTypeBox.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> store = new ListStore<GWTStock>();
		store.add(new GWTStock("报文类参数","0"));
		store.add(new GWTStock("SQL参数","1"));
		//store.add(new GWTStock("交易数据类参数","2"));
		store.add(new GWTStock("函数处理类参数","3"));
		store.add(new GWTStock("条件分支类参数","4"));
		paraTypeBox.setStore(store);
		paraTypeBox.setAllowBlank(false);
		paraTypeBox.setTriggerAction(TriggerAction.ALL);
		paraTypeBox.setValue(new GWTStock(EditPara.getParameterTypeStr(),EditPara.getParameterType()));		
		formPanel.add(paraTypeBox,formdata);
		
		final ComboBox<GWTStock> conditionBox = new ComboBox<GWTStock>();
		conditionBox.setEditable(false);
		conditionBox.setName("condition");
		conditionBox.setFieldLabel("匹配条件");
		conditionBox.setValueField(GWTStock.N_Name);
		conditionBox.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> conditionStore = new ListStore<GWTStock>();
		conditionStore.add(new GWTStock("完全一样","0"));
		conditionStore.add(new GWTStock("实际值中包含有预期值","1"));
		conditionStore.add(new GWTStock("预期值中包含有实际值","2"));
		conditionBox.setStore(conditionStore);
		conditionBox.setTriggerAction(TriggerAction.ALL);
		conditionBox.setAllowBlank(false);
		conditionBox.setValue(new GWTStock(EditPara.getCompareConditionStr(),EditPara.getCompareCondition()));		
		formPanel.add(conditionBox,formdata);
		
		
		final ComboBox<GWTStock> hostBox = new ComboBox<GWTStock>();
		hostBox.setEditable(false);
		hostBox.setName("HostType");
		hostBox.setFieldLabel("参数所在机器");
		hostBox.setValueField(GWTStock.N_Name);
		hostBox.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> hostStore = new ListStore<GWTStock>();
		hostStore.add(new GWTStock("默认机器","0"));
		hostStore.add(new GWTStock("指定机器","1"));
		hostStore.add(new GWTStock("卡所指定机器","2"));
		hostBox.setStore(hostStore);
		hostBox.setTriggerAction(TriggerAction.ALL);
		hostBox.setAllowBlank(false);
		hostBox.setValue(new GWTStock(EditPara.getParameterHostTypeStr(),EditPara.getParameterHostType()));			
		formPanel.add(hostBox,formdata);
		
		final ComboBox<GWTHost> ipBox = new ComboBox<GWTHost>();
		ipBox.setEditable(false);		
		RpcProxy<List<GWTHost>> proxy = new RpcProxy<List<GWTHost>>() {

				@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GWTHost>> callback) {
				// TODO Auto-generated method stub
				sysParaService.GetHostList(systemID,callback);
			}
				
		};
		BaseListLoader<ListLoadResult<GWTHost>> loader = new BaseListLoader<ListLoadResult<GWTHost>>(proxy);
		ListStore<GWTHost> ipStore = new ListStore<GWTHost>(loader);
			
		ipBox.setName("HostIp");
		ipBox.setFieldLabel("主机IP");
		ipBox.setValueField(GWTHost.N_Ipaddress);
		ipBox.setDisplayField(GWTHost.N_Ipaddress);
		ipBox.setStore(ipStore);
		ipBox.setValue(new GWTHost(EditPara.getParameterHostId(),EditPara.getParameterHostIP(),EditPara.getParameterHostPort()));			
		formPanel.add(ipBox,formdata);
		
		
		//当参数所在机器不是指定机器时，无法配置主机IP
		hostBox.addSelectionChangedListener(new SelectionChangedListener<GWTStock>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<GWTStock> se) {
				// TODO Auto-generated method stub
				if(se.getSelectedItem() != null) {
					if(!se.getSelectedItem().getPos().equalsIgnoreCase("1")) {
						ipBox.setAllowBlank(true);
						ipBox.setEnabled(false);
						ipBox.setValue(null);
					}
					else {
						ipBox.setAllowBlank(false);
						ipBox.setEnabled(true);
					}
				}
			}
			
		});
		hostBox.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(hostBox.getValue() != null) {
					if(!hostBox.getValue().getPos().equalsIgnoreCase("1")) {
						ipBox.setAllowBlank(true);
						ipBox.setEnabled(false);
						ipBox.setValue(null);
					}
					else {
						ipBox.setAllowBlank(false);
						ipBox.setEnabled(true);
					}
				}
			}
		});
		
	    Radio radio = new Radio();  
		radio.setBoxLabel("是");  		
		radio.setValue(true);  
		radio.setData("value", "1");
		Radio radio2 = new Radio();  
		radio2.setBoxLabel("否");  
		radio2.setData("value", "0");
		final RadioGroup radioGroup = new RadioGroup();  
		radioGroup.setFieldLabel("界面显示");  
		radioGroup.add(radio);  
		radioGroup.add(radio2);
		radioGroup.setSpacing(42);
		FormData style = new FormData();
		style.setWidth(150);
		radioGroup.setValue(EditPara.getDisplayFlag().equalsIgnoreCase("1")?radio:radio2);			
		formPanel.add(radioGroup, style); 

	    Radio radio3 = new Radio();  
		radio3.setBoxLabel("是");  		
		radio3.setValue(true);  
		radio3.setData("value", "1");
		Radio radio4 = new Radio();  
		radio4.setBoxLabel("否");  
		radio4.setData("value", "0");
		final RadioGroup radioGroup2 = new RadioGroup();  
		radioGroup2.setFieldLabel("是否有效");  
		radioGroup2.add(radio3);  
		radioGroup2.add(radio4);
		radioGroup2.setSpacing(42);
		radioGroup2.setValue(EditPara.getIsValid().equalsIgnoreCase("1")?radio3 :radio4);			
		formPanel.add(radioGroup2, style); 
		
	    Radio radio5 = new Radio();  
		radio5.setBoxLabel("请求报文");  		
		radio5.setValue(true);  
		radio5.setData("value", "1");
		Radio radio6 = new Radio();  
		radio6.setBoxLabel("响应报文");  
		radio6.setData("value", "2");
		final RadioGroup radioGroup3 = new RadioGroup();  
		radioGroup3.setFieldLabel("报文来源");  
		radioGroup3.add(radio5);  
		radioGroup3.add(radio6);
		radioGroup3.setSpacing(6);
		
		//当为报文类参数时才需选择是上传报文或下传报文
		paraTypeBox.addSelectionChangedListener(new SelectionChangedListener<GWTStock>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<GWTStock> se) {
				// TODO Auto-generated method stub
				if(se.getSelectedItem() != null) {
					if(!se.getSelectedItem().getPos().equalsIgnoreCase("0")) {
						radioGroup3.setEnabled(false);
					}
					else {
						radioGroup3.setEnabled(true);
					}
				}
			}
			
		});
		paraTypeBox.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(paraTypeBox.getValue() != null) {
					if(!paraTypeBox.getValue().getPos().equalsIgnoreCase("0")) {
						radioGroup3.setEnabled(false);
					}
					else {
						radioGroup3.setEnabled(true);
					}
				}
			}
		});
		radioGroup3.setValue(EditPara.getParamFromMsgSrc().equalsIgnoreCase("1")? radio5 :radio6);			
		formPanel.add(radioGroup3, style); 
		
//	    Radio radio5 = new Radio();  
//		radio5.setBoxLabel("是");  		
//		radio5.setValue(true);  
//		radio5.setData("value", "1");
//		Radio radio6 = new Radio();  
//		radio6.setBoxLabel("否");  
//		radio6.setData("value", "0");
//		final RadioGroup radioGroup3 = new RadioGroup();  
//		radioGroup3.setFieldLabel("关键字段");  
//		radioGroup3.add(radio5);  
//		radioGroup3.add(radio6);
//		radioGroup3.setSpacing(50);
//		radioGroup3.setValue(EditPara.getIsKeyMsgField().equalsIgnoreCase("1")?radio5 :radio6);
//		formPanel.add(radioGroup3, style); 

		
	    Radio radio7 = new Radio();  
		radio7.setBoxLabel("是");  		
		radio7.setValue(true);  
		radio7.setData("value", "1");
		Radio radio8 = new Radio();  
		radio8.setBoxLabel("否");  
		radio8.setData("value", "0");
		final RadioGroup radioGroup4 = new RadioGroup();  
		radioGroup4.setFieldLabel("回溯获取");  
		radioGroup4.add(radio7);  
		radioGroup4.add(radio8);
		radioGroup4.setSpacing(42);
		radioGroup4.setValue(EditPara.getRefetchFlag().equalsIgnoreCase("1")?radio7:radio8);
		formPanel.add(radioGroup4, style); 
		
	/*	final ComboBox<GWTStock> cbRefetchMethod = new ComboBox<GWTStock>();
		cbRefetchMethod.setEditable(false);
		cbRefetchMethod.setName("RefatchMethod");
		cbRefetchMethod.setFieldLabel("回溯方式");
		cbRefetchMethod.setValueField(GWTStock.N_Name);
		cbRefetchMethod.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> rmListStore = new ListStore<GWTStock>();
		rmListStore.add(new GWTStock("使用原有参数进行查询","0"));
		rmListStore.add(new GWTStock("使用新参数进行查询","1"));
		rmListStore.add(new GWTStock("新老参数各查询一遍","2"));
		cbRefetchMethod.setStore(rmListStore);
		
		formPanel.add(cbRefetchMethod,formdata);
		

		radioGroup4.addListener(Events.Render, new Listener<BaseEvent>() {
		
			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(radioGroup4.getValue().getData("value").equals("0")){
					cbRefetchMethod.setAllowBlank(true);
					cbRefetchMethod.setEnabled(false);
					cbRefetchMethod.setValue(null);
				}else{
					cbRefetchMethod.setEnabled(true);
					cbRefetchMethod.setAllowBlank(false);
					cbRefetchMethod.setValue(new GWTStock(EditPara.GetRefetchMethodStr(),EditPara.GetRefetchMethod()));
				}
			}
			
		});
		radioGroup4.addListener(Events.Change, new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(radioGroup4.getValue().getData("value").equals("0")){
					cbRefetchMethod.setAllowBlank(true);
					cbRefetchMethod.setEnabled(false);
					cbRefetchMethod.setValue(null);
				}else{
					cbRefetchMethod.setEnabled(true);
					cbRefetchMethod.setAllowBlank(false);
					cbRefetchMethod.setValue(new GWTStock(EditPara.GetRefetchMethodStr(),EditPara.GetRefetchMethod()));
				}
			}
			
		});
		
		*/
		
		final TextField<String> paraDefaultValue = new TextField<String>();
		paraDefaultValue.setName(GWTSysDynamicPara.N_DefaultExpectedValue);
		paraDefaultValue.setAllowBlank(true);
		paraDefaultValue.setFieldLabel("默认预期值");
		paraDefaultValue.setValue(EditPara.getDefaultExpectedValue());
		formPanel.add(paraDefaultValue,formdata);	
		
		final TextArea paraExpress = new TextArea();
		paraExpress.setName(GWTSysDynamicPara.N_ParameterExpression);
		paraExpress.setAllowBlank(true);
		paraExpress.setFieldLabel("参数表达式");
		paraExpress.setValue(EditPara.getParameterExpression());
		formPanel.add(paraExpress,formdata);		
		
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				Integer dirId = dir;
				if(allowPathSelect){
					dirId = pathSelectField.getSelectedItems().get(0).GetID();
				}
				EditPara.SetValue(paraName.getValue(),paraDesc.getValue()==null?"":paraDesc.getValue(),
						paraTypeBox.getValue().getPos(), conditionBox.getValue().getPos(),
						hostBox.getValue().getPos(),ipBox.getValue()==null?null:(String)ipBox.getValue().get(GWTHost.N_Hostid),
						(String)radioGroup.getValue().getData("value"),(String)radioGroup2.getValue().getData("value"),
						(String)radioGroup4.getValue().getData("value"),
						paraDefaultValue.getValue()==null?"":paraDefaultValue.getValue(),
						paraExpress.getValue()==null?"":paraExpress.getValue(),EditPara.GetRefetchMethod(),
								(String)radioGroup3.getValue().getData("value"), dirId);
				
				sysParaService.SaveSysDynamicPara(EditPara, loginLogId,
						new AsyncCallback<GWTSysDynamicPara>() {
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
								MessageBox.alert("错误信息", "保存失败", null);
							}
						
							public void onSuccess(GWTSysDynamicPara result) {
								final boolean isNew = EditPara.IsNew();
								if(panel != null){
									final BaseTreeModel folder = panel.getTreeGrid().getSelectionModel()
									.getSelectedItem();
									if (isNew)
										panel.getStore()
												.add(folder instanceof GWTParameterDirectory ? folder
														: panel.getStore()
																.getParent(folder),
														result, false);
									else {
										panel.getStore().update(EditPara);
									}
									panel.getTreeGrid().setExpanded(folder, true);
									
									// 打开用例信息
									panel.getTreeGrid().getSelectionModel()
											.deselectAll();
									panel.getTreeGrid().getSelectionModel().select(
											result, true);
								}
								if(allowPathSelect){
									MessageBox.info("提示", "添加成功", null);
								}
								window.hide();
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
		
		if (EditPara.IsNew()) {
			window.setHeading("新增系统动态参数");
		} else {									
			window.setHeading("编辑系统动态参数");
		}
		
		window.show();
	}
}
