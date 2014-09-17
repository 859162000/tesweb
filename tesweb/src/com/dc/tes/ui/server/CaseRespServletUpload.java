package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.util.StreamUtils;

import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.ui.client.model.GWTCase;

public class CaseRespServletUpload extends HttpServlet {
	private static final Log log = LogFactory
			.getLog(CaseRespServletUpload.class);
	private static final long serialVersionUID = 1L;

	public CaseRespServletUpload() {
		super();

	}

	public void doGet(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException {
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {

			CaseService caseService = new CaseService();
			GWTCase gwtCaseInfo = new GWTCase();
			Case caseBean = new Case();

			Enumeration emu = req.getParameterNames();
			while (emu.hasMoreElements()) {
				String pName = emu.nextElement().toString();
				gwtCaseInfo.set(pName, req.getParameter(pName));
			}

			if (!gwtCaseInfo.IsNew())
				caseBean = caseService.GetCaseBean(gwtCaseInfo.GetCaseId());
				caseBean.setCaseName(gwtCaseInfo.GetCaseName());
				caseBean.setCaseNo(gwtCaseInfo.GetCaseNo());
				caseBean.setIsParseable(gwtCaseInfo.GetCaseParse());
				caseBean.setTransactionId(gwtCaseInfo.GetTransactionID());

				Transaction tran = new TransactionService().GetSingle(caseBean
						.getTransactionId());
			if (gwtCaseInfo.IsNew()) {
				if (gwtCaseInfo.GetCaseParse() == 1) {
				}
			} else {
			}

			//可解析情况下
			if (gwtCaseInfo.GetCaseParse() == 1) {
				if (gwtCaseInfo.IsNew()) {
					//发起端交易
					if (tran.getIsClientSimu() == 1) {
						caseBean.setRequestXml(tran.getRequestStruct());
					}
					//接收端交易
					else {
						// 从交易获得响应报文，这里不做任何的覆盖
						caseBean.setRequestXml(tran.getResponseStruct());
					}
				}
			} else {
				FileItemIterator iter = new ServletFileUpload()
						.getItemIterator(req);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					if (item.isFormField()) {
						continue;
					} else {
						if (!item.getName().isEmpty()) {
							InputStream stream = item.openStream();
							byte[] resData = StreamUtils.getBytes(stream);
							if (resData.length == 0)
								throw new IOException();
							stream.close();

							// 读取上传的文件
							caseBean.setRequestMsg(resData);
						}
						break;
					}
				}
			}

			if (gwtCaseInfo.IsNew() && tran.getIsClientSimu() == 1) {
				caseBean.setResponseXml(tran.getResponseStruct());
			}

			if (gwtCaseInfo.IsNew())
				caseService.AddCaseInfo(caseBean);
			else
				caseService.EditCaseInfo(caseBean);
		} catch (IOException ex) {
			log.error(ex, ex);
			ex.printStackTrace();
			
			resp.getOutputStream().write("案例数据上传失败，请确认该文件存在".getBytes());
			resp.flushBuffer();
			return;
		} catch (Exception ex) {
			log.error(ex, ex);
			ex.printStackTrace();
			resp.getOutputStream().write("遇到未处理的异常情况,信息保存失败".getBytes());
			resp.flushBuffer();
			return;
		}
		
		// 成功保存
		resp.getOutputStream().write("".getBytes());
		resp.flushBuffer();
	}
}
