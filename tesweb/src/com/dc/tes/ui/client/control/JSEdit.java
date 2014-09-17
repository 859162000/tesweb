package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.Element;

public class JSEdit extends Component {
	Element iframe;
	Element contentDiv;
	private static int ID = 0;
	private String url = "scriptEdit.html";
	public JSEdit() {
		this(400);
	}
	
	public JSEdit(int height) {
		ID++;
		StringBuffer sb = new StringBuffer();
		sb.append("<iframe id = 'editScriptFrm' width='100%' height=' " + height + "' scrolling='no' frameborder='0' " +
//				"style='height:400px;width :100%; border-width:0px; '  " +
				"src='" + url + "'" +
				" onreadystatechange = 'try {if (!this.contentWindow || !this.contentWindow.document)" +
				" return;var scriptContent = document.getElementById(\"divContentScript" + ID +
				"\").innerText;" +
				" this.contentWindow.setTextValue(scriptContent);} catch (e) {return ;}'>" +
				"</iframe>");
		iframe = new El(sb.toString()).dom;
		
		sb = new StringBuffer();
		sb.append("<div id = 'divContentScript" + ID + "' style = 'display:none;'></div>");
		contentDiv = new El(sb.toString()).dom;
	}

	@Override
	protected void onRender(Element target, int index) {
		try {
			Element divElement = new El("<div></div>").dom;
			divElement.appendChild(contentDiv);
			divElement.appendChild(iframe);
			
			setElement(divElement, target, index);

			if (GXT.isIE && GXT.isSecure) {
				getElement().setPropertyString("src", GXT.SSL_SECURE_URL);
			}
			el().insertInto(target, index);
		} catch (Exception ex) {
		}
	}
	
	public String getValue() {
		return GetValue(iframe);
	}
	
	public void setValue(String value)
	{
		value = value.replaceAll("\r\n", "<BR>");
		value = value.replaceAll("\t", "\u2008");
//		contentDiv.setInnerText(value);//.setInnerHTML(value);
		contentDiv.setInnerHTML(value);
	}
	
	public void setIFrameHeight(int height)
	{
		iframe.setAttribute("height", height + "px");
	}

	/**
	 * 从Iframe中获得文本框的值
	 * @param frm  iframe元素
	 * @return  iframe中文本框的值
	 */
	private native String GetValue(Element frm) /*-{
		try {
		 if (!frm.contentWindow || !frm.contentWindow.document)
		   return null;
		return frm.contentWindow.getTextValue();
		} catch (e) {
		 return null;
		}
	}-*/;
}
