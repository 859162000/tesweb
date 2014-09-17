package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IDbHostService;
import com.dc.tes.ui.client.IDbHostServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTHost;
import com.dc.tes.ui.client.model.GWTStock;
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
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DbHostPage extends BasePage {
	
	private GridContentPanel<GWTHost> panel;
	private FormContentPanel<GWTHost> detailPanel;
	private ConfigToolBar configBar;
	private IDbHostServiceAsync hostService;
	private GWTHost EditHost;

	public DbHostPage() {
		
	}
	
	@Override
	protected void onRender(Element parent, int index) {

		super.onRender(parent, index);
		hostService = ServiceHelper.GetDynamicService("dbHost", IDbHostService.class);
		panel = new GridContentPanel<GWTHost>();
		RpcProxy<PagingLoadResult<GWTHost>> proxy = new RpcProxy<PagingLoadResult<GWTHost>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTHost>> callback) {
				hostService.GetGWTSysDynamicParaPageList(GetSystemID(), 
						panel.GetSearchCondition(), (PagingLoadConfig)loadConfig,callback);
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

		detailPanel = new FormContentPanel<GWTHost>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	private List<ColumnConfig> GetColumnConfig() {

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTHost.N_DbHost, "主机名", 120));
		columns.add(new ColumnConfig(GWTHost.N_Ipaddress, "主机地址", 100));
		columns.add(new ColumnConfig(GWTHost.N_Portnum, "主机端口", 80));
		columns.add(new ColumnConfig(GWTHost.N_IsLongConnStr, "长连接", 60));
		columns.add(new ColumnConfig(GWTHost.N_OsType, "操作系统", 80));
		columns.add(new ColumnConfig(GWTHost.N_DbType, "数据库", 100));
		columns.add(new ColumnConfig(GWTHost.N_DbName, "数据库名", 100));
		columns.add(new ColumnConfig(GWTHost.N_DbUser, "数据库用户名", 100));
		columns.add(new ColumnConfig(GWTHost.N_Description, "描述", 100));
		return columns;
	}

	private Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTHost.N_DbHost, "主机名称");
		detailMap.put(GWTHost.N_Description, "主机描述");
		detailMap.put(GWTHost.N_Ipaddress, "主机地址");
		detailMap.put(GWTHost.N_Portnum, "主机端口");
		return detailMap;
	}

	private void CreateEditForm() {

		final Window window = new Window();

		window.setSize(380, 370);
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
		
		final DistTextField tfParaName = new DistTextField(EditHost, 
				EditHost.getDbHost(), "主机名");
		tfParaName.setLabelStyle(labelStyle);
		tfParaName.setMaxLength(32);
		formPanel.add(tfParaName, formData);
		
		final TextField<String> hostIP = new TextField<String>();
		hostIP.setName(GWTHost.N_Ipaddress);
		hostIP.setAllowBlank(true);
		hostIP.setFieldLabel("主机地址");
		hostIP.setValue(EditHost.getIpAddress());
		hostIP.setRegex("((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))");
		hostIP.getMessages().setRegexText("格式错误 ");
		formPanel.add(hostIP,formData);
		
		final NumberField hostPort = new NumberField();
		hostPort.setName(GWTHost.N_Portnum);
		hostPort.setAllowBlank(true);
		hostPort.setFieldLabel("主机端口");
		formPanel.add(hostPort,formData);
		
		Radio radio = new Radio();  
		radio.setBoxLabel("是");  		
		radio.setData("value", "1");
		Radio radio2 = new Radio();  
		radio2.setBoxLabel("否");  
		radio2.setData("value", "0");
		final RadioGroup rgIsLongConn = new RadioGroup();  
		rgIsLongConn.setFieldLabel("长连接");  
		rgIsLongConn.add(radio);  
		rgIsLongConn.add(radio2);
		rgIsLongConn.setSpacing(30);
		formPanel.add(rgIsLongConn, formData);
		
		final ComboBox<GWTStock> cbOsType = new ComboBox<GWTStock>();
		cbOsType.setEditable(false);
		cbOsType.setFieldLabel("操作系统");
		cbOsType.setName("osType");
		cbOsType.setValueField(GWTStock.N_Name);
		cbOsType.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> osStore = new ListStore<GWTStock>();
		osStore.add(new GWTStock("WINDOWS", "WINDOWS"));
		osStore.add(new GWTStock("AS400", "AS400"));
		osStore.add(new GWTStock("RS6000", "RS6000"));
		osStore.add(new GWTStock("LINUX", "LINUX"));
		cbOsType.setStore(osStore);
		cbOsType.setTriggerAction(TriggerAction.ALL);
		formPanel.add(cbOsType, formData);
		
		final ComboBox<GWTStock> cbDBType = new ComboBox<GWTStock>();
		cbDBType.setEditable(false);
		cbDBType.setFieldLabel("数据库类型");
		cbDBType.setName("dbType");
		cbDBType.setValueField(GWTStock.N_Name);
		cbDBType.setDisplayField(GWTStock.N_Name);
		ListStore<GWTStock> listStore = new ListStore<GWTStock>();
		listStore.add(new GWTStock("DB2", "DB2"));
		listStore.add(new GWTStock("Mysql", "Mysql"));
		listStore.add(new GWTStock("Oracle", "Oracle"));
		listStore.add(new GWTStock("SqlServer", "SqlServer"));
		cbDBType.setStore(listStore);
		cbDBType.setTriggerAction(TriggerAction.ALL);
		formPanel.add(cbDBType, formData);
		
		final TextField<String> tfDbName = new TextField<String>();
		tfDbName.setName("dbName");
		tfDbName.setFieldLabel("数据库名称");
		formPanel.add(tfDbName, formData);
		
		final TextField<String> tfDBUser = new TextField<String>();
		tfDBUser.setName("dbUser");
		tfDBUser.setFieldLabel("用户名");
		formPanel.add(tfDBUser, formData);
		
		final TextField<String> tfDbPwd = new TextField<String>();
		tfDbPwd.setName("dbPwd");
		tfDbPwd.setFieldLabel("密码");
		formPanel.add(tfDbPwd, formData);
		
		final TextField<String> hostDesc = new TextField<String>();
		hostDesc.setName(GWTHost.N_Description);
		hostDesc.setAllowBlank(true);
		hostDesc.setFieldLabel("主机描述");
		hostDesc.setValue(EditHost.getDescription());
		formPanel.add(hostDesc,formData);
		
		Button btnOK = new Button("确定",new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				EditHost.SetValue(tfParaName.getValue(), hostIP.getValue(), 
						String.valueOf(hostPort.getValue().intValue()), hostDesc.getValue(),
						Integer.parseInt(rgIsLongConn.getValue().getData("value").toString()),
						cbDBType.getValue()==null?"":cbDBType.getValue().getPos(),
								tfDbName.getValue(), tfDBUser.getValue(), tfDbPwd.getValue(),
								cbOsType.getValue()==null?"":cbOsType.getValue().getPos());
				
				hostService.SaveHost(EditHost, GetLoginLogID(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert("错误信息", "保存失败", null);
					}

					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(Boolean suc) {
						// TODO Auto-generated method stub
						panel.loaderReLoad(EditHost.IsNew());
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

		if (EditHost.IsNew()) {
			window.setHeading("新增主机信息");
			radio.setValue(true);
		} else {
			tfParaName.setValue(EditHost.getDbHost());
			rgIsLongConn.setValue(EditHost.getIsLongConn()==1?radio:radio2);
			cbDBType.setValue(new GWTStock(EditHost.getDbType()==null?"":EditHost.getDbType(), 
					EditHost.getDbType()==null?"":EditHost.getDbType()));
			tfDbName.setValue(EditHost.getDbName());
			tfDBUser.setValue(EditHost.getDbUser());
			tfDbPwd.setValue(EditHost.getDbPwd());
			hostPort.setValue(Integer.parseInt(EditHost.getPortnum()));
			cbOsType.setValue(new GWTStock(EditHost.getOsType(), EditHost.getOsType()));
			window.setHeading("编辑主机信息");
		}

		window.show();	
	}
	
	
	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditHost = new GWTHost("",GetSystemID());
				CreateEditForm();
			}
		};
	}


	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<GWTHost> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditHost = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}
	
	
	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					hostService.DeleteHost(panel.getSelection(), GetLoginLogID(),
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


}
