package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ParameterDirectory;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.model.GWTParameterDirectory;

/**
 * 导出案例预期值
 * @author HO274208
 *
 */
public class CaseExpectValueExport extends HttpServlet {

	private static final long serialVersionUID = -6833287804153850757L;

	public CaseExpectValueExport(){
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
		
		resp.setCharacterEncoding("utf8");
		IDAL<TransactionDynamicParameter> tranParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		IDAL<CaseParameterExpectedValue> caseParamValueDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
		IDAL<ParameterDirectory> paramDirDAL = DALFactory.GetBeanDAL(ParameterDirectory.class);
		
		
		String caseID = req.getParameter("caseID");
		String tranID = req.getParameter("tranID");
		
		try {
		
			File file =new HelperService().CreateTempFile("UseCaseExport-"
					+ (new SimpleDateFormat("yyyyMMdd")).format(new Date()), ".xls");
			FileOutputStream stream = new FileOutputStream(file);
			WritableWorkbook workbook = Workbook.createWorkbook(stream);
			WritableSheet sheet = workbook.createSheet(
					"案例预期值", 0);
		
			WritableFont wf34= new WritableFont(WritableFont.createFont("黑体"), 12,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
			WritableCellFormat TitleWcf = new WritableCellFormat(wf34); // 单元格定义
			TitleWcf.setBackground(jxl.format.Colour.TAN); // 设置单元格的背景颜色
			TitleWcf.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
			TitleWcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//设置垂直对齐方式
			TitleWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN); //加边框
			sheet.setColumnView(0, 30);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			
			sheet.addCell(new Label(0,0,"路径",TitleWcf));		
			sheet.addCell(new Label(1,0,"参数描述",TitleWcf));
			sheet.addCell(new Label(2,0,"预期值",TitleWcf));
			sheet.addCell(new Label(3,0,"变量?(0/1)",TitleWcf));
			
			List<TransactionDynamicParameter> lst = tranParamDAL.ListAll(Op.EQ("transactionId", tranID));
		    int row = 1;
			for(TransactionDynamicParameter tranParam : lst) {
				int col = 0;
				SystemDynamicParameter sysParam = tranParam.getSystemParameter();
				
				String expectedValue = sysParam.getDefaultExpectedValue();
				CaseParameterExpectedValue caseParamValue = caseParamValueDAL.Get(Op.EQ("caseId", caseID),Op.EQ("transParameter", tranParam));
				int expectedValueType = 0;
				if(caseParamValue != null) {
					//案例预期值表的记录优先。
					expectedValue = caseParamValue.getExpectedValue();
					expectedValueType = caseParamValue.getExpectedValueType();
				}
				StringBuilder path = new StringBuilder();
				path.insert(0,"\\"+sysParam.getName());
				ParameterDirectory paramDir = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, sysParam.getDirectoryId()));;			
				while(paramDir !=null) {
					path.insert(0,"\\"+paramDir.getName());
					paramDir = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, paramDir.getParentDirId()));			
				}
				//写入路径
				sheet.addCell(new Label(col++,row,path.toString()));
				//参数描述
				sheet.addCell(new Label(col++,row,sysParam.getDesc()));
				//预期值
				sheet.addCell(new Label(col++,row,expectedValue));
				//变量？
				sheet.addCell(new Label(col++,row,String.valueOf(expectedValueType)));
				row++;
		    }
			
			workbook.write();
			workbook.close();
			resp.getOutputStream().write(file.getName().getBytes("gb2312"));
			resp.flushBuffer();
			stream.close();
			
		}  catch (DownLoadException ex) {
			resp.getOutputStream().write(ex.getMessage().getBytes("utf8"));
			resp.flushBuffer();
			ex.printStackTrace();
		} catch (Exception ex) {
			resp.getOutputStream().write(
					"error:导出案例预期值发生异常".getBytes("utf8"));
			resp.flushBuffer();
			ex.printStackTrace();
		}
			
	}
}
