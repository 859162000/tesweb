package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 案例数据编辑页面
 * 
 *         
 */
public class OracleCaseDataPage extends BasePage implements IUserLoader {
	private String caseId = "";
	private String caseName = "";
	/**
	 * 案例对应的交易信息
	 */
	private GWTTransaction tranInfo = null;

	private boolean isClientSimu = true;
	private boolean isForCase = true;
	private boolean isInit = false;
	private ICaseServiceAsync caseService = null;
	private TreeStore<ModelData> store = new TreeStore<ModelData>();
	private TreeGrid<ModelData> tree = null;
	private ArrayList<GWTMsgAttribute> sAttrs = null;
	private Window fillDataWindow = null;
	private ArrayList<GWTMsgAttribute> fAttrs = null;
	private boolean lenValidate = false;
	private ToolBar toolBar;
	private HashMap<String, Object> editorMap = new HashMap<String, Object>();

	/**
	 * 上传窗体
	 */
	UploadWin upWindow;

	/**
	 * 构造函数
	 * 
	 * @param caseId
	 *            所属案例标识
	 * @param caseName
	 *            案例名称
	 * @param tranInfo
	 *            交易信息
	 *
	 */
	public OracleCaseDataPage(String caseId, String caseName,
			GWTTransaction tranInfo) {
		this.caseId = caseId;
		this.caseName = caseName;
		this.tranInfo = tranInfo;
		//this.isClientSimu = true;
		// 唯一出现false的情况是 发起端 且 isForCase = false
		this.isForCase = false;
		caseService = ServiceHelper.GetDynamicService(CasePage.SERVERNAME,
				ICaseService.class);
		upWindow = new UploadWin(this);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		AppContext.getTabPanel().getSelectedItem().addListener(
				Events.BeforeClose, TabColseListener());
		//setLayout(new FitLayout());
		tree = new TreeGrid<ModelData>(store, DefineColumnModel()) {
			@Override
			protected void onDoubleClick(GridEvent<ModelData> e) {
				// 根节点不允许编辑
				if (e.getRowIndex() == 0)
					return;
				// 屏蔽双击树节点时的收缩/展开动作，改为编辑动作
				if (e.getRowIndex() != -1) {
					fireEvent(Events.RowDoubleClick, e);
					if (e.getColIndex() != -1) {
						fireEvent(Events.CellDoubleClick, e);
					}
				}
			}

			@Override
			protected boolean hasChildren(ModelData model) {
				return model instanceof GWTPack_Struct;
			}

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
			
		};
		tree.setBorders(false);
		tree.setAutoExpandColumn("name");
		tree.setTrackMouseOver(true);
		tree
				.setHeight(AppContext.getTabPanel().getSelectedItem()
						.getHeight() - 26);

		tree.getStyle().setLeafIcon(MainPage.ICONS.EmptyField());

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
		

		RowEditor<ModelData> editor = new RowEditor<ModelData>() {
			
			@Override 
			protected void positionButtons() {				
				GridView view = grid.getView();
				int scroll = view.getScrollState().x;
				int mainBodyWidth = view.getScroller().getWidth(true);
				//修复上下滑动时显示不全的BUG
				if(rendered){					
				    int h = el().getClientHeight();
				    if (btns != null) {					    	
					    int columnWidth = grid.getColumnModel().getTotalWidth();
					    int width = columnWidth < mainBodyWidth ? columnWidth : mainBodyWidth;
	
					    int bw = btns.getWidth(true);
					    this.btns.setPosition((width / 2) - (bw / 2) + scroll, h - 2);
					}
					if (toolTipAlignWidget != null) {
					    toolTipAlignWidget.setStyleAttribute("position", "absolute");
					    toolTipAlignWidget.setSize(mainBodyWidth - (view.getScroller().isScrollableY() ? XDOM.getScrollBarWidth() : 0), h);
					    toolTipAlignWidget.setPosition(scroll, Style.DEFAULT);
					}
				 }
		    }		
			
			@Override
			public void removeToolTip() {
				if (toolTip != null) {
					toolTip.initTarget(null);
					toolTip = null;
				}
				toolTip = new ToolTip();
				toolTip.hide();
				toolTip.hideToolTip();
			}

//			@Override
//			public void startEditing(int rowIndex, boolean doFocus) {
//				stopEditing(false);
//				//scckobe 清除之前设置的选项，解决之前出现错位情况
//				removeAll();
//
//				ModelData selectItem = tree.getSelectionModel()
//						.getSelectedItem();
//
//				if (selectItem instanceof GWTPack_Field) {
//					for (int i = 0; i < tree.getColumnModel().getColumns()
//							.size(); i++) {
//						ColumnConfig cc = tree.getColumnModel().getColumn(i);
//						if (editorMap.containsKey(cc.getId()))
//							tree.getColumnModel().setEditor(i,
//									(CellEditor) editorMap.get(cc.getId()));
//					}
//				} else {
//					for (ColumnConfig cc : tree.getColumnModel().getColumns()) {
//						cc.setEditor(new CellEditor(new LabelField()));
//					}
//					for (GWTMsgAttribute attr : sAttrs) {
//						ColumnConfig cc = tree.getColumnModel().getColumnById(
//								attr.get("name").toString());
//						if (cc != null)
//							cc
//									.setEditor((CellEditor) editorMap.get(cc
//											.getId()));
//					}
//				}
//
//				initFields();
//				super.startEditing(rowIndex, doFocus);
//			}
		};
		
		
		editor.setClicksToEdit(ClicksToEdit.TWO);
		editor.addListener(Events.AfterEdit, EditItemListener());
		tree.addPlugin(editor);

		tree.setContextMenu(DefineTreeContextMenu());

		new TreeGridDragSource(tree) {
			@Override
			protected void onDragDrop(DNDEvent event) {
				if (event.getOperation() == Operation.MOVE) {
					List<TreeModel> sel = event.getData();
					for (TreeModel tm : sel) {
						ModelData m = (ModelData) tm.get("model");
						treeGrid.getTreeStore().remove(m);
						((BaseTreeModel) m).getParent().remove(m);
					}
				}
			}

			@Override
			protected void onDragStart(DNDEvent e) {
				ResetSelection();
				super.onDragStart(e);
			}

			/**
			 * 向上查询父节点是否选择（出于效率考虑，本系统的报文应该是层次不高，但是子节点多的情况）
			 * @param sel		当前选择的节点列表
			 * @param ckChild	当前验证的节点
			 * @return 			true:父节点没被选择 false：父节点被选择
			 */
			private boolean ParentNoChecked(List<ModelData> sel,
					ModelData ckChild) {
				ModelData parent = treeGrid.getTreeStore().getParent(ckChild);
				if (parent == null)
					return true;
				else {
					if (sel.contains(parent))
						return false;
					return ParentNoChecked(sel, parent);
				}
			}

			/**
			 * scckobe 对选择父节点的同时也选择子节点的情况
			 */
			private void ResetSelection() {
				List<ModelData> sel = treeGrid.getSelectionModel()
						.getSelectedItems();
				List<ModelData> targList = new ArrayList<ModelData>();
				for (ModelData checkChild : sel) {
					if (ParentNoChecked(sel, checkChild))
						targList.add(checkChild);
				}
				setStatusText("正在移动 ({0}) 项数据");
				treeGrid.getSelectionModel().setSelection(targList);
			}
		};

		TreeGridDropTarget target = new TreeGridDropTarget(tree) {
			@SuppressWarnings("unchecked")
			@Override
			protected void handleInsert(DNDEvent event, final TreeNode item) {
				//scckobe 保证根节点不会添加兄弟节点
				if (item != null && item.getParent() == null) {
					int height = treeGrid.getView().getRow(item.getModel())
							.getOffsetHeight();
					int mid = height / 2;
					int top = treeGrid.getView().getRow(item.getModel())
							.getAbsoluteTop();
					mid += top;
					int y = event.getClientY();
					boolean before = y < mid;

					if (!((before && y > top + 4) || (!before && y < top
							+ height - 4))) {
						activeItem = null;
						appendItem = null;
						Insert.get().hide();
						event.getStatus().setStatus(false);
						return;
					}
				}
				super.handleInsert(event, item);
			}

			
			@SuppressWarnings("rawtypes")
			@Override
			protected void onDragDrop(DNDEvent event) {
				super.onDragDrop(event);
				// 保证Struct再拖拽完后仍然是展开状态
				List sel = event.getData();
				if (sel.size() > 0) {
					ModelData source = (ModelData) sel.get(0);
					source = (ModelData) source.get("model");
					tree.setExpanded(source, true);
				}
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void handleAppendDrop(DNDEvent event, TreeNode item) {
				List<ModelData> models = prepareDropData(event.getData(), false);
				if (models.size() > 0) {
					ModelData p = null;
					if (item != null) {
						p = item.getModel();
						renameChild(p, models);
						appendModel(p, models, treeGrid.getTreeStore()
								.getChildCount(item.getModel()));
					} else {
						appendModel(p, models, 0);
					}
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void handleInsertDrop(DNDEvent event, TreeNode item,
					int index) {
				List sel = event.getData();
				if (sel.size() > 0) {
					int idx = treeGrid.getTreeStore().indexOf(item.getModel());
					idx = status == 0 ? idx : idx + 1;
					if (item.getParent() != null) {
						ModelData p = item.getParent().getModel();
						renameChild(p, sel);
						appendModel(p, sel, idx);
					} else {
						appendModel(null, sel, idx);
					}
				}
			}

			private void renameChild(ModelData parentNode,
					List<ModelData> insertChild) {
				List<ModelData> parentChild = treeGrid.getTreeStore()
						.getChildren(parentNode);

				int iCount = insertChild.size();
				for (int i = 0; i < iCount; i++) {
					ModelData iChild = insertChild.get(i);
					if (iChild instanceof TreeStoreModel)
						iChild = iChild.get("model");
					if (parentChild.contains(iChild))
						continue;
					String name = iChild.get("name");

					//先保证自己内部不重名
					for (int j = 0; j < iCount; j++) {
						if (j == i)
							continue;
						ModelData cChild = insertChild.get(j);
						if (cChild instanceof TreeStoreModel)
							cChild = cChild.get("model");
						if (name.compareTo(cChild.get("name").toString()) == 0)
							name += "_1";
					}

					//保证与要追求的节点不重名
					for(int j = 0; j<parentChild.size();j++)
					{
						if (name.compareTo(parentChild.get(j).get("name").toString()) == 0)
						{
							name += "(*)";
							j = -1;
						}
					}
					iChild.set("name", name);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void appendModel(ModelData p, List<ModelData> models,
					int index) {
				if (models.size() == 0)
					return;
				if (models.get(0) instanceof TreeModel) {
					TreeModel test = (TreeModel) models.get(0);
					if (test.getPropertyNames().contains("model")) {

						List<ModelData> children = new ArrayList<ModelData>();
						for (ModelData tm : models) {
							ModelData child = tm.get("model");
							children.add(child);
						}
						if (p == null) {
							treeGrid.getTreeStore().insert(children, index,
									false);
							((BaseTreeModel) activeItem.getModel()).getParent()
									.insert(children.get(0), index);
						} else {
							treeGrid.getTreeStore().insert(p, children, index,
									false);
							int i = 0;
							for (ModelData tm : models) {
								ModelData child = tm.get("model");
								((BaseTreeModel) p)
										.insert(child, index + (i++));
							}
						}
						for (ModelData tm : models) {
							ModelData child = tm.get("model");
							List sub = (List) ((TreeModel) tm).getChildren();
							appendModel(child, sub, 0);
						}
						tree.getStore().setMonitorChanges(true);
						return;
					}
				}
				if (p == null) {
					treeGrid.getTreeStore().insert(models, index, false);
				} else {
					treeGrid.getTreeStore().insert(p, models, index, false);
				}
			}
		};
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH);
		target.setAddChildren(true);
		target.setOperation(Operation.MOVE);
		tree.setContextMenu(DefineTreeContextMenu());
		InitToolBar();
		load();
	}

	
	/**
	 * 编辑树节点
	 * 
	 * @return
	 */
	private Listener<BaseEvent> EditItemListener() {
		return new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				ModelData selectItem = tree.getSelectionModel()
						.getSelectedItem();

				if (selectItem != null) {
					((BaseTreeModel) selectItem).setProperties(selectItem
							.getProperties());

				}

				tree.getStore().setMonitorChanges(true);
			}
		};
	}
	/**
	 * 初始化按钮工具栏
	 */
	private void InitToolBar() {
		toolBar = new ToolBar();

		// 保存按钮
		Button btnSave = new Button("保存", ICONS.Save(),
				SaveHandler());
		toolBar.add(btnSave);

		// 数据填充
		Button btnFillData = new Button("填充数据", MainPage.ICONS.Download(),
				FillDataHandler());
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
			Button btnViewPackage = new Button("组包预览", MainPage.ICONS
					.ViewPackge(), PreviewHandler());
			toolBar.add(new SeparatorToolItem());
			toolBar.add(btnViewPackage);
		}

		// 上传、下载按钮
		String Name = isForCase ? "案例数据" : "预期结果";
		Button btnUpload = new Button("上传" + Name, MainPage.ICONS.Upload(),
				UploadHandler(Name));
		Button btnDownload = new Button("下载" + Name, MainPage.ICONS.Download(),
				DownloadHandler(Name));
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnUpload);
		toolBar.add(btnDownload);

		if(!isForCase){
			Button btnGetExpectedXml = new Button("获取预期报文结构", 
					MainPage.ICONS.ViewPackge(), GetExpectedXmlHandler());
			toolBar.add(new SeparatorToolItem());
			toolBar.add(btnGetExpectedXml);
		}


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
						(GWTPack_Struct) tree.getStore().getAt(0),new GWTPackNeed(GetSysInfo(),tranInfo.GetChanel(),tranInfo.getTranCode()),OracleCaseDataPage.this);
				result.setIsClientSimu(TypeTranslate.BooleanToInt(isClientSimu));
				result.Show();
			}
		};
	}

	/**
	 * 获得案例执行控制函数
	 * 
	 * @return 案例执行控制函数
	 */
	private SelectionListener<ButtonEvent> ExecHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ResultCompare resultWin = new ResultCompare();
				GWTPack_Struct root = (GWTPack_Struct) tree.getStore().getAt(0);
				resultWin.Show(GetSysInfo(), tranInfo, caseName, root,GetSelf());
			}
		};
	}

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
								+ "&rd=" + Random.nextInt());
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
	 * 根据Field、struct属性，定义TreeGrid列模型
	 * 
	 * @param attrs
	 *            Field、struct属性集合
	 * @return TreeGrid列模型
	 */
	private ColumnModel DefineColumnModel(List<GWTMsgAttribute> attrs) {

		List<ColumnConfig> cmList = new ArrayList<ColumnConfig>();
		ColumnConfig conf;

		for (GWTMsgAttribute attr : attrs) {
			String widthStr = attr.get("width").toString();
			int width = 100;
			if (!widthStr.equals(""))
				width = Integer.parseInt(widthStr);

			conf = new ColumnConfig((String) attr.get("name"), (String) attr
					.get("display"), width);
			conf.setSortable(false);
			TextField<String> text1 = new TextField<String>();
			text1.setSelectOnFocus(true);
			conf.setEditor(new CellEditor(text1));
			if (((String) attr.get("name")).equals("name")) {
				conf.setRenderer(new TreeGridCellRenderer<ModelData>());

				ColumnConfig data = new ColumnConfig("data", isForCase ? "值"
						: "预期结果", 180);
				TextField<String> text = new TextField<String>();
				text.setSelectOnFocus(true);
				data.setEditor(new CellEditor(text));
				data.setSortable(false);
				cmList.add(conf);
				cmList.add(data);
				continue;
			}
			//针对银企项目的改动
			//if (((String) attr.get("name")).equals("optional")) {
				ColumnConfig editable = new ColumnConfig((String) attr.get("name"), (String) attr
						.get("display"), width);
				editable.setSortable(false);
				TextField<String> text = new TextField<String>();
				text.setSelectOnFocus(true);
				editable.setEditor(new CellEditor(text));

				cmList.add(editable);
				
			//}

			//cmList.add(conf);
		}

		return new ColumnModel(cmList);
	}
	
	/**
	 * 弹出复制数据对话框
	 */
	public SelectionListener<MenuEvent> ShowCopyDataWindow() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(final MenuEvent ce) {
				// TODO Auto-generated method stub
				
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
		
				Button btnClear = new Button("确定",
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
				fillDataWindow.addButton(btnClear);
		
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
			caseService.SaveCaseContent(new GWTPackNeed(GetSysInfo(),tranInfo.GetChanel(),
					tranInfo.getTranCode()),caseId, isForCase, TypeTranslate.BooleanToInt(isClientSimu), root, GetLoginLogID(),
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
														.toLowerCase().equals(
																"yes"))
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

		MenuItem newStruct = new MenuItem();
		newStruct.setText("插入结构");
		newStruct.setIcon(MainPage.ICONS.NewStruct());
		MenuItem newField = new MenuItem();
		newField.setText("插入字段");
		newField.setIcon(MainPage.ICONS.NewField());
		MenuItem delete = new MenuItem();
				
		delete.setText("删除节点");
		delete.setIcon(MainPage.ICONS.DelCom());
		contextMenu.add(newStruct);
		contextMenu.add(newField);
		contextMenu.add(new MenuItem("插入接口", ICONS.menuTran(), AddNewInterfaceListener()));
		contextMenu.add(delete);
		
		newField.addSelectionListener(AddNewFieldListener());
		newStruct.addSelectionListener(AddNewStructListener());
		delete.addSelectionListener(DeleteItemListener());
		
		contextMenu.add(new MenuItem("复制数据",ICONS.Copy(),
				FieldCopyHandler()));
		contextMenu.add(new MenuItem("复制多份数据",ICONS.Copy(),
				ShowCopyDataWindow()));				
		return contextMenu;
	}

	private SelectionListener<? extends MenuEvent> AddNewInterfaceListener() {
		// TODO Auto-generated method stub
		return new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO Auto-generated method stub
				InterfaceSelectWin win = new InterfaceSelectWin();
				win.drawWindow(new AsyncCallback<GWTPack_Struct>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(GWTPack_Struct result) {
						// TODO Auto-generated method stub
						ModelData selectItem = tree.getSelectionModel()
							.getSelectedItem();
						List<ModelData> structs = result.getChildren();
						for(int i = structs.size()-1; i >= 0; i--){
							GWTPack_Struct struct = (GWTPack_Struct)structs.get(i);
							if (selectItem instanceof GWTPack_Struct) {
								((BaseTreeModel) selectItem).add(struct);
								TreeModel itemParent = (TreeModel)selectItem;
								itemParent.insert(struct, store.indexOf(selectItem));
								store.add(selectItem, struct, true);
							} else {
								((BaseTreeModel) selectItem).getParent().insert(struct,
										store.indexOf(selectItem));
								TreeModel itemParent = ((BaseTreeModel)selectItem).getParent();
								itemParent.insert(struct, store.indexOf(selectItem));
								store.insert(store.getParent(selectItem), struct, store
										.indexOf(selectItem), true);
							}
							tree.setExpanded(struct, true);
						}					
						tree.getStore().setMonitorChanges(true);
						tree.setExpanded(selectItem, true);	
					}
				});
			}
		};
	}

	private SelectionListener<MenuEvent> FieldCopyHandler() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				BaseTreeModel selectItem = (BaseTreeModel) tree
						.getSelectionModel().getSelectedItem();
				selectItem.set("isarray", "true");
				store.update(selectItem);
				GWTPack_Base source = (GWTPack_Base) selectItem;
				
				GWTPack_Base copyValue = source.copy();
				copyValue.set("isarray", "true");
				TreeModel itemParent = selectItem.getParent();
				ModelData storeParent = store.getParent(selectItem);
				int index = store.indexOf(selectItem);

				Insert(itemParent, storeParent, copyValue, index + 1);
			}
		};
	}
	
	private void FieldCopyMultiHandler(final int count,MenuEvent ce) {
							
			BaseTreeModel selectItem = (BaseTreeModel) tree
					.getSelectionModel().getSelectedItem();
			selectItem.set("isarray", "true");
			store.update(selectItem);
			final GWTPack_Base source = (GWTPack_Base) selectItem;
			final TreeModel itemParent = selectItem.getParent();
			final ModelData storeParent = store.getParent(selectItem);
			final int index = store.indexOf(selectItem);
			
			final MessageBox box = MessageBox.progress("请等待", "正在复制数据...",  
	            "已完成0%...");  
	        final ProgressBar bar = box.getProgressBar();  
			DeferredCommand.addCommand(new IncrementalCommand() {   //分片执行，避免界面长时间等待出现浏览器停止响应现象
				int counter = 0;
				//int round =count > 100 ? 5 : count/20 + 1;
				@Override
				public boolean execute() {
					// TODO Auto-generated method stub
				//	for(int i=0; i<round; i++) {
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


	private void Insert(TreeModel itemParent, ModelData storeParent,
			BaseTreeModel selectItem, int index) {
		itemParent.insert(selectItem, index);
		store.insert(storeParent, selectItem, index, true);

		tree.getStore().setMonitorChanges(true);
		tree.setExpanded(selectItem, true);
	}

	/*private void Remove(BaseTreeModel selectItem) {
		TreeModel parent = selectItem.getParent();
		parent.remove(selectItem);
		store.removeAll(selectItem);
		tree.getTreeStore().removeAll(selectItem);
		tree.getStore().remove(selectItem);
		store.remove(selectItem);
	}*/

	@Override
	public void load() {
		toolBar.setEnabled(false);
		//int isClientSimu = 0;
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
					}
				});
	}
	
	@SuppressWarnings("serial")
	protected void handleLoadSuccess(GWTPack_Struct root, boolean isClearData) {
		// TODO Auto-generated method stub
		toolBar.setEnabled(true);
		isInit = false;
		fAttrs = new ArrayList<GWTMsgAttribute>() {
		};
		sAttrs = new ArrayList<GWTMsgAttribute>() {
		};
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
		tree.reconfigure(store,
				DefineColumnModel(fAttrs));
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
	

	/**
	 * 插入新字段操作
	 * 
	 * @return SelectionListener<MenuEvent>
	 */
	private SelectionListener<MenuEvent> AddNewFieldListener() {
		return new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				ModelData selectItem = tree.getSelectionModel()
						.getSelectedItem();
				if (selectItem != null) {
					GWTPack_Field field = new GWTPack_Field();
					for (GWTMsgAttribute attr : fAttrs) {
						field.set((String) attr.get("name"), (String) attr
								.get("default"));
					}
					field.set("name", GetNewItemName(true));
					if (selectItem instanceof GWTPack_Struct) {
						((BaseTreeModel) selectItem).add(field);
						//						try
						//						{
						//							List<ModelData> items = store.getAllItems();
						store.add(selectItem, field, false);
						//						}
						//						catch (Exception e) {
						//							GWTPack_Field field1 = new GWTPack_Field();
						//						}
					} else {
						((BaseTreeModel) selectItem).getParent().insert(field,
								store.indexOf(selectItem));

						store.insert(store.getParent(selectItem), field, store
								.indexOf(selectItem) + 1, false);
					}
					tree.getStore().setMonitorChanges(true);
					tree.setExpanded(selectItem, true);
				}
			}
		};
	}

	/**
	 * 插入新结构操作
	 * 
	 * @return SelectionListener<MenuEvent>
	 */
	private SelectionListener<MenuEvent> AddNewStructListener() {
		return new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				ModelData selectItem = tree.getSelectionModel()
						.getSelectedItem();
				if (selectItem != null) {
					GWTPack_Struct struct = new GWTPack_Struct();
					for (GWTMsgAttribute attr : sAttrs) {
						struct.set((String) attr.get("name"), (String) attr
								.get("default"));
					}
					struct.set("name", GetNewItemName(false));
					if (selectItem instanceof GWTPack_Struct) {
						((BaseTreeModel) selectItem).add(struct);

						store.add(selectItem, struct, false);
					} else {
						((BaseTreeModel) selectItem).getParent().insert(struct,
								store.indexOf(selectItem) + 1);

						store.insert(store.getParent(selectItem), struct, store
								.indexOf(selectItem) + 1, false);
					}

					tree.getStore().setMonitorChanges(true);
					tree.setExpanded(selectItem, true);
				}
			}
		};
	}
	
	/**
	 * 删除树节点操作
	 * 
	 * @return SelectionListener<MenuEvent>
	 */
	private SelectionListener<MenuEvent> DeleteItemListener() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final List<ModelData> selected = tree.getSelectionModel()
						.getSelectedItems();
				if (isRoot(selected)) {
					MessageBox.alert("无效操作", "根节点无法删除", null);
					return;
				}

				MessageBox.confirm("删除确认", "您确定要删除该项吗?",
						new Listener<MessageBoxEvent>() {

							@Override
							public void handleEvent(MessageBoxEvent be) {
								if (be.getButtonClicked().getText().compareTo(
										Message.messageBox_yes()) == 0) {
									for (ModelData sel : selected) {
										BaseTreeModel model1 = (BaseTreeModel) sel;
										TreeModel parent = model1.getParent();

										//										// scckobe 防止先删除父节点在删除子节点，因为Tree的selectedModel取之是不是会有问题不好说
										////										if (parent.getChildCount() == 0)
										////											continue;
										//
										parent.remove(sel);
										RemoveItem(sel);
									}
									tree.getStore().setMonitorChanges(true);
								}
							}
						});
			}
		};
	}
	
	/**
	 * 递归删除
	 * 
	 * @param item
	 */
	private void RemoveItem(ModelData item) {
		//对store也进行操作，解决删除之后无法添加的问题
		store.remove(item);
		tree.getStore().remove(item);
		List<ModelData> sub = (List<ModelData>) ((TreeModel) item)
				.getChildren();
		for (ModelData m : sub) {
			RemoveItem(m);
		}
	}
	
	/**
	 * 新增结构或Field时，生成新的Filed名称，保证新增Field名称不会重复
	 * 
	 * @param isField
	 * @return
	 */
	private String GetNewItemName(boolean isField) {

		String fieldName = isField ? "new field1" : "new struct1";
		int endIdx = isField ? 9 : 10;
		for (int i = 1; tree.getStore().findModel("name", fieldName) != null; i++) {
			fieldName = fieldName.substring(0, endIdx) + String.valueOf(i);
		}
		return fieldName;
	}
	
	/**
	 * 判断当前选择选择的节点是否存在根节点
	 * @param selected	当前选择的节点列表
	 * @return			true：存在根节点 false：不包含根节点
	 */
	private boolean isRoot(List<ModelData> selected) {
		for (ModelData sel : selected) {
			BaseTreeModel model = (BaseTreeModel) sel;
			if (model == tree.getStore().getAt(0))
				return true;
		}
		return false;
	}
}

