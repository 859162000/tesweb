package com.dc.tes.ui.client.control;

import java.util.List;


import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;

/**
 * Grid列表ContentPanel基类 
 */
 public class TreeGridContentPanel<T extends ModelData> extends ContentPanel {

	/**
	 * 搜索框
	 */
	protected TextField<String> tfSearch = null;

	/**
	 * 搜索栏
	 */
	protected ToolBar searchBar = null;
	
	protected Button ibSearch = null;
	
	/**
	 * 数据DataGrid
	 */
	protected TreeGrid<T> treeGrid = null;

	protected RpcProxy<List<T>> proxy = null;

	protected BaseTreeLoader<T> loader = null;

	protected TreeStore<T> store = null;

	protected List<ColumnConfig> columns = null;

	private int height = 243;
	  
	
	CheckBoxSelectionModel<T> columnCK = new CheckBoxSelectionModel<T>();
	RowNumberer columnNum = new RowNumberer();

	/**
	 * 底部工具栏
	 */
	protected ButtonBar bottomBar = null;
	
	FormContentPanel<T> detailPanel = null;
	
	public TreeGridContentPanel() {
		super();
		this.setLayout(new FitLayout());
		this.setHeaderVisible(false);
		this.setBorders(false);

		tfSearch = new TextField<String>();
	}

	public ToolBar getSearchBar() {
		return searchBar;
	}

	public void setSearchBar(ToolBar searchBar) {
		this.searchBar = searchBar;
	}

	public TreeGrid<T> getTreeGrid() {
		return treeGrid;
	}

	public List<T> getSelection()
	{
		return treeGrid.getSelectionModel().getSelection();
	}
	
	public void reloadGrid()
	{
		treeGrid.clearState();
		treeGrid.fireEvent(Events.Attach);
	}
	
	public void setTreeGrid(TreeGrid<T> dataGrid) {
		this.treeGrid = dataGrid;
	}

	public ButtonBar getBottomBar() {
		return bottomBar;
	}

	public void setBottomBar(ButtonBar bottomBar) {
		this.bottomBar = bottomBar;
		this.setBottomComponent(bottomBar);
	}
	
	public FormContentPanel<T> getDetailForm() {
		return detailPanel;
	}
	
	public void setDetailForm(FormContentPanel<T> detailPanel)
	{
		this.detailPanel = detailPanel;
	}

	public TextField<String> getTfSearch() {
		return tfSearch;
	}

	public void setTfSearch(TextField<String> tfSearch) {
			this.tfSearch = tfSearch;
	}

	public RpcProxy<List<T>> getProxy() {
		return proxy;
	}

	public void setProxy(RpcProxy<List<T>> proxy) {
		this.proxy = proxy;
	}

	public BaseTreeLoader<T> getLoader() {
		return loader;
	}

	public void setLoader(BaseTreeLoader<T> loader) {
		this.loader = loader;
	}
	
	public void loaderReLoad(boolean isNew)
	{
		if(isNew)
		{
			SetSearchCondition("");
			reloadGrid();
		}
		else
			loader.load();
	}

	public TreeStore<T> getStore() {
		return store;
	}

	public void setStore(TreeStore<T> store) {
		this.store = store;
	}

	public List<ColumnConfig> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnConfig> columns) {
		this.columns = columns;
	}

	public void DrowSearchBar(String toolTip)
	{
		tfSearch.setToolTip(toolTip);
		tfSearch.setWidth(160);

		this.searchBar = new ToolBar();
		this.searchBar.setAutoHeight(true);
		this.searchBar.add(tfSearch);

		ibSearch = new Button();
		ibSearch.setIcon(MainPage.ICONS.search());
		ibSearch.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (treeGrid != null) {
					treeGrid.clearState();
					treeGrid.fireEvent(Events.Attach);
				}
			}

		});
		this.searchBar.add(ibSearch);
		tfSearch.addKeyListener(new KeyListener()
		{
			@Override
			public void handleEvent(ComponentEvent e) 
			{
				if( e.getKeyCode() == 13)
				{
					ibSearch.fireEvent(Events.Select);
				}
			}
		});
		this.setTopComponent(this.searchBar);
	}
	
	public void DrowSearchBar() {
		DrowSearchBar("");
		tfSearch.setToolTip("请输入查询条件");
	}

	public void SetHeight(int height)
	{
		this.height = height;
		if(treeGrid != null)
			treeGrid.setHeight(height);
	}
	
	public void DrowGridView(String autoExpandName, boolean showCk,
			boolean showNum) {
		loader = new BaseTreeLoader<T>(proxy);
//		loader.setRemoteSort(true);
		store = new TreeStore<T>(loader) {
		};

		if (showNum)
			columns.add(0, columnNum);
		if (showCk)
		{
			ColumnConfig ckConfig = columnCK.getColumn();
			columns.add(0, ckConfig);
		}
		for(int i = 0; i < columns.size(); i++){
			columns.get(i).setSortable(false);
		}
		
		ColumnModel cm = new ColumnModel(columns)
		{
			public int findColumnIndex(String dataIndex) {
				if(dataIndex.trim().isEmpty())
					return -1;
				
			    for (int i = 0, len = configs.size(); i < len; i++) {
			      if (configs.get(i).getDataIndex().equals(dataIndex)) {
			        return i;
			      }
			    }
			    return -1;
			  }
		};
		
		treeGrid = new TreeGrid<T>(store, cm){
			@Override
			public void setExpanded(T arg0, boolean expand, boolean deep) {
				// TODO Auto-generated method stub
				super.setExpanded(arg0, expand, deep);
				BaseTreeModel treeModel = (BaseTreeModel)arg0;
				for(ModelData model : treeModel.getChildren()) {
					if(model instanceof GWTParameterDirectory) {
						treeGrid.setExpanded((T) model, true);
					}
				}
			}

			@Override  
		      public boolean hasChildren(T parent) {  
		        return HasChildren(parent);  
		      }  

		};
		treeGrid.setLoadMask(true);
		treeGrid.getView().setForceFit(true);
		treeGrid.setView(new TreeGridView(){
	    	protected void onBeforeDataChanged(StoreEvent<ModelData> se) {
	    	    if (grid.isLoadMask()) {
	    	      grid.mask("加载中...");
	    	    }
	    	  }
	    });
		treeGrid.setBorders(false);
		treeGrid.setHeight(height);
		treeGrid.setAutoWidth(true);
		if (showCk)
		{
			treeGrid.setSelectionModel(columnCK);
			treeGrid.addPlugin(columnCK);  
		}
		if (!autoExpandName.isEmpty())
			treeGrid.setAutoExpandColumn(autoExpandName);
		treeGrid.addListener(Events.Attach, new Listener<GridEvent<T>>() {
			public void handleEvent(GridEvent<T> be) {				
				loader.load();
			}
		});
		treeGrid.getSelectionModel().addListener(
				Events.SelectionChange,
				new Listener<SelectionChangedEvent<T>>() {
					public void handleEvent(
							SelectionChangedEvent<T> be) {
						if(detailPanel != null)
							detailPanel.bind(be.getSelection());
						if(bottomBar != null)
							bottomBar.ButtonEnabled(be.getSelection().size());
					}
				});		  

		this.add(treeGrid);
	}

	/**
	 * Need to Override
	 * @param parent
	 * @return
	 */
	 public  boolean HasChildren(T parent) {
		// TODO Auto-generated method stub
		return false;
	}

	public void DrowGridView(String autoExpandName) {
		DrowGridView(autoExpandName, true, true);
	}

	public void DrowGridView() {
		DrowGridView("");
	}

	public String GetSearchCondition() {
		if (tfSearch == null || tfSearch.getValue() == null)
			return "";
		else
			return tfSearch.getValue().toString();
	}
	
	public void SetSearchCondition(String condition)
	{
		if(tfSearch != null)
			tfSearch.setValue(condition);
	}
	
	public void setSearchButtonEvent(SelectionListener<ButtonEvent> listener) {
		ibSearch.removeAllListeners();
		ibSearch.addSelectionListener(listener);
	}


}

