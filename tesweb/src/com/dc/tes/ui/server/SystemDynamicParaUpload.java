package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.model.ParameterDirectory;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTHost;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;

public class SystemDynamicParaUpload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7796413488285406620L;
	private static IDAL<ParameterDirectory> paramDirDAL = DALFactory.GetBeanDAL(ParameterDirectory.class);
	private static IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
	private static IDAL<DbHost> dbHostDAL = DALFactory.GetBeanDAL(DbHost.class);
	private static IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
	private static IDAL<TransactionDynamicParameter> transDynParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
	private static final Log log = LogFactory.getLog(SystemDynamicParaUpload.class);
	
	public SystemDynamicParaUpload(){
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
					String systemId = request.getParameter("sysId");
					IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
					SysType sys = sysDAL.Get(Op.EQ(GWTSimuSystem.N_SystemID,
							systemId));
					if(sys == null) {
						throw new DownLoadException("无法获得当前模拟系统信息，无法上传");
					}
					resultList = UploadSysDynParas(sys.getSystemId(), item, request, response);	
										
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
					"error:发生异常，请与管理员联系。".getBytes("utf8"));
			response.flushBuffer();
			return;
		}
			
		String msg = resultList.toString();
		response.getOutputStream().write(msg.getBytes("utf8"));
		response.flushBuffer();
	}

	private List<String> UploadSysDynParas(String systemId, FileItemStream item, HttpServletRequest request, HttpServletResponse response)
			throws IOException, BiffException {
		// TODO Auto-generated method stub
		List<String> resultList = new ArrayList<String>();
		InputStream stream = item.openStream();
		Workbook workbook = Workbook.getWorkbook(stream);
		String userId = request.getParameter("userId");
		Integer loginLogId = Integer.parseInt(request.getParameter("loginLogId").toString());
		String sheetName = "";
		boolean found = false;
		try{
			//读取Excel sheet页面
			for (Sheet sheet : workbook.getSheets()) {
				sheetName = sheet.getName().toLowerCase();				
				if(sheetName.contains("主机")){
					DeserializeDbHost(systemId, sheet, resultList);
				}
			}
			for (Sheet sheet : workbook.getSheets()) {
				sheetName = sheet.getName().toLowerCase();				
				if(sheetName.contains(("参数"))) {
					found = true;
					DeserializeSysParam(systemId, userId, loginLogId, sheet, resultList);				
				} 
			}
			
		}catch(Exception e) {
			log.error(e);
			throw new DownLoadException("遇到未处理的问题，请与管理员联系;");
			// resultList.add("error:" + sheet.getName() + ":[遇到遇到情况]");
		}	
		if(found == false)
			resultList.add("newadd:" + "没有找到含有 参数 字样的表格");
		return resultList;
		
	}
	public static void DeserializeDbHost(String systemId, Sheet sheet,
			List<String> resultList) {
		// TODO Auto-generated method stub
		int rows = sheet.getRows();
		int succ = 0;
		int update = 0;
		int wrong = 0;
		long time = System.currentTimeMillis();
		
		//该项目模板从第1行开始有数据
		for(int row=1; row < rows; row++) {	
			
			boolean fl= false;//标记案例是否有效			
			for(int i = 0; i < 3; i++){//前3列如有空值，则为无效案例，跳过
				if(sheet.getCell(i, row).getContents().equals("")){
					fl = true;
					break;
				}
			}
			if(fl){
				wrong++;
				continue;
			}	
			//主机名
			String hostName = sheet.getCell(0, row).getContents(); 
			//主机地址
			String hostIP = sheet.getCell(1, row).getContents();
			//主机端口
			String port = sheet.getCell(2,row).getContents();
			//是否长连接
			String isLongConn = sheet.getCell(3,row).getContents();
			//操作系统
			String  osType = sheet.getCell(4,row).getContents();
			//数据库类型
			String dbType = sheet.getCell(5,row).getContents();
			//数据库名称
			String dbName = sheet.getCell(6,row).getContents();
			//用户名
			String dbUser = sheet.getCell(7,row).getContents();
			//密码
			String dbPwd = sheet.getCell(8,row).getContents();
			//主机描述
			String hostDesc = sheet.getCell(9,row).getContents();
			
			if(!validateIP(hostIP)){
				wrong++;
				continue;
			}
			try{
				DbHost dbHost = new DbHost();
				dbHost.setDbHostName(hostName);
				dbHost.setIpaddress(hostIP);
				dbHost.setPortnum(Integer.parseInt(port));
				dbHost.setIsLongConn(Integer.parseInt(isLongConn));
				dbHost.setOsType(getOsType(Integer.parseInt(osType)));
				dbHost.setDbType(dbType.isEmpty() ? null : getDbType(Integer.parseInt(dbType)));
				dbHost.setDbName(dbName);
				dbHost.setDbUser(dbUser);
				dbHost.setDbPwd(dbPwd);
				dbHost.setDescription(hostDesc);
				dbHost.setSystemId(systemId);
				DbHost old = dbHostDAL.Get(Op.EQ(GWTHost.N_DbHost, dbHost.getDbHostName()),
						Op.EQ(GWTHost.N_SystemId, dbHost.getSystemId()));
				if(old == null){
					dbHostDAL.Add(dbHost);
					succ++;
				}else{
					dbHost.setHostid(old.getHostid());
					dbHostDAL.Edit(dbHost);
					update++;
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				wrong++;
				continue;
			}			
		}
		time = (System.currentTimeMillis() - time)/1000;
		
		resultList.add("newadd:" + "共添加主机记录"+succ+"条");
		resultList.add("edit:" + "共更新主机记录"+update+"条");
		resultList.add("newadd:" + "共跳过无效主机记录"+wrong+"条");
		resultList.add("newadd:" + "导入主机记录共花费"+time+"s");

		
	}
	
	private static String getDbType(int i) {
		// TODO Auto-generated method stub
		switch (i) {
		case 1:
			return "DB2";
		case 2:
			return "Mysql";
		case 3:
			return "Oracle";
		case 4:
			return "SqlServer";

		default:
			return null;
		}
	}
	private static String getOsType(int i){
		switch (i) {
		case 1:
			return "WINDOWS";
		case 2:
			return "AS400";
		case 3:
			return "RS6000";
		case 4:
			return "LINUX";

		default:
			return null;
		}
	}
	
	
	public static void DeserializeSysParam(String systemId, String userId, Integer loginLogId, Sheet sheet,
			List<String> resultList) {
		// TODO Auto-generated method stub
		int rows = sheet.getRows();
		int succ = 0;
		int update = 0;
		int wrong = 0;
		long time = System.currentTimeMillis();
		boolean hasTranCol = false; //第一列是否是交易名称
		if(sheet.getCell(0, 0).getContents().equals("交易名称")){
			hasTranCol = true;
		}
		//该项目模板从第1行开始有数据
		for(int row=1; row < rows; row++) {	
			
			boolean fl= false;//标记案例是否有效			
			for(int i = hasTranCol?1:0; i < (hasTranCol?3:2); i++){//前2列如有空值，则为无效案例，跳过
				if(sheet.getCell(i, row).getContents().equals("")){
					fl = true;
					break;
				}
			}
			if(fl){
				wrong++;
				continue;
			}
			int col = 0;
			String tranName = "";
		
			if(hasTranCol){
				 tranName      = sheet.getCell(col++, row).getContents();
			}
		//路径
			String path        = sheet.getCell(col++, row).getContents(); 
		//参数名称
			String name        = sheet.getCell(col++, row).getContents();
		//参数描述
			String desc        = sheet.getCell(col++, row).getContents();
		//参数类型
			String type        = sheet.getCell(col++, row).getContents();
		//匹配条件
			String condition   = sheet.getCell(col++, row).getContents();
		//参数所在主机
			String hostType    = sheet.getCell(col++, row).getContents();
		//主机地址IP
			String hostIP      = sheet.getCell(col++, row).getContents();			
		//报文来源
			String msgSource   = sheet.getCell(col++, row).getContents();
		//是否可见
			String displayFlag = sheet.getCell(col++, row).getContents();
		//是否有效
			String isValid     = sheet.getCell(col++, row).getContents();		
		//默认预期值
			String defValue    = sheet.getCell(col++, row).getContents();
		//参数表达式
			String expression  = sheet.getCell(col++, row).getContents();
		
			try{
				if(!path.startsWith("\\")){ //如果路径不是以反斜杠开头，视为无效案例
					wrong++;
					continue;
				}
				Integer directoryId = createParamDir(path, systemId);
				SystemDynamicParameter sysParam = new SystemDynamicParameter();
				sysParam.setDirectoryId(directoryId);
				sysParam.setName(name);
				sysParam.setDesc(desc);
				sysParam.setParameterType(String.valueOf(Integer.parseInt(type)-1));
				sysParam.setCompareCondition(String.valueOf(Integer.parseInt(condition)-1));
				int dbHostType = Integer.parseInt(hostType)-1;
				sysParam.setParameterHostType(dbHostType);
				if(dbHostType == 1){  // 如果是指定主机IP，则需要对IP地址进行验证 
					if(validateIP(hostIP)){ 
						DbHost dbHost = dbHostDAL.Get(Op.EQ(GWTHost.N_SystemId, systemId),
								Op.EQ(GWTHost.N_Ipaddress, hostIP));						
						sysParam.setParameterHostId(dbHost.getHostid());
					}else{
						wrong++;
						continue;
					}
				}
				sysParam.setParamFromMsgSrc(Integer.parseInt(msgSource)-1);
				sysParam.setDisplayFlag(displayFlag);
				sysParam.setIsValid(isValid);
				sysParam.setDefaultExpectedValue(defValue);
				sysParam.setParameterExpression(expression);
				sysParam.setRefetchFlag(0);
				sysParam.setSystemId(systemId);
				SystemDynamicParameter old = sysParamDAL.Get(Op.EQ(GWTSysDynamicPara.N_ParameterName, sysParam.getName()),
						Op.EQ(GWTSysDynamicPara.N_DirectoryID, sysParam.getDirectoryId()),
						Op.EQ(GWTSysDynamicPara.N_SystemID, sysParam.getSystemId()));
				if(old == null){
					sysParam.setCreatedTime(new Date());
					sysParam.setCreatedUserId(userId);
					sysParamDAL.Add(sysParam);
					OperationLogService.writeOperationLog(OpType.SystemDynamicParameter, IDUType.Import,
							Integer.parseInt(sysParam.getId()), sysParam.getName(),
							"name", null, sysParam.getName(), loginLogId);					
					succ++;
				}else{
					sysParam.setLastModifiedTime(new Date());
					sysParam.setLastModifiedUserId(userId);
					sysParam.setId(old.getId());
					OperationLogService.writeOperationLog(OpType.SystemDynamicParameter, IDUType.Import, 
							Integer.parseInt(sysParam.getId()), old.getName(),
							"name", old.getName(), sysParam.getName(), loginLogId);
					sysParamDAL.Edit(sysParam);
					update++;
				}
				
				if(hasTranCol){
					if(!tranName.isEmpty()){
						Transaction tran = tranDAL.Get(Op.EQ("systemId", systemId), Op.EQ("tranName", tranName));
						if(tran == null){
							continue;
						}
						TransactionDynamicParameter tranParam = transDynParamDAL.Get(Op.EQ("transactionId", tran.getTransactionId()),
								Op.EQ("systemParameter.id", sysParam.getId()));
						if(tranParam !=null){
							continue;  //已存在该参数，不添加
						}else{
							tranParam = new TransactionDynamicParameter();
							tranParam.setModifyTime(new Date());
							tranParam.setSystemParameter(sysParam);
							tranParam.setTransactionId(tran.getTransactionId());
							tranParam.setUserId(userId.equals("Administrator")?"0":userId);
							transDynParamDAL.Add(tranParam);
						}
					}
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				wrong++;
				continue;
			}
		}
		time = (System.currentTimeMillis() - time)/1000;
		
		resultList.add("newadd:" + "共添加动态参数记录"+succ+"条");
		resultList.add("edit:" + "共更新动态参数记录"+update+"条");
		resultList.add("error:" + "共跳过无效动态参数记录"+wrong+"条");
		resultList.add("newadd:" + "导入动态参数共花费"+time+"s");
		
	}
	private static Integer createParamDir(String path, String systemId) {
		// TODO Auto-generated method stub
		String[] dirName = path.split("\\\\");		
		ParameterDirectory paramDir;
		//"\a\b\c"
		int parentID = 0;
		int i;
		//找到不存在的目录
		for(i=1; i<dirName.length; i++) {
			paramDir = paramDirDAL.Get(
				Op.EQ(GWTParameterDirectory.N_SystemID, systemId),
				Op.EQ(GWTParameterDirectory.N_ParentDirID, parentID),
				Op.EQ(GWTParameterDirectory.N_Name, dirName[i]));
			if(paramDir == null) {
				break;
			}
			parentID = paramDir.getId();
		}
		
		if(i == dirName.length)
			return parentID;
		
		int count = paramDirDAL.Count(
					Op.EQ(GWTParameterDirectory.N_SystemID, systemId),
					Op.EQ(GWTParameterDirectory.N_ParentDirID, parentID));
		
		StringBuffer loaclPath = new StringBuffer();
		if(parentID != 0){
			ParameterDirectory parent = paramDirDAL.Get(Op.EQ(GWTParameterDirectory.N_ID, parentID));
			loaclPath.append(parent.getPath());
		}
		//循环创建不存在的目录
		for(int j=i; j<dirName.length; j++) {					
			paramDir = new ParameterDirectory();
			paramDir.setSystemId(systemId);
			paramDir.setParentDirId(parentID);
			

			loaclPath.append("\\");
			loaclPath.append(dirName[j]);
			paramDir.setPath(loaclPath.toString());
			
			if(j == i)
				paramDir.setSortIndex(count+1);
			else 
				paramDir.setSortIndex(1);
			paramDir.setName(dirName[j]);
			paramDirDAL.Add(paramDir);
			parentID = paramDir.getId();
		}	
		return parentID;
	}
	
	private static boolean validateIP(String hostIP){
		String ip="((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))"; 
		Pattern pattern = Pattern.compile(ip); 
		Matcher matcher = pattern.matcher(hostIP); 
		return matcher.matches();
	}
	
}
