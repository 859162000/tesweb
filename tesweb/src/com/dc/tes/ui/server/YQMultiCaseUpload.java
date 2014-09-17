package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.CaseDirectory;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;


public class YQMultiCaseUpload extends HttpServlet{

	private static final long serialVersionUID = 5604902452502910982L;
	private static final Log log = LogFactory.getLog(MultiCaseUpload.class);
	private Map<String, CaseFlow> caseFlowMap = new HashMap<String, CaseFlow>();
	IDAL<CaseDirectory> caseDirDAL = DALFactory.GetBeanDAL(CaseDirectory.class); 
	IDAL<CaseFlow> caseFlowDAL = DALFactory.GetBeanDAL(CaseFlow.class);

	
	public YQMultiCaseUpload() {
		super();
	}
	
	public void destroy() {
		super.destroy();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		response.setCharacterEncoding("utf8");
		List<String> resultList = new ArrayList<String>();
		ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				if (item.isFormField()) {
					continue;
				} else {
					int isClientSimu = Integer.parseInt(request.getParameter("isClientSimu"));
					boolean isAdmin = Boolean.parseBoolean(request.getParameter("isAdmin"));
					// 获得报文样式
					String systemId = request.getParameter("systemId");
					IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
					SysType sys = sysDAL.Get(Op.EQ(GWTSimuSystem.N_SystemID,
							systemId));
					if(sys == null) {
						throw new DownLoadException("无法获得当前模拟系统信息，无法上传");
					}
					resultList = UploadMultiCase(sys.getSystemId(), isClientSimu,
								item, request, response, isAdmin);						
					break;
				}
			}
		} catch (DownLoadException ex) {
			ex.printStackTrace();
			response.getOutputStream()
					.write(ex.getMessage().getBytes("utf8"));
			response.flushBuffer();
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					"error:发生异常，请与管理员联系。".getBytes("utf8"));
			response.flushBuffer();
			return;
		}
		
		String msg = resultList.toString();
		response.getOutputStream().write(msg.getBytes("utf8"));
		response.flushBuffer();
	}

	
	private List<String> UploadMultiCase(String systemId, int isClientSimu,
			FileItemStream item, HttpServletRequest request,
			HttpServletResponse response, boolean isAdmin) throws Exception {

		List<String> resultList = new ArrayList<String>();
		Integer loginLogId = Integer.parseInt(request.getParameter("loginLogId"));
		String userId;
		if(request.getParameter("userId").equals("Administrator")){
			userId="0";
		}else
			userId = request.getParameter("userId");
//		
//		String importBatchNo = getBatchNo(systemId, userId, desc); //生成批次号		
//		
		InputStream stream = item.openStream();
		Workbook workbook = Workbook.getWorkbook(stream);
		String sheetName = "";
		boolean found = false;
		try{
			//读取Excel sheet的页面
			for (Sheet sheet : workbook.getSheets()) {
				sheetName = sheet.getName().toLowerCase();				
				if(sheetName.contains("主机")){
					SystemDynamicParaUpload.DeserializeDbHost(systemId, sheet, resultList);
				}
			}
			
			//导入交易信息
			TranStructServletUpload.UploadMultiTran(workbook, request, response, false);
			
			for (Sheet sheet : workbook.getSheets()) {
				sheetName = sheet.getName().toLowerCase();				
				if(sheetName.contains(("参数"))) {
					SystemDynamicParaUpload.DeserializeSysParam(systemId, userId, loginLogId, sheet, resultList);				
				} 
			}
			
			for (Sheet sheet : workbook.getSheets()) {
				sheetName = sheet.getName().toLowerCase();				
				if (sheetName.contains(("测试用例"))) {
					found = true;
					DeserializeCaseFlow(systemId, userId, loginLogId, sheet, resultList);				
				} 
			}
			for (Sheet sheet : workbook.getSheets()) {
				sheetName = sheet.getName().toLowerCase();				
				
				if (sheetName.contains(("用例步骤"))) {
					deserializeCases(systemId, userId, sheet, resultList);				
				} 
			}
		}
		catch(Exception e) {
			log.error(e);
			throw new DownLoadException("遇到未处理的问题，请与管理员联系;\n" + e.getMessage());
			// resultList.add("error:" + sheet.getName() + ":[遇到遇到情况]");
		}	
		if(found == false)
			resultList.add("error:" + "没有找到含有 测试用例 字样的表格");
		return resultList;	
	}

	
	private void DeserializeCaseFlow(String systemId, String userId, Integer loginLogId,
			Sheet sheet, List<String> resultList) {
		// TODO Auto-generated method stub
		int rows = sheet.getRows();
		int succ = 0;
		int update = 0;
		int wrong = 0;
		long time = System.currentTimeMillis();
		CaseFlow caseFlow = null; 
		IDAL<ScriptFlow> scriptFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
		
		//该项目模板从第4行开始有数据
		for(int row=3; row < rows; row++) {	
			boolean fl= false;//标记案例是否有效
			
			for(int i = 0; i < 3; i++){//前四列如有空值，则为无效案例，跳过
				if(sheet.getCell(i, row).getContents().equals("")){
					fl = true;
					break;
				}
			}
			if(fl){
				wrong++;
				continue;
			}	

			//路径
			String path = sheet.getCell(0, row).getContents(); 
			//用例编号
			String caseFlowNo = sheet.getCell(1, row).getContents();
			//用例名称
			String caseFlowName = sheet.getCell(2,row).getContents();
			//用例描述
			String caseFlowDes = sheet.getCell(3,row).getContents();
			//用例前置条件
			String caseFlowPreCon = sheet.getCell(4,row).getContents();
			//步骤描述
			String caseFlowStep = sheet.getCell(5,row).getContents();
			//预期结果
			String caseFlowExp = sheet.getCell(6,row).getContents();
			//优先级
			String caseFlowPri = sheet.getCell(7,row).getContents();
			//用例类型
			String caseFlowType = sheet.getCell(8,row).getContents();
			//用例属性
			String caseFlowPro = sheet.getCell(9,row).getContents();
			//设计者
			String caseFlowDesigner = sheet.getCell(10,row).getContents();
			//设计时间
			String designTime = sheet.getCell(11, row).getContents();
			//备注
			String memo = sheet.getCell(12, row).getContents();
			
			int directoryId = createCaseDir(path,Integer.parseInt(systemId));
			caseFlow = caseFlowDAL.Get(Op.EQ("systemId", Integer.parseInt(systemId)),
					Op.EQ("caseFlowNo", caseFlowNo), Op.EQ("directoryId", directoryId));
			if(caseFlow == null){
				caseFlow = new CaseFlow();
				caseFlow.setDirectoryId(directoryId);
				caseFlow.setCaseFlowNo(caseFlowNo);		
				caseFlow.setSystemId(Integer.parseInt(systemId));
			}			
			
		//	caseFlow.setImportBatchNo(importBatchNo);			
			caseFlow.setCaseFlowPath(path);				
			caseFlow.setCaseFlowName(caseFlowName);
			caseFlow.setDescription(caseFlowDes);
			caseFlow.setPreConditions(caseFlowPreCon);
			caseFlow.setCaseFlowStep(caseFlowStep);
			caseFlow.setExpectedResult(caseFlowExp);
			caseFlow.setPriority(caseFlowPri);
			caseFlow.setCaseType(caseFlowType);
			caseFlow.setCaseProperty(caseFlowPro);
			caseFlow.setDesigner(caseFlowDesigner);	
			caseFlow.setDesignTime(designTime);
			caseFlow.setMemo(memo);
			
			caseFlow.setDisabledFlag(0);
			caseFlow.setStepCount(0);
			caseFlow.setPassFlag(0);
					
//			ScriptFlow scriptFlow = new ScriptFlow();
//			String busiFlowName = importBatchNo+ "_" +caseFlow.getCaseFlowNo();
//			scriptFlow.setName(busiFlowName);
//			scriptFlow.setDescription(caseFlow.getCaseFlowName());
//			scriptFlow.setSystemid(systemId);
//			String srcipt ="run_caseFlow(\"" + importBatchNo;
//			srcipt +=  "\", \"" + caseFlow.getCaseFlowNo() + "\");";
//			scriptFlow.setSrcipt(srcipt);
//			scriptFlowDAL.Add(scriptFlow);			
//			caseFlow.setScriptFlowId(scriptFlow.getId());
			if(caseFlow.getId() == null){
				caseFlow.setCreatedUserId(userId);			
				caseFlow.setCreatedTime(new Date());
				caseFlowDAL.Add(caseFlow);
				OperationLogService.writeOperationLog(OpType.CaseFlow, IDUType.Import, 
						caseFlow.getId(), caseFlow.getCaseFlowName(),
						"caseFlowName", null, caseFlow.getCaseFlowName(), loginLogId);
				succ++;
			}
			else{			
				caseFlow.setLastModifiedTime(new Date());
				caseFlow.setLastModifiedUserId(userId);
				OperationLogService.writeOperationLog(OpType.CaseFlow, IDUType.Import, 
						caseFlow.getId(), caseFlow.getCaseFlowName(),
						"caseFlowName", caseFlow.getCaseFlowName(), caseFlow.getCaseFlowName(), loginLogId);
				caseFlowDAL.Edit(caseFlow);
				update++;
			}
			caseFlowMap.put(caseFlow.getCaseFlowNo(), caseFlow);	
		}
		
		time = (System.currentTimeMillis() - time)/1000;
		
		resultList.add("newadd:" + "共新增用例记录"+succ+"条");
		resultList.add("edit:" + "共更新用例记录"+update+"条");
		resultList.add("newadd:" + "共跳过无效用例记录"+wrong+"条");
		resultList.add("newadd:" + "导入用例共花费"+time+"s");
	}
 
	private int createCaseDir(String path, int systemId) {
		// TODO Auto-generated method stub	
		String[] dirName = path.split("\\\\");		
		CaseDirectory caseDir;
		//"\a\b\c"
		int parentID = 0;
		int i;
		//找到不存在的目录
		for(i=1; i<dirName.length; i++) {
			caseDir = caseDirDAL.Get(
				Op.EQ(GWTCaseDirectory.N_SystemID, systemId),
				Op.EQ(GWTCaseDirectory.N_ParentDirID, parentID),
				Op.EQ(GWTCaseDirectory.N_Name, dirName[i]));
			if(caseDir == null) {
				break;
			}
			parentID = caseDir.getId();
		}
		
		if(i == dirName.length)
			return parentID;
		
		int count = caseDirDAL.Count(
					Op.EQ(GWTCaseDirectory.N_SystemID, systemId),
					Op.EQ(GWTCaseDirectory.N_ParentDirID, parentID));
		
		StringBuffer loaclPath = new StringBuffer();
		if(parentID != 0){
			CaseDirectory parent = caseDirDAL.Get(Op.EQ(GWTCaseDirectory.N_ID, parentID));
			loaclPath.append(parent.getPath());
		}
		//循环创建不存在的目录
		for(int j=i; j<dirName.length; j++) {					
			caseDir = new CaseDirectory();
			caseDir.setSystemId(systemId);
			caseDir.setParentDirId(parentID);
			

			loaclPath.append("\\");
			loaclPath.append(dirName[j]);
			caseDir.setPath(loaclPath.toString());
			
			if(j == i)
				caseDir.setSortIndex(count+1);
			else 
				caseDir.setSortIndex(1);
			caseDir.setName(dirName[j]);
			caseDirDAL.Add(caseDir);
			parentID = caseDir.getId();
		}	
		return parentID;
	}

//	/**
//	*新增一个批次记录，并返回批次号
//	* @param desc 
//	*@param
//	*@return 批次号
//	*/
//	private String getBatchNo(String systemId, String userId, String desc) {
//		String userName = "";
//		
//		if(userId.equals("0")) {
//			userName="Admin";
//		} else {
//			IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
//			User user = userDAL.Get(Op.EQ(GWTUser.N_id,userId));
//			userName = user.getName();
//		}
//		
//		Date dt = new Date();
//		SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
//		String batchNum = userName + date.format(dt); 
//		date = new SimpleDateFormat("yyyy年MM月dd日,HH:mm:ss");
//		CaseImportBatch caseImportBatch = new CaseImportBatch();
//		caseImportBatch.setBatchNo(batchNum);
//		if(desc != null) {
//			caseImportBatch.setDescription(desc);
//		} else {
//			caseImportBatch.setDescription("用户名: "+userName+"   日期: "+ date.format(dt));
//		}	
//		caseImportBatch.setUserId(Integer.parseInt(userId));
//		caseImportBatch.setSystemId(Integer.parseInt(systemId));
//		caseImportBatch.setImportTime(dt);
//		IDAL<CaseImportBatch> caseImportBatchDAL = DALFactory.GetBeanDAL(CaseImportBatch.class);
//		caseImportBatchDAL.Add(caseImportBatch);
//		return batchNum;
//	}
	
	private void deserializeCases(String systemId, String userId,
			 Sheet sheet, List<String> resultList){
		int rows = sheet.getRows();
		int succ = 0;
		int wrong = 0;
		int update = 0;
		long time = System.currentTimeMillis();
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
		Case caseBean = null;
		CaseFlow lastCaseFlow = null;
		for(int row=1; row < rows; row++) {	
			boolean fl= false;//标记案例是否有效
			
			for(int i = 1; i < 4; i++){//前四列如有空值，则为无效案例，跳过
				if(sheet.getCell(i, row).getContents().equals("")){
					fl = true;
					break;
				}
			}
			if(fl){
				wrong++;
				continue;
			}
			
			//用例编号
			String caseFlowNo = sheet.getCell(0, row).getContents();
			//交易类型
			String tranType = sheet.getCell(1,row).getContents();
			//步骤编号
			String caseNo = sheet.getCell(2,row).getContents();
			//步骤名称
			String caseName = sheet.getCell(3,row).getContents();
			//步骤描述
			String desc = sheet.getCell(4,row).getContents();
			
			CaseFlow caseFlow;
			if(caseFlowNo == null || caseFlowNo.isEmpty()){
				caseFlow = lastCaseFlow;
			}else{
				if(caseFlowMap.get(caseFlowNo.trim())==null){
					wrong++;
					resultList.add("error: " + sheet.getName() + 
							"第" + (row+1) + "行" + "找不到用例编号为"+caseFlowNo+"的用例！");
					continue;
				}
				caseFlow = caseFlowMap.get(caseFlowNo.trim());
			}
			caseBean = caseDAL.Get(Op.EQ("caseFlow.id", caseFlow.getId()), Op.EQ("caseNo", caseNo));
			if(caseBean == null){
				caseBean = new Case();
				caseBean.setCaseNo(caseNo);
				caseBean.setCaseFlow(caseFlow);
			}			
			Transaction tran = tranDAL.Get(Op.EQ("systemId", systemId), Op.EQ("tranName", tranType));
			if(tran == null){
				wrong++;
				resultList.add("error: " + sheet.getName() + 
						"第" + (row+1) + "行" + "找不到交易名称为"+tranType+"的交易！");
				continue;
			}
			if(caseBean.getCaseId() == null || !caseBean.getTransactionId().equals(tran.getTransactionId())){
				caseBean.setTransactionId(tran.getTransactionId());
				caseBean.setRequestXml(tran.getRequestStruct());
				caseBean.setExpectedXml(tran.getResponseStruct());
			}
			caseBean.setCaseName(caseName);
			caseBean.setDescription(desc);
			caseBean.setBreakPointFlag(0);
			caseBean.setFlag(0);
		//	caseBean.setImportBatchNo(caseFlow.getImportBatchNo());
			caseBean.setIsdefault(0);
			caseBean.setIsParseable(1);
			if(caseBean.getCaseId() == null){
				int sequence = caseDAL.Count(Op.EQ("caseFlow.id", caseFlow.getId()));
				caseBean.setSequence(sequence);
				caseDAL.Add(caseBean);
				succ++;
			}else{
				caseDAL.Edit(caseBean);
				update++;
			}
			lastCaseFlow = caseFlow;
		}
		time = (System.currentTimeMillis() - time)/1000;
		resultList.add("newadd:" + "共新增用例步骤记录"+succ+"条");
		resultList.add("edit:" + "共更新用例步骤记录"+update+"条");
		resultList.add("newadd:" + "共跳过无效用例步骤记录"+wrong+"条");
		resultList.add("newadd:" + "导入用例步骤共花费"+time+"s");
	}
	
}
