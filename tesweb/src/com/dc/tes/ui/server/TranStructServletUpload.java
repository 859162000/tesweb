package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.dc.tes.ui.util.ExcelSerializer;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.StringUtil;
import com.dc.tes.ui.util.SystemConfigManager;

public class TranStructServletUpload extends HttpServlet {
	private static final long serialVersionUID = -4658754990050679519L;

	public TranStructServletUpload() {
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
		System.out.println(request.getCharacterEncoding());
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		ServletFileUpload upload = new ServletFileUpload();
		
		try {
			String type = request.getParameter("type");

			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				System.out.println(item.getFieldName() + "....."
						+ item.getName());
				if (item.isFormField()) {
					System.out.println(item.getFieldName() + "....."
							+ item.getName());

				} else {
//					if (!item.getName().toLowerCase().trim().endsWith(".xls"))
//						throw new DownLoadException("文件扩展名错误，要求为(.xls)!");

					if (type.toLowerCase().trim().equals("single")) {
						UploadSingleTran(item, request, response);
					} else {
						InputStream stream = item.openStream();
						if (stream == null)
							throw new IOException();
						Workbook workbook = Workbook.getWorkbook(stream);
						UploadMultiTran(workbook, request, response, true);
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
		} catch (IOException ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					"error:发生异常，请确认本地路径下存在所上传的文件".getBytes("utf8"));
			response.flushBuffer();
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					"error:发生异常，请与管理员联系。".getBytes("utf8"));
			response.flushBuffer();
		}

	}

	private void UploadSingleTran(FileItemStream item,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, DownLoadException, Exception {
		InputStream stream = item.openStream();
		if (stream == null)
			throw new IOException();
		String tranId = request.getParameter("tranId");
		Integer loginLogId = Integer.parseInt(request.getParameter("loginLogId").toString());
		int isClientSimu = Integer.parseInt(request.getParameter("isClientSimu"));
		boolean isRes = Boolean.parseBoolean(request.getParameter("isRes"));
		boolean isEdit = false;

		Transaction tranBean = new TransactionService().GetSingle(tranId);
		if (tranBean == null)
			throw new DownLoadException("本交易已被删除，上传失败");
		String tranName = tranBean.getTranName();
		String systemId = tranBean.getSystemId();

		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sys = sysDAL.Get(Op.EQ(GWTSimuSystem.N_SystemID, systemId));

		final ISystemConfig config = SystemConfigManager.getConfig(sys
				.getSystemName(), isClientSimu);
		List<MsgAttribute> fAttrs = isRes ? config.getRespFieldAttributes()
				: config.getReqFieldAttributes();

		Workbook workbook = Workbook.getWorkbook(stream);
		String sheetTranName = "";
		boolean sheetIsRes = true;
		for (Sheet sheet : workbook.getSheets()) {
			System.out.println(sheet.getName());
			String[] strs = StringUtils.split(sheet.getName(), "|");
			if (strs.length > 0) {
				sheetTranName = strs[0].trim();
				sheetIsRes = strs[1].trim().toLowerCase().equals("out");
			} else
				sheetTranName = sheet.getName();

			// 交易名称相符，并且输入/输出 报文类型匹配
			if (sheetTranName.equals(tranName) && sheetIsRes == isRes) {
				MsgDocument doc = ExcelSerializer
						.DeserializeStru(fAttrs, sheet);
				
				if (doc != null && !StringUtil.IsNullorEmpty(doc.toString())) {
//					doc.ForEach(new ISimpleForEachVisitor() {
//					
//								@Override
//								public void Visit(ForEachSource source, MsgItem item) {
//									// TODO Auto-generated method stub
//									if(source == ForEachSource.StruEnd) {
//										MsgStruct stru = (MsgStruct)item;
//										Iterator<MsgItem> it = stru.iterator();
//										int totalLen = 0;
//										while(it.hasNext()) {
//											MsgItem Item = it.next();
//											if(Item.getAttribute("len") != Value.empty) {
//												String len = (Item.getAttribute("len").str.isEmpty()?
//														"0" : Item.getAttribute("len").str);
//												totalLen += Integer.parseInt(len);
//											}
//										}
//										stru.setAttribute("len", String.valueOf(totalLen));
//														
//									}
//								}		
//							});
					
					
					IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
					String docStr = doc.get(0) == null ? "" : doc.toString();
					if (isRes){
						tranBean.setResponseStruct(docStr);
						OperationLogService.writeOperationLog(OpType.ExpectedMessage, IDUType.Import,
								Integer.parseInt(tranBean.getTransactionId()), tranBean.getTranName(),
								"responseStruct", tranBean.getTranName(), "导入请求报文", loginLogId);
					}else{
						tranBean.setRequestStruct(docStr);
						OperationLogService.writeOperationLog(OpType.RequestMessage, IDUType.Import,
								Integer.parseInt(tranBean.getTransactionId()), tranBean.getTranName(),
								"requestStruct", tranBean.getTranName(), "导入请求报文", loginLogId);
					}	
					tranDAL.Edit(tranBean);
					isEdit = true;
					break;
				}
			}
		}

		stream.close();

		if (!isEdit)
			response.getOutputStream().write(
					"error:未找到交易对应的excel表格，请检查导入文件!".getBytes("utf8"));
		else
			response.getOutputStream().write(";edit:更新成功!".getBytes("utf8"));
		response.flushBuffer();
	}

	
	/**
	 * 
	 * @param item
	 * @param request
	 * @param response
	 * @param flag     遇到EXCEL的SHEET名不符合规则是否报错
	 * @throws IOException
	 * @throws DownLoadException
	 * @throws Exception
	 */
	 static void UploadMultiTran(Workbook workbook,
			HttpServletRequest request, HttpServletResponse response, boolean flag)
			throws IOException, DownLoadException, Exception {
		

		String systemId = request.getParameter("systemId");
		int isClientSimu = Integer.parseInt(request.getParameter("isClientSimu"));
		Integer loginLogId = Integer.parseInt(request.getParameter("loginLogId"));
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sys = sysDAL.Get(Op.EQ("systemId", systemId));

		final ISystemConfig config = SystemConfigManager.getConfig(sys
				.getSystemName(), isClientSimu);
		List<MsgAttribute> reqAttrs = config.getReqFieldAttributes();
		List<MsgAttribute> resAttrs = config.getRespFieldAttributes();

		TransactionService ts = new TransactionService();
		List<Transaction> tranList = ts.GetTransDAO(systemId, isClientSimu);
		HashMap<String, Transaction> tranMap = new HashMap<String, Transaction>();
		for (Transaction tran : tranList) {
			//tranMap.put(tran.getTranCode(), tran);
			tranMap.put(tran.getTranName(), tran);
		}

		List<String> resultList = new ArrayList<String>();

		
		for (Sheet sheet : workbook.getSheets()) {
			String sheetTranName = "";
			boolean sheetIsRes = true;

			try {
				String[] strs = StringUtils.split(sheet.getName(), "|");
				if (strs.length > 1) {
					sheetTranName = strs[0].trim();
					sheetIsRes = strs[1].trim().toLowerCase().equals("out");
				} else{
					if(flag){
						sheetTranName = sheet.getName();
					}else{
						continue;  //不报错继续读下个EXCEL表
					}
				}
					

				MsgDocument doc = sheetIsRes ? ExcelSerializer.DeserializeStru(
						resAttrs, sheet) : ExcelSerializer.DeserializeStru(
						reqAttrs, sheet);
						
				String docStr = doc.get(0) == null ? "" : doc.toString();
				// 如果已存在该交易，则做更新操作，否则做新增操作
				if (tranMap.containsKey(sheetTranName)) { //修改如果存在相同交易名则更新
					Transaction tran = tranMap.get(sheetTranName);
					if (sheetIsRes)
						tran.setResponseStruct(docStr);
					else
						tran.setRequestStruct(docStr);

					//tran.setTranName(doc.getAttribute("tranName").getStr());
					tran.setDescription(doc.getAttribute("tranDesc").str);
					tran.setTransactionCategoryId(doc.getAttribute("transCateId").str);
					tran.setTranCode(doc.getAttribute("tranCode").str);
					ts.EditTran(tran, loginLogId);
					resultList.add("edit:" + sheet.getName());
				} else {
					Transaction tran = new Transaction();
					tran.setTranName(sheetTranName);
					tran.setTranCode(doc.getAttribute("tranCode").str);
					tran.setDescription(doc.getAttribute("tranDesc").str);
					tran.setTransactionCategoryId(doc.getAttribute("transCateId").str);
					tran.setScript("");
					tran.setIsClientSimu(isClientSimu);
					tran.setSystemId(systemId);
					if (sheetIsRes)
						tran.setResponseStruct(docStr);
					else
						tran.setRequestStruct(docStr);

					tran = ts.AddNewTran(tran, loginLogId);
					tranMap.put(tran.getTranName(), tran);
					resultList.add("newadd:" + sheet.getName());
				}
			} catch (Exception ex) {

				resultList.add("error:" + sheet.getName());
				ex.printStackTrace();
				continue;
			}
		}
		String msg = resultList.toString();
		response.getOutputStream().write(msg.getBytes("utf8"));
		response.flushBuffer();
	}

	public void init() throws ServletException {
	}

}
