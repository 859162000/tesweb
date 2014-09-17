package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IClientTransaction;
import com.dc.tes.ui.client.IClientTransactionAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.common.TypeTranslate;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.model.GWTMsgAttribute;
import com.dc.tes.ui.client.model.GWTPack_Base;
import com.dc.tes.ui.client.model.GWTPack_Field;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
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
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TranStructConfig extends BasePage implements IUserLoader {
	public static List<GWTPack_Base> copyList = null;
	
	private String tranId = null;
	private boolean isClientSimu = false;
	private boolean isRes = true;

	private String SERVLETNAME = "transerver";
	private IClientTransactionAsync tran = ServiceHelper.GetDynamicService(this.SERVLETNAME, IClientTransaction.class);
	private TreeStore<ModelData> store = new TreeStore<ModelData>();
	private TreeGrid<ModelData> tree = null;
	private ToolBar toolBar;
	private boolean isInit = false;
	private Button btnSave = null;
	private Button btnUpload = null;
	private Button btnDownload = null;
	private Button btnRevise = null;
	private List<GWTMsgAttribute> fAttrs = null;
	private List<GWTMsgAttribute> sAttrs = null;
	private UploadWin uploadWin = null;
	private HashMap<String, Object> editorMap = new HashMap<String, Object>();

	public TranStructConfig(String tranId, boolean isClientSimu, boolean isRes) {
		this.tranId = tranId;
		this.isRes = isRes;
		this.isClientSimu = isClientSimu;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		AppContext.getTabPanel().getSelectedItem().addListener(
				Events.BeforeClose, TabColseListener());

		setLayout(new FlowLayout(0));

		InitPage(DefineColumnModel());

		load();
	}

	private void InitPage(ColumnModel cm) {
		InitTree(cm);
		InitToolBar();

		ContentPanel cp = new ContentPanel();
		cp.add(toolBar);
		cp.add(tree);
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);

		add(cp);
		fireEvent(Events.Attach);
	}

	private void InitToolBar() {
		toolBar = new ToolBar();
		btnSave = new Button("保存");
		btnSave.setIcon(ICONS.Save());
		btnSave.addSelectionListener(SaveTreeStructListener());
		btnUpload = new Button("上传报文结构");
		btnUpload.setIcon(MainPage.ICONS.Upload());
		btnUpload.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ShowUpdateTranWindow();
			}
		});
		btnDownload = new Button("下载报文结构");
		btnDownload.setIcon(MainPage.ICONS.Download());
	
		btnDownload.addSelectionListener(DownLoadPackStructListener());

		btnRevise = new Button("长度校验");
		btnRevise.setIcon(MainPage.ICONS.CaseDataEdit());
		btnRevise.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				ReviseLen();
			}

		});
		
		// 树加载之前不允许点击按钮
		btnSave.disable();
		btnUpload.disable();
		btnDownload.disable();
		btnRevise.disable();
		
		toolBar.add(btnSave);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(btnUpload);
		toolBar.add(btnDownload);
		toolBar.add(btnRevise);
		toolBar.repaint();
	}

	private void InitTree(ColumnModel cm) {
		tree = new TreeGrid<ModelData>(store, cm) {
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
			
			@Override
			protected void onShowContextMenu(int x, int y) {
				
				List<ModelData> selectItems = getSelectionModel().getSelectedItems();
				//没选中行，菜单不可用
				if(selectItems.size() == 0)
					return;
				
				//是否单选
				boolean isSingle =  selectItems.size() == 1;
				
				//是否选中的是Struct
				boolean isStruct = false;
				if(isSingle)
				{
					if(selectItems.get(0) instanceof GWTPack_Struct)
						isStruct = true;
				}
				
				//根节点，删除不可用
				
				getContextMenu().getItem(0).setEnabled(isSingle && isStruct);
				getContextMenu().getItem(1).setEnabled(isSingle  && isStruct);
//				getContextMenu().getItem(2).setEnabled(!isSingle);
				if(isClientSimu){
					getContextMenu().getItem(3).setEnabled(isSingle && !isStruct);
				}
				super.onShowContextMenu(x,y);
			}
		};

		tree.setBorders(false);
		tree.setAutoExpandColumn("name");
		tree.setTrackMouseOver(true);
		tree
				.setHeight(AppContext.getTabPanel().getSelectedItem()
						.getHeight() - 26);

		tree.getStyle().setLeafIcon(MainPage.ICONS.GWTPack_Field());

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

			@Override
			public void startEditing(int rowIndex, boolean doFocus) {
				stopEditing(false);
				//scckobe 清除之前设置的选项，解决之前出现错位情况
				removeAll();

				ModelData selectItem = tree.getSelectionModel()
						.getSelectedItem();

				if (selectItem instanceof GWTPack_Field) {
					for (int i = 0; i < tree.getColumnModel().getColumns()
							.size(); i++) {
						ColumnConfig cc = tree.getColumnModel().getColumn(i);
						if (editorMap.containsKey(cc.getId()))
							tree.getColumnModel().setEditor(i,
									(CellEditor) editorMap.get(cc.getId()));
					}
				} else {
					for (ColumnConfig cc : tree.getColumnModel().getColumns()) {
						cc.setEditor(new CellEditor(new LabelField()));
					}
					for (GWTMsgAttribute attr : sAttrs) {
						ColumnConfig cc = tree.getColumnModel().getColumnById(
								attr.get("name").toString());
						if (cc != null)
							cc
									.setEditor((CellEditor) editorMap.get(cc
											.getId()));
					}
				}

				initFields();
				super.startEditing(rowIndex, doFocus);
			}
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
	}

	/**
	 * 定义TreeGrid默认列
	 * 
	 * @return
	 */
	private ColumnModel DefineColumnModel() {
		TextField<String> text = new TextField<String>();
		text.setAllowBlank(false);

		ColumnConfig name = new ColumnConfig("name", "字段名称", 100);
		name.setRenderer(new TreeGridCellRenderer<ModelData>());
		name.setEditor(new CellEditor(text));

		ColumnConfig desc = new ColumnConfig("desc", "中文描述", 180);
		desc.setEditor(new CellEditor(new TextField<String>()));

		ColumnModel cm = new ColumnModel(Arrays.asList(name, desc));

		return cm;
	}

	/**
	 * 根据配置文件动态生成TreeGrid列
	 * 
	 * @param attrs
	 * @return
	 */
	private ColumnModel DefineColumnModel(List<GWTMsgAttribute> attrs) {

		List<ColumnConfig> cmList = new ArrayList<ColumnConfig>();
		ColumnConfig conf;

		for (GWTMsgAttribute attr : attrs) {
			String widthStr = attr.get("width").toString();
			int width = 100;
			if (!widthStr.equals(""))
				width = Integer.parseInt(widthStr);

			if (attr.get("list") != null
					&& attr.get("list").toString().length() > 0) {
				conf = GetSimpleComboBox(attr, width);
				conf.setSortable(false);
				cmList.add(conf);
			} else {
				conf = new ColumnConfig((String) attr.get("name"),
						(String) attr.get("display"), width);
				conf.setSortable(false);
				if (((String) attr.get("name")).equals("name")) {
					conf.setRenderer(new TreeGridCellRenderer<ModelData>());
					TextField<String> text = new TextField<String>();
					text.setValidateOnBlur(true);
					//text.setValidator(GetNameValidator());
					text.setAllowBlank(false);
					conf.setWidth(Math.max(width, 200));
					conf.setEditor(new CellEditor(text));
				} else {
					conf.setEditor(new CellEditor(new TextField<String>()));
				}
				conf.setMenuDisabled(true);
				cmList.add(conf);
			}
			editorMap.put(conf.getId(), conf.getEditor());
		}
//		conf = new ColumnConfig("data","默认值", 100);
//		conf.setSortable(false);
//		conf.setMenuDisabled(true);
//		cmList.add(conf);
//		conf.setEditor(new CellEditor(new TextField<String>()));
//		editorMap.put(conf.getId(), conf.getEditor());

		return new ColumnModel(cmList);
	}

	/**
	 * 生成兄弟节点名称验证对象
	 * @return	编辑域名称的验证器
	 */
	private Validator GetNameValidator() {
		return new Validator() {
			@SuppressWarnings("unchecked")
			@Override
			public String validate(Field field, String value) {
				BaseTreeModel cur = (BaseTreeModel) tree.getSelectionModel()
						.getSelectedItem();
				List<ModelData> siblings = cur.getParent().getChildren();
				GWTPack_Base gwtCur = (GWTPack_Base) cur;

				for (ModelData child : siblings) {
					GWTPack_Base gwtChild = (GWTPack_Base) child;
					if (value.compareTo(gwtChild.getName()) == 0) {
						if (gwtCur.getId() != gwtChild.getId())
							return "兄弟节点名称不允许相同";
					}
				}
				return null;
			}
		};
	}

	private ColumnConfig GetSimpleComboBox(GWTMsgAttribute attr, int width) {
		final SimpleComboBox<String> combo = new SimpleComboBox<String>();
		combo.setName((String) attr.get("name"));
		combo.setAllowBlank(false);
		combo.setEditable(false);
		combo.setForceSelection(false);
		combo.setFieldLabel((String) attr.get("display"));
		combo.setForceSelection(true);
		combo.setTriggerAction(TriggerAction.ALL);

		String[] valueList = attr.get("list").toString().split("\\|");
		for (String v : valueList) {
			combo.add(v);
		}

		CellEditor comboEditor = new CellEditor(combo) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return combo.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((ModelData) value).get("value");
			}
		};

		ColumnConfig cc = new ColumnConfig((String) attr.get("name"),
				(String) attr.get("display"), width);
		cc.setEditor(comboEditor);
		return cc;
	}

	/**
	 * 定义树的上下文菜单（插入结构/域/删除域）
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
		
		MenuItem menuCopy = new MenuItem();
		menuCopy.setText("复制");
		menuCopy.setIcon(MainPage.ICONS.DelCom());
		
		MenuItem menuPaste = new MenuItem();
		menuPaste.setText("粘贴");
		menuPaste.setIcon(MainPage.ICONS.DelCom());
		
		contextMenu.add(newStruct);
		contextMenu.add(newField);
		contextMenu.add(delete);
		if(isClientSimu){
			MenuItem addParam = new MenuItem();
			addParam.setText("定义为参数");
			addParam.setIcon(MainPage.ICONS.menuTran());
			contextMenu.add(addParam);
			addParam.addSelectionListener(AddNewSysParamListener());
		}
//		contextMenu.add(menuCopy);
//		contextMenu.add(menuPaste);

		newField.addSelectionListener(AddNewFieldListener());
		newStruct.addSelectionListener(AddNewStructListener());
		
		delete.addSelectionListener(DeleteItemListener());
		menuCopy.addSelectionListener(CopyItemsListener());
		menuPaste.addSelectionListener(PasteItemsListener());
		
		return contextMenu;
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
	 * 插入新接口操作
	 * 
	 * @return SelectionListener<MenuEvent>
	 */
	private SelectionListener<MenuEvent> AddNewSysParamListener() {
		// TODO Auto-generated method stub
		return new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO Auto-generated method stub
				ModelData sel = tree.getSelectionModel().getSelectedItem();
				int depth = store.getDepth(sel);
				String path = "";
				ModelData current = sel;
				for(int i = 2; i < depth; i++){ //忽略第1层  
					ModelData parent = store.getParent(current);
					path =parent.get("name").toString() + (path.isEmpty()?"":("." + path));
					current = parent;
				}
				if(!path.isEmpty()){
					path += ".";
				}
				path += sel.get("name").toString();
				System.out.println(path);
				GWTSysDynamicPara sysParam = new GWTSysDynamicPara(GetSystemID());
				sysParam.SetValue(sel.get("name").toString(), "", "0", "0", "0", "", "1", "1", 
						"0", "", path, "", isRes?"0":"1", null);
				SysParamEditWindow.CreateEditForm(sysParam, null, GetSystemID(), GetLoginLogID(), true);
			}
		};
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

	private SelectionListener<MenuEvent> CopyItemsListener() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce){
				copyList = new ArrayList<GWTPack_Base>();
				for(ModelData data : tree.getSelectionModel().getSelectedItems()){
					copyList.add(((GWTPack_Base)data).copy());
				}
			}
		};
	}
	
	private SelectionListener<MenuEvent> PasteItemsListener() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				ModelData selectItem = tree.getSelectionModel()
						.getSelectedItem();
				if (selectItem != null) {
					int index = 0;
					TreeModel parentTreeModel = (TreeModel) selectItem;
					ModelData 	  parentModel = selectItem;
					
					if(selectItem instanceof GWTPack_Field)
					{
						parentTreeModel = parentTreeModel.getParent();
						parentModel = store.getParent(parentModel);
					}
					
					for(GWTPack_Base insertItem : copyList)
					{
						parentTreeModel.insert(insertItem,index);
						store.insert(parentModel, insertItem,index, true);
						index++;
					}
					
					tree.getStore().setMonitorChanges(true);
					tree.setExpanded(selectItem, true);
				}
			}
		};
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
	 * 点击保存按钮事件
	 * 
	 * @return
	 */
	private SelectionListener<ButtonEvent> SaveTreeStructListener() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				saveTreeStruct();
				tree.getStore().setMonitorChanges(false);
			}
		};
	}

	/**
	 * 关闭报文结构定义Tab页事件，用于判断树状结构修改后，提示用户保存。
	 * 
	 * @return
	 */
	private Listener<BaseEvent> TabColseListener() {
		return new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				be.setCancelled(tree.getStore().isMonitorChanges());
				if (tree.getStore().isMonitorChanges())
					MessageBox.confirm("提示", "报文结构已发生更改，是否需要保存?",
							new Listener<MessageBoxEvent>() {

								@Override
								public void handleEvent(MessageBoxEvent be) {
									if (be
											.getButtonClicked()
											.getText()
											.compareTo(Message.messageBox_yes()) == 0) {
										saveTreeStruct();
									}
									tree.getStore().setMonitorChanges(false);
									AppContext.getTabPanel().getSelectedItem()
											.close();
								}
							});
			}
		};
	}

	/**
	 * 保存树结构，提交到server处理
	 * 
	 */
	private void saveTreeStruct() {
		if (tree != null) {
			GWTPack_Struct root = (GWTPack_Struct) tree.getStore().getAt(0);
			tran.SaveTreeRoot(tranId, isRes, root, GetLoginLogID(),
					new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					MessageBox.alert("提示", "保存失败,请与管理员联系.", null);
				}

				@Override
				public void onSuccess(Void result) {
					MessageBox.info("提示", "保存成功！", null);
				}
			});
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
	 * 下载报文结构到Excel表格
	 * 
	 * @return
	 */
	private SelectionListener<ButtonEvent> DownLoadPackStructListener() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final PostFormPanel formPanel = new PostFormPanel();
				formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				mask("正在生成交易报文结构,请稍后……");
				formPanel.setMethod(FormPanel.Method.POST);
				formPanel.setAction("TranStructServletDownload?type=single"
						+ "&tranId=" + tranId + "&isClientSimu="
						+ TypeTranslate.BooleanToInt(isClientSimu) + "&isRes="
						+ String.valueOf(isRes));
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
	 * 上传交易报文结构对话框
	 * 
	 */
	private void ShowUpdateTranWindow() {
		uploadWin = new UploadWin(this);
		uploadWin.Show("上传交易报文结构(.xls)", "正在上传报文结构Excel,请稍后……",
				"TranStructServletUpload" + "?type=single" + "&isClientSimu="
						+ TypeTranslate.BooleanToInt(isClientSimu) + "&tranId="
						+ tranId + "&isRes=" + String.valueOf(isRes)
						+ "&loginLogId=" + GetLoginLogID());
	}

	@Override
	public void load() {
		btnSave.disable();
		btnUpload.disable();
		btnDownload.disable();
		tran.GetTreeRoot(tranId, isRes, new AsyncCallback<GWTPack_Struct>() {

			@Override
			public void onFailure(Throwable caught) {
				tree.hide();
				MessageBox.alert("提示", "加载报文结构失败,请与管理员联系.", null);
			}

			@SuppressWarnings("serial")
			@Override
			public void onSuccess(GWTPack_Struct root) {
				try {
					btnSave.enable();
					btnUpload.enable();
					btnDownload.enable();
					btnRevise.enable();
			
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
					tree.reconfigure(store,DefineColumnModel(fAttrs));
					tree.setAutoExpandColumn("name");
					tree.setExpanded(store.getChild(0), true, true);
				} catch (Exception e) {
					MessageBox.alert("", "", null);
				}
			}
		});

	}
	
	private int setChildLen(GWTPack_Base item) {
		int length = 0;
		if(item instanceof GWTPack_Struct) {			
			List<ModelData> list = item.getChildren();
			for(ModelData base : list)
				length += setChildLen((GWTPack_Base)base);
			item.set("len", String.valueOf(length));		
		} else {
			String len = item.get("len");
			length = len.isEmpty()? 0:Integer.parseInt(len);
		}
		return length;
	}
	
	//校验长度
	private void ReviseLen() {	
		GWTPack_Struct root = (GWTPack_Struct) store.getChild(0);	
		for(int i=0; i<root.getChildCount(); i++)
			setChildLen((GWTPack_Base)root.getChild(i));
		
		tree.reconfigure(store,DefineColumnModel(fAttrs));
		tree.setExpanded(store.getChild(0), true, true);
		tree.getStore().setMonitorChanges(true);
	}
}
