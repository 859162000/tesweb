package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.event.ComponentEvent;

public class PostEvent extends ComponentEvent {

	  private PostPanel formPanel;
	  private String resultHtml;

	  public PostEvent(PostPanel formPanel) {
	    this(formPanel, null);
	  }

	  public PostEvent(PostPanel formPanel, String resultHtml) {
	    super(formPanel);
	    this.formPanel = formPanel;
	    this.resultHtml = resultHtml;
	  }

	  /**
	   * Returns the source form panel.
	   * 
	   * @return the form panel
	   */
	  public PostPanel getFormPanel() {
	    return formPanel;
	  }

	  /**
	   * Sets the source form panel.
	   * 
	   * @param formPanel the form panel
	   */
	  public void setFormPanel(PostPanel formPanel) {
	    this.formPanel = formPanel;
	  }

	  /**
	   * Returns the result html.
	   * 
	   * @return the result html
	   */
	  public String getResultHtml() {
	    return resultHtml;
	  }

	  /**
	   * Sets the result html.
	   * 
	   * @param resultHtml the result html
	   */
	  public void setResultHtml(String resultHtml) {
	    this.resultHtml = resultHtml;
	  }

	}
