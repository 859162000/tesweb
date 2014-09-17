package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceFieldValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.util.CaseResultUtil;

public class YQCaseFlowResultExport  extends HttpServlet {

	private static final long serialVersionUID = 3714165662615617550L;

	private IDAL<CaseFlowInstance> caseFlowInstanceDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
	private IDAL<CaseInstance> caseInstanceDAL = DALFactory.GetBeanDAL(CaseInstance.class);
	private IDAL<CaseInstanceFieldValue> fieldValueDAL = DALFactory.GetBeanDAL(CaseInstanceFieldValue.class);
	private IDAL<CaseParameterExpectedValue> caseParamExpValDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
	//private IDAL<TransactionDynamicParameter> tranParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
	private IDAL<ExecuteSet> executeSetDAL = DALFactory.GetBeanDAL(ExecuteSet.class);
	//private IDAL<ExecuteSetDirectory> executeSetDirDAL = DALFactory.GetBeanDAL(ExecuteSetDirectory.class);
	
	private CaseResultUtil exportUtils;
	
	private int currentRow;
	
	private String[] title = {"用例编号","用例名称","状态","步骤序号","步骤名称","状态",
							  "发起报文","反馈报文","参数名称","参数描述","预期值","实际值"};
	
	private String[] statisticTitle = {"执行集","用例总个数","成功个数","失败个数","成功率", 
			"开始执行时间", "结束执行时间", "执行时长", "执行人"};
	
	public YQCaseFlowResultExport() {
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
			
			IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
			
			ExecuteLog executeLog = executeLogDAL.Get(Op.EQ("id",Integer.parseInt(id)));
			
			if (executeLog == null) {
				throw new DownLoadException("无法获取执行日志ID，请与管理员联系。");
			}
			//通过执行批次找到同时执行的多个执行集
			String excuteBatchNo = executeLog.getExecuteBatchNo();
			
			List<ExecuteLog> executeLogSetList = executeLogDAL.ListAll("id", true, Op.EQ("executeBatchNo", excuteBatchNo));
			
			
			exportUtils = new CaseResultUtil();

			exportExecuteSetResult(executeLogSetList, request, response);
		    
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
	


	private void exportExecuteSetResult(List<ExecuteLog> executeLogSetList, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		File file = new HelperService().CreateTempFile(date.format(new Date()), ".xls");
		
		FileOutputStream stream = new FileOutputStream(file);
		WritableWorkbook workbook = Workbook.createWorkbook(stream);
		
		WritableSheet sheet0 = workbook.createSheet("结果统计",0);
		
		//写统计信息
		exportStatistic(executeLogSetList,sheet0);
		
		
		for(int i=0; i<executeLogSetList.size(); i++) { //每个执行集单独一个Sheet
			ExecuteLog executeLog = executeLogSetList.get(i);
			WritableSheet sheet = workbook.createSheet(executeLog.getExecuteSetName(),i+1);
			currentRow = 0;
			int executeLogID = executeLog.getId();
			
			//写执行集标题
			ExecuteSet queue = executeSetDAL.Get(Op.EQ("id", String.valueOf(executeLog.getExecuteSetId())));		
			String executeSetName = queue == null ? "" : queue.getName();
			sheet.addCell(new Label(0,currentRow,"执行集:",exportUtils.SuccOWcf));
			sheet.addCell(new Label(1,currentRow,executeSetName,exportUtils.SuccOWcf));
			currentRow += 2;
			
			//执行集下的业务流
			List<CaseFlowInstance> caseFlowInsList = caseFlowInstanceDAL.ListAll("id",
					true, Op.EQ("executeLogId", executeLogID));
		    
			for(int j=0; j<caseFlowInsList.size(); j++) {
				CaseFlowToExcel(sheet,caseFlowInsList.get(j));
				currentRow += 2;
			}
		}
		
		workbook.write();
		workbook.close();
		response.getOutputStream().write(file.getName().getBytes("gb2312"));
		response.flushBuffer();
		stream.close();
	}
	
	private void exportStatistic(List<ExecuteLog> executeLogSetList, WritableSheet sheet) throws RowsExceededException, WriteException {		
	
		/*
		Map<String,Integer> executeDirAllSet = new HashMap<String,Integer>();
		
		Map<Integer,Integer> executedDirSet = new HashMap<Integer,Integer>();
		
		for(int i=0; i<executeLogSetList.size(); i++) {
			ExecuteLog executeLog = executeLogSetList.get(i);
			
			ExecuteSet queue = executeSetDAL.Get(Op.EQ("id", executeLog.getQueueListId()));
			ExecuteSetDirectory execSetDir = executeSetDirDAL.Get(Op.EQ("objectId", Integer.parseInt(queue.getId())));
			if(executedDirSet.containsKey(execSetDir.getParentDirId())) {
				executedDirSet.put(execSetDir.getParentDirId(),
						executedDirSet.get(execSetDir.getParentDirId())+1);
			} else {
				executedDirSet.put(execSetDir.getParentDirId(),1);
			}
		}
		
		Set<Integer> set = executedDirSet.keySet();
		
		for(int parentDirId : set) {
			int count = executeSetDirDAL.Count(Op.EQ("parentDirId", parentDirId));
			String name = executeSetDirDAL.Get(Op.EQ("id", parentDirId)).getName();
			executeDirAllSet.put(name, count);
		}*/
		int row = 8;
		exportUtils.setStatisticTitleWidth(sheet,1);
		exportUtils.writeTitleCell(sheet, row++, 1, Arrays.asList(statisticTitle));
		
		
		
		for(int i=0; i<executeLogSetList.size(); i++) {
			int col = 1;
			ExecuteLog executeLog = executeLogSetList.get(i);
			
			ExecuteSet queue = executeSetDAL.Get(Op.EQ("id", String.valueOf(executeLog.getExecuteSetId())));
			//执行集名称
			String setName = queue == null?"": queue.getName();
			
			//执行集下的业务流
			List<CaseFlowInstance> caseFlowInsList = caseFlowInstanceDAL.ListAll("caseFlowNo",
					true, Op.EQ("executeLogId", executeLog.getId()));
			//业务流总数
			int size = caseFlowInsList.size();
			
			//成功数
			int sizeOfSucc = 0;
			for(int j=0; j<size; j++) {
				if(caseFlowInsList.get(j).getCaseFlowPassFlag() == 1) {
					sizeOfSucc++;
				}
			}
			
			float succPercentage =  (float)sizeOfSucc / (float)size * 100;
			String percent = String.format("%.2f", succPercentage);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			String startTime = sdf.format(executeLog.getBeginRunTime());
			String endTime = executeLog.getEndRunTime()==null? "" : sdf.format(executeLog.getEndRunTime());
			String runTime = executeLog.getRunDuration();
			String execUser = (DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", executeLog.getUserId().toString()))).getName();
			
			sheet.addCell(new Label(col++, row, setName,                           exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, String.valueOf(size),              exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, String.valueOf(sizeOfSucc),        exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, String.valueOf(size - sizeOfSucc), exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, percent+"%",                       exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, startTime,                         exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, endTime,                           exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, runTime,                           exportUtils.Wcf));
			sheet.addCell(new Label(col++, row, execUser,                          exportUtils.Wcf));
			row++;
		}
		
		
	}

	private void CaseFlowToExcel(WritableSheet sheet,
			CaseFlowInstance caseflowInstance) throws Exception {
		// TODO Auto-generated method stub
		
		List<CaseInstance> caseInstances = caseInstanceDAL.ListAll("sequence", true, Op.EQ("caseFlowInstance.id", caseflowInstance.getId()));
		int col = 0;
		
		//写列名
		exportUtils.setTitleWidth(sheet, col);
		exportUtils.writeTitleCell(sheet, currentRow, 0, Arrays.asList(title));
		currentRow++;
		
		//写业务流内容
		sheet.addCell(new Label(col++,currentRow,caseflowInstance.getCaseFlowNo(),exportUtils.Wcf));
		sheet.addCell(new Label(col++,currentRow,caseflowInstance.getCaseFlowName(),exportUtils.Wcf));
		if(caseflowInstance.getCaseFlowPassFlag() == 1) 
			sheet.addCell(new Label(col++,currentRow,"成功",exportUtils.SuccWcf));
		else
			sheet.addCell(new Label(col++,currentRow,"失败",exportUtils.ErrWcf));
		
		int caseCol;
		
		//写案例内容
		for(int i=0; i<caseInstances.size(); i++) {
			caseCol = col;
			
			CaseInstance caseIns = caseInstances.get(i);
			sheet.addCell(new Label(caseCol++,currentRow,caseIns.getSequence().toString(),exportUtils.Wcf));
			sheet.addCell(new Label(caseCol++,currentRow,caseIns.getCaseName(),exportUtils.Wcf));
			if(caseIns.getCasePassFlag()!=null) {
				if(caseIns.getCasePassFlag()==1)
					sheet.addCell(new Label(caseCol++,currentRow,"成功",exportUtils.SuccWcf));
				else
					sheet.addCell(new Label(caseCol++,currentRow,"失败",exportUtils.ErrWcf));
			}
			else 
				sheet.addCell(new Label(caseCol++,currentRow,"未执行",exportUtils.ErrWcf));
				
			Label request = new Label(caseCol++,currentRow,"查看",exportUtils.Wcf);
			WritableCellFeatures cellFeatutes = new WritableCellFeatures(); 
			cellFeatutes.setComment(caseIns.getRequestMsg()==null?"无":caseIns.getRequestMsg(),2,12);
			request.setCellFeatures(cellFeatutes);
			sheet.addCell(request);
			
			Label response = new Label(caseCol++,currentRow,"查看",exportUtils.Wcf);
			WritableCellFeatures cellFeatutes2 = new WritableCellFeatures(); 
			String responContent = "";
			if(caseIns.getResponseMsg()!= null){
				responContent = caseIns.getResponseMsg().length()>5000?"返回报文过长":caseIns.getResponseMsg();
			}
			if(!responContent.isEmpty()){
				cellFeatutes2.setComment(responContent,2,12);
				response.setCellFeatures(cellFeatutes2);
			}
			sheet.addCell(response);
			
			writeParamResult(sheet,caseCol,caseIns);
			currentRow++;
		}
		
	}

	private void writeParamResult(WritableSheet sheet, int currentCol,
			CaseInstance caseIns) throws Exception {
		// TODO Auto-generated method stub
		int paramCol;
		
		//只显示匹配不正确的预期值
		List<CaseParameterExpectedValue> paramExpecteds = caseParamExpValDAL.ListAll(Op.EQ("caseId", caseIns.getCaseId().toString()));
		boolean flag = false;
		for(int i=0; i<paramExpecteds.size(); i++) {		
			paramCol = currentCol;		
			CaseParameterExpectedValue param = paramExpecteds.get(i);
			//这个预期值可能会是参数
			String paramExpectedValue = param.getExpectedValue();
			TransactionDynamicParameter tranParam = param.getTransParameter();
			if(paramExpectedValue != null) {
				CaseInstanceFieldValue fieldValue = fieldValueDAL.Get(Op.EQ("caseInstanceId", caseIns.getId()),Op.EQ("transParameter", tranParam));
				//这个是参数计算后的真实预期值
				String expectedValue = paramExpectedValue;
				String realValue = "";
				if(fieldValue != null) {
					realValue = fieldValue.getMsgFieldValue() == null? "" : fieldValue.getMsgFieldValue();
					expectedValue = fieldValue.getExpectedValue() == null? "" : fieldValue.getExpectedValue();
				}
				SystemDynamicParameter sysParam = tranParam.getSystemParameter();
			    
				if(!expectedValue.equalsIgnoreCase(realValue)) {
					sheet.addCell(new Label(paramCol++,currentRow,sysParam.getName(),exportUtils.Wcf));
					sheet.addCell(new Label(paramCol++,currentRow,sysParam.getDesc(),exportUtils.Wcf));
					sheet.addCell(new Label(paramCol++,currentRow,expectedValue,exportUtils.ExpectWcf));
					sheet.addCell(new Label(paramCol++,currentRow,realValue,exportUtils.RecWcf));
					currentRow++;
					flag = true;
				} 
			}
		}
		if(flag)
	       currentRow--;
		else {
			for(int j=currentCol; j<4+currentCol; j++)
				sheet.addCell(new Label(j,currentRow,"",exportUtils.Wcf));
			
		}
	}

}
