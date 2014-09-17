package com.dc.tes.ui.client.page;

import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ISimuSystemService;
import com.dc.tes.ui.client.ISimuSystemServiceAsync;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.model.GWTMsgType;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTStock;
import com.extjs.gxt.ui.client.data.BaseLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SimuSystemDialog extends Window{
	ISimuSystemServiceAsync systemService = null;
	GWTSimuSystem EditSystem = null;
	PagingLoader<PagingLoadResult<ModelData>> loader = null;
	private boolean isCurSys = false;
	private Integer loginLogID = null;
	public SimuSystemDialog()
	{
		this(new GWTSimuSystem(),null, null);
	}
	
	public SimuSystemDialog(GWTSimuSystem editSystem,
			PagingLoader<PagingLoadResult<ModelData>> loader, Integer loginLogId)
	{
		systemService = ServiceHelper.GetDynamicService("simuSys", ISimuSystemService.class);
		this.EditSystem = editSystem;
		this.loader = loader;
		this.loginLogID = loginLogId;
		InitDialog();
	}

	private LabelField getFormStyleLable(String labelText)
	{
		if(!labelText.isEmpty())
			labelText = labelText + ":";
		LabelField label = new LabelField(labelText);
		label.setWidth(75);
		label.setStyleName("x-form-item-label");
		label.setStyleAttribute("font", "normal 12px tahoma, arial, helvetica, sans-serif");
		return label;
	}

	@SuppressWarnings("rawtypes")
	private HorizontalPanel getHPanel(Field leftControl,Field rightControl)
	{
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		hPanel.setStyleAttribute("margin-bottom", "5px");
		
		hPanel.add(getFormStyleLable(leftControl.getFieldLabel()));
		
		leftControl.setWidth(142);
		hPanel.add(leftControl);
		
		LabelField lbRight = getFormStyleLable(rightControl.getFieldLabel());
		lbRight.setStyleAttribute("margin-left", "30px");
		hPanel.add(lbRight);
		
		rightControl.setWidth(142);
		hPanel.add(rightControl);
		
		return hPanel;
	}
	
	private void InitDialog()
	{
		setSize(500, 460);
		setPlain(true);
		setModal(true);
		setBlinkModal(false);
		setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		
		String labelStyle = "width:70px;";
		FormData formdata = new FormData("95%");
		
		final DistTextField tfSystemName = new DistTextField(EditSystem,EditSystem.GetSystemName(),"系统名称");
		tfSystemName.setLabelStyle(labelStyle);
		tfSystemName.setMaxLength(32);
//		tfSystemName.setToolTip("正在使用当前系统，名称无法修改");
		isCurSys = !EditSystem.IsNew() && CookieManage.GetSimuSystemID().equalsIgnoreCase(EditSystem.GetSystemID());
//		tfSystemName.setEnabled(!EditSystem.IsNew() && CookieManage.GetSimuSystemID() == EditSystem.GetSystemID());
//		tfSystemName.setReadOnly(isCurSys);
		formPanel.add(tfSystemName, formdata);
		
		final TextField<String> tfSystemNo = new TextField<String>();
		tfSystemNo.setFieldLabel("系统号");
		tfSystemNo.setLabelStyle(labelStyle);
		tfSystemNo.setMaxLength(32);
		formPanel.add(tfSystemNo,formdata);
		
//		final TextField<String> tfChanel = new TextField<String>();
//		tfChanel.setFieldLabel("通道名称");
//		tfChanel.setLabelStyle(labelStyle);
//		tfChanel.setMaxLength(32);
//		formPanel.add(getHPanel(tfChanel, tfSystemNo));
		
		final RequireTextField tfIP = new RequireTextField("核心IP");
		tfIP.setRegex("((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))");
		tfIP.getMessages().setRegexText("格式错误 ");
		tfIP.setSelectOnFocus(true);
		
		final NumberField tfPort = new NumberField();
		tfPort.setMaxLength(5);
		tfPort.setAllowBlank(false);
		tfPort.setMinValue(0);
		tfPort.setFieldLabel("核心端口");
		tfPort.setSelectOnFocus(true);
		
		formPanel.add(getHPanel(tfIP, tfPort));
		
		Radio radio = new Radio();  
		radio.setBoxLabel("是");  		  
		radio.setData("value", "1");
		Radio radio2 = new Radio();  
		radio2.setBoxLabel("否");  
		radio2.setData("value", "0");
		final RadioGroup rgNeedSqlCheck = new RadioGroup();  
		rgNeedSqlCheck.setFieldLabel("SQL查询");  
		rgNeedSqlCheck.add(radio);  
		rgNeedSqlCheck.add(radio2);
		rgNeedSqlCheck.setSpacing(20);
		
		final NumberField tfTranTimeOut = new NumberField();
		tfTranTimeOut.setFieldLabel("交易超时");
		tfTranTimeOut.setMaxLength(5);
		tfTranTimeOut.setAllowBlank(false);
		tfTranTimeOut.setMinValue(0);
		formPanel.add(getHPanel(rgNeedSqlCheck, tfTranTimeOut));
		
		Radio radio3 = new Radio();  
		radio3.setBoxLabel("TCP");  		  
		radio3.setData("value", "0");
		Radio radio4 = new Radio();  
		radio4.setBoxLabel("JDBC");  
		radio4.setData("value", "1");
		final RadioGroup rgSqlGetMethod = new RadioGroup();
		rgSqlGetMethod.setFieldLabel("查询方式");
		rgSqlGetMethod.add(radio3);
		rgSqlGetMethod.add(radio4);
		rgSqlGetMethod.setSpacing(10);
		
		final TextField<String> tfSqlGetDbAddr = new TextField<String>();
		tfSqlGetDbAddr.setFieldLabel("默认机器");
		tfSqlGetDbAddr.setMaxLength(17);
		tfSqlGetDbAddr.setAllowBlank(true);
		formPanel.add(getHPanel(rgSqlGetMethod, tfSqlGetDbAddr));
		
		rgNeedSqlCheck.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(rgNeedSqlCheck.getValue().getData("value").equals("0")){
					rgSqlGetMethod.setEnabled(false);
					tfSqlGetDbAddr.setEnabled(false);
				}else{
					rgSqlGetMethod.setEnabled(true);
					tfSqlGetDbAddr.setEnabled(true);
				}	
			}
		});
		rgNeedSqlCheck.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(rgNeedSqlCheck.getValue().getData("value").equals("0")){
					rgSqlGetMethod.setEnabled(false);
					tfSqlGetDbAddr.setEnabled(false);
				}else{
					rgSqlGetMethod.setEnabled(true);
					tfSqlGetDbAddr.setEnabled(true);
				}	
			}
		});
		
		final TextField<String> tfEncoding4ReqMsg = new TextField<String>();
		tfEncoding4ReqMsg.setFieldLabel("请求报文编码");
		final TextField<String> tfEncoding4ResMsg = new TextField<String>();
		tfEncoding4ResMsg.setFieldLabel("响应报文编码");
		formPanel.add(getHPanel(tfEncoding4ReqMsg, tfEncoding4ResMsg));

		
		//通讯方式
		Radio radio7 = new Radio();  
		radio7.setBoxLabel("同步");  		  
		radio7.setData("value", "1");
		Radio radio8 = new Radio();  
		radio8.setBoxLabel("异步");  
		radio8.setData("value", "0");
		final RadioGroup rgCommType = new RadioGroup();
		rgCommType.setFieldLabel("通讯方式");
		rgCommType.add(radio7);
		rgCommType.add(radio8);
		rgCommType.setSpacing(2);
		
		
		//统一响应报文
		Radio radio9 = new Radio();  
		radio9.setBoxLabel("是");  		  
		radio9.setData("value", "1");
		Radio radio10 = new Radio();  
		radio10.setBoxLabel("否");  
		radio10.setData("value", "0");
		final RadioGroup rgSameStruct = new RadioGroup();
		rgSameStruct.setFieldLabel("统一响应报文");	
		rgSameStruct.add(radio9);
		rgSameStruct.add(radio10);
		rgSameStruct.setSpacing(26);
		//formPanel.add(getHPanel(rgSameStruct, field));
		//formPanel.add(getHPanel(rgSysType, rgCommType));
		formPanel.add(getHPanel(rgCommType, rgSameStruct));
		
		//系统类型
		final Radio rdClient = new Radio();  
		rdClient.setBoxLabel("客户端");  		  
		rdClient.setData("value", "1");
		final Radio rdRecv = new Radio();  
		rdRecv.setBoxLabel("服务端");  
		rdRecv.setData("value", "0");
		final Radio rdBoth = new Radio();  
		rdBoth.setBoxLabel("双向模拟");  
		rdBoth.setData("value", "2");
		
		final RadioGroup rgSysType = new RadioGroup();
		rgSysType.setFieldLabel("系统类型");
		rgSysType.setLabelStyle(labelStyle);
		rgSysType.add(rdClient);
		rgSysType.add(rdRecv);
		rgSysType.add(rdBoth);
		rgSysType.setSpacing(2);
		formPanel.add(rgSysType, formdata);
		//formPanel.add(getHPanel(rgSysType, field));
		//formPanel.add(rgSysType, formdata);
					
		final ComboBox<GWTMsgType> reqMsgTypeBox = new ComboBox<GWTMsgType>();
		reqMsgTypeBox.setEditable(false);
		reqMsgTypeBox.setFieldLabel("发起方拆包");
		reqMsgTypeBox.setToolTip("发起方请求报文拆包,用于案例原始报文上传");
		reqMsgTypeBox.setLabelStyle(labelStyle);
		reqMsgTypeBox.setDisplayField(GWTMsgType.N_StyleName);
		reqMsgTypeBox.setValueField(GWTMsgType.N_ComponentId);
		final ListStore<GWTMsgType> msgTypeStore = new ListStore<GWTMsgType>();
		reqMsgTypeBox.setStore(msgTypeStore);
		reqMsgTypeBox.setAllowBlank(true);
		reqMsgTypeBox.setTriggerAction(TriggerAction.ALL);
		
		final ComboBox<GWTMsgType> resMsgTypeBox = new ComboBox<GWTMsgType>();
		resMsgTypeBox.setEditable(false);
		resMsgTypeBox.setFieldLabel("接收方拆包");
		resMsgTypeBox.setToolTip("接收方响应报文拆包,用于案例原始报文上传");
		resMsgTypeBox.setLabelStyle(labelStyle);
		resMsgTypeBox.setDisplayField(GWTMsgType.N_StyleName);
		resMsgTypeBox.setValueField(GWTMsgType.N_ComponentId);
		resMsgTypeBox.setStore(msgTypeStore);
		resMsgTypeBox.setAllowBlank(true);
		resMsgTypeBox.setTriggerAction(TriggerAction.ALL);
		formPanel.add(getHPanel(reqMsgTypeBox, resMsgTypeBox));
		systemService.getUnPackerList(new AsyncCallback<List<GWTMsgType>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<GWTMsgType> result) {
				// TODO Auto-generated method stub
				msgTypeStore.add(result);
				reqMsgTypeBox.setValue(EditSystem.GetReqUnPackerID()==null?null:msgTypeStore.findModel(GWTMsgType.N_ComponentId, EditSystem.GetReqUnPackerID()));
				resMsgTypeBox.setValue(EditSystem.GetResUnPackerID()==null?null:msgTypeStore.findModel(GWTMsgType.N_ComponentId, EditSystem.GetResUnPackerID()));
			}
		});
		
		
		//应答模式
		final ComboBox<GWTStock> responseModeBox = new ComboBox<GWTStock>();
		responseModeBox.setEditable(false);
		responseModeBox.setLabelStyle(labelStyle);
		responseModeBox.setName("ResponseMode");
		responseModeBox.setFieldLabel("应答模式");
		responseModeBox.setValueField(GWTStock.N_Name);
		responseModeBox.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> store = new ListStore<GWTStock>();
		store.add(new GWTStock("使用默认案例的应答报文","0"));
		store.add(new GWTStock("使用交易的应答报文报文","1"));
		store.add(new GWTStock("根据录制报文匹配返回","2"));
		store.add(new GWTStock("根据案例实例匹配返回","3"));
		responseModeBox.setStore(store);
		responseModeBox.setAllowBlank(false);
		responseModeBox.setTriggerAction(TriggerAction.ALL);
		//responseModeBox.setValue(new GWTStock(EditSystem.GetResponseModeStr(),EditSystem.GetResponseMode()));		
		formPanel.add(responseModeBox,formdata);
		//formPanel.add(getHPanel(rgSysType, responseModeBox));
		rgSysType.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(rgSysType.getValue().getData("value").equals("1")){
					responseModeBox.setEnabled(false);
				}else{
					responseModeBox.setEnabled(true);
				}
			}
		});
		
		
		//备注
		final TextArea tfDesc = new TextArea();
		tfDesc.setLabelStyle(labelStyle);
		tfDesc.setMaxLength(256);
		tfDesc.setHeight(80);
		tfDesc.setFieldLabel("备注");
		formPanel.add(tfDesc, formdata);
		
		//按钮
		Button btnCopySystem = new Button("复制系统数据");
		btnCopySystem.addSelectionListener(AddHandler());
		addButton(btnCopySystem);
		
		if (EditSystem.IsNew()) {
			btnCopySystem.setVisible(false);
		}
		else {
			btnCopySystem.setVisible(true);
		}

		
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				EditSystem.SetValue(tfSystemNo.getValue(), tfSystemName.getValue(),
						tfDesc.getValue(),tfIP.getValue(),tfPort.getValue().intValue(),EditSystem.GetChanel(),
						Integer.parseInt(rgNeedSqlCheck.getValue().getData("value").toString()),tfTranTimeOut.getValue().intValue(),
						Integer.parseInt(rgSqlGetMethod.getValue().getData("value").toString()),tfSqlGetDbAddr.getValue(),
						tfEncoding4ReqMsg.getValue(), tfEncoding4ResMsg.getValue(),
						Integer.parseInt(rgSysType.getValue().getData("value").toString()),
						Integer.parseInt(rgCommType.getValue().getData("value").toString()),
						Integer.parseInt(rgSameStruct.getValue().getData("value").toString()),
						responseModeBox.getValue().getPos(),
						reqMsgTypeBox.getValue()==null?null:reqMsgTypeBox.getValue().get(GWTMsgType.N_ComponentId).toString(),
						resMsgTypeBox.getValue()==null?null:resMsgTypeBox.getValue().get(GWTMsgType.N_ComponentId).toString());
				//EditSystem.SetResponseMode(responseModeBox.getValue().getPos());
				systemService.Save(EditSystem, loginLogID, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						MessageBox.alert("错误信息", "保存失败", null);
					}

					public void onSuccess(Boolean suc) {
						if(loader != null)
							loader.load();
						if(suc)
						{
							hide();
							if(loader == null)
							{
								CookieManage.Login();
								AppContext.GetEntryPoint().Login();
							}
							else
							{
								//更新当前系统
								if(isCurSys)
								{
									AppContext.GetEntryPoint().comboSystem.setRawValue(tfSystemName.getValue());
									AppContext.GetEntryPoint().comboSystem.getSelection().get(0).SetValue(tfSystemNo.getValue(), 
											tfSystemName.getValue(),tfDesc.getValue(),tfIP.getValue(),
											tfPort.getValue().intValue(),EditSystem.GetChanel(),
											Integer.parseInt(rgNeedSqlCheck.getValue().getData("value").toString()),tfTranTimeOut.getValue().intValue(),
											Integer.parseInt(rgSqlGetMethod.getValue().getData("value").toString()),tfSqlGetDbAddr.getValue(),
											tfEncoding4ReqMsg.getValue(), tfEncoding4ResMsg.getValue(),
											Integer.parseInt(rgSysType.getValue().getData("value").toString()),
											Integer.parseInt(rgCommType.getValue().getData("value").toString()),
											Integer.parseInt(rgSameStruct.getValue().getData("value").toString()),
											responseModeBox.getValue().getPos(),
											reqMsgTypeBox.getValue()==null?null:reqMsgTypeBox.getValue().get(GWTMsgType.N_ComponentId).toString(),
											reqMsgTypeBox.getValue()==null?null:resMsgTypeBox.getValue().get(GWTMsgType.N_ComponentId).toString());
								}
								SimuSystemPage.ReloadParent();
							}
						}
						else
						{
							tfSystemName.focus();
							tfSystemName.EnforceValidate();
						}
					}
				});
			}
		});
		addButton(btnOK);

		addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				close();
			}
		}));
		add(formPanel);

		if (EditSystem.IsNew()) {
			setHeading("新增模拟系统");
			tfPort.setValue(EditSystem.GetPort());
			tfTranTimeOut.setValue(EditSystem.GetTransactionTimeOut());
			rgNeedSqlCheck.setValue(EditSystem.GetNeedSqlCheck()==1?radio:radio2);
			rgSqlGetMethod.setValue(EditSystem.GetSqlGetMethod()==0?radio3:radio4);
			rgSysType.setValue(EditSystem.GetIsClient()==1? rdClient:rdRecv);
			rgCommType.setValue(EditSystem.GetIsSync()==1? radio7:radio8);
			rgSameStruct.setValue(EditSystem.GetUseSameStruct()==1?radio9:radio10);
		} else {
			tfSystemNo.setValue(EditSystem.GetSystemNo());
			tfSystemName.setValue(EditSystem.GetSystemName());
			tfDesc.setValue(EditSystem.GetDesc());
			tfIP.setValue(EditSystem.GetIP());
			tfPort.setValue(EditSystem.GetPort());
			tfTranTimeOut.setValue(EditSystem.GetTransactionTimeOut());
			rgNeedSqlCheck.setValue(EditSystem.GetNeedSqlCheck()==1?radio:radio2);
			rgSqlGetMethod.setValue(EditSystem.GetSqlGetMethod()==0?radio3:radio4);
			tfSqlGetDbAddr.setValue(EditSystem.GetSqlGetDbAddr());
			tfEncoding4ReqMsg.setValue(EditSystem.GetEncoding4RequestMsg());
			tfEncoding4ResMsg.setValue(EditSystem.GeteEnoding4ResponseMsg());
			rgSysType.setValue(EditSystem.GetIsClient()==1? rdClient:rdRecv);
			rgCommType.setValue(EditSystem.GetIsSync()==1? radio7:radio8);
			rgSameStruct.setValue(EditSystem.GetUseSameStruct()==1?radio9:radio10);
			responseModeBox.setValue(new GWTStock(EditSystem.GetResponseModeStr(),EditSystem.GetResponseMode()));
			setHeading("编辑模拟系统");
		}
			
		show();
	}
	
	
	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				CreateCopySystemDialog();
			}
		};
	}
	
	private void CreateCopySystemDialog() {
		int iCurrSystemId = Integer.parseInt(EditSystem.GetSystemID());
		new CopySystemDialog(iCurrSystemId, EditSystem, this.loader);
	}
}
