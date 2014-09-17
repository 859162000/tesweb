package com.dc.tes.ui.client.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.model.GWTButtonConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar.PagingToolBarMessages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ConfigToolBar extends ButtonBar implements IButtonConfig {
	private Map<String,Component> toolWidget;
	private Map<String,Component> menuWidget;
	private PagingToolBar pageToolBar = null;
	
	public ConfigToolBar() {
		toolWidget = new  LinkedHashMap<String,Component>();
		menuWidget = new  LinkedHashMap<String,Component>();
	}
	
	public void initPageToolBar(PagingLoader<PagingLoadResult<ModelData>> loader)
	{
		initPageToolBar(10,loader);
	}
	
	public void initPageToolBar(int pageSize,PagingLoader<PagingLoadResult<ModelData>> loader)
	{
		setPageSize(pageSize);
		pageToolBar = new PagingToolBar(pageSize)
		{
			/**
			 * 重载，处理没有记录时，输入页码导致的页面按钮可用的bug
			 */
			@Override
			public void last() {
				if(super.totalLength == 0)
				{
					setActivePage(1);
					return;
				}
				super.last();
			}
		};
		pageToolBar.bind(loader);
		pageToolBar.setBorders(false);
		PagingToolBarMessages barMsg = pageToolBar.getMessages();
		barMsg.setDisplayMsg("");
		barMsg.setEmptyMsg("");
	}
	
	public PagingToolBar getPageToolBar()
	{
		return pageToolBar;
	}
	
	public void AddWidget(Component widget)
	{
		toolWidget.put("", widget);
	}
	
	public void AddButton(String btnName,Button btn) {
		toolWidget.put(btnName, btn);
	}
	
	public void AddButton(String btnName,Button btn, AbstractImagePrototype icon,
			SelectionListener<ButtonEvent> listener) {
		btn.setIcon(icon);
		btn.addSelectionListener(listener);
		toolWidget.put(btnName, btn);
	}
	
	public void AddMenuButton(String btnName,Button btn, AbstractImagePrototype icon,
			SelectionListener<ButtonEvent> listener)
	{
		AddButton(btnName,btn,icon,listener);
		Menu menu = btn.getMenu();
		if(menu != null)
		{
			for(Component menuItem : menu.getItems())
				menuWidget.put(menuItem.getItemId(), menuItem);
		}
	}
	
	public void AddNewBtn(String btnName,SelectionListener<ButtonEvent> listener) {
		AddButton(btnName,new Button("添加"), MainPage.ICONS.AddCom(), listener);
	}
	
	public void AddEditBtn(String btnName,SelectionListener<ButtonEvent> listener) {
		AddButton(btnName,new Button("编辑"), MainPage.ICONS.EditCom(), listener);
	}
	
	public void AddDelBtn(String btnName,final SelectionListener<ButtonEvent> listener) {
		AddButton(btnName, new Button("删除"), MainPage.ICONS.DelCom(),listener);
	}
	
	public void AddDelBtn(String btnName,final Listener<MessageBoxEvent> listener) {
		AddDelBtn(btnName,
				new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						MessageBox.confirm("提示信息", "是否确认删除", listener);
					}
				});
	}
	
	@Override
	public void ButtonInit(Map<String, GWTButtonConfig> btnConfigMap) {
		if(pageToolBar != null)
		{
			add(pageToolBar);
			add(new SeparatorToolItem());
		}
		
		for (String key : toolWidget.keySet())
			Config(btnConfigMap, key, toolWidget.get(key), true);

		for (String key : menuWidget.keySet())
			Config(btnConfigMap, key, menuWidget.get(key), false);
	}
	
	private void Config(Map<String, GWTButtonConfig> btnConfigMap,String key,Component widget,boolean needAdd)
	{
		if(key.isEmpty() && needAdd)
			add(toolWidget.get(key));
		else
		{
			GWTButtonConfig config = btnConfigMap.get(key);
			if(config == null)
				config = new GWTButtonConfig();
			
			//是否可见
			if(!config.GetVisible())
				widget.setVisible(false);
			else
			{
				//是否需要禁用
				if(!config.GetEnable())
				{
					widget.setToolTip("当前版本不支持本功能");
					widget.disable();
				}
				//设置选择模式
				else
				{
					String type = config.GetBtnType();
					widget.disable();
					if(type.equalsIgnoreCase("multi"))
						AddMulti(widget);
					else if(type.equalsIgnoreCase("single"))
						AddSingle(widget);
					else if(type.isEmpty())
						widget.enable();
						
				}
				if(needAdd)
					add(widget);
			}
		}
	}
}
