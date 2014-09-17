package com.dc.tes.ui.client.control.Result;

import com.dc.tes.ui.client.control.XMLEdit;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class XMLResultWin implements IResultWin {
	
	private Window msgViewWindow;
	private XMLEdit xmlEdit;
	int isClientSimu = 1;
	private String charSet = "gb2312";
	
	public XMLResultWin(String header,String value)
	{
		msgViewWindow = new Window();

		msgViewWindow.setHeading(header);
		msgViewWindow.setSize(600, 540);
		msgViewWindow.setPlain(true);
		msgViewWindow.setModal(true);
		msgViewWindow.setDraggable(false);
		msgViewWindow.setResizable(false);
		msgViewWindow.setBlinkModal(false);
		msgViewWindow.setLayout(new FitLayout());
		
		xmlEdit = new XMLEdit();
		xmlEdit.setValue(value);
		msgViewWindow.add(xmlEdit);
		msgViewWindow.show();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void Close() {
		msgViewWindow.close();
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
		xmlEdit.setValue(value);
	}

	@Override
	public void SetComboListner(
			SelectionChangedListener<SimpleComboValue<String>> listner) {
		// 不做处理
	}

	@Override
	public void Show() {
		if(msgViewWindow != null)
			msgViewWindow.show();
	}
	
	@Override
	public void SetCharSet(String charSet) {
		// TODO Auto-generated method stub
		this.charSet = charSet;
	}
}
