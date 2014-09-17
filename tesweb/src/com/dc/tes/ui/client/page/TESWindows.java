package com.dc.tes.ui.client.page;

import com.dc.tes.ui.client.control.XMLEdit;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * 组包预览对话框
 * @author scckobe
 *
 */
public class TESWindows {
	
	private static Window ConstructLoadingWin(String msg)
	{
		Window loadingWin = new Window();
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
		HtmlContainer loadControl = new HtmlContainer();
		StringBuffer sb = new StringBuffer();
		sb.append("<span style='width:100%;height:100%;text-align:center; padding-top:30px; font-size:12px;'>" +
				"<img src='gxt/images/default/shared/large-loading.gif' height='32'/>" +
				msg +
				"</span>");
		loadControl = new HtmlContainer(sb.toString());
		loadControl.removeAll();
		loadingWin.add(loadControl);
		
		return loadingWin;
	}
	
	public static AsyncCallback<String> setValueAsyncCallback(String value,String msg)
	{
		return new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("错误信息", "获取失败，请与管理员联系", null);
			}

			@Override
			public void onSuccess(String result) {
				if(result.trim().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
					ShowXMLWin(result);
				else
					ShowCommonWin(result);
			}
		};
	}
	
	public static AsyncCallback<String> ShowWinCallBack(String value,String msg)
	{
		final Window loadingWin = ConstructLoadingWin(msg);
		loadingWin.show();
		
		return new AsyncCallback<String>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onFailure(Throwable caught) {
				loadingWin.close();
				MessageBox.alert("错误信息", "获取失败，请与管理员联系", null);
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(String result) {
				loadingWin.close();
				if(result.trim().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
					ShowXMLWin(result);
				else
					ShowCommonWin(result);
			}
		};
	}
	
	private static void ShowCommonWin(String value)
	{
		final Window wData = new Window();

		wData.setHeading("案例数据");
		wData.setSize(520, 400);
		wData.setPlain(true);
		wData.setModal(true);
		wData.setResizable(false);
		wData.setBlinkModal(false);
		wData.setLayout(new FitLayout());

		final SimpleComboBox<String> charsetCombox = new SimpleComboBox<String>();
		charsetCombox.setInEditor(false);
		charsetCombox.setEditable(false);
		charsetCombox.add("UTF-8");
		charsetCombox.add("gb2312");
		charsetCombox.add("Unicode");
		charsetCombox.setFieldLabel("请选择编码方式");
		charsetCombox.setStyleAttribute("text-algain", "right");
		charsetCombox.setSimpleValue("UTF-8");
		
		final TextArea tfdata = new TextArea();
		tfdata.setWidth(500);
		tfdata.setHeight(310);
		//设置等宽字体
		tfdata.setStyleAttribute("font-family", "'宋体', Simsun");
		tfdata.setStyleAttribute("white-space", "pre");
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
	
	private static void ShowXMLWin(String value)
	{
		Window msgViewWindow;
		
		msgViewWindow = new Window();

		msgViewWindow.setHeading("组包预览");
		msgViewWindow.setSize(700, 600);
		msgViewWindow.setPlain(true);
		msgViewWindow.setModal(true);
		msgViewWindow.setLayout(new FitLayout());
		
		final XMLEdit jsEdit = new XMLEdit();
		jsEdit.setValue(value);
		msgViewWindow.add(jsEdit);

		msgViewWindow.show();
	}
	
	@SuppressWarnings("unused")
	private static String GetSimpleValue()
	{
		return "Hello World!";
	}
	
//	private static String GetTestValue()
//	{
//		String Name = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\r\n&lt;Transaction&gt;\r\n";
//		Name += "  &lt;Transaction_Header&gt;\r\n";
//		Name += "    &lt;processes currentprocess=\"1\"&gt;\r\n";
//		Name += "      &lt;process id=\"1\"&gt;\r\n";
//		Name += "        &lt;end_timestamp&gt;2009-10-29 16:29:56.916&lt;/end_timestamp&gt;\r\n";
//		Name += "        &lt;process_timestamp&gt;2009-10-29 16:29:56.579&lt;/process_timestamp&gt;\r\n";
//		Name += "        &lt;target_q&gt;CCB.TRANS.REQUEST.OUT.DCC.NORTH&lt;/target_q&gt;\r\n";
//		Name += "        &lt;sub_target_id&gt;0002&lt;/sub_target_id&gt;\r\n";
//		Name += "        &lt;target_id&gt;0001&lt;/target_id&gt;\r\n";
//		Name += "        &lt;sync_reversal_trans_id&gt;99988840199901&lt;/sync_reversal_trans_id&gt;\r\n";
//		Name += "        &lt;async_reversal_trans_id&gt;99988800199901&lt;/async_reversal_trans_id&gt;\r\n";
//		Name += "        &lt;sub_branch_id&gt;110001900&lt;/sub_branch_id&gt;\r\n";
//		Name += "        &lt;timeout&gt;600000&lt;/timeout&gt;\r\n";
//		Name += "        &lt;reversal_seq&gt;1&lt;/reversal_seq&gt;\r\n";
//		Name += "        &lt;key_trans&gt;1&lt;/key_trans&gt;\r\n";
//		Name += "        &lt;status&gt;COMPLETE&lt;/status&gt;\r\n";
//		Name += "        &lt;transaction_id&gt;00011201117600&lt;/transaction_id&gt;\r\n";
//		Name += "        &lt;INM_ORG_TELLER_ID&gt;110001900UQ0&lt;/INM_ORG_TELLER_ID&gt;\r\n";
//		Name += "        &lt;INM_ORG_TERM_SRL&gt;260&lt;/INM_ORG_TERM_SRL&gt;\r\n";
//		Name += "        &lt;INM_SND_TX_LOG_NO&gt;2916295658341000024&lt;/INM_SND_TX_LOG_NO&gt;\r\n";
//		Name += "        &lt;INM_SND_TX_CYCLE&gt;20091029&lt;/INM_SND_TX_CYCLE&gt;\r\n";
//		Name += "        &lt;BackSystemBeginTime&gt;2009-10-29 16:29:56.611&lt;/BackSystemBeginTime&gt;\r\n";
//		Name += "        &lt;BackSystemEndTime&gt;2009-10-29 16:29:56.904&lt;/BackSystemEndTime&gt;\r\n";
//		Name += "        &lt;provider_biz_date&gt;20091029&lt;/provider_biz_date&gt;\r\n";
//		Name += "        &lt;provider_process_date&gt;20091029&lt;/provider_process_date&gt;\r\n";
//		Name += "        &lt;provider_process_time&gt;162124176&lt;/provider_process_time&gt;\r\n";
//		Name += "        &lt;provider_trans_log&gt;110001900UQ00000031&lt;/provider_trans_log&gt;\r\n";
//		Name += "        &lt;provider_msg_code/&gt;\r\n";
//		Name += "        &lt;provider_msg_desc/&gt;\r\n";
//		Name += "      &lt;/process&gt;\r\n";
//		Name += "    &lt;/processes&gt;\r\n";
//		Name += "    &lt;end_timestamp&gt;2009-10-29 16:29:56.916&lt;/end_timestamp&gt;\r\n";
//		Name += "    &lt;start_timestamp&gt;2009-10-29 16:29:56.578&lt;/start_timestamp&gt;\r\n";
//		Name += "    &lt;tran_response&gt;\r\n";
//		Name += "      &lt;code/&gt;\r\n";
//		Name += "      &lt;desc/&gt;\r\n";
//		Name += "      &lt;status&gt;COMPLETE&lt;/status&gt;\r\n";
//		Name += "    &lt;/tran_response&gt;\r\n";
//		Name += "    &lt;msglog&gt;1&lt;/msglog&gt;\r\n";
//		Name += "    &lt;timeout&gt;600000&lt;/timeout&gt;\r\n";
//		Name += "    &lt;name&gt;&lt;/name&gt;\r\n";
//		Name += "    &lt;transaction_sn&gt;0000000000040004636&lt;/transaction_sn&gt;\r\n";
//		Name += "    &lt;transaction_id&gt;00011201117600&lt;/transaction_id&gt;\r\n";
//		Name += "    &lt;requester_id&gt;0041&lt;/requester_id&gt;\r\n";
//		Name += "    &lt;branch_id&gt;110001900&lt;/branch_id&gt;\r\n";
//		Name += "    &lt;channel_id&gt;020811&lt;/channel_id&gt;\r\n";
//		Name += "    &lt;transaction_date&gt;20091029&lt;/transaction_date&gt;\r\n";
//		Name += "    &lt;transaction_time/&gt;\r\n";
//		Name += "    &lt;version_id&gt;02&lt;/version_id&gt;\r\n";
//		Name += "    &lt;INM_ORG_TERM_TYP&gt;U&lt;/INM_ORG_TERM_TYP&gt;\r\n";
//		Name += "    &lt;INM_ORG_TERM_SRL&gt;260&lt;/INM_ORG_TERM_SRL&gt;\r\n";
//		Name += "    &lt;INM_ORG_TELLER_ID&gt;110001900UQ0&lt;/INM_ORG_TELLER_ID&gt;\r\n";
//		Name += "    &lt;INM_OPR_VR_ID&gt;1&lt;/INM_OPR_VR_ID&gt;\r\n";
//		Name += "  &lt;/Transaction_Header&gt;\r\n";
//		Name += "  &lt;Transaction_Body&gt;\r\n";
//		Name += "    &lt;response process=\"1\"&gt;\r\n";
//		Name += "      &lt;OPM_MSG_STATUS&gt;32768&lt;/OPM_MSG_STATUS&gt;\r\n";
//		Name += "      &lt;FL_IN_ADD/&gt;\r\n";
//		Name += "      &lt;DATE&gt;20091029&lt;/DATE&gt;\r\n";
//		Name += "      &lt;DRAWEE_OPAC_BANK&gt;&lt;/DRAWEE_OPAC_BANK&gt;\r\n";
//		Name += "      &lt;DRAWEE_NAME_FULL&gt;&lt;/DRAWEE_NAME_FULL&gt;\r\n";
//		Name += "        &lt;DRAWEE_ACCT_NO&gt;11001019500053007695&lt;/DRAWEE_ACCT_NO&gt;\r\n";
//		Name += "        &lt;PAYEE_OPAC_BANK&gt;&lt;/PAYEE_OPAC_BANK&gt;\r\n";
//		Name += "        &lt;PAYEE_NAME_FULL&gt;&lt;/PAYEE_NAME_FULL&gt;\r\n";
//		Name += "          &lt;PAYEE_ACCT_NO&gt;11001019500053004890&lt;/PAYEE_ACCT_NO&gt;\r\n";
//		Name += "          &lt;CAPITAL_AMT&gt;&lt;/CAPITAL_AMT&gt;\r\n";
//		Name += "          &lt;AMT&gt;500.00000000&lt;/AMT&gt;\r\n";
//		Name += "          &lt;RMRK&gt;CMP0207&lt;/RMRK&gt;\r\n";
//		Name += "          &lt;RCKR_NO/&gt;\r\n";
//		Name += "          &lt;OPR_NO&gt;110001900UQ0&lt;/OPR_NO&gt;\r\n";
//		Name += "          &lt;USG_RE/&gt;\r\n";
//		Name += "          &lt;TX_LOG_NO&gt;110001900UQ00000031&lt;/TX_LOG_NO&gt;\r\n";
//		Name += "          &lt;NBNK_DOC_NO&gt;40004636&lt;/NBNK_DOC_NO&gt;\r\n";
//		Name += "          &lt;NBNK_CUST_NO/&gt;\r\n";
//		Name += "          &lt;NBNK_LOG_NO&gt;36&lt;/NBNK_LOG_NO&gt;\r\n";
//		Name += "          &lt;FL_OUT_ADD/&gt;\r\n";
//		Name += "        &lt;/response&gt;\r\n";
//		Name += "  &lt;/Transaction_Body&gt;\r\n";
//		Name += "&lt;/Transaction&gt;\r\n";
//		
//		return Name;
//	}
	
	/**
	 * 打开下载对话框
	 * @param fileName 下载文件名 或者错误提示信息
	 */
	public static void ShowDownLoad(String fileName)
	{
		if(fileName.startsWith("error:")){
			MessageBox.alert("导出失败", fileName.substring(6), null);
			return;
		}
			
//		String url = GWT.getModuleBaseURL() + "temp/" + fileName;
//		com.google.gwt.user.client.Window.open(url, "", "");
//		
//		url = GWT.getHostPageBaseURL() + "temp/" + fileName;
//		com.google.gwt.user.client.Window.open(url, "", "");
		
		String URL= GWT.getHostPageBaseURL()+ "DownloadFile?fileName=" + fileName; 
		FormElement formElement = Document.get().createFormElement(); 
		formElement.setAction(URL); 
		formElement.setName("Excel表格下载"); 
		formElement.setMethod("post"); 
		Document.get().appendChild(formElement); 
		formElement.submit();
		
//		PostPanelAsync panel = new PostPanelAsync(false);
//		panel.setAction(URL);
//		panel.setEncoding(FormPanel.Encoding.MULTIPART);
//		panel.setMethod(FormPanel.Method.POST);
//		panel.submit();
//		com.google.gwt.user.client.Window.open(URL, "", "");
	}
}
