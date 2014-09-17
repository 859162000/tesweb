package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.common.TypeTranslate;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.util.ExcelSerializer;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.ui.util.TranStructTreeUtil;

public class CaseStructServletUpload extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(CaseStructServletUpload.class);
	private static final long serialVersionUID = -4658754990050679519L;

	public CaseStructServletUpload() {
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
			throws ServletException, IOException {
		response.setCharacterEncoding("utf8");
		List<String> resultList = new ArrayList<String>();
		ServletFileUpload upload = new ServletFileUpload();
		try {
			String type = request.getParameter("type");

			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				if (item.isFormField()) {
					continue;
				} else {
					//文件中文问题未解决
//					if (!item.getName().toLowerCase().endsWith(".xls"))
//						throw new DownLoadException("文件扩展名错误，要求为(.xls)!");

					int isClientSimu = Integer.parseInt(request.getParameter("isClientSimu"));
					
					// 获得报文样式
					String systemId = request.getParameter("sysId");
					Integer loginLogId = Integer.parseInt(request.getParameter("loginLogId"));
					IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
					SysType sys = sysDAL.Get(Op.EQ(GWTSimuSystem.N_SystemID,
							systemId));
					if(sys == null)
					{
						throw new DownLoadException("无法获得当前模拟系统信息，无法上传");
					}
//					final ISystemConfig config = SystemConfigManager.getConfig(
//							sys.getSystemName(), isClientSimu);
					if (type.toLowerCase().trim().equals("single")) {
						resultList = UploadSingleCase(sys.getSystemName(), isClientSimu,
								item,request, response, loginLogId);
					} else {
						resultList = UploadMultiCase(sys.getSystemName(), isClientSimu,
								item, request, response, loginLogId);
					}
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
		response.getOutputStream().write(msg.getBytes());
		response.flushBuffer();
	}

	private List<String> UploadSingleCase(String sysName,int isClientSimu, 
			FileItemStream item,HttpServletRequest request,
			HttpServletResponse response, Integer loginLogId) throws Exception {
		String caseId = request.getParameter("caseId");
		
		boolean isClient = TypeTranslate.IntToBoolean(isClientSimu);
		boolean setCaseContent = Boolean.parseBoolean(request.getParameter("setCaseContent"));
		
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case caseBean = caseDAL.Get(Op.EQ(GWTCase.N_caseId, caseId));
		if (caseBean == null)
			throw new DownLoadException("本案例已不存在，上传失败");
	//	String caseName = caseBean.getCaseName();
		
		Transaction tranInfo = new TransactionService().GetSingle(caseBean.getTransactionId());		
		//一般不会出现这个情况
		if(tranInfo == null)
			throw new DownLoadException("无法获得交易信息，请确认该案例所对应交易已删除！");
		String tranName = tranInfo.getTranName();
		SysType sysInfo = new SimuSystemService().GetSimuSystemSignle(tranInfo.getSystemId());
		//一般不会出现这个情况
		if(sysInfo == null)
			throw new DownLoadException("所属系统已删除！");
		
		MsgDocument tranStruct = null;

		// 样式属性
		String sheetNameEnd = "out";
		if (isClient) {
			if (setCaseContent) {
				sheetNameEnd = "in";
				tranStruct = MsgLoader.LoadXml(tranInfo.getRequestStruct());
			} else {
				tranStruct = MsgLoader.LoadXml(tranInfo.getResponseStruct());
			}
		} else
			tranStruct = MsgLoader.LoadXml(tranInfo.getResponseStruct());

		String sheetTranName = "";
		boolean sheetIsRes = false;
		List<String> resultList = new ArrayList<String>();
		InputStream stream = item.openStream();
		Workbook workbook = Workbook.getWorkbook(stream);
		
		for (Sheet sheet : workbook.getSheets()) {
			String specStr = "";
			try
			{
				specStr = CaseService.GetPackContent(new GWTPackNeed(sysInfo.getSystemId(),sysInfo.getSystemName(),sysInfo.getChannel(),isClient, tranInfo.getChannel(),tranInfo.getTranCode()));
			}
			catch (Exception e) {
				resultList.add("error:" + e.getMessage());
			}
			
			String[] strs = StringUtils.split(sheet.getName(), "|");
			if (strs.length > 0) {
				sheetTranName = strs[0].trim();
				sheetIsRes = strs[1].trim().toLowerCase().equals(sheetNameEnd);
			} else
				sheetTranName = sheet.getName();

			// 交易名称相符，并且是响应报文
			if (sheetTranName.equals(tranName) && sheetIsRes) {
				Map<String, MsgDocument> cases = ExcelSerializer
						.DeserializeCases(sheet);
				// 找到对应的案例
				for (String key : cases.keySet()) {
					//不作匹配，方面写案例    by ljs
					//if (key.compareTo(caseName) == 0) {
						MsgDocument caseDoc = cases.get(key);
						//暂不做清洗，这会造成无法构造重复域   by ljs
						//caseDoc = TranStructTreeUtil.CleanCaseStruct(tranStruct, caseDoc);
						if (caseDoc != null) {
								String msg = new CaseService().SaveCaseContent(
										specStr,tranInfo.getTranCode(), caseBean, setCaseContent, isClientSimu,
										caseDoc.toString(), loginLogId);
								resultList.add("edit:" +"Upload casedata success");
								if(!msg.isEmpty())
									resultList.add(msg);
								break;
						} else {
							throw new DownLoadException("结构解析失败");
						}
				//	} else {
					//	resultList.add("error:" + "案例名称【" + key + "】不符合要求；跳过");
					//}
				}
				break;
			} else {
				resultList.add("error:" + sheet.getName() + "[sheet表不符合规则]");
			}
		}
		return resultList;
	}

	private List<String> UploadMultiCase(String sysName, int isClientSimu, 
			FileItemStream item,HttpServletRequest request,
			HttpServletResponse response, Integer loginLogId) throws Exception {
		String tranId = request.getParameter("tranId");
		boolean isClient = TypeTranslate.IntToBoolean(isClientSimu);
		
		Transaction tranInfo = new TransactionService().GetSingle(tranId);
		if(tranInfo == null)
			throw new DownLoadException("无法获得交易信息！");
		String tranCode = tranInfo.getTranCode();
		
		SysType sysInfo = new SimuSystemService().GetSimuSystemSignle(tranInfo.getSystemId());
		//一般不会出现这个情况
		if(sysInfo == null)
			throw new DownLoadException("所属系统已删除！");
		
		MsgDocument dataStruct = MsgLoader.LoadXml(isClient ? tranInfo.getRequestStruct() : tranInfo.getResponseStruct());
		MsgDocument oracleStruct = MsgLoader.LoadXml(!isClient ? tranInfo.getRequestStruct() : tranInfo.getResponseStruct());

		String sheetTranCode = "";
		List<String> resultList = new ArrayList<String>();
		InputStream stream = item.openStream();
		Workbook workbook = Workbook.getWorkbook(stream);
		Map<String, MsgDocument> casesContent = null;
		Map<String, MsgDocument> oracleContent = null;
		
		try {
			String specStr = "";
			try
			{
				specStr = CaseService.GetPackContent(new GWTPackNeed(sysInfo.getSystemId(),sysInfo.getSystemName(),
						sysInfo.getChannel(),isClient, tranInfo.getChannel(),tranInfo.getTranCode()));
			}
			catch (Exception e) {
				resultList.add("error:" + e.getMessage());
			}
			
			//读取Excel sheet页面
			for (Sheet sheet : workbook.getSheets()) {
				String[] strs = StringUtils.split(sheet.getName(), "|");
				String sheetEndName = "";
				if (strs.length > 0) {
					sheetTranCode = strs[0].trim();
					sheetEndName = strs[1].trim().toLowerCase();
				} else
					sheetTranCode = sheet.getName();

				// 交易名称相符，并且是响应报文
				if (sheetTranCode.equals(tranCode)) {
					if (sheetEndName.equals("in")) {
						// 发起端交易
						if (isClient) {
							// 保证只初始化一次
							if (casesContent == null) {
								casesContent = ExcelSerializer
										.DeserializeCases(sheet);
							}
						}
						// 接收端交易
						// else
						// {
						// resultList.add("error:接收方交易，不需要预期结果");
						// }
					} else if (sheetEndName.equals("out")) {
						if (isClient) {
							// 保证只初始化一次
							if (oracleContent == null) {
								oracleContent = ExcelSerializer
										.DeserializeCases(sheet);
							}
						} else {
							// 保证只初始化一次
							if (casesContent == null) {
								casesContent = ExcelSerializer
										.DeserializeCases(sheet);
							}
						}
					}
					// else
					// {
					// resultList.add("error:后缀名不符合要求");
					// }
				} else {
					resultList.add("error:" + sheet.getName() + "，交易码错误");
				}
			}

			//保存数据
			if(casesContent != null)
			{
				for (String key : casesContent.keySet()) {
					IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
					Case caseBean = caseDAL.Get(Op.EQ(GWTCase.N_transactionId,
							tranId), Op.EQ(GWTCase.N_caseName, key));

					//组装预期就跳过
					String oracleStr = "";
					if(isClient)
					{
						if (oracleContent != null) {
							MsgDocument tempDoc = oracleContent.get(key);
							if (tempDoc != null) {
								TranStructTreeUtil.CleanCaseStruct(
										oracleStruct, tempDoc);
								oracleStr = tempDoc.toString();
							} else
								oracleStr = tranInfo.getResponseStruct();
						}
						else
						{
							oracleStr = tranInfo.getResponseStruct();
						}
					}

					MsgDocument caseDoc = casesContent.get(key);
					caseDoc = TranStructTreeUtil.CleanCaseStruct(dataStruct, caseDoc);
					
					if (caseBean == null) { // 不存在则新增
						caseBean = new Case();
						caseBean.setCaseName(key);
						caseBean.setIsParseable(1);
						caseBean.setTransactionId(tranId);
						caseBean.setExpectedXml(oracleStr);
						
						// 批量案例上传完调用组包
						byte[] packedMsg = SystemConfigManager.PackSpecification(
								specStr, caseDoc.toString(),tranInfo.getTranCode());
						caseBean.setRequestXml(caseDoc.toString());
						caseBean.setRequestMsg(packedMsg);
						caseBean.setCreatedTime(new Date());
						caseBean.setCreatedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
						caseDAL.Add(caseBean);
						OperationLogService.writeOperationLog(OpType.Case, IDUType.Import,
								Integer.parseInt(caseBean.getCaseId()), caseBean.getCaseName(),
								"caseName", null, caseBean.getCaseName(), loginLogId);
						resultList.add("newadd:" + key);
					}
					else { // 存在则更新
						caseBean.setExpectedXml(oracleStr);
						String msg = new CaseService().SaveCaseContent(specStr, tranInfo.getTranCode(), caseBean, true, isClientSimu, caseDoc.toString(), loginLogId);
						resultList.add("edit:" + key + "," + msg);
					}
				}
			}
			else
			{
				resultList.add("error:导入失败，由于缺少案例数据sheet页【" + tranCode + "|" + (isClient ? "in" : "out") + "】;");
			}
		} catch (Exception e) {
			log.error(e);
			throw new DownLoadException("遇到未处理的问题，请与管理员联系;");
			// resultList.add("error:" + sheet.getName() + ":[遇到遇到情况]");
		}
		return resultList;
	}

	public void init() throws ServletException {
	}
}