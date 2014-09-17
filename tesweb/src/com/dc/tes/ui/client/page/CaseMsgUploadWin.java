package com.dc.tes.ui.client.page;

import com.dc.tes.ui.client.MainPage;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class CaseMsgUploadWin {
	private Window window;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private IUserLoader myLoader;
	private boolean needLoader = false;
	
	public CaseMsgUploadWin(PagingLoader<PagingLoadResult<ModelData>> loader)
	{
		this.loader = loader;
	}
	
	public CaseMsgUploadWin(IUserLoader myLoader)
	{
		this.myLoader = myLoader;
	}
	
	public CaseMsgUploadWin() {
		
	}
	
	
	//帮助那些不好继承loader的控件,当关闭上传成功窗口时，做想做的事
	public void setWindowHideEvent(Listener<? extends BaseEvent> listener) {
		window.addListener(Events.Hide, listener);
	}
	
	public void Show(String header,final String msg,
			String action)
	{
		window = new Window();

		window.setHeading(header);
		window.setIcon(MainPage.ICONS.Upload());
		window.setSize(500, 160);
		window.setClosable(false);
		window.setPlain(true);
		window.setModal(true);
		window.setLayout(new FitLayout());
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(60);
		formPanel.setPadding(4);
		formPanel.setHeaderVisible(false);
		
		//将上传文件提交到servlet
		formPanel.setEncoding(FormPanel.Encoding.MULTIPART);

		formPanel.setMethod(FormPanel.Method.POST);
		formPanel.setAction(action);
		formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

			@Override
			public void handleEvent(FormEvent be) {
						
						window.hide();
						formPanel.unmask();
						String returnMsg = be.getResultHtml();
						if(returnMsg.equals(""))return;
						String res[] = returnMsg.substring(1, returnMsg.length() - 1).split(",");
						
						//格式化上传结果
						String html = "";
						for(String str:res){
							if(str.isEmpty())
								continue;
							html += getTemplate(str);
						}
						int height = res.length > 15 ? 300 : Math.max(200,res.length * 20);
						html = "<div style=\"height:" + height + ";overflow:auto;\">" + html + "</div>";
						
						window = new Window();
						window.setHeading("上传结果");
						window.addText(html);
					    Button btnClose = new Button("关闭");
					    btnClose.addSelectionListener(new SelectionListener<ButtonEvent>(){
							@Override
							public void componentSelected(ButtonEvent ce) {
								window.hide();
							}});
					    window.addButton(btnClose);
					    window.show();
						
					    if (needLoader) {
							if (loader != null)
								loader.load();
							else if (myLoader != null)
								myLoader.load();
						}
					}
				});
		
		final RadioGroup msgTypeGroup = new RadioGroup();
		msgTypeGroup.setFieldLabel("报文类型");
		Radio rd_Str = new Radio();
		rd_Str.setBoxLabel("字符串");
		rd_Str.setData("data", "0");
		rd_Str.setValue(true);
		msgTypeGroup.add(rd_Str);
		Radio rd_Hex = new Radio();
		rd_Hex.setBoxLabel("16进制字符");
		rd_Hex.setToolTip("AC 2E B8...或 AC2EB8...");
		rd_Hex.setData("data", "1");
		msgTypeGroup.add(rd_Hex);
		Radio rd_Byte = new Radio();
		rd_Byte.setBoxLabel("字节字符");
		rd_Byte.setToolTip("[32, 48, 50...]");
		rd_Byte.setData("data", "2");
		msgTypeGroup.add(rd_Byte);
		formPanel.add(msgTypeGroup);
		
		final RadioGroup wayGroup = new RadioGroup();
		wayGroup.setFieldLabel("上传方式");
		Radio rd_fileUpload = new Radio();
		rd_fileUpload.setBoxLabel("文件上传");
		rd_fileUpload.setData("value", "0");
		rd_fileUpload.setValue(true);
		wayGroup.add(rd_fileUpload);
		Radio rd_paste = new Radio();
		rd_paste.setBoxLabel("直接输入");
		rd_paste.setData("value", "1");
		wayGroup.add(rd_paste);
		formPanel.add(wayGroup);
		
		final TextArea ta_content = new TextArea();
		ta_content.setFieldLabel("报文内容");
		ta_content.setHeight(300);
		ta_content.setVisible(false);
		formPanel.add(ta_content, new FormData("99%"));
		
		final FileUploadField  fpTranStruct = new FileUploadField ();
		fpTranStruct.setLabelStyle("");
		fpTranStruct.setFieldLabel("文件上传");
		fpTranStruct.setLabelSeparator("");
		fpTranStruct.setName("fpUpload");	
		formPanel.add(fpTranStruct, new FormData("99%"));
		
		wayGroup.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(wayGroup.getValue().getData("value").equals("0")){
					fpTranStruct.setVisible(true);
					ta_content.setVisible(false);
					ta_content.setAllowBlank(true);
					fpTranStruct.setAllowBlank(false);	
					window.setHeight(160);
				}else{
					fpTranStruct.setVisible(false);
					ta_content.setVisible(true);
					ta_content.setAllowBlank(false);
					fpTranStruct.setAllowBlank(true);	
					window.setHeight(430);
				}
			}
		});
		
		Button btnOK = new Button("上传", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if(wayGroup.getValue().getData("value").equals("0")){ //文件上传
					if(fpTranStruct.getValue().trim().equals("")){			
						return;
					}
				}else{
					if(ta_content.getValue()==null || ta_content.getValue().isEmpty()){	
						return;
					}
				}
				String url = formPanel.getAction();
				url +="&msgType=" + msgTypeGroup.getValue().getData("data")
					+"&uploadWay=" + wayGroup.getValue().getData("value")
					+"&content=" + ta_content.getValue();
				formPanel.setAction(url);
				formPanel.submit();
				formPanel.mask(msg);
			}
		});
		
		Button btnCancel = new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		});
		
		window.addButton(btnOK);
		window.addButton(btnCancel);
		window.add(formPanel);
		
		window.show();
	}
	
	private String getTemplate(String msg){
		String template = "";
		String image = "";
		String name = "";
		if(msg.trim().startsWith("newadd")){
			needLoader = true;
			image = "add.png";
		}else if(msg.trim().startsWith("edit")){
			needLoader = true;
			image = "right.png";
		}else{
			image = "delete.png";
		}
		name = msg.split(":")[1];
		template = "<div style = 'height:20px;' ><img src='gxt/images/cus/" + image + "'>&nbsp;&nbsp;&nbsp;&nbsp;" + name + "</div>";
		return template;
	}
}
