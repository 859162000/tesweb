package com.dc.tes.ui.client.control;

import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.MainPage;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * Grid列表ContentPanel基类
 * 
 * @author shenfx
 * 
 */
public class GridContentPanel<T extends ModelData> extends ContentPanel {

	/**
	 * 搜索框
	 */
	protected TextField<String> tfSearch = null;

	/**
	 * 搜索栏
	 */
	protected ToolBar searchBar = null;
	
	/**
	 * 数据DataGrid
	 */
	protected Grid<T> dataGrid = null;

	protected RpcProxy<PagingLoadResult<T>> proxy = null;

	protected PagingLoader<PagingLoadResult<ModelData>> loader = null;

	protected ListStore<T> store = null;

	protected List<ColumnConfig> columns = null;

	private int height = 243;
	
	CheckBoxSelectionModel<T> columnCK = new CheckBoxSelectionModel<T>();
	RowNumberer columnNum = new RowNumberer();

	/**
	 * 底部工具栏
	 */
	protected ButtonBar bottomBar = null;
	
	FormContentPanel<T> detailPanel = null;
	
	public GridContentPanel() {
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

	public Grid<T> getDataGrid() {
		return dataGrid;
	}

	public List<T> getSelection()
	{
		return dataGrid.getSelectionModel().getSelection();
	}
	
	public void reloadGrid()
	{
		dataGrid.clearState();
		dataGrid.fireEvent(Events.Attach);
	}
	
	public void setDataGrid(Grid<T> dataGrid) {
		this.dataGrid = dataGrid;
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

	public RpcProxy<PagingLoadResult<T>> getProxy() {
		return proxy;
	}

	public void setProxy(RpcProxy<PagingLoadResult<T>> proxy) {
		this.proxy = proxy;
	}

	public PagingLoader<PagingLoadResult<ModelData>> getLoader() {
		return loader;
	}

	public void setLoader(PagingLoader<PagingLoadResult<ModelData>> loader) {
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

	public ListStore<T> getStore() {
		return store;
	}

	public void setStore(ListStore<T> store) {
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

		final Button ibSearch = new Button();
		ibSearch.setIcon(MainPage.ICONS.search());
		ibSearch.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (dataGrid != null) {
					dataGrid.clearState();
					dataGrid.fireEvent(Events.Attach);
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
		if(dataGrid != null)
			dataGrid.setHeight(height);
	}
	
	public void DrowGridView(String autoExpandName, boolean showCk,
			boolean showNum) {
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
//		loader.setRemoteSort(true);
		store = new ListStore<T>(loader) {
		};

		if (showNum)
			columns.add(0, columnNum);
		if (showCk)
		{
			ColumnConfig ckConfig = columnCK.getColumn();
			
			columns.add(0, ckConfig);
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

		
		dataGrid = new Grid<T>(store, cm);
		dataGrid.setLoadMask(true);
		dataGrid.getView().setForceFit(true);
		dataGrid.setView(new GridView(){
	    	protected void onBeforeDataChanged(StoreEvent<ModelData> se) {
	    	    if (grid.isLoadMask()) {
	    	      grid.mask("加载中...");
	    	    }
	    	  }
	    });
		dataGrid.setBorders(false);
		dataGrid.setHeight(height);
		dataGrid.setAutoWidth(true);
		if (showCk)
		{
			dataGrid.setSelectionModel(columnCK);
			dataGrid.addPlugin(columnCK);  
		}
		if (!autoExpandName.isEmpty())
			dataGrid.setAutoExpandColumn(autoExpandName);
		dataGrid.addListener(Events.Attach, new Listener<GridEvent<T>>() {
			public void handleEvent(GridEvent<T> be) {
				PagingLoadConfig config = new BasePagingLoadConfig();
				config.setOffset(0);
				config.setLimit(10);
				if(bottomBar != null)
					config.setLimit(bottomBar.getPageSize());
				Map<String, Object> state = dataGrid.getState();
				if (state.containsKey("offset")) {
					int offset = (Integer) state.get("offset");
					int limit = (Integer) state.get("limit");
					config.setOffset(offset);
					config.setLimit(limit);
				}
				if (state.containsKey("sortField")) {
					config.setSortField((String) state.get("sortField"));
					config.setSortDir(SortDir.valueOf((String) state
							.get("sortDir")));
				}
				loader.load(config);
			}
		});
		dataGrid.getSelectionModel().addListener(
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

		this.add(dataGrid);
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

	

}
