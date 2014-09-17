package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.JXLException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceFieldValue;
import com.dc.tes.data.model.CaseInstanceSqlValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.util.CaseResultUtil;
import com.dc.tes.ui.util.ZipUtil;

public class CaseFlowResultExport extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7166702548670575626L;

	public CaseFlowResultExport() {
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

		try {
			String id = request.getParameter("executeLogId");
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
					true, Op.EQ("executeLogId", executeLogID)/*
															 * , Op.NE(
															 * "caseFlowInstance.id"
															 * , -1)
															 */);
			exportCaseResult(caseInsList, executeLog, request, response);
		} catch (DownLoadException ex) {
			response.getOutputStream()
					.write(ex.getMessage().getBytes("utf8"));
			response.flushBuffer();
			ex.printStackTrace();
		} catch (Exception ex) {
			response.getOutputStream().write(
					"error:导出案例结果发生异常".getBytes("utf8"));
			response.flushBuffer();
			ex.printStackTrace();
		}
	}

	private void exportCaseResult(List<CaseInstance> caseInsList,
			ExecuteLog executeLog, HttpServletRequest request,
			HttpServletResponse response) throws Exception, DownLoadException {
		// TODO Auto-generated method stub
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		File root = new File(new HelperService().GetRootPath() + "temp/"
				+ date.format(new Date()));
		root.mkdirs();
		List<CaseInstance> caseInstances = new ArrayList<CaseInstance>();
		while (!caseInsList.isEmpty()) {
			if (caseInsList.get(0).getCaseFlowInstance() == null) {
				File file = File.createTempFile("["
						+ caseInsList.get(0).getCaseNo() + "]"
						+ caseInsList.get(0).getCaseName(), ".xls", root);
				FileOutputStream stream = new FileOutputStream(file);
				int sheetCount = 0;
				WritableWorkbook workbook = Workbook.createWorkbook(stream);
				CaseToExcel(workbook, sheetCount, caseInsList.get(0));
				workbook.write();
				workbook.close();
				stream.close();
				caseInsList.remove(0);
			} else {
				Integer caseflowInsId = caseInsList.get(0)
						.getCaseFlowInstance().getId();
				for (CaseInstance caseInstance : caseInsList) {
					if(caseInstance.getCaseFlowInstance()==null){
						break;
					}
					if (caseInstance.getCaseFlowInstance().getId() == caseflowInsId) {
						caseInstances.add(caseInstance);
					} else
						break;
				}
				int sheetCount = 0;
				File file = File.createTempFile(caseInstances.get(0)
						.getCaseFlowInstance().getCaseFlowName()
						+ "["
						+ caseInstances.get(0).getCaseFlowInstance()
								.getCaseFlowNo() + "]", ".xls", root);
				FileOutputStream stream = new FileOutputStream(file);
				WritableWorkbook workbook = Workbook.createWorkbook(stream);
				CaseFlowToExcel(workbook, sheetCount, caseInstances);
				workbook.write();
				workbook.close();
				stream.close();
				for (CaseInstance caseIns : caseInstances) {
					caseInsList.remove(caseIns);
				}
				caseInstances.clear();
			}
		}
		String zipName = root.getPath() + ".zip";
		ZipUtil.ZipFile(root.getPath(), zipName);
		ZipUtil.DeleteDirFile(root.getPath());
		File file = new File(zipName);
		response.getOutputStream().write(file.getName().getBytes("utf8"));
		response.flushBuffer();
		return;
	}

	public static void CaseToExcel(WritableWorkbook workbook, int sheetCount,
			CaseInstance caseInstance) throws JXLException {
		// TODO Auto-generated method stub
		IDAL<CaseInstanceFieldValue> fieldValueDAL = DALFactory
				.GetBeanDAL(CaseInstanceFieldValue.class);
		IDAL<CaseInstanceSqlValue> sqlValueDAL = DALFactory
				.GetBeanDAL(CaseInstanceSqlValue.class);
		IDAL<CaseParameterExpectedValue> caseParamExpValDAL = DALFactory
				.GetBeanDAL(CaseParameterExpectedValue.class);
		IDAL<TransactionDynamicParameter> tranParamDAL = DALFactory
				.GetBeanDAL(TransactionDynamicParameter.class);
		WritableSheet sheet = workbook.createSheet(
				caseInstance.getCaseName(), sheetCount);
		SetColumnWidth(sheet, false);
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		sheet.addCell(new Label(0, 0, "编号", caseResultUtil.TitleWcf)); // 普通的带有定义格式的单元格
		sheet.addCell(new Label(1, 0, "用例名称", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(2, 0, "卡号", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(3, 0, "交易金额", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(4, 0, "测试结果", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(0, 1, caseInstance.getCaseNo(), caseResultUtil.Wcf));
		sheet.addCell(new Label(1, 1, caseInstance.getCaseName(), caseResultUtil.Wcf));
		sheet.addCell(new Label(2, 1, caseInstance.getCardNumber(), caseResultUtil.Wcf));
		sheet.addCell(new Label(3, 1, caseInstance.getAmount(), caseResultUtil.Wcf));
		sheet.addCell(new Label(4, 1, caseInstance.getCasePassFlag()==1?"Success" : "Fail", 
				caseInstance.getCasePassFlag()==1 ? caseResultUtil.SuccWcf : caseResultUtil.ErrWcf));
		int currentRow = 3;
		SetTitleCell(currentRow++, sheet, false);
		List<TransactionDynamicParameter> transParams = tranParamDAL
		.ListAll(Op.EQ("transactionId", caseInstance.getTransactionId().toString()));
		List<ParamRow> rows = new ArrayList<ParamRow>();
		for (TransactionDynamicParameter transParam : transParams) {
			if (!transParam.getSystemParameter().getDisplayFlag()
					.equals("1")) {
				continue;
			}
			SystemDynamicParameter sysParam = transParam
					.getSystemParameter();
			ParamRow paramRow = new ParamRow();
			paramRow.setCaseName(caseInstance.getCaseName());
			paramRow.setParamName(sysParam.getName());
			paramRow.setParamDesc(sysParam.getDesc());
			paramRow.setParamType(sysParam.getParameterType());
			CaseParameterExpectedValue caseParam = caseParamExpValDAL.Get(
					Op.EQ("caseId", caseInstance.getCaseId()
							.toString()),
					Op.EQ("transParameter", transParam));
			String defExpVal = transParam.getSystemParameter()
					.getDefaultExpectedValue();
			if (caseParam == null && defExpVal != null
					&& !defExpVal.isEmpty()) {
				paramRow.setExpVal(defExpVal);
			} else if (caseParam != null) {
				paramRow.setExpVal(caseParam.getExpectedValue());
			}
			if (sysParam.getParameterType().equals("1")) {
				CaseInstanceSqlValue caseInsSqlVal = sqlValueDAL
						.Get(Op.EQ("caseInstanceId", caseInstance.getId()), 
								Op.EQ("caseFlowStep", 0),
								Op.EQ("transParameter", transParam));
				if (caseInsSqlVal != null) {
					paramRow.setRealVal(caseInsSqlVal.getRealValue());
					paramRow.setSql(caseInsSqlVal.getRealSql());
				}
			} else if (sysParam.getParameterType().equals("0")) {
				CaseInstanceFieldValue caseInstanceFieldValue = fieldValueDAL
						.Get(Op.EQ("caseInstanceId", caseInstance.getId()),
								Op.EQ("transParameter",transParam));
				if (caseInstanceFieldValue != null) {
					paramRow.setRealVal(caseInstanceFieldValue
							.getMsgFieldValue());
					paramRow.setSql("");
				}
			}
			paramRow.setCurrentRow(currentRow++);
			rows.add(paramRow);
		}
		ParamRow.SetParamRow(rows, sheet, false);
		rows.clear();
	}

	public static void CaseFlowToExcel(WritableWorkbook workbook, int sheetCount,
			List<CaseInstance> caseInsList) throws JXLException {
		// TODO Auto-generated method stub
		IDAL<CaseInstanceFieldValue> fieldValueDAL = DALFactory
				.GetBeanDAL(CaseInstanceFieldValue.class);
		IDAL<CaseInstanceSqlValue> sqlValueDAL = DALFactory
				.GetBeanDAL(CaseInstanceSqlValue.class);
		IDAL<CaseParameterExpectedValue> caseParamExpValDAL = DALFactory
				.GetBeanDAL(CaseParameterExpectedValue.class);
		IDAL<TransactionDynamicParameter> tranParamDAL = DALFactory
				.GetBeanDAL(TransactionDynamicParameter.class);
		CaseFlowInstance caseFlowInstance = caseInsList.get(0)
				.getCaseFlowInstance();
		WritableSheet sheet = workbook.createSheet(
				caseFlowInstance.getCaseFlowName(), sheetCount);
		SetColumnWidth(sheet, true);

		CaseResultUtil caseResultUtil = new CaseResultUtil();
		sheet.addCell(new Label(0, 0, "业务流名称", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(1, 0, "案例序号", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(2, 0, "案例名称", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(3, 0, "卡号", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(4, 0, "交易金额", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(5, 0, "测试结果", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(0, 1, caseFlowInstance.getCaseFlowName(),
				caseResultUtil.Wcf));
		for (int i = 1; i <= caseInsList.size(); i++) {
			sheet.addCell(new Label(1, i, String.valueOf(i),
					caseResultUtil.NumWcf));
			sheet.addCell(new Label(2, i, caseInsList.get(i - 1).getCaseName(),
					caseResultUtil.Wcf));
			sheet.addCell(new Label(3, i, caseInsList.get(i - 1).getCardNumber(),
					caseResultUtil.Wcf));
			sheet.addCell(new Label(4, i, caseInsList.get(i - 1).getAmount(),
					caseResultUtil.Wcf));
			sheet.addCell(new Label(5, i, caseInsList.get(i - 1).getCasePassFlag()==1?"Success" : "Fail", 
				caseInsList.get(i - 1).getCasePassFlag()==1 ? caseResultUtil.SuccWcf : caseResultUtil.ErrWcf));
		}
		int currentRow = caseInsList.size() + 1;
		SetTitleCell(currentRow++, sheet, true);
		for (int i = 0; i < caseInsList.size(); i++) {
			sheet.addCell(new Label(0, currentRow, "第" + (i + 1) + "步",
					caseResultUtil.Wcf));
			List<ParamRow> rows = new ArrayList<ParamRow>();
			for (int j = 0; j < i; j++) {
				List<TransactionDynamicParameter> transParams = tranParamDAL
						.ListAll(Op.EQ("transactionId", caseInsList.get(j)
								.getTransactionId().toString()));
				sheet.mergeCells(1, currentRow, 9, currentRow);
				sheet.addCell(new Label(1, currentRow++, "第" + (j + 1)
						+ "步回溯结果：", caseResultUtil.Wcf));
				for (TransactionDynamicParameter transParam : transParams) {
					if (transParam.getSystemParameter().getRefetchFlag() != 1) {
						continue;
					}
					SystemDynamicParameter sysParam = transParam
							.getSystemParameter();
					ParamRow paramRow = new ParamRow();
					paramRow.setCaseName(caseInsList.get(j).getCaseName());
					paramRow.setParamName(sysParam.getName());
					paramRow.setParamDesc(sysParam.getDesc());
					paramRow.setParamType(sysParam.getParameterType());
					CaseParameterExpectedValue caseParam = caseParamExpValDAL
							.Get(Op.EQ("caseId", caseInsList.get(j).getCaseId()
									.toString()),
									Op.EQ("transParameter", transParam));
					String defExpVal = transParam.getSystemParameter()
							.getDefaultExpectedValue();
					if (caseParam == null && defExpVal != null
							&& !defExpVal.isEmpty()) {
						paramRow.setExpVal(defExpVal);
					} else if (caseParam != null) {
						paramRow.setExpVal(caseParam.getExpectedValue());
					}

					CaseInstanceSqlValue caseInsSqlVal = sqlValueDAL
							.Get(Op.EQ("caseInstanceId", caseInsList.get(j)
									.getId()), Op.EQ("caseFlowStep", i), Op.EQ(
									"transParameter", transParam),
									Op.EQ("isCurrentStep", 0));
					if (caseInsSqlVal != null) {
						paramRow.setRealVal(caseInsSqlVal.getRealValue());
						paramRow.setSql(caseInsSqlVal.getRealSql());
					}
					paramRow.setCurrentRow(currentRow++);
					rows.add(paramRow);
				}
				ParamRow.SetParamRow(rows, sheet, true);
				rows.clear();
			}

			sheet.mergeCells(1, currentRow, 9, currentRow);
			sheet.addCell(new Label(1, currentRow++, "第" + (i + 1) + "步执行结果：",
					caseResultUtil.Wcf));
			List<TransactionDynamicParameter> transParams = tranParamDAL
					.ListAll(Op.EQ("transactionId", caseInsList.get(i)
							.getTransactionId().toString()));
			for (TransactionDynamicParameter transParam : transParams) {
				if (!transParam.getSystemParameter().getDisplayFlag()
						.equals("1")) {
					continue;
				}
				SystemDynamicParameter sysParam = transParam
						.getSystemParameter();
				ParamRow paramRow = new ParamRow();
				paramRow.setCaseName(caseInsList.get(i).getCaseName());
				paramRow.setParamName(sysParam.getName());
				paramRow.setParamDesc(sysParam.getDesc());
				paramRow.setParamType(sysParam.getParameterType());
				CaseParameterExpectedValue caseParam = caseParamExpValDAL.Get(
						Op.EQ("caseId", caseInsList.get(i).getCaseId()
								.toString()),
						Op.EQ("transParameter", transParam));
				String defExpVal = transParam.getSystemParameter()
						.getDefaultExpectedValue();
				if (caseParam == null && defExpVal != null
						&& !defExpVal.isEmpty()) {
					paramRow.setExpVal(defExpVal);
				} else if (caseParam != null) {
					paramRow.setExpVal(caseParam.getExpectedValue());
				}
				if (sysParam.getParameterType().equals("1")) {
					CaseInstanceSqlValue caseInsSqlVal = sqlValueDAL
							.Get(Op.EQ("caseInstanceId", caseInsList.get(i)
									.getId()), Op.EQ("caseFlowStep", i), Op.EQ(
									"transParameter", transParam),
									Op.EQ("isCurrentStep", 1));
					if (caseInsSqlVal != null) {
						paramRow.setRealVal(caseInsSqlVal.getRealValue());
						paramRow.setSql(caseInsSqlVal.getRealSql());
					}
				} else if (sysParam.getParameterType().equals("0")) {
					CaseInstanceFieldValue caseInstanceFieldValue = fieldValueDAL
							.Get(Op.EQ("caseInstanceId", caseInsList.get(i)
									.getId()), Op.EQ("transParameter",
									transParam));
					if (caseInstanceFieldValue != null) {
						paramRow.setRealVal(caseInstanceFieldValue
								.getMsgFieldValue());
						paramRow.setSql("");
					}
				}
				paramRow.setCurrentRow(currentRow++);
				rows.add(paramRow);

			}
			ParamRow.SetParamRow(rows, sheet, true);
			currentRow++;
		}
		sheet.addCell(new Label(0, currentRow, "业务流通过与否",
				caseResultUtil.TitleWcf));
		sheet.addCell(new Label(1, currentRow, caseInsList.get(0)
				.getCaseFlowInstance().getCaseFlowPassFlag() == 1 ? "是" : "否",
				caseResultUtil.Wcf));

	}

	public static  void SetTitleCell(int currentRow, WritableSheet sheet, boolean isCaseFlow) throws JXLException{
		// TODO Auto-generated method stub
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		int i = 0;
		if(isCaseFlow)
			sheet.addCell(new Label(i++, currentRow, "执行步骤", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "序号", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "案例名称", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "参数名称", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "参数描述", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "参数类型", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "预期值", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "实际值", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow, "是否一致", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, currentRow++, "查询语句",
				caseResultUtil.TitleWcf));
	}

	public static  void SetColumnWidth(WritableSheet sheet, boolean isCaseFlow) {
		// TODO Auto-generated method stub
		int i = 0;
		if(isCaseFlow)
			sheet.setColumnView(i++, 18); // 设置列的宽度
		sheet.setColumnView(i++, 10); // 设置列的宽度
		sheet.setColumnView(i++, 25); // 设置列的宽度
		sheet.setColumnView(i++, 20); // 设置列的宽度
		sheet.setColumnView(i++, 20); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 20); // 设置列的宽度
		sheet.setColumnView(i++, 20); // 设置列的宽度
		sheet.setColumnView(i++, 10); // 设置列的宽度
		sheet.setColumnView(i++, 25); // 设置列的宽度
	}

}

class ParamRow {
	private String caseName;
	private String paramName;
	private String paramDesc;
	private String paramType;
	private String expVal;
	private String realVal;
	private String sql;
	private int currentRow;
	private WritableSheet sheet;

	public ParamRow() {
	}

	public ParamRow(String caseName, String paramName, String paramDesc,
			String paramType, String expVal, String realVal, String sql,
			int currentRow) {
		this.caseName = caseName;
		this.paramName = paramName;
		this.paramDesc = paramDesc;
		this.paramType = paramType;
		this.expVal = expVal;
		this.realVal = realVal;
		this.sql = sql;
		this.currentRow = currentRow;
	}

	public static void SetParamRow(List<ParamRow> list, WritableSheet sheet, boolean isCaseFlow) {
		try {
			int i = 1;
			for (ParamRow paramRow : list) {
				SetParamRow(i++, paramRow, sheet, isCaseFlow);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void SetParamRow(int sequence, ParamRow paramRow,
			WritableSheet sheet, boolean isCaseFlow) throws JXLException {
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		int i;
		if(isCaseFlow)
			i=1;
		else {
			i=0;
		}
		sheet.addCell(new Label(i++, paramRow.currentRow, String
				.valueOf(sequence), caseResultUtil.NumWcf));
		sheet.addCell(new Label(i++, paramRow.currentRow, paramRow.getCaseName(),
				caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, paramRow.currentRow,
				paramRow.getParamName(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, paramRow.currentRow,
				paramRow.getParamDesc(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, paramRow.currentRow, getTypeCHS(paramRow
				.getParamType()), caseResultUtil.Wcf)); // 报文类型需转为中文描述
		if (paramRow.getExpVal() == null && paramRow.getRealVal() == null) {
			sheet.addCell(new Label(i, paramRow.currentRow, "",
					caseResultUtil.Wcf));
			sheet.addCell(new Label(i+1, paramRow.currentRow, "",
					caseResultUtil.Wcf));
			sheet.addCell(new Label(i+2, paramRow.currentRow, "",
					caseResultUtil.Wcf));
		} else if (paramRow.getExpVal() != null
				&& paramRow.getExpVal().equalsIgnoreCase(paramRow.getRealVal())) {
			sheet.addCell(new Label(i+2, paramRow.currentRow, "是",
					caseResultUtil.SuccWcf));
			sheet.addCell(new Label(i, paramRow.currentRow, paramRow
					.getExpVal(), caseResultUtil.Wcf));
			sheet.addCell(new Label(i+1, paramRow.currentRow, paramRow
					.getRealVal() != null ? paramRow.getRealVal() : "",
					caseResultUtil.Wcf));
		} else {
			sheet.addCell(new Label(i+2, paramRow.currentRow, "否",
					caseResultUtil.ErrWcf));
			sheet.addCell(new Label(i, paramRow.currentRow, paramRow
					.getExpVal() != null ? paramRow.getExpVal() : "",
					caseResultUtil.ExpectWcf));
			sheet.addCell(new Label(i+1, paramRow.currentRow, paramRow
					.getRealVal() != null ? paramRow.getRealVal() : "",
					caseResultUtil.RecWcf));
		}
		sheet.addCell(new Label(i+3, paramRow.currentRow++,
				paramRow.getSql() != null ? paramRow.getSql() : "",
				caseResultUtil.Wcf));
	}

	public static String getTypeCHS(String type) {
		switch (Integer.parseInt(type)) {
		case 0:
			return "报文参数";
		case 1:
			return "SQL参数";
		case 2:
			return "交易数据类参数";
		case 3:
			return "函数处理类参数";
		case 4:
			return "条件分支类参数";
		default:
			return type;
		}

	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getExpVal() {
		return expVal;
	}

	public void setExpVal(String expVal) {
		this.expVal = expVal;
	}

	public String getRealVal() {
		return realVal;
	}

	public void setRealVal(String realVal) {
		this.realVal = realVal;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getParamName() {
		return paramName;
	}

	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public WritableSheet getSheet() {
		return sheet;
	}

	public void setSheet(WritableSheet sheet) {
		this.sheet = sheet;
	}

}
