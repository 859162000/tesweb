package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.JXLException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceFieldValue;
import com.dc.tes.data.model.CaseInstanceSqlValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.util.CaseResultUtil;

public class CaseResultExport extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4983186457248134293L;

	public CaseResultExport() {
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
		response.setCharacterEncoding("gb2312");

		try {
			String id = request.getParameter("ID");
			if (id == null) {
				throw new DownLoadException("无法获取执行日志ID，请与管理员联系。");
			}
			// String systemId = request.getParameter("systemId");
			IDAL<ExecuteLog> executeLogDAL = DALFactory
					.GetBeanDAL(ExecuteLog.class);
			ExecuteLog executeLog = executeLogDAL.Get(Op.EQ("id",
					Integer.parseInt(id)));
			if (executeLog == null) {
				throw new DownLoadException("无法获取执行日志ID，请与管理员联系。");
			}
			int executeLogID = executeLog.getId();
			IDAL<CaseInstance> caseInstanceDAL = DALFactory
					.GetBeanDAL(CaseInstance.class);
			List<CaseInstance> caseInsList = caseInstanceDAL.ListAll("caseNo",
					true, Op.EQ("executeLogId", executeLogID));
			exportCaseResult(caseInsList, executeLog, request, response);

		} catch (DownLoadException ex) {
			response.getOutputStream()
					.write(ex.getMessage().getBytes("gb2312"));
			response.flushBuffer();
			ex.printStackTrace();
		} catch (Exception ex) {
			response.getOutputStream().write(
					"error:导出案例结果发生异常".getBytes("gb2312"));
			response.flushBuffer();
			ex.printStackTrace();
		}

	}

	private void exportCaseResult(List<CaseInstance> caseInsList,
			ExecuteLog executeLog, HttpServletRequest request,
			HttpServletResponse response) throws Exception, DownLoadException {

		int sheetCount = 0;
		File file = new HelperService().CreateTempFile("Case_Result"
				+ executeLog.getExecuteBatchNo(), ".xls");
		FileOutputStream stream = new FileOutputStream(file);
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		String sheetName = "测试结果";
		CaseResultUtil.BuildCaseResultExcelFormat(workbook, sheetName,
				sheetCount);
		fillDataToExcel(workbook, sheetCount, caseInsList);

		workbook.write();
		workbook.close();
		response.getOutputStream().write(file.getName().getBytes("gb2312"));
		response.flushBuffer();
		return;

	}

	private static void fillDataToExcel(WritableWorkbook workbook,
			int sheetCount, List<CaseInstance> caseInsList) throws JXLException {
		// TODO Auto-generated method stub
		WritableSheet sheet = workbook.getSheet(sheetCount);
		int row = 1;
		int odd = 0;// 行与行进行颜色区分
		int bfCaseFlowId = -1;
		boolean isSucc = false;
		int totalCol = 7;
		int insertPos = 7;
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		for (CaseInstance caseInstance : caseInsList) {
			if (caseInstance.getCaseFlowInstance() != null) {
				if (caseInstance.getCaseFlowInstance().getId() != bfCaseFlowId) {
					odd = (odd + 1) % 2;
					bfCaseFlowId = caseInstance.getCaseFlowInstance().getId();
				}
			} else {
				odd = (odd + 1) % 2;
				bfCaseFlowId = -1;
			}
			if (caseInstance.getCasePassFlag() == 1) {
				isSucc = true;
			} else {
				isSucc = false;
			}
			WritableCellFormat wf = isSucc ? (odd == 1 ? caseResultUtil.NorWcf
					: caseResultUtil.NorOWcf) : caseResultUtil.FailWcf;
			sheet.addCell(new Label(0, row, caseInstance.getCaseNo(), wf));
			sheet.addCell(new Label(1, row, caseInstance.getCaseName(), wf));
			if (caseInstance.getCaseFlowInstance() != null) {
				sheet.addCell(new Label(2, row, caseInstance
						.getCaseFlowInstance().getCaseFlowNo(), wf));
				sheet.addCell(new Label(3, row, caseInstance
						.getCaseFlowInstance().getCaseFlowName(), wf));
			} else {
				sheet.addCell(new Label(2, row, "", wf));
				sheet.addCell(new Label(3, row, "", wf));
			}
			sheet.addCell(new Label(4, row, caseInstance.getCardNumber(), wf));
			sheet.addCell(new Label(5, row, caseInstance.getAmount(), wf));
			sheet.addCell(new Label(6, row, isSucc ? "Success" : "Fail",
					isSucc ? (odd == 1 ? caseResultUtil.SuccWcf
							: caseResultUtil.SuccOWcf) : caseResultUtil.ErrWcf));
			/*
			 * if(caseInstance.getExpectedField39().equals(caseInstance.getField39
			 * ())){ sheet.addCell(new Label(7, row,
			 * caseInstance.getExpectedField39(), wf)); sheet.addCell(new
			 * Label(8, row, caseInstance.getField39(), wf)); }else{
			 * sheet.addCell(new Label(7, row,
			 * caseInstance.getExpectedField39(), caseResultUtil.ExpectWcf));
			 * sheet.addCell(new Label(8, row, caseInstance.getField39(),
			 * caseResultUtil.RecWcf)); }
			 */
			IDAL<CaseParameterExpectedValue> caseExpValDAL = DALFactory
					.GetBeanDAL(CaseParameterExpectedValue.class);
			IDAL<CaseInstanceSqlValue> caseInsSqlValDAL = DALFactory
					.GetBeanDAL(CaseInstanceSqlValue.class);
			IDAL<CaseInstanceFieldValue> caseInsFieldValDAL = DALFactory
					.GetBeanDAL(CaseInstanceFieldValue.class);
			List<CaseParameterExpectedValue> caseEVList = caseExpValDAL
					.ListAll(Op.EQ("caseId", caseInstance.getCaseId()
							.toString()));
			for (CaseParameterExpectedValue obj : caseEVList) {
				boolean isExist = false;
				CaseInstanceSqlValue sqlValue = null;
				CaseInstanceFieldValue fieldValue = null;
				SystemDynamicParameter sysParam = obj.getTransParameter()
						.getSystemParameter();
				boolean isSqlParam = sysParam.getParameterType().equals("1") ? true
						: false;
				if (isSqlParam) {
					List<CaseInstanceSqlValue> lst = caseInsSqlValDAL.ListAll(
							Op.EQ("transParameter", obj.getTransParameter()),
							Op.EQ("caseInstanceId", caseInstance.getId()),
							Op.EQ("isCurrentStep", 1));
					if (lst != null && !lst.isEmpty()) {
						sqlValue = lst.get(0);
					}
				} else {
					List<CaseInstanceFieldValue> lst = caseInsFieldValDAL
							.ListAll(
									Op.EQ("transParameter",
											obj.getTransParameter()),
									Op.EQ("caseInstanceId",
											caseInstance.getId()));
					if (lst != null && !lst.isEmpty()) {
						fieldValue = lst.get(0);
					}
				}
				for (int i = 7; i < totalCol; i++) {
					if (sheet
							.getCell(i, 0)
							.getContents()
							.equals(obj.getTransParameter()
									.getSystemParameter().getName()
									+ "预期值")) {
						if (isSqlParam) {
							if (sqlValue != null) {
								if (obj.getExpectedValue().equalsIgnoreCase(
										sqlValue.getRealValue())) {
									sheet.addCell(new Label(i, row, obj
											.getExpectedValue(), wf));
									sheet.addCell(new Label(i + 1, row,
											sqlValue.getRealValue(), wf));
								} else {
									sheet.addCell(new Label(i, row, obj
											.getExpectedValue(),
											caseResultUtil.ExpectWcf));
									sheet.addCell(new Label(i + 1, row,
											sqlValue.getRealValue(),
											caseResultUtil.RecWcf));
								}
							} else {
								sheet.addCell(new Label(i, row, obj
										.getExpectedValue(),
										caseResultUtil.ExpectWcf));
								sheet.addCell(new Label(i + 1, row, "",
										caseResultUtil.RecWcf));
							}
							isExist = true;
							for (int j = i + 2; j < totalCol; j++) {
								if (sheet
										.getCell(j, 0)
										.getContents()
										.equals(obj.getTransParameter()
												.getSystemParameter()
												.getName()
												+ "查询语句")) {
									sheet.addCell(new Label(j, row,
											sqlValue != null ? sqlValue
													.getRealSql() : "", wf));
									break;
								}
							}
							break;
						} else {

							if (fieldValue != null) {
								if (obj.getExpectedValue().equalsIgnoreCase(
										fieldValue.getMsgFieldValue())) {
									sheet.addCell(new Label(i, row, obj
											.getExpectedValue(), wf));
									sheet.addCell(new Label(i + 1, row,
											fieldValue.getMsgFieldValue(), wf));
								} else {
									sheet.addCell(new Label(i, row, obj
											.getExpectedValue(),
											caseResultUtil.ExpectWcf));
									sheet.addCell(new Label(i + 1, row,
											fieldValue.getMsgFieldValue(),
											caseResultUtil.RecWcf));
								}
							} else {
								sheet.addCell(new Label(i, row, obj
										.getExpectedValue(),
										caseResultUtil.ExpectWcf));
								sheet.addCell(new Label(i + 1, row, "",
										caseResultUtil.RecWcf));
							}
							isExist = true;
						}
					}
				}
				if (!isExist) {
					sheet.insertColumn(insertPos);
					sheet.addCell(new Label(insertPos++, 0, obj
							.getTransParameter().getSystemParameter()
							.getName()
							+ "预期值", caseResultUtil.TitleWcf));
					sheet.insertColumn(insertPos);
					sheet.addCell(new Label(insertPos++, 0, obj
							.getTransParameter().getSystemParameter()
							.getName()
							+ "实际值", caseResultUtil.TitleWcf));
					if (isSqlParam) {
						sheet.addCell(new Label(totalCol + 2, 0, obj
								.getTransParameter().getSystemParameter()
								.getName()
								+ "查询语句", caseResultUtil.TitleWcf));
						totalCol = totalCol + 3;

						if (sqlValue != null) {
							if (obj.getExpectedValue().equalsIgnoreCase(
									sqlValue.getRealValue())) {
								sheet.addCell(new Label(insertPos - 2, row, obj
										.getExpectedValue(), wf));
								sheet.addCell(new Label(insertPos - 1, row,
										sqlValue.getRealValue(), wf));

							} else {
								sheet.addCell(new Label(insertPos - 2, row, obj
										.getExpectedValue(),
										caseResultUtil.ExpectWcf));
								sheet.addCell(new Label(insertPos - 1, row,
										sqlValue.getRealValue(),
										caseResultUtil.RecWcf));
							}
							sheet.addCell(new Label(totalCol - 1, row, sqlValue
									.getRealSql(), wf));
						} else {
							sheet.addCell(new Label(insertPos - 2, row, obj
									.getExpectedValue(),
									caseResultUtil.ExpectWcf));
							sheet.addCell(new Label(insertPos - 1, row, "",
									caseResultUtil.RecWcf));
							sheet.addCell(new Label(totalCol - 1, row, "", wf));
						}
					} else {
						totalCol += 2;
						if (fieldValue != null) {
							if (obj.getExpectedValue().equalsIgnoreCase(
									fieldValue.getMsgFieldValue())) {
								sheet.addCell(new Label(insertPos - 2, row, obj
										.getExpectedValue(), wf));
								sheet.addCell(new Label(insertPos - 1, row,
										fieldValue.getMsgFieldValue(), wf));

							} else {
								sheet.addCell(new Label(insertPos - 2, row, obj
										.getExpectedValue(),
										caseResultUtil.ExpectWcf));
								sheet.addCell(new Label(insertPos - 1, row,
										fieldValue.getMsgFieldValue(),
										caseResultUtil.RecWcf));
							}

						} else {
							sheet.addCell(new Label(insertPos - 2, row, obj
									.getExpectedValue(),
									caseResultUtil.ExpectWcf));
							sheet.addCell(new Label(insertPos - 1, row, "",
									caseResultUtil.RecWcf));
						}
					}
				}
			}
			row++;

			/*
			 * if(caseInstance.getExpectedRtnCod().equals(caseInstance.getReturnCode
			 * ())){ sheet.addCell(new Label(9, row,
			 * caseInstance.getExpectedRtnCod(), wf)); sheet.addCell(new
			 * Label(10, row, caseInstance.getReturnCode(), wf)); }else{
			 * sheet.addCell(new Label(9, row, caseInstance.getExpectedRtnCod(),
			 * CaseResultUtil.ExpectWcf)); sheet.addCell(new Label(10, row,
			 * caseInstance.getReturnCode(), CaseResultUtil.RecWcf)); }
			 * 
			 * sheet.addCell(new Label(11, row, caseInstance.getSpSql(), wf));
			 * sheet.addCell(new Label(12, row, caseInstance.getSpSqlResult(),
			 * wf)); sheet.addCell(new Label(13, row, caseInstance.getRpSql(),
			 * wf)); sheet.addCell(new Label(14, row,
			 * caseInstance.getRpSqlResult(), wf)); sheet.addCell(new Label(15,
			 * row, caseInstance.getAcSql1(), wf)); sheet.addCell(new Label(16,
			 * row, caseInstance.getAcSql1Result(), wf)); sheet.addCell(new
			 * Label(17, row, caseInstance.getAcSql2(), wf)); sheet.addCell(new
			 * Label(18, row, caseInstance.getAcSql2Result(),wf));
			 * sheet.addCell(new Label(19, row,
			 * caseInstance.getOtherFieldExpected(), wf)); sheet.addCell(new
			 * Label(20, row, caseInstance.getOtherFieldResult(), wf));
			 * sheet.addCell(new Label(21, row, caseInstance.getOtherSql(),
			 * wf)); sheet.addCell(new Label(22, row++,
			 * caseInstance.getOtherSqlResult(), wf));
			 */}

	}

}
