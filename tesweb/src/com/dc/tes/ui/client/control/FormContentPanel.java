package com.dc.tes.ui.client.control;

import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;

import com.extjs.gxt.ui.client.widget.layout.FormData;

public class FormContentPanel<T extends ModelData> extends FormPanel {
	
	FormBinding formBindings;
	
	/**
	 * 默认构造函数
	 * 
	 * 初始化FormPanel的默认样式
	 */
	public FormContentPanel()
	{
		this.setWidth("100%");
//		this.setAutoWidth(true);
		this.setHeaderVisible(false);
		this.setBodyBorder(false);
		this.setScrollMode(Scroll.AUTOY);
		this.setPadding(15);
	}
	
	public void setBindInfo(Map<String,String> detailInfo)
	{
		for(String key : detailInfo.keySet())
		{
			LabelField lbDetail = new LabelField();
			lbDetail.setName(key);
			
			//lbDetail.setWidth(400);			
			//lbDetail.setAutoHeight(true);
			lbDetail.setBorders(true);		
			lbDetail.setFieldLabel("<b>" + detailInfo.get(key) + "</b>");
			lbDetail.setStyleAttribute("float", "left");
			this.add(lbDetail, new FormData("100%"));
		}
		
		setNoDisplay();
		formBindings = new FormBinding(this, true);
	}
	
	
	public void bind(List<T> bindData)
	{
		if(bindData.size() > 0)
		{
			setDisplay();
			formBindings.bind((ModelData) bindData.get(0));
		}
		else{
			setNoDisplay();
		}
	}
	
	private void setDisplay()
	{
		this.setStyleAttribute("display", "");
	}
	
	private void setNoDisplay()
	{
		this.setStyleAttribute("display", "none");
	}
	
	
}
