package com.dc.tes.ui.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.dc.tes.dom.DefaultForEachVisitor;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.util.DocBuilder;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.msg.util.Value;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.type.Pair;
import com.dc.tes.util.type.Wrapper;

/**
 * 工具类 负责报文结构和案例数据与Excel之间的转换
 * 
 * @author lijic
 * @modify scckobe 从原有系统中加入两个函数ToExcel，ToSheet；并做了适当调整
 */
public class ExcelSerializer {
	/**
	 * 将一篇报文结构保存到Excel的一个Sheet中
	 * @param attrList 结构的列属性集合
	 * @param sheet 保存数据的Sheet
	 * @param stru 报文结构
	 * @throws JXLException 
	 */
	public static Set<String> SerializeStru(List<MsgAttribute> attrList, final WritableSheet sheet, MsgDocument stru) throws JXLException {

//		Set<String> attrNameSet = calcAttributes(stru);
		//注释原因：
		//1)不影响后续语句正确性
		//2)针对：当从界面新建交易的时候， 报文结构为空字符串
		//	这时候我要导出这个新建的交易
		//	那么这句话肯定有问题
//		if(attrList.size() != attrNameSet.size()) 
//			throw new RuntimeException("报文结构的属性列不一致"); 
		
		sheet.addCell(new Label(0, 0, "交易码"));
		sheet.addCell(new Label(1, 0, stru.getAttribute("tranCode").str));
		sheet.addCell(new Label(0, 1, "交易描述"));
		sheet.addCell(new Label(1, 1, stru.getAttribute("tranDesc").str));
		sheet.addCell(new Label(0, 2, "交易类别"));
		sheet.addCell(new Label(1, 2, stru.getAttribute("transCateId").str));
		sheet.addCell(new Label(2, 2, "(1:POS类交易; 2:ATM类交易; 3:转账类交易; 4:其它类交易)"));

		//按照参数1初始化Excel中属性列名称
		LinkedHashSet<String> attrTitleSet = new LinkedHashSet<String>();
		LinkedHashSet<String> attrSet = new LinkedHashSet<String>();
		for (MsgAttribute attr : attrList) {
			attrTitleSet.add(attr.getName() + "|" + attr.getDisplayName());
			attrSet.add(attr.getName());
		}				
		final Set<String> attrs = attrSet;
		writeTitleRow(sheet, attrTitleSet); 

		WriteStruct(attrs,sheet,stru,4);
		return attrs;
	}
	
	private static void WriteStruct(final Set<String> attrs, final WritableSheet sheet, MsgItem item,
			final int startRow) throws JXLException
	{
		WriteStruct(attrs, sheet, item, startRow, false,"");
	}
	
	private static void WriteStruct(final Set<String> attrs, final WritableSheet sheet, MsgItem item,
			final int startRow,final boolean insert,final String ingoreChar) throws JXLException
	{
		if(!(item instanceof MsgStruct))
		{
			sheet.insertRow(startRow);
			writeItemRow(sheet, item, attrs, startRow);
			return;
		}
		
		MsgStruct stru = (MsgStruct)item;
		stru.ForEach(new DefaultForEachVisitor() {
			int row = startRow;
			private void AddRow(boolean setIngore)
			{
				if(insert)
				{
					sheet.insertRow(row);
					try
					{
						int col = sheet.getColumns();
						int begin = attrs.size() + 2;
						if(setIngore)
							for(;begin < col;begin++)
								sheet.addCell(new Label(begin, row, ingoreChar));
					}
					catch (Exception e) {
						
					}
				}
			}
			
			@Override
			public void StruStart(MsgStruct stru) {
				try {
					AddRow(false);
					sheet.addCell(new Label(0, row, "["));
					writeItemRow(sheet, stru, attrs, row);
					row++;
				} catch (JXLException ex) {
					throw new RuntimeException(ex);
				}
			}

			@Override
			public void StruEnd(MsgStruct stru) {
				try {
					AddRow(false);
					sheet.addCell(new Label(0, row, "]"));
					sheet.addCell(new Label(1, row, stru.getAttribute("name").getStr()));
					row++;
				} catch (JXLException ex) {
					throw new RuntimeException(ex);
				}
			}

			@Override
			public void Field(MsgField field) {
				try {
					AddRow(true);
					writeItemRow(sheet, field, attrs, row);
					row++;
				} catch (JXLException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
	}

	/**
	 * 从一篇Sheet中读取报文结构
	 * @param attrList 结构的列属性集合
	 * @param sheet 保存数据的Sheet
	 * @return 读取出的报文结构 tranName属性[r1c1]为交易名称 tranDesc属性[r2c1]为交易描述
	 * @throws JXLException 
	 */
	public static MsgDocument DeserializeStru(List<MsgAttribute> attrList, Sheet sheet) throws JXLException {
		DocBuilder builder = new DocBuilder();

		//区分属性列和值列:空列
		List<String> attrNameListFromExcel = readTitleRow(attrList, sheet);	
		
		for (int row = 4; row < sheet.getRows(); row++) {//4: 表示从数据行开始解析
			String sign = sheet.getCell(0, row).getContents();
			Map<String,String> simpleAttr = readItemRow(attrList, sheet, attrNameListFromExcel, row);
			//scckobe 处理空行
			if(simpleAttr == null)
				continue;
			
			// 判断该行类型
			if (sign.equals("["))
				// 结构起始
				builder.BeginStru(simpleAttr);
			else if (sign.equals("]"))
				// 结构结束
				builder.EndStru();
			else if (sign.length() == 0){
				// 域
				String value = "";
				if(simpleAttr.containsKey("defaultValue")) {
					value = simpleAttr.get("defaultValue");
				}
				builder.Field(value, simpleAttr);
			}
			else
				// 意外的类型
				throw new UnsupportedOperationException("xls文件格式错误[行" + row + "列0]：未知的标记[" + sign + "]");
		}

		MsgDocument doc = builder.Export();
		doc.setAttribute("tranCode", new Value(sheet.getCell(1, 0).getContents()));
		doc.setAttribute("tranDesc", new Value(sheet.getCell(1, 1).getContents()));
		doc.setAttribute("transCateId", new Value(sheet.getCell(1, 2).getContents()));

		return doc;
	}

	/**
	 * 将一些案例保存到Excel的一个Sheet中
	 * @param attrList 结构的列属性集合
	 * @param sheet 保存数据的Sheet
	 * @param stru 这些案例对应的报文结构
	 * @param cases 案例名称与案例数据的Map
	 * @throws JXLException 
	 * 
	 * @author guhb
	 */
	public static void SerializeCases(List<MsgAttribute> attrList, final WritableSheet sheet, 
			MsgDocument stru, Map<String, MsgDocument> cases) throws JXLException {
		final Set<String> attrs = SerializeStru(attrList, sheet, stru);	
		
		LinkedHashSet<String> attrSet = new LinkedHashSet<String>();
		for (MsgAttribute attr : attrList) {
			attrSet.add(attr.getName());
		}
		
		//在“结构标记列+属性列+空列”之后
		final int startCasePos =  attrList.size() + 2; 
		int offset = 0;
		
		for (String caseName : cases.keySet()) {
			final int curCasePos = startCasePos + offset++; 
			
			MsgDocument caseDoc = cases.get(caseName);
			
			sheet.addCell(new Label(curCasePos, 2, caseName)); //添加案例名称
			
			//遍历案例数据
			caseDoc.ForEach(new DefaultForEachVisitor() {
				int row= 4; //跳过前三行(非数据)
				MsgItem lastMstItem = null;
				@Override
				public void StruStart(MsgStruct stru) {
					Stru(stru);
				}

				@Override
				public void StruEnd(MsgStruct stru) {
					Stru(stru);
				}
				
				private void Stru(MsgStruct stru) {
					try {
						StructCompare(stru);
						row++;
						lastMstItem = stru;
					} catch (JXLException ex) {
						throw new RuntimeException(ex);
					}
				}

				@Override
				public void Field(MsgField field) {
					try {
						FieldCompare(field,field.value(),"#");
						lastMstItem = field;
						row++;
					} catch (JXLException ex) {
						throw new RuntimeException(ex);
					}
				}
				
				private void StructCompare(MsgItem item) throws JXLException
				{
					String curItemName = getName(item);
					boolean isStructEnd = sheet.getCell(0, row).getContents().equalsIgnoreCase("]");
//					if(!isStructEnd && lastMstItem != null && curItemName.compareTo(getName(lastMstItem)) == 0)
//						WriteStruct(attrs,sheet,item,row,true,"#");
				}
				
				private void FieldCompare(MsgItem item,String value,String ingoreValue) throws JXLException
				{
					String curItemName = getName(item);
					String sheetItemName = sheet.getCell(1, row).getContents();
					//与当前行名称相同
					if(curItemName.compareTo(sheetItemName) == 0)
						sheet.addCell(new Label(curCasePos, row, value));
					//数组，与上一行的名称相同  1）属性拷贝  2）之前案例填空  3）填值
					else if(lastMstItem != null && curItemName.compareTo(getName(lastMstItem)) == 0)
					{
						WriteStruct(attrs,sheet,item,row,true,"#");
						sheet.addCell(new Label(curCasePos, row, value));
						//之前的行用空值符号填写
						for(int i = startCasePos-1; i< curCasePos;i++)
							sheet.addCell(new Label(i, row, ingoreValue));
					}
					else
						sheet.addCell(new Label(curCasePos, row, ingoreValue));
				}
				
				private String getName(MsgItem item)
				{
					return item.getAttribute("name").getStr();
				}
			});	
			
			//默认多加一列
			int maxRowIndex = sheet.getRows();
			for(int i = 4; i< maxRowIndex; i++)
				if(sheet.getCell(0, i).getContents().isEmpty())
					sheet.addCell(new Label(curCasePos + 1, i, "#"));
		}
		
		//清除最后一列，为了能实现数组忽略值往后能填写
		int col = sheet.getColumns();
		sheet.removeColumn(col-1);
	}

	/**
	 * 从一篇Sheet中读取案例名称列表
	 * @param sheet 保存数据的Sheet
	 * @return 案例名称列表
	 */
	public static ArrayList<String> ListCaseNames(Sheet sheet) {
		boolean attrSign = true;
		ArrayList<String> caseNames = new ArrayList<String>();
		for (int c = 1; c < sheet.getColumns(); c++) {
			String cText = sheet.getCell(c, 2).getContents();
			if (cText == null || cText.length() == 0)
				attrSign = false;
			else if (!attrSign)
				caseNames.add(cText);
		}
		return caseNames;
	}
	
	public static boolean IsCaseExist(Sheet sheet,String caseName) {
		List<String> caseNames = ListCaseNames(sheet);
		for(String name: caseNames)
			if(name.compareTo(caseName) == 0)
				return true;
		
		return false;
	}

	/**
	 * 从一篇Sheet中读取案例列表
	 * @param sheet 保存数据的Sheet
	 * @return 案例列表
	 * @throws JXLException \
	 */
	public static Map<String, MsgDocument> DeserializeCases(Sheet sheet) throws JXLException {
		
		LinkedHashMap<String, MsgDocument> casesMap = new LinkedHashMap<String, MsgDocument>();
		
		//名称属性的行号
		int spaceIndex = 0;
		
		//区分属性列和值列：空列
		LinkedList<String> attrs = new LinkedList<String>();
		for (int c = 1; c < sheet.getColumns(); c++) {
			String attrName = sheet.getCell(c, 3).getContents().split("[|]")[0]; //获得列名称
			if (attrName == null || attrName.length() == 0)
			{
				spaceIndex = c;
				break;
			} else {
				attrs.add(attrName);
			}
		}
		
		final int dataRowStartPos = 4; //数据行位置
		final int caseDataColStartPos = spaceIndex + 1;//在“结构标记列+属性列+空列”之后
		
		for(int col = caseDataColStartPos; col < sheet.getColumns(); col++){

			String caseName = sheet.getCell(col, dataRowStartPos-2).getContents();
			
			//scckobe：如果案例名称为空，不跳过   方面写案例   by ljs
			if(caseName == null || caseName.isEmpty())
				caseName = " ";
			
			//解析案例数据列
			DocBuilder builder = new DocBuilder();
			
			for (int row = dataRowStartPos; row < sheet.getRows(); row++) {
				
				String sign = sheet.getCell(0, row).getContents().trim();
				Map<String,String> simpleAttr = readItemRow(sheet,attrs,row);
				
				//scckobe 处理空行
				if(simpleAttr.get("name") == "")
					continue;
				
				// 判断该行类型
				// 结构起始
				if (sign.equals("["))
					builder.BeginStru(simpleAttr);
				// 结构结束
				else if (sign.equals("]"))
				{
					builder.EndStru(true);
				}
				// 域
				else if (sign.length() == 0){
					String value = sheet.getCell(col, row).getContents();
					//忽略
					if(value.equalsIgnoreCase("#"))
						continue;
					builder.Field(value, simpleAttr);
				}
				else
					// 意外的类型
					throw new UnsupportedOperationException("xls文件数据格式错误[行" + row + "列0]：未知的标记[" + sign + "]");
			}

			MsgDocument doc = builder.Export();
			doc.setAttribute("tranCode", new Value(sheet.getCell(0, 0).getContents()));
			doc.setAttribute("tranDesc", new Value(sheet.getCell(0, 1).getContents()));
			doc.setAttribute("transCateId", new Value(sheet.getCell(0, 2).getContents()));
			
			casesMap.put(caseName, doc);
		}

		return casesMap;		
	}

	/**
	 * 将给定的<输入报文, 输出报文>列表导出成excel表格
	 * 
	 * @param trans
	 *            一个<输出报文, 输出报文>的列表 某个交易没有输入或输出报文，则对应的对象为null
	 * @param streamXls
	 *            指向一个有效的xls文件的流
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void toExcel(Map<String, Pair<MsgDocument, MsgDocument>> trans, 
			OutputStream streamXls, ISystemConfig config, boolean writeData) 
	throws IOException, WriteException {
		WritableWorkbook workbook = Workbook.createWorkbook(streamXls);

		// 整理列标头
		List<MsgAttribute> attsIn = new ArrayList<MsgAttribute>();
		List<MsgAttribute> attsOut = new ArrayList<MsgAttribute>();

		for (MsgAttribute attribute : config.getReqFieldAttributes())
			attsIn.add(attribute);
		for (MsgAttribute attribute : config.getReqStructAttributes()) {
			boolean duplicate = false;
			for (MsgAttribute a : attsIn)
				if (attribute.getName().equals(a.getName()))
					duplicate = true;

			if (!duplicate)
				attsIn.add(attribute);
		}

		for (MsgAttribute attribute : config.getRespFieldAttributes())
			attsOut.add(attribute);
		for (MsgAttribute attribute : config.getRespStructAttributes()) {
			boolean duplicate = false;
			for (MsgAttribute a : attsOut)
				if (attribute.getName().equals(a.getName()))
					duplicate = true;

			if (!duplicate)
				attsOut.add(attribute);
		}

		// 输出交易报文
		int count = 0;
		for (String tranName : trans.keySet()) {
			MsgDocument msgIn = trans.get(tranName).getA();
			MsgDocument msgOut = trans.get(tranName).getB();

			if (msgIn != null) {
				WritableSheet sheet = workbook.createSheet(
						tranName + "|in", count++);
				toSheet(msgIn, sheet, attsIn, writeData);
			}
			if (msgOut != null) {
				WritableSheet sheet = workbook.createSheet(tranName
						+ "|out", count++);
				toSheet(msgOut, sheet, attsOut, writeData);
			}
		}

		workbook.write();
		workbook.close();
	}
	
	/**
	 * 工具函数 用于将一个报文结构对象导出为一张工作表
	 * 
	 * @param doc
	 *            报文结构定义
	 * @param sheet
	 *            将要写入报文结构的工作表
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private static void toSheet(MsgDocument doc, final WritableSheet sheet, final List<MsgAttribute> atts,
			final boolean writeData) 
	throws RowsExceededException, WriteException {
		final Wrapper<Integer> rowCount = new Wrapper<Integer>(4);

		WritableFont fontNormal= new WritableFont(WritableFont.ARIAL,11);
		fontNormal.setBoldStyle(WritableFont.BOLD);
		
		WritableCellFormat cellFormat = new WritableCellFormat(fontNormal);
		cellFormat.setAlignment(jxl.format.Alignment.LEFT);
		// 输出交易信息
		Label lb = new Label(0, 0, "交易码");
		lb.setCellFormat(cellFormat);
		sheet.addCell(lb);
		
		lb = new Label(0, 1, "交易描述");
		lb.setCellFormat(cellFormat);
		sheet.addCell(lb);
		
		lb = new Label(0, 2, "交易类别");
		lb.setCellFormat(cellFormat);
		sheet.addCell(lb);
		sheet.addCell(new Label(2, 2, "(1:POS类交易; 2:ATM类交易; 3:转账类交易)"));
		
		sheet.addCell(new Label(1, 0, doc.getAttribute("tranCode").str));
		sheet.addCell(new Label(1, 1, doc.getAttribute("tranDesc").str));
		sheet.addCell(new Label(1, 2, doc.getAttribute("transCateId").str));

		
		// 输出列标头 顺便设置列宽
		for (int i = 0; i < atts.size(); i++) {
			sheet.addCell(new Label(i + 1, 3, atts.get(i).getName() + "|" + atts.get(i).getDisplayName()));
			switch (i) {
			case 0:
				// 结构信息列
				sheet.setColumnView(i, 25);
				break;
			case 1:
				// 名称列
				sheet.setColumnView(i, 25);
				break;
			case 2:
				// 描述列
				sheet.setColumnView(i, 40);
				break;
			default:
				// 其它信息列为默认宽度
				break;
			}
		}
		
		doc.ForEach(new ISimpleForEachVisitor() {			

			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				// TODO Auto-generated method stub
				try {
					switch (source) {
					case StruStart:
						if (item instanceof MsgDocument)
							break;

						sheet.addCell(new Label(0, rowCount.getValue(), "["));
					case Field:
						for (int i = 0; i < atts.size(); i++)
							sheet.addCell(new Label(i + 1, rowCount.getValue(), item.getAttribute(atts.get(i).getName()).getStr()));

						rowCount.setValue(rowCount.getValue() + 1);
						break;
					case StruEnd:
						if (item instanceof MsgDocument)
							break;
						sheet.addCell(new Label(0, rowCount.getValue(), "]"));
						sheet.addCell(new Label(1, rowCount.getValue(), item.getAttribute("name").getStr()));

						rowCount.setValue(rowCount.getValue() + 1);
						break;
					}
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

		});
	}

	/**
	 * 工具函数 填写标题行
	 */
	private static void writeTitleRow(WritableSheet sheet, Collection<String> attrs) throws JXLException {
		int c = 1;
		for (String attr : attrs)
			sheet.addCell(new Label(c++, 3, attr));

		sheet.setColumnView(0, 12); // 首列宽度
		sheet.setColumnView(1, 25); // 名称列宽度
		sheet.setColumnView(2, 40); // 注释列宽度
	}

	/**
	 * 工具函数 读取标题行
	 */
	private static LinkedList<String> readTitleRow(List<MsgAttribute> attrList, Sheet sheet) throws JXLException {
		LinkedList<String> attrs = new LinkedList<String>();
		
		for (int c = 1; c < sheet.getColumns(); c++) {
			String attrName = sheet.getCell(c, 3).getContents().split("[|]")[0]; //获得列名称
			if (attrName == null || attrName.length() == 0)
				break;
			MsgAttribute ma = searchMsgAttribute(attrList, attrName);
			if(ma == null) continue; //跳过多余字段
			
			attrs.add(attrName);
		}
		return attrs;
	}

	/**
	 * 工具函数 填写报文元素行
	 */
	private static void writeItemRow(WritableSheet sheet, MsgItem item, Set<String> attrs, int row) throws JXLException {
		int c = 1;
		for (String attr : attrs)
			sheet.addCell(new Label(c++, row, item.getAttribute(attr).getStr()));
	}

	/**
	 * 工具函数 读取报文元素行
	 */
	private static MsgAttribute searchMsgAttribute(List<MsgAttribute> attrList, String attrName){
		for(MsgAttribute ma: attrList){
			if(ma.getName().equals(attrName) || ma.getDisplayName().equals(attrName))
				return ma;
		}
		return null;
	}

	private static Map<String, String> readItemRow(Sheet sheet, List<String> attrsFromExcel, int row) throws JXLException {
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		
		for(int i = 0; i< attrsFromExcel.size();i++)
		{
			String dataFromExcel = sheet.getCell(i+1, row).getContents().trim();	
			data.put(attrsFromExcel.get(i), dataFromExcel);
		}
	
		return data;
	}
	
	/**
	 * 工具函数 读取报文元素行
	 * 
	 * @param attrList 应该有的属性列表
	 * @param sheet
	 * @param attrsFromExcel 
	 * @param row
	 * @return
	 * @throws JXLException
	 */
	private static Map<String, String> readItemRow(List<MsgAttribute> attrList, Sheet sheet, List<String> attrsFromExcel, int row) throws JXLException {

		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		
		int col = 1; //跳过第一列“结构标识”
		for (String attrName: attrsFromExcel){
			String dataFromExcel = sheet.getCell(col++, row).getContents();	
			 //填充遗漏的字段
			if(dataFromExcel == null || dataFromExcel.length() == 0){
				//名称列为空，则跳过这一行
				if(attrName.equalsIgnoreCase("name"))
					return null;
				MsgAttribute ma = searchMsgAttribute(attrList, attrName);
				if(ma != null){
					data.put(ma.getName(), ma.getDefaultValue());
				}
			}else{
				data.put(attrName, dataFromExcel);
			}
		}
		
		//在尾部补充缺失的属性列
		//找到应该有但是Excel中没有的属性
		for (MsgAttribute ma: attrList){
			if(attrsFromExcel.contains(ma.getName()) || attrsFromExcel.contains(ma.getDisplayName()))
				continue;
			else{
				data.put(ma.getName(), ma.getDefaultValue());
			}
		}
	
		return data;
	}

	/**
	 * 工具函数 统计出报文中所有属性的名称列表
	 */
	private static LinkedHashSet<String> calcAttributes(MsgDocument doc) {
		final LinkedHashSet<String> attrs = new LinkedHashSet<String>();
		attrs.add("name");
		attrs.add("desc");

		doc.ForEach(new ISimpleForEachVisitor() {		

			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				// TODO Auto-generated method stub
				if (item instanceof MsgDocument)
					return;

				for (String name : item.getAttributes().keySet())
					if (!attrs.contains(name))
						attrs.add(name);
			}
		});
		return attrs;
	}
	
	public static void main(String[] args) throws Exception {

		//准备调用参数
		
		//data8583, dataXml, CISS报文结构
		MsgDocument struData = MsgLoader.LoadXml(RuntimeUtils.ReadResource("test/dataXml.xml", RuntimeUtils.gb2312));
		
		List<MsgAttribute> fAttrs = new LinkedList<MsgAttribute>();
		Set<String> attrSet = ExcelSerializer.calcAttributes(struData);
		for(String s: attrSet){
			fAttrs.add(new MsgAttribute(s, s, null, "DDDDD", ""));
		}
		
		
		String tranName = "testTrans";
		struData.setAttribute("tranName", new Value(tranName));
		struData.setAttribute("tranDesc", new Value(tranName));
		
		
		
		//上传报文结构
		Workbook workbookForRead = Workbook.getWorkbook(new File("d:\\testRead.xls"));
		Sheet sheetForReadStruct = workbookForRead.getSheet(0);
		MsgDocument struByRead = DeserializeStru(fAttrs, sheetForReadStruct);
				
		System.out.println(struByRead);
		

		System.out.println("------------------------------------------------------------------");
		//下载报文结构
		WritableWorkbook workbookForWrite = Workbook.createWorkbook(RuntimeUtils.MapFile("test.xls"));		
		WritableSheet sheet = workbookForWrite.createSheet(tranName + "|in", 0);		
		SerializeStru(fAttrs,sheet, struByRead);

		
		//上传案例数据
		Sheet sheetForCase = workbookForRead.getSheet(1);
		Map<String, MsgDocument> caseMap = DeserializeCases(sheetForCase);

		System.out.println(caseMap);
		
		
		//下载案例数据
		WritableSheet sheetFoWriteCase = workbookForWrite.createSheet(tranName + "|in|data", 1);	
		
		Map<String, MsgDocument> caseMapForCase = new HashMap<String, MsgDocument>();
		caseMapForCase.put(tranName, struData);
		SerializeCases(fAttrs, sheetFoWriteCase, struByRead, caseMap);
		
		
		workbookForRead.close();
		

		workbookForWrite.write();
		workbookForWrite.close();
	}
}
