package com.dc.tes.ui.client.control.Result;

import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;

public interface IResultWin {
	void SetValue(String value);
	void setIsClientSimu(int isClient);
	String GetCharSet();
	void SetCharSet(String charSet);
	void Show();
	void Close();
	void SetComboListner(SelectionChangedListener<SimpleComboValue<String>> listner);
}
