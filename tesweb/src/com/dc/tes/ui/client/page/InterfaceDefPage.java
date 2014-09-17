package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dc.tes.ui.client.IInterfaceService;
import com.dc.tes.ui.client.IInterfaceServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.CascadeContentPanel;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.dc.tes.ui.client.model.GWTInterfaceField;
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
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InterfaceDefPage extends BasePage {
	IInterfaceServiceAsync interfaceService = ServiceHelper.GetDynamicService("interface", IInterfaceService.class);
	GWTInterfaceDef EditDef = null;
	GWTInterfaceField EditField = null;
	/**
	 * 列表控件
	 */
	CascadeContentPanel<GWTInterfaceDef> panel;
	/**
	 * 底部菜单栏
	 */
	ConfigToolBar configBar;
	Grid<GWTInterfaceField> fieldGrid;
	InterfaceFieldPanel detailPanel;
	UploadWin upWindow = null;
	public InterfaceDefPage(){
		super();
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new CascadeContentPanel<GWTInterfaceDef>();

		RpcProxy<PagingLoadResult<GWTInterfaceDef>> proxy = new RpcProxy<PagingLoadResult<GWTInterfaceDef>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTInterfaceDef>> callback) {
				interfaceService.GetInterfaceDefList(GetSystemID(), panel.GetSearchCondition(),
						(PagingLoadConfig)loadConfig, callback);
			}
		};
		
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView(GWTInterfaceDef.N_ChineseName);
		panel.DrowSearchBar();
		
		detailPanel = new InterfaceFieldPanel();
		panel.setCascadePanel(detailPanel);
		fieldGrid = detailPanel.getDataGrid();
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		
		configBar.AddButton("btnFieldAdd",new Button("添加字段"), MainPage.ICONS
				.AddCom(), AddFieldHandler());
		configBar.AddButton("btnFieldEdit",new Button("编辑字段"), MainPage.ICONS
				.EditCom(), EditFieldHandler());
		configBar.AddButton("btnFieldDel",new Button("删除字段"), MainPage.ICONS
				.DelCom(), DeleteFieldHandler());
		configBar.AddButton("btnFieldSave", detailPanel.getSaveButton());
		configBar.AddButton("btnUpload",new Button("上传接口"), MainPage.ICONS
				.WebUp(), UploadHandler());
		configBar.AddWidget(new FillToolItem());
		configBar.AddNewBtn("btnAdd", AddHandler());
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);		
		add(detailPanel);
	}	

	

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTInterfaceDef.N_InterfaceName, "接口名称", 150));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_ChineseName, "中文名称", 120));	
		columns.add(new ColumnConfig(GWTInterfaceDef.N_InterfaceLen, "接口长度", 100));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_FieldCount, "字段数量", 100));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_UserName, "添加用户", 100));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_ImportTime, "添加时间", 120));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_Memo, "备注", 100));
		return columns;
	}
	
	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					interfaceService.DeleteInterfaceDef(panel.getSelection(),GetLoginLogID(),
							new AsyncCallback<Void>() {
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败", null);
								}

								public void onSuccess(Void obj) {
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

				List<GWTInterfaceDef> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditDef = selectedItems.get(0);
				CreateEditForm();
			}			
		};
	}

	private SelectionListener<ButtonEvent> AddHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditDef = new GWTInterfaceDef(GetSystemID());
				CreateEditForm();
			}
		};
	}
	
	private void CreateEditForm() {
		// TODO Auto-generated method stub
		final Window window = new Window();

		window.setSize(380, 250);
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
		
		final DistTextField tfName = new DistTextField(EditDef, 
				EditDef.GetInterfaceName(), "接口名称", "接口名称不能重复，请重新输入");
		tfName.setLabelStyle(labelStyle);
		tfName.setMaxLength(32);
		formPanel.add(tfName, formData);
		
		final TextField<String> tfChnName = new TextField<String>();
		tfChnName.setFieldLabel("中文名称");
		tfChnName.setLabelStyle(labelStyle);
		formPanel.add(tfChnName, formData);
		
		final NumberField tfLen = new NumberField();
		tfLen.setFieldLabel("接口长度");
		tfLen.setMaxLength(4);
		tfLen.setLabelStyle(labelStyle);
		formPanel.add(tfLen, formData);
		
		final NumberField tfCount = new NumberField();
		tfCount.setFieldLabel("字段数");
		tfCount.setMaxLength(4);
		tfCount.setLabelStyle(labelStyle);
		formPanel.add(tfCount, formData);
		
		
		final TextArea tfMemo = new TextArea();
		tfMemo.setFieldLabel("备注");
		tfMemo.setMaxLength(256);
		tfMemo.setLabelStyle(labelStyle);
		formPanel.add(tfMemo, formData);
		
		Button btnOK = new Button("确定",new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!tfName.isValid()){
					return;
				}
				
				String userId = EditDef.IsNew()?GetUserID():EditDef.GetImportUserID();
				String time = EditDef.IsNew()?DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()):EditDef.GetImportTime();
				
				EditDef.SetValue(tfName.getValue(), tfChnName.getValue(), tfLen.getValue().intValue(), 
						tfCount.getValue().intValue(), userId, time, tfMemo.getValue());
				interfaceService.SaveOrUpdateInterfaceDef(EditDef, GetLoginLogID(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
						MessageBox.alert("错误提示", "保存失败，请联系管理员！", null);
					}

					@Override
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						if(result){
							panel.loaderReLoad(EditDef.IsNew());
							window.hide();
						}else{
							MessageBox.alert("错误提示", "保存失败，请联系管理员！", null);
						}
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

		if (EditDef.IsNew()) {
			window.setHeading("新增接口信息");
		} else {
			tfName.setValue(EditDef.GetInterfaceName());
			tfChnName.setValue(EditDef.GetChineseName());
			tfLen.setValue(EditDef.GetInterfaceLen());
			tfCount.setValue(EditDef.GetFieldCount());
			tfMemo.setValue(EditDef.GetMemo());
			window.setHeading("编辑接口信息");
		}

		window.show();	
	}
	
	private SelectionListener<ButtonEvent> AddFieldHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				List<GWTInterfaceDef> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditDef = selectedItems.get(0);
				EditField = new GWTInterfaceField(EditDef.GetInterfaceID());
				CreateFieldEditForm();
			}			
		};
	}
	
	private SelectionListener<ButtonEvent> DeleteFieldHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示", "是否确认删除所选字段？", new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						// TODO Auto-generated method stub
						if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){
							List<GWTInterfaceField> fields = fieldGrid.getSelectionModel().getSelectedItems();
							interfaceService.DeleteInterfaceField(fields, GetLoginLogID(), new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败，请联系管理员！", null);

								}

								@Override
								public void onSuccess(Void result) {
									// TODO Auto-generated method stub
									detailPanel.reloadGrid();
								}
							});
						}
					}
				});
			}
		};
	}

	private SelectionListener<ButtonEvent> EditFieldHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				EditField = fieldGrid.getSelectionModel().getSelectedItems().get(0);
				CreateFieldEditForm();
			}
		};
	}
	
	private void CreateFieldEditForm() {
		// TODO Auto-generated method stub
		final Window window = new Window();

		window.setSize(380, 350);
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
		
		final DistTextField tfName = new DistTextField(EditField, 
				EditField.GetFieldName(), "字段名称", "字段名称不能重复，请重新输入");
		tfName.setLabelStyle(labelStyle);
		tfName.setMaxLength(32);
		formPanel.add(tfName, formData);
		
		final TextField<String> tfChnName = new TextField<String>();
		tfChnName.setFieldLabel("中文名称");
		tfChnName.setLabelStyle(labelStyle);
		formPanel.add(tfChnName, formData);
		
		final NumberField tfLen = new NumberField();
		tfLen.setFieldLabel("字段长度");
		tfLen.setMaxLength(4);
		tfLen.setAllowBlank(false);
		tfLen.setLabelStyle(labelStyle);
		formPanel.add(tfLen, formData);
		
		final ComboBox<GWTStock> cbType = new ComboBox<GWTStock>();
		cbType.setFieldLabel("字段类型");
		cbType.setDisplayField(GWTStock.N_Name);
		cbType.setAllowBlank(false);
		cbType.setValueField(GWTStock.N_Pos);
		ListStore<GWTStock> store = new ListStore<GWTStock>();
		store.add(new GWTStock("A：ASC类型字符串", "A"));
		store.add(new GWTStock("S：数字类型字符串", "S"));
		store.add(new GWTStock("O：全角中文", "O"));
		store.add(new GWTStock("D：日期类型", "D"));
		store.add(new GWTStock("T: 时间类型", "T"));
		cbType.setStore(store);
		cbType.setLabelStyle(labelStyle);
		cbType.setTriggerAction(TriggerAction.ALL);
		formPanel.add(cbType, formData);			
		
		final NumberField tfDigits = new NumberField();
		tfDigits.setFieldLabel("小数位数");
		tfDigits.setMaxLength(4);
		tfDigits.setLabelStyle(labelStyle);
		formPanel.add(tfDigits, formData);
		
		final CheckBox cbOptional = new CheckBox();
		cbOptional.setFieldLabel("是否必填");
		cbOptional.setValue(false);
		formPanel.add(cbOptional, formData);
		
		final TextField<String> tfDefVal = new TextField<String>();
		tfDefVal.setFieldLabel("默认值");
		tfDefVal.setLabelStyle(labelStyle);
		formPanel.add(tfDefVal, formData);
		
		final TextArea tfMemo = new TextArea();
		tfMemo.setFieldLabel("备注");
		tfMemo.setMaxLength(256);
		tfMemo.setLabelStyle(labelStyle);
		formPanel.add(tfMemo, formData);
		
		Button btnOK = new Button("确定",new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!tfName.isValid()){
					return;
				}
				int sequence;
				if(EditField.IsNew()){
					sequence = fieldGrid.getStore().getCount();
				}else{
					sequence = EditField.GetSequence();
				}
				//组类型表达式。
				String fieldTypeExpr = "";
				int len = tfLen.getValue().intValue();
				String fieldType = cbType.getValue().getPos();
				fieldTypeExpr = fieldType+ "(" + len + 
						(fieldType.equals("S")? ","+tfDigits.getValue().intValue() : "") + ")";				
				EditField.SetValue(sequence, tfName.getValue(),
						tfChnName.getValue(), fieldTypeExpr, fieldType,
						len, tfDigits.getValue().intValue(),
						cbOptional.getValue()?"C":"O", tfDefVal.getValue(), tfMemo.getValue());
				interfaceService.SaveOrUpdateInterfaceField(EditField, GetLoginLogID(), new AsyncCallback<GWTInterfaceField>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
						MessageBox.alert("错误提示", "保存失败，请联系管理员！", null);
					}

					@Override
					public void onSuccess(GWTInterfaceField result) {
						// TODO Auto-generated method stub					
						detailPanel.reloadGrid();
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

		if (EditField.IsNew()) {
			window.setHeading("新增字段信息");
		} else {
			tfName.setValue(EditField.GetFieldName());
			tfChnName.setValue(EditField.GetChineseName());
			tfLen.setValue(EditField.GetFieldLen());
			tfDigits.setValue(EditField.GetDecimalDigits());
			tfDefVal.setValue(EditField.GetDefaultValue());
			cbOptional.setValue(EditField.GetOptional().equals("C"));
			cbType.setValue(store.findModel(GWTStock.N_Pos, EditField.GetFieldType()));
			tfMemo.setValue(EditField.GetMemo());
			window.setHeading("编辑字段信息");
		}

		window.show();	
	}

	private SelectionListener<ButtonEvent> UploadHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				upWindow = new UploadWin(panel.getLoader());
				upWindow.Show("上传接口信息(.txt)", "正在上传接口信息TXT,请稍后……",
						"As400InterfaceImport?" +
						"sysId=" + GetSystemID() +
						"&loginLogId=" + GetLoginLogID()
						+ "&userId=" + GetUserID());
			}
		};
	}
}
