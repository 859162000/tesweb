package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.OperationLog;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IOperationLogService;
import com.dc.tes.ui.client.model.GWTOperationLog;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.model.LoginLog;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.util.type.Pair;


public class OperationLogService extends RemoteServiceServlet implements IOperationLogService{

	private static final long serialVersionUID = 4047315395382949028L;

	IDAL<OperationLog> operationLogDAL = DALFactory.GetBeanDAL(OperationLog.class);
	IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);

	private static final Log log = LogFactory.getLog(OperationLogService.class);
	private static IDAL<LoginLog> loginLogDAL = DALFactory.GetBeanDAL(LoginLog.class);
	private static IDAL<OperationLog> opLogDAL = DALFactory.GetBeanDAL(OperationLog.class);
	private static String[] exceptList = {"createdTime", "createdUserId", "lastModifiedTime", "ResponseStruct",
		"lastModifiedUserId", "Basecfg", "IsParamModified", "requestMsg", "requestXml", "expectedXml", "caseFlow"};
	
	@Override
	public PagingLoadResult<GWTOperationLog> GetList(String systemID,
			String searchKey, String loginLogID ,PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try {
			List<GWTOperationLog> resultList = new ArrayList<GWTOperationLog>();
			Op[] conditions;
			int count = 0;
			List<OperationLog> lst = null;
			List<Op> conList = new ArrayList<Op>();
			conList.add(Op.EQ("systemId", systemID));	
			if(!loginLogID.isEmpty())
				conList.add(Op.EQ("loginLogId", Integer.parseInt(loginLogID)));
			
			conditions = new Op[conList.size()];
			for(int i = 0; i < conList.size(); i++){
				conditions[i] = conList.get(i);
			}
			
			if(searchKey.isEmpty()) {
				count = operationLogDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = operationLogDAL.List(pse.getStart(), pse.getEnd(),
						conditions);
			} else {
				String[] properties = {GWTOperationLog.N_IduType,GWTOperationLog.N_OpType};
				count = operationLogDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = operationLogDAL.Match(searchKey, properties,
						pse.getStart(), pse.getEnd(), conditions);
			}
			
			for(OperationLog log : lst) {
				resultList.add(BeanToModel(log));
			}
			return new BasePagingLoadResult<GWTOperationLog>(resultList,
					config.getOffset(), count);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}

	private GWTOperationLog BeanToModel(OperationLog log) {
		// TODO Auto-generated method stub
		User user = userDAL.Get(Op.EQ("id", log.getUserId()));
		GWTOperationLog gwt = new GWTOperationLog(log.getId().toString(),log.getSystemId(),user.getId(),
				user.getName(),log.getLoginLogId().toString(),log.getObjId().toString(),log.getObjName(),
				log.getIduType().toString(),log.getOpType().toString(),log.getOpField(),log.getOldValue(),
				log.getNewValue(),log.getMemo());
		return gwt;
	}
	
	public String writeLoginLog(String userId, String systemId, Integer loginLogId){
		try{
			if(loginLogId != null){
				LoginLog old = getLoginLogById(loginLogId);
				old.setLogoutTime(new Date());
				loginLogDAL.Edit(old);
			}
			
			LoginLog loginLog = new LoginLog();
			if(userId.equals("Administrator")){
				userId = "0";
			}
			loginLog.setUserId(userId);
			loginLog.setSystemId(systemId);
			HttpServletRequest request = getThreadLocalRequest();
			String ipAddress = "";
			if (request.getHeader("x-forwarded-for") == null) {
				ipAddress = request.getRemoteAddr();
			}else{
				ipAddress = request.getHeader("x-forwarded-for");
			}
			loginLog.setIpAddress(ipAddress);
			loginLog.setMachineName(request.getRemoteHost());
			loginLog.setLoginTime(new Date());
			int count = loginLogDAL.Count(Op.EQ("userId", userId), 
					Op.EQ("ipAddress", ipAddress));
			loginLog.setLoginCount(count);
			loginLogDAL.Add(loginLog);
			return loginLog.getId().toString();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
		}
		return null;
		
	}
	
	public static void writeOperationLog(OpType opType, IDUType iduType,
			Integer objId, String objName, String opField,
			String oldValue, String newValue, Integer loginLogId){
		LoginLog loginLog = getLoginLogById(loginLogId);
		OperationLog oLog = new OperationLog(loginLog.getSystemId(), loginLog.getUserId());
		oLog.setIduType(iduType.getDbValue());
		oLog.setOpType(opType.getDbValue());
		oLog.setObjId(objId);
		oLog.setObjName(objName);
		oLog.setOpField(opField);
		oLog.setOldValue(oldValue);
		oLog.setNewValue(newValue);
		oLog.setLoginLogId(loginLogId);
		opLogDAL.Add(oLog);
	}
	
	public static LoginLog getLoginLogById(Integer loginLogId){
		try{
			return loginLogDAL.Get(Op.EQ("id", loginLogId));
		}catch (Exception e) {
			// TODO: handle exception
			log.error(e);
		}
		return null;
	}
	
	public static void writeUpdateOperationLog(OpType opType, Class<?> beanClass, Integer objId, String objName, 
			Object oldBean, Object newBean, Integer loginLogId){
		Map<String, Pair<String, String>> map = Reflect_Object_Compare(oldBean, newBean, beanClass);
		for(String fieldName : map.keySet()){
			Pair<String, String> pair = map.get(fieldName);
			writeOperationLog(opType, IDUType.Update, objId, objName, fieldName, pair.getA(), pair.getB(), loginLogId);
		}
	}
	
	
	public static Map<String, Pair<String, String>> Reflect_Object_Compare(Object oldBean, Object newBean, Class<?> beanClass){
		Map<String, Pair<String, String>> map = new HashMap<String, Pair<String,String>>();
		try {        	
            Method[] methods = beanClass.getDeclaredMethods();//获得类的方法集合       
            //遍历方法集合
            for(int i =0 ;i<methods.length;i++){
               //获取所有getXX()的返回值
               //methods[i].getName()方法返回方法名
               if(methods[i].getName().startsWith("get")){
            	   if(!isNeedWrite(methods[i].getName().substring(3))){
            		   continue;
            	   }
                   Object oldObject = methods[i].invoke(oldBean);
                   Object newObject = methods[i].invoke(newBean);
                   String fieldName = methods[i].getName().substring(3);
                   if(oldObject == null && newObject != null){
                	   map.put(fieldName, new Pair<String, String>(null, newObject.toString()));
                   }else if(oldObject != null && newObject == null){
                	   map.put(fieldName, new Pair<String, String>(oldObject.toString(), null));
                   }else if(oldObject == null && newObject == null){
                	   continue;
                   }else if(!oldObject.toString().equals(newObject.toString())){
                	   map.put(fieldName, new Pair<String, String>(oldObject.toString(), newObject.toString()));
                   }
               }
            }
            return map;
           } catch (Exception e) {
               e.printStackTrace();
           }
           return map;
    }
	
	private static boolean isNeedWrite(String fieldName){
		for(int i = 0; i < exceptList.length; i++){
			if(fieldName.equalsIgnoreCase(exceptList[i])){
				return false;
			}
		}
		return true;
	}

	@Override
	public void deleteOperationLog(List<GWTOperationLog> logs) {
		// TODO Auto-generated method stub
		try{
			for(GWTOperationLog operationLog : logs){
				OperationLog log = new OperationLog();
				log.setId(Integer.parseInt(operationLog.get(GWTOperationLog.N_ID).toString()));
				log.setSystemId(operationLog.get(GWTOperationLog.N_SystemID).toString());
				log.setUserId(operationLog.get(GWTOperationLog.N_UserID).toString());
				operationLogDAL.Del(log);
			}
		}catch (Exception ex) {
			throw new RuntimeException(ex);	
		}
	}

}
