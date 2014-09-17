package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.model.GWTAdapter;
import com.dc.tes.ui.client.model.GWTComponent;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdapterPage extends ComponentBasePage {

	public AdapterPage(){
		super(ComponentEnum.Adapter);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	}
	
	@Override
	public List<ColumnConfig> GetColumnConfig() {
		
		GridCellRenderer<GWTComponent> csTypeRender = new GridCellRenderer<GWTComponent>(){

			@Override
			public Object render(GWTComponent model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTComponent> store, Grid<GWTComponent> grid) {
				int type = model.<Integer>get(property);
				return CsType.valueOfDbValue(type).getChDesc();
			}
		};
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(GWTAdapter.N_Protocol, "通讯协议", 100));
		ColumnConfig csType = new ColumnConfig(GWTAdapter.N_CsType, "类型", 120);
		csType.setAlignment(HorizontalAlignment.CENTER);
		csType.setRenderer(csTypeRender);
		columns.add(csType);
		columns.add(new ColumnConfig(GWTAdapter.N_PlugIn, "插件类", 250));
		columns.add(new ColumnConfig(GWTAdapter.N_Desc, "描述", 120));
		
		
		
		return columns;
	}
	
	@Override
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTAdapter.N_ConfigTemplate, "配置模板");
		return detailMap;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void CreateEditForm(final GWTComponent edit) {
		final Window window = new Window();

		if(edit == null)
			window.setHeading("添加通讯协议");
		else
			window.setHeading("编辑通讯协议");
		window.setSize(500, 550);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);

		String labelStyle = "width:80px;";
		FormData formdata = new FormData("95%");
		
		final RequireTextField tfProtocol = new RequireTextField("通讯协议");
		tfProtocol.setLabelStyle(labelStyle);
		tfProtocol.setMaxLength(32);
		formPanel.add(tfProtocol, formdata);
		
		final TextField<String> tfDesc = new TextField<String>();
		tfDesc.setLabelStyle(labelStyle);
		tfDesc.setFieldLabel("适配器描述");
		formPanel.add(tfDesc, formdata);
		
		final SimpleComboBox comboCsType = new SimpleComboBox();
		comboCsType.setEditable(false);
		comboCsType.setFieldLabel("类型");
		comboCsType.setLabelStyle(labelStyle);
		for(CsType type : CsType.values()){
			comboCsType.add(type.getChDesc());
		}
		comboCsType.setSimpleValue(CsType.values()[0].getChDesc());
		comboCsType.setTriggerAction(TriggerAction.ALL);
		formPanel.add(comboCsType, formdata);
		
		final RequireTextField tfClass = new RequireTextField("组件类");
		tfClass.setLabelStyle(labelStyle);
		tfClass.setFieldLabel("组件类");
		formPanel.add(tfClass, formdata);

		final TextArea tfConfTemplate = new TextArea();
		tfConfTemplate.setLabelStyle(labelStyle);
		tfConfTemplate.setHeight(360);
		tfConfTemplate.setMaxLength(2048);
		tfConfTemplate.setFieldLabel("默认配置模板");
		formPanel.add(tfConfTemplate, formdata);
		
		if(edit != null){
			tfProtocol.setValue(edit.get(GWTAdapter.N_Protocol).toString());
			tfDesc.setValue(edit.get(GWTAdapter.N_Desc).toString());
			int csType = edit.<Integer>get(GWTAdapter.N_CsType);
			comboCsType.setSimpleValue(CsType.valueOfDbValue(csType).getChDesc());
			tfClass.setValue(edit.get(GWTAdapter.N_PlugIn).toString());
			tfConfTemplate.setValue(edit.get(GWTAdapter.N_ConfigTemplate).toString());
		}

		Button btnOK = new Button("确定");
		btnOK.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!formPanel.isValid())
					return;
				if(edit == null){
					GWTComponent comp = new GWTAdapter();
					comp.set(GWTComponent.N_ComponentId, "");
					comp.set(GWTAdapter.N_Protocol, tfProtocol.getValue());
					comp.set(GWTAdapter.N_Desc, tfDesc.getValue());
					int csType = CsType.valueOfChDesc(comboCsType.getSimpleValue().toString()).getDbValue();
					comp.set(GWTAdapter.N_CsType, csType);
					comp.set(GWTAdapter.N_PlugIn, tfClass.getValue());
					comp.set(GWTAdapter.N_ConfigTemplate, tfConfTemplate.getValue());
					service.AddNewComponent(comp, new AsyncCallback<Void>(){
	
						@Override
						public void onFailure(Throwable caught) {
							MessageBox.alert("添加数据失败", "添加数据失败，请与管理员联系", null);
						}
	
						@Override
						public void onSuccess(Void result) {
							panel.getLoader().load();
						}
					});
				}else{
					edit.set(GWTAdapter.N_Protocol, tfProtocol.getValue());
					edit.set(GWTAdapter.N_Desc, tfDesc.getValue());
					int csType = CsType.valueOfChDesc(comboCsType.getSimpleValue().toString()).getDbValue();
					edit.set(GWTAdapter.N_CsType, csType);
					edit.set(GWTAdapter.N_PlugIn, tfClass.getValue());
					edit.set(GWTAdapter.N_ConfigTemplate, tfConfTemplate.getValue());
					service.UpdateComponent(edit, new AsyncCallback<Void>(){
						
						@Override
						public void onFailure(Throwable caught) {
							MessageBox.alert("更新数据失败", "更新数据失败，请与管理员联系", null);
						}
	
						@Override
						public void onSuccess(Void result) {
							panel.getLoader().load();
						}
					});
				}
				window.close();
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
		window.show();
	}
}
