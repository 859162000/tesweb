package com.dc.tes.ui.client.control;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * 拖动异步处理支持
 * @author HO274218
 *
 */
public class StrongTreePanelDragSource extends TreePanelDragSource {  
	  private List<TreeModel> sel;  
	  
	  @SuppressWarnings({ "rawtypes" })  
	  public StrongTreePanelDragSource(TreePanel tree) {  
	    super(tree);  
	  }  
	  
	  public void removeSource() {  
	    if (sel != null && sel.size() > 0) {  
	      for (TreeModel tm : sel) {  
	        ModelData m = (ModelData) tm.get("model");  
	        tree.getStore().remove(m);  
	      }  
	    }  
	  }  
	  
	  @Override  
	  protected void onDragDrop(DNDEvent event) {  
	    if (event.getOperation() == Operation.MOVE) {  
	      sel = event.getData();  
	    }  
	  }  
	} 
