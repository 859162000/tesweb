package com.dc.tes.ui.client.control.Result;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class HexResultWin implements IResultWin {
	
	private Window wData = new Window();
	private SimpleComboBox<String> charsetCombox;
	private TextArea tfdata;
	private int isClientSimu = 1;
	private String charSet = "gb2312";
	public HexResultWin(String header,String value)
	{
		wData.setHeading(header);
		wData.setSize(520, 400);
		wData.setPlain(true);
		wData.setModal(true);
		wData.setResizable(false);
		wData.setBlinkModal(false);
		wData.setLayout(new RowLayout());

		charsetCombox = new SimpleComboBox<String>();
		charsetCombox.setInEditor(false);
		charsetCombox.setEditable(true);
		charsetCombox.add("UTF-8");
		charsetCombox.add("gb2312");
		charsetCombox.setHeight(22);
		charsetCombox.setWidth("99%");
//		charsetCombox.add("Unicode");  暂时不要，会出现IE down的问题
		charsetCombox.setFieldLabel("请选择编码方式");
		charsetCombox.setToolTip("请选择编码方式");
		charsetCombox.setStyleAttribute("text-algain", "right");
		charsetCombox.setSimpleValue("gb2312");
		
		charsetCombox.setUseQueryCache(false);
		//强制
//		charsetCombox.addListener(Events.TriggerClick, new Listener<BaseEvent> () {
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				// TODO Auto-generated method stub
//				charsetCombox.doQuery("", true);
//			}
//			
//		});
		
		tfdata = new TextArea();
		tfdata.setValue(value);
		tfdata.setWidth(500);
		tfdata.setHeight(310);
		//设置等宽字体
//		tfdata.setStyleAttribute("white-space", "pre");
		tfdata.setStyleAttribute("font-family", "'宋体', Simsun");
		tfdata.setReadOnly(true);
		wData.addButton(new Button("关闭", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				wData.close();
			}
		}));
		wData.setButtonAlign(HorizontalAlignment.CENTER);
		
		wData.add(charsetCombox);
		wData.add(tfdata, new FormData("96%"));
		wData.show();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void Close() {
		wData.close();
	}

	@Override
	public String GetCharSet() {
		if(charsetCombox == null)
			return charSet;
		
		return charsetCombox.getSimpleValue();
	}

	@Override
	public void setIsClientSimu(int isClient) {
		this.isClientSimu = isClient;
	}
	
	@Override
	public void SetValue(String value) {
		if(tfdata == null)
			return;
		
		tfdata.setValue(value);
		tfdata.focus();
	}

	@Override
	public void SetComboListner(
			SelectionChangedListener<SimpleComboValue<String>> listner) {
		if(charsetCombox == null)
			return;
		
		charsetCombox.addSelectionChangedListener(listner);
	}

	@Override
	public void Show() {
		if(wData != null)
			wData.show();
	}

	@Override
	public void SetCharSet(String charSet) {
		// TODO Auto-generated method stub
		this.charSet = charSet;
	}
}
