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

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseDirectory;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.model.GWTCaseFlow;

public class UseCaseInfoExport extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4516310248291761963L;
	public UseCaseInfoExport(){
		super();
	}
	
	public void destroy() {
		super.destroy();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setCharacterEncoding("utf8");
		IDAL<CaseDirectory> caseDirectoryDAL = DALFactory.GetBeanDAL(CaseDirectory.class);
		IDAL<CaseFlow> caseFlowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		
		try {
			String systemId = req.getParameter("systemID");
			String caseFlowName = req.getParameter("caseFlowName");
			String designer = req.getParameter("designer");
			String caseFlowNo = req.getParameter("caseFlowNo");
			String startDate = req.getParameter("startDate");
			String endDate = req.getParameter("endDate");
			String pathId = req.getParameter("pathId");
			
			List<Op> ops = new ArrayList<Op>();
			ops.add(Op.EQ(GWTCaseFlow.N_SystemID, Integer.parseInt(systemId)));
			if(!caseFlowNo.isEmpty()){
				ops.add(Op.LIKE(GWTCaseFlow.N_CaseFlowNo, caseFlowNo.trim()));
			}
			if(!caseFlowName.isEmpty()){
				ops.add(Op.LIKE("caseFlowName", caseFlowName.trim()));
			}
			if(!designer.isEmpty()){
				ops.add(Op.LIKE(GWTCaseFlow.N_Designer, designer.trim()));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			if(!startDate.isEmpty()){//起始日期不为空				
				Date start = sdf.parse(startDate);
				ops.add(Op.GE(GWTCaseFlow.N_CreateTime, start));
			}
			if(!endDate.isEmpty()){//结束日期不为空
				Date end = sdf.parse(endDate);
				ops.add(Op.LE(GWTCaseFlow.N_CreateTime, end));
			}
			if(!pathId.isEmpty()){
				List<CaseDirectory> list = new ArrayList<CaseDirectory>();
				String[] ids = pathId.split("a");
				for(int i=0; i<ids.length; i++){
					CaseDirectory c = caseDirectoryDAL.Get(Op.EQ("id", Integer.parseInt(ids[i])));
					list.add(c);
				}
				list = UseCaseService.getAllDirectory(list);
				List<Integer> ints = new ArrayList<Integer>(); //构造子目录的ID集合，方便做IN查询
				for(CaseDirectory c : list){
					ints.add(c.getId());
				}
				ops.add(Op.IN("directoryId", ints));				
			}
			Op[] conditions = new Op[ops.size()];
			for(int i=0; i<ops.size(); i++){    //把集合类转成数组
				conditions[i] = ops.get(i);
			}
			List<CaseFlow> caseFlows = new ArrayList<CaseFlow>();
			caseFlows = caseFlowDAL.ListAll(GWTCaseFlow.N_CaseFlowNo, true, conditions);
			
			ExportUseCases(caseFlows, req, resp);

		} catch (DownLoadException ex) {
			resp.getOutputStream()
					.write(ex.getMessage().getBytes("utf8"));
			resp.flushBuffer();
			ex.printStackTrace();
		} catch (Exception ex) {
			resp.getOutputStream().write(
					"error:导出用例发生异常".getBytes("utf8"));
			resp.flushBuffer();
			ex.printStackTrace();
		}
	}

	private void ExportUseCases(List<CaseFlow> caseFlows,
			HttpServletRequest req, HttpServletResponse resp) throws IOException, WriteException {
		// TODO Auto-generated method stub
		File file =new HelperService().CreateTempFile("UseCaseExport-"
				+ (new SimpleDateFormat("yyyyMMdd")).format(new Date()), ".xls");
		FileOutputStream stream = new FileOutputStream(file);
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		DrawXlsStruct(workbook);
		FillDataToXls(workbook, caseFlows);
		workbook.write();
		workbook.close();
		resp.getOutputStream().write(file.getName().getBytes("gb2312"));
		resp.flushBuffer();
		stream.close();
	}

	private void FillDataToXls(WritableWorkbook workbook,
			List<CaseFlow> caseFlows) throws WriteException {
		// TODO Auto-generated method stub
		WritableFont wf = new WritableFont(WritableFont.createFont("宋体"), 10,
				WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat wcf = new WritableCellFormat(wf);
		wcf.setAlignment(jxl.format.Alignment.CENTRE);
		wcf.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
		wcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
		wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
		wcf.setWrap(true);
		WritableSheet sheet = workbook.getSheet(0);
		int i = 3;
		for(CaseFlow caseFlow: caseFlows){
			sheet.addCell(new Label(0, i, caseFlow.getCaseFlowPath(), wcf));
			sheet.addCell(new Label(1, i, caseFlow.getCaseFlowNo(), wcf));
			sheet.addCell(new Label(2, i, caseFlow.getCaseFlowName(), wcf));
			sheet.addCell(new Label(3, i, caseFlow.getDescription(), wcf));
			sheet.addCell(new Label(4, i, caseFlow.getPreConditions(), wcf));
			sheet.addCell(new Label(5, i, caseFlow.getCaseFlowStep(), wcf));
			sheet.addCell(new Label(6, i, caseFlow.getExpectedResult(), wcf));
			sheet.addCell(new Label(7, i, caseFlow.getPriority(), wcf));
			sheet.addCell(new Label(8, i, caseFlow.getCaseType(), wcf));
			sheet.addCell(new Label(9, i, caseFlow.getCaseProperty(), wcf));
			sheet.addCell(new Label(10, i, caseFlow.getDesigner(), wcf));
			sheet.addCell(new Label(11, i, caseFlow.getDesignTime(), wcf));
			sheet.addCell(new Label(12, i, caseFlow.getMemo(), wcf));
			i++;
		}
	}

	private void DrawXlsStruct(WritableWorkbook workbook) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		WritableSheet sheet = workbook.createSheet(
				"测试用例", 0);
		SetColumnSize(sheet);
		String imgPath = new HelperService().GetRootPath() + "dctheme/Image/cmbLogo.png";
		File imgFile = new File(imgPath);  
		// WritableImage(col, row, width, height, imgFile);    width / 64  ; height /17
		WritableImage image = new WritableImage(0, 0, 1, 1, imgFile);  
		sheet.addImage(image);  
		sheet.mergeCells(0, 1, 12, 1);
		WritableFont wf = new WritableFont(WritableFont.createFont("宋体"), 20,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat titleWcf = new WritableCellFormat(wf);
		titleWcf.setAlignment(jxl.format.Alignment.CENTRE);
		titleWcf.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
		titleWcf.setVerticalAlignment(VerticalAlignment.CENTRE);
		titleWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
		sheet.addCell(new Label(0, 1, "×××项目测试用例", titleWcf));
		wf = new WritableFont(WritableFont.createFont("宋体"), 10,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat ThWcf = new WritableCellFormat(wf);
		ThWcf.setAlignment(jxl.format.Alignment.LEFT);
		ThWcf.setVerticalAlignment(VerticalAlignment.CENTRE);
		ThWcf.setWrap(true);
		ThWcf.setBackground(jxl.format.Colour.LIGHT_TURQUOISE); // 设置单元格的背景颜色
		ThWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
		int i = 0;
		sheet.addCell(new Label(i++, 2, "路径", ThWcf));
		sheet.addCell(new Label(i++, 2, "用例编号（导案例时的名称）", ThWcf));
		sheet.addCell(new Label(i++, 2, "测试名称", ThWcf));
		sheet.addCell(new Label(i++, 2, "描述", ThWcf));
		sheet.addCell(new Label(i++, 2, "前置条件", ThWcf));
		sheet.addCell(new Label(i++, 2, "步骤描述", ThWcf));
		sheet.addCell(new Label(i++, 2, "预期结果", ThWcf));
		sheet.addCell(new Label(i++, 2, "优先级", ThWcf));
		sheet.addCell(new Label(i++, 2, "用例类型", ThWcf));
		sheet.addCell(new Label(i++, 2, "用例属性", ThWcf));
		sheet.addCell(new Label(i++, 2, "设计人", ThWcf));
		sheet.addCell(new Label(i++, 2, "设计日期", ThWcf));
		sheet.addCell(new Label(i++, 2, "备注", ThWcf));
		
				
	}
	private void SetColumnSize(WritableSheet sheet) {
		// TODO Auto-generated method stub
		int i = 0;
		try {
			sheet.setRowView(i, 850);//设置行的高度		 
			sheet.setColumnView(i++, 18); // 设置列的宽度
			sheet.setRowView(i, 600);
			sheet.setColumnView(i++, 16); // 设置列的宽度
			sheet.setRowView(i, 500);
			sheet.setColumnView(i++, 15); // 设置列的宽度
			sheet.setColumnView(i++, 18); // 设置列的宽度
			sheet.setColumnView(i++, 18); // 设置列的宽度
			sheet.setColumnView(i++, 20); // 设置列的宽度
			sheet.setColumnView(i++, 20); // 设置列的宽度
			sheet.setColumnView(i++, 8); // 设置列的宽度
			sheet.setColumnView(i++, 10); // 设置列的宽度
			sheet.setColumnView(i++, 10); // 设置列的宽度
			sheet.setColumnView(i++, 10);
			sheet.setColumnView(i++, 10);
			sheet.setColumnView(i++, 10);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
