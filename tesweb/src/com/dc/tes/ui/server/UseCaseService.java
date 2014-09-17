package com.dc.tes.ui.server;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.CaseDirectory;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UseCaseService extends RemoteServiceServlet implements
		com.dc.tes.ui.client.IUseCaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5357763613130890994L;
	IDAL<CaseFlow> caseFlowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
	IDAL<CaseDirectory> caseDirectoryDAL = DALFactory.GetBeanDAL(CaseDirectory.class);
	
	private static final Log log = LogFactory.getLog(UseCaseService.class);
	
	
	private CaseDirectory ModelToBean(CaseDirectory caseDirectory, GWTCaseDirectory gwtCaseDirectory){
		if(gwtCaseDirectory == null){
			return null;
		}
		CaseDirectory cd = caseDirectory;
		if(cd == null){			
			if(gwtCaseDirectory.GetID()!=null){
				cd = caseDirectoryDAL.Get(Op.EQ(GWTCaseDirectory.N_ID, Integer.parseInt(gwtCaseDirectory.GetID())));
			}else
				cd = new CaseDirectory();
		}
		cd.setDescription(gwtCaseDirectory.GetDesc());
		cd.setName(gwtCaseDirectory.GetName());
		if(gwtCaseDirectory.GetParentDirID()!=null && !gwtCaseDirectory.GetParentDirID().isEmpty() 
				&& !gwtCaseDirectory.GetParentDirID().equals("0")){
			cd.setParentDirId(Integer.parseInt(gwtCaseDirectory.GetParentDirID()));
			CaseDirectory parent = caseDirectoryDAL.Get(Op.EQ("id", Integer.parseInt(gwtCaseDirectory.GetParentDirID())));
			String path = parent.getPath() + (parent.getPath().endsWith("\\")?cd.getName():("\\"+cd.getName()));
			cd.setPath(path);
		}else{
			if(gwtCaseDirectory.GetParentDirID()!=null)
				cd.setParentDirId(Integer.parseInt(gwtCaseDirectory.GetParentDirID()));
			cd.setPath("\\" + cd.getName());
		}
		if(gwtCaseDirectory.GetSortIndex()!=null)
			cd.setSortIndex(Integer.parseInt(gwtCaseDirectory.GetSortIndex()));
		cd.setSystemId(Integer.parseInt(gwtCaseDirectory.GetSystemID()));
		return cd;
	}
	
	private static CaseFlow ModelToBean(CaseFlow caseFlow, GWTCaseFlow gwtCaseFlow){
		if(gwtCaseFlow == null){
			return null;
		}
		CaseFlow cf = caseFlow;
		if(cf == null){
			cf = new CaseFlow();
		}
		cf.setBreakPointFlag(gwtCaseFlow.GetBreakPointFlag()==null? null:Integer.parseInt(gwtCaseFlow.GetBreakPointFlag()));
		cf.setCaseFlowName(gwtCaseFlow.GetName());
		cf.setCaseFlowNo(gwtCaseFlow.GetCaseFlowNo());
		cf.setCaseFlowPath(gwtCaseFlow.GetCaseFlowPath());
		cf.setCaseFlowStep(gwtCaseFlow.GetCaseFlowStep());
		cf.setCaseProperty(gwtCaseFlow.GetCaseProperty());
		cf.setCaseType(gwtCaseFlow.GetCaseType());
		cf.setDescription(gwtCaseFlow.GetDesc());
		cf.setDirectoryId(Integer.parseInt(gwtCaseFlow.GetDirectoryID()));
		cf.setExpectedResult(gwtCaseFlow.GetExpectedResult());
		cf.setPreConditions(gwtCaseFlow.GetPreConditions());
		cf.setPriority(gwtCaseFlow.GetPriority());
		cf.setDesigner(gwtCaseFlow.GetDesigner());
		cf.setPassFlag(gwtCaseFlow.GetPassFlag());
		cf.setDisabledFlag(gwtCaseFlow.GetDisabledFlag());
		cf.setDesignTime(gwtCaseFlow.GetDesignTime());
		cf.setMemo(gwtCaseFlow.GetMemo());
		return cf;
	}
	public static GWTCaseFlow BeanToModel(CaseFlow caseFlow) {
		// TODO Auto-generated method stub
		if(caseFlow == null){
			return null;
		}
		IDAL<User> userIdal = DALFactory.GetBeanDAL(User.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		String createTime = sdf.format(caseFlow.getCreatedTime());
		String userName = userIdal.Get(Op.EQ(GWTUser.N_id, caseFlow.getCreatedUserId())).getName();
		GWTCaseFlow gwtCaseFlow = new GWTCaseFlow(caseFlow.getId().toString(),
				caseFlow.getCaseFlowName(), caseFlow.getCaseFlowNo(),
				caseFlow.getDescription(), caseFlow.getSystemId().toString(),
				caseFlow.getCreatedUserId().toString(), userName, createTime);
		gwtCaseFlow.SetExtraValue(caseFlow.getBreakPointFlag()==null?null:caseFlow.getBreakPointFlag().toString(), 
				caseFlow.getCaseFlowPath(), caseFlow.getDirectoryId().toString(),caseFlow.getDesigner(), 
				caseFlow.getCaseFlowStep(), caseFlow.getPreConditions(), caseFlow.getExpectedResult(),
				caseFlow.getCaseType(), caseFlow.getCaseProperty(), caseFlow.getPriority(), caseFlow.getDesignTime(),
				caseFlow.getMemo());

//		IDAL<ScriptFlow> scriptflowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
//		ScriptFlow scriptFlow = scriptflowDAL.Get(Op.EQ(GWTScriptFlow.N_ID, caseFlow.getScriptFlowId()));
//		GWTScriptFlow gwtScriptFlow = ScriptFlowService.BeanToModel(scriptFlow, false);
//		gwtCaseFlow.setScriptFlow(gwtScriptFlow);
		gwtCaseFlow.SetDisabledFlag(caseFlow.getDisabledFlag());
		gwtCaseFlow.SetPassFlag(caseFlow.getPassFlag());
		return gwtCaseFlow;
	}
	
	private GWTCaseDirectory BeanToModel(CaseDirectory cd){
		return new GWTCaseDirectory(cd.getId().toString(), 
				cd.getSystemId().toString(),
				cd.getParentDirId().toString(),
				cd.getSortIndex().toString(),
				cd.getName(),
				cd.getPath(), 
				cd.getDescription());
	}
	
	public List<CaseFlow> getCaseFlowList(CaseDirectory cd) {
		if(cd == null){
			return null;
		}
		List<CaseFlow> caseFlows = caseFlowDAL.ListAll(GWTCaseFlow.N_ID, true, Op.EQ(GWTCaseFlow.N_DirectoryID, cd.getId()));
		return caseFlows;
	}
	

	@Override
	public List<ModelData> getUseCaseTree(GWTSimuSystem system, GWTCaseDirectory gwtCaseDirectory) {
		// TODO Auto-generated method stub
		if(gwtCaseDirectory == null){ //目录为空，返回根目录文件夹
			List<CaseDirectory> cds = caseDirectoryDAL.ListAll(GWTCaseDirectory.N_Name, true, 
					Op.EQ(GWTCaseDirectory.N_SystemID, Integer.parseInt(system.GetSystemID())),
					Op.EQ(GWTCaseDirectory.N_ParentDirID, 0));
			List<ModelData> directories = new ArrayList<ModelData>();
			for(CaseDirectory directory: cds){
				directories.add(BeanToModel(directory));
			}
			return directories;			
		}
		List<ModelData> result = new ArrayList<ModelData>();
		Integer id = Integer.parseInt(gwtCaseDirectory.GetID());
		List<CaseDirectory> cds = caseDirectoryDAL.ListAll(GWTCaseDirectory.N_Name, true,
				Op.EQ(GWTCaseDirectory.N_ParentDirID, id));
		if(!cds.isEmpty()){
			for(CaseDirectory c : cds){
				result.add(BeanToModel(c));
			}
		}
		List<CaseFlow> caseFlows = caseFlowDAL.ListAll(GWTCaseFlow.N_CaseFlowNo, true, Op.EQ(GWTCaseFlow.N_DirectoryID, id));
		if(caseFlows!=null){
			for(CaseFlow caseFlow : caseFlows){
				result.add((ModelData)BeanToModel(caseFlow));
			}
		}
		
		return result;
	}
	@Override
	public GWTCaseDirectory saveOrUpdateDirectory(GWTCaseDirectory caseDirectory) {
		// TODO Auto-generated method stub
		if(caseDirectory==null){
			return null;
		}
		if(caseDirectory.GetID()!=null){//update
			CaseDirectory oldDir = caseDirectoryDAL.Get(Op.EQ(GWTCaseDirectory.N_ID, Integer.parseInt(caseDirectory.GetID())));
			Integer oldParentDirID = oldDir.getParentDirId(); //保存原来父目录的ID值
			String oldName = oldDir.getName(); //保存原目录名称
			final CaseDirectory cd = ModelToBean(oldDir, caseDirectory);
			caseDirectoryDAL.Edit(cd);
			if(!cd.getName().equals(oldName) || 
					cd.getParentDirId() != oldParentDirID){//当目录信息发生改变时,
				try{                 //新建一线程进行目录下的目录路径与案例路径更新
					new Thread(new Runnable() {
						                  				
						@Override
						public void run() {
							// TODO Auto-generated method stub
							List<CaseFlow> caseFlows = getCaseFlowList(cd);
							for(CaseFlow caseFlow : caseFlows){
								caseFlow.setCaseFlowPath(cd.getPath());
								caseFlowDAL.Edit(caseFlow);
							}
							updatePath(cd);
						}
						
						private List<CaseDirectory> getSubDirectories(CaseDirectory dir){
							return caseDirectoryDAL.ListAll(Op.EQ("parentDirId", dir.getId()));
						}
						
						private void updatePath(CaseDirectory dir){
							List<CaseDirectory> dirs = getSubDirectories(dir);
							for(CaseDirectory d : dirs){
								d.setPath(dir.getPath()+ "\\" +d.getName());
								caseDirectoryDAL.Edit(d);
								List<CaseFlow> caseFlows = getCaseFlowList(d);
								for(CaseFlow caseFlow : caseFlows){
									caseFlow.setCaseFlowPath(d.getPath());
									caseFlowDAL.Edit(caseFlow);
								}
								updatePath(d);
							}
							
						}
					}).start();
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			return caseDirectory;
		}else{//save
			CaseDirectory cd = ModelToBean(null, caseDirectory);
			if(cd.getParentDirId()==null){
				cd.setParentDirId(0);
			}
			Integer count = caseDirectoryDAL.Count(
					Op.EQ(GWTCaseDirectory.N_ParentDirID, cd.getParentDirId()));
			cd.setSortIndex(count+1);
			caseDirectoryDAL.Add(cd);
			return BeanToModel(cd);
		}
	}
	@Override
	public boolean deleteSelectedItem(List<ModelData> items, Integer loginLogId) {
		// TODO Auto-generated method stub
		try{
			for(ModelData item : items){
				if(item instanceof GWTCaseFlow){
					
					CaseFlow caseFlow = caseFlowDAL.Get(Op.EQ("id", Integer.parseInt(((GWTCaseFlow) item).GetID())));
//					IDAL<ScriptFlow> scriptFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
//					ScriptFlow scriptFlow = scriptFlowDAL.Get(Op.EQ(GWTScriptFlow.N_ID, caseFlow.getScriptFlowId()));
//					if(scriptFlow!=null){
//						scriptFlowDAL.Del(scriptFlow); 
//					}
					OperationLogService.writeOperationLog(OpType.CaseFlow, IDUType.Delete, 
							caseFlow.getId(), caseFlow.getCaseFlowName(),
							"caseFlowName", caseFlow.getCaseFlowName(), null, loginLogId);
					caseFlowDAL.Del(caseFlow);
				}
				if(item instanceof GWTCaseDirectory){
					CaseDirectory caseDirectory = ModelToBean(null, (GWTCaseDirectory)item);
					int count = caseDirectoryDAL.Count(Op.EQ(GWTCaseDirectory.N_ParentDirID, caseDirectory.getId()));
					count += caseFlowDAL.Count(Op.EQ(GWTCaseFlow.N_DirectoryID, caseDirectory.getId()));
					if(count != 0){
						return false;
					}else{
						caseDirectoryDAL.Del(caseDirectory);
					}
				}
			}
			return true;
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTCaseFlow saveOrUpdateCaseFlow(GWTCaseFlow gwtCaseFlow, Integer loginLogId) {
		// TODO Auto-generated method stub
		String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
		IDAL<ScriptFlow> scriptFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
		if(gwtCaseFlow.GetID()==null){
			CaseFlow caseFlow = ModelToBean(null, gwtCaseFlow);
			caseFlow.setStepCount(0);
			caseFlow.setSystemId(Integer.parseInt(gwtCaseFlow.GetSystemID()));
			caseFlow.setCreatedTime(new Date());
			caseFlow.setCreatedUserId(userId);
		
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//			String importBatchNo = userName+ sdf.format(caseFlow.getCreateTime());
//			caseFlow.setImportBatchNo(importBatchNo);
//			
//			ScriptFlow scriptFlow = new ScriptFlow();
//			String busiFlowName = importBatchNo+ "_" +caseFlow.getCaseFlowNo();
//			scriptFlow.setName(busiFlowName);
//			scriptFlow.setDescription(caseFlow.getCaseFlowName());
//			scriptFlow.setSystemid(gwtCaseFlow.GetSystemID());
//			String srcipt ="run_caseFlow(\"" + importBatchNo;
//			srcipt +=  "\", \"" + caseFlow.getCaseFlowNo() + "\");";
//			scriptFlow.setSrcipt(srcipt);
//			scriptFlowDAL.Add(scriptFlow);
//			
//			caseFlow.setScriptFlowId(scriptFlow.getId());
			caseFlowDAL.Add(caseFlow);
			OperationLogService.writeOperationLog(OpType.CaseFlow, IDUType.Insert, 
					caseFlow.getId(), caseFlow.getCaseFlowName(), 
					"caseFlowName", null, caseFlow.getCaseFlowName(), loginLogId);
			return BeanToModel(caseFlow);
		}else{
			CaseFlow caseFlow = caseFlowDAL.Get(Op.EQ("id", Integer.parseInt(gwtCaseFlow.GetID())));
			caseFlow = ModelToBean(caseFlow, gwtCaseFlow);
			caseFlow.setLastModifiedTime(new Date());
			caseFlow.setLastModifiedUserId(userId);
			CaseFlow oldCaseFlow = caseFlowDAL.Get(Op.EQ("id", Integer.parseInt(gwtCaseFlow.GetID())));
			OperationLogService.writeUpdateOperationLog(OpType.CaseFlow, CaseFlow.class, 
					oldCaseFlow.getId(), oldCaseFlow.getCaseFlowName(), 
					oldCaseFlow, caseFlow, loginLogId);
			caseFlowDAL.Edit(caseFlow);
//			ScriptFlow scriptFlow = scriptFlowDAL.Get(Op.EQ("id", caseFlow.getScriptFlowId()));
//			if(scriptFlow!=null){
//				String busiFlowName = caseFlow.getImportBatchNo()+ "_" +caseFlow.getCaseFlowNo();
//				scriptFlow.setName(busiFlowName);
//				scriptFlow.setDescription(caseFlow.getCaseFlowName());
//				scriptFlow.setSystemid(gwtCaseFlow.GetSystemID());
//				String srcipt ="run_caseFlow(\"" + caseFlow.getImportBatchNo();
//				srcipt +=  "\", \"" + caseFlow.getCaseFlowNo() + "\");";
//				scriptFlow.setSrcipt(srcipt);
//				scriptFlowDAL.Edit(scriptFlow);
//			}
			return BeanToModel(caseFlow);
		}
	}

	@Override
	public List<ModelData> getAllChildDatas(GWTSimuSystem system,
			List<ModelData> Nodes) {
		// TODO Auto-generated method stub
		List<ModelData> result = new ArrayList<ModelData>();		
		for(ModelData node : Nodes){
			if(node instanceof GWTCaseFlow){
				result.add(node);
			}else{
				result.addAll(getAllChildDatas(system, getUseCaseTree(system, (GWTCaseDirectory)node)));
			}
		}
		return result;
	}

	@Override
	public PagingLoadResult<GWTCaseFlow> getAllUseCases(GWTSimuSystem system, String[] searchKey, Date[] dates, List<GWTCaseDirectory> directories, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		List<GWTCaseFlow> result = new ArrayList<GWTCaseFlow>();
		int count;
		List<CaseFlow> lst;
		List<Op> ops =new ArrayList<Op>();  //查询条件集合
		ops.add(Op.EQ(GWTCaseFlow.N_SystemID, Integer.parseInt(system.GetSystemID())));
		if(searchKey[0]!=null && !(searchKey[0]).trim().isEmpty()){ //用例编号不为空
			ops.add(Op.LIKE(GWTCaseFlow.N_CaseFlowNo, searchKey[0].trim()));
		}
		if(searchKey[1]!=null && !(searchKey[1]).trim().isEmpty()){//用例名字不为空
			ops.add(Op.LIKE("caseFlowName", searchKey[1].trim()));
		}
		if(searchKey[2]!=null && !(searchKey[2]).trim().isEmpty()){//设计人不为空
			ops.add(Op.LIKE(GWTCaseFlow.N_Designer, searchKey[2].trim()));
		}
		if(!searchKey[3].trim().trim().isEmpty()){ //优先级不为空
			ops.add(Op.LIKE(GWTCaseFlow.N_Priority, searchKey[3].trim()));
		}
		if(!searchKey[4].trim().isEmpty()){ //是否实例化， 当为1时为查询已实例化，为0时查询未实例化
			if(searchKey[4].equals("0")){
				ops.add(Op.EQ("stepCount", 0));
			}
			if(searchKey[4].equals("1")){
				ops.add(Op.GT("stepCount", 0));
			}
		}
		if(!searchKey[5].trim().isEmpty()){  //是否通过，当为1时用例已通过，为0时用例未通过
			ops.add(Op.EQ(GWTCaseFlow.N_PassFlag, searchKey[5].equals("1")?1:0));
		}
		if(!searchKey[6].trim().isEmpty()){  //是否有效，当为1时用例有效，为0时用例无效
			ops.add(Op.EQ(GWTCaseFlow.N_DisabledFlag, searchKey[6].equals("1")?1:0));
		}
		if((dates[0])!=null){//起始日期不为空
			ops.add(Op.GE(GWTCaseFlow.N_CreateTime, dates[0]));
		}
		if((dates[1])!=null){//结束日期不为空
			ops.add(Op.LE(GWTCaseFlow.N_CreateTime, dates[1]));
		}
		if(directories != null && directories.size()!=0){ //查询路径不为空
			List<CaseDirectory> caseDirectories = new ArrayList<CaseDirectory>();
			for(GWTCaseDirectory md : directories){
				caseDirectories.add(ModelToBean(null, md));				
			}
			caseDirectories = getAllDirectory(caseDirectories);//获得所给路径下的所有子目录
			List<Integer> ints = new ArrayList<Integer>(); //构造子目录的ID集合，方便做IN查询
			for(CaseDirectory c : caseDirectories){
				ints.add(c.getId());
			}
			ops.add(Op.IN("directoryId", ints));
		}
		Op[] conditions = new Op[ops.size()];
		for(int i=0; i<ops.size(); i++){    //把集合类转成数组
			conditions[i] = ops.get(i);
		}
		count = caseFlowDAL.Count(conditions);
		PageStartEnd pse = new PageStartEnd(config, count);
		lst = caseFlowDAL.List(GWTCaseFlow.N_CaseFlowNo, true, pse.getStart(), pse.getEnd(), conditions);
		
		for(CaseFlow caseFlow : lst){
			result.add(BeanToModel(caseFlow));
		}
			
		return new BasePagingLoadResult<GWTCaseFlow>(result, config.getOffset(), count);
	}
	/**
	 * 获得所给路径下的所有子目录
	 * @param dirs
	 * @return
	 */
	static List<CaseDirectory> getAllDirectory(List<CaseDirectory> dirs){
		IDAL<CaseDirectory> caseDirectoryDAL = DALFactory.GetBeanDAL(CaseDirectory.class);
		List<CaseDirectory> caseDirectories = new ArrayList<CaseDirectory>();
		for(CaseDirectory c : dirs){
			if(!caseDirectories.contains(c)){
				caseDirectories.add(c);
				List<CaseDirectory> lst = caseDirectoryDAL.ListAll(Op.EQ(GWTCaseDirectory.N_ParentDirID, c.getId()));
				if(lst != null && lst.size() != 0)
					caseDirectories.addAll(getAllDirectory(lst));
			}
		}
		return caseDirectories;
	}

	@Override
	public List<GWTCaseDirectory> getCaseDirectoryTree(GWTSimuSystem system,
			GWTCaseDirectory gwtCaseDirectory) {
		// TODO Auto-generated method stub
		if(gwtCaseDirectory == null){ //目录为空，返回根目录文件夹
			List<CaseDirectory> cds = caseDirectoryDAL.ListAll(GWTCaseDirectory.N_Name, true, 
					Op.EQ(GWTCaseDirectory.N_SystemID, Integer.parseInt(system.GetSystemID())),
					Op.EQ(GWTCaseDirectory.N_ParentDirID, 0));
			List<GWTCaseDirectory> directories = new ArrayList<GWTCaseDirectory>();
			for(CaseDirectory directory: cds){
				directories.add(BeanToModel(directory));
			}
			return directories;			
		}
		List<GWTCaseDirectory> result = new ArrayList<GWTCaseDirectory>();
		Integer id = Integer.parseInt(gwtCaseDirectory.GetID());
		List<CaseDirectory> cds = caseDirectoryDAL.ListAll(GWTCaseDirectory.N_Name, true,
				Op.EQ(GWTCaseDirectory.N_ParentDirID, id));
		if(!cds.isEmpty()){
			for(CaseDirectory c : cds){
				result.add(BeanToModel(c));
			}
		}
		
		return result;
	}

	@Override
	public GWTCase getFirstCase(GWTCaseFlow caseFlow) {
		// TODO Auto-generated method stub
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		List<Case> cases = caseDAL.ListAll(GWTCase.N_Sequence, true, Op.EQ("caseFlow.id", Integer.parseInt(caseFlow.GetID())));
		if(cases.size()>0){
			Case casebean = cases.get(0);
			return CaseService.BeanToModel(casebean);
		}else
			return null;
	}

	@Override
	public BaseTreeModel getSearchResult(String systemId,
			String searchKey) {
		// TODO Auto-generated method stub
		GWTCaseDirectory root = new GWTCaseDirectory(); //用来存放整棵树
		Map<String, GWTCaseDirectory> directoryMap =  //树节点与叶子节点
				new HashMap<String, GWTCaseDirectory>();
		List<CaseFlow> list = caseFlowDAL.ListAll(Op.EQ("systemId", Integer.parseInt(systemId)),
				Op.LIKE("caseFlowName", searchKey));
		for(CaseFlow cf : list){
			GWTCaseFlow caseFlow = BeanToModel(cf);
			if(cf.getDirectoryId() == 0){
				root.add(caseFlow);
				continue;
			}
			CaseDirectory cd = caseDirectoryDAL.Get(Op.EQ("id", cf.getDirectoryId()));
			GWTCaseDirectory model = BeanToModel(cd);
			if(directoryMap.get(model.GetID()) == null){
				directoryMap.put(model.GetID(), model);
			}
			directoryMap.get(model.GetID()).add(caseFlow);
		}
		directoryMap = findTreeNode(directoryMap); //找所有已有节点的父亲节点
		for(GWTCaseDirectory cd : directoryMap.values()){
			if(cd.GetParentDirID().equals("0")){
				root.add(cd);
			}
		}
		return root;
	}

	private Map<String, GWTCaseDirectory> findTreeNode(
			Map<String, GWTCaseDirectory> directoryMap) {
		// TODO Auto-generated method stub
		Map<String, GWTCaseDirectory> resultMap = new HashMap<String, GWTCaseDirectory>();
		for(GWTCaseDirectory node : directoryMap.values()){
			recurFindFatherNode(node, resultMap);		
		}
		return resultMap;
	}

	private GWTCaseDirectory getParentCaseDirectory(GWTCaseDirectory node) {
		// TODO Auto-generated method stub
		CaseDirectory caseDirectory = caseDirectoryDAL.Get(Op.EQ(GWTCaseDirectory.N_ID, 
				Integer.parseInt(node.get(GWTCaseDirectory.N_ParentDirID).toString())));
		return BeanToModel(caseDirectory);
	}

	private void recurFindFatherNode(GWTCaseDirectory node,
			Map<String, GWTCaseDirectory> resultMap) {
		// TODO Auto-generated method stub
		 //是不是新来的啊？
		if(resultMap.get(node.GetID())==null){  //是
			resultMap.put(node.GetID(), node);  //行，入队
			if(node.GetParentDirID().equals("0")){   //你爸是不是BOSS
 				return;   //那没你什么事了
			}
			GWTCaseDirectory fatherNode = getParentCaseDirectory(node);	 //把你爸叫出来		
			recurFindFatherNode(fatherNode, resultMap); //不验你验你爸了。
			resultMap.get(fatherNode.GetID()).add(node);  //验完了，好， 你是你爸儿子。
		}else{  //不是新来的
			return; //那没你什么事了
		}
		
	}

}
