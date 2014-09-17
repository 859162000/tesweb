package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.IUseCaseService;
import com.dc.tes.ui.client.IUseCaseServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ExecuteCaseFlow;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractUseCaseForm extends BasePage {
	private IUseCaseServiceAsync useCaseService = null;
	/**
	 * 编辑测试用例成功Handler
	 * 用于实现编辑后操作
	 */
	public abstract void EditUseCaseSuccHandler(GWTCaseFlow editResult);
	
	/**
	 * 删除测试用例成功Handler
	 * 用于实现删除后操作
	 * 可调用getDeleteResult()来获取删除返回的Boolean值
	 */
	public abstract void DeleteUseCaseSuccHandler(Boolean deleteResult);
	
	public AbstractUseCaseForm(){
		useCaseService = ServiceHelper.GetDynamicService("useCase",
				IUseCaseService.class);		
	}
	
	/**
	 * 绘制用例信息界面
	 * @param sourcePanel 用例信息所在的TabPanel
	 * @param item 所选的测试用例 
	 * @return
	 */
	protected TabItem BaseUseCaseInfo(final TabPanel sourcePanel, final GWTCaseFlow EditCaseFlow) {
		// TODO Auto-generated method stub
		TabItem tabItem = new TabItem("用例信息");
		tabItem.setId("1");
		tabItem.setClosable(false);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		tabItem.setScrollMode(Scroll.AUTO);

		FormPanel cp = new FormPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setScrollMode(Scroll.AUTO);
		cp.setFrame(false);

		cp.add(drawTable(EditCaseFlow));
		Button btn_update = new Button("修改用例");
		btn_update.setIcon(ICONS.EditCom());
		btn_update.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				CreateEditForm(EditCaseFlow);
			}
		});
		ToolBar toolBar = new ToolBar();
		toolBar.add(btn_update);
		Button btn_del = new Button("删除用例");
		btn_del.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									List<ModelData> list = new ArrayList<ModelData>();
									list.add(EditCaseFlow);
									
									useCaseService.deleteSelectedItem(list, GetLoginLogID(),
											new AsyncCallback<Boolean>() {

												@Override
												public void onFailure(
														Throwable caught) {
													// TODO Auto-generated
													// method stub
													caught.printStackTrace();
													MessageBox.alert("错误提示",
															"删除失败", null);
												}

												@Override
												public void onSuccess(
														Boolean result) {
													// TODO Auto-generated
													// method stub												
													DeleteUseCaseSuccHandler(result);
												}
											});
								}
							}
						});
			}
		});

		btn_del.setIcon(ICONS.DelCom());
		toolBar.add(btn_del);
		final ExecuteCaseFlow executeCaseFlow = new ExecuteCaseFlow(EditCaseFlow);
		Button btn_exec = new Button("执行用例", MainPage.ICONS.Exec(),
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						executeCaseFlow.ExecCaseFlow();
					}
				});
		toolBar.add(btn_exec);

		Button btn_result = new Button("执行结果", ICONS.Log(),
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						if(sourcePanel.getItemByItemId("3") != null){
							sourcePanel.setSelection(sourcePanel.getItemByItemId("3"));
						}else{
							TabItem tabItem = new TabItem("执行结果");
							tabItem.setId("3");
							tabItem.setClosable(true);
							tabItem.setLayout(new FitLayout());
							tabItem.setBorders(false);
							tabItem.setScrollMode(Scroll.AUTO);
							BasePage page = new CaseFlowResultPage(EditCaseFlow.GetCaseFlowID().toString(), sourcePanel);
							tabItem.add(page);
							sourcePanel.add(tabItem);
							sourcePanel.repaint();
							sourcePanel.setSelection(tabItem);
						}
					}
				});
		toolBar.add(btn_result);
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setStyleAttribute("border-left-style", "solid");
		toolBar.setStyleAttribute("border-left-color", "#99bbe8");
		toolBar.setStyleAttribute("border-left-width", "1px");
		cp.setTopComponent(toolBar);
		tabItem.add(cp);

		return tabItem;
	}
	
	/**
	 * 用例信息编辑框，新增用例时可传入目录或同级用例，将在目录下或同经用例所在的目录下新增该用例
	 * @param EditCaseFlow
	 * @param folder 目录或同级用例
	 */
	void CreateEditForm(GWTCaseFlow EditCaseFlow, final ModelData folder){
		if (folder instanceof GWTCaseDirectory) {
			EditCaseFlow.SetDirectoryID(((GWTCaseDirectory) folder).GetID());
			EditCaseFlow.SetCaseFlowPath(((GWTCaseDirectory) folder).GetPath());
		} else {
			EditCaseFlow.SetDirectoryID(((GWTCaseFlow) folder).GetDirectoryID());
			EditCaseFlow.SetCaseFlowPath(((GWTCaseFlow) folder).GetCaseFlowPath());			
		}
		CreateEditForm(EditCaseFlow);
	}
	
	
	void CreateEditForm(final GWTCaseFlow EditCaseFlow) {

		final String dir = EditCaseFlow.GetDirectoryID();
		final String path = EditCaseFlow.GetCaseFlowPath();	


		final Window window = new Window();
		window.setScrollMode(Scroll.AUTOY);
		window.setWidth(400);
		window.setModal(true);
		window.setPlain(true);
		window.setLayout(new FitLayout());
		window.setHeight(637);

		final FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setBorders(false);
		fp.setPadding(5);
		fp.setHeaderVisible(false);
		fp.setScrollMode(Scroll.AUTOY);
		FormData formdata = new FormData("90%");

		final RequireTextField tfNo = new RequireTextField("用例编号");
		tfNo.setName(GWTCaseFlow.N_CaseFlowNo);
		fp.add(tfNo, formdata);

		final RequireTextField tfCaseFlowName = new RequireTextField("用例名称");
		tfCaseFlowName.setName(GWTCaseFlow.N_Name);
		fp.add(tfCaseFlowName, formdata);

		final TextField<String> tfCaseType = new TextField<String>();
		tfCaseType.setFieldLabel("用例类型");
		tfCaseType.setName(GWTCaseFlow.N_CaseType);
		fp.add(tfCaseType, formdata);

		final TextField<String> tfPriority = new TextField<String>();
		tfPriority.setFieldLabel("优先级");
		tfPriority.setName(GWTCaseFlow.N_Priority);
		fp.add(tfPriority, formdata);

		final TextField<String> tfCaseProperty = new TextField<String>();
		tfCaseProperty.setFieldLabel("用例属性");
		tfCaseProperty.setName(GWTCaseFlow.N_CaseProperty);
		fp.add(tfCaseProperty, formdata);

		final TextField<String> tfDesigner = new TextField<String>();
		tfDesigner.setFieldLabel("设计人");
		tfDesigner.setName(GWTCaseFlow.N_Designer);
		fp.add(tfDesigner, formdata);
		
		final TextField<String> tfDesignTime = new TextField<String>();
		tfDesignTime.setFieldLabel("设计时间");
		tfDesignTime.setName(GWTCaseFlow.N_DesignTime);
		fp.add(tfDesignTime, formdata);
		
		final TextArea taDesc = new TextArea();
		taDesc.setFieldLabel("用例描述");
		taDesc.setName(GWTCaseFlow.N_Desc);
		fp.add(taDesc, formdata);

		final TextArea taPreCond = new TextArea();
		taPreCond.setFieldLabel("前置条件");
		taPreCond.setName(GWTCaseFlow.N_PreConditions);
		fp.add(taPreCond, formdata);

		final TextArea taCaseFlowStep = new TextArea();
		taCaseFlowStep.setFieldLabel("步骤描述");
		taCaseFlowStep.setName(GWTCaseFlow.N_CaseFlowStep);
		fp.add(taCaseFlowStep, formdata);

		final TextArea taExpectedValue = new TextArea();
		taExpectedValue.setFieldLabel("预期结果");
		taExpectedValue.setName(GWTCaseFlow.N_ExpectedResult);
		fp.add(taExpectedValue, formdata);
		
		final TextArea taMemo = new TextArea();
		taMemo.setFieldLabel("备注");
		taMemo.setName(GWTCaseFlow.N_Memo);
		fp.add(taMemo, formdata);
		
		Radio radioYes = new Radio();
		radioYes.setData("data", "1");
		radioYes.setBoxLabel("是");
		Radio radioNo = new Radio();
		radioNo.setData("data", "0");
		radioNo.setBoxLabel("否");
		radioNo.setValue(true);
		final RadioGroup rgDisableFlag = new RadioGroup();
		rgDisableFlag.setFieldLabel("暂时不用");
		rgDisableFlag.add(radioNo);
		rgDisableFlag.add(radioYes);
		rgDisableFlag.setSpacing(10);
		fp.add(rgDisableFlag, formdata);
		Button btn_save = new Button("保存",
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub					
						if (!fp.isValid())
							return;
						window.mask("正在保存，请稍候...");
						EditCaseFlow.SetEditValue(tfNo.getValue(),
								tfCaseFlowName.getValue(), taDesc.getValue(),
								GetSystemID(), GetUserID());
						EditCaseFlow.SetExtraValue("0", path, dir,
								tfDesigner.getValue(),
								taCaseFlowStep.getValue(),
								taPreCond.getValue(),
								taExpectedValue.getValue(),
								tfCaseType.getValue(),
								tfCaseProperty.getValue(),
								tfPriority.getValue(),
								tfDesignTime.getValue(),
								taMemo.getValue());
						EditCaseFlow.SetDisabledFlag(Integer.parseInt(rgDisableFlag.getValue().getData("data").toString()));
						useCaseService.saveOrUpdateCaseFlow(EditCaseFlow, GetLoginLogID(),
								new AsyncCallback<GWTCaseFlow>() {

									@Override
									public void onSuccess(GWTCaseFlow result) {
										// TODO Auto-generated method stub
										EditUseCaseSuccHandler(result);
										window.unmask();
										window.hide();
									}

									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub
										caught.printStackTrace();
										window.unmask();
										MessageBox.alert("错误信息", "保存失败", null);
									}
								});
					}
				});
		window.addButton(btn_save);

		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		}));

		if (EditCaseFlow.IsNew()) {
			window.setHeading("新增用例信息");
		} else {
			window.setHeading("修改用例信息");
			tfNo.setValue(EditCaseFlow.GetCaseFlowNo());
			tfCaseFlowName.setValue(EditCaseFlow.GetName());
			tfCaseType.setValue(EditCaseFlow.GetCaseType());
			tfPriority.setValue(EditCaseFlow.GetPriority());
			tfDesigner.setValue(EditCaseFlow.GetDesigner());
			tfCaseProperty.setValue(EditCaseFlow.GetCaseProperty());
			taCaseFlowStep.setValue(EditCaseFlow.GetCaseFlowStep());
			taDesc.setValue(EditCaseFlow.GetDesc());
			tfDesignTime.setValue(EditCaseFlow.GetDesignTime());
			taMemo.setValue(EditCaseFlow.GetMemo());
			taExpectedValue.setValue(EditCaseFlow.GetExpectedResult());
			taPreCond.setValue(EditCaseFlow.GetPreConditions());
			rgDisableFlag.setValue(EditCaseFlow.GetDisabledFlag() == 0 ? radioNo : radioYes);
		}
		window.add(fp);
		window.show();

	}
	
	static Widget drawTable(GWTCaseFlow item) {
		// TODO Auto-generated method stub
		String tableHead = "<table width=\"95%\" style=\"border:1px solid #cad9ea;color:#666; table-layout:fixed;"
				+ "empty-cells:show; border-collapse: collapse; margin:0 auto;font-size:12px;\">";
		String tdLabel = "<td width=\"14%\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0;background-color:#f5fafe; text-align:right;min-height:20px;\">";
		String tdContent = "<td width=\"36%\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0; min-height:20px;\">";
		String tdContentConb = "<td width=\"36%\" colspan=\"3\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0; min-height:20px;\">";
		String tdEnd = "</td>";
		String tableEnd = "</table>";

		String htmlStr = tableHead
				+ "<tr>"
				+ tdLabel
				+ "用例编号"
				+ tdEnd
				+ tdContent
				+ item.GetCaseFlowNo()
				+ tdEnd
				+ tdLabel
				+ "创建时间"
				+ tdEnd
				+ tdContent
				+ item.GetCreateTime()
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "用例名称"
				+ tdEnd
				+ tdContent
				+ item.GetName()
				+ tdEnd
				+ tdLabel
				+ "优先级"
				+ tdEnd
				+ tdContent
				+ item.GetPriority()
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "用例类型"
				+ tdEnd
				+ tdContent
				+ item.GetCaseType()
				+ tdEnd
				+ tdLabel
				+ "用例属性"
				+ tdEnd
				+ tdContent
				+ item.GetCaseProperty()
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "设计人"
				+ tdEnd
				+ tdContent
				+ item.GetDesigner()
				+ tdEnd
				+ tdLabel
				+ "设计时间"
				+ tdEnd
				+ tdContent
				+ (item.GetDesignTime() == null ? "" : item.GetDesignTime())
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "用例路径"
				+ tdEnd
				+ tdContentConb
				+ (item.GetCaseFlowPath() == null ? "" : item.GetCaseFlowPath().replace("\n",
						"<br/>"))
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "用例描述"
				+ tdEnd
				+ tdContentConb
				+ (item.GetDesc() == null ? "" : item.GetDesc().replace("\n",
						"<br/>"))
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "前置条件"
				+ tdEnd
				+ tdContentConb
				+ (item.GetPreConditions() == null ? "" : item
						.GetPreConditions().replace("\n", "<br/>"))
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "步骤描述"
				+ tdEnd
				+ tdContentConb
				+ (item.GetCaseFlowStep() == null ? "" : item.GetCaseFlowStep()
						.replace("\n", "<br/>"))
				+ tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "预期结果"
				+ tdEnd
				+ tdContentConb
				+ (item.GetExpectedResult() == null ? "" : item
						.GetExpectedResult().replace("\n", "<br/>")) + tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "备注"
				+ tdEnd
				+ tdContentConb
				+ (item.GetMemo() == null ? "" : item
						.GetMemo().replace("\n", "<br/>")) + tdEnd
				+ "</tr>"
				+ "<tr>"
				+ tdLabel
				+ "是否通过"
				+ tdEnd
				+ tdContent
				+ (item.GetPassFlag() == 1 ? "<p style=\"color:green\">是</p>":"<p style=\"color:red\">否</p>")
				+ tdEnd
				+ tdLabel
				+ "暂时不用"
				+ tdEnd
				+ tdContent
				+ (item.GetDisabledFlag() == 1 ?"<p style=\"color:red\">是</p>" : "<p style=\"color:green\">否</p>")
				+ tdEnd
				+ "</tr>"
				+ tableEnd;
		Html html = new Html(htmlStr);
		return html;
	}

}
