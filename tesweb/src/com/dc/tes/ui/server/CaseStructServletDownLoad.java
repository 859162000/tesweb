package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.ui.client.common.TypeTranslate;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.dc.tes.ui.util.ExcelSerializer;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;

public class CaseStructServletDownLoad  extends HttpServlet {

	private static final long serialVersionUID = -1627222864446712538L;
	IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String type = request.getParameter("type");
		try {
			boolean bGetCaseContent = false;
			boolean bGetOracleContent = false;
			int isClientSimu = 1;
			try	{
				isClientSimu = Integer.parseInt(request.getParameter("isClientSimu"));
			}
			catch (Exception e) {
			}
			
			List<Case> caseList = new ArrayList<Case>();
			String tranId = request.getParameter("tranId");
			if (type.toLowerCase().trim().equals("single"))
			{
				//设置取的数据类别
				bGetCaseContent = Boolean.parseBoolean(request.getParameter("bGetCaseContent"));
				bGetOracleContent = !bGetCaseContent;
				
				String caseId = request.getParameter("caseId");
				caseList = new ArrayList<Case>();
				Case caseInfo = caseDAL.Get(Op.EQ(GWTCase.N_caseId, caseId));
				if(caseInfo != null)
					caseList.add(caseInfo);
				else
				{
					throw new DownLoadException("未获取到案例配置信息");
				}
				tranId = caseInfo.getTransactionId();
			}
			else if(type.toLowerCase().trim().equals("multi"))
			{
				//设置取的数据类别
				bGetCaseContent = true;
				bGetOracleContent = !TypeTranslate.IntToBoolean(isClientSimu);
				
				String caseIds = request.getParameter("caseId");
				String[] caseIDArray = StringUtils.split(caseIds,",");
				caseList = new ArrayList<Case>();
				for(int i = 0; i<caseIDArray.length; i++)
				{
					Case caseInfo = caseDAL.Get(Op.EQ(GWTCase.N_caseId, caseIDArray[i]));
					if(caseInfo != null)
					{
						tranId = caseInfo.getTransactionId();
						caseList.add(caseInfo);
					}
					else {
						continue;
					}
				}
			}
			DownloadCaseStruct(tranId,isClientSimu,bGetCaseContent,bGetOracleContent, caseList,request, response);
//			else  if(type.toLowerCase().trim().equals("all"))
//			{
//				List<Case> caseList = caseDAL.ListAll(Op.EQ(GWTCase.N_transactionId, tranId),Op.EQ(GWTCase.N_isParseable, "1"));
//				if(caseList.size() == 0)
//				{
////					Case caseInfo = new Case();
////					caseInfo.setCaseId("1");
////					caseInfo.setCaseName("模板");
////					caseInfo.setIsParseable(1);
////					caseInfo.setTransactionId(tranId);
////					caseInfo.setXmlContent(tran);
//				}
//				DownloadCaseStruct(tranId,caseList,request, response);
//			}
		} 
		catch(DownLoadException ex)	{
			response.getOutputStream().write(ex.getMessage().getBytes("utf8"));
			response.flushBuffer();
			ex.printStackTrace();
		}
		catch (Exception ex) {
			response.getOutputStream().write(
					"error:下载案例数据发生异常".getBytes("utf8"));
			response.flushBuffer();
			ex.printStackTrace();
		}
	}
	
	
	private void DownloadCaseStruct(String tranId, int isClientSimu, boolean bGetCaseContent,
			boolean bGetOracleContent, List<Case> caseList, HttpServletRequest request, HttpServletResponse response)
		throws Exception,DownLoadException {
		
		boolean isClient = TypeTranslate.IntToBoolean(isClientSimu);
		
		//获得交易信息
		Transaction tran = new TransactionService().GetSingle(tranId);
		if(tran == null){
			throw new DownLoadException("无法获得交易信息，下载失败");
		}
		
		//获得系统报文样式信息
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sys = sysDAL.Get(Op.EQ(GWTSimuSystem.N_SystemID, tran.getSystemId()));
		if(sys == null){
			throw new DownLoadException("无法获得模拟系统信息，下载失败");
		}
		ISystemConfig config = SystemConfigManager.getConfig(sys.getSystemName(),tran.getIsClientSimu());
		
		//案例数据、预期结果 Document列表
		Map<String,MsgDocument> dataContentList = new HashMap<String,MsgDocument>();
		Map<String,MsgDocument> oracleContentList = new HashMap<String,MsgDocument>();
		MsgDocument tranDoc = null;
		for(Case caseInfo : caseList)
		{
			//案例数据
			if(bGetCaseContent)
			{
				MsgDocument doc = MsgLoader.LoadXml(caseInfo.getRequestXml());
				dataContentList.put(caseInfo.getCaseName(), doc);
				//下载案例不用交易模板了，直接用案例的模板。这样暂时无法支持批量下载案例数据
				tranDoc = doc;
			}
			
			//预期结果
			if(bGetOracleContent)
			{
				MsgDocument doc = MsgLoader.LoadXml(caseInfo.getExpectedXml());
				oracleContentList.put(caseInfo.getCaseName(), doc);
				tranDoc = doc;
			}
		}
		
		int sheetCount = 0;
		File file = new HelperService().CreateTempFile("Case_stru", ".xls");
		FileOutputStream stream = new FileOutputStream(file);
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		//案例数据
		if(bGetCaseContent)
		{
			//MsgDocument tranDoc =  MsgLoader.LoadXml(isClient ?  tran.getRequestStruct() : tran.getResponseStruct() );
			tranDoc.setAttribute("tranCode", new Value(tran.getTranCode()));
			tranDoc.setAttribute("tranDesc", new Value(tran.getDescription()));
			tranDoc.setAttribute("transCateId", new Value(tran.getTransactionCategoryId()));
			String sheetName = isClient ? (tran.getTranName() + "|in") : (tran.getTranName() + "|out");
			//TODO:这里只取了字段属性
			List<MsgAttribute> fAttrs = isClient ? config.getReqFieldAttributes() : config.getRespFieldAttributes();
			WritableSheet sheet = workbook.createSheet(sheetName, sheetCount);
			ExcelSerializer.SerializeCases(fAttrs, sheet, tranDoc, dataContentList);
			sheetCount ++;
		}
		
		//预期结果
		if(bGetOracleContent)
		{
		//	tranDoc =  MsgLoader.LoadXml(tran.getResponseStruct());
			tranDoc.setAttribute("tranCode", new Value(tran.getTranCode()));
			tranDoc.setAttribute("tranDesc", new Value(tran.getDescription()));
			tranDoc.setAttribute("transCateId", new Value(tran.getTransactionCategoryId()));

			String sheetName = tran.getTranName() + "|out";
			List<MsgAttribute> fAttrs = config.getRespFieldAttributes();
			WritableSheet sheet = workbook.createSheet(sheetName, sheetCount);
			ExcelSerializer.SerializeCases(fAttrs, sheet, tranDoc, oracleContentList);
		}
		
		workbook.write();
		workbook.close();
		
		response.getOutputStream().write(file.getName().getBytes("utf8"));
		response.flushBuffer();
		return;
	}
	
}
