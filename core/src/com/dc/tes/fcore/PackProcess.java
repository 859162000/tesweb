package com.dc.tes.fcore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;

public class PackProcess {
	
	private static final Log log = LogFactory.getLog(PackProcess.class);
	
	
	
	/**
	 * 组包前做报文预处理
	 * 设置默认值
	 */
	public static MsgDocument setDefaultValue(final OutMessage out) throws Exception {		

		MsgDocument outMsg = out.data;

		//逐个处理请求报文中的变量字段
		outMsg.ForEach(new ISimpleForEachVisitor() {
			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				//给定值为空，但是默认值不空的字段，用默认值填充
				if (item instanceof MsgField) {
					if (!item.getAttribute("variable").str.equals("true")) { //非变量字段
						//字段名
						String strMsgItemName = item.getAttribute("name").str;
						if (!strMsgItemName.isEmpty() && item.getAttribute("defaultValue") != null) {				
							//字段值
							String strFieldValue = ((MsgField)item).value();
							//默认值
							String strDefaultValue = item.getAttribute("defaultValue").str;
							if (strFieldValue.isEmpty() && !strDefaultValue.isEmpty()) {
								((MsgField)item).set(strDefaultValue);
							}
						}
					}
				}
			}
		});
		
		return outMsg;
	}
	
	
	/**
	 * 组包前做报文预处理
	 * 处理报文中含有变量表达式等
	 */
	public static MsgDocument prePackProcess(final OutMessage out, final InMessage in) throws Exception {
			
		final Map<String, Integer> dynamicParamArrayList = new HashMap<String, Integer>();
		
		MsgDocument rawOutMessage = out.data;
		dynamicParamArrayList.clear();
		
		//逐个处理请求报文中的变量字段
		rawOutMessage.ForEach(new ISimpleForEachVisitor() {
			
			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				
				if (item.getAttribute("variable").str.equals("true")) { //为变量字段
					//字段名
					String strMsgItemName = item.getAttribute("name").str;
					//动态函数类型（GetNextValue, GetCurrentDate, GetMessageData, GetParameterData, GetSqlParameterResult）
					String strFormatType = item.getAttribute("format").str; // function? format?
					//字段的参数值（初始字段值、表达式）
					String strFieldValueExpr = ((MsgField)item).value();
					if (strFormatType.contains("(")) {
						if (strFieldValueExpr == null || strFieldValueExpr.isEmpty()) {
							strFieldValueExpr = strFormatType.substring(strFormatType.indexOf("(") + 1, strFormatType.length());
							strFieldValueExpr = strFieldValueExpr.replace(")", "");
						}
						strFormatType = strFormatType.substring(0, strFormatType.indexOf("("));
					}
					if (strMsgItemName.isEmpty() || strFormatType.isEmpty()) {
						return;
					}
					
					//动态函数的参数
					String strFuncExpr = strFieldValueExpr;
					//函数的最后返回值
					String strFuncValue = strFuncExpr;
					
					String daytime_operators[] = {".add_year", ".add_month", ".add_day", ".add_hour", ".add_minute", ".add_second"};
					int iDayTimeTag = -1;
					int iDayTimeGap = 0;
					
					//支持日期的运算
					if (strFieldValueExpr.toLowerCase().contains(".add_")) {
						for (int i=0; i<daytime_operators.length;i++) {			
							if (strFieldValueExpr.toLowerCase().contains(daytime_operators[i])) {
								int iPosOfDotAdd = strFieldValueExpr.indexOf(daytime_operators[i]);
								String strAddDaytimeExpr = strFieldValueExpr.substring(iPosOfDotAdd);
								int iPosOfLeftBracket = strAddDaytimeExpr.indexOf("(");
								int iPosOfRightBracket = strAddDaytimeExpr.indexOf(")");
								String strAddDaytimeGap = strAddDaytimeExpr.substring(iPosOfLeftBracket+1, iPosOfRightBracket);
								if (strAddDaytimeGap != null && !strAddDaytimeGap.isEmpty()) {
									strAddDaytimeGap = strAddDaytimeGap.trim();
									try {
										iDayTimeGap = Integer.parseInt(strAddDaytimeGap);
										strFuncExpr = strFieldValueExpr.substring(0, iPosOfDotAdd);
										iDayTimeTag = i;
									}
									catch(Exception epi) {
										epi.printStackTrace();
									}
								}
								break;
							}
						}
					}
					
					String strSubstrParams = "";
					if (strFieldValueExpr.toLowerCase().contains(".substring")) {
						int iPos = strFieldValueExpr.indexOf(".substring");
						String strSubstrExpr = strFieldValueExpr.substring(iPos);
						strSubstrParams = strSubstrExpr.replace(".substring", "");
						strFuncExpr = strFuncExpr.replace(strSubstrExpr, "");
					}
					
					if (strFuncExpr != null && !strFuncExpr.isEmpty() && strFuncExpr.contains(".i.")) {
						if (!dynamicParamArrayList.containsKey(strFuncExpr)) {
							dynamicParamArrayList.put(strFuncExpr, 0);
							strFuncExpr = strFuncExpr.replace(".i.", ".0.");
						}
						else {
							int i = dynamicParamArrayList.get(strFuncExpr);
							dynamicParamArrayList.put(strFuncExpr, i++);
							strFuncExpr = strFuncExpr.replace(".i.", "." + i + ".");
						}
					}
					
					try {
						MsgDocument msgDoc = out.data;
						if (strFormatType.toLowerCase().equals(("GetInMsgData").toLowerCase())) {
							msgDoc = in.data;
						}
						//调用动态的业务处理函数
						strFuncValue = invokeDynamicBusinessFunction(strFuncExpr, strFormatType, strMsgItemName,out.executeLogID,out.caseID, msgDoc);
					} catch (NoSuchMethodException e) {
						log.error(e);
					} catch (IllegalAccessException e) {
						log.error(e);
					} catch (InvocationTargetException e) {
						log.error(e);
					} 
					
					if ("GetNextValue".equals(strFormatType)) { //把获取到的下一个值进行回写
						try {
							DbSet.replaceCaseXmlMsgContent(out.caseID, item.toString(), ((MsgField)item).value(), strFuncValue);
						}
						catch(Exception e){
							log.error(e);
						}
					}
			
					if (iDayTimeTag >= 0 && strFuncValue != null && !strFuncValue.isEmpty()) {
						
						SimpleDateFormat sdf;

						if (iDayTimeTag <= 2)
							sdf = new SimpleDateFormat("yyyyMMdd");
						else
							sdf = new SimpleDateFormat("HHmmss");
						
						Calendar cal = Calendar.getInstance();
						try {
							cal.setTime(sdf.parse(strFuncValue));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						switch (iDayTimeTag) {
						case 0:
							cal.add(Calendar.YEAR, iDayTimeGap);
							break;
						case 1:
							cal.add(Calendar.MONTH, iDayTimeGap);
							break;
						case 2:
							cal.add(Calendar.DATE, iDayTimeGap);
							break;
						case 3:
							cal.add(Calendar.HOUR, iDayTimeGap);
							break;
						case 4:
							cal.add(Calendar.MINUTE, iDayTimeGap);
							break;
						case 5:
							cal.add(Calendar.SECOND, iDayTimeGap);
							break;
						default: 
							break;
						}					
						
						try {
							strFuncValue = sdf.format(cal.getTime());
						} catch (Exception pe) {
							pe.printStackTrace();
						}
					}
					
					if (strFieldValueExpr.toLowerCase().contains(".substring") && strSubstrParams != null && !strSubstrParams.isEmpty()) {
						strSubstrParams = strSubstrParams.replace("(", "");
						strSubstrParams = strSubstrParams.replace(")", "");
						if (strSubstrParams != null && !strSubstrParams.isEmpty()) {
							int iBeginIndex = 0, iEndIndex = 0;
							String strParams[] = strSubstrParams.split(",");
							if (strParams.length == 1) {
								try {
									iBeginIndex = Integer.parseInt(strParams[0].trim());
									strFuncValue = strFuncValue.substring(iBeginIndex);
								}
								catch(Exception pie) {
									System.out.println(pie.getMessage());
								}
							}
							else if (strParams.length == 2) {
								try {
									iBeginIndex = Integer.parseInt(strParams[0].trim());
									iEndIndex = Integer.parseInt(strParams[1].trim());
									strFuncValue = strFuncValue.substring(iBeginIndex, iBeginIndex + iEndIndex);
								}
								catch(Exception pie) {
									System.out.println(pie.getMessage());
									
								}
							}
						}
					}
					
					((MsgField)item).set(strFuncValue);
				}
			}
		});
		
		return rawOutMessage;
	}
	
	
	//调用动态业务处理函数（methodName为调用的方法名称，fieldValue为字段值、变量表达式，fieldName为字段名）
	public static String invokeDynamicBusinessFunction(String fieldValue, String methodName, String fieldName, String logID, String caseID, MsgDocument inData) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException  {
				
		try {
			DynamicBusinessFunctions dynamicBusiFunc = new DynamicBusinessFunctions();
	        Class<? extends DynamicBusinessFunctions> clazz = dynamicBusiFunc.getClass(); 
			Method method = clazz.getMethod(methodName, String.class, String.class, int.class, int.class, MsgDocument.class);
			//dynamicBusiFunc中隐含了 动态函数
			int iExecuteLogId = 0, iCaseId = 0;
			if (logID != null) {
				iExecuteLogId = Integer.parseInt(logID);
			}
			if (caseID != null) {
				iCaseId = Integer.parseInt(caseID);
			}
			return (String) method.invoke(dynamicBusiFunc, fieldValue, fieldName, iExecuteLogId, iCaseId, inData); 
		} catch (Exception e) {
			log.error(e);

		}
		return fieldValue;
	}

}
