package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ParameterDirectory;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;

public class CaseExpectValueUpload extends HttpServlet {

	private static final long serialVersionUID = -834860354245090782L;
	private IDAL<ParameterDirectory> paramDirDAL = DALFactory.GetBeanDAL(ParameterDirectory.class);
	private IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
	private IDAL<TransactionDynamicParameter> tranParamDAL =  DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
	private IDAL<CaseParameterExpectedValue> caseParamValueDAL =  DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
	public CaseExpectValueUpload(){
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
		List<String> resultList = new ArrayList<String>();
		ServletFileUpload upload = new ServletFileUpload();
		try {
		
			FileItemIterator iter = upload.getItemIterator(request);
		
			while (iter.hasNext()) {	
				FileItemStream item = iter.next();
				if (item.isFormField()) {
					
					continue;
					
				} else {
					String caseID = request.getParameter("caseID");
					String tranID = request.getParameter("tranID");
					String systemID = request.getParameter("systemID");
					resultList = UploadCaseExpectValue(caseID, tranID, systemID, item, request, response);	
										
					break;
				}
			} 
		}catch (DownLoadException ex) {
			
			ex.printStackTrace();
			response.getOutputStream()
					.write(ex.getMessage().getBytes("utf8"));
			response.flushBuffer();
			return;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					("error:发生异常，请与管理员联系。"+ex.getMessage()).getBytes("utf8"));
			response.flushBuffer();
			return;
		}
			
		String msg = resultList.toString();
		response.getOutputStream().write(msg.getBytes("utf8"));
		response.flushBuffer();
		
	}
	private List<String> UploadCaseExpectValue(String caseID, String tranID, String systemID,
			FileItemStream item, HttpServletRequest request,
			HttpServletResponse response) throws IOException, BiffException {
		// TODO Auto-generated method stub
		List<String> resultList = new ArrayList<String>();
		InputStream stream = item.openStream();
		Workbook workbook = Workbook.getWorkbook(stream);
		
		Sheet sheet = workbook.getSheet(0);
		
		int rows = sheet.getRows();
		int succ = 0;
		int update = 0;
		int wrong = 0;
		long time = System.currentTimeMillis();
		
		//该项目模板从第1行开始有数据
		for(int row=1; row<rows; row++) {	
			
			boolean fl= false;//标记案例是否有效			
			for(int i = 0; i < 1; i++){//1列如有空值，则为无效案例，跳过
				if(sheet.getCell(i, row).getContents().equals("")){
					fl = true;
					break;
				}
			}
			if(fl){
				wrong++;
				continue;
			}	
			//路径
			String path = sheet.getCell(0, row).getContents();
			//参数描述 (不写)
			String desc = sheet.getCell(1, row).getContents();
			//预期值
			String expectValue = sheet.getCell(2, row).getContents();
			//类型
			String valueType = sheet.getCell(3, row).getContents();
			
			if(!path.startsWith("\\")){ //如果路径不是以反斜杠开头，视为无效案例
				wrong++;
				continue;
			}
			
			ParameterDirectory paramDir;
			int last = path.lastIndexOf("\\");
			//最后一个是叶子节点，为系统参数，前面都是路径
			//直接拿前缀去匹配数据库
			//  \\系统参数   不会有这种情况
			String preDir = path.substring(0, last);
			String paramName = path.substring(last+1);
			paramDir = paramDirDAL.Get(
					Op.EQ(GWTParameterDirectory.N_SystemID, systemID),
					Op.EQ(GWTParameterDirectory.N_Path, preDir));
			if(paramDir == null) {
				wrong++;
				continue;
			}
			
			SystemDynamicParameter sysParam = sysParamDAL.Get(Op.EQ(GWTSysDynamicPara.N_DirectoryID, paramDir.getId()),
					Op.EQ(GWTSysDynamicPara.N_ParameterName, paramName));
			if(sysParam == null) {
				wrong++;
				continue;
			}

			//判断该系统参数是不是属于该交易
			TransactionDynamicParameter tranParam = tranParamDAL.Get(Op.EQ("transactionId", tranID),
													Op.EQ("systemParameter",sysParam));
			if(tranParam == null) {
				wrong++;
				continue;
			}
			
			CaseParameterExpectedValue caseParamValue = caseParamValueDAL.Get(Op.EQ("caseId", caseID),Op.EQ("transParameter", tranParam));
			if(caseParamValue != null) {
				caseParamValue.setExpectedValue(expectValue);
				caseParamValue.setExpectedValueType(Integer.parseInt(valueType));
				caseParamValueDAL.Edit(caseParamValue);
				update++;
			} else {
				caseParamValue = new CaseParameterExpectedValue();
				caseParamValue.setCaseId(caseID);
				caseParamValue.setExpectedValue(expectValue);
				caseParamValue.setExpectedValueType(Integer.parseInt(valueType));
				caseParamValue.setTransParameter(tranParam);
				caseParamValueDAL.Add(caseParamValue);
				succ++;
			}	
		}
		
		time = (System.currentTimeMillis() - time)/1000;
		
		resultList.add("newadd:" + "共添加案例参数预期值记录"+succ+"条");
		resultList.add("newadd:" + "共更新案例参数预期值记录"+update+"条");
		resultList.add("newadd:" + "共跳过无效案例参数预期值记录"+wrong+"条");
		resultList.add("newadd:" + "共花费"+time+"s");
		
		return resultList;
	}
}
