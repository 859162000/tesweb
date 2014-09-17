package com.dc.tes.channel.remote;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.cmb.fbsdk.FbSdkJni;
import com.cmb.fbsdk.SdkResult;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.MsgPacker;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.pack.PackSpecification;

public class FbSdkHelper {
	private static FbSdkJni fdSdk = new FbSdkJni();
	private static PackSpecification packSpec;
	
	private static void setPackSpecification(SysType sys) throws Exception{
		IDAL<Channel> chanelIdal = DALFactory.GetBeanDAL(Channel.class);
		Channel channel = chanelIdal.Get(Op.EQ("systemid", sys.getSystemId()), 
				Op.EQ("name", sys.getChannel()));
		if(channel == null){
			throw new Exception("指定通道不存在");
		}
		MsgPacker packer = channel.getPackChannel();
		packSpec = MsgService.LoadPackSpecification(packer.getContent());				
	}
	
	/**
	 * 输入一个String类型的XML报文与Transaction对象，当输入的XML报文为旧功能接口时将调用旧功能接口API，并把结果SdkResult类转换成XML报文输出；
	 * 如非旧功能接口，则直接调用XmlComm接口进行处理，并把SdkResult.getData()中的XML报文输出
	 * @param xml
	 * @param tran
	 * @return xml报文
	 * @throws DocumentException
	 */
	public static String CallFunctionByXML(String xml, Transaction tran) throws DocumentException {
		SdkResult sdkResult= new SdkResult();
		int flag = 0;
		Element doc = DocumentHelper.parseText(xml).getRootElement();
		String funName = doc.element("INFO").element("FUNNAM").getText();
		
		if(funName.equals("Login")){
			sdkResult = Login(doc);
		}else if(funName.equals("Logout")){
			sdkResult = Logout(doc);
		}else if(funName.equals("SetConfig")){
			sdkResult = SetConfig(doc);
		}else if(funName.equals("GetNewNotice")){
			sdkResult = GetNewNotice(doc);
		}else if(funName.equals("GetSysInfo")){
			sdkResult = GetSysInfo(doc);
		}else if(funName.equals("ListMode")){
			sdkResult = ListMode(doc);
		}else if(funName.equals("SetAlive")){
			sdkResult = SetAlive(doc);
		}else if(funName.equals("ListAccount")){
			sdkResult = ListAccount(doc);
		}else if(funName.equals("GetAccInfo")){
			sdkResult = GetAccInfoA(doc);
		}else if(funName.equals("GetAccInfoA")){
			sdkResult = GetAccInfoA(doc);
		}else if(funName.equals("GetTransInfo")){
			sdkResult = GetTransInfoA(doc);
		}else if(funName.equals("GetTransInfoA")){
			sdkResult = GetTransInfoA(doc);
		}else if(funName.equals("Payment")){
			sdkResult = Payment(doc);
		}else if(funName.equals("DirectPayment")){
			sdkResult = DirectPayment(doc);
		}else if(funName.equals("DirectGroupPayment")){
			sdkResult = DirectGroupPayment(doc);
		}else if(funName.equals("GetPaymentInfo")){
			sdkResult = GetPaymentInfo(doc);
		}else if(funName.equals("QueryAgentList")){
			sdkResult = QueryAgentList(doc);
		}else if(funName.equals("AgentRequest")){
			sdkResult = AgentRequest(doc);
		}else if(funName.equals("GetAgentInfo")){
			sdkResult = GetAgentInfo(doc);
		}else if(funName.equals("GetAgentDetail")){
			sdkResult = GetAgentDetail(doc);
		}else if(funName.equals("CreditApply")){
			sdkResult = CreditApply(doc);
		}else if(funName.equals("CreditCancel")){
			sdkResult = CreditCancel(doc);
		}else if(funName.equals("CreditAuth")){
			sdkResult = CreditAuth(doc);
		}else if(funName.equals("CreditQuery")){
			sdkResult = CreditQuery(doc);
		}else if(funName.equals("CreditBnfQuery")){
			sdkResult = CreditBnfQuery(doc);
		}else if(funName.equals("CreditQueryCancel")){
			sdkResult = CreditQueryCancel(doc);
		}else if(funName.equals("CreditQueryAuth")){
			sdkResult = CreditQueryAuth(doc);
		}else if(funName.equals("GetOrderStatus")){
			sdkResult = GetOrderStatus(doc);
		}else if(funName.equals("VerifySignature")){
			sdkResult = VerifySignature(doc);
		}else{
			sdkResult = fdSdk.XmlComm(xml);
			flag = 1;
		}
		String resultXml = "";
		if(flag == 0){
			try {
				resultXml = sdkResultToXml(sdkResult, funName, tran);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			resultXml = sdkResult.getData();
		}
		return resultXml;
	}
	
	

	

	private static SdkResult VerifySignature(Element doc) {
		// TODO Auto-generated method stub
		String param1 = "";
		if(doc.element("SDKTRDVSX").element("SRCDAT")!=null)
			param1 = doc.element("SDKTRDVSX").element("SRCDAT").getText();
		String verkey = "";
		if(doc.element("SDKTRDVSX").element("VERKEY")!=null)
			verkey = doc.element("SDKTRDVSX").element("VERKEY").getText();
		String sigdta = "";
		if(doc.element("SDKTRDVSX").element("SIGDTA")!=null)
			sigdta =doc.element("SDKTRDVSX").element("SIGDTA").getText(); 		
		String param2 ="VERKEY="+verkey+" ;SIGDTA="+ sigdta;
		return new FbSdkJni().VerifySignature(param1, param2);
	}

	private static SdkResult GetOrderStatus(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKTRDQYX")!=null){
			param = parseNodes(doc.element("SDKTRDQYX"));
		}
		return fdSdk.GetOrderStatus(param);
	}

	private static SdkResult CreditQueryAuth(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDAQYX")!=null){
			param = parseNodes(doc.element("SDKCDAQYX"));
		}
		return fdSdk.CreditQueryAuth(param);
	}

	private static SdkResult CreditQueryCancel(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDAQYX")!=null){
			param = parseNodes(doc.element("SDKCDAQYX"));
		}
		return fdSdk.CreditQueryCancel(param);
	}

	private static SdkResult CreditBnfQuery(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDBQYX")!=null){
			param = parseNodes(doc.element("SDKCDBQYX"));
		}
		return fdSdk.CreditBnfQuery(param);
	}

	private static SdkResult CreditQuery(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDAQYX")!=null){
			param = parseNodes(doc.element("SDKCDAQYX"));
		}
		return fdSdk.CreditQuery(param);
	}

	private static SdkResult CreditAuth(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDTATX")!=null){
			param = parseNodes(doc.element("SDKCDTATX"));
		}
		return fdSdk.CreditAuth(param);
	}

	private static SdkResult CreditCancel(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDTCLX")!=null){
			param = parseNodes(doc.element("SDKCDTCLX"));
		}
		return fdSdk.CreditCancel(param);
	}

	private static SdkResult CreditApply(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKCDTAPX")!=null){
			param = parseNodes(doc.element("SDKCDTAPX"));
		}
		return fdSdk.CreditApply(param);
	}

	private static SdkResult GetAgentDetail(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKATDQYX")!=null){
			param = parseNodes(doc.element("SDKATDQYX"));
		}
		return fdSdk.GetAgentDetail(param);
	}

	private static SdkResult GetAgentInfo(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKATSQYX")!=null){
			param = parseNodes(doc.element("SDKATSQYX"));
		}
		return fdSdk.GetAgentInfo(param);
	}

	@SuppressWarnings("unchecked")
	private static SdkResult AgentRequest(Element doc) {
		// TODO Auto-generated method stub
		String param1 = "";
		if(doc.element("SDKATSRQX")!=null){
			param1 = parseNodes(doc.element("SDKATSRQX"));
		}
		String param2 = "";
		if(doc.element("SDKATDRQX")!=null){
			param2 = parseNodes(doc.elements("SDKATDRQX"));
		}
		return fdSdk.AgentRequest(param1, param2);
	}

	private static SdkResult QueryAgentList(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKAGTTSX")!=null){
			param = parseNodes(doc.element("SDKAGTTSX"));
		}
		return fdSdk.QueryAgentList(param);
	}

	private static SdkResult GetPaymentInfo(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKPAYQYX")!=null){
			param = parseNodes(doc.element("SDKPAYQYX"));
		}
		return fdSdk.GetPaymentInfo(param);
	}

	@SuppressWarnings("unchecked")
	private static SdkResult Payment(Element doc) {
		// TODO Auto-generated method stub
		String param1 = "";
		if(doc.element("SDKPAYRQX")!=null){
			param1 = parseNodes(doc.element("SDKPAYRQX"));
		}
		String param2 = "";
		if(doc.element("SDKPAYDTX")!=null){
			param2 = parseNodes(doc.elements("SDKPAYDTX"));
		}
		return fdSdk.Payment(param1, param2);
	}
	
	@SuppressWarnings("unchecked")
	private static SdkResult DirectGroupPayment(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKPAYDTX")!=null){
			param = parseNodes(doc.elements("SDKPAYDTX"));
		}
		return fdSdk.DirectGroupPayment(param);
	}

	@SuppressWarnings("unchecked")
	private static SdkResult DirectPayment(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKPAYDTX")!=null){
			param = parseNodes(doc.elements("SDKPAYDTX"));
		}
		return fdSdk.DirectPayment(param);
	}

	private static SdkResult GetTransInfo(Element doc) {
		// TODO Auto-generated method stub
		String param1 = doc.element("SDKTSINFX").element("C_BBKNBR") == null? ""
				: doc.element("SDKTSINFX").element("C_BBKNBR").getText();
		String param2 = doc.element("SDKTSINFX").element("ACCNBR") == null? ""
				: doc.element("SDKTSINFX").element("ACCNBR").getText();
		String param3 = doc.element("SDKTSINFX").element("BGNDAT") == null? ""
				: doc.element("SDKTSINFX").element("BGNDAT").getText();
		String param4 = doc.element("SDKTSINFX").element("ENDDAT") == null? ""
				: doc.element("SDKTSINFX").element("ENDDAT").getText();
		return fdSdk.GetTransInfo(param1, param2, param3, param4);
	}

	private static SdkResult GetAccInfo(Element doc) {
		// TODO Auto-generated method stub
		String param1 = doc.element("SDKACINFX").element("C_BBKNBR") == null? ""
				: doc.element("SDKACINFX").element("C_BBKNBR").getText();
		String param2 = doc.element("SDKACINFX").element("ACCNBR") == null? ""
				: doc.element("SDKACINFX").element("ACCNBR").getText();
		return fdSdk.GetAccInfo(param1, param2);
	}
	
	private static SdkResult GetTransInfoA(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKTSINFX")!=null){
			param = parseNodes(doc.elements("SDKTSINFX"));
		}
		return fdSdk.GetTransInfoA(param);
	}

	private static SdkResult GetAccInfoA(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKACINFX")!=null){
			param = parseNodes(doc.elements("SDKACINFX"));
		}
		return fdSdk.GetAccInfoA(param);
	}

	private static SdkResult ListAccount(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKACLSTX")!=null){
			param = parseNodes(doc.element("SDKACLSTX"));
		}
		return fdSdk.ListAccount(param);
	}

	private static SdkResult SetAlive(Element doc) {
		// TODO Auto-generated method stub
		return fdSdk.SetAlive();
	}

	private static SdkResult ListMode(Element doc) {
		// TODO Auto-generated method stub
		return fdSdk.ListMode("SDKMDLSTX");
	}

	private static SdkResult GetSysInfo(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKACINFX")!=null){
			param = parseNodes(doc.element("SDKACINFX"));
		}
		return fdSdk.GetSysInfo(param);
	}

	private static SdkResult GetNewNotice(Element doc) {
		// TODO Auto-generated method stub
		return fdSdk.GetNewNotice();
	}

	private static SdkResult SetConfig(Element doc) {
		// TODO Auto-generated method stub9102
		int i = Integer.parseInt(doc.element("SDKCFGSTX").element("CFGTYP").getText());
		return fdSdk.SetConfig(i);
	}

	private static SdkResult Logout(Element doc) {
		// TODO Auto-generated method stub
		return fdSdk.Logout();
	}

	private static SdkResult Login(Element doc) {
		// TODO Auto-generated method stub
		String param = "";
		if(doc.element("SDKUSRLNX")!=null){
			param = parseNodes(doc.element("SDKUSRLNX"));
		}
		return fdSdk.Login(param);
		
	}

	/**
	 * 把XML一个节点下的所有子节点转换成 "nodeName=nodeValue ;nodeName2=nodeValue2"格式
	 * @param element
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String parseNodes(Element element){
		StringBuffer sb = new StringBuffer();
		List list = element.elements();
		Iterator it = list.iterator();
		while(it.hasNext()){
			Element e = (Element)it.next();
			if(sb.length()!= 0){
				sb.append(" ;");
			}
			sb.append(e.getName()+ "=" + e.getText());			
		}
		return sb.toString();
	}
	
	/**
	 * 把XML一个节点下的所有子节点转换成 "nodeName=nodeValue ;nodeName2=nodeValue2"格式
	 * @param element
	 * @return
	 */

	public static String parseNodes(List<Element> list){
		String string = "";
		for(Element e : list){
			if(!string.isEmpty()){
				string += "\r\n";
			}
			string +=parseNodes(e);
		}
		return string;
	}
	
	
	
	
	/**
	 * 把一个旧功能接口API调用返回的SdkResult类转化为xml报文
	 * @param sdkResult
	 * @param funName 接口名称
	 * @param tran  交易类型
	 * @return xml报文 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String sdkResultToXml(SdkResult sdkResult, String funName, Transaction tran) throws Exception{
		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("FUNNAM", funName);
		infoMap.put("DATTYP", "2");
		infoMap.put("RETCOD", String.valueOf(sdkResult.getErrorID()).trim());
		infoMap.put("ERRMSG", sdkResult.getErrorMessage().trim().replace("&", "&amp;"));
		SysType sysType = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemId", tran.getSystemId()));
		setPackSpecification(sysType);
		String templete = tran.getResponseStruct();
		String data = sdkResult.getData();
		if(funName.equals("QueryAgentList")){
			data = AccessQueryAgentListResult(data);
		}
		String[] strs = data.split("\r\n");
		Element doc = DocumentHelper.parseText(templete).getRootElement();
		List<Element> elements = doc.elements();
		for(Element element: elements){
			if(element.attribute("name").getValue().equals("INFO")){
				List<Element> nodes = element.elements();
				for(Element el : nodes){
					if(infoMap.keySet().contains(el.attribute("name").getValue().trim())){
					el.setText(infoMap.get(el.attribute("name").getValue().trim()));
					}
				}
			}else{
				if(strs.length>=1 && !strs[0].trim().isEmpty()){
					doc.remove(element);
					for(int i=0; i<strs.length; i++){
						Element e = element.createCopy();
						Map<String, String> map = parseResult(strs[i]);
						List<Element> nodes = e.elements();
						for(Element el : nodes){
							if(map.keySet().contains(el.attribute("name").getValue().trim())){
							el.setText(map.get(el.attribute("name").getValue().trim()));
							}
						}
						doc.add(e);
					}
					
				}else{
					doc.remove(element);
				}
			}
		}
		XMLWriter xmlWriter = null;  
		StringWriter out = new StringWriter(10240);  
		OutputFormat format = OutputFormat.createPrettyPrint();  
		format.setEncoding("GBK");  
		String ret = new String();
		MsgDocument document = null;
		try  
		{  
		    xmlWriter = new XMLWriter(out, format);  
		    xmlWriter.write(doc);   
		    ret = out.toString();
		//System.out.println(ret);
		    document = MsgLoader.LoadXml(ret);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		byte[] result = MsgService.Pack(document, packSpec, new MsgContext(tran.getTranCode()));
		
		return new String(result, "utf8");
	}
	
	private static String AccessQueryAgentListResult(String result) {
		// TODO Auto-generated method stub
		String[] nodes = result.split(" ;");
		String str = "";
		for(String node : nodes){
			if(!str.isEmpty()){
				str += "\r\n";
			}
			int index = node.indexOf("=");
			str += "TRSTYP="+node.substring(0, index) 
				+ " ;C_TRSTYP=" + node.substring(index+1);
		}	
		return str;
	}

	private static Map<String, String> parseResult(String result){
		Map<String, String>map = new HashMap<String, String>();
		String[] nodes = result.split(" ;");
		for(String node : nodes){
			int index = node.indexOf("=");
			map.put(node.substring(0, index), node.substring(index+1));
		}	
		return map;
	}
	
	public static boolean isLogin(String xml) throws DocumentException{
		Element doc = DocumentHelper.parseText(xml).getRootElement();
		String funName = doc.element("INFO").element("FUNNAM").getText();
		
		if(funName.equals("Login")){
			return true;
		}else {
			return false;
		}
	}

}

