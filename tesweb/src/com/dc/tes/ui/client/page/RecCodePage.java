package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.model.GWTComponent;
import com.dc.tes.ui.client.model.GWTRecCode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RecCodePage extends ComponentBasePage {

	public RecCodePage(){
		super(ComponentEnum.RecCode);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	}
	
	@Override
	public List<ColumnConfig> GetColumnConfig() {
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(GWTRecCode.N_Name, "名称", 160));
		columns.add(new ColumnConfig(GWTRecCode.N_Desc, "描述", 120));
		columns.add(new ColumnConfig(GWTRecCode.N_Type, "识别类型", 160));
		columns.add(new ColumnConfig(GWTRecCode.N_Class, "组件类", 250));
		
		return columns;
	}
	
	@Override
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTRecCode.N_Template, "配置模板");
		return detailMap;
	}
	
	@Override
	public void CreateEditForm(final GWTComponent edit) {
		final Window window = new Window();

		if(edit == null)
			window.setHeading("添加交易识别组件");
		else
			window.setHeading("编辑交易识别组件");
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
		
		final RequireTextField tfName = new RequireTextField("名称");
		tfName.setLabelStyle(labelStyle);
		tfName.setMaxLength(32);
		formPanel.add(tfName, formdata);
		
		final TextField<String> tfDesc = new TextField<String>();
		tfDesc.setLabelStyle(labelStyle);
		tfDesc.setFieldLabel("描述");
		formPanel.add(tfDesc, formdata);
		
		final TextField<String> tfType = new TextField<String>();
		tfType.setLabelStyle(labelStyle);
		tfType.setFieldLabel("类型");
		formPanel.add(tfType, formdata);
		
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
			tfName.setValue(edit.get(GWTRecCode.N_Name).toString());
			tfDesc.setValue(edit.get(GWTRecCode.N_Desc).toString());
			tfType.setValue(edit.get(GWTRecCode.N_Type).toString());
			tfClass.setValue(edit.get(GWTRecCode.N_Class).toString());
			tfConfTemplate.setValue(edit.get(GWTRecCode.N_Template).toString());
		}

		Button btnOK = new Button("确定");
		btnOK.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!formPanel.isValid())
					return;
				if(edit == null){
					GWTComponent comp = new GWTRecCode();
					comp.set(GWTComponent.N_ComponentId, "");
					comp.set(GWTRecCode.N_Name, tfName.getValue());
					comp.set(GWTRecCode.N_Desc, tfDesc.getValue());
					comp.set(GWTRecCode.N_Type, tfType.getValue());
					comp.set(GWTRecCode.N_Class, tfClass.getValue());
					comp.set(GWTRecCode.N_Template, tfConfTemplate.getValue());
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
					edit.set(GWTRecCode.N_Name, tfName.getValue());
					edit.set(GWTRecCode.N_Desc, tfDesc.getValue());
					edit.set(GWTRecCode.N_Type, tfType.getValue());
					edit.set(GWTRecCode.N_Class, tfClass.getValue());
					edit.set(GWTRecCode.N_Template, tfConfTemplate.getValue());
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
