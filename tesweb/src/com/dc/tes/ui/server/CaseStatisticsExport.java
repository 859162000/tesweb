package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseRunStatistics;
import com.dc.tes.data.model.CaseRunUserStats;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.util.CaseResultUtil;

public class CaseStatisticsExport extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -697250112494029412L;
	IDAL<CaseRunStatistics> caseRunStatDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
	IDAL<CaseRunUserStats> caseRunUserStatDAL = DALFactory.GetBeanDAL(CaseRunUserStats.class);
	public CaseStatisticsExport() {
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
			String id = request.getParameter("id");
			if (id == null) {
				throw new DownLoadException("无法获取系统ID，请与管理员联系。");
			}
			
			//CaseInstance caseInstance = caseInstanceDAL.Get(Op.EQ("id",
			//		Integer.parseInt(id)));
			exportCaseStatistics(id, request, response);

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

	private void exportCaseStatistics(String id, HttpServletRequest request,
			HttpServletResponse response) throws IOException, WriteException {
		// TODO Auto-generated method stub
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		File file =new HelperService().CreateTempFile("CaseStatistics" , ".xls");
		FileOutputStream stream = new FileOutputStream(file);
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		caseStatisticsToExcel(workbook,  id);
		workbook.write();
		workbook.close();
		response.getOutputStream().write(file.getName().getBytes("gb2312"));
		response.flushBuffer();
		stream.close();
	}

	private void caseStatisticsToExcel(WritableWorkbook workbook, String id) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		String[] ids = id.split("a");
		List<CaseRunStatistics> crslist = new ArrayList<CaseRunStatistics>();
		for(int i = 0; i < ids.length; i++){
			crslist.add(caseRunStatDAL.Get(Op.EQ("caseRunStatisticsId", Integer.parseInt(ids[i]))));
		}
		
		WritableSheet sheet = workbook.createSheet(
				"概要统计", 0);
		
		SetColumnWidth(sheet);
		setCaseRunStatData(sheet, crslist);
		WritableSheet sheet2 = workbook.createSheet(
				"详情统计", 1);
		SetColumnWidth(sheet2);
		setCaseRunUserStatData(sheet2, crslist);
		
	}
	

	private void setCaseRunStatData(WritableSheet sheet,
			List<CaseRunStatistics> crslist) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		setCaseRunStatTitle(sheet, 0);
		int currentRow = 1;		
		for(CaseRunStatistics stat : crslist){			
			setCaseRunStatRow(stat, sheet, currentRow);
			currentRow++;
		}
	}
	
	private void setCaseRunUserStatData(WritableSheet sheet,
			List<CaseRunStatistics> crslist) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		int currentRow = 0;
		for(CaseRunStatistics stat : crslist){
			List<CaseRunUserStats> userStatList = caseRunUserStatDAL.ListAll(
					Op.EQ("caseRunStatistics.caseRunStatisticsId", stat.getCaseRunStatisticsId()));
			setCaseRunStatTitle(sheet, currentRow++);
			setCaseRunStatRow(stat, sheet, currentRow++);
			setCaseRunUserStatTitle(sheet, currentRow++);
			for(CaseRunUserStats userStats : userStatList){
				setCaseRunUserStatsRow(userStats, sheet, currentRow++);
			}
			currentRow++;
		}
		
	}

	

	private void setCaseRunStatRow(CaseRunStatistics stat, WritableSheet sheet,
			int currentRow) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		int i = 0;
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		sheet.addCell(new Label(i++, currentRow, stat.getStatMonth(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, currentRow, stat.getStatStartDay(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, currentRow, stat.getStatEndDay(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalRunUserCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalRunCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalPassedCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, currentRow, stat.getCaseFlowPassRate(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalRunCaseCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedTransactionCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedCaseCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedSysParamCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedTransactionCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedCaseCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedSysParamCount(), caseResultUtil.Wcf));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		sheet.addCell(new Label(i++, currentRow, sdf.format(stat.getStatTime()), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, currentRow, stat.getMemo(), caseResultUtil.Wcf));
	}
	
	private void setCaseRunUserStatsRow(CaseRunUserStats stat,
			WritableSheet sheet, int currentRow) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		int i = 3;
		IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
		User user = userDAL.Get(Op.EQ("id", stat.getRunUserId().toString()));
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		sheet.addCell(new Label(i++, currentRow, user.getName(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalRunCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalPassedCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, currentRow, stat.getCaseFlowPassRate(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getTotalRunCaseCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedTransactionCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedCaseCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getCreatedSysParamCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedTransactionCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedCaseFlowCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedCaseCount(), caseResultUtil.Wcf));
		sheet.addCell(new Number(i++, currentRow, stat.getModifiedSysParamCount(), caseResultUtil.Wcf));
		sheet.addCell(new Label(i++, currentRow, stat.getMemo(), caseResultUtil.Wcf));
	}

	private void setCaseRunStatTitle(WritableSheet sheet, int row) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		int i = 0;
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		sheet.addCell(new Label(i++, row, "统计月份", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "开始日期", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "截止日期", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "执行用户总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "执行用例总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "通过用例总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "用例通过率", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "执行案例总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建交易个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建用例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建案例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建参数个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改交易个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改用例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改案例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改参数个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "统计时间", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "备注", caseResultUtil.TitleWcf));		
	}
	
	private void setCaseRunUserStatTitle(WritableSheet sheet, int row) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		int i = 3;
		CaseResultUtil caseResultUtil = new CaseResultUtil();
		sheet.addCell(new Label(i++, row, "用户", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "执行用例总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "通过用例总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "用例通过率", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "执行案例总数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建交易个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建用例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建案例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "新建参数个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改交易个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改用例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改案例个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "修改参数个数", caseResultUtil.TitleWcf));
		sheet.addCell(new Label(i++, row, "备注", caseResultUtil.TitleWcf));		

	}

	public static  void SetColumnWidth(WritableSheet sheet) {
		// TODO Auto-generated method stub
		int i = 0;
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		sheet.setColumnView(i++, 20); // 设置列的宽度
		sheet.setColumnView(i++, 15); // 设置列的宽度
		
	}
}
