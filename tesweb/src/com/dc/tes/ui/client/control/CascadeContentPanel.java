package com.dc.tes.ui.client.control;


import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;



/**
 * 联动的GridContentPanel
 * 实现列表中的一行关联显示另一个列表
 * @author HO274218
 *
 * @param <T>
 */
public class CascadeContentPanel<T extends ModelData> extends GridContentPanel<T>{

	private ICascadePanel cascadePanel = null;
	public CascadeContentPanel(){
		super();
	}
	
	@Override
	public void DrowGridView(String autoExpandName, boolean showCk,
			boolean showNum) {
		super.DrowGridView(autoExpandName, showCk, showNum);
		super.getDataGrid().getSelectionModel().removeAllListeners();
		super.getDataGrid().getSelectionModel().addListener(
				Events.SelectionChange,
				new Listener<SelectionChangedEvent<T>>() {
					public void handleEvent(
							SelectionChangedEvent<T> be) {
						if(bottomBar != null)
							bottomBar.ButtonEnabled(be.getSelection().size());
						if(be.getSelection().size()!=0)
							cascadePanel.showCascadePanel(be.getSelection().get(0));
						else{
							cascadePanel.showCascadePanel(null);
						}
					}
				});
	}
	
	public void setCascadePanel(ICascadePanel resultContentPanel) {
		this.cascadePanel = resultContentPanel;
	}

	public ICascadePanel getCascadePanel() {
		return cascadePanel;
	}
	
	
}
