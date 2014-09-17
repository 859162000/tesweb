/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.dc.tes.ui.client.control;

import java.util.ArrayList;
import java.util.Stack;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.model.GWTMoniLogDetail;
import com.dc.tes.ui.client.page.MonitorPage;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

public class LogInfoPopPanel extends ContentPanel {

  private static Stack<LogInfoPopPanel> infoStack = new Stack<LogInfoPopPanel>();
  private static ArrayList<LogInfoPopPanel> slots = new ArrayList<LogInfoPopPanel>();
  private ContentPanel logPanel = null;

  /**
   * Displays a message using the specified config.
   * 
   * @param config the LogInfoPopPanel config
   */
  public static void display(InfoConfig config, GWTMoniLogDetail item) {
    pop().show(config, item);
  }

  private static int firstAvail() {
    int size = slots.size();
    for (int i = 0; i < size; i++) {
      if (slots.get(i) == null) {
        return i;
      }
    }
    return size;
  }

  private static LogInfoPopPanel pop() {
    LogInfoPopPanel LogInfoPopPanel = infoStack.size() > 0 ? (LogInfoPopPanel) infoStack.pop() : null;
    if (LogInfoPopPanel == null) {
      LogInfoPopPanel = new LogInfoPopPanel();
    }
    return LogInfoPopPanel;
  }

  private static void push(LogInfoPopPanel LogInfoPopPanel) {
    infoStack.push(LogInfoPopPanel);
  }

  protected InfoConfig config;
  protected int level;

  /**
   * Creates a new LogInfoPopPanel instance.
   */
  public LogInfoPopPanel() {
    baseStyle = "x-info";
    frame = true;
    setShadow(true);
    setLayoutOnChange(true);
  }

  public void hide() {
    super.hide();
    afterHide();
  }

  /**
   * Displays the LogInfoPopPanel.
   * 
   * @param config the LogInfoPopPanel config
   */
  public void show(InfoConfig config, GWTMoniLogDetail item) {
    this.config = config;
    config.width = 240;
    config.height = 110;
    onShowInfo(item);
  }

  protected void onShowInfo(GWTMoniLogDetail item) {
    RootPanel.get().add(this);
    el().makePositionable(true);

    setTitle();
    //setText();
    //this.ad
    String logStr = "";
    String title = "";
    if(item.get(GWTMoniLogDetail.N_REGINFO) != null &&
    		!item.get(GWTMoniLogDetail.N_REGINFO).equals("")){
    	title = "<b>核心向监控服务发起注册请求</b><br/>";
    	logStr=title+"<b>系统名称:&nbsp;</b>"+ item.get(GWTMoniLogDetail.N_REGINFO).toString() + "<br/> <br/> <br/>";
    } else{
    	
    	if(item.get(GWTMoniLogDetail.N_TRANSTATE).equals(1))
    		title = "<b>模拟器返回一条响应信息</b><br>";
    	else
    		title = "<b>模拟器发起一条请求信息</b><br>";
    	logStr = title
    			+ "<b>系统名称:&nbsp;</b>" + item.get(GWTMoniLogDetail.N_SYSNAME).toString() + "<br/>"
    			+"<b>出错提示:&nbsp;&nbsp;</b>" + item.get(GWTMoniLogDetail.N_ERRMSG).toString() + "<br/>";
    }
	
	logPanel = new ContentPanel();
    logPanel.setHeaderVisible(false);
    logPanel.setBorders(false);
    //logPanel.setHeight(55);
    logPanel.addText(logStr);
    //logPanel.setFrame(true);
    logPanel.setBodyBorder(false);
    logPanel.addButton(new Button("查看详情",new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			// TODO Auto-generated method stub
			if(AppContext.GetMonitorPage() == null){
				return;
			}
			AppContext.getTabPanel().setSelection(AppContext.getTabPanel().getItemByItemId("Monitor"));
		} 	
    }));
    
    removeAll();
    add(logPanel);
	
    level = firstAvail();
    slots.add(level, this);

    Point p = position();
    el().setLeftTop(p.x, p.y);
    
    setSize(config.width, config.height);

    afterShow();
  }

  protected Point position() {
    Size s = XDOM.getViewportSize();
    int left = s.width - config.width - 10 + XDOM.getBodyScrollLeft();
    int top = s.height - config.height - 10 - (level * (config.height + 10))
        + XDOM.getBodyScrollTop();
    return new Point(left, top);
  }

  private void afterHide() {
    RootPanel.get().remove(this);
    slots.set(level, null);
    push(this);
  }

  private void afterShow() {
    Timer t = new Timer() {
      public void run() {
        afterHide();
      }
    };
    t.schedule(config.display);
  }

  @SuppressWarnings("unused")
private void setText() {
    if (config.text != null) {
      if (config.params != null) {
        config.text = Format.substitute(config.text, config.params);
      }
      removeAll();
      addText(config.text);
    }
  }

  private void setTitle() {
    if (config.title != null) {
      head.setVisible(true);
      if (config.params != null) {
        config.title = Format.substitute(config.title, config.params);
      }
      setHeading(config.title);
    } else {
      head.setVisible(false);
    }
  }

}
