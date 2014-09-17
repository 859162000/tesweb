package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class PostFormPanel extends FormPanel {
	
	public PostFormPanel() {
		super();
	}
	
	public void submit() {
		this.setVisible(false);
		render(RootPanel.get().getElement());
		super.onAttach();
		super.submit();
	}

}
