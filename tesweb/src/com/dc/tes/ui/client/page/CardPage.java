package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IBatchService;
import com.dc.tes.ui.client.IBatchServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.model.GWTCard;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CardPage extends BasePage{

	private IBatchServiceAsync batchService = null;
	private String batchNo;
	
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCard> panel;
	/**
	 * 详细信息控件
	 */
	FormContentPanel<GWTCard> detailPanel;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	/**
	 * 正在编辑的卡信息
	 */
	private GWTCard EditCard = null;
	public CardPage(String batchNO) {
		// TODO Auto-generated constructor stub
		batchNo = batchNO;
	}
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new GridContentPanel<GWTCard>();
		batchService = ServiceHelper.GetDynamicService("batchNo", IBatchService.class);
		
		RpcProxy<PagingLoadResult<GWTCard>> proxy = new RpcProxy<PagingLoadResult<GWTCard>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTCard>> callback) {
				batchService.GetCardList(batchNo, panel
						.GetSearchCondition(), (PagingLoadConfig) loadConfig,
						callback);
			}						
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView();
		panel.DrowSearchBar();
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());		
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddNewBtn("btnAdd", AddHandler());
		bottomBar.AddEditBtn("btnEdit", EditHandler());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTCard>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	
	}
	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();

		detailMap.put(GWTCard.N_sequence, "编号：");
		detailMap.put(GWTCard.N_cardNo, "卡号：");
		detailMap.put(GWTCard.N_cardPwd, "卡密码：");
		detailMap.put(GWTCard.N_cmbHost, "主机：");
		detailMap.put(GWTCard.N_cardType, "卡类型：");
		detailMap.put(GWTCard.N_cardStatus, "卡状态：");
		detailMap.put(GWTCard.N_vaildUntil, "有效期：");
		detailMap.put(GWTCard.N_track2, "二磁：");
		detailMap.put(GWTCard.N_track3, "三磁：");
		detailMap.put(GWTCard.N_subBankNo, "所属分行：");
		detailMap.put(GWTCard.N_subsidiaryNo, "所属机构号：");
		
		return detailMap;
	}
	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					batchService.DeleteCard(panel.getSelection(),
							new AsyncCallback<Void>() {
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败", null);
								}

								public void onSuccess(Void obj) {
									panel.reloadGrid();
								}
							});
				}
			}
		};
	}
	private SelectionListener<ButtonEvent> EditHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTCard> selectedItems = panel.getDataGrid()
				.getSelectionModel().getSelectedItems();
			if (selectedItems.size() != 1) {
				MessageBox.alert("Alert", "请选择一个系统进行编辑", null);
				return;
			}
			EditCard = selectedItems.get(0);
			CreateEditForm();
			}
		};
	}
	private SelectionListener<ButtonEvent> AddHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditCard = new GWTCard(batchNo);
				CreateEditForm();
			}
		};		
	}
	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTCard.N_sequence, "编号", 30));
		columns.add(new ColumnConfig(GWTCard.N_cardNo, "卡号", 130));
		columns.add(new ColumnConfig(GWTCard.N_cardPwd, "卡密码", 60));
		columns.add(new ColumnConfig(GWTCard.N_cmbHost, "主机", 50));
		columns.add(new ColumnConfig(GWTCard.N_cardType, "卡类型", 60));
		columns.add(new ColumnConfig(GWTCard.N_cardStatus, "卡状态", 60));
		columns.add(new ColumnConfig(GWTCard.N_vaildUntil, "有效期", 60));
		columns.add(new ColumnConfig(GWTCard.N_subBankNo, "所属分行", 60));
		columns.add(new ColumnConfig(GWTCard.N_subsidiaryNo, "所属机构号", 80));
		columns.add(new ColumnConfig(GWTCard.N_track2, "二磁", 150));
		columns.add(new ColumnConfig(GWTCard.N_track3, "三磁", 150));
		return columns;
	}
	
	private void CreateEditForm(){
		final Window window = new Window();

		window.setSize(400, 500);
		//window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		//formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		formPanel.setScrollMode(Scroll.AUTOY);
		FormData formdata = new FormData("90%");
		
		final DistTextField tfCardNo = new DistTextField(EditCard, 
				EditCard.getCardNo(), "卡号", "卡号已存在，请重新输入");
		tfCardNo.setName(GWTCard.N_cardNo);
		tfCardNo.setMaxLength(20);
		tfCardNo.setAllowBlank(false);
		formPanel.add(tfCardNo, formdata);
		
		final RequireTextField tfCardPwd = new RequireTextField("卡密码");
		tfCardPwd.setName(GWTCard.N_cardPwd);
		tfCardPwd.setMaxLength(6);
		formPanel.add(tfCardPwd, formdata);
		
		final TextField<String> tfCmbHost = new TextField<String>();
		tfCmbHost.setName(GWTCard.N_cmbHost);
		tfCmbHost.setFieldLabel("主机");		
		formPanel.add(tfCmbHost, formdata);
		
		final TextField<String> tfSequence = new TextField<String>();
		tfSequence.setName(GWTCard.N_sequence);
		tfSequence.setFieldLabel("编号");		
		formPanel.add(tfSequence, formdata);
		
		final TextField<String> tfCardType = new TextField<String>();
		tfCardType.setName(GWTCard.N_cardType);
		tfCardType.setFieldLabel("卡类型");
		formPanel.add(tfCardType, formdata);
		
		final TextField<String> tfCardStatus = new TextField<String>();
		tfCardStatus.setName(GWTCard.N_cardStatus);
		tfCardStatus.setFieldLabel("卡状态");		
		formPanel.add(tfCardStatus, formdata);
				
		final RequireTextField tfValidUntil = new RequireTextField("有效期");
		tfValidUntil.setName(GWTCard.N_vaildUntil);
		tfValidUntil.setMaxLength(4);		
		formPanel.add(tfValidUntil, formdata);
		
		final TextField<String> tfSubBankNo = new TextField<String>();
		tfSubBankNo.setName(GWTCard.N_subBankNo);
		tfSubBankNo.setFieldLabel("所属分行");		
		formPanel.add(tfSubBankNo, formdata);
		
		final TextField<String> tfSubsidiaryNo = new TextField<String>();
		tfSubsidiaryNo.setName(GWTCard.N_subsidiaryNo);
		tfSubsidiaryNo.setFieldLabel("所属机构号");		
		formPanel.add(tfSubsidiaryNo, formdata);
		
		final TextArea tfTrack2 = new TextArea();
		tfTrack2.setName(GWTCard.N_track2);
		tfTrack2.setFieldLabel("二磁");
		tfTrack2.setAllowBlank(false);
		tfTrack2.setMaxLength(37);	
		formPanel.add(tfTrack2, formdata);
				
		final TextArea tfTrack3 = new TextArea();
		tfTrack3.setName(GWTCard.N_track3);
		tfTrack3.setFieldLabel("三磁");
		tfTrack3.setAllowBlank(false);
		tfTrack3.setMaxLength(104);		
		formPanel.add(tfTrack3, formdata);

		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				EditCard.SetEditValue(tfCmbHost.getValue(), tfSubBankNo.getValue(), 
						tfSubsidiaryNo.getValue(), tfSequence.getValue(), tfCardNo.getValue(),
						tfCardType.getValue(), tfCardPwd.getValue(), tfCardStatus.getValue(), 
						tfValidUntil.getValue(), tfTrack2.getValue(), tfTrack3.getValue());
				batchService.SaveCard(GetSysInfo(), EditCard, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						MessageBox.alert("错误信息", "保存失败", null);
					}

					public void onSuccess(Boolean suc) {
						panel.loaderReLoad(EditCard.IsNew());
						if (suc)
							window.hide();
						else {
							//tfTranCode.focus();
							//tfTranCode.EnforceValidate();
							tfCardNo.focus();
							tfCardNo.EnforceValidate();
						}
					}
				});
			}
		});
		window.addButton(btnOK);

		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		}));
		window.add(formPanel);
		if (EditCard.IsNew()) {
			window.setHeading("新增卡信息");
		} else {
			tfCardNo.setValue(EditCard.getCardNo());
			tfCardPwd.setValue(EditCard.getCardPwd());
			tfCmbHost.setValue(EditCard.getCmbHost());
			tfSequence.setValue(EditCard.getSequence());
			tfCardType.setValue(EditCard.getCardType());
			tfCardStatus.setValue(EditCard.getCardStatus());
			tfValidUntil.setValue(EditCard.getVaildUntil());
			tfSubBankNo.setValue(EditCard.getSubBankNo());
			tfSubsidiaryNo.setValue(EditCard.getSubsidiaryNo());
			tfTrack2.setValue(EditCard.getTrack2());
			tfTrack3.setValue(EditCard.getTrack3());
			window.setHeading("编辑卡信息");
		}
		window.show();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
