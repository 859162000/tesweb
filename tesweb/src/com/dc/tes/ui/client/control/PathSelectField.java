package com.dc.tes.ui.client.control;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableRowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.google.gwt.user.client.Element;


public class PathSelectField<T extends ModelData> extends LayoutContainer{
	
	private TextField<String> textField;	
	private Button btn_select;
	private RpcProxy<List<T>> proxy;
	protected TreeStore<T> store = null;
	private boolean checkable = false;
	private boolean readOnly = false;
	private boolean allowBlank = true;
	private int fieldLabelWidth = 80;
	private String fieldLabel;
	private SelectionMode selectionMode = SelectionMode.MULTI;
	private int textFieldWidth = 200;
	private List<T> selectedItems = new ArrayList<T>();
	
	/**
	 * 路径选择控件，需传入路径获取的RpcProxy
	 */
	public PathSelectField(){
		super();	   	
		
	}
	
	/**
	 * 路径选择控件，需传入路径获取的RpcProxy
	 * @param fieldLabel 控件名称
	 * @param selectionMode 路径选择模式
	 * @param checkable 是否使用checkbox
	 */
	public PathSelectField(String fieldLabel, SelectionMode selectionMode, boolean checkable){
		this.fieldLabel = fieldLabel;
		this.selectionMode = selectionMode;
		this.checkable = checkable;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		TableRowLayout layout = new TableRowLayout();
		layout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
	    layout.setCellVerticalAlign(VerticalAlignment.TOP);
	    layout.setCellSpacing(0);
	    layout.setCellPadding(0);
	    layout.setWidth("100%");
	    layout.setHeight("20px");
		this.setLayout(layout);
		this.setStyleAttribute("margin-top", "5px");
		this.setStyleAttribute("margin-bottom", "5px"); 
		this.add(getFormStyleLable(fieldLabel));		
		textField = new TextField<String>();		
		textField.setFieldLabel(fieldLabel);
		textField.setWidth(textFieldWidth);
		textField.setReadOnly(readOnly);
		textField.setAllowBlank(allowBlank);
		textField.setStyleAttribute("margin-right", "1px");
		this.add(textField);
		btn_select = new Button("选择");
		btn_select.setWidth(40);
		btn_select.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				DrawSelectPathWin();				
				
			}			
		});
		this.add(btn_select);
	}
	
	private void DrawSelectPathWin() {
		// TODO Auto-generated method stub		
		final Window window = new Window();
		window.setSize(350, 400);
		window.setScrollMode(Scroll.AUTOY);
		window.setModal(true);
		window.setPlain(true);
		window.setHeading("选择路径");
		window.setLayout(new FitLayout());
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setBodyBorder(false);
		contentPanel.setBorders(false);
		contentPanel.setHeaderVisible(false);
		
		final TreeLoader<T> loader = new BaseTreeLoader<T>(
				proxy);
		store = new TreeStore<T>(loader);
		final TreePanel<T> tree = new TreePanel<T>(store) {
			@Override
			public boolean hasChildren(T parent) {
					return HasChildren(parent);						
			}			
		};  
		tree.getSelectionModel().setSelectionMode(selectionMode);
	    tree.setDisplayProperty("name");  
	    tree.setWidth(335); 
	    tree.setHeight(330);
	    tree.setCheckable(checkable);
	    tree.setCheckedSelection(selectedItems);
	    tree.setCheckStyle(CheckCascade.NONE);
	    contentPanel.add(tree);
	    window.add(contentPanel);
	    Button btn_ok = new Button("确定", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				if(checkable){
					selectedItems = tree.getCheckedSelection();
					
				}else{
					selectedItems.clear();
					selectedItems.addAll(tree.getSelectionModel().getSelectedItems());				
				}
				String s = "";
				for(T d : selectedItems){
					if(!s.isEmpty()){
						s+="; ";
					}
					s+=d.get("path");
				}
				textField.setValue(s);
				window.hide();
			}
		});
	    window.addButton(btn_ok);
	    window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				window.hide();
			}
		}));
	    
	    window.show();
	}
	
	private LabelField getFormStyleLable(String labelText)
	{
		LabelField label = new LabelField(labelText + ":");
		label.setWidth(fieldLabelWidth);
		label.setStyleName("x-form-item-label");
		label.setStyleAttribute("font", "normal 12px tahoma, arial, helvetica, sans-serif");
		return label;
	}
	
	protected boolean HasChildren(T parent) {
		// TODO Auto-generated method stub
		return true;
	}

	public TextField<String> getTextField() {
		return textField;
	}

	public RpcProxy<List<T>> getProxy() {
		return proxy;
	}

	public TreeStore<T> getStore() {
		return store;
	}

	public boolean isCheckable() {
		return checkable;
	}

	public boolean isAllowBlank() {
		return allowBlank;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public List<T> getSelectedItems() {
		return selectedItems;
	}

	public void setProxy(RpcProxy<List<T>> proxy) {
		this.proxy = proxy;
	}

	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	public void setAllowBlank(boolean allowBlank) {
		this.allowBlank = allowBlank;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public void setSelectedItems(List<T> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setTextFieldWidth(int textFieldWidth) {
		this.textFieldWidth = textFieldWidth;		
	}

	public int getTextFieldWidth() {
		return textFieldWidth;
	}

	public void setFieldLabelWidth(int fieldLabelWidth) {
		this.fieldLabelWidth = fieldLabelWidth;
	}

	public int getFieldLabelWidth() {
		return fieldLabelWidth;
	}
}
