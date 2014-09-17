package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTCaseDirectory extends BaseTreeModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 923091708810941280L;
	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_ParentDirID = "parentDirId";
	public static String N_SortIndex ="sortIndex";
	public static String N_Name = "name";
	public static String N_Path = "path";
	public static String N_Desc = "description";
	
	public void SetValue(String systemId, String name, String parentId, String desc){
		this.set(N_SystemID, systemId);
		this.set(N_Name, name);
		this.set(N_ParentDirID, parentId);
		this.set(N_Desc, desc);
	}
	public GWTCaseDirectory(){
	}
	public GWTCaseDirectory(String id, String systemId, String parentId, String sortIndex,
			String name, String path, String desc){
		SetValue(systemId, name, parentId, desc);
		this.set(N_ID, id);
		this.set(N_SortIndex, sortIndex);
		this.set(N_Path, path);
	}

	public String GetID(){
		return this.get(N_ID);
	}
	
	public String GetSystemID(){
		return this.get(N_SystemID);
	}
	
	public String GetParentDirID(){
		return this.get(N_ParentDirID);
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
		return GetID() == null;
	}
}
