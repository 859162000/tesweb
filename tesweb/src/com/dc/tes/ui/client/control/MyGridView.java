package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.GridView;


public class MyGridView extends GridView {
	
	public MyGridView() {
		super();
	}
		
	public void refreshRow(int row) {
		super.refreshRow(row);
	}
	
	protected void onRemove(ListStore<ModelData> ds, ModelData m, int index, boolean isUpdate) {		
		if (grid != null && grid.isViewReady()) {
		    detachWidget(index, true);
		    removeRow(index);
		}
	}

}
