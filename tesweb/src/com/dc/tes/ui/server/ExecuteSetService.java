package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.ExecuteSetDirectory;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.op.Op;

import org.apache.commons.logging.Log;
import com.dc.tes.ui.client.IExecuteSetService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTExecuteSetDirectory;
import com.dc.tes.ui.client.model.GWTQueue;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;



public class ExecuteSetService extends RemoteServiceServlet implements
		IExecuteSetService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IDAL<ExecuteSetDirectory> executeSetDirDAL = DALFactory.GetBeanDAL(ExecuteSetDirectory.class);
	private IDAL<ExecuteSet> executeSetDAL = DALFactory.GetBeanDAL(ExecuteSet.class);
	private static final Log log = LogFactory.getLog(ExecuteSetService.class);
	
	private ExecuteSetDirectory ModelToBean(ExecuteSetDirectory executeSet, GWTExecuteSetDirectory gwtExSet){
		if(gwtExSet == null){
			return null;
		}
		
		ExecuteSetDirectory sed = executeSet;
		if(sed == null){
			if(gwtExSet.GetID()!=null){
				sed = executeSetDirDAL.Get(Op.EQ(GWTExecuteSetDirectory.N_ID, gwtExSet.GetID()));
			}else
				sed = new ExecuteSetDirectory();		
		}
		
		sed.setName(gwtExSet.GetName());
		sed.setObjType(gwtExSet.GetObjType());
		sed.setDesc(gwtExSet.GetDesc());
		if(gwtExSet.GetParentDirID()!=null && gwtExSet.GetParentDirID()!=sed.getParentDirId()){
			sed.setParentDirId(gwtExSet.GetParentDirID());
			if(gwtExSet.GetParentDirID() != 0){
				ExecuteSetDirectory parent = executeSetDirDAL.Get(Op.EQ("id", sed.getParentDirId()));
				String path = parent.getPath() + (parent.getPath().endsWith("/")?sed.getName():("/"+sed.getName()));
				sed.setPath(path);
			}else{
				sed.setPath(sed.getName());
			}
		}else if(gwtExSet.GetParentDirID() == null){
			sed.setPath(sed.getName());
			sed.setParentDirId(0);
		}
		sed.setSystemId(Integer.parseInt(gwtExSet.GetSystemID()));
		return sed;
	}
	
	private static GWTExecuteSetDirectory BeanToModel(ExecuteSetDirectory executeSet){
		GWTExecuteSetDirectory gwt = new GWTExecuteSetDirectory(executeSet.getId(), 
				executeSet.getSystemId().toString(), executeSet.getObjType(),
				executeSet.getExecuteSetId(), executeSet.getParentDirId(),executeSet.getSortIndex(),
				executeSet.getName(), executeSet.getPath(), executeSet.getDesc());
		gwt.setTaskList(null);
	//	if(gwt.GetObjType() == 1 && gwt.GetObjectID()!=null)
	//		gwt.setTaskList(new ExecuteSetTaskService().GetQueueTask(gwt.GetObjectID().toString()));
		return gwt;
	}
		
	
	@Override
	public List<ModelData> getExecuteSetTree(GWTSimuSystem system,
			GWTExecuteSetDirectory gwtExecuteSetDirectory) {
		// TODO Auto-generated method stub
		List<ModelData> result = new ArrayList<ModelData>();
		List<ExecuteSetDirectory> esds = executeSetDirDAL.ListAll(
				GWTExecuteSetDirectory.N_Name, true, 
				Op.EQ(GWTExecuteSetDirectory.N_ParentDirID,
						gwtExecuteSetDirectory == null ? 0
								: gwtExecuteSetDirectory.GetID()),
				Op.EQ(GWTExecuteSetDirectory.N_SystemID, Integer.parseInt(system.GetSystemID())));
		for (ExecuteSetDirectory esd : esds) {
			result.add(BeanToModel(esd));
		}
		return result;
	}

	@Override
	public GWTExecuteSetDirectory saveOrUpdateExecuteSet(
			GWTExecuteSetDirectory gwt, Integer loginLogId) {
		// TODO Auto-generated method stub
		try{
			if(gwt == null){
				return null;
			}
			if(gwt.GetID() != null){
				ExecuteSetDirectory execSet = executeSetDirDAL.Get(Op.EQ(GWTExecuteSetDirectory.N_ID, gwt.GetID()));
				if(!execSet.getName().equals(gwt.GetName())){
					if(execSet.getExecuteSetId() != null) {
						ExecuteSet qList = executeSetDAL.Get(Op.EQ(GWTQueue.N_ID, execSet.getExecuteSetId().toString()));
						String oldName = qList.getName();
						qList.setName(gwt.GetName());
						executeSetDAL.Edit(qList);
						OperationLogService.writeOperationLog(OpType.ExecuteSet, IDUType.Update, Integer.parseInt(qList.getId()), 
								oldName, "executeSetName", oldName, qList.getName(), loginLogId);
					}
				}
				executeSetDirDAL.Edit(ModelToBean(execSet, gwt));
				return gwt;
			}else{
				ExecuteSetDirectory esd = ModelToBean(null, gwt);
				Integer count = executeSetDirDAL.Count(Op.EQ(GWTExecuteSetDirectory.N_ObjType, esd.getObjType()),
						Op.EQ(GWTExecuteSetDirectory.N_ParentDirID, esd.getParentDirId()));
				esd.setSortIndex(count + 1);
				if(esd.getObjType()==1){
					ExecuteSet executeSet = new ExecuteSet();
					executeSet.setName(gwt.GetName());
					executeSet.setSystemId(gwt.GetSystemID());
					executeSet.setImportBatchNo((new Date()).toString());
					executeSet.setDescription(gwt.GetDesc());
					executeSetDAL.Add(executeSet);
					OperationLogService.writeOperationLog(OpType.ExecuteSet, IDUType.Insert, 
							Integer.parseInt(executeSet.getId()), executeSet.getName(),
							"executeSetName", null, executeSet.getName(), loginLogId);
					esd.setExecuteSetId(Integer.parseInt(executeSet.getId()));
				}
					
				
				executeSetDirDAL.Add(esd);
				return BeanToModel(esd);
			}
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean deleteSelectedItem(List<ModelData> items, Integer loginLogId) {
		// TODO Auto-generated method stub
		try{
			for(ModelData item : items){
				ExecuteSetDirectory executeSetDirectory = executeSetDirDAL.Get(Op.EQ(GWTExecuteSetDirectory.N_ID, 
						((GWTExecuteSetDirectory)item).GetID()));
				if(executeSetDirectory.getObjType()==1){
					Integer objId = ((GWTExecuteSetDirectory)item).GetObjectID();					
					if(objId!=null){
						ExecuteSet executeSet = executeSetDAL.Get(Op.EQ(GWTQueue.N_ID, 
								objId.toString()));
						if(executeSet!=null){
							executeSetDAL.Del(executeSet);
							OperationLogService.writeOperationLog(OpType.ExecuteSet, IDUType.Delete, Integer.parseInt(executeSet.getId()), 
									executeSet.getName(), "executeSetName", executeSet.getName(), null, loginLogId);
						}		
					}
				}else{
					int count = executeSetDirDAL.Count(Op.EQ(GWTExecuteSetDirectory.N_ParentDirID, 
							executeSetDirectory.getId()));
					if(count != 0){
						return false;
					}
				}
				executeSetDirDAL.Del(executeSetDirectory);		
			}
			return true;
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	







}
