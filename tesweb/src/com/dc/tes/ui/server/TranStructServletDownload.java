package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.dc.tes.ui.util.ExcelSerializer;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;

public class TranStructServletDownload extends HttpServlet {

	private static final long serialVersionUID = -1627222864446712538L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		String type = request.getParameter("type");
		try {
			if (type.toLowerCase().trim().equals("single"))
				DownloadSingleTranStruct(request, response);
			else
				DownloadSystemTranStruct(request, response);
		} 
		catch(DownLoadException ex)
		{
			ex.printStackTrace();
			response.getOutputStream().write(ex.getMessage().getBytes("utf8"));
			response.flushBuffer();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					"error:下载报文结构发生异常".getBytes("utf8"));
			response.flushBuffer();
		}
		return;
	}
	
	/**
	 * 下载单个交易报文结构
	 * 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	private void DownloadSingleTranStruct(HttpServletRequest request, HttpServletResponse response) 
		throws Exception,DownLoadException{
		String tranId = request.getParameter("tranId");
		boolean isRes = Boolean.parseBoolean(request.getParameter("isRes"));
		
		Transaction tran = new TransactionService().GetSingle(tranId);
		if(tran == null)
			throw new DownLoadException("未获取到交易配置信息");
		
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sys = sysDAL.Get(Op.EQ("systemId", tran.getSystemId()));

		final ISystemConfig config = SystemConfigManager.getConfig(sys.getSystemName(),tran.getIsClientSimu());
		List<MsgAttribute> fAttrs = isRes ? config.getRespFieldAttributes() : config.getReqFieldAttributes();
		
		String xmlContent = isRes ? tran.getResponseStruct() : tran.getRequestStruct();
		String sheetName = tran.getTranName() + (isRes? "|out" : "|in");
		
		File file = new HelperService().CreateTempFile("transtru_", ".xls");
		FileOutputStream stream = new FileOutputStream(file);
		
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		WritableSheet sheet = workbook.createSheet(sheetName, 0);
		
		MsgDocument doc = MsgLoader.LoadXml(xmlContent);
		doc.setAttribute("tranCode", new Value(tran.getTranCode()));
		doc.setAttribute("tranDesc", new Value(tran.getDescription()));
		doc.setAttribute("transCateId", new Value(tran.getTransactionCategoryId()));
		
		ExcelSerializer.SerializeStru(fAttrs, sheet, doc);
		
		workbook.write();
		workbook.close();
		
		response.getOutputStream().write(file.getName().getBytes("utf8"));
		response.flushBuffer();
	}
	
	/**
	 * 下载系统下所有交易报文结构
	 * 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	private void DownloadSystemTranStruct(HttpServletRequest request,
			HttpServletResponse response) throws Exception,DownLoadException {
		String sysId = request.getParameter("sysId");
		int isClientSimu = Integer.parseInt(request.getParameter("isClientSimu"));
		
		boolean hasReq = Boolean.parseBoolean(request.getParameter("hasReq"));
		boolean hasRes = Boolean.parseBoolean(request.getParameter("hasRes"));
		boolean allowEmpty = Boolean.parseBoolean(request.getParameter("allowEmpty"));

		File file = new HelperService().CreateTempFile("transtru_", ".xls");
		FileOutputStream stream = new FileOutputStream(file);

		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		WritableSheet sheet;

		List<Transaction> tranList = new TransactionService().GetTransDAO(sysId,isClientSimu);
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sys = sysDAL.Get(Op.EQ("systemId", sysId));

		final ISystemConfig config = SystemConfigManager.getConfig(sys.getSystemName(),isClientSimu);
		List<MsgAttribute> reqAttrs = config.getReqFieldAttributes();
		List<MsgAttribute> resAttrs = config.getRespFieldAttributes();

		int count = 0;
		if(tranList.size() == 0)
			throw new DownLoadException("本系统下无交易信息，不能下载！");
		for (Transaction tran : tranList) {
			
			try {
				String sheetName = "";
				String xmlContent = "";
				MsgDocument doc = null;
				
				// 输入报文结构
				if(hasReq){
					xmlContent = tran.getRequestStruct();
					if(xmlContent == null) xmlContent = "";
					if(!xmlContent.trim().equals("") || allowEmpty){
						sheetName = String.format("%s|in", tran.getTranName());
						sheet = workbook.createSheet(sheetName, count++);
						
						doc = MsgLoader.LoadXml(xmlContent);
						doc.setAttribute("tranCode", new Value(tran.getTranCode()));
						doc.setAttribute("tranDesc", new Value(tran.getDescription()));
						doc.setAttribute("transCateId", new Value(tran.getTransactionCategoryId()));
						
						ExcelSerializer.SerializeStru(reqAttrs, sheet, doc);
					}
				}

				// 输出报文结构
				if(hasRes){
					xmlContent = tran.getResponseStruct();
					if(xmlContent == null) xmlContent = "";
					if(!xmlContent.trim().equals("") || allowEmpty){
						sheetName = String.format("%s|out", tran.getTranName());
						sheet = workbook.createSheet(sheetName, count++);
						
						doc = MsgLoader.LoadXml(xmlContent);
						doc.setAttribute("tranCode", new Value(tran.getTranCode()));
						doc.setAttribute("tranDesc", new Value(tran.getDescription()));
						doc.setAttribute("transCateId", new Value(tran.getTransactionCategoryId()));
						ExcelSerializer.SerializeStru(resAttrs, sheet, doc);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}
		workbook.write();
		workbook.close();
		
		response.getOutputStream().write(file.getName().getBytes("utf8"));
		response.flushBuffer();
		return;
	}
}
