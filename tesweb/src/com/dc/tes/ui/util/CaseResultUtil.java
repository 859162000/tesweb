package com.dc.tes.ui.util;

import java.util.List;

import jxl.CellView;
import jxl.JXLException;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


import com.dc.tes.exception.DownLoadException;

public class CaseResultUtil {

	public  WritableCellFormat TitleWcf;  //列名
	public  WritableCellFormat NorWcf; //正常
	public  WritableCellFormat Wcf; //正常加框
	public  WritableCellFormat NumWcf; //数字右对齐
	public  WritableCellFormat NorOWcf; //正常奇数行
	public  WritableCellFormat SuccWcf; //运行结果成功显绿色字体
	public  WritableCellFormat SuccOWcf; //运行结果成功显绿色字体奇数行
	public  WritableCellFormat FailWcf; //执行结果不符预期显蓝色
	public  WritableCellFormat ErrWcf; //执行结果Fail显红色字体
	public  WritableCellFormat ExpectWcf; //预期结果显绿色背景
	public  WritableCellFormat RecWcf; //实际接收结果显浅红色背景
	
	public CaseResultUtil() {
		// TODO Auto-generated constructor stub
	
		try{
					
			WritableFont wf = new WritableFont(WritableFont.createFont("宋体"), 10,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
			
			NorWcf = new WritableCellFormat(wf);
			NorWcf.setAlignment(jxl.format.Alignment.LEFT);
			NorWcf.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
			NorWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			Wcf = new WritableCellFormat(wf);
			Wcf.setAlignment(jxl.format.Alignment.LEFT);
			Wcf.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
			Wcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			NumWcf = new WritableCellFormat(wf);
			NumWcf.setAlignment(jxl.format.Alignment.RIGHT);
			NumWcf.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
			NumWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			NorOWcf = new WritableCellFormat(wf);
			NorOWcf.setAlignment(jxl.format.Alignment.LEFT);
			NorOWcf.setBackground(jxl.format.Colour.IVORY); // 设置单元格的背景颜色
			NorOWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			FailWcf = new WritableCellFormat(wf);
			FailWcf.setAlignment(jxl.format.Alignment.LEFT);
			FailWcf.setBackground(jxl.format.Colour.ICE_BLUE); // 设置单元格的背景颜色
			FailWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			RecWcf = new WritableCellFormat(wf);
			RecWcf.setAlignment(jxl.format.Alignment.LEFT);
			RecWcf.setBackground(jxl.format.Colour.CORAL); // 设置单元格的背景颜色
			RecWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			ExpectWcf = new WritableCellFormat(wf);
			ExpectWcf.setAlignment(jxl.format.Alignment.LEFT);
			ExpectWcf.setBackground(jxl.format.Colour.LIME); // 设置单元格的背景颜色	
			ExpectWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			WritableFont wf1 = new WritableFont(WritableFont.createFont("宋体"), 10,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.GREEN);
			SuccWcf = new WritableCellFormat(wf1);
			SuccWcf.setAlignment(jxl.format.Alignment.LEFT);
			SuccWcf.setBackground(jxl.format.Colour.WHITE);
			SuccWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			WritableFont wf3 = new WritableFont(WritableFont.createFont("宋体"), 10,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.GREEN);
			SuccOWcf = new WritableCellFormat(wf3);
			SuccOWcf.setAlignment(jxl.format.Alignment.LEFT);
			SuccOWcf.setBackground(jxl.format.Colour.IVORY);
			SuccOWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			WritableFont wf2 = new WritableFont(WritableFont.createFont("宋体"), 10,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.RED);
			ErrWcf = new WritableCellFormat(wf2);
			ErrWcf.setAlignment(jxl.format.Alignment.LEFT);
			ErrWcf.setBackground(jxl.format.Colour.ICE_BLUE);
			ErrWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			
			WritableFont wf34= new WritableFont(WritableFont.createFont("黑体"), 10,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
			TitleWcf = new WritableCellFormat(wf34); // 单元格定义
			TitleWcf.setBackground(jxl.format.Colour.TAN); // 设置单元格的背景颜色
			TitleWcf.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
			TitleWcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//设置垂直对齐方式
			TitleWcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN); //加边框
		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 构造案例批量导出EXCEL结构
	 * @param workbook
	 * @param sheetCount
	 */
	public static void BuildCaseResultExcelFormat(WritableWorkbook workbook,
			String sheetName, int sheetCount) throws JXLException{
		// TODO Auto-generated method stub
		
		try {
			WritableSheet sheet = workbook.createSheet(sheetName, sheetCount);
			   /**
			   * 定义单元格样式
			   */
			CaseResultUtil caseResultUtil = new CaseResultUtil();
		    sheet.setColumnView(0, 7); // 设置列的宽度
		    sheet.setColumnView(1, 10); // 设置列的宽度
		    sheet.setColumnView(2, 12); // 设置列的宽度
		    sheet.setColumnView(3, 12); // 设置列的宽度
		    sheet.setColumnView(4, 17); // 设置列的宽度
		    sheet.setColumnView(5, 10); // 设置列的宽度
		    sheet.setColumnView(6, 10); // 设置列的宽度
		    sheet.setColumnView(7, 14); // 设置列的宽度
		    sheet.setColumnView(8, 14); // 设置列的宽度
		    sheet.setColumnView(9, 14); // 设置列的宽度
		    sheet.setColumnView(10, 14); // 设置列的宽度		    
		    sheet.setColumnView(11, 14); // 设置列的宽度
		    sheet.setColumnView(12, 14); // 设置列的宽度
		    sheet.setColumnView(13, 14); // 设置列的宽度
		    sheet.setColumnView(14, 14); // 设置列的宽度
		    sheet.setColumnView(15, 14); // 设置列的宽度
		    sheet.setColumnView(16, 21); // 设置列的宽度
		    sheet.setColumnView(17, 21); // 设置列的宽度
		    sheet.setColumnView(18, 21); // 设置列的宽度
		    sheet.setColumnView(19, 21); // 设置列的宽度
		    sheet.setColumnView(20, 21); // 设置列的宽度
		    sheet.setColumnView(21, 21); // 设置列的宽度
		    sheet.setColumnView(22, 21); // 设置列的宽度
		    sheet.setColumnView(23, 21); // 设置列的宽度
		    sheet.setColumnView(24, 21); // 设置列的宽度
		    sheet.setRowView(0, 450); //设置行的高度
			/**
			 * 使用样式的单元格
			 */
			sheet.addCell(new Label(0, 0, "编号", caseResultUtil.TitleWcf)); // 普通的带有定义格式的单元格
			sheet.addCell(new Label(1, 0, "用例名称", caseResultUtil.TitleWcf));
			sheet.addCell(new Label(2, 0, "业务流编号", caseResultUtil.TitleWcf));
			sheet.addCell(new Label(3, 0, "业务流名称", caseResultUtil.TitleWcf));
			sheet.addCell(new Label(4, 0, "卡号", caseResultUtil.TitleWcf));
			sheet.addCell(new Label(5, 0, "交易金额", caseResultUtil.TitleWcf));
			sheet.addCell(new Label(6, 0, "测试结果", caseResultUtil.TitleWcf));
			/*sheet.addCell(new Label(7, 0, "39域预期结果", caseResultUtil.TitleWcf));
			sheet.addCell(new Label(8, 0, "39域实际结果", caseResultUtil.TitleWcf));
				sheet.addCell(new Label(9, 0, "返回码预期值", caseResultUtil.TitleWcf));
			
			sheet.addCell(new Label(10, 0, "返回码实际值", wcf));
			sheet.addCell(new Label(11, 0, "预期SQL【SP】", wcf));
			sheet.addCell(new Label(12, 0, "实际SQL【SP】结果", wcf));
			sheet.addCell(new Label(13, 0, "预期SQL【RP】", wcf));
			sheet.addCell(new Label(14, 0, "实际SQL【RP】结果", wcf));
			sheet.addCell(new Label(15, 0, "预期SQL【AC1】", wcf));
			sheet.addCell(new Label(16, 0, "实际SQL【AC1】结果", wcf));
			sheet.addCell(new Label(17, 0, "预期SQL【AC2】", wcf));
			sheet.addCell(new Label(18, 0, "实际SQL【AC2】结果", wcf));
			sheet.addCell(new Label(19, 0, "其它域的预期值", wcf));
			sheet.addCell(new Label(20, 0, "其它域的实际值", wcf));
			sheet.addCell(new Label(21, 0, "其它SQL预期值", wcf));
			sheet.addCell(new Label(22, 0, "其它SQL实际值", wcf));
			*/
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new DownLoadException("定义EXCEL格式异常，请联系管理员");
			
		}
	}
	
	public void writeTitleCell(WritableSheet sheet, int row, 
			int col, List<String> title) {
		
		for(int i=0; i<title.size(); i++) {	
			try {
				sheet.addCell(new Label(col++, row, title.get(i), TitleWcf));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void setTitleWidth(WritableSheet sheet, int col) {
		
		sheet.setColumnView(col++, 20);
		sheet.setColumnView(col++, 20);
		sheet.setColumnView(col++, 10);
		sheet.setColumnView(col++, 10);
		sheet.setColumnView(col++, 10);
		sheet.setColumnView(col++, 10);
		sheet.setColumnView(col++, 10);
		sheet.setColumnView(col++, 10);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 20);
		sheet.setColumnView(col++, 20);
		
	}
	
	public void setStatisticTitleWidth(WritableSheet sheet, int col) {
		try {
			sheet.mergeCells(col, 7, col+8, 7);
			WritableFont wf= new WritableFont(WritableFont.createFont("黑体"), 20,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		    WritableCellFormat cellFormat=new WritableCellFormat(wf);
		    cellFormat.setAlignment(jxl.format.Alignment.CENTRE);
		    cellFormat.setBackground(jxl.format.Colour.LIME);
			sheet.addCell(new Label(1,7,"执行集统计",cellFormat));
		} catch (Exception ex) {
			
		}
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col, new CellView());
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);
		sheet.setColumnView(col++, 15);

		
	}
	
}
