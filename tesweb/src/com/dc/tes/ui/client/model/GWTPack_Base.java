package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.Collection;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
/**
 * 报文结构基类
 * 现有用途：为了让结构域与字段域的ID不一样
 * @author scckobe
 *
 */
public class GWTPack_Base extends BaseTreeModel implements Serializable{
	private static final long serialVersionUID = -3646014131056358729L;
	protected static int ID = 0;
	public static String m_id = "id";
	public static String m_name = "name";
	public static String m_length = "len";
	public static String m_isArray = "isarray";
	public static String m_defaultValue = "defaultValue"; 
	public static String m_data = "data"; 
	
	public GWTPack_Base()
	{
		set(m_id, ID++);
	}
	
	public GWTPack_Base(String name)
	{
		this(name,String.valueOf(false));
	}
	
	public GWTPack_Base(String name,String isArray)
	{
		this();
		set(m_isArray, isArray);
		set(m_name, name);
	}
	
	public Integer getId() {
		return (Integer) get(m_id);
	}

	public String getName() {
		return (String) get(m_name);
	}
	
	public boolean getIsArray() {
		String bool = get(m_isArray).toString();
		return Boolean.parseBoolean(bool);
	}
	
	@Override
	public <X> X set(String name, X value)
	{
		if(value == null)
			value = (X) "";
		if(name.equals(m_defaultValue)) {
			if(this.get(m_data) != null && this.get(m_data).equals(""))
				set(m_data,value);
		}
		return super.set(name, value);
	 }
	
	public GWTPack_Base copy()
	{
		GWTPack_Base copyValue = null;
		if(this instanceof GWTPack_Field)
			copyValue = new GWTPack_Field();
		else if(this instanceof GWTPack_Struct)
			copyValue = new GWTPack_Struct();
		else 
			copyValue = new GWTPack_Base();
		
		Collection<String> properties = getPropertyNames();
		for(String pro : properties)
		{
			if(pro.equalsIgnoreCase(m_id))
				continue;
			copyValue.set(pro, get(pro));
		}
		
		for(ModelData child : this.getChildren())
		{
			copyValue.add(((GWTPack_Base)child).copy());
		}
		
		return copyValue;
	}
}
