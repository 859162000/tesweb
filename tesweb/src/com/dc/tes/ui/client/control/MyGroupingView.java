package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;

public class MyGroupingView extends GroupingView {
	
	public MyGroupingView() {
		super();
	}
	
	public void setPreventScrollToTopOnRefresh(boolean prevent) {
		super.preventScrollToTopOnRefresh = prevent;
	}
	
	public void setScrollBottom() {
		Point p = super.getScrollState();
		super.scroller.setScrollTop(p.y + 100);
		super.scroller.setScrollLeft(p.x + 0);
	}

}
