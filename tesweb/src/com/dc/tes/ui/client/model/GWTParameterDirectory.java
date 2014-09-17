package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;


public class GWTParameterDirectory extends BaseTreeModel implements
		Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5647887807163118394L;
	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_ParentDirID = "parentDirId";
	public static String N_SortIndex = "sortIndex";
	public static String N_Name = "name";
	public static String N_Path = "path";
	public static String N_Desc = "desc";
	public static String N_NO = "no";
	
	private static int ID = 0;
	  

	public GWTParameterDirectory() {
		
	}
	
	/**
	 * 供同步树使用
	 * @param name
	 * @param children
	 */
	public GWTParameterDirectory(String name,String no,String parentDirId, BaseTreeModel[] children) {
		this.set(N_Name, name);
		this.set(N_ParentDirID, parentDirId);
		this.set(N_NO, no);
		set("id", ID++);
		for (int i = 0; i < children.length; i++) {
	        add(children[i]);
	    }
	}
	
	public GWTParameterDirectory(Integer id, String systemId, Integer parentDirID, Integer sortIndex,
			String name, String path, String desc){
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		this.set(N_ParentDirID, parentDirID);
		this.set(N_SortIndex, sortIndex);
		this.set(N_Name, name);
		this.set(N_Path, path);
		this.set(N_Desc, desc);
	}
	
	public void SetValue(String systemId, Integer parentDirID, Integer sortIndex,
			String name, String desc){
		this.set(N_SystemID, systemId);
		this.set(N_ParentDirID, parentDirID);
		this.set(N_SortIndex, sortIndex);
		this.set(N_Name, name);
		this.set(N_Desc, desc);
	}
	
	
	
	
	public Integer GetID(){
		return this.get(N_ID)==null?null:Integer.parseInt(this.get(N_ID).toString());
	}
	
	public String GetSystemID(){
		return this.get(N_SystemID);
	}
	
	public Integer GetParentDirID(){
		return this.get(N_ParentDirID)==null?null:Integer.parseInt(this.get(N_ParentDirID).toString());
	}
	
	public String GetSortIndex(){
		return this.get(N_SortIndex);
	}

	
	public String GetName(){
		return this.get(N_Name);
	}
	
	public String GetPath(){
		return this.get(N_Path);
	}
	
	public String GetDesc(){
		return this.get(N_Desc);
	}
	
	public boolean IsNew(){
		return GetID()==null;
	}
	
	public String getNO() {
		return this.get(N_NO);
	}
	
	public String getParentID() {
		return this.get(N_ParentDirID);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj != null && obj instanceof GWTParameterDirectory) {
			GWTParameterDirectory p = (GWTParameterDirectory)obj;
			if(p.GetID().equals(this.GetID()))
				return true;
		}
		return false;	
	}	
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return GetID();
	}

}
