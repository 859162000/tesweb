package com.dc.tes.ui.client.control;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class ButtonBar extends ToolBar {
	private int pageSize = 10;
	private List<Component> singleButton = new ArrayList<Component>();
	private List<Component> multiButton = new ArrayList<Component>();
	
	protected ButtonBar()
	{
	}
	
	
	protected void ButtonEnabled(int selectedSize)
	{
		for (int i = 0; i < singleButton.size(); i++) {
			if (selectedSize == 1)
				singleButton.get(i).enable();
			else
				singleButton.get(i).disable();
		}

		for (int i = 0; i < multiButton.size(); i++) {
			if (selectedSize > 0)
				multiButton.get(i).enable();
			else
				multiButton.get(i).disable();
		}
	}
	
	public int getPageSize()
	{
		return pageSize;
	}
	
	protected void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
	
	protected void AddSingle(Component widget)
	{
		singleButton.add(widget);
	}
	
	protected void AddMulti(Component widget)
	{
		multiButton.add(widget);
	}
}
