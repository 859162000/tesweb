package com.dc.tes.ui.client.control.Result;

import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;


public class LoadResultWin implements IResultWin {
	
	Window loadingWin = new Window();
	int isClientSimu = 1;
	private String charSet = "gb2312"; 
	
	public LoadResultWin(String msg)
	{
		loadingWin = new Window();
		loadingWin.setHeaderVisible(false);
		loadingWin.setBlinkModal(false);
		loadingWin.setResizable(false);
		loadingWin.setBodyBorder(false);
		loadingWin.setBorders(false);
		loadingWin.setDraggable(false);
		loadingWin.setFrame(false);
		loadingWin.setModal(true);
		loadingWin.setStyleAttribute("background-color", "white");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<span style='width:100%;height:100%;text-align:center; padding-top:30px; font-size:12px;'>" +
				"<img src='gxt/images/default/shared/large-loading.gif' height='32'/>" +
				msg +
				"</span>");
		loadingWin.addText(sb.toString());
//		HtmlContainer loadControl = new HtmlContainer();
//		loadControl = new HtmlContainer(sb.toString());
//		loadControl.removeAll();
//		loadingWin.add(loadControl);
		loadingWin.show();
	}
	
	@Override
	public String GetCharSet() {
		return charSet;
	}

	@Override
	public void setIsClientSimu(int isClient) {
		this.isClientSimu = isClient;
	}
	
	@Override
	public void SetValue(String value) {
		//不做任何处理
	}

	@SuppressWarnings("deprecation")
	@Override
	public void Close() {
		loadingWin.close();
	}

	@Override
	public void SetComboListner(
			SelectionChangedListener<SimpleComboValue<String>> listner) {
		//不做任何处理
	}

	@Override
	public void Show() {
		if(loadingWin != null)
			loadingWin.show();
	}

	@Override
	public void SetCharSet(String charSet) {
		// TODO Auto-generated method stub
		this.charSet = charSet;
	}
}
