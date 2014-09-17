package com.dc.tes.ui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.InterfaceDef;
import com.dc.tes.data.model.InterfaceField;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.dc.tes.ui.client.model.GWTInterfaceField;
import com.dc.tes.ui.client.model.GWTSimuSystem;

public class As400InterfaceImport extends HttpServlet {
	private static final Log log = LogFactory
			.getLog(As400InterfaceImport.class);
	private static final long serialVersionUID = 1L;
	private IDAL<InterfaceDef> iDefDAL = DALFactory.GetBeanDAL(InterfaceDef.class);
	private IDAL<InterfaceField> fieldDAL = DALFactory.GetBeanDAL(InterfaceField.class);
	public As400InterfaceImport() {
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
			throws ServletException, IOException{
		response.setCharacterEncoding("gb2312");
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
					if(sys == null)
					{
						throw new DownLoadException("无法获得当前模拟系统信息，无法上传");
					}
					String userId = request.getParameter("userId");
					if(userId.equals("Administrator")){
						userId = "0";
					}
					resultList = UploadInterface(sys.getSystemId(), userId,
								item, request, response);	
										
					break;
				}
			}
		} catch (DownLoadException ex) {
			ex.printStackTrace();
			response.getOutputStream()
					.write(ex.getMessage().getBytes("gb2312"));
			response.flushBuffer();
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getOutputStream().write(
					"error:发生异常，请与管理员联系。".getBytes("gb2312"));
			response.flushBuffer();
			return;
		}
		String msg = resultList.toString();
		response.getOutputStream().write(msg.getBytes("utf8"));
		response.flushBuffer();
	}



	private List<String> UploadInterface(String systemId, String userId,
			FileItemStream item, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
		InputStream input = null ;
		Integer loginLogId = Integer.parseInt(request.getParameter("loginLogId").toString());
		List<String> resultList = new ArrayList<String>();
		try{             
			input = item.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "gb2312")); 
			String str = null;  
			StringBuffer sb = new StringBuffer();
			List<String> strs = new ArrayList<String>();
			while((str = reader.readLine()) != null){    
				if(!sb.toString().isEmpty())
					sb.append("\n");
				sb.append(str);
				strs.add(str);
			}  
			reader.close();  			
			System.out.println(sb.toString());
			deserializeInterfaces(strs, userId, systemId, loginLogId, resultList);
		}catch(Exception e){
			log.error(e);
		}finally{
             if (input != null){
                try{
                	input.close();
                }catch(IOException e){
                    e.getStackTrace();
                }
            }
        }
		return resultList;
	}

	private void deserializeInterfaces(List<String> strs, String userId, String systemId, 
			Integer loginLogId, List<String> resultList) {
		// TODO Auto-generated method stub
		int succNum = 0;
		int errorNum = 0;
		InterfaceDef def = null;   //用来存储当前接口定义
		List<InterfaceField> fields = new ArrayList<InterfaceField>();   // 用来存储当前接口下的所有字段
		int row = 1; //用来记录当前行数
		for(String str : strs){
			try{
				
				if(str.startsWith(":")){   //接口定义
					if(def != null){ //不是第一个接口，需更新当前接口字段数与长度
						int len = 0;
						for(InterfaceField field : fields){
							len += field.getFieldLen();
						}
						def.setFieldCount(fields.size());
						def.setInterfaceLen(len);
						iDefDAL.Edit(def);
						succNum++;
					}				
					
					str = str.substring(1);
					String args[] = str.split("\\|");
					String name = args[0];	
					String chnName = "";
					if(args.length >1){
						chnName = args[1].trim();
					}
					InterfaceDef interfaceDef = null;
					interfaceDef = iDefDAL.Get(Op.EQ(GWTInterfaceDef.N_InterfaceName, name), 
							Op.EQ(GWTInterfaceDef.N_SystemID, systemId));
					if(interfaceDef == null){ //如果不存在同名则新增
						interfaceDef = new InterfaceDef();
					}else{ //存在则更新接口定义，需先删除原有接口下的所有字段
						List<InterfaceField> fs = fieldDAL.ListAll(Op.EQ(GWTInterfaceField.N_InterfaceDefID, 
								interfaceDef.getInterfaceId()));
						for(InterfaceField f : fs){
							fieldDAL.Del(f);
						}
					}
					interfaceDef.setInterfaceName(name);
					interfaceDef.setChineseName(chnName);
					interfaceDef.setImportTime(new Date());
					interfaceDef.setImportUserId(userId);
					interfaceDef.setSystemId(systemId);
					interfaceDef.setFieldCount(0);
					interfaceDef.setInterfaceLen(0);
					interfaceDef.setMemo("");
					if(interfaceDef.getInterfaceId() == null){						
						iDefDAL.Add(interfaceDef);
						OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Import, 
								interfaceDef.getInterfaceId(), interfaceDef.getInterfaceName(),
								"interfaceName", null, interfaceDef.getInterfaceName(), loginLogId);
					}else {
						iDefDAL.Edit(interfaceDef);
						OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Import, 
								interfaceDef.getInterfaceId(), interfaceDef.getInterfaceName(),
								"interfaceName", interfaceDef.getInterfaceName(), 
								interfaceDef.getInterfaceName(), loginLogId);		
					}
					def = interfaceDef;
					fields.clear(); //清空字段List
				}else if(!str.trim().isEmpty()){ //   xHdrVar|2	0	A	^报文头版本号
					if(def == null){
						continue;
					}
					str = str.trim();
					int f = str.indexOf("|");
					String name = str.substring(0, f);
					String args[] = str.substring(f+1).split("\\s+");;
					List<String> ss = new ArrayList<String>();
					for(int i = 0; i < args.length; i++){
						if(args[i].trim().isEmpty()){
							continue;
						}
						ss.add(args[i]);
					}
					int len = Integer.parseInt(ss.get(0));
					int decimal = Integer.parseInt(ss.get(1));
					String type = ss.get(2);					
					String chnName = "";
					if(ss.size() > 3){
						for(int i = 3; i<ss.size(); i++){
							chnName += ss.get(i);
						}
						chnName = chnName.trim().substring(1);
					}
					InterfaceField field = new InterfaceField();
					field.setChineseName(chnName);
					field.setDecimalDigits(decimal);
					field.setFieldLen(len);
					field.setFieldName(name);
					field.setDefaultValue("");
					field.setInterfaceDefId(def.getInterfaceId());
					field.setFieldType(type);
					String fieldTypeExpr = type+ "(" + len + 
					(type.equals("S")? ","+decimal : "") + ")";	
					field.setFieldTypeExpr(fieldTypeExpr);
					field.setMemo("");
					field.setOptional("O");
					field.setSequence(fields.size());
					fieldDAL.Add(field);
					fields.add(field);
				}
				if(row == strs.size()){
					if(def != null){ //更新最后一个接口字段数与长度
						int len = 0;
						for(InterfaceField field : fields){
							len += field.getFieldLen();
						}
						def.setFieldCount(fields.size());
						def.setInterfaceLen(len);
						iDefDAL.Edit(def);
						succNum++;
					}
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				log.error(e);
				errorNum++;
				def = null;
				fields.clear();
			}
			row++;
		}
		resultList.add("newadd:" + "成功添加或更新接口记录"+succNum+"条");
		resultList.add("error:" + "跳过接口记录"+errorNum+"条");
	}
}
