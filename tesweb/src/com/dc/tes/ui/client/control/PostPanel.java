package com.dc.tes.ui.client.control;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
//import com.google.gwt.user.client.ui.impl.FormPanelImpl;
import com.google.gwt.user.client.ui.impl.FormPanelImpl;
import com.google.gwt.user.client.ui.impl.FormPanelImplHost;

/**
 * 重新Panel类 由于普通的Panelsubmit会弹出新窗口
 * 原因是由于通过javascript写name属性失败，所以本类直接通过String写IFrame
 * 
 * @author scckobe
 *
 */
public class PostPanel extends ContentPanel implements FormPanelImplHost {
	private static FormPanelImpl impl = GWT.create(FormPanelImpl.class);
	private static int formId = 0;
	private LabelAlign labelAlign = LabelAlign.LEFT;
	private int labelWidth = 75;
	private int fieldWidth = 210;
	private String labelSeparator = ":";
	private boolean hideLabels;
	private int padding = 10;
	private El form;
	private Method method = Method.GET;
	private Encoding encoding;
	private String action = "javascript:;";
	private String frameName;
	private Element iframe;
	private String target;
	
	public PostPanel() {
		frameName = "gxt.formpanel-" + (++formId);
		setTarget(frameName);
	}
	
	public void clear() {
		for (Field<?> f : getFields()) {
			f.setValue(null);
		}
	}
	  /**
	   * Resets the dirty state for all fields by setting the original value to be
	   * equal to the current value.
	   */
	  @SuppressWarnings({"unchecked", "rawtypes"})
	  public void clearDirtyFields() {
	    for (Field f : getFields()) {
	      if (f.isDirty()) {
	        f.setOriginalValue(f.getValue());
	      }
	    }
	  }


	public void reset() {
		for (Field<?> f : getFields()) {
			f.reset();
		}
	}

	public String getAction() {
		return action;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public List<Field<?>> getFields() {
		List<Field<?>> fields = new ArrayList<Field<?>>();
		getChildFields(this, fields);
		return fields;
	}

	public int getFieldWidth() {
		return fieldWidth;
	}

	public boolean getHideLabels() {
		return hideLabels;
	}

	public LabelAlign getLabelAlign() {
		return labelAlign;
	}

	public String getLabelSeparator() {
		return labelSeparator;
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	@Override
	public El getLayoutTarget() {
		return form;
	}

	public Method getMethod() {
		return method;
	}

	public int getPadding() {
		return padding;
	}

	public String getTarget() {
		return target;
	}

	public boolean isDirty() {
		for (Field<?> f : getFields()) {
			if (f.isDirty()) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid() {
		return isValid(false);
	}

	public boolean isValid(boolean preventMark) {
		boolean valid = true;
		for (Field<?> f : getFields()) {
			if (!f.isValid(preventMark)) {
				valid = false;
			}
		}
		return valid;
	}

	public boolean onFormSubmit() {
		UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
		if (handler != null) {
			return onFormSubmitAndCatch(handler);
		} else {
			return onFormSubmitImpl();
		}
	}

	public void onFrameLoad() {
		UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
		if (handler != null) {
			onFrameLoadAndCatch(handler);
		} else {
			onFrameLoadImpl();
		}
	}

	public void setAction(String url) {
		this.action = url;
		if (rendered) {
			form.dom.setAttribute("action", url);
		}
	}

	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
		if (rendered) {
			impl.setEncoding(form.dom, encoding.value());
		}
	}

	public void setFieldWidth(int fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	public void setHideLabels(boolean hideLabels) {
		this.hideLabels = hideLabels;
	}

	public void setLabelAlign(LabelAlign align) {
		this.labelAlign = align;
	}

	public void setLabelSeparator(String labelSeparator) {
		this.labelSeparator = labelSeparator;
	}

	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	public void setMethod(Method method) {
		this.method = method;
		if (rendered) {
			form.dom.setAttribute("method", method.name().toLowerCase());
		}
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setReadOnly(boolean readOnly) {
		for (Field<?> f : getFields()) {
			f.setReadOnly(readOnly);
		}
	}

	public void submit() {
		if (fireEvent(Events.BeforeSubmit, new PostEvent(this))) {
			impl.submit(form.dom, iframe);
		}
	}
	
//	 @Override
//	  protected Size adjustBodySize() {
//	    return body.getFrameSize();
//	  }
	 
	@Override
	protected void onAttach() {
		super.onAttach();
		createFrame();
		XDOM.getBody().appendChild(iframe);
		impl.hookEvents(iframe, form.dom, this);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		impl.unhookEvents(iframe, form.dom);
		XDOM.getBody().removeChild(iframe);
		iframe = null;
		form = null;
	}


	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		body.setStyleAttribute("background", "none");

		form = new El(DOM.createForm());
		form.setStyleAttribute("overflow", "hidden");
		body.appendChild(form.dom);

		setMethod(method);
		setTarget(this.target);

		if (encoding != null) {
			setEncoding(encoding);
		}
		if (action != null) {
			setAction(action);
		}

		getLayoutTarget().setStyleAttribute("padding", padding + "px");

		if (getLayout() == null) {
			FormLayout layout = new FormLayout();
			layout.setDefaultWidth(fieldWidth);
			layout.setLabelWidth(labelWidth);
			layout.setLabelAlign(labelAlign);
			layout.setLabelSeparator(labelSeparator);
			layout.setHideLabels(hideLabels);
			setLayout(layout);
		}

		form.addEventsSunk(Event.ONLOAD);
		
//		setAriaRole("region");
	}


	
	protected void createFrame() {
		StringBuffer sb = new StringBuffer();
		sb.append("<IFRAME id=x-auto-22 name = '" + frameName + "' " +
				"style=\"BORDER: 0px; display:none; visibility:hidden;" +
				" HEIGHT: 1px;width:1px\"frameborder=\"0\"" +
				" ></IFRAME>");
		iframe = new El(sb.toString()).dom;
		if (GXT.isIE && GXT.isSecure) {
			iframe.setPropertyString("src", GXT.SSL_SECURE_URL);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getChildFields(Container<Component> c, List<Field<?>> fields) {
		for (Component comp : c.getItems()) {
			if (comp instanceof Field) {
				fields.add((Field) comp);
			} else if (comp instanceof Container) {
				getChildFields((Container) comp, fields);
			}
		}
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
		return fireEvent(Events.BeforeSubmit, new PostEvent(this));
	}

	private void onFrameLoadAndCatch(UncaughtExceptionHandler handler) {
		try {
			onFrameLoadImpl();
		} catch (Throwable e) {
			handler.onUncaughtException(e);
		}
	}

	private void onFrameLoadImpl() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				fireEvent(Events.Submit, new PostEvent(PostPanel.this, impl
						.getContents(iframe)));
			}
		});
	}

	private void setTarget(String target) {
		this.target = target;
		if (rendered) {
			form.dom.setPropertyString("target", target);
		}
	}
	
	protected FormPanelImpl getImpObj()
	{
		return impl;
	}
	
	protected Element getFrameObj()
	{
		return iframe;
	}
	
	protected Element GetDom()
	{
		return form.dom;
	}
}
