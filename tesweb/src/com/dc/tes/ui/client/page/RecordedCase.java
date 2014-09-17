package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IRecordedCaseService;
import com.dc.tes.ui.client.IRecordedCaseServiceAsync;
import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;

import com.dc.tes.ui.client.model.GWTRecordedCase;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;



public class RecordedCase extends BasePage {
	
	private GridContentPanel<GWTRecordedCase> panel;
	private FormContentPanel<GWTRecordedCase> detailPanel;
	private ConfigToolBar configBar;
	private IRecordedCaseServiceAsync recordedCaseService;
	private GWTRecordedCase gwtRecordedCase;
	private Window outsideContainerWindow;
	
	PagingLoadConfig loadConfig;
	Button dateSelectBtn = new Button("录制时间");
	Button buttonOk = new Button("确定");
	
	private TabPanel tabPanel = null;
	
	public RecordedCase() {
	}
	
	public RecordedCase(TabPanel tp) {
		tabPanel = tp;
	}
	
	public void SetGWTRecordedCase(GWTRecordedCase recordedCase) {
		gwtRecordedCase = recordedCase;
	}
	
	public GWTRecordedCase GetGWTRecordedCase() {
		return gwtRecordedCase;
	}
	
	public void SetOutsideContainerWindow(Window window) {
		outsideContainerWindow = window;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		loadConfig = new BasePagingLoadConfig();
		loadConfig.setLimit(10);
		
		panel = new GridContentPanel<GWTRecordedCase>();

		recordedCaseService = ServiceHelper.GetDynamicService("recordedCase", IRecordedCaseService.class);
		RpcProxy<PagingLoadResult<GWTRecordedCase>> proxy = new RpcProxy<PagingLoadResult<GWTRecordedCase>>() {
			@Override
			public void load(Object loadConfig, AsyncCallback<PagingLoadResult<GWTRecordedCase>> callback) {
				recordedCaseService.GetGWTRecordedCasePageList(GetSystemID(), panel.GetSearchCondition(), (PagingLoadConfig)loadConfig,callback);
			}
		};

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		
		//添加根据用户过滤结果
	    final ComboBox<GWTUser> userComboBox = new ComboBox<GWTUser>();
	    //userComboBox.setEditable(false);
	    userComboBox.setDisplayField(GWTUser.N_name);
	    userComboBox.setValueField(GWTUser.N_id);
	    final ListStore<GWTUser> userList = new ListStore<GWTUser>();
	    userList.add(new GWTUser("-1", "所有用户", "","", 0, 0));

	    IUserServiceAsync userService = ServiceHelper.GetDynamicService("user", IUserService.class);
	    userService.GetUserBySystem(GetSysInfo().GetSystemID(), new AsyncCallback<List<GWTUser>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<GWTUser> result) {
				userList.add(result);
			}
		});
	    
	    userComboBox.setStore(userList);
	    userComboBox.setValue(userList.getAt(0));
	    userComboBox.setTriggerAction(TriggerAction.ALL);
	    userComboBox.addListener(Events.SelectionChange, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				loadConfig.set("user", userComboBox.getValue().getUserID());
				panel.getLoader().load(loadConfig);
			}
		});
		panel.getSearchBar().add(userComboBox);

		//panel.DrowGridView();
		panel.DrowGridView("", true, true);
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());

		configBar.AddButton("btnDate", dateSelectBtn, MainPage.ICONS.DatCom(), DateHandler());
		
		configBar.AddWidget(new FillToolItem());
		configBar.AddButton("btnOk", buttonOk, MainPage.ICONS.RightField(), PickUpRecodedMsgHandler());
		if (tabPanel == null) {
			buttonOk.setVisible(false);
		}
		else {
			buttonOk.setVisible(true);
		}
		
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTRecordedCase>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}
	

	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTRecordedCase.N_RequestMsg, "请求报文", 180));
		columns.add(new ColumnConfig(GWTRecordedCase.N_ResponseMsg, "应答报文", 180));
		columns.add(new ColumnConfig(GWTRecordedCase.N_ResponseFlagStr, "是否收到了应答报文", 100));
		columns.add(new ColumnConfig(GWTRecordedCase.N_IsCasedStr, "是否生成过案例", 100));
		//columns.add(new ColumnConfig(GWTRecordedCase.N_RecordUserId, "录制用户ID", 100));
		columns.add(new ColumnConfig(GWTRecordedCase.N_RecordUserName, "录制用户", 60));
		columns.add(new ColumnConfig(GWTRecordedCase.N_RecordTime, "录制时间", 120));
		columns.add(new ColumnConfig(GWTRecordedCase.N_Memo, "备注", 200));
		return columns;
	}

	private Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTRecordedCase.N_RequestMsg, "请求报文");
		detailMap.put(GWTRecordedCase.N_ResponseMsg, "应答报文");
		//detailMap.put(GWTRecordedCase.N_IsCased, "是否做过案例化");
		//detailMap.put(GWTRecordedCase.N_ResponseFlag, "应答标志");
		//detailMap.put(GWTRecordedCase.N_RecordTime, "录制时间");
		//detailMap.put(GWTRecordedCase.N_RecordUserId, "录制用户");
		detailMap.put(GWTRecordedCase.N_Memo, "备注");		
		return detailMap;
	}

	private void CreateEditForm() {

		final Window window = new Window();
		
		window.setSize(800, 600);
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
		
		final TextArea tfRequestMsg = new TextArea();
		tfRequestMsg.setFieldLabel("请求报文");
		tfRequestMsg.setHeight(200);
		tfRequestMsg.setLabelStyle(labelStyle);
		formPanel.add(tfRequestMsg, formData);
		tfRequestMsg.setEnabled(false);
		
		final TextArea tfResponseMsg = new TextArea();
		tfResponseMsg.setFieldLabel("应答报文");
		tfResponseMsg.setHeight(200);
		tfResponseMsg.setLabelStyle(labelStyle);
		formPanel.add(tfResponseMsg, formData);
		tfResponseMsg.setEnabled(false);
	
		Radio radio = new Radio();  
		radio.setBoxLabel("是");  		
		radio.setData("value", "1");
		Radio radio1 = new Radio();  
		radio1.setBoxLabel("否");  
		radio1.setData("value", "0");
		final RadioGroup rgResponseFlag = new RadioGroup();  
		rgResponseFlag.setFieldLabel("是否收到了应答报文");  
		rgResponseFlag.add(radio);  
		rgResponseFlag.add(radio1);
		rgResponseFlag.setSpacing(30);
		rgResponseFlag.setEnabled(false);
		//formPanel.add(rgResponseFlag, formData);
		
		Radio radio2 = new Radio();  
		radio2.setBoxLabel("是");  		
		radio2.setData("value", "1");
		Radio radio3 = new Radio();  
		radio3.setBoxLabel("否");  
		radio3.setData("value", "0");
		final RadioGroup rgIsCased = new RadioGroup();  
		rgIsCased.setFieldLabel("是否生成过案例");  
		rgIsCased.add(radio2);  
		rgIsCased.add(radio3);
		rgIsCased.setSpacing(30);
		formPanel.add(getHPanel(rgResponseFlag, rgIsCased));
		
		/*final TextField<String> tfRecordUser = new TextField<String>();
		tfRecordUser.setFieldLabel("录制用户");
		tfRecordUser.setLabelStyle(labelStyle);
		//tfRecordTime.setMaxLength(32);
		formPanel.add(tfRecordUser,formData);
		//tfRecordUser.setEnabled(false);*/
		
		/*final ComboBox<GWTUser> userBox = new ComboBox<GWTUser>();
		//userBox.setEditable(false);		
		RpcProxy<List<GWTUser>> proxy = new RpcProxy<List<GWTUser>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<List<GWTUser>> callback) {
				recordedCaseService.GetUserList(gwtRecordedCase.getSystemID(), callback);
			}		
		};
		
		BaseListLoader<ListLoadResult<GWTUser>> loader = new BaseListLoader<ListLoadResult<GWTUser>>(proxy);
		ListStore<GWTUser> userIdStore = new ListStore<GWTUser>(loader);
			
		userBox.setName("RecordUserId");
		userBox.setFieldLabel("录制用户");
		userBox.setValueField(GWTRecordedCase.N_RecordUserName);
		userBox.setDisplayField(GWTRecordedCase.N_RecordUserName);
		userBox.setStore(userIdStore);
		userBox.setValue(new GWTUser(gwtRecordedCase.GetRecordUserId(), gwtRecordedCase.GetRecordUserName()));			
		formPanel.add(userBox, formData);*/
		
		final TextField<String> tfRecordTime = new TextField<String>();
		tfRecordTime.setFieldLabel("录制时间");
		tfRecordTime.setLabelStyle(labelStyle);
		//tfRecordTime.setMaxLength(32);
		//formPanel.add(tfRecordTime,formData);
		formPanel.add(tfRecordTime,formData);
		tfRecordTime.setEnabled(false);
		//formPanel.add(getHPanel(userBox, tfRecordTime));
		
		final TextArea tfMemo = new TextArea();
		tfMemo.setFieldLabel("备注");
		tfMemo.setHeight(120);
		tfMemo.setLabelStyle(labelStyle);
		formPanel.add(tfMemo, formData);
		

		Button btnOK = new Button("确定",new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {

				gwtRecordedCase.SetValue(tfMemo.getValue(), 
						Integer.parseInt(rgIsCased.getValue().getData("value").toString()));
				
				recordedCaseService.SaveRecordedCase(gwtRecordedCase, GetLoginLogID(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert("错误信息", "保存失败", null);
					}

					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(Boolean suc) {
						panel.loaderReLoad(gwtRecordedCase.IsNew());
						if (suc)
							window.close();
					}
				});
			}
		});
		
		window.addButton(btnOK);

		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		}));
		
		window.add(formPanel);

		if (!gwtRecordedCase.IsNew()) {
			tfRequestMsg.setValue(gwtRecordedCase.getRequestMsg());
			tfResponseMsg.setValue(gwtRecordedCase.getResponseMsg());
			tfRecordTime.setValue(gwtRecordedCase.GetRecordTime());
			tfMemo.setValue(gwtRecordedCase.getMemo());
			rgResponseFlag.setValue(gwtRecordedCase.getResponseFlag()==1?radio:radio1);
			rgIsCased.setValue(gwtRecordedCase.getIsCased()==1?radio2:radio3);
			window.setHeading("编辑录制的交易的备注信息");
		}

		window.show();	
	}
	

	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTRecordedCase> selectedItems = panel.getDataGrid().getSelectionModel().getSelectedItems();
				gwtRecordedCase = selectedItems.get(0);
				try {
					CreateEditForm();
				}
				catch(Exception e) {
					MessageBox.alert("错误提示", "创建'录制的交易编辑'窗口失败：" + e.getMessage(), null);
				}
			}
		};
	}
	
	
	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					recordedCaseService.DeleteRecordedCase(panel.getSelection(), GetLoginLogID(),
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

	
	private SelectionListener<ButtonEvent> PickUpRecodedMsgHandler() {
		
		return new SelectionListener<ButtonEvent>() {
			
		@Override
		public void componentSelected(ButtonEvent ce) {
				List<GWTRecordedCase> gwtRecordedCaseList = (List<GWTRecordedCase>) panel.getSelection();
				if (gwtRecordedCaseList.size() == 1) {
					gwtRecordedCase = gwtRecordedCaseList.get(0); 
					//hide();
					if (outsideContainerWindow != null) {
						outsideContainerWindow.close();
					}
				}
				else {
					gwtRecordedCase = null;
				}
			}
		};
	}
	
	private SelectionListener<ButtonEvent> DateHandler() {

		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Popup popUp = new Popup();
				final DatePicker datePicker = new DatePicker();
				datePicker.addListener(Events.Select,
						new Listener<ComponentEvent>() {
							public void handleEvent(ComponentEvent be) {
								String date = DateTimeFormat.getFormat("yyyy-MM-dd").format(datePicker.getValue());								
								loadConfig.set("date", date);
								panel.getLoader().load(loadConfig);
								datePicker.hide();
							}
						});

				popUp.add(datePicker);
				popUp.show(dateSelectBtn.getElement(), "tl-br?");
			}
			
		};
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
		
		leftControl.setWidth(300);
		hPanel.add(leftControl);
		
		LabelField lbRight = getFormStyleLable(rightControl.getFieldLabel());
		lbRight.setStyleAttribute("margin-left", "30px");
		hPanel.add(lbRight);
		
		rightControl.setWidth(300);
		hPanel.add(rightControl);
		
		return hPanel;
	}

}
