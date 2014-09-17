package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IPersistentDataService;
import com.dc.tes.ui.client.IPersistentDataServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.model.GWTPersistentData;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PersisDataPage extends BasePage {
	IPersistentDataServiceAsync pDataService = null;
	GWTPersistentData EditPData = null;

	GridContentPanel<GWTPersistentData> panel;
	FormContentPanel<GWTPersistentData> detailPanel;
	ConfigToolBar configBar;

	public PersisDataPage() {
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		pDataService = ServiceHelper.GetDynamicService("pData", IPersistentDataService.class);
		panel = new GridContentPanel<GWTPersistentData>();
		RpcProxy<PagingLoadResult<GWTPersistentData>> proxy = new RpcProxy<PagingLoadResult<GWTPersistentData>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTPersistentData>> callback) {
				pDataService.GetGWTPersistentDataList(GetSystemID(), panel
						.GetSearchCondition(), (PagingLoadConfig) loadConfig,
						callback);
			}
		};

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView(GWTPersistentData.N_Parameter);

		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddWidget(new FillToolItem());
		configBar.AddNewBtn("btnAdd", AddHandler());
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTPersistentData>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns
				.add(new ColumnConfig(GWTPersistentData.N_Parameter, "参数名称",
						150));
		columns.add(new ColumnConfig(GWTPersistentData.N_Curvalue, "当前值", 250));
		columns.add(new ColumnConfig(GWTPersistentData.N_TypeStr, "参数类型", 250));

		return columns;
	}

	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTPersistentData.N_Parameter, "参数名称");
		detailMap.put(GWTPersistentData.N_Curvalue, "当前值");
		detailMap.put(GWTPersistentData.N_TypeStr, "参数类型");
		return detailMap;
	}

	private void CreateEditForm() {
		final Window window = new Window();

		window.setSize(280, 160);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		formPanel.setPadding(5);

		FormData formData = new FormData("85%");
		String labelStyle = "width:60px;";
		final DistTextField tfParaName = new DistTextField(EditPData, EditPData
				.getParameter(), "参数名称");
		tfParaName.setLabelStyle(labelStyle);
		tfParaName.setMaxLength(32);
		formPanel.add(tfParaName, formData);

		final RequireTextField tfCurValue = new RequireTextField("当前值");
		tfCurValue.setMaxLength(16);
		tfCurValue.setLabelStyle(labelStyle);
		formPanel.add(tfCurValue, formData);

		final RadioGroup rgType = new RadioGroup();
		rgType.setFieldLabel("参数类型");
		rgType.setStyleAttribute("nowrap", "true");
		rgType.setLabelStyle("width:60px;");
		rgType.setOrientation(Orientation.HORIZONTAL);

		Radio radioType = new Radio();
		radioType.setBoxLabel("数字");
		radioType.setFieldLabel("1");
		radioType.setValue(EditPData.getType() == 1);
		radioType.setStyleAttribute("padding-top", "5px;");
		rgType.add(radioType);

		radioType = new Radio();
		radioType.setBoxLabel("字符");
		radioType.setFieldLabel("0");
		radioType.setValue(EditPData.getType() == 0);
		radioType.setStyleAttribute("padding-top", "5px;");
		rgType.add(radioType);
		FormData style = new FormData();
		style.setWidth(150);
		formPanel.add(rgType, style);	

		rgType.addListener(Events.Change , new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(tfCurValue.isValid())
						tfCurValue.clearInvalid();
			}
		});
		tfCurValue.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				int type = Integer.parseInt(rgType
						.getValue().getFieldLabel());
				if(type == 1)
				{
					try
					{
						Double.parseDouble(tfCurValue.getValue());
						return null;
					}
					catch (Exception e) {
						
					}
					return "参数类型为数字，当前值同时也要求为数字";
				}
				return null;
			}
		});
		
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				int type = Integer.parseInt(rgType
						.getValue().getFieldLabel());
				
				EditPData.SetValue(tfParaName.getValue(),
						tfCurValue.getValue(), type);
				pDataService.SavePersistentData(EditPData,
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
								MessageBox.alert("错误信息", "保存失败", null);
							}

							@SuppressWarnings("deprecation")
							public void onSuccess(Boolean suc) {
								panel.loaderReLoad(EditPData.IsNew());
								if (suc)
									window.close();
								else {
									tfParaName.focus();
									tfParaName.EnforceValidate();
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

		if (EditPData.IsNew()) {
			window.setHeading("新增持久化参数");
		} else {
			tfParaName.setValue(EditPData.getParameter());
			tfCurValue.setValue(EditPData.getCurvalue());
			window.setHeading("编辑持久化参数");
		}

		window.show();
	}

	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditPData = new GWTPersistentData(GetSystemID());
				CreateEditForm();
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<GWTPersistentData> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditPData = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}

	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					pDataService.DeletePersistentData(panel.getSelection(),
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
}
