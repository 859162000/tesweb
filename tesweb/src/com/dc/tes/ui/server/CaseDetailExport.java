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
import jxl.write.WritableWorkbook;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;

public class CaseDetailExport extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4504016832014079325L;
	IDAL<CaseInstance> caseInstanceDAL = DALFactory.GetBeanDAL(CaseInstance.class);
	public CaseDetailExport() {
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
			String id = request.getParameter("id");
			if (id == null) {
				throw new DownLoadException("无法获取执行日志ID，请与管理员联系。");
			}
			
			//CaseInstance caseInstance = caseInstanceDAL.Get(Op.EQ("id",
			//		Integer.parseInt(id)));
			exportResult(id, request, response);

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

	private void exportResult(CaseInstance caseInstance,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, DownLoadException {
		// TODO Auto-generated method stub
		if(caseInstance.getCaseFlowInstance()==null){
			File file =new HelperService().CreateTempFile("["
					+ caseInstance.getCaseNo() + "]" , ".xls");
			FileOutputStream stream = new FileOutputStream(file);
			int sheetCount = 0;
			WritableWorkbook workbook = Workbook.createWorkbook(stream);
			CaseFlowResultExport.CaseToExcel(workbook, sheetCount, caseInstance);
			workbook.write();
			workbook.close();
			response.getOutputStream().write(file.getName().getBytes("gb2312"));
			response.flushBuffer();
			stream.close();
		}else{
			List<CaseInstance> caseInstances = caseInstanceDAL.ListAll("caseNo", true,
					Op.EQ("caseFlowInstance", caseInstance.getCaseFlowInstance()));
			int sheetCount = 0;
			File file = new HelperService().CreateTempFile("[" + 
					caseInstances.get(0).getCaseNo()+ "]" + caseInstances.get(0).getCaseFlowInstance()
							.getCaseFlowNo(), ".xls");
			FileOutputStream stream = new FileOutputStream(file);
			WritableWorkbook workbook = Workbook.createWorkbook(stream);
			CaseFlowResultExport.CaseFlowToExcel(workbook, sheetCount, caseInstances);
			workbook.write();
			workbook.close();
			response.getOutputStream().write(file.getName().getBytes("gb2312"));
			response.flushBuffer();
			stream.close();
		}
		
	}
	
	private void exportResult(String id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, DownLoadException {
		List<CaseInstance> caseInstances = caseInstanceDAL.ListAll("caseNo", true,
				Op.EQ("caseFlowInstance.id", Integer.parseInt(id)));
		int sheetCount = 0;
		File file = new HelperService().CreateTempFile("[" + 
				caseInstances.get(0).getCaseNo()+ "]" + caseInstances.get(0).getCaseFlowInstance()
						.getCaseFlowNo(), ".xls");
		FileOutputStream stream = new FileOutputStream(file);
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		CaseFlowResultExport.CaseFlowToExcel(workbook, sheetCount, caseInstances);
		workbook.write();
		workbook.close();
		response.getOutputStream().write(file.getName().getBytes("gb2312"));
		response.flushBuffer();
		stream.close();
	}
}
