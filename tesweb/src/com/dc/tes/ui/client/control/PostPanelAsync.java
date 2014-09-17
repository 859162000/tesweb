package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 异步Postpanel
 * Ext中的FormPanel会出现弹出新窗口的问题
 * google中的FormPanel则会没有样式化操作
 * 因此做了如下控件
 * 重载PostPanel以及添加了异步处理机制
 * @author scckobe
 *
 */
public class PostPanelAsync extends PostPanel {
	/**
	 * 内部类：submit之后触发的事件
	 * 在submit之后，frame加载完成之后进行初始化
	 * @author scckobe
	 *
	 */
	public static class SubmitCompleteEvent extends
			GwtEvent<SubmitCompleteHandler> {
		/**
		 * Response返回信息
		 */
		private String resultHtml;

		/**
		 * 构造函数
		 * @param resultsHtml  post对应页面的Response值
		 */
		protected SubmitCompleteEvent(String resultsHtml) {
			this.resultHtml = resultsHtml;
		}
		
		/**
		 * 获得Servlet的Response返回的信息
		 * @return 返回的信息
		 */
		public String getResults() {
			return resultHtml;
		}

		private static Type<SubmitCompleteHandler> TYPE;

		static Type<SubmitCompleteHandler> getType() {
			if (TYPE == null) {
				TYPE = new Type<SubmitCompleteHandler>();
			}
			return TYPE;
		}

		@Override
		public final Type<SubmitCompleteHandler> getAssociatedType() {
			return TYPE;
		}
		
		@Override
		protected void dispatch(SubmitCompleteHandler handler) {
			handler.onSubmitComplete(this);
		}
	}

	/**
	 * submit完成之后的事件处理接口
	 * 
	 * @author scckobe
	 *
	 */
	public interface SubmitCompleteHandler extends EventHandler {
		void onSubmitComplete(SubmitCompleteEvent event);
	}

	/**
	 * 提交事件
	 * 在PostPanelAsync调用submit时进行实例化
	 * 
	 * @author scckobe
	 *
	 */
	public static class SubmitEvent extends GwtEvent<SubmitHandler> {
		/**
		 * 是否已取消submit
		 */
		private boolean canceled = false;

		/**
		 * 取消submit操作
		 */
		public void cancel() {
			this.canceled = true;
		}
		
		/**
		 * 查看submit操作已被取消
		 * @return submit操作是否被取消
		 */
		public boolean isCanceled() {
			return canceled;
		}
		
		/**
		 * 设置是否取消submit操作
		 * @param canceled 是否取消submit操作
		 */
		void setCanceled(boolean canceled) {
			this.canceled = canceled;
		}
		
		private static Type<SubmitHandler> TYPE = new Type<SubmitHandler>();

		static Type<SubmitHandler> getType() {
			if (TYPE == null) {
				TYPE = new Type<SubmitHandler>();
			}
			return TYPE;
		}

		@Override
		public final Type<PostPanelAsync.SubmitHandler> getAssociatedType() {
			return TYPE;
		}
		
		@Override
		protected void dispatch(PostPanelAsync.SubmitHandler handler) {
			handler.onSubmit(this);
		}
	}

	/**
	 * submit调用时的事件处理接口
	 * @author scckobe
	 *
	 */
	public interface SubmitHandler extends EventHandler {
		void onSubmit(PostPanelAsync.SubmitEvent event);
	}

	/**
	 * 添加post请求完成之后的Handler
	 * @param handler 
	 * @return
	 */
	public HandlerRegistration addSubmitCompleteHandler(
			SubmitCompleteHandler handler) {
		return addHandler(handler, SubmitCompleteEvent.getType());
	}

	/**
	 * 添加post请求时的Handler
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addSubmitHandler(SubmitHandler handler) {
		return addHandler(handler, SubmitEvent.getType());
	}
	
	/**
	 * 友情提示信息
	 */
	String submitMsg = "文件上传中....";
	/**
	 * 友情提示对话框
	 */
	Window loadingWin = new Window();

	/**
	 * 是否手动render以及加载Iframe
	 */
	boolean autoAttach;
	
	/**
	 * 构造函数  
	 * 主要用于文件上传
	 */
	public PostPanelAsync()
	{
		this(true);
	}
	
	/**
	 * 构造函数  
	 * 主要用于文件下载
	 * 该Panel没有直接负载到任何其他的Container中
	 * @param autoAttach 是否手动render以及加载Iframe
	 */
	public PostPanelAsync(boolean autoAttach) {
		this.autoAttach = autoAttach;
		if(!autoAttach)
		{
			setBorders(false);
			setBodyBorder(false);
			setLabelWidth(55);
			setPadding(5);
			setHeaderVisible(false);
		}
	}
	
	/**
	 * 提交请求
	 */
	public void submit() {
		if (!fireSubmitEvent()) {
			return;
		}
		if(!autoAttach)
		{
			this.setVisible(false);
			render(RootPanel.get().getElement());
			onAttach();
		}else{
			setLoadinWin();
			loadingWin.show();
		}
		//getImpObj().submit(GetDom(), getFrameObj());
		super.submit();
	}
	
	/**
	 * 设置加载控件
	 */
	private void setLoadinWin()
	{
		loadingWin = new Window();
		loadingWin.setHeaderVisible(false);
		loadingWin.setBlinkModal(false);
		loadingWin.setResizable(false);
		loadingWin.setBodyBorder(false);
		loadingWin.setBorders(false);
		loadingWin.setDraggable(false);
		loadingWin.setFrame(false);
		loadingWin.setModal(true);
		loadingWin.setStyleAttribute("background-color", "white");
		HtmlContainer loadControl = new HtmlContainer();
		StringBuffer sb = new StringBuffer();
		sb.append("<span style='width:100%;height:100%;text-align:center; padding-top:30px; font-size:12px;'>" +
				"<img src='gxt/images/default/shared/large-loading.gif' height='32'/>" +
				submitMsg +
				"</span>");
		loadControl = new HtmlContainer(sb.toString());
		loadControl.removeAll();
		loadingWin.add(loadControl);
	}
	
	/**
	 * 设置加载提示信息
	 * @param submitMsg 加载提示信息
	 */
	public void setSubmitMsg(String submitMsg)
	{
		this.submitMsg = submitMsg;
	}
	
	/**
	 * 获得加载提示信息
	 * @return 加载提示信息
	 */
	public String getSubmitMsg()
	{
		return submitMsg;
	}
	
	/**
	 * submit之前的预处理
	 * @return 是否取消提交（submit）
	 */
	 private boolean fireSubmitEvent() {
		SubmitEvent event = new SubmitEvent();
		fireEvent(event);
		return !event.isCanceled();
	}
	 
	 protected boolean onFormSubmitAndCatch(UncaughtExceptionHandler handler) {
		 try {
		      return onFormSubmitImpl();
		    } catch (Throwable e) {
		      handler.onUncaughtException(e);
		      return false;
		    }
	}

	protected boolean onFormSubmitImpl() {
		return fireSubmitEvent();
	}

	private void onFrameLoadAndCatch(UncaughtExceptionHandler handler) {
		try {
			onFrameLoadImpl();
		} catch (Throwable e) {
			handler.onUncaughtException(e);
		}
	}

	/**
	 * 关键函数：异步调用完成之后通过该方法出发完成之后的处理函数
	 */
	private void onFrameLoadImpl() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				loadingWin.hide();
				fireEvent(new SubmitCompleteEvent(getImpObj().getContents(
						getFrameObj())));
				if(!autoAttach)
				{
//					removeFromParent();
				}
			}
		});
	}

	@Override
	public boolean onFormSubmit() {
		UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
		if (handler != null) {
			return onFormSubmitAndCatch(handler);
		} else {
			return onFormSubmitImpl();
		}
	}
	
	@Override
	public void onFrameLoad() {
		UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
		if (handler != null) {
			onFrameLoadAndCatch(handler);
		} else {
			onFrameLoadImpl();
		}
	}

	@Override
	public void onDetach()
	{
		if(loadingWin != null)
			loadingWin.hide();
		super.onDetach();
	}
}



