package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.enums.MsgType;
import com.dc.tes.ui.client.model.GWTComponent;
import com.dc.tes.ui.client.model.GWTMsgType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MsgTypePage extends ComponentBasePage {

	String defaultPackClass = "com.dc.tes.fcore.msg.DefaultPacker";
	String defaultUnPackClass = "com.dc.tes.fcore.msg.DefaultUnpacker";
	
	public MsgTypePage(){
		super(ComponentEnum.MsgType);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	}
	
	@Override
	public List<ColumnConfig> GetColumnConfig() {
		
		GridCellRenderer<GWTComponent> packTypeRender = new GridCellRenderer<GWTComponent>(){

			@Override
			public Object render(GWTComponent model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTComponent> store, Grid<GWTComponent> grid) {
				int type = model.<Integer>get(property);
				return MsgType.valueOfDbValue(type).getChDesc();
			}
		};
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(GWTMsgType.N_StyleName, "样式名称", 100));
		ColumnConfig packType = new ColumnConfig(GWTMsgType.N_Type, "类型", 120);
		packType.setAlignment(HorizontalAlignment.CENTER);
		packType.setRenderer(packTypeRender);
		columns.add(packType);
		columns.add(new ColumnConfig(GWTMsgType.N_Protocol, "报文协议", 120));
		columns.add(new ColumnConfig(GWTMsgType.N_Class, "组件类", 250));
		//columns.add(new ColumnConfig(GWTMsgType.N_Desc, "描述", 120));
		
		return columns;
	}
	
	@Override
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTMsgType.N_StyleName, "样式名称");
		detailMap.put(GWTMsgType.N_Type, "类型");
		detailMap.put(GWTMsgType.N_Protocol, "报文协议");
		detailMap.put(GWTMsgType.N_Class, "组件类");
		return detailMap;
	}
	
	public void CreateEditForm(final GWTComponent edit) {
		final Window window = new Window();
		
		if(edit == null)
			window.setHeading("添加报文协议");
		else
			window.setHeading("编辑报文协议");
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
		
		final RequireTextField tfStyleName = new RequireTextField("报文样式名称");
		tfStyleName.setLabelStyle(labelStyle);
		tfStyleName.setMaxLength(32);
		formPanel.add(tfStyleName, formdata);
		
		final SimpleComboBox<String> typeCB = new SimpleComboBox<String>();
		typeCB.setEditable(false);
		typeCB.setFieldLabel("报文类型");
		typeCB.setLabelStyle(labelStyle);
		for(MsgType t : MsgType.values()){
			typeCB.add(t.getChDesc());
		}
		typeCB.setSimpleValue(MsgType.values()[0].getChDesc());
		typeCB.setTriggerAction(TriggerAction.ALL);
		formPanel.add(typeCB, formdata);
		
		final SimpleComboBox<String> msgType = new SimpleComboBox<String>();
		msgType.setFieldLabel("报文协议");
		msgType.setLabelStyle(labelStyle);
		msgType.add(Arrays.asList("XML", "8583", "定长", "二进制", "其它"));
		msgType.setSimpleValue("XML");
		msgType.setTriggerAction(TriggerAction.ALL);
		formPanel.add(msgType, formdata);
		
		final RequireTextField tfClass = new RequireTextField("组件类");
		tfClass.setLabelStyle(labelStyle);
		tfClass.setFieldLabel("组件类");
		tfClass.setValue(defaultPackClass);
		formPanel.add(tfClass, formdata);
		
		typeCB.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				String text = se.getSelectedItem().getValue();
				if(edit != null)return;
				if(text.equals(MsgType.Pack.getChDesc()))
					tfClass.setValue(defaultPackClass);
				else
					tfClass.setValue(defaultUnPackClass);
				
			}
			
		});

		final TextArea tfConfTemplate = new TextArea();
		tfConfTemplate.setLabelStyle(labelStyle);
		tfConfTemplate.setHeight(360);
		tfConfTemplate.setMaxLength(8192);
		tfConfTemplate.setFieldLabel("样式文件内容");
		formPanel.add(tfConfTemplate, formdata);
		
		if(edit != null){
			tfStyleName.setValue(edit.get(GWTMsgType.N_StyleName).toString());
			//tfDesc.setValue(edit.get(GWTMsgType.N_Desc).toString());
			int type = edit.<Integer>get(GWTMsgType.N_Type);
			typeCB.setSimpleValue(MsgType.valueOfDbValue(type).getChDesc());
			msgType.setSimpleValue(edit.get(GWTMsgType.N_Protocol).toString());
			tfClass.setValue(edit.get(GWTMsgType.N_Class).toString());
			tfConfTemplate.setValue(edit.get(GWTMsgType.N_Content).toString());
		}
		
		Button btnOK = new Button("确定");
		btnOK.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!formPanel.isValid())
					return;
				if(edit == null){
					GWTComponent comp = new GWTMsgType();
					comp.set(GWTComponent.N_ComponentId, "");
					comp.set(GWTMsgType.N_StyleName, tfStyleName.getValue());
					//comp.set(GWTMsgType.N_Desc, tfDesc.getValue());
					int type = MsgType.valueOfChDesc(typeCB.getSimpleValue()).getDbValue();
					comp.set(GWTMsgType.N_Type, type);
					comp.set(GWTMsgType.N_Protocol, msgType.getSimpleValue());
					comp.set(GWTMsgType.N_Class, tfClass.getValue());
					comp.set(GWTMsgType.N_Content, tfConfTemplate.getValue());
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
					edit.set(GWTMsgType.N_StyleName, tfStyleName.getValue());
					//edit.set(GWTMsgType.N_Desc, tfDesc.getValue());
					int type = MsgType.valueOfChDesc(typeCB.getSimpleValue().toString()).getDbValue();
					edit.set(GWTMsgType.N_Type, type);
					edit.set(GWTMsgType.N_Protocol, msgType.getSimpleValue());
					edit.set(GWTMsgType.N_Class, tfClass.getValue());
					edit.set(GWTMsgType.N_Content, tfConfTemplate.getValue());
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
