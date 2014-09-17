package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTExecuteSetDirectory extends BaseTreeModel implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8119073213513346393L;
	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_ParentDirID = "parentDirId";
	public static String N_SortIndex = "sortIndex";
	public static String N_ObjType = "objType";
	public static String N_ObjectID = "objectId";
	public static String N_Name = "name";
	public static String N_Path = "path";
	public static String N_Desc = "desc";
	public static String N_StatusCHS = "statusChs";
	public static String N_Status = "status";
	private GWTQueueTask taskList = new GWTQueueTask();
	public void SetValue(String systemId, String name, Integer objType, Integer parentId, String desc){
		this.set(N_SystemID, systemId);
		this.set(N_Name, name);
		this.set(N_ParentDirID, parentId);
		this.set(N_ObjType, objType);
		this.set(N_Desc, desc);
	}
	
	public GWTExecuteSetDirectory(Integer id, String systemId, Integer objType, Integer objectId, Integer parentId,
			Integer sortIndex, String name, String path, String desc){
		SetValue(systemId, name, objType, parentId, desc);
		this.set(N_ID, id);
		this.set(N_ObjectID, objectId);
		this.set(N_Path, path);
		this.set(N_SortIndex, sortIndex);
	}
	
	public GWTExecuteSetDirectory(){
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
	
	public Integer GetObjType(){
		return this.get(N_ObjType)==null?null:Integer.parseInt(this.get(N_ObjType).toString());
	}
	
	public Integer GetObjectID(){
		return this.get(N_ObjectID)==null?null:Integer.parseInt(this.get(N_ObjectID).toString());
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
	
	public void SetStatus(int status){
		this.set(N_Status, status);
		if(status == 1){
			this.set(N_StatusCHS, "未执行");
		}else if(status == 2){
			this.set(N_StatusCHS, "正在执行");
		}else if(status == 3){
			this.set(N_StatusCHS, "执行完成");
		}		
	}
	
	public String GetStatus(){
		return this.get(N_StatusCHS);
	}

	public void setTaskList(GWTQueueTask taskList) {
		this.taskList = taskList;
	}

	public GWTQueueTask getTaskList() {
		return taskList;
	}
}
