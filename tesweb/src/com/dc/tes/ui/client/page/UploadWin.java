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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;


/**
 * 上传对话框
 * 
 * 1)弹出对话框
 * 2)实行上传
 * 3)上传结果显示
 * 4)页面刷新
 * @author Administrator
 *
 */
public class UploadWin {
	private Window upWindow;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private IUserLoader myLoader;
	private boolean needLoader = false;
	
	public UploadWin(PagingLoader<PagingLoadResult<ModelData>> loader)
	{
		this.loader = loader;
	}
	
	public UploadWin(IUserLoader myLoader)
	{
		this.myLoader = myLoader;
	}
	
	public UploadWin() {
		
	}
	
	
	//帮助那些不好继承loader的控件,当关闭上传成功窗口时，做想做的事
	public void setWindowHideEvent(Listener<? extends BaseEvent> listener) {
		upWindow.addListener(Events.Hide, listener);
	}
	
	public void Show(String header,final String msg,
			String action)
	{
		upWindow = new Window();

		upWindow.setHeading(header);
		upWindow.setIcon(MainPage.ICONS.Upload());
		upWindow.setSize(500, 100);
		upWindow.setClosable(false);
		upWindow.setPlain(true);
		upWindow.setModal(true);
		upWindow.setLayout(new FitLayout());
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(0);
		formPanel.setPadding(4);
		formPanel.setHeaderVisible(false);
		
		//将上传文件提交到servlet
		formPanel.setEncoding(FormPanel.Encoding.MULTIPART);

		formPanel.setMethod(FormPanel.Method.POST);
		formPanel.setAction(action);
		formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

			@Override
			public void handleEvent(FormEvent be) {
						
						upWindow.hide();
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
						
						upWindow = new Window();
						upWindow.setHeading("上传结果");
						upWindow.addText(html);
					    Button btnClose = new Button("关闭");
					    btnClose.addSelectionListener(new SelectionListener<ButtonEvent>(){
							@Override
							public void componentSelected(ButtonEvent ce) {
								upWindow.hide();
							}});
					    upWindow.addButton(btnClose);
					    upWindow.show();
						
					    if (needLoader) {
							if (loader != null)
								loader.load();
							else if (myLoader != null)
								myLoader.load();
						}
					}
				});
		
		final FileUploadField  fpTranStruct = new FileUploadField ();
		fpTranStruct.setLabelStyle("");
		fpTranStruct.setFieldLabel("");
		fpTranStruct.setLabelSeparator("");
		fpTranStruct.setName("fpUpload");
		fpTranStruct.setAllowBlank(false);
		
		formPanel.add(fpTranStruct, new FormData("99%"));
		
		Button btnOK = new Button("上传", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if(!fpTranStruct.getValue().trim().equals("")){
					formPanel.submit();
					formPanel.mask(msg);
				}
			}
		});
		
		Button btnCancel = new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				upWindow.close();
			}
		});
		
		upWindow.addButton(btnOK);
		upWindow.addButton(btnCancel);
		upWindow.add(formPanel);
		
		upWindow.show();
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
