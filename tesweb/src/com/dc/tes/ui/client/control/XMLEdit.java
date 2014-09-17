package com.dc.tes.ui.client.control;


import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.BoxComponent;

import com.google.gwt.user.client.Element;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class XMLEdit extends BoxComponent {
	String url;
	String value;
	Element iframe;
	Element contentDiv;
		
	StringBuffer buffer = new StringBuffer();
	String brStr = "<BR>";
	String tabStr = "&nbsp;&nbsp;";
	
	public XMLEdit() {
		this("msgpackview.html");
	}
	
	public XMLEdit(String url) {
		this.url = url;
		AddFrame();
	}
	
	private void AddFrame()
	{
		String id = "divContentXML"+this.hashCode();
		StringBuffer sb = new StringBuffer();
		sb.append("<iframe id = 'editXMLFrm' width='100%' height='500' scrolling='no' frameborder='0' " +
				" src='" + url + "'" +
				" onreadystatechange = 'try {if (!this.contentWindow || !this.contentWindow.document)return;" +
				"var scriptContent = document.getElementById(\""+id+"\").innerText;" +
				"this.contentWindow.setTextValue(scriptContent);} catch (e) {return ;}'>" +
				"</iframe>");
		iframe = new El(sb.toString()).dom;
		
		sb = new StringBuffer();
		sb.append("<div id = '" + id + "' style = 'display:none;'></div>");
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
	
	public void setValue(String value)
	{	//by ljs
		
// 		if(!value.isEmpty()) {
// 			String header = "";
// 			int f = value.indexOf("<?");
// 			int l = value.indexOf("?>");
// 			if(f !=-1 && l !=-1) {
// 				header = value.substring(f, l+2);
// 				header = header.replaceAll("<", "&lt;");
// 				header = header.replaceAll(">", "&gt;");
// 				buffer.append(header);
// 				buffer.append(brStr);
// 			}
// 			buffer.append(value);
// 			parse(value);
// 			contentDiv.setInnerHTML(buffer.toString());
// 		} else {
// 			contentDiv.setInnerHTML("");
// 		}
 		
		
		value = value.replaceAll("><", ">\r\n<");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		value = value.replaceAll("\r\n", "<BR>&nbsp;");
		contentDiv.setInnerHTML(value);
		
		/*
		//TODO:scckobe 试进行格式化
		String[] lines = value.split("<BR>");
		StringBuilder content = new StringBuilder(500);
		int lineCount = lines.length;
		int halfCount = lineCount / 2;
		String brStr = "<BR>";
		String tabStr = "&nbsp;&nbsp;";
		for(int i = 0; i < lineCount; i++)
		{
			String appStr = lines[i].trim();
			if(i == 0)
				content.append(appStr + brStr);
			else if(i == lineCount - 1)
				content.append(appStr);
			else if(i < halfCount)
			{
				
				content.append(lines[i].trim() + brStr);
				for(int j = 0;j<i;j++)
					content.append(tabStr);
			}
			else if(i > halfCount)
			{
				content.append(appStr);
			}
			else
			{
				content.append(appStr);
			}
		}
		contentDiv.setInnerHTML(content.toString());
		*/

	}

	public void parse(String value) {   
		
		try {   
        	Document doc = XMLParser.parse(value);
        	com.google.gwt.xml.client.Element root = doc.getDocumentElement();          
        	readNode(root,-1);
        } catch (Exception e) {   
            e.printStackTrace();   
        }    
    }

	private void readNode(Node element,int hierarchy) {
		// TODO Auto-generated method stub
		NodeList childNodes = element.getChildNodes();	
		hierarchy++;		
		int length = childNodes.getLength();
		if(element.getNodeType() != 3) {
			buffer.append(wrapNode(element.getNodeName(),element.getAttributes(), hierarchy));
			for(int i=0; i<length; i++) {
				if(element.hasChildNodes() && element.getFirstChild().getNodeType() != 3)
					buffer.append(brStr);
			    readNode(childNodes.item(i), hierarchy);
			}
		} else {
			buffer.append(element.getNodeValue());
		}
		
		if(element.getNodeType() !=3 ) {
			if((element.hasChildNodes() && element.getFirstChild().getNodeType() == 3)|| !element.hasChildNodes()) {
				buffer.append("&lt;"+"/"+element.getNodeName()+"&gt;");
			} else {
				buffer.append(brStr);
			    buffer.append(wrapNode("/"+element.getNodeName(),hierarchy));
			}
		}
	} 
	
	private String wrapNode(String name, int hierarchy) {
		StringBuffer wrap = new StringBuffer();
		for(int i=0; i<hierarchy; i++)
			wrap.append(tabStr);
		wrap.append("&lt;");
		wrap.append(name);
		wrap.append("&gt;");
		
		return wrap.toString();
		
	}
	
	//add by aite  添加对属性的支持
	private String wrapNode(String name, NamedNodeMap attrs, int hierarchy) {
		StringBuffer wrap = new StringBuffer();
		for(int i=0; i<hierarchy; i++)
			wrap.append(tabStr);
		wrap.append("&lt;");
		wrap.append(name);
		for(int i=0; i<attrs.getLength(); i++){
			Node node = attrs.item(i);
			wrap.append(" " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"");
		}
		wrap.append("&gt;");
		
		return wrap.toString();
		
	}
}
