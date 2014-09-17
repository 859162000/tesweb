//package com.dc.tes.ui.client.control;
//
//import com.dc.tes.ui.client.MainPage;
//import com.extjs.gxt.ui.client.data.ModelData;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.MessageBoxEvent;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.widget.Component;
//import com.extjs.gxt.ui.client.widget.MessageBox;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
//import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar.PagingToolBarMessages;
//import com.google.gwt.user.client.ui.AbstractImagePrototype;
//
//public class GridBottomToolBar<T extends ModelData> extends ButtonBar {
//	private PagingToolBar pageToolBar = null;
//	
//	public GridBottomToolBar(GridContentPanel<T> gridPanel,int pageSize)
//	{
//		setPageSize(pageSize);
//		pageToolBar = new PagingToolBar(pageSize)
//		{
//			//处理没有记录时，输入页码导致的页面按钮可用的bug
//			@Override
//			public void last() {
//				if(super.totalLength == 0)
//				{
//					setActivePage(1);
//					return;
//				}
//				super.last();
//			}
//		};
//		pageToolBar.bind(gridPanel.getLoader());
//		pageToolBar.setBorders(false);
//		PagingToolBarMessages barMsg = pageToolBar.getMessages();
//		barMsg.setDisplayMsg("");
//		barMsg.setEmptyMsg("");
//		
//		add(pageToolBar);
//		add(new SeparatorToolItem());
//	}
//	
//	public GridBottomToolBar(GridContentPanel<T> gridPanel)
//	{
//		this(gridPanel,10);
//	}
//
//	public void AddButtonSingle(Button btn, AbstractImagePrototype icon,
//			SelectionListener<ButtonEvent> listener) {
//		AddButtonSingle(btn.getId(),btn,icon,listener);
//	}
//	
//	public void AddButtonSingle(String btnName,Button btn, AbstractImagePrototype icon,
//			SelectionListener<ButtonEvent> listener) {
//		AddButton(btnName,btn, icon, listener);
//		AddSingle(btn);
//		btn.disable();
//	}
//
//	public void AddButtonMulti(Button btn, AbstractImagePrototype icon,
//			SelectionListener<ButtonEvent> listener) {
//		AddButtonMulti(btn.getId(),btn,icon,listener);
//	}
//	
//	public void AddButtonMulti(String btnName,Button btn, AbstractImagePrototype icon,
//			SelectionListener<ButtonEvent> listener) {
//		AddButton(btnName,btn, icon, listener);
//		AddMulti(btn);
//		btn.disable();
//	}
//
//	public void AddButton(Button btn, AbstractImagePrototype icon,
//			SelectionListener<ButtonEvent> listener) {
//		AddButton(btn.getId(),btn,icon,listener);
//	}
//	
//	public void AddButton(String btnName,Button btn, AbstractImagePrototype icon,
//			SelectionListener<ButtonEvent> listener) {
//		btn.setIcon(icon);
//		btn.addSelectionListener(listener);
//		AddButton(btn);
//	}
//
//	public void AddButton(Component btn) {
//		add(btn);
//	}
//
//	public void AddNewBtn(SelectionListener<ButtonEvent> listener) {
//		AddNewBtn("btnAdd",listener);
//	}
//	
//	public void AddNewBtn(String btnName,SelectionListener<ButtonEvent> listener) {
//		AddButton(btnName,new Button("添加"), MainPage.ICONS.AddCom(), listener);
//	}
//
//	public void AddEditBtn(SelectionListener<ButtonEvent> listener) {
//		AddEditBtn("btnEdit",listener);
//	}
//	
//	public void AddEditBtn(String btnName,SelectionListener<ButtonEvent> listener) {
//		AddButtonSingle(btnName,new Button("编辑"), MainPage.ICONS.EditCom(), listener);
//	}
//
//	public void AddDelBtn(final Listener<MessageBoxEvent> listener)
//	{
//		AddDelBtn("btnDel",listener);
//	}
//	
//	public void AddDelBtn(String btnName,final Listener<MessageBoxEvent> listener) {
//		AddButtonMulti(btnName, new Button("删除"), MainPage.ICONS.DelCom(),
//				new SelectionListener<ButtonEvent>() {
//					public void componentSelected(ButtonEvent ce) {
//						MessageBox.confirm("提示信息", "是否确认删除", listener);
//					}
//				});
//	}
//}
