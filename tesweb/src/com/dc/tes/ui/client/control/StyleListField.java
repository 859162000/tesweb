package com.dc.tes.ui.client.control;

import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class StyleListField<D extends ModelData> extends Field<D> implements SelectionProvider<D>  {
	protected ListView<D> listView;
	  protected ListStore<D> store;
	  protected int listHeight = 100;
	  protected int listWidth = 200;

	  private XTemplate template;
	  private String listStyle = "x-combo-list";
	  private String selectedStyle = "x-combo-selected";
	  private String itemSelector;
	  private El input;
	  private String valueField;

	  public StyleListField() {
	    listView = new ListView<D>();
	    setPropertyEditor(new ListModelPropertyEditor<D>());
	  }

	  public void addSelectionChangedListener(SelectionChangedListener<D> listener) {
	    addListener(Events.SelectionChange, listener);
	  }

	  @Override
	  public void disable() {
	    super.disable();
	    listView.disable();
	  }

	  @Override
	  public void enable() {
	    super.enable();
	    listView.enable();
	  }

	  /**
	   * Returns the display field.
	   * 
	   * @return the display field
	   */
	  public String getDisplayField() {
	    return getPropertyEditor().getDisplayProperty();
	  }

	  /**
	   * Returns the item selector.
	   * 
	   * @return the item selector
	   */
	  public String getItemSelector() {
	    return itemSelector;
	  }

	  /**
	   * Returns the field's list view.
	   * 
	   * @return the list view
	   */
	  public ListView<D> getListView() {
	    return listView;
	  }

	  @Override
	  public ListModelPropertyEditor<D> getPropertyEditor() {
	    return (ListModelPropertyEditor<D>) propertyEditor;
	  }

	  @Override
	  public String getRawValue() {
	    return "";
	  }

	  public List<D> getSelection() {
	    return listView.getSelectionModel().getSelectedItems();
	  }

	  /**
	   * Returns the field's store.
	   * 
	   * @return the store
	   */
	  public ListStore<D> getStore() {
	    return store;
	  }

	  /**
	   * Returns the custom template.
	   * 
	   * @return the template
	   */
	  public XTemplate getTemplate() {
	    return template;
	  }

	  @Override
	  public D getValue() {
	    List<D> sel = getSelection();
	    if (sel.size() > 1) {
	      return sel.get(0);
	    }
	    return null;
	  }

	  public String getValueField() {
	    return valueField;
	  }

	  public void removeSelectionListener(SelectionChangedListener<D> listener) {
	    removeListener(Events.SelectionChange, listener);
	  }

	  /**
	   * Sets the display field.
	   * 
	   * @param displayField the display field
	   */
	  public void setDisplayField(String displayField) {
	    getPropertyEditor().setDisplayProperty(displayField);
	  }

	  /**
	   * This setting is required if a custom XTemplate has been specified.
	   * 
	   * @param itemSelector the item selector
	   */
	  public void setItemSelector(String itemSelector) {
	    this.itemSelector = itemSelector;
	  }

	  @Override
	  public void setPropertyEditor(PropertyEditor<D> propertyEditor) {
	    assert propertyEditor instanceof ListModelPropertyEditor : "PropertyEditor must be a ModelPropertyEditor instance";
	    super.setPropertyEditor(propertyEditor);
	  }

	  public void setSelection(List<D> selection) {
	    if (selection.size() > 0) {
	      setValue(selection.get(0));
	      listView.getSelectionModel().setSelection(selection);
	    } else {
	      setValue(null);
	    }
	  }

	  /**
	   * Sets the list field's list store.
	   * 
	   * @param store the store
	   */
	  public void setStore(ListStore<D> store) {
	    this.store = store;
	  }

	  /**
	   * Sets the field's template used to render the list.
	   * 
	   * @param html the html frament
	   */
	  public void setTemplate(String html) {
	    assertPreRender();
	    template = XTemplate.create(html);
	  }

	  /**
	   * Sets the field's template used to render the list.
	   * 
	   * @param template
	   */
	  public void setTemplate(XTemplate template) {
	    assertPreRender();
	    this.template = template;
	  }

	  /**
	   * Sets the field's value field.
	   * 
	   * @param valueField the value field
	   */
	  public void setValueField(String valueField) {
	    this.valueField = valueField;
	  }

	  @Override
	  protected void doAttachChildren() {
	    super.doAttachChildren();
	    ComponentHelper.doAttach(listView);
	  }

	  @Override
	  protected void doDetachChildren() {
	    super.doDetachChildren();
	    ComponentHelper.doDetach(listView);
	  }

	  @Override
	  protected El getFocusEl() {
	    return el();
	  }

	  @Override
	  protected El getInputEl() {
	    return input;
	  }

	  @Override
	  protected void onClick(ComponentEvent ce) {
	    super.onClick(ce);
	    input.focus();
	  }

	  @Override
	  protected void onFocus(ComponentEvent ce) {
	    super.onFocus(ce);
	  }

	  @Override
	  protected void onRender(Element parent, int index) {
	    setElement(DOM.createDiv(), parent, index);
	    setStyleName("x-form-list");

	    if (width != null) {
	      setWidth(width);
	    } else {
	      setWidth(listWidth);
	    }

	    input = new El(DOM.createInputText());
	    input.dom.getStyle().setProperty("left", "-500");
	    input.dom.getStyle().setProperty("position", "absolute");
	    getElement().appendChild(input.dom);
	    if (template == null) {
	      String html = "<tpl for=\".\"><div class='x-combo-list-item ' style = 'padding:0px; margin:0px;'>{" + getDisplayField()
	          + "}</div><hr/></tpl>";
	      template = XTemplate.create(html);
	    }
	    listView.setBorders(false);
	    listView.setTemplate(template);
	    listView.setStyleName(listStyle);
	    listView.setItemSelector(itemSelector != null ? itemSelector : ".x-combo-list-item ");
	    listView.setStore(store);
	    
	    listView.setSelectStyle(selectedStyle);
	    listView.setOverStyle("x-combo-over");

	    listView.getSelectionModel().addListener(Events.SelectionChange,
	        new Listener<SelectionChangedEvent<D>>() {
	          public void handleEvent(SelectionChangedEvent<D> se) {
	            onSelectionChange(se.getSelection());
	          }
	        });

	    if (height != null) {
	      listView.setHeight(height);
	    } else {
	      listView.setHeight(listHeight);
	    }

	    listView.render(getElement());
	    disableTextSelection(true);
	    DOM.sinkEvents(input.dom, Event.FOCUSEVENTS);
	    sinkEvents(Event.ONCLICK);
	    
	    super.onRender(parent, index);
	  }

	  @Override
	  protected void onResize(int width, int height) {
	    super.onResize(width, height);
	    if (height != -1) {
	      listView.setHeight(height);
	    }
	  }

	  protected void onSelectionChange(List<D> sel) {
	    String prop = valueField != null ? valueField : listView.getDisplayProperty();
	    StringBuffer sb = new StringBuffer();
	    for (D m : sel) {
	      sb.append(m.get(prop));
	      sb.append(",");
	    }
	    String s = sb.toString();
	    if (sb.length() > 1) {
	      s = s.substring(0, s.length() - 1);
	    }
	    input.setValue(s);
	  }

	  @Override
	  protected boolean validateValue(String value) {
	    return true;
	  }
	}