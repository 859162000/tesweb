package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;

/**
 * 关闭前需要缓存的页面
 * @author scckobe
 *
 */
public class BasePage_Register extends BasePage {
	/**
	 * 按钮列表
	 */
	protected List<Button> btnList = new ArrayList<Button>();
	
	/**
	 * 构造函数
	 */
	public BasePage_Register()
	{
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
//		AppContext.getTabPanel().getSelectedItem().addListener(
//				Events.BeforeClose, TabColseListener());
//		AppContext.RegisterPage(GetParentTabID(), this);
	}
	
	/**
	 * 获得当前的TabItem的ItemID
	 * @return	当前的TTabItem的ItemID
	 */
	private String GetParentTabID()
	{
		return AppContext.getTabPanel().getSelectedItem().getItemId();
	}
	
	/**
	 * 当前页面是否需要进行客户端保存
	 * @return	是否需要进行客户端保存
	 */
	protected boolean NeedRegister()
	{
		return true;
	}
	
	/**
	 * 执行任务、脚本
	 */
	public void Exec()
	{
//		throw new Exception("本页面无执行操作");
	}
	
	/**
	 * 重新刷新页面某一部分控件
	 */
	public void PageReconfigure()
	{
	}
	
	/**
	 * 关闭Tab页事件，用于判断页面是否需要清除缓存
	 * 
	 * @return
	 */
	private Listener<BaseEvent> TabColseListener() {
		return new Listener<BaseEvent>() {
			@Override
			public void handleEvent(final BaseEvent be) {
				if(!NeedRegister())
				{
					AppContext.UnRegisterPage(GetParentTabID());
				}
			}
		};
	}
}
