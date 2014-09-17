package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class MyGrid<M extends ModelData> extends Grid<M> {
	
	public MyGrid(ListStore<M> store, ColumnModel cm) {
		super(store,cm);
		super.setView(new MyGridView());
	}

	public void refreshRow(int row) {
		((MyGridView) this.view).refreshRow(row);
	}

}
