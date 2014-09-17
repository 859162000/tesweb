package com.dc.tes.ui.client.control;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * 异步处理拖拽保存
 * 主要解决需要对拖拽行为进行”是否确定拖拽“的支持
 * 原本拖拽时在onDragDrap触发时TreeGridDragSource直接就对拖拽源的item进行
 * 删除处理，使用此类则是把删除操作以方法的形式曝露出来，可以在真正响应拖拽
 * 处理时再进行删除
 * @author HO274218
 *
 */
public class StrongTreeGridDragSource extends TreeGridDragSource{
	
	private List<TreeModel> sel;
	
	public StrongTreeGridDragSource(Component component){
		super(component);
	}
	
	public void removeSource(){
		if (sel != null && sel.size() > 0) {  
			for (TreeModel tm : sel) {
		        ModelData m = (ModelData) tm.get("model");
		        treeGrid.getTreeStore().remove(m);		        
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
