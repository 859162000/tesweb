package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseDirectory;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.model.ParameterDirectory;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTCaseParamExpectedValue;
import com.dc.tes.ui.client.model.GWTHost;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SysDynamicParameterService extends RemoteServiceServlet implements ISysDynamicParameter {
	private static final long serialVersionUID = 2965279556562502797L;
	
	private static final Log log = LogFactory.getLog(SysDynamicParameterService.class);
	IDAL<SystemDynamicParameter> dataDao = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
	IDAL<DbHost> hostDao =  DALFactory.GetBeanDAL(DbHost.class);
	IDAL<TransactionDynamicParameter> tranParaDao =  DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
	IDAL<CaseParameterExpectedValue> caseParaValueDao =  DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
	IDAL<ParameterDirectory> paramDirDAL = DALFactory.GetBeanDAL(ParameterDirectory.class);
	@Override
	public PagingLoadResult<GWTSysDynamicPara> GetGWTSysDynamicParaPageList(
			String systemID, String searchInfo, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		
		try {
			int count;
			List<SystemDynamicParameter> lst;
			Op op1 = Op.EQ(GWTSysDynamicPara.N_SystemID, systemID);
			if (searchInfo.isEmpty()) {
				count = dataDao.Count(op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.List(pse.getStart(), pse.getEnd(),op1);
			} else {
				String[] searchField = new String[]{GWTSysDynamicPara.N_ParameterName,
						GWTSysDynamicPara.N_ParameterDesc};
				count = dataDao.MatchCount(searchInfo,searchField,op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.Match(searchInfo,searchField,pse.getStart(), pse.getEnd(),op1);
			}

			List<GWTSysDynamicPara> returnList = new ArrayList<GWTSysDynamicPara>();
			for (SystemDynamicParameter data : lst)
				returnList.add(BeanToModel(data));

			return new BasePagingLoadResult<GWTSysDynamicPara>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
	}

	private GWTSysDynamicPara BeanToModel(SystemDynamicParameter data) {
		// TODO Auto-generated method stub
		if(data == null)
			return null;
		DbHost host = new DbHost();
		if(data.getParameterHostId() != null && !data.getParameterHostId().isEmpty())
			host = hostDao.Get(Op.EQ("hostid",data.getParameterHostId()));
		return new GWTSysDynamicPara(data.getId(),data.getName(),data.getDesc(),data.getParameterType(),
				data.getDefaultExpectedValue(),data.getParameterExpression(),data.getDisplayFlag(),
				data.getIsValid(),data.getSystemId(),String.valueOf(data.getRefetchFlag()),data.getCompareCondition(),String.valueOf(data.getParameterHostType())
				,data.getParameterHostId(),host.getIpaddress(),host.getPortnum()==0?null:String.valueOf(host.getPortnum()), String.valueOf(data.getRefetchMethod()),
				String.valueOf(data.getParamFromMsgSrc()), data.getDirectoryId());		
	}

	@Override
	public GWTSysDynamicPara SaveSysDynamicPara(GWTSysDynamicPara dataInfo, Integer loginLogId) {
		// TODO Auto-generated method stub
		SystemDynamicParameter data = dataDao.Get(new HelperService().GetDistinctOpArray(dataInfo, dataInfo.getParameterName()));
		String opUserId = OperationLogService.getLoginLogById(loginLogId).getUserId();
		if (data != null && data.getId().compareTo(dataInfo.getID()) != 0) {
			throw new RuntimeException("error:已存在该名称的系统参数!");
		}
		data = ModelToBean(dataInfo);
		if (dataInfo.IsNew()){
			data.setCreatedTime(new Date());
			data.setCreatedUserId(opUserId);
			dataDao.Add(data);
			OperationLogService.writeOperationLog(OpType.SystemDynamicParameter, IDUType.Insert, 
					Integer.parseInt(data.getId()), data.getName(),
					"sysDynamicParameterName", null, data.getName(), loginLogId);
		}else{
			SystemDynamicParameter oldData = dataDao.Get(Op.EQ(GWTSysDynamicPara.N_ID, data.getId()));
			data.setLastModifiedTime(new Date());
			data.setLastModifiedUserId(opUserId);
			OperationLogService.writeUpdateOperationLog(OpType.SystemDynamicParameter, SystemDynamicParameter.class, 
					Integer.parseInt(oldData.getId()), oldData.getName(),
					oldData, data, loginLogId);
			dataDao.Edit(data);
		}
		updatePassToCore(data.getSystemId());
		return BeanToModel(data);
	}
	
	

	private static SystemDynamicParameter ModelToBean(GWTSysDynamicPara dataInfo) {
		SystemDynamicParameter data = new SystemDynamicParameter();
		data.setCompareCondition(dataInfo.getCompareCondition());
		data.setDisplayFlag(dataInfo.getDisplayFlag());
		data.setIsValid(dataInfo.getIsValid());
		data.setDesc(dataInfo.getParameterDesc());
		data.setParameterExpression(dataInfo.getParameterExpression());
		data.setParameterHostId(dataInfo.getParameterHostId());
		data.setParameterHostType(Integer.parseInt(dataInfo.getParameterHostType()));
		data.setName(dataInfo.getParameterName());
		data.setParameterType(dataInfo.getParameterType());
		data.setRefetchFlag(Integer.parseInt((dataInfo.getRefetchFlag())));
		data.setSystemId(dataInfo.getSystemID());
		data.setDefaultExpectedValue(dataInfo.getDefaultExpectedValue());
		//data.setRefetchMethod(Integer.parseInt(dataInfo.GetRefetchMethod()));
		data.setParamFromMsgSrc(Integer.parseInt(dataInfo.getParamFromMsgSrc()));
		data.setDirectoryId(dataInfo.GetDirectoryID());
		if(!dataInfo.IsNew())
			data.setId(dataInfo.getID());
		return data;
		
	}

	@Override
	public Boolean DeleteSysDynamicParaItems(List<BaseTreeModel> dataList, Integer loginLogId) {
		// TODO Auto-generated method stub
		try {
			boolean flag = false; //标记是否需updatePassToCore
			for(BaseTreeModel gwtData : dataList) {
				if(gwtData instanceof GWTSysDynamicPara){
					OperationLogService.writeOperationLog(OpType.SystemDynamicParameter, IDUType.Delete, 
							Integer.parseInt(((GWTSysDynamicPara) gwtData).getID()), ((GWTSysDynamicPara) gwtData).getParameterName(),
							"name", ((GWTSysDynamicPara) gwtData).getParameterName(), null, loginLogId);
					dataDao.Del(ModelToBean((GWTSysDynamicPara)gwtData));
					flag = true;
				}else if(gwtData instanceof GWTParameterDirectory){
					ParameterDirectory bean = ModelToBean((GWTParameterDirectory)gwtData, null);
					int count = paramDirDAL.Count(Op.EQ(GWTParameterDirectory.N_ParentDirID, bean.getId()));
					count += dataDao.Count(Op.EQ(GWTSysDynamicPara.N_DirectoryID, bean.getId()));
					if(count != 0){
						return false;
					}else{
						paramDirDAL.Del(bean);
					}
				}			
			}
			if(flag)
				updatePassToCore(((GWTSysDynamicPara)(dataList.get(0))).getSystemID());
			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	

	@Override
	public List<GWTHost> GetHostList(String systemID) {
		// TODO Auto-generated method stub
		List<DbHost> list = hostDao.ListAll(Op.EQ("systemId", systemID));
		List<GWTHost> gwtList = new ArrayList<GWTHost>();
		for(DbHost host : list) {
			gwtList.add(BeanToModel(host));
		}
		return gwtList;
	}

	private static GWTHost BeanToModel(DbHost host) {
		// TODO Auto-generated method stub
		return new GWTHost(host.getHostid(),host.getDbHostName(),host.getIpaddress(),
				String.valueOf(host.getPortnum()),host.getSystemId(),host.getDescription(),host.getIsLongConn(),
				host.getDbType(), host.getDbName(), host.getDbUser(), host.getDbPwd(), host.getOsType());
	}

	@Override
	public List<GWTSysDynamicPara> GetGWTSysDynamicParaList(String systemID) {
		// TODO Auto-generated method stub
		List<SystemDynamicParameter> lst;
		lst = dataDao.ListAll(Op.EQ(GWTSysDynamicPara.N_SystemID, systemID));
		List<GWTSysDynamicPara> returnList = new ArrayList<GWTSysDynamicPara>();
		for (SystemDynamicParameter data : lst)
				returnList.add(BeanToModel(data));
		return returnList;
	}

	
	private void getAll(List<ParameterDirectory> dirLst,int id) {
		
		ParameterDirectory paramDir = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, id));
		if(paramDir != null) {
			dirLst.add(paramDir);
			getAll(dirLst, paramDir.getParentDirId());
		}
	
	}

	@Override
	public List<ModelData> GetGWTTranParamList(String tranID) {
		// TODO Auto-generated method stub
		List<TransactionDynamicParameter> lst = tranParaDao.ListAll(Op.EQ("transactionId", tranID));
		List<GWTSysDynamicPara> sysParamList = new ArrayList<GWTSysDynamicPara>();
		for(TransactionDynamicParameter tran : lst) {
			sysParamList.add(BeanToModel(tran.getSystemParameter()));
		}
		List<ModelData> returnList = new ArrayList<ModelData>();
		
		List<ParameterDirectory> dirLst = new ArrayList<ParameterDirectory>();
		
		for(GWTSysDynamicPara sysParam : sysParamList) {
			ParameterDirectory paramDir = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, 
					sysParam.GetDirectoryID()));	
			dirLst.add(paramDir);
			getAll(dirLst, paramDir.getParentDirId());
		}
		
		Set<GWTParameterDirectory> gwtDirLst = new HashSet<GWTParameterDirectory>();
		for(ParameterDirectory dir : dirLst) {
			gwtDirLst.add(BeanToModel(dir));
		}
		
		returnList.addAll(gwtDirLst);
		returnList.addAll(sysParamList);
		return returnList;
		
	}

	
	private void getAllRecurcively(Set<GWTSysDynamicPara>set, List<GWTParameterDirectory> dirs) {
		
		for(GWTParameterDirectory dir : dirs) {
			List<GWTParameterDirectory> result = new ArrayList<GWTParameterDirectory>();
			//该文件夹下的参数保存起来
			Integer id = dir.GetID();		
			List<SystemDynamicParameter> lst;
			lst = dataDao.ListAll(Op.EQ(GWTSysDynamicPara.N_DirectoryID, id));
			for (SystemDynamicParameter data : lst)
					set.add(BeanToModel(data));
				
			List<ParameterDirectory> cds = paramDirDAL.ListAll(GWTParameterDirectory.N_Name, true,
					Op.EQ(GWTParameterDirectory.N_ParentDirID, id));
			if(!cds.isEmpty()){
				for(ParameterDirectory c : cds){
					result.add(BeanToModel(c));
				}
				getAllRecurcively(set,result);
			}
		}	
		
	}
	
	@Override
	public Boolean SaveTranDynamicPara(List<GWTSysDynamicPara> dataList,
			List<GWTTransaction> selection,String userID, Integer loginLogId) {
		// TODO Auto-generated method stub	
//		Set<GWTSysDynamicPara> set = new HashSet<GWTSysDynamicPara>();
//		
//		//针对选择了文件夹做处理
//		for(ModelData raw : dataList) {
//			if(raw instanceof GWTSysDynamicPara) {
//				
//				set.add((GWTSysDynamicPara)raw);
//			
//			} else {
//				
//				List<GWTParameterDirectory> result = new ArrayList<GWTParameterDirectory>();
//				//文件夹类型
//				GWTParameterDirectory dir = (GWTParameterDirectory)raw;
//				//该文件夹下的参数保存起来
//				Integer id = dir.GetID();		
//				List<SystemDynamicParameter> lst;
//				lst = dataDao.ListAll(Op.EQ(GWTSysDynamicPara.N_DirectoryID, id));
//				for (SystemDynamicParameter data : lst)
//						set.add(BeanToModel(data));
//				
//				
//				List<ParameterDirectory> cds = paramDirDAL.ListAll(GWTParameterDirectory.N_Name, true,
//						Op.EQ(GWTParameterDirectory.N_ParentDirID, id));
//				if(!cds.isEmpty()){
//					for(ParameterDirectory c : cds){
//						result.add(BeanToModel(c));
//					}
//					getAllRecurcively(set,result);
//				}
//			}
//		}
			
		for(GWTTransaction selectTran : selection) {
			List<TransactionDynamicParameter> tranParamLst = tranParaDao.ListAll(Op.EQ("transactionId", selectTran.getTranID()));
		
			for(GWTSysDynamicPara param : dataList) {
				if(!isExistTranParam(tranParamLst,param)) {
					TransactionDynamicParameter tranParam = new TransactionDynamicParameter();
					tranParam.setTransactionId(selectTran.getTranID());
					tranParam.setSystemParameter(ModelToBean(param));
					if(userID.equalsIgnoreCase("Administrator")) {
						tranParam.setUserId("0");		
					}else{
						tranParam.setUserId(userID);
					}			
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//String now = sdf.format(new Date());
					tranParam.setModifyTime(new Date());
					OperationLogService.writeOperationLog(OpType.TransactionParameter, IDUType.Insert, 
							Integer.parseInt(selectTran.getTranID()), selectTran.getTranName(),
							"paramName", selectTran.getTranName(), param.getParameterName(), loginLogId);
					tranParaDao.Add(tranParam);
				}
				
			}
			
			//删除已不在新选项中的记录。
			int count;	
			for(TransactionDynamicParameter tranParam : tranParamLst){
				count = 0;
				for(GWTSysDynamicPara param : dataList) {
					if(tranParam.getSystemParameter().getId().equalsIgnoreCase(param.getID())) {
						//数据库的记录仍在新选择中。
						break;
					}	
					count++;
				}
				if(count == dataList.size()) {
					OperationLogService.writeOperationLog(OpType.TransactionParameter, IDUType.Delete, 
							Integer.parseInt(selectTran.getTranID()), selectTran.getTranName(),
							"paramName", selectTran.getTranName(), tranParam.getSystemParameter().getName(), loginLogId);
					tranParaDao.Del(tranParam);
				}
			}
		}
		updatePassToCore(selection.get(0).getSystemID());

		return true;
	}
	
	private boolean isExistTranParam(List<TransactionDynamicParameter> tranParamLst,GWTSysDynamicPara param) {
		for(TransactionDynamicParameter tranParam : tranParamLst){
			if(param.getID().equalsIgnoreCase(tranParam.getSystemParameter().getId())){
				return true;
			}
		}	
		return false;
	}


	@Override
	public Boolean SaveCaseParaExpectedValue(
			List<GWTCaseParamExpectedValue> dataList, Integer loginLogId) {
		// TODO Auto-generated method stub
		for(GWTCaseParamExpectedValue data : dataList) {
			CaseParameterExpectedValue bean = ModelToBean(data);
			if(data.getExpectedValue() == null && !data.IsNew())
				caseParaValueDao.Del(bean);
			else if(data.IsNew())
				caseParaValueDao.Add(bean);
			else 
				caseParaValueDao.Edit(bean);
		}
		Case casebean = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("caseId", dataList.get(0).getCaseID()));
		OperationLogService.writeOperationLog(OpType.CaseParameterExpectValue, IDUType.Update, Integer.parseInt(casebean.getCaseId()), 
				casebean.getCaseName(), "CaseParaExpectedValue", casebean.getCaseName(), "修改案例的参数预期值", loginLogId);
		return null;
	}

	private CaseParameterExpectedValue ModelToBean(
			GWTCaseParamExpectedValue data) {
		// TODO Auto-generated method stub
		CaseParameterExpectedValue bean = new CaseParameterExpectedValue();
		bean.setExpectedValue(data.getExpectedValue());		
		bean.setTransParameter(tranParaDao.Get(Op.EQ("id", data.getTranParameterID())));
		bean.setCaseId(data.getCaseID());
		bean.setExpectedValueType(data.isExpectedValueVar()? 1:0);
		if(!data.IsNew()) {
			bean.setId(data.getID());
		}		
		return bean;
	}
	
	private void updatePassToCore(String systemId) {
		// TODO Auto-generated method stub
		IDAL<SysType> sysIdal = DALFactory.GetBeanDAL(SysType.class);
		SysType sysType = sysIdal.Get(Op.EQ("systemId", systemId));
		if(sysType.getIsParamModified()==0){
			sysType.setIsParamModified(1);
			sysIdal.Edit(sysType);
		}	
	}
	
	@Override
	public List<ModelData> getSysParamTree(GWTSimuSystem system,
			GWTParameterDirectory gwtParameterDirectory) {
		// TODO Auto-generated method stub
		if(gwtParameterDirectory == null){ //目录为空，返回根目录文件夹
			List<ParameterDirectory> cds = paramDirDAL.ListAll(GWTParameterDirectory.N_Name, true, 
					Op.EQ(GWTParameterDirectory.N_SystemID, system.GetSystemID()),
					Op.EQ(GWTParameterDirectory.N_ParentDirID, 0));
			List<ModelData> directories = new ArrayList<ModelData>();
			for(ParameterDirectory directory: cds){
				directories.add(BeanToModel(directory));
			}
			return directories;			
		}
		
		List<ModelData> result = new ArrayList<ModelData>();
		Integer id = gwtParameterDirectory.GetID();
		List<ParameterDirectory> cds = paramDirDAL.ListAll(GWTParameterDirectory.N_Name, true,
				Op.EQ(GWTParameterDirectory.N_ParentDirID, id));
		if(!cds.isEmpty()){
			for(ParameterDirectory c : cds){
				result.add(BeanToModel(c));
			}
		}
		
		List<SystemDynamicParameter> lst;
		lst = dataDao.ListAll(Op.EQ(GWTSysDynamicPara.N_DirectoryID, id));
		for (SystemDynamicParameter data : lst)
			 result.add(BeanToModel(data));
		
		return result;
	}
	
	@Override
	public List<BaseTreeModel> getSysDynamicParamTree(BaseTreeModel parent, String systemId){
		List<BaseTreeModel> result = new ArrayList<BaseTreeModel>();
		Integer parentDirId = parent == null ? 0 : 
			 ((GWTParameterDirectory)parent).GetID();
		List<ParameterDirectory> pdList = paramDirDAL.ListAll(Op.EQ(GWTParameterDirectory.N_SystemID, systemId), Op.EQ(GWTParameterDirectory.N_ParentDirID, parentDirId));
		for(ParameterDirectory pd : pdList){
			result.add(BeanToModel(pd));
		}
		List<SystemDynamicParameter> sdpList = dataDao.ListAll(Op.EQ(GWTSysDynamicPara.N_SystemID, systemId), Op.EQ(GWTSysDynamicPara.N_DirectoryID, parentDirId));
		for(SystemDynamicParameter sdp : sdpList){
			result.add(BeanToModel(sdp));
		}
		return result;
	}

	private GWTParameterDirectory BeanToModel(ParameterDirectory pd) {
		// TODO Auto-generated method stub
		return new GWTParameterDirectory(pd.getId(),pd.getSystemId(),
				pd.getParentDirId(),pd.getSortIndex(), pd.getName(),
				pd.getPath(), pd.getDescription());
	}
	
	private ParameterDirectory ModelToBean(GWTParameterDirectory gwtParameterDirectory,
			ParameterDirectory parameterDirectory){
		if(gwtParameterDirectory == null){
			return null;
		}
		ParameterDirectory bean = parameterDirectory;
		if(bean == null){
			bean = new ParameterDirectory();
			if(gwtParameterDirectory.GetID()!=null){
				bean.setId(gwtParameterDirectory.GetID());
			}
		}
		bean.setDescription(gwtParameterDirectory.GetDesc());
		bean.setName(gwtParameterDirectory.GetName());
		bean.setParentDirId(gwtParameterDirectory.GetParentDirID()==null?
				0 : gwtParameterDirectory.GetParentDirID());
		bean.setSystemId(gwtParameterDirectory.GetSystemID());
		
		
		return bean;
	}

	@Override
	public GWTParameterDirectory saveOrUpdateParamDirectory(
			GWTParameterDirectory parameterDirectory) {
		ParameterDirectory old = null;
		if(!parameterDirectory.IsNew())
			old = getParameterDirectoryByID(parameterDirectory.GetID());
		ParameterDirectory bean = ModelToBean(parameterDirectory, old);
		if(bean.getParentDirId()==0){ //位于根目录
			bean.setPath("\\"+ bean.getName());
		}else if(parameterDirectory.IsNew() || bean.getParentDirId() != old.getParentDirId()){
			ParameterDirectory parent = getParameterDirectoryByID(bean.getParentDirId());
			bean.setPath(parent.getPath() + "\\" + bean.getName());
		}else{
			bean.setPath(parameterDirectory.GetPath());
		}
		if(parameterDirectory.IsNew()){
			paramDirDAL.Add(bean);
		}else{
			paramDirDAL.Edit(bean);
		}
		return BeanToModel(bean);
	}
	
	private ParameterDirectory getParameterDirectoryByID(Integer id){
		return paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, id));
	}
	

	@Override
	public List<GWTCaseParamExpectedValue> GetGWTCaseParamList(String caseID,
			String tranID) {
		// TODO Auto-generated method stub
		List<TransactionDynamicParameter> lst = tranParaDao.ListAll(Op.EQ("transactionId", tranID));
		List<GWTCaseParamExpectedValue> caseParamLst = new ArrayList<GWTCaseParamExpectedValue>();
		
		for(TransactionDynamicParameter tranParam : lst) {
			SystemDynamicParameter sysParam = tranParam.getSystemParameter();
			
			String expectedValue = sysParam.getDefaultExpectedValue();
			CaseParameterExpectedValue caseParamValue = caseParaValueDao.Get(Op.EQ("caseId", caseID),Op.EQ("transParameter", tranParam));
			String id = "";
			int expectedValueType = 0;
			if(caseParamValue != null) {
				//案例预期值表的记录优先。
				expectedValue = caseParamValue.getExpectedValue();
				id = caseParamValue.getId();
				expectedValueType = caseParamValue.getExpectedValueType();
			}
			
			caseParamLst.add(new GWTCaseParamExpectedValue(id,caseID,
					sysParam.getName(),sysParam.getDesc(),
					sysParam.getParameterType(),tranParam.getId(),expectedValue,expectedValueType,sysParam.getDirectoryId().toString()));	
			
		}
		
		return caseParamLst;
	}

	@Override
	public List<ModelData> GetCaseParamTree(String caseID, String tranID) {
		// TODO Auto-generated method stub
		List<TransactionDynamicParameter> lst = tranParaDao.ListAll(Op.EQ("transactionId", tranID));
		List<ModelData> rootList = new ArrayList<ModelData>();
		if(lst.size() == 0)
			return rootList;
		
		//保存兄弟子节点
		HashMap<String,ArrayList<ModelData>> siblingSet = new HashMap<String,ArrayList<ModelData>>();
		//保存兄弟文件夹
		HashMap<String,ArrayList<ModelData>> siblingFolder = new HashMap<String,ArrayList<ModelData>>();
		
		for(TransactionDynamicParameter tranParam : lst) {
			SystemDynamicParameter sysParam = tranParam.getSystemParameter();
			
			String expectedValue = sysParam.getDefaultExpectedValue();
			CaseParameterExpectedValue caseParamValue = caseParaValueDao.Get(Op.EQ("caseId", caseID),Op.EQ("transParameter", tranParam));
			String id = "";
			int expectedValueType = 0;
			if(caseParamValue != null) {
				//案例预期值表的记录优先。
				expectedValue = caseParamValue.getExpectedValue();
				id = caseParamValue.getId();
				expectedValueType = caseParamValue.getExpectedValueType();
			}
			//构造叶子节点
			String dirID = sysParam.getDirectoryId().toString();
			GWTCaseParamExpectedValue paramExpectedValue = (new GWTCaseParamExpectedValue(id,caseID,
					sysParam.getName(),sysParam.getDesc(),
					sysParam.getParameterType(),tranParam.getId(),expectedValue,expectedValueType,dirID));	
			
			//以父节点ID为KEY，保存兄弟节点
			ArrayList<ModelData> sibling = siblingSet.get(dirID);
			if(sibling == null) {
				sibling = new ArrayList<ModelData>();
			}
			sibling.add(paramExpectedValue);
			siblingSet.put(dirID, sibling);
			
		}
		//在内存里构造 数组型的树形结构
		for(String dirID : siblingSet.keySet()) {
			//循环找其父节点
			ParameterDirectory paramDir = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, Integer.valueOf(dirID)));;			
			BaseTreeModel[] modelArray = new BaseTreeModel[]{};
			//把同个父节点的叶子节点合并起来
			GWTParameterDirectory gwtDir = new GWTParameterDirectory(paramDir.getName(),paramDir.getId().toString(),
					paramDir.getParentDirId().toString(),siblingSet.get(dirID).toArray(modelArray));
		
			while(paramDir != null) {	
				//以父节点ID为KEY，保存兄弟节点
				ArrayList<ModelData> sibling = siblingFolder.get(paramDir.getParentDirId().toString());
				if(sibling == null) {
					sibling = new ArrayList<ModelData>();
				}
				//ID号相同的不用加入，主要是防止根目录重复加入情况,
				int i = 0;
				for(; i<sibling.size(); i++) {
					GWTParameterDirectory dir = (GWTParameterDirectory)sibling.get(i);
					if(dir.getNO().equals(gwtDir.getNO())) {
						for(int j=0; j<gwtDir.getChildCount(); j++)
							dir.add(gwtDir.getChild(j));
						break;
					}
						
				}
				if(i == sibling.size())
					sibling.add(gwtDir);
				
				siblingFolder.put(paramDir.getParentDirId().toString(), sibling);
								
				paramDir = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, paramDir.getParentDirId()));
				if(paramDir != null) 
					gwtDir = new GWTParameterDirectory(paramDir.getName(),paramDir.getId().toString(),
							paramDir.getParentDirId().toString(),new BaseTreeModel[]{});
			}
			
		}
		
		//寻找siblingFolder里有父子关系的对象
		for(String dirID : siblingFolder.keySet()) {
			ArrayList<ModelData> fathers = siblingFolder.get(dirID);
			for(ModelData father : fathers) {
				ArrayList<ModelData> children = siblingFolder.get(((GWTParameterDirectory)father).getNO());
				if(children != null)
					for(ModelData child : children)
						((GWTParameterDirectory)father).add(child);
			}
					
		}
		rootList = siblingFolder.get("0");
			
		return rootList;
	}

	@Override
	public List<GWTParameterDirectory> GetParamDirTree(
			GWTParameterDirectory parent, String systemId) {
		// TODO Auto-generated method stub
		List<GWTParameterDirectory> result = new ArrayList<GWTParameterDirectory>();
		Integer parentDirId = parent == null ? 0 : 
			 parent.GetID();
		List<ParameterDirectory> pdList = paramDirDAL.ListAll(Op.EQ(GWTParameterDirectory.N_SystemID, systemId), Op.EQ(GWTParameterDirectory.N_ParentDirID, parentDirId));
		for(ParameterDirectory pd : pdList){
			result.add(BeanToModel(pd));
		}		
		return result;
	}

	@Override
	public BaseTreeModel GetSearchResult(String systemID, String searchKey) {
		// TODO Auto-generated method stub

		GWTParameterDirectory root = new GWTParameterDirectory(); //用来存放整棵树
		Map<String, GWTParameterDirectory> directoryMap =  //树节点与叶子节点
				new HashMap<String, GWTParameterDirectory>();
		List<SystemDynamicParameter> list = dataDao.ListAll(Op.EQ("systemId", systemID),
				Op.LIKE("name", searchKey));
		
		for(SystemDynamicParameter cf : list){
			GWTSysDynamicPara sysParam = BeanToModel(cf);
			if(cf.getDirectoryId() == 0){
				root.add(sysParam);
				continue;
			}
			ParameterDirectory cd = paramDirDAL.Get(Op.EQ("id", cf.getDirectoryId()));
			GWTParameterDirectory model = BeanToModel(cd);
			if(directoryMap.get(model.GetID().toString()) == null){
				directoryMap.put(model.GetID().toString(), model);
			}
			directoryMap.get(model.GetID().toString()).add(sysParam);
		}
		directoryMap = findTreeNode(directoryMap); //找所有已有节点的父亲节点
		for(GWTParameterDirectory cd : directoryMap.values()){
			if(cd.GetParentDirID() == 0){
				root.add(cd);
			}
		}
		return root;	
	}
	

	private Map<String, GWTParameterDirectory> findTreeNode(
			Map<String, GWTParameterDirectory> directoryMap) {
		// TODO Auto-generated method stub
		Map<String, GWTParameterDirectory> resultMap = new HashMap<String, GWTParameterDirectory>();
		for(GWTParameterDirectory node : directoryMap.values()){
			recurFindFatherNode(node, resultMap);		
		}
		return resultMap;
	}

	private GWTParameterDirectory getParentParamDirectory(GWTParameterDirectory node) {
		// TODO Auto-generated method stub
		ParameterDirectory paramDirectory = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, 
				Integer.parseInt(node.get(GWTParameterDirectory.N_ParentDirID).toString())));
		return BeanToModel(paramDirectory);
	}
	
	private void recurFindFatherNode(GWTParameterDirectory node,
			Map<String, GWTParameterDirectory> resultMap) {
		// TODO Auto-generated method stub

		if(resultMap.get(node.GetID().toString())==null){  
			resultMap.put(node.GetID().toString(), node);  
			if(node.GetParentDirID() == 0){   
 				return;   
			}
			GWTParameterDirectory fatherNode = getParentParamDirectory(node);		
			recurFindFatherNode(fatherNode, resultMap); 
			resultMap.get(fatherNode.GetID().toString()).add(node); 
		
		} else {  
			return; 
		}
		
	}

}
