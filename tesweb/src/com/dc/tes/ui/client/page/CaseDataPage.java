package com.dc.tes.ui.client.page;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.common.TypeTranslate;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.Result.ReusltFactory;
import com.dc.tes.ui.client.model.GWTMsgAttribute;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.ui.client.model.GWTPack_Base;
import com.dc.tes.ui.client.model.GWTPack_Field;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 案例数据编辑页面
 * 
 *         
 */
public class CaseDataPage extends BasePage implements IUserLoader {
	
	private String caseId = "";
	//private String caseName = "";
	/**
	 * 案例对应的交易信息
	 */
	private GWTTransaction tranInfo = null;
	private boolean isClientSimu = true;
	private boolean isForCase = true;
	private boolean isInit = false;
	private boolean lenValidate = false;
	private ICaseServiceAsync caseService = null;
	
	private TreeStore<ModelData> store = new TreeStore<ModelData>();
	private EditorTreeGrid<ModelData> tree = null;
	
	private ArrayList<GWTMsgAttribute> sAttrs = null;
	private ArrayList<GWTMsgAttribute> fAttrs = null;
	
	private Window fillDataWindow = null;
	private ToolBar toolBar;

	TabPanel tabPanel;
	
	/**
	 * 上传窗体
	 */
	UploadWin upWindow;
	CaseMsgUploadWin win;

	/**
	 * 构造函数
	 * 
	 * @param caseId
	 *            所属案例标识
	 * @param caseName
	 *            案例名称
	 * @param tranInfo
	 *            交易信息
	 * @param isClientSimu
	 *            交易时发起端还是接收端
	 * @param isForCase
	 *            true：案例数据编辑 false：预期结果编辑(如果是接收端只能是只能进行案例数据编辑)
	 */
	public CaseDataPage(String caseId, String caseName, GWTTransaction tranInfo, boolean isClientSimu, boolean isForCase) {
		
		this.caseId = caseId;
		//this.caseName = caseName;
		this.tranInfo = tranInfo;
		this.isClientSimu = isClientSimu;
		// 唯一出现false的情况是 发起端 且 isForCase = false
		this.isForCase = isClientSimu || isForCase;
		caseService = ServiceHelper.GetDynamicService(CasePage.SERVERNAME,	ICaseService.class);
		upWindow = new UploadWin(this);
		win = new CaseMsgUploadWin(this);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		AppContext.getTabPanel().getSelectedItem().addListener(
				Events.BeforeClose, TabColseListener());
		//setLayout(new FitLayout());
		tree = new EditorTreeGrid<ModelData>(store, DefineColumnModel()) {
			private ModelData selectedModel = null;

			@Override
			protected void onClick(GridEvent<ModelData> e) {
				selectedModel = e.getModel();
				if (selectedModel instanceof GWTPack_Struct) {
					e.cancelBubble();
					e.setCancelled(true);
					return;
				} else {
					super.onClick(e);
				}
			}

			// protect void

			@Override
			public void reconfigure(ListStore<ModelData> store, ColumnModel cm) {
				viewReady = true;
				rendered = true;
				afterRender();
				super.reconfigure(store, cm);
			}

			@Override
			protected void afterRenderView() {
				if (!isInit) {
					isInit = true;
					super.afterRenderView();
				}
			}

			@Override
			protected void onShowContextMenu(int x, int y) {
				TreeModel selectItem = (TreeModel) getSelectionModel()
						.getSelectedItem();

				// 没选中行，菜单不可用
				if (selectItem == null)
					return;

				GWTPack_Base item = (GWTPack_Base) selectItem;
				// 是否是数组
				if (!item.getIsArray())
					return;

				TreeModel parentItem = selectItem.getParent();
				int index = parentItem.indexOf(selectItem);
				String name = item.getName();

				// 上面是否有相同名称的字段
				boolean havePre = false;
				ModelData preItem = parentItem.getChild(index - 1);
				if (preItem != null
						&& name.compareTo(((GWTPack_Base) preItem).getName()) == 0)
					havePre = true;
				// 下面是否有相同名称的字段
				boolean haveNext = false;
				ModelData nextItem = parentItem.getChild(index + 1);
				if (nextItem != null
						&& name.compareTo(((GWTPack_Base) nextItem).getName()) == 0)
					haveNext = true;
				// 是否已复制过
				boolean isMulti = havePre || haveNext;

				// 删除
				getContextMenu().getItem(2).setEnabled(isMulti);
				// 上移
				getContextMenu().getItem(3).setEnabled(havePre);
				// 下移
				getContextMenu().getItem(4).setEnabled(haveNext);

				super.onShowContextMenu(x, y);
			}
		};
		tree.setSelectionModel(new TreeGridSelectionModel<ModelData>());
		tree.mask("正在加载...");
		tree.setBorders(false);
		tree.setAutoExpandColumn(GWTPack_Base.m_name);

		tree.setClicksToEdit(ClicksToEdit.ONE);
		tree.getStyle().setLeafIcon(MainPage.ICONS.EmptyField());
		//tree
		//		.setHeight(AppContext.getTabPanel().getSelectedItem()
		//				.getHeight() - 26);
		tree.addListener(Events.AfterEdit, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (tree != null) {
					tree.getStore().setMonitorChanges(true);
				}
			}
		});
		tree.setIconProvider(new ModelIconProvider<ModelData>() {

			@Override
			public AbstractImagePrototype getIcon(ModelData item) {
				if (item instanceof GWTPack_Field) {
					GWTPack_Field field = (GWTPack_Field) item;
					String data = ((GWTPack_Field) item).getData();
					if (data == null || data.isEmpty()) {
						return MainPage.ICONS.EmptyField();
					}
					if (!lenValidate
							|| (field.getData().length() <= field.getLength()
									|| field.getLength() == null || field
									.getLength() == 0)) {
						return MainPage.ICONS.RightField();
					} else {
						return MainPage.ICONS.ErrField();
					}
				} else
					return MainPage.EXTICONS.tree_folder();
			}
		});

		tree.setContextMenu(DefineTreeContextMenu());
		InitToolBar();
		try {
			load();
		}
		catch(Exception e) {
			MessageBox.alert("提示", "获取案例数据失败,错误提示信息:" + e.getMessage(), null);
		}
	}

	/**
	 * 初始化按钮工具栏
	 */
	private void InitToolBar() {
		toolBar = new ToolBar();

		// 保存按钮
		Button btnSave = new Button("保存", ICONS.Save(), SaveHandler());
		toolBar.add(btnSave);

		// 数据填充
		Button btnFillData = new Button("填充数据", MainPage.ICONS.Download(), FillDataHandler());
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnFillData);

		// 数据验证
		Button btnDataValidate = new Button("数据验证");
		btnDataValidate.setIcon(MainPage.ICONS.ErrField());
		Menu menu = new Menu();
		final CheckMenuItem menuItem1 = new CheckMenuItem("依据长度验证");
		menuItem1.setChecked(false);
		menuItem1.addSelectionListener(LengthHandler());
		menu.add(menuItem1);
		// CheckMenuItem menuItem2 = new CheckMenuItem("依据类型验证");
		// menuItem2.setChecked(false);
		// menu.add(menuItem2);
		btnDataValidate.setMenu(menu);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnDataValidate);

		// 案例，则存在组包预览
		if (isForCase) {
			Button btnViewPackage = new Button("组包预览", MainPage.ICONS.ViewPackge(), PreviewHandler());
			toolBar.add(new SeparatorToolItem());
			toolBar.add(btnViewPackage);		
		}

		// 上传、下载按钮
		String Name = isForCase ? "案例数据" : "预期结果";
		Button btnUpload = new Button("上传" + Name, MainPage.ICONS.Upload(), UploadHandler(Name));
		Button btnDownload = new Button("下载" + Name, MainPage.ICONS.Download(), DownloadHandler(Name));
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnUpload);
		toolBar.add(btnDownload);

		Button btnImportRecordedMsgData = new Button("从录制报文导入数据", MainPage.ICONS.ViewPackge(), ImportRecordedMsgHandler());
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnImportRecordedMsgData);
		
		Button btnUploadMsg = new Button("上传原始报文", MainPage.ICONS.WebUp(), CaseMsgUploadHandler());
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnUploadMsg);
		
		if(!isForCase){
			Button btnGetExpectedXml = new Button("获取预期报文结构", MainPage.ICONS.ViewPackge(), GetExpectedXmlHandler());
			toolBar.add(new SeparatorToolItem());
			toolBar.add(btnGetExpectedXml);
		}
		
		// 发起端,并且是案例数据 才有执行按钮
//		if (isClientSimu && isForCase) {
//			Button btnExec = new Button("执行案例", MainPage.ICONS.Exec(),
//					ExecHandler());
//			toolBar.add(new SeparatorToolItem());
//			toolBar.add(btnExec);
//		}

		ContentPanel cp = new ContentPanel();
		cp.setTopComponent(toolBar);
		cp.add(tree);
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);

		add(cp);
	}

	/**
	 * 获得长度验证控制函数
	 * 
	 * @return 长度验证控制函数
	 */
	private SelectionListener<MenuEvent> LengthHandler() {
		return new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				lenValidate = ((CheckMenuItem) ce.getItem()).isChecked();
				if (tree != null)
					tree.getTreeView().refresh(false);
			}

		};
	}

	/**
	 * 获得案例数据、预期结果保存控制函数
	 * 
	 * @return 案例数据、预期结果控制函数
	 */
	private SelectionListener<ButtonEvent> SaveHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				SaveCaseData(false);
			}
		};
	}

	/**
	 * 获得自动填充控制函数
	 * 
	 * @return 自动填充控制函数
	 */
	private SelectionListener<ButtonEvent> FillDataHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (fillDataWindow == null)
					ShowFillDataWindow();
				else {
					ShowFillDataWindow();
				}

			}
		};
	}

	/**
	 * 获得组包预览控制函数
	 * 
	 * @return 组包预览控制函数
	 */
	private SelectionListener<ButtonEvent> PreviewHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ReusltFactory result = new ReusltFactory(caseId,
						(GWTPack_Struct)tree.getStore().getAt(0), new GWTPackNeed(GetSysInfo(),tranInfo.GetChanel(),tranInfo.getTranCode()),CaseDataPage.this);
				result.setIsClientSimu(TypeTranslate.BooleanToInt(isClientSimu));
				result.setCharSet(isClientSimu?GetSysInfo().GetEncoding4RequestMsg()
						:GetSysInfo().GeteEnoding4ResponseMsg());
				result.Show();
			}
		};
	}

	/**
	 * 获得案例执行控制函数
	 * 
	 * @return 案例执行控制函数
	 */
	/*private SelectionListener<ButtonEvent> ExecHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ResultCompare resultWin = new ResultCompare();
				GWTPack_Struct root = (GWTPack_Struct) tree.getStore().getAt(0);
				resultWin.Show(GetSysInfo(), tranInfo, caseName, root,GetSelf());
			}
		};
	}*/

	/**
	 * 获得下载控制函数
	 * 
	 * @return 下载控制函数
	 */
	private SelectionListener<ButtonEvent> DownloadHandler(final String Name) {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final PostFormPanel formPanel = new PostFormPanel();
				mask("正在获取" + Name + ",请稍后...");

				formPanel.setAction("CaseStructServletDownLoad?type=single"
						+ "&isClientSimu=" + TypeTranslate.BooleanToInt(isClientSimu) 
						+ "&caseId=" + caseId + "&getCaseContent=" + isForCase);
				formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);

				formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

					@Override
					public void handleEvent(FormEvent be) {
						// TODO Auto-generated method stub
						unmask();
						TESWindows.ShowDownLoad(be.getResultHtml());
					}
					
				});
				formPanel.submit();
			}
		};
	}

	/**
	 * 获得上传控制函数
	 * 
	 * @return 上传控制函数
	 */
	private SelectionListener<ButtonEvent> UploadHandler(final String Name) {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				upWindow.Show("上传" + Name + "(.xls)", "正在上传" + Name + ",请稍后……",
						"CaseStructServletUpload" + "?type=single" + "&sysId="
								+ GetSystemID() + "&isClientSimu="
								+ TypeTranslate.BooleanToInt(isClientSimu)
								+ "&caseId=" + caseId + "&setCaseContent="
								+ isForCase + "&tranCode="
								+ tranInfo.getTranCode()
								+ "&loginLogId=" + GetLoginLogID());
			}

		};
	}
	
	private SelectionListener<ButtonEvent> CaseMsgUploadHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				win.Show("上传原始报文" + "(.txt/.xml)", "正在上传原始报文" + ",请稍后……",
						"CaseMsgUpload" + "?" + "&systemID="
								+ GetSystemID() + "&isClient="
								+ TypeTranslate.BooleanToInt(isClientSimu)
								+ "&caseID=" + caseId + "&isCaseData="
								+ isForCase +  "&loginLogId=" + GetLoginLogID());
			}

		};
	}

	/**
	 * 从录制报文导入案例数据控制函数
	 * 
	 * @return 从录制报文导入案例数据控制函数
	 */
	private SelectionListener<ButtonEvent> ImportRecordedMsgHandler() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final Window window = new Window();
				window.setHeading("选择预先录制好的交易报文");
				window.setWidth(850);
				window.setModal(true);
				window.setPlain(true);
				window.setLayout(new FitLayout());
				window.setHeight(670);
				window.setMaximizable(true);
				tabPanel = new TabPanel();
				tabPanel.setBorders(false);
				tabPanel.setBodyBorder(false);
				tabPanel.setTabPosition(TabPosition.TOP);
			
				TabItem tabItem = new TabItem("录制的交易");
				tabItem.setId("0");
				tabItem.setClosable(false);
				tabItem.setLayout(new FitLayout());
				tabItem.setBorders(false);
				tabItem.setScrollMode(Scroll.AUTO);
				
				final BasePage page = new RecordedCase(tabPanel);
				tabItem.add(page);
				tabPanel.add(tabItem);
				window.add(tabPanel);
				
				final RecordedCase recordedCasePage = (RecordedCase)page;
				recordedCasePage.SetOutsideContainerWindow(window);
				
				window.addListener(Events.Hide, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (recordedCasePage.GetGWTRecordedCase() != null) { //从录制的报文拆包并写入案例数据
							String msgStr = "";
							if (isClientSimu) {
								msgStr = recordedCasePage.GetGWTRecordedCase().getRequestMsg();
							}
							else {
								msgStr = recordedCasePage.GetGWTRecordedCase().getResponseMsg();
							}

							GWTPack_Struct root = (GWTPack_Struct) tree.getStore().getAt(0);
							
							caseService.ImportRecordedCaseData(new GWTPackNeed(GetSysInfo(), tranInfo.GetChanel(), tranInfo.getTranCode()), 
									msgStr, caseId, TypeTranslate.BooleanToInt(isClientSimu), root, 
									new AsyncCallback<GWTPack_Struct>() {
										@Override
										public void onFailure(Throwable caught) {
											MessageBox.alert("导入数据失败", caught.getMessage(), null);
										}

										@Override
										public void onSuccess(GWTPack_Struct root) {
											mask("正在加载,请稍后...");
											handleLoadSuccess(root, false);
											unmask();
											MessageBox.alert("导入数据成功", "从录制的报文导入案例数据成功！", null);
										}
									});						
						}
					}
					
				});
				
				window.show();
			}
		};
	}

	
	/**
	 * 定义TreeGrid默认列
	 * 
	 * @return TreeGrid列模型
	 */
	private ColumnModel DefineColumnModel() {

		ColumnConfig name = new ColumnConfig("name", "字段名称", 100);
		name.setSortable(false);
		name.setRenderer(new TreeGridCellRenderer<ModelData>());

		ColumnConfig desc = new ColumnConfig("desc", "中文描述", 180);
		desc.setSortable(false);
		ColumnModel cm = new ColumnModel(Arrays.asList(name, desc));

		return cm;
	}

	/**
	 * 根据Field、struct属性，定义TreeGrid列模型（不包含具体取值）
	 * 
	 * @param attrs
	 *            Field、struct属性集合
	 * @return TreeGrid列模型
	 */
	private ColumnModel DefineColumnModel(List<GWTMsgAttribute> attrs) {

		List<ColumnConfig> cmList = new ArrayList<ColumnConfig>();
		ColumnConfig conf;

		for (GWTMsgAttribute attr : attrs) {
			//printGWTMsgAttribute(attr); //debug
			String widthStr = attr.get("width").toString();
			int width = 100;
			if (!widthStr.equals("")) {
				width = Integer.parseInt(widthStr);
			}

			conf = new ColumnConfig((String) attr.get("name"), (String) attr.get("display"), width);
			conf.setSortable(false);
			if (((String) attr.get("name")).equals("name")) {//add value column when "name" is met
				conf.setRenderer(new TreeGridCellRenderer<ModelData>());
				ColumnConfig data = new ColumnConfig("data", isForCase ? "值" : "预期结果", 180);
				TextField<String> text = new TextField<String>();
				text.setSelectOnFocus(true);
				data.setEditor(new CellEditor(text));
				data.setSortable(false);
				cmList.add(conf); //name  column
				cmList.add(data); //value column
				continue;
			}
			//每一列都是可编辑的（除了Name列以外）
			//if (((String) attr.get("name")).equals("optional")) {
			ColumnConfig editable = new ColumnConfig((String) attr.get("name"), (String) attr.get("display"), width);
			editable.setSortable(false);
			TextField<String> text = new TextField<String>();
			text.setSelectOnFocus(true);
			editable.setEditor(new CellEditor(text));
			
			cmList.add(editable); //other columns			
			//}
			//cmList.add(conf);
		}

		return new ColumnModel(cmList);
	}
	
	/*private void printGWTMsgAttribute(GWTMsgAttribute attr) {
		System.out.println("name=" + attr.get("name").toString());
		System.out.println("desc=" + attr.get("desc")==null? "" : attr.get("desc").toString());
		System.out.println("attr=" + attr.get("attr")==null? "" : attr.get("attr").toString());
		System.out.println("option=" + attr.get("option").toString());
		System.out.println("isarray=" + attr.get("isarray").toString());
		System.out.println("variable=" + attr.get("variable").toString());
		System.out.println("value=" + attr.get("value")==null? "" : attr.get("value").toString());
	}*/
	
	/**
	 * 弹出复制数据对话框
	 */
	public SelectionListener<MenuEvent> ShowCopyDataWindow() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(final MenuEvent ce) {
				
				fillDataWindow = new Window();
		
				fillDataWindow.setHeading("数据复制");
				fillDataWindow.setSize(250, 90);
				fillDataWindow.setPlain(true);
				fillDataWindow.setModal(true);
				fillDataWindow.setBlinkModal(false);
				fillDataWindow.setLayout(new FitLayout());
		
				final FormPanel formPanel = new FormPanel();
				formPanel.setBorders(false);
				formPanel.setBodyBorder(false);
				formPanel.setHeaderVisible(false);
				formPanel.setPadding(0);
				formPanel.setStyleAttribute("padding-top", "5px");
				formPanel.setStyleAttribute("padding-left", "5px");
		
				final TextField<String> txtNoNumberField = new TextField<String>();
				txtNoNumberField.setFieldLabel("复制份数");
				txtNoNumberField.setValue("5");
				txtNoNumberField.setLabelStyle("width:55px;");
				txtNoNumberField.setSelectOnFocus(true);
				formPanel.add(txtNoNumberField, new FormData("95%"));
		
				fillDataWindow.add(formPanel);
		
				Button btnConfirm = new Button("确定",
						new SelectionListener<ButtonEvent>() {
							@SuppressWarnings("deprecation")
							public void componentSelected(ButtonEvent c) {
								if (tree != null) {
									String countString = txtNoNumberField.getValue();
									if (countString.equals(""))
										return;
									try{
										int count = Integer.parseInt(countString);
										FieldCopyMultiHandler(count,ce);
									} catch(Exception ex) {
										return;
									}
								}
								fillDataWindow.close();
							}
						});
				fillDataWindow.addButton(btnConfirm);
		
				fillDataWindow.addButton(new Button("取消",
						new SelectionListener<ButtonEvent>() {
							@SuppressWarnings("deprecation")
							public void componentSelected(ButtonEvent ce) {
								fillDataWindow.close();
							}
						}));
		
				fillDataWindow.show();
			}
		};
	}

	/**
	 * 弹出自动填充对话框
	 */
	public void ShowFillDataWindow() {
		fillDataWindow = new Window();

		fillDataWindow.setHeading("数据填充");
		fillDataWindow.setSize(320, 190);
		fillDataWindow.setPlain(true);
		fillDataWindow.setModal(true);
		fillDataWindow.setBlinkModal(false);
		fillDataWindow.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		formPanel.setPadding(0);
		formPanel.setStyleAttribute("padding-top", "5px");
		formPanel.setStyleAttribute("padding-left", "5px");

		final TextField<String> txtNoNumberField = new TextField<String>();
		txtNoNumberField.setFieldLabel("填充域值");
		txtNoNumberField.setValue("1");
		txtNoNumberField.setAutoWidth(true);
		txtNoNumberField.setLabelStyle("width:60px;");
		txtNoNumberField.setSelectOnFocus(true);
		formPanel.add(txtNoNumberField, new FormData());

		final RadioGroup rgFillType = new RadioGroup();
		rgFillType.setFieldLabel("已有域值处理方式");
		rgFillType.setStyleAttribute("nowrap", "true");
		rgFillType.setLabelStyle("width:60px;padding-top:15px;");
		rgFillType.setOrientation(Orientation.VERTICAL);

		Radio radioType = new Radio();
		radioType.setBoxLabel("已有域值不做处理");
		radioType.setFieldLabel("0");
		radioType.setValue(true);
		rgFillType.add(radioType);

		radioType = new Radio();
		radioType.setBoxLabel("覆盖已有域值");
		radioType.setFieldLabel("1");
		rgFillType.add(radioType);

		radioType = new Radio();
		radioType.setBoxLabel("在已有域值后追加");
		radioType.setFieldLabel("2");
		rgFillType.add(radioType);

		formPanel.add(rgFillType, new FormData());

		fillDataWindow.add(formPanel);

		Button btnClear = new Button("清除数据",
				new SelectionListener<ButtonEvent>() {
					@SuppressWarnings("deprecation")
					public void componentSelected(ButtonEvent ce) {
						if (tree != null) {
							ClearTreeStoreValue();
							tree.getView().refresh(true);
						}
						fillDataWindow.close();
					}
				});
		fillDataWindow.addButton(btnClear);

		fillDataWindow.addButton(new Button("填充",
				new SelectionListener<ButtonEvent>() {
					@SuppressWarnings("deprecation")
					public void componentSelected(ButtonEvent ce) {
						if (tree != null) {
							String fillValue = txtNoNumberField.getValue();
							if (fillValue.equals(""))
								return;
							int fillType = Integer.parseInt(rgFillType
									.getValue().getFieldLabel());
							SetTreeStoreValue(fillValue, fillType);
							tree.getView().refresh(true);
						}
						fillDataWindow.close();
					}
				}));

		fillDataWindow.show();
	}

	/**
	 * 填充案例数据
	 * 
	 * @param value
	 *            要填充的字符
	 * @param fillType
	 *            已有域值处理方式 0:不处理 1：覆盖 3：追加
	 * 
	 */
	private void SetTreeStoreValue(String value, int fillType) {
		if (store != null) {
			List<ModelData> models = store.getModels();
			for (ModelData model : models) {
				if (model instanceof GWTPack_Field) {
					GWTPack_Field field = (GWTPack_Field) model;
					String sourceData = field.getData();
					if (sourceData == null)
						sourceData = "";

					// 非空且不处理
					if (!sourceData.isEmpty() && fillType == 0)
						continue;
					else if (fillType == 1)
						sourceData = "";

					// scckobe：长度为0的域进行长度为10的自动填充
					int fieldLength = field.getLength();
					if (fieldLength == 0)
						fieldLength = 10;

					fieldLength -= sourceData.length();
					if (fieldLength <= 0)
						continue;

					for (int i = 0; value.length() <= fieldLength; i++)
						value += value;
					field.setData(sourceData + value.substring(0, fieldLength));
					tree.getStore().setMonitorChanges(true);
				}
			}
		}
	}

	/**
	 * 清除案例数据
	 */
	private void ClearTreeStoreValue() {
		if (store != null) {
			List<ModelData> models = store.getModels();
			for (ModelData model : models) {
				if (model instanceof GWTPack_Field) {
					GWTPack_Field field = (GWTPack_Field) model;
					if (field.getData() == null || field.getData().isEmpty())
						continue;
					field.setData("");
					tree.getStore().setMonitorChanges(true);
				}
			}
		}
	}

	/**
	 * 保存案例数据
	 * 
	 * @param closeTab
	 *            保存完之后是否关闭当前Tab页
	 */
	private void SaveCaseData(final boolean closeTab) {
		
		if (tree != null) {
			GWTPack_Struct root = (GWTPack_Struct) tree.getStore().getAt(0);
			caseService.SaveCaseContent(new GWTPackNeed(GetSysInfo(), tranInfo.GetChanel(),
							tranInfo.getTranCode()), caseId, isForCase, 
							TypeTranslate.BooleanToInt(isClientSimu), root, GetLoginLogID(),
					new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							if (closeTab) {
								MessageBox.confirm("提示",
										"保存失败,请与管理员联系.是否继续关闭？",
										new Listener<MessageBoxEvent>() {
											@Override
											public void handleEvent(
													MessageBoxEvent be) {
												if (be.getButtonClicked()
														.getText()
														.toLowerCase()
														.equals("yes"))
													CloseTab();
											}
										});
							} else {
								MessageBox.alert("提示", "保存失败,请与管理员联系.", null);
							}
						}

						@Override
						public void onSuccess(String result) {
							// scckobe:如果保存成功，将树的修改属性设为false
							tree.getStore().setMonitorChanges(false);
							String msg = "";
							if (result.startsWith("error")) {
								msg = "案例数据保存成功," + result.substring(6);
							} else {
								msg = "保存成功!";
							}

							MessageBox.info("提示", msg,
									new Listener<MessageBoxEvent>() {
										@Override
										public void handleEvent(
												MessageBoxEvent be) {
											if (closeTab)
												CloseTab();
										}
									});
						}
					});
		}
	}

	/**
	 * 关闭案例数据编辑Tab页事件，用于判断数据是否修改，提示用户保存。
	 * 
	 * @return
	 */
	private Listener<BaseEvent> TabColseListener() {
		return new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				be.setCancelled(tree.getStore().isMonitorChanges());
				if (tree.getStore().isMonitorChanges())
					MessageBox.confirm("提示", "案例数据已发生更改，是否需要保存?",
							new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be.getButtonClicked().getText()
											.toLowerCase().equals("yes")) {
										SaveCaseData(true);
									} else
										CloseTab();
								}
							});
			}
		};
	}

	private void CloseTab() {
		tree.getStore().setMonitorChanges(false);
		AppContext.getTabPanel().getSelectedItem().close();
	}

	/**
	 * 定义树的上下文菜单（拷贝、删除，上移）
	 * 
	 * @return Menu
	 */
	private Menu DefineTreeContextMenu() {
		Menu contextMenu = new Menu();
		contextMenu.setWidth(140);

		contextMenu.add(new MenuItem("复制数据",ICONS.Copy(),
				FieldCopyHandler()));
		contextMenu.add(new MenuItem("复制多份数据",ICONS.Copy(),
				ShowCopyDataWindow()));
		contextMenu.add(new MenuItem("删除", ICONS.DelCom(),
				FieldDeleteHandler()));
		contextMenu.add(new MenuItem("上移", ICONS.Upload(),
				FieldUpHandler()));
		contextMenu.add(new MenuItem("下移", ICONS.Download(),
				FieldDownHandler()));

		return contextMenu;
	}

	private SelectionListener<MenuEvent> FieldCopyHandler() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				BaseTreeModel selectItem = (BaseTreeModel) tree
						.getSelectionModel().getSelectedItem();

				GWTPack_Base source = (GWTPack_Base) selectItem;
				GWTPack_Base copyValue = source.copy();

				TreeModel itemParent = selectItem.getParent();
				ModelData storeParent = store.getParent(selectItem);
				int index = store.indexOf(selectItem);

				Insert(itemParent, storeParent, copyValue, index + 1);
			}
		};
	}
	
	private void FieldCopyMultiHandler(final int count,MenuEvent ce) {
		BaseTreeModel selectItem = (BaseTreeModel) tree.getSelectionModel().getSelectedItem();
		selectItem.set("isarray", "true");
		store.update(selectItem);
		final GWTPack_Base source = (GWTPack_Base) selectItem;
		final TreeModel itemParent = selectItem.getParent();
		final ModelData storeParent = store.getParent(selectItem);
		final int index = store.indexOf(selectItem);
		
		final MessageBox box = MessageBox.progress("请等待", "正在复制数据...", "已完成0%...");  
		final ProgressBar bar = box.getProgressBar();  
		DeferredCommand.addCommand(new IncrementalCommand() {   //分片执行，避免界面长时间等待出现浏览器停止响应现象
			int counter = 0;
		//	int round =count > 100 ? 5 : count/20 + 1;
		
			@Override
			public boolean execute() {
				// TODO Auto-generated method stub
		//		for(int i=0; i<round; i++) {
				if(counter == count){
					box.close();
					return false;
				}
				GWTPack_Base copyValue = source.copy();
				copyValue.set("isarray", "true");
				Insert(itemParent, storeParent, copyValue, index + 1 + counter);
				counter++;					
		//		}
				bar.updateProgress((counter+0.0) / count, "已完成" + (int) counter * 100 / count  + "%");  
				return true;
			}
		});	
	}

	private SelectionListener<MenuEvent> FieldDeleteHandler() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Remove((BaseTreeModel) tree.getSelectionModel()
						.getSelectedItem());
				tree.getStore().setMonitorChanges(true);
			}
		};
	}

	private void Insert(TreeModel itemParent, ModelData storeParent,
			BaseTreeModel selectItem, int index) {
		itemParent.insert(selectItem, index);
		store.insert(storeParent, selectItem, index, true);

		tree.getStore().setMonitorChanges(true);
		tree.setExpanded(selectItem, true);
	}

	private void Remove(BaseTreeModel selectItem) {
		TreeModel parent = selectItem.getParent();
		parent.remove(selectItem);
		store.removeAll(selectItem);
		tree.getTreeStore().removeAll(selectItem);
		tree.getStore().remove(selectItem);
		store.remove(selectItem);
	}

	private void ChangePosition(BaseTreeModel selectItem, int position) {
		TreeModel itemParent = selectItem.getParent();
		ModelData storeParent = store.getParent(selectItem);
		int index = store.indexOf(selectItem) + position;

		Remove(selectItem);
		Insert(itemParent, storeParent, selectItem, index);
	}

	private SelectionListener<MenuEvent> FieldUpHandler() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				ChangePosition((BaseTreeModel) tree.getSelectionModel()
						.getSelectedItem(), -1);
			}
		};
	}

	private SelectionListener<MenuEvent> FieldDownHandler() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				ChangePosition((BaseTreeModel) tree.getSelectionModel()
						.getSelectedItem(), 1);
			}
		};
	}

	@Override
	public void load() {
		toolBar.setEnabled(false);
		caseService.GetCaseContent(caseId, isForCase, TypeTranslate.BooleanToInt(isClientSimu), 
				new AsyncCallback<GWTPack_Struct>() {

					@Override
					public void onFailure(Throwable caught) {
						tree.hide();
						MessageBox.alert("提示", "加载报文结构失败,请与管理员联系.", null);
					}
					
					@Override
					public void onSuccess(GWTPack_Struct root) {						
						handleLoadSuccess(root, false);
						tree.unmask();
					}
				});
	}
	
	@SuppressWarnings("serial")
	protected void handleLoadSuccess(GWTPack_Struct root, boolean isClearData) {
		toolBar.setEnabled(true);
		isInit = false;
		fAttrs = new ArrayList<GWTMsgAttribute>() {};
		sAttrs = new ArrayList<GWTMsgAttribute>() {};
		for (GWTMsgAttribute attr : root.getFieldAttrList())
			fAttrs.add(attr);
		for (GWTMsgAttribute attr : root.getStructAttrList())
			sAttrs.add(attr);

		tree.getStore().removeAll();
		store.removeAll();
		tree.setLazyRowRender(0);
		tree.getView().setForceFit(true);		 
		
		store.add(root.getChildren(), true);
		if(isClearData){
			ClearTreeStoreValue();
		}
		tree.reconfigure(store,	DefineColumnModel(fAttrs));
		tree.setAutoExpandColumn("name");
		tree.setExpanded(root.getChild(0), true, true);		
	}
	

	/**
	 * 从CaseInstance表中获取该案例执行的响应报文来充当预期值报文模板
	 * @return
	 */
	private SelectionListener<ButtonEvent> GetExpectedXmlHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				caseService.GetExpectedXmlFromCaseInstance(caseId, new AsyncCallback<GWTPack_Struct>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						tree.hide();
						MessageBox.alert("提示", "加载报文结构失败,请与管理员联系.", null);
					}

					@Override
					public void onSuccess(GWTPack_Struct result) {
						// TODO Auto-generated method stub
						if(result == null){
							MessageBox.alert("提示", "该步骤尚未找到任何执行记录，" +
									"要获取预期报文，请至少成功执行一次该步骤！", null);
						}else{
							handleLoadSuccess(result, true);							
						}
					}
				});
			}
		};
	}
}
