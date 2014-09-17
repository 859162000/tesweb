package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.Card;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.ExecuteSetTaskItem;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.util.XmlPackUtil;

/**
	*批量上传案例
	*@param
	*@return
	*/
@Deprecated
public class MultiCaseUpload extends HttpServlet {
	
	private static final long serialVersionUID = 6753653051964081175L;
	private static final Log log = LogFactory.getLog(MultiCaseUpload.class);
	
	public MultiCaseUpload(){
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
		response.setCharacterEncoding("gb2312");
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
					String desc =request.getParameter("desc").equals("null")?null:request.getParameter("desc");
					boolean isAdmin = Boolean.parseBoolean(request.getParameter("isAdmin"));
					// 获得报文样式
					String systemId = request.getParameter("sysId");
					IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
					SysType sys = sysDAL.Get(Op.EQ(GWTSimuSystem.N_SystemID,
							systemId));
					if(sys == null)
					{
						throw new DownLoadException("无法获得当前模拟系统信息，无法上传");
					}
					resultList = UploadMultiCase(sys.getSystemId(), isClientSimu,
								item, request, response, isAdmin, desc);	
										
					break;
				}
			}
		} catch (DownLoadException ex) {
			ex.printStackTrace();
			response.getOutputStream()
					.write(ex.getMessage().getBytes("gb2312"));
			response.flushBuffer();
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					"error:发生异常，请与管理员联系。".getBytes("gb2312"));
			response.flushBuffer();
			return;
		}
		
		String msg = resultList.toString();
		response.getOutputStream().write(msg.getBytes("gb2312"));
		response.flushBuffer();
	}
	
	private List<String> UploadMultiCase(String systemId, int isClientSimu,
			FileItemStream item, HttpServletRequest request,
			HttpServletResponse response, boolean isAdmin, String desc) throws Exception{
		// TODO Auto-generated method stub
		
		String sheetName = "";
		String userId;
		if(request.getParameter("userId").equals("Administrator")){
			userId="0";
		}else
			userId = request.getParameter("userId");
		
		List<String> resultList = new ArrayList<String>();
		InputStream stream = item.openStream();
		Workbook workbook = Workbook.getWorkbook(stream);
	//	String importBatchNo = getBatchNo(systemId,userId, desc); //生成批次号		
//		ExecuteSet queue = createExecuteSet(importBatchNo, systemId, desc);  //生成任务队列，获得对象
//		try{
//			//读取Excel sheet页面
//			for (Sheet sheet : workbook.getSheets()) {
//				sheetName = sheet.getName().toLowerCase();
//				
//				// 如果是Card页
//				if (sheetName.equalsIgnoreCase("card")) {
//					DeserializeCards(sheet, importBatchNo, resultList);
//				}else if(sheetName.startsWith("case") ||
//						sheetName.startsWith("test case")){
//					DeserializeCases(sheet, importBatchNo, isClientSimu, systemId, resultList, queue, userId);
//				}else
//					continue;
//			}
//		}catch(Exception e) {
//			log.error(e);
//			throw new DownLoadException("遇到未处理的问题，请与管理员联系;");
//			// resultList.add("error:" + sheet.getName() + ":[遇到遇到情况]");
//		}
			
		return resultList;
			
	}
	
	/**
	 * 新增一个任务队列，并返回任务队列ID
	 * @param importBatchNo
	 * @param systemId
	 * @param desc 
	 * @return 任务队列对象
	 */
	private ExecuteSet createExecuteSet(String importBatchNo, String systemId, String desc) {
		// TODO Auto-generated method stub
		IDAL<ExecuteSet> executeSetDAL = DALFactory.GetBeanDAL(ExecuteSet.class);
		ExecuteSet executeSet = new ExecuteSet();
		executeSet.setName(importBatchNo);
		executeSet.setImportBatchNo(importBatchNo);
		executeSet.setSystemId(systemId);
		if(desc!=null){
			executeSet.setDescription(desc);
		}else{
			executeSet.setDescription("导入案例时自动创建，批次号为："+importBatchNo);
		}		
		executeSetDAL.Add(executeSet);
		//queueList = queueListDAL.Get(Op.EQ(GWTQueue.N_Name, importBatchNo));
		//if(queueList!=null)
			return executeSet;
	//	else {
		//	throw new DownLoadException("添加任务队列失败，无法导入案例;");
	//	}
	}
	/**
	 * 从EXCEL中导入卡信息
	 * @param sheet
	 * @param importBatchNo
	 * @param resultList
	 * @throws JXLException 
	 */
	private void DeserializeCards(Sheet sheet, String importBatchNo,
			List<String> resultList)throws JXLException {
		int rowNum = sheet.getRows();
		IDAL<Card> cardDAL = DALFactory.GetBeanDAL(Card.class);
		int succNum = 0;
		for(int i = 1; i < rowNum; i++){	
			String cardNo = sheet.getCell(2, i).getContents();
			if(cardNo.equals("") || cardNo == null){
				continue;
			}
			if(cardDAL.Get(Op.EQ("cardNumber", cardNo), Op.EQ("importBatchNo", importBatchNo))!=null){
				resultList.add("error:" + "编号["+sheet.getCell(0, i).getContents()+"]的卡帐号已存在，不导入");
				continue;
			}
			Card card = new Card();
			card.setCardNumber(cardNo);
			card.setSequence(Integer.parseInt(sheet.getCell(0, i).getContents()));
			card.setDbHost(sheet.getCell(1, i).getContents());
			card.setCardPwd(sheet.getCell(3, i).getContents());
			card.setVaildUntil(sheet.getCell(4, i).getContents());
			card.setSubBankNo(sheet.getCell(5, i).getContents());
			card.setSubsidiaryNo(sheet.getCell(6, i).getContents());
			card.setCardType(sheet.getCell(7, i).getContents());
			card.setCardStatus(sheet.getCell(8, i).getContents());
			if(sheet.getCell(9, i).getContents().length()<=37){
				card.setTrack2(sheet.getCell(9, i).getContents());
			}else{
				resultList.add("error:" + "编号["+sheet.getCell(0, i).getContents()+"]的卡二磁长度大于37位，不导入");
				continue;
			}
			if(sheet.getCell(10, i).getContents().length()<=104){
				card.setTrack3(sheet.getCell(10, i).getContents());
			}else{
				resultList.add("error:" + "编号["+sheet.getCell(0, i).getContents()+"]的卡三磁长度大于104位，不导入");
				continue;
			}
			card.setImportBatchNo(importBatchNo);
			try{
			cardDAL.Add(card);
			}catch(Exception e){
				log.error(e);
				throw new DownLoadException("导入卡信息出现问题，请与管理员联系;");
			}
			succNum++;
			}
		resultList.add("newadd:" + "成功增加卡信息记录"+succNum+"条");
		
	}
	
	/**
	 * 从EXCEL中导入案例信息
	 * @param sheet
	 * @param importBatchNo
	 * @param isClientSimu 
	 * @param systemId 
	 * @param resultList
	 * @param queue 
	 * @param userId 
	 * @throws JXLException 
	 */
	private void DeserializeCases(Sheet sheet, String importBatchNo, int isClientSimu,
			String systemId, List<String> resultList, ExecuteSet queue, String userId) throws JXLException {
//		int rows = sheet.getRows(); // 总行数
//		int cols = sheet.getColumns(); //总列数
//		int succ = 0; //成功导入的案例记录数
//		int wrong = 0; //无效的案例记录数
//		CaseFlow caseFlow = null; //业务流编号
//		int sequence = 0;
//		LinkedHashMap<String, String> field = new LinkedHashMap<String,String>();//用来暂时存放域名和域值
//		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
//		IDAL<Card> cardDAL = DALFactory.GetBeanDAL(Card.class);
//		IDAL<CaseFlow> caseFlowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
//		IDAL<ExecuteSetTaskItem> executeSetTaskItemDAL = DALFactory.GetBeanDAL(ExecuteSetTaskItem.class);
//		IDAL<ScriptFlow> scriptFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
//		IDAL<CaseParameterExpectedValue> casePrarmDAL = DALFactory.
//				GetBeanDAL(CaseParameterExpectedValue.class);
//		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.
//				GetBeanDAL(TransactionDynamicParameter.class);
//		IDAL<SystemDynamicParameter> systemParamDAL = DALFactory.
//		        GetBeanDAL(SystemDynamicParameter.class);
//		for(int row = 1; row < rows; row++){
//			boolean fl= false;//标记案例是否有效
//			
//			for(int i = 0; i < 4; i++){//前四列如有空值，则为无效案例，跳过
//				if(sheet.getCell(i, row).getContents().equals("")){
//					fl = true;
//					break;
//				}
//			}
//			if(fl){
//				wrong++;
//				continue;
//			}
//			
//			String caseNo = sheet.getCell(0, row).getContents();    //A.案例编号
//			if(caseDAL.Get(Op.EQ("caseNo", caseNo), Op.EQ("importBatchNo", importBatchNo))!=null){
//				resultList.add("error:" + "案例编号【"+sheet.getCell(0, row).getContents()+"】的案例已存在，不导入");
//				wrong++;
//				continue;
//			}
//			Case _case = new Case();
//			_case.setCaseNo(caseNo);
//			_case.setCaseName(sheet.getCell(1, row).getContents());                            //B.案例名称
//			int cardSequence = Integer.parseInt(sheet.getCell(2, row).getContents());                 //C.卡编号
//			
//			Card card = cardDAL.Get(Op.EQ("importBatchNo", importBatchNo),Op.EQ("sequence", cardSequence));
//
//			if( card!= null){
//				_case.setCardId(card.getId());
//				field.put("b2", card.getCardNumber());
//				field.put("b14", card.getVaildUntil());
//				field.put("b52", card.getCardPwd());
//				field.put("b35", card.getTrack2());
//				field.put("b36", card.getTrack3());
//			}else{
//				resultList.add("error:" + "案例【"+caseNo+"】导入不成功，卡编号【"+cardSequence+"】对应的卡不存在");
//				wrong++;
//				continue;
//			}
//			String tranName = sheet.getCell(3, row).getContents();                             //D.交易类型
//			Transaction tranInfo = new TransactionService().GetSingleByName(tranName,isClientSimu,systemId);
//			if(tranInfo == null){
//				String err  = "交易类型【"+tranName+"】不存在";
//				if(!resultList.contains(err))
//					resultList.add("error:" + err);
//				wrong++;
//				continue;
//			}
//			_case.setTransactionId(tranInfo.getTransactionId());
//			
//			if(!sheet.getCell(4, row).getContents().equals(""))
//				_case.setAmount(Float.parseFloat(sheet.getCell(4, row).getContents()));
//				field.put("b4", sheet.getCell(4, row).getContents());                              //E.交易金额     
//			_case.setDescription(sheet.getCell(7, row).getContents());       //H.备注			
//			//_case.setExpectedField39(sheet.getCell(8, row).getContents());                      //I. [39]
//			_case.setIsParseable(1);
//			_case.setImportBatchNo(importBatchNo);
//			caseDAL.Add(_case);//写入数据库
//			succ++;
//			for(int i=8; i<cols; i++){
//				String param = "";
//				String cellText = sheet.getCell(i, row).getContents();
//				if(cellText.isEmpty()){
//					continue;
//				}
//				
//				String title = sheet.getCell(i, 0).getContents().trim();
//				if(title.startsWith("[") && title.endsWith("]")){
//					title = title.substring(1, title.length()-1);
//					try{
//						int fieldNo = Integer.parseInt(title);
//						title = "b" + fieldNo;
//						field.put(title, cellText);
//						continue;
//					}catch (Exception e) {
//						// TODO: handle exception
//						continue;
//					}
//				}
//
//				String[] strs = StringUtils.split(title, "|");
//				if (strs.length > 0) {
//					param = strs[1].trim();
//					SystemDynamicParameter sysParam = systemParamDAL.
//						Get(Op.EQ("systemId", systemId),Op.EQ("parameterName", param));
//					if(sysParam != null){
//						TransactionDynamicParameter transParam = transParamDAL.
//								Get(Op.EQ("transactionId", _case.getTransactionId()),
//										Op.EQ("systemParameter", sysParam));
//						if(transParam != null && !cellText.isEmpty()){
//							CaseParameterExpectedValue caseExpValue = new CaseParameterExpectedValue();
//							caseExpValue.setTransParameter(transParam);
//							caseExpValue.setCaseId(_case.getCaseId());
//							caseExpValue.setExpectedValue(cellText);
//							casePrarmDAL.Add(caseExpValue);
//						}else if(transParam == null){
//							resultList.add("error:" + "案例编号【"+caseNo+"】案例的"+strs[0]+"参数不属于该交易类型");
//						}
//					}else {
//						resultList.add("error:" + "系统上不存在案例编号【"+caseNo+"】案例的"+strs[0]+"参数");
//					}
//				}/*else if(title.startsWith("[")){
//					int endIndex = title.indexOf("]");
//					if(endIndex != -1 && endIndex == title.length()+1){
//						String fieldNo = title.substring(1, endIndex);
//						
//					}
//				}*/else
//					continue;
//			}
//			_case.setRequestXml(XmlPackUtil.PackXmlContent(field, tranInfo, isClientSimu));
//			caseDAL.Edit(_case);
//			String caseFlowNo = sheet.getCell(5, row).getContents();                            //F.业务流编号
//			//如果业务流编号不为空，判断前一条记录的业务流编号是否与之相等，
//			//如果相等，直接新增FlowCases记录, 否则先新增CaseFlow记录。
//			if(!caseFlowNo.equals("")){	 //存在业务流案例					
//				//如果上一条案例业务流, 检查数据库是否已存在该业务流
//				if(caseFlow == null || !caseFlow.getCaseFlowNo().equals(caseFlowNo)){ 					
//					caseFlow = caseFlowDAL.Get(Op.EQ(GWTCaseFlow.N_CaseFlowNo, caseFlowNo),
//							Op.EQ(GWTCaseFlow.N_ImportBatchNo, importBatchNo));
//					if(caseFlow == null){    //不存在则新增						
//						caseFlow = new CaseFlow();
//						caseFlow.setCreateTime(new Date());
//						caseFlow.setCaseFlowNo(caseFlowNo);
//						caseFlow.setImportBatchNo(importBatchNo);
//						caseFlow.setCaseFlowName(sheet.getCell(6, row).getContents());                //G.业务流名称
//						caseFlow.setSystemId(Integer.parseInt(systemId));
//						caseFlow.setUserId(Integer.parseInt(userId));
//						caseFlowDAL.Add(caseFlow);
//					}else{
//						resultList.add("error:" + "案例编号【"+caseNo+"】案例的业务流系统已存在，无法重复添加");
//						caseFlow = null;
//						wrong++;
//						continue;
//					}					
//				}
//				_case.setCaseFlow(caseFlow);
//				_case.setSequence(sequence++);
//				caseDAL.Edit(_case);
//				//如果下个caseFlow不一致，则业务流结束，先新增业务脚本，再新增任务至任务队列
//				boolean flag;
//				if(row<rows-1){
//					flag = !sheet.getCell(5, row+1).getContents().equals(caseFlow.getCaseFlowNo());
//				}else {
//					flag = true;
//				}
//				if(flag){
//					ScriptFlow scriptFlow = new ScriptFlow();
//					String busiFlowName = importBatchNo+ "_" +caseFlow.getCaseFlowNo();
//					scriptFlow.setName(busiFlowName);
//					scriptFlow.setDescription(caseFlow.getCaseFlowName());
//					scriptFlow.setSystemid(systemId);
//					String srcipt ="run_caseFlow(\"" + importBatchNo;
//					srcipt +=  "\", \"" + caseFlow.getCaseFlowNo() + "\");";
//					scriptFlow.setSrcipt(srcipt);
//					scriptFlowDAL.Add(scriptFlow);
//					caseFlow.setScriptFlowId(scriptFlow.getId());
//					caseFlowDAL.Edit(caseFlow);
//					
//					ExecuteSetTaskItem executeSetTaskItem = new ExecuteSetTaskItem();
//					executeSetTaskItem.setExecuteSet(queue);
//					executeSetTaskItem.setRepCount(1);
//					executeSetTaskItem.setTaskId(scriptFlow.getId());
//					executeSetTaskItem.setType(1);
//					executeSetTaskItem.setTransactionId(tranInfo.getTransactionId());
//					executeSetTaskItemDAL.Add(executeSetTaskItem);
//					caseFlow = null;					
//					sequence = 0;
//				}
//			}else {
//				ExecuteSetTaskItem executeSetTaskItem = new ExecuteSetTaskItem();
//				executeSetTaskItem.setExecuteSet(queue);
//				executeSetTaskItem.setRepCount(1);
//				executeSetTaskItem.setTaskId(_case.getCaseId());
//				executeSetTaskItem.setType(0);
//				executeSetTaskItem.setTransactionId(tranInfo.getTransactionId());
//				executeSetTaskItemDAL.Add(executeSetTaskItem);							
//			}
//			field.clear();
//		}
//		resultList.add("newadd:" + "共新增案例记录"+succ+"条");
//		resultList.add("newadd:" + "共跳过无效案例记录"+wrong+"条");
//		
	}
	

//	/**
//	*新增一个批次记录，并返回批次号
//	 * @param desc 
//	*@param
//	*@return 批次号
//	*/
//	private String getBatchNo(String systemId, String userId, String desc) {
//		String userName = "";
//		if(userId.equals("0")){
//			userName="Admin";
//		}else{
//			IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
//			User user = userDAL.Get(Op.EQ(GWTUser.N_id,userId));
//			userName = user.getName();
//		}
//		Date dt = new Date();
//		SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
//		String batchNum = userName + date.format(dt); 
//		date = new SimpleDateFormat("yyyy年MM月dd日,HH:mm:ss");
//		CaseImportBatch caseImportBatch = new CaseImportBatch();
//		caseImportBatch.setBatchNo(batchNum);
//		if(desc != null){
//			caseImportBatch.setDescription(desc);
//		}else {
//			caseImportBatch.setDescription("用户名: "+userName+"   日期: "+ date.format(dt));
//		}	
//		caseImportBatch.setUserId(Integer.parseInt(userId));
//		caseImportBatch.setSystemId(Integer.parseInt(systemId));
//		caseImportBatch.setImportTime(dt);
//		IDAL<CaseImportBatch> caseImportBatchDAL = DALFactory.GetBeanDAL(CaseImportBatch.class);
//		caseImportBatchDAL.Add(caseImportBatch);
//		return batchNum;
//	}
//	
}
