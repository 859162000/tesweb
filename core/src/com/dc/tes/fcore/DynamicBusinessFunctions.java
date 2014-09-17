package com.dc.tes.fcore;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TransactionDynamicParameter;

import com.dc.tes.data.op.Op;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.util.MsgLoader;


public class DynamicBusinessFunctions {

	//GetMsg, GetParam, GetSql; GetMsgData, GetMsgStr; GetParamData, GetParamStr; GetSqlData, GetSqlStr

	private String m_strFieldName = "";
	private String m_strFielValue = "";


	public static void setSystemId() {

	}

	public String GetCurrentDate(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		return GetNowDate(fieldValue);
	}

	public String GetCurrentTime(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		return GetNowTime(fieldValue);
	}

	public String SameAs(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		return "";
	}

	//获取下一个值（顺次加一）
	public String GetNextValue(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		int iNextValue = 0;

		if (fieldValue == null || fieldValue.isEmpty()) { // 输出的是空串
			iNextValue = 1;
			return String.valueOf(iNextValue);
		}

		char[] charFieldValue = fieldValue.toCharArray();
		if (!Utility.isDigitChar(charFieldValue[charFieldValue.length - 1])) {// 最后一位不是数字！
			return fieldValue;
		}

		int iDigitSepPos = GetDigitStrSepPos(charFieldValue);
		// 最左边的字母系列
		String strLeftStr = fieldValue.substring(0, iDigitSepPos + 1);
		// 右边的数字系列
		String strRightDigit = fieldValue.substring(iDigitSepPos + 1);
		if (strRightDigit == null || strRightDigit.isEmpty()) {
			return fieldValue;
		}
		// 计数的数字
		int iCounterSepPos = GetDigitCounterSepPos(strRightDigit.toCharArray());
		String strLeftDigitStr = "";
		String strCounterDigit = "";
		String strCounterDigitStr = "";
		if (iCounterSepPos >= 0) {
			strLeftDigitStr = strRightDigit.substring(0, iCounterSepPos);
			strCounterDigit = strRightDigit.substring(iCounterSepPos);
			int iCounterDigit = Integer.parseInt(strCounterDigit);
			iCounterDigit++;
			String strFormat = "%0" + strCounterDigit.length() + "d";
			strCounterDigitStr = String.format(strFormat, iCounterDigit);
		} else {// 数字为9999
			String strFormat = "%0" + strRightDigit.length() + "d";
			strCounterDigitStr = String.format(strFormat, 0);
		}

		return strLeftStr + strLeftDigitStr + strCounterDigitStr;
	}

	
	//GetMessageValue
	//GetMessageString
	//获取报文数据（比如前一步骤中报文的某一个域）
	public String GetMessageData(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseId", String.valueOf(iCaseId)));
		if (c == null) {
			System.out.println("找不到对应[CaseId=" + iCaseId + "]的案例");
			return null;
		}

		int iCaseSequence = c.getSequence();
		CaseFlow cf = c.getCaseFlow();
		String strNewFieldValue = fieldValue;
		for (int i = iCaseSequence - 1; i >= 0; i--) {
			String stepInPrefix = i + ".in.";
			String stepOutPrefix = i + ".out.";
			if (strNewFieldValue.contains(stepInPrefix)
					|| strNewFieldValue.contains(stepOutPrefix)) { // 这一步的变量或参数有！
				strNewFieldValue = ParseOnePreviousStepMsgFieldValues(
						strNewFieldValue, iExecuteLogId, iCaseId, cf, i);
			}
		}
		return Arithmetic.calculate(strNewFieldValue);
	}


	//GetParameter：自动区分value还是String
	//GetParameterValue
	//GetParameterString
	//获取参数数据值（预先已经定义的参数并且已经有值了，往往引用的是前面步骤中的参数）
	public String GetParameterData(String fieldValue, String fieldName,	int iExecuteLogId, int iCaseId, MsgDocument inData) {

		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseId", String.valueOf(iCaseId)));
		if (c == null) {
			System.out.println("找不到对应[CaseId=" + iCaseId + "]的案例");
			return null;
		}

		int iCaseSequence = c.getSequence();
		CaseFlow cf = c.getCaseFlow();
		String strNewFieldValue = fieldValue;
		for (int i = iCaseSequence - 1; i >= 0; i--) {
			String stepPrefix = i + ".";
			if (strNewFieldValue.contains(stepPrefix)) { // 这一步的变量或参数是确实存在的！
				strNewFieldValue = ParseOnePreviousStepParameterValues(strNewFieldValue, iExecuteLogId, iCaseId, cf, i);
			}
		}

		return Arithmetic.calculate(strNewFieldValue);
	}

	//GetSqlValue
	//GetSqlString
	//GetSqlDate
	//通过SQL参数的查询结果字串
	public String GetSqlParameterResult(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		String strSqlParamName = fieldValue;
		
		//根据参数名称来获取参数定义
		SystemDynamicParameter sysParam = DbGet
				.getSystemParameterByParameterName(strSqlParamName, DbGet.m_sysType.getSystemId());
		String strQueryResult = "";
		try {
			//获取SQL表达式的实际查询结果
			strQueryResult = ParameterProcess.GetSqlQueryingResult(sysParam.getParameterExpression(), 0, sysParam);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return strQueryResult;
	}
	
	
	
	public String GetInMsgData(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		if (inData == null) {
			return "";
		}
		String xmlFiledValue = GetXmlMsgFieldValue(inData.toString(), fieldValue);
		return xmlFiledValue;
	}
	
	
	//------------------------------------------------------------------------------------------------------------
	public int GetDigitStrSepPos(char[] charFieldValue) {
		int i = 0;
		for (i = charFieldValue.length - 1; i >= 0; i--) {
			if (!Utility.isDigitChar(charFieldValue[i])) { // 不是数字了
				break;
			}
		}
		return i;
	}

	public int GetDigitCounterSepPos(char[] digitStr) { // 第一个小于9的数字所在的位置
		int i = 0, counter = 0;
		for (i = digitStr.length - 1; i >= 0; i--) {
			counter++;
			if (counter >= 8) {
				return i;
			}
			if (digitStr[i] < '9') {// 碰到了小于9的数字
				return i;
			}
		}
		return i;
	}

	
	public String GetRequestMsgData(String fieldValue, String fieldName,
			int iExecuteLogId, int iCaseId, MsgDocument inData) {

		String msgFieldValue = null;
		if (inData != null) {
			//报文参数，从报文中获取实际值
			msgFieldValue = ((MsgField)inData.SelectSingleField(fieldValue)).value();
		}
		return msgFieldValue;
	}
	
	
	//解析上一步骤的参数并获取参数值
	public String ParseOnePreviousStepParameterValues(String fieldValue,
			int iExecuteLogId, int iCaseId, CaseFlow cf, int step) {

		if (cf == null) {
			return null;
		}

		// 获取这一步的案例
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseFlow", cf), Op.EQ("sequence", step));
		if (c == null) {
			return null;
		}

		// 获取这一步的案例所对应的案例实例
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance ci = ciDAL.Get(Op.EQ("caseId", Integer.parseInt(c.getCaseId())), Op.EQ("executeLogId", iExecuteLogId));
		if (ci == null) {
			return null;
		}

		// 从请求报文中获取数据
		String strNewFieldValue = fieldValue;
		String stepPrefixDot = step + ".";
		String[] strParamFields = fieldValue.split(stepPrefixDot);
		for (int i = 0; i < strParamFields.length; i++) {
			if (strParamFields[i] == null || strParamFields[i].isEmpty()) {
				continue;
			}
			strParamFields[i] = strParamFields[i].trim();
			String strFieldName = GetPureParamFieldName(strParamFields[i]);
			String strFieldValue = GetPreviouisParameterValue(strFieldName, ci);
			strNewFieldValue = strNewFieldValue.replace(stepPrefixDot + strFieldName, strFieldValue);
		}

		return strNewFieldValue;
	}

	public String GetPreviouisParameterValue(String strParamName, CaseInstance ci) {

		String sTransactionId = ci.getTransactionId().toString();
		TransactionDynamicParameter transParam = ParameterProcess.getTransParameterByParameterName(strParamName, sTransactionId);
		return DbGet.getCaseInstanceParameterValue(strParamName, transParam, ci);
	}

	
	public String ParseOnePreviousStepMsgFieldValues(String fieldValue,
			int iExecuteLogId, int iCaseId, CaseFlow cf, int step) {

		if (cf == null) {
			return null;
		}

		// 获取这一步的案例
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseFlow", cf), Op.EQ("sequence", step));
		if (c == null) {
			return null;
		}

		// 获取这一步的案例所对应的案例实例
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance ci = ciDAL.Get(
				Op.EQ("caseId", Integer.parseInt(c.getCaseId())),
				Op.EQ("executeLogId", iExecuteLogId));
		if (ci == null) {
			return null;
		}

		String strNewFieldValue = fieldValue;
		String outDot = step + ".out.";
		String inDot = step + ".in.";
		if (fieldValue.contains(outDot)) { // 引用了请求报文中的字段
			String[] strOutFields = fieldValue.split(outDot); // 按 "0.out" 劈开
			for (int i = 0; i < strOutFields.length; i++) {
				if (strOutFields[i] == null || strOutFields[i].isEmpty()) {
					continue;
				}
				strOutFields[i] = strOutFields[i].trim();
				// String stepDot = "." + step + ".";
				if (!strOutFields[i].contains(inDot)) {// 排除掉 包含 in. 的字段引用
					String strFieldName = GetPureMsgFieldName(strOutFields[i],
							outDot);
					String strFieldValue = "";
					if (ci.getRequestXml() != null) {
						strFieldValue = GetXmlMsgFieldValue(ci.getRequestXml(),
								strFieldName);
					}
					strNewFieldValue = strNewFieldValue.replace(outDot
							+ strOutFields[i], strFieldValue);
				}
			}
		}

		if (fieldValue.contains(inDot)) { // 引用了应答报文中的字段
			String[] strInFields = strNewFieldValue.split(inDot); // 在新的基础上替换
			for (int i = 0; i < strInFields.length; i++) {
				if (strInFields[i] == null || strInFields[i].isEmpty()) {
					continue;
				}
				strInFields[i] = strInFields[i].trim();
				// String stepDot = "." + step + ".";
				if (!strInFields[i].contains(outDot)) { // 排除掉 包含 out. 的字段引用
					String strFieldName = GetPureMsgFieldName(strInFields[i], inDot);
					String strFieldValue = "";
					if (ci.getResponseXml() != null) {
						strFieldValue = GetXmlMsgFieldValue(ci.getResponseXml(), strFieldName);
					}
					String str2Replace = inDot + strFieldName;
					String strTemp = strNewFieldValue;
					strNewFieldValue = strTemp.replace(str2Replace,	strFieldValue);
				}
			}
		}

		return strNewFieldValue;
	}

	private String GetPureMsgFieldName(String strRawField, String stepPrefixOut) {

		int iParamEndPos = GetParameterEndPosition(strRawField);

		String strNewFieldName = strRawField.substring(0, iParamEndPos);
		// strNewFieldName = strNewFieldName.replace(stepPrefixOut, "");

		return strNewFieldName;
	}

	private String GetPureParamFieldName(String strRawField) {

		int iParamEndPos = GetParameterEndPosition(strRawField);

		String strNewFieldName = strRawField.substring(0, iParamEndPos);

		return strNewFieldName;
	}

	private int GetParameterEndPosition(String strRawField) {

		if (strRawField.length() == 0)
			return 0;
		char[] charRawField = strRawField.toCharArray();
		int i = 0;
		for (i = 0; i < charRawField.length; i++) {
			if (charRawField[i] == ' ' || charRawField[i] == '+'
					|| charRawField[i] == '-' || charRawField[i] == '*'
					|| charRawField[i] == '/') {
				break;
			}
		}
		return i;
	}

	private String GetXmlMsgFieldValue(String strXml, String strFieldName) {

		String strReturnValue = GetXmlMsgFieldValue2(strXml, strFieldName);
		if (strReturnValue.isEmpty()) {
			strReturnValue = GetXmlMsgFieldValue1(strXml, strFieldName);
		}
		return strReturnValue;
	}

	private String GetXmlMsgFieldValue1(String strXml, String strFieldName) {

		try {
			MsgDocument msgDoc = MsgLoader.LoadXml(strXml);
			m_strFielValue = ((MsgField) msgDoc.SelectSingleField(strFieldName))
					.value();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			int iPosDot2 = strFieldName.lastIndexOf(".");
			if (iPosDot2 > 0) {
				String strLeft = strFieldName.substring(0, iPosDot2);
				int iPosDot1 = strLeft.lastIndexOf(".");
				if (iPosDot1 > 0) {
					String strArrayTag = strFieldName.substring(iPosDot1 + 1,
							iPosDot2);
					if (Utility.isDigitString(strArrayTag)) {
						strFieldName = strFieldName.substring(0, iPosDot1 + 1)
								+ strFieldName.substring(iPosDot2 + 1);
						try {
							MsgDocument msgDoc = MsgLoader.LoadXml(strXml);
							m_strFielValue = ((MsgField) msgDoc
									.SelectSingleField(strFieldName)).value();
						} catch (Exception e2) {
							System.out.println(e.getMessage());
							int iPos = strFieldName.lastIndexOf(".");
							m_strFieldName = strFieldName.substring(iPos + 1);

							MsgDocument xmlMsgDoc = MsgLoader.LoadXml(strXml);
							xmlMsgDoc.ForEach(new ISimpleForEachVisitor() {
								@Override
								public void Visit(ForEachSource source,
										MsgItem item) {
									if (item.getAttribute("name").str
											.equals(m_strFieldName)) {
										m_strFielValue = ((MsgField) item)
												.value();
									}
								}
							});
						}
					}
				}
			}
		}

		return m_strFielValue;
	}

	// 从给定XML报文中获取给定的字段（需要考虑重复字段的问题，以及数组.i 字段的问题）
	private String GetXmlMsgFieldValue2(String strXml, String strFieldName) {

		try {
			MsgDocument msgDoc = MsgLoader.LoadXml(strXml);
			m_strFielValue = ((MsgField) msgDoc.SelectSingleField(strFieldName))
					.value();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// 去掉前面的 "CMBSDKPGK.", 基本上都要执行到这里来
			int iPosDot1 = strFieldName.indexOf(".");
			if (iPosDot1 > 0) {
				String strMsgFieldName = strFieldName.substring(iPosDot1 + 1);

				try {
					MsgDocument msgDoc = MsgLoader.LoadXml(strXml);
					m_strFielValue = ((MsgField) msgDoc
							.SelectSingleField(strMsgFieldName)).value();
				} catch (Exception e2) {
					System.out.println(e.getMessage());
					// 取最后的名称来试
					int iPosOfLastDot = strFieldName.lastIndexOf(".");
					m_strFieldName = strFieldName.substring(iPosOfLastDot + 1);

					MsgDocument xmlMsgDoc = MsgLoader.LoadXml(strXml);
					xmlMsgDoc.ForEach(new ISimpleForEachVisitor() {
						@Override
						public void Visit(ForEachSource source, MsgItem item) {
							if (item.getAttribute("name").str
									.equals(m_strFieldName)) {
								m_strFielValue = ((MsgField) item).value();
							}
						}
					});
				}
			}
		}

		return m_strFielValue;
	}

	//获取系统当前日期
	public String GetNowDate(String strDateFormat) {

		if (strDateFormat == null || strDateFormat.isEmpty()) {
			strDateFormat = "yyyyMMdd";
		}
		if (strDateFormat.charAt(0) != 'Y' && strDateFormat.charAt(0) != 'y') {
			strDateFormat = "yyyyMMdd";
		}
		String strCurrentDate = "";
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		strCurrentDate = sdf.format(dt);
		return strCurrentDate;
	}

	
	//获取系统当前时间
	public String GetNowTime(String strDateFormat) {

		if (strDateFormat == null || strDateFormat.isEmpty()) {
			strDateFormat = "HHmmss";
		}
		if (strDateFormat.charAt(0) != 'Y' && strDateFormat.charAt(0) != 'y') {
			strDateFormat = "HHmmss";
		}
		String strCurrentTime = "";
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		strCurrentTime = sdf.format(dt);
		return strCurrentTime;
	}
	
	public static void main(String args[]) {
		DynamicBusinessFunctions cls = new DynamicBusinessFunctions();
		// String s1 = cls.GetNextValue("abc91999a1999", null, 0, 0);
		// System.out.println(s1);
		String s1 = cls.GetMessageData("", null, 567, 8807, null);
	}

}
