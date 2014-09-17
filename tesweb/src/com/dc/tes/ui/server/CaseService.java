package com.dc.tes.ui.server;


import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Card;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.unpack.UnpackSpecification;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTCard;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.StringUtil;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.ui.util.TranStructTreeUtil;
import com.dc.tes.util.RuntimeUtils;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 案例数据管理服务类 1）案例基本信息的增删改查 2）案例数据、预期结果的返回和更新 3）案例执行后结果比对结果的返回
 * 
 * @author scckobe
 * 
 */
public class CaseService extends RemoteServiceServlet implements ICaseService {
	
	private static final Log log = LogFactory.getLog(CaseService.class);
	private static final long serialVersionUID = -8137982802313904990L;
	IDAL<Case> caseDao = DALFactory.GetBeanDAL(Case.class);

	
	public static GWTCase BeanToModel(Case caseInfo) {
		if (caseInfo == null)
			return null;
		//获取交易类型
		Transaction tran = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ("transactionId", caseInfo.getTransactionId()));
		//获取业务流编号、名称
		IDAL<Card> cardDAL = DALFactory.GetBeanDAL(Card.class);
		Card card= cardDAL.Get(Op.EQ(GWTCard.N_ID, caseInfo.getCardId()));
		String cardNo;
		if(card!=null)
			cardNo = card.getCardNumber();
		else cardNo = "";
		CaseFlow caseflow = caseInfo.getCaseFlow();
		
		GWTCase gwtCase = new GWTCase(caseInfo.getCaseId(), 
				caseInfo.getTransactionId(), 
				caseInfo.getCaseName(), 
				caseInfo.getIsParseable(), 
				caseInfo.getFlag(),
				caseInfo.getCaseNo(), 
				cardNo, tran.getTranName(),
				caseflow!=null? caseflow.getCaseFlowNo():"",
				caseflow!=null? caseflow.getCaseFlowName():"",
				caseInfo.getAmount()==null?"":caseInfo.getAmount().toString(),
				caseInfo.getSequence()==null?"":caseInfo.getSequence().toString(),
				caseInfo.getDescription(),
				caseInfo.getIsdefault());
		gwtCase.SetResponseData(caseInfo.getRequestMsg());
		gwtCase.setN_tran(TransactionService.BeanToModel(tran));
		gwtCase.setBreakPointFlag(caseInfo.getBreakPointFlag()==null?0:caseInfo.getBreakPointFlag());
		if(caseflow != null)
			gwtCase.setCaseFlow(BatchService.BeanToModel(caseflow));
		return gwtCase;
	}

	public static Case ModelToBean(Case caseBean, GWTCase gwtCaseInfo) {
		if (gwtCaseInfo == null)
			return null;
		Case caseInfo = caseBean;
		if (caseInfo == null) {
			caseInfo = new Case();
			caseInfo.setRequestMsg(new byte[0]);
			caseInfo.setRequestXml("");
		}
		if (!gwtCaseInfo.GetCaseId().isEmpty()){
			IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
			caseInfo = caseDAL.Get(Op.EQ("caseId", gwtCaseInfo.GetCaseId()));
		}
		caseInfo.setCaseName(gwtCaseInfo.GetCaseName());
		caseInfo.setCaseNo(gwtCaseInfo.GetCaseNo());
		caseInfo.setIsParseable(gwtCaseInfo.GetCaseParse());
		caseInfo.setFlag(gwtCaseInfo.GetFlag());
		caseInfo.setTransactionId(gwtCaseInfo.GetTransactionID());
		caseInfo.setBreakPointFlag(gwtCaseInfo.getBreakPointFlag()==null?0:Integer.parseInt(gwtCaseInfo.getBreakPointFlag()));
		caseInfo.setAmount(gwtCaseInfo.GetAmount()==null||gwtCaseInfo.GetAmount().isEmpty()?null:Float.parseFloat(gwtCaseInfo.GetAmount()));
		caseInfo.setDescription(gwtCaseInfo.GetDesc());
		caseInfo.setIsdefault(gwtCaseInfo.GetDefault());
		IDAL<CaseFlow> caseflowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		if(gwtCaseInfo.getCaseFlow() != null) {
			CaseFlow caseFlow = caseflowDAL.Get(Op.EQ("id", Integer.parseInt(gwtCaseInfo.getCaseFlow().GetID())));
			caseInfo.setCaseFlow(caseFlow);
		}
		String sequence = gwtCaseInfo.GetSequence();
		if(!StringUtil.IsNullorEmpty(sequence))
			caseInfo.setSequence(Integer.parseInt(gwtCaseInfo.GetSequence()));
	//	caseInfo.setImportBatchNo(gwtCaseInfo.GetImportBatchNo());
		return caseInfo;
	}

	
	@Override
	public String SetDefaultCase(String tranID,String caseID) {
		String msg = "";
		try {
			//获得之前的默认案例
			Case lstDefault = caseDao.Get(Op.EQ(GWTCase.N_transactionId, tranID),Op.EQ(GWTCase.N_default, 1));
			
			//设置当前的案例
			Case curDefault = caseDao.Get(Op.EQ(GWTCase.N_caseId, caseID));
			if(curDefault == null)
				return "当前案例已被其他用户删除，设置失败";
			else
			{
				curDefault.setIsdefault(1);
				caseDao.Edit(curDefault);
				
				if(lstDefault != null)
				{
					lstDefault.setIsdefault(0);
					caseDao.Edit(lstDefault);
				}
			}
		} catch (Exception ex) {
			msg = "服务器请求失败";
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		return msg;
	}
	
	@Override
	public void DeleteCase(GWTSimuSystem sysInfo, List<GWTCase> caseList, Integer loginLogId) {
		try {
			for (int i = 0; i < caseList.size(); i++) {	
				Case casebean = ModelToBean(null, caseList.get(i));
				OperationLogService.writeOperationLog(OpType.Case, IDUType.Delete,  
						Integer.parseInt(casebean.getCaseId()), casebean.getCaseName(),
						"caseName", casebean.getCaseName(), null, loginLogId);
				caseDao.Del(casebean);
				if(casebean.getCaseFlow()!=null){
					updateCaseFlowStepCount(casebean.getCaseFlow());
					updateCaseSequence(casebean.getCaseFlow());
				}
				new QueueService().DeleteCaseTask(caseList.get(i).GetCaseId());
			}
			new HelperService().SendToBack(sysInfo);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	private void updateCaseSequence(CaseFlow caseFlow) {
		List<Case> cases = caseDao.ListAll(GWTCase.N_Sequence, true, Op.EQ("caseFlow.id", caseFlow.getId()));
		for(int i=0; i<cases.size(); i++){
			cases.get(i).setSequence(i);
			caseDao.Edit(cases.get(i));
		}
	}

	@Override
	public PagingLoadResult<GWTCase> GetCaseList(String tranID,
			String searchKey, PagingLoadConfig config) {
		try {
			int count;
			List<Case> lst;
			Op[] conditions = new Op[] { Op.EQ(GWTCase.N_transactionId, tranID) };
			if (searchKey.isEmpty()) {
				count = caseDao.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseDao.List(pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = { GWTCase.N_caseName };
				count = caseDao.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseDao.Match(searchKey, properties, pse.getStart(), pse
						.getEnd(), conditions);
			}

			List<GWTCase> returnList = new ArrayList<GWTCase>();
			for (Case c : lst)
				returnList.add(BeanToModel(c));

			return new BasePagingLoadResult<GWTCase>(returnList, config
					.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTPack_Struct GetCaseContent(String caseId, boolean isCaseData, int isClientSimu) {
		Case caseInfo = null;
		try {
			caseInfo = GetCaseBean(caseId);
			ISystemConfig config = SystemConfigManager.getConfigByTranID(caseInfo.getTransactionId(), isClientSimu);
			
			boolean isRes = GetIsResForCase(isClientSimu, isCaseData);
			return TranStructTreeUtil.GetGWTTreeRoot(caseInfo, isCaseData, isRes, config, isClientSimu);
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	@Override
	public String GetRespData(GWTPackNeed needInfo,String caseID, int isClientSimu, GWTPack_Struct root, String charsetStr) {
		if (root == null)
			throw new RuntimeException("error:未取得报文结构, root为空");

		try {
			ISystemConfig config = SystemConfigManager.getConfigByCaseID(caseID, isClientSimu);
			// 设置报文结构
			boolean isRes = GetIsResForCase(isClientSimu, true);
			String xmlContent = TranStructTreeUtil.GetMsgDocument(root, isRes, config, 2).toString();

			String specStr = "";
			try	{
				specStr = GetPackContent(needInfo);
			}
			catch (Exception e) {
				throw new RuntimeException("error:" + e.getMessage());
			}
			
			byte[] packedMsg = SystemConfigManager.PackSpecification(specStr, xmlContent,needInfo.GetTranCode());
			if (packedMsg == null)
				throw new RuntimeException("error:尝试调用组包失败!");
			else
				return GetResultData(packedMsg, charsetStr);
		} catch (RuntimeException e) {
			log.error(e, e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("遇到未处理的错误的，请与管理员联系");
		}
	}
	
		
	@Override
	public GWTPack_Struct ImportRecordedCaseData(GWTPackNeed needInfo, String msgStr, String caseID, int isClientSimu, GWTPack_Struct root) {
		byte[] msg;
		try {
			msg = msgStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		return ImportRecordedCaseData(needInfo, msg, caseID, isClientSimu, root);
	}
	
	//从录制的交易报文导入到案例数据
	public GWTPack_Struct ImportRecordedCaseData(GWTPackNeed needInfo, byte[] msg, String caseID, int isClientSimu, GWTPack_Struct root) {
		/*if (root == null) {
			throw new RuntimeException("error:未取得报文结构, root为空");
		}*/
		try {
			ISystemConfig config = SystemConfigManager.getConfigByCaseID(caseID, isClientSimu);
			// 设置报文结构
			boolean isRes = GetIsResForCase(isClientSimu, true);
			String specStr = "";
			try	{
				specStr = GetUnPackStyleContent(needInfo);
			}
			catch (Exception e) {
				throw new RuntimeException("error:" + e.getMessage());
			}

			StringReader sr = new StringReader(specStr);
			InputSource is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			UnpackSpecification spec = new UnpackSpecification(doc);
			
			//报文字段定义模板
			MsgDocument msgTemplateDoc = GetUnpackTemplateDocument(caseID, isClientSimu); 
			//specStr为样式文件格式规范
			Case caseInfo = GetCaseBean(caseID);
			Transaction trans = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ(GWTTransaction.N_TransID, caseInfo.getTransactionId()));
			MsgDocument unPackedDoc = MsgService.Unpack(msg, msgTemplateDoc, spec, new MsgContext(trans.getTranCode()));
			unPackedDoc = fillUnpackedMsg2CaseMsgTemplate(msgTemplateDoc, unPackedDoc);
			//unPackedDoc中的格式内容不全，要补全
			root = new GWTPack_Struct(" 案例数据 [从录制的报文导入]");
			return TranStructTreeUtil.GetGWTTreeRoot(unPackedDoc.toString(), isRes, root, config);
		} catch (RuntimeException e) {
			log.error(e, e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("遇到未处理的错误的，请与管理员联系");
		}
	}
	
	
	/**
	 * 组包前做报文预处理
	 * 设置默认值
	 */
	public MsgDocument fillUnpackedMsg2CaseMsgTemplate(final MsgDocument msgTemplateDoc, final MsgDocument unPackedDoc) throws Exception {		

		final MsgDocument outMsgDoc = msgTemplateDoc;
		
		//逐个处理案例报文模板中的所有字段
		outMsgDoc.ForEach(new ISimpleForEachVisitor() {
			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				//模板中的给定字段，用拆包后的unPackedDoc相应字段填充
				if (item instanceof MsgField) {
					if (!item.getAttribute("variable").str.toLowerCase().equals("true")) { //非变量字段
						//字段名
						String strMsgItemName = item.getAttribute("name").str;
						if (!strMsgItemName.isEmpty()) {
							String unPackedDocItemValue = "";
							try {
								String xPath = item.dpath();
								MsgField msgField = (MsgField)unPackedDoc.SelectSingleField(xPath);
								if (msgField == null && xPath.contains(".0.")) {
									xPath = xPath.replace(".0.", ".");
									msgField = (MsgField)unPackedDoc.SelectSingleField(xPath);
								}
								if (msgField == null && xPath.contains(".0")) {
									xPath = xPath.replace(".0", "");
									msgField = (MsgField)unPackedDoc.SelectSingleField(xPath);
								}
								if (msgField != null) {
									unPackedDocItemValue = msgField.value();
								}
							}
							catch(Exception e) {	
								System.out.println(e.getMessage());
							}
							if (unPackedDocItemValue != null && !unPackedDocItemValue.isEmpty()) {
								//录制的报文中的相应值
								((MsgField)item).set(unPackedDocItemValue);
							}
							/*else {
								((MsgField)item).set("");
							}*/
						}
					}
				}
			}
		});
		
		return outMsgDoc;
	}
	
	/**
	 * 获得组包样式
	 * @param needInfo		组包必备对象
	 * @throws Exception	未配置发送通道，或者发送通道为配置组包组件、组包失败
	 */
	public static String GetPackContent(GWTPackNeed needInfo) throws Exception
	{
		if(needInfo.GetIsClient())
			return GetClientPackContent(needInfo);
		else
			return GetServerPackContent(needInfo);
//		try
//		{
//			PackSpecification spec = PackService.LoadPackSpecification(specStr);
//			MsgDocument doc = MsgLoader.LoadXml(xmlContent);
//			return PackService.PackDocument(doc, spec, null, null);
//		}
//		catch (Exception e) {
//			System.out.println("组包失败：系统为:" + needInfo.GetSysName());
//			e.printStackTrace();
//			throw new Exception("组包失败");
//		}
	}
	
	public static String GetUnPackStyleContent(GWTPackNeed needInfo) throws Exception
	{
		if(needInfo.GetIsClient())
			return GetClientUnPackContent(needInfo);
		else
			return GetServerUnPackContent(needInfo);
	}
	
	//组包
	public static String GetServerPackContent(GWTPackNeed needInfo) throws Exception
	{
		List<Channel> result = new ComponentService().GetChannelBeanListBySystemId(needInfo.GetSysID());
		
		for (Channel chanel : result) {
			if (chanel.getAdapter().getCstype() == CsType.Server.getDbValue())
			{
				if(chanel.getPackChannel() != null)
				{
					return chanel.getPackChannel().getContent();
				}
			}
		}
		
		if(result.size() == 0)
			throw new Exception("该模拟系统尚未配置接收端通道,接收端交易组包失败");
		else
			throw new Exception("该模拟系统所配置的接收端通道未配置组包组件，接收端交易组包失败");
	}
	
	public static String GetClientPackContent(GWTPackNeed needInfo) throws Exception
	{
		String sysID = needInfo.GetSysID();
		String sysChanel = needInfo.GetSysChanel();
		String tranChanel = needInfo.GetTranChanel();
		
		String channelName = "";
		if(sysChanel == null || sysChanel.isEmpty())
			throw new Exception("该模拟系统尚未配置发送通道,组包失败");
		
		Channel chanel = null;
		//获得所属交易的通道信息
		if(tranChanel != null && !tranChanel.isEmpty())
		{
			channelName = tranChanel;
			chanel = new ComponentService().GetChanelBean(sysID, tranChanel);
		}
		
		//交易所配置的通道不存在，获取系统的通道信息
		if(chanel == null)
		{
			channelName = sysChanel;
			chanel= new ComponentService().GetChanelBean(sysID,channelName);
		}
		
		if(chanel.getPackChannel() == null)
			throw new Exception("发送通道【" + channelName + "】未配置组包组件,组包失败");
		
		String specStr = chanel.getPackChannel().getContent();
		if(specStr == null || specStr.isEmpty())
			throw new Exception("样式文件：" + chanel.getPackChannel().getStylename() + ",没有组包样式,组包失败");
		
		return specStr;
	}

	//拆包
	public static String GetServerUnPackContent(GWTPackNeed needInfo) throws Exception
	{
		List<Channel> result = new ComponentService().GetChannelBeanListBySystemId(needInfo.GetSysID());
		
		for (Channel chanel : result) {
			if (chanel.getAdapter().getCstype() == CsType.Server.getDbValue())
			{
				if(chanel.getUnpackChannel() != null)
				{
					return chanel.getUnpackChannel().getContent();
				}
			}
		}
		
		if(result.size() == 0)
			throw new Exception("该模拟系统尚未配置接收端通道,接收端交易拆包失败");
		else
			throw new Exception("该模拟系统所配置的接收端通道未配置拆包组件，接收端交易拆包失败");
	}
	
	public static String GetClientUnPackContent(GWTPackNeed needInfo) throws Exception
	{
		String sysID = needInfo.GetSysID();
		String sysChanel = needInfo.GetSysChanel();
		String tranChanel = needInfo.GetTranChanel();
		
		String channelName = "";
		if(sysChanel == null || sysChanel.isEmpty())
			throw new Exception("该模拟系统尚未配置发送通道,组包失败");
		
		Channel chanel = null;
		//获得所属交易的通道信息
		if(tranChanel != null && !tranChanel.isEmpty())
		{
			channelName = tranChanel;
			chanel = new ComponentService().GetChanelBean(sysID, tranChanel);
		}
		
		//交易所配置的通道不存在，获取系统的通道信息
		if(chanel == null)
		{
			channelName = sysChanel;
			chanel= new ComponentService().GetChanelBean(sysID,channelName);
		}
		
		if(chanel.getUnpackChannel() == null)
			throw new Exception("发送通道【" + channelName + "】未配置拆包组件,拆包失败");
		
		String specStr = chanel.getUnpackChannel().getContent();
		if(specStr == null || specStr.isEmpty())
			throw new Exception("样式文件：" + chanel.getPackChannel().getStylename() + ",没有拆包样式,拆包失败");
		
		return specStr;
	}
	
	@Override
	public String GetRespData(String caseID, int isClientSimu, String charsetStr) {
		try {
			Case caseInfo = GetCaseBean(caseID);
			byte[] info;
			if(isClientSimu == 1)
				info = caseInfo.getRequestMsg();
			else {
				info = caseInfo.getResponseMsg();
			}
			return GetResultData(info, charsetStr);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	//报文字段定义模板
	public MsgDocument GetUnpackTemplateDocument(String caseID, int isClientSimu) {
		try {
			Case caseInfo = GetCaseBean(caseID);
			String transactionId = caseInfo.getTransactionId();
			Transaction trans = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ(GWTTransaction.N_TransID, transactionId));
			String templateInXML = "";
			if (trans != null) {
				templateInXML = (isClientSimu == 1 ? trans.getRequestStruct() : trans.getResponseStruct());
			}
			if (templateInXML.isEmpty()) {
				String systemId = trans.getSystemId();
				SysType sysType = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ(GWTSimuSystem.N_SystemID, systemId));
				if (sysType != null && isClientSimu == 1 && sysType.getUseSameResponseStruct() == 1) {
					templateInXML = sysType.getResponseStruct();
				}
			}
			if (templateInXML.isEmpty()) {
				return null;
			}
			MsgDocument unpackTemplate = MsgLoader.LoadXml(templateInXML);
			return unpackTemplate;
		} 
		catch (Exception ex) {
			System.out.println();
		}
		return null;
	}
	
	
	@Override
	public GWTCompareResult GetResultCompare(GWTSimuSystem sysInfo,
			GWTTransaction tranInfo, String caseID, String executeLogId) {
		try {
			// 获得案例数据
			Case caseInfo = GetCaseBean(caseID);
			if (caseInfo == null)
			{
				GWTCompareResult result = new GWTCompareResult();
				result.setBoolResult(false);
				result.setErrorMsg("查询不到对应的案例的信息，执行被取消");
				return result;
			}

			return GetResultCompare(sysInfo, tranInfo, caseInfo, executeLogId);

		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTCompareResult GetResultCompare(GWTSimuSystem sysInfo,
			GWTTransaction tranInfo, String caseName, GWTPack_Struct root) {
		try {
			// 获得未经组包的MsgDocument
			ISystemConfig config = SystemConfigManager.getConfig(sysInfo
					.GetSystemName(), 0);
			MsgDocument doc = TranStructTreeUtil.GetMsgDocument(root, true,	config, 2);
			return new HelperService().RunCase(sysInfo, tranInfo, caseName, null, doc.toString(),"");
		} catch (Exception e) {
		}

		return null;
	}

	@Override
	public GWTCompareResult GetResultCompare(GWTSimuSystem sysInfo,
			GWTTransaction tranInfo) {
		GWTCompareResult result = new GWTCompareResult();
		Case caseInfo = null;
		try {
			List<Case> caseList = caseDao.List(0, 1, new Op[] { Op.EQ(
					GWTCase.N_transactionId, tranInfo.getTranID()),Op.NE("responContent", "".getBytes())});
//			,Op.NE("responContent",  "".getBytes())
			if (caseList.size() == 0) {
				result.setErrorMsg("该交易下没有可执行的案例（要求有案例数据）");
				return result;
			}
			caseInfo = caseList.get(0);
		} catch (Exception e) {
			result.setErrorMsg("未能成功取到该交易下的案例");
			log.error(e);
			e.printStackTrace();
			return result;
		}
		return GetResultCompare(sysInfo, tranInfo, caseInfo, "");
		
	}

	private GWTCompareResult GetResultCompare(GWTSimuSystem sysInfo,
			GWTTransaction tranInfo, Case caseInfo, String executeLogId) {

		try {
			//将传caseName改为caseId。针对本次模拟银联。
			return new HelperService().RunCase(sysInfo, tranInfo, caseInfo.getCaseId(),
					null, caseInfo.getRequestXml(), executeLogId);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	
	@Override
	public String SaveCaseContent(GWTPackNeed needInfo,String caseId, boolean isCaseData, int isClientSimu,
			GWTPack_Struct root, Integer loginLogId) {
		
		String msg = "";
		if (root == null) {
			return "error:未取得报文结构, root为空";
		}

		Case caseInfo = null;
		try {
			caseInfo = caseDao.Get(Op.EQ(GWTCase.N_caseId, caseId));
			if (caseInfo != null) {
				ISystemConfig config = SystemConfigManager.getConfigByTranID(caseInfo.getTransactionId(), isClientSimu);
				// 设置报文结构
				boolean isRes = GetIsResForCase(isClientSimu, isCaseData);
				String xmlContent = TranStructTreeUtil.GetMsgDocument(root,	isRes, config, 2).toString();
				msg = SaveCaseContent(needInfo, caseInfo, isCaseData, isClientSimu, xmlContent, loginLogId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return msg;
	}


	/**
	 * 保存案例数据，预期结果
	 * 
	 * @param needInfo
	 *            组包必备信息
	 * @param caseId
	 *            案例标识
	 * @param isCaseData
	 *            true：案例数据 false：预期结果
	 * @param xmlContent
	 *            案例数据、预期结果的文本信息
	 * @return
	 */
	public String SaveCaseContent(GWTPackNeed needInfo, String caseId,
			boolean isCaseData, int isClientSimu, String xmlContent, Integer loginLogId) {
		try {
			Case caseInfo = GetCaseBean(caseId);
			return SaveCaseContent(needInfo, caseInfo, isCaseData, isClientSimu, xmlContent, loginLogId);
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 保存案例数据，预期结果
	 * 
	 * @param needInfo
	 *            组包必备信息
	 * @param caseInfo
	 *            案例基本信息
	 * @param isCaseData
	 *            true：案例数据 false：预期结果
	 * @param xmlContent
	 *            案例数据、预期结果的文本信息
	 * @return 非空则保存失败，否则保存成功
	 * @throws Exception
	 */
	public String SaveCaseContent(GWTPackNeed needInfo,Case caseInfo,
			boolean isCaseData, int isClientSimu, String xmlContent, Integer loginLogId) {
		String msg = "";
		// 案例数据需要组包操作
		if (isCaseData) {
			if (caseInfo.getIsParseable() == 1) {
				ISystemConfig config = SystemConfigManager.getConfigByTranID(caseInfo.getTransactionId(), isClientSimu);
				if (config.getIsClientSimu() == 1) { //client
					caseInfo.setRequestXml(xmlContent);
				}
				else {
					caseInfo.setResponseXml(xmlContent);
				}
				String specStr = "";
				try
				{
					specStr = GetPackContent(needInfo);
					byte[] packedMsg = SystemConfigManager.PackSpecification(specStr, xmlContent,needInfo.GetTranCode());
					if (packedMsg == null)
						msg = "error:尝试调用组包失败!";
					else {
						if (config.getIsClientSimu() == 1) { //client
							caseInfo.setRequestMsg(packedMsg);
						}
						else {
							caseInfo.setResponseMsg(packedMsg);
						}
					}
				}
				catch (Exception e) {
					msg = "error:" + e.getMessage();
				}
			}
		} else { //预期值
			caseInfo.setExpectedXml(xmlContent);
		}

		try {
			caseInfo.setLastModifiedTime(new Date());
			caseInfo.setLastModifiedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
			OperationLogService.writeOperationLog(isCaseData?OpType.RequestMessage:OpType.ExpectedMessage, IDUType.Update,
					Integer.parseInt(caseInfo.getCaseId()), caseInfo.getCaseName(),
					isCaseData?"requestMessage":"expectedMessage", caseInfo.getCaseName(), isCaseData?"修改请求报文":"修改应答报文预期值", loginLogId);
			caseDao.Edit(caseInfo);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		return msg;
	}

	
	/**
	 * 保存案例数据，预期结果
	 * 
	 * @param specStr
	 *            组包样式
	 * @param caseInfo
	 *            案例基本信息
	 * @param isCaseData
	 *            true：案例数据 false：预期结果
	 * @param xmlContent
	 *            案例数据、预期结果的文本信息
	 * @return 非空则保存失败，否则保存成功
	 * @throws Exception
	 */
	public String SaveCaseContent(String specStr,String tranCode,Case caseInfo, 
			boolean isCaseData, int isClientSimu, String xmlContent, Integer loginLogId) {
		String msg = "";
		// 案例数据需要组包操作
		if (isCaseData) {
			if (caseInfo.getIsParseable() == 1) {
				
				ISystemConfig config = SystemConfigManager.getConfigByTranID(caseInfo.getTransactionId(), isClientSimu);
				if (config.getIsClientSimu() == 1) { //client
					caseInfo.setRequestXml(xmlContent);
				}
				else {
					caseInfo.setResponseXml(xmlContent);
				}
				try	{
					byte[] packedMsg = SystemConfigManager.PackSpecification(specStr, xmlContent, tranCode);
					if (packedMsg == null)
						msg = "error:尝试调用组包失败!";
					else {
						if (config.getIsClientSimu() == 1) { //client
							caseInfo.setRequestMsg(packedMsg);
						}
						else {
							caseInfo.setResponseMsg(packedMsg);
						}
					}
				}
				catch (Exception e) {
					msg = "error:" + e.getMessage();
				}
			}
		} 
		else { //预期值
			caseInfo.setExpectedXml(xmlContent);
		}

		try {
			caseInfo.setLastModifiedTime(new Date());
			caseInfo.setLastModifiedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
			OperationLogService.writeOperationLog(isCaseData?OpType.RequestMessage:OpType.ExpectedMessage, 
						IDUType.Update, Integer.parseInt(caseInfo.getCaseId()), caseInfo.getCaseName(),
						isCaseData?"requestMessage":"expectedMessage", caseInfo.getCaseName(),
						isCaseData?"导入请求报文":"导入应答报文预期值", loginLogId);
			caseDao.Edit(caseInfo);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		return msg;
	}
	

	/**
	 * 根据案例标识获得案例信息
	 * 
	 * @param caseID
	 *            案例标识
	 * @return 案例信息
	 */
	public Case GetCaseBean(String caseID) {
		try {
			return caseDao.Get(Op.EQ(GWTCase.N_caseId, caseID));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 添加案例信息
	 * 
	 * @param caseBean
	 *            案例信息
	 */
	public void AddCaseInfo(Case caseBean) {
		try {
			caseDao.Add(caseBean);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 更新案例信息
	 * 
	 * @param caseBean
	 *            案例信息
	 */
	public void EditCaseInfo(Case caseBean) {
		try {
			caseDao.Edit(caseBean);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	// /**
	// * 根据服务器返回的比对结果转换为客户端的Bean
	// * @return 客户端Bean的比对结果
	// */
	// private GWTCompareResult GetResultCompare()
	// {
	// return null;
	// }

	/**
	 * 响应样式还是发起样式 true:响应样式 false：请求样式
	 * 
	 * @param isClientSimu
	 *            接收端交易或者发起端交易
	 * @param isCaseData
	 *            是否案例数据
	 * @return true:响应样式 false：请求样式
	 */
	private boolean GetIsResForCase(int isClientSimu, boolean isCaseData) {
		return !isCaseData || (isClientSimu == 0); //预期值 || 服务器端
	}

	/**
	 * 以文本形式返回案例数据
	 * 
	 * @param info
	 *            案例数据字节数组
	 * @param charsetStr
	 *            案例数据编码方式
	 * @return 文本形式的案例数据
	 */
	private String GetResultData(byte[] info, String charsetStr) {
		try {
			if (info != null) {
				String preStr = new String(info, charsetStr);
				if (preStr.trim().toLowerCase().startsWith("<?xml version="))
					return preStr;
				else
					return RuntimeUtils.PrintHex(info, Charset.forName(charsetStr));
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			throw new RuntimeException("编码名称错误，不支持该编码方式：" + charsetStr);
		}
		return "";
	}

	/**
	 * 案例比对结果Mock数据
	 */
	public GWTCompareResult ResultCompareMock(String tranCode, String caseName,
			Byte[] reqMsg, String reqData) {
		GWTCompareResult result = new GWTCompareResult();
		try {
			String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
			xmlStr += "<msg>";
			xmlStr += "<field name=\"BkSeq\" desc=\"银行流水号\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\" expect_result=\"12937198268767123\">12937198268767123No</field>";
			xmlStr += "<struct name=\"head\">";
			xmlStr += "    <struct name=\"head2\">";
			xmlStr += "      <field name=\"field1\"  expect_result=\"\">黄色</field>";
			for (int i = 2; i < 20; i++)
				xmlStr += "      <field name=\"field" + i
						+ "\"  expect_result=\"23\">23</field>";
			xmlStr += "    </struct>";
			xmlStr += "  </struct>";
			xmlStr += "</msg>";

			ISystemConfig config = SystemConfigManager.getConfig("CISS", 0);
			GWTPack_Struct root = TranStructTreeUtil.GetCompResultRoot(xmlStr,
					caseName, config);

			result.setBoolResult(true);
			result.setCompareResult(root);
		} catch (Exception e) {

		}
		return result;
	}
	
	public List<Case> GetCaseListByTranID(String tranID)
	{
		try
		{
			return caseDao.ListAll(Op.EQ(GWTCase.N_transactionId, tranID));
		}	
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	public List<Case> GetCaseListByTranIDAndBatchNo(String tranID, String batchNo)
	{
		try
		{
			return null; //caseDao.ListAll(Op.EQ(GWTCase.N_transactionId, tranID),Op.EQ(GWTCase.N_importBatchNo, batchNo));
		}	
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	//插入ExecuteLog
	public String Insert2DataBase(String caseId, String userId, String sysId) {
		Case c = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("id", caseId));
		IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog log = new ExecuteLog();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String now = sdf.format(date);
		log.setCreateTime(now);
		log.setBeginRunTime(date);
		log.setSystemId(Integer.parseInt(sysId));
		log.setDescription(c.getDescription());
		log.setExecuteSetName("");
		log.setType(2);
		User user = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", userId));
		sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		log.setExecuteBatchNo(user.getName()+ sdf.format(date));
		if(userId.equals("Administrator"))
			log.setUserId(0);
		else {
			log.setUserId(Integer.parseInt(userId));
		}
		executeLogDAL.Add(log);
		return String.valueOf(log.getId());
	}
	
	//Add by xuat
	@Override
	public GWTCompareResult GetResultCompare(GWTSimuSystem sysInfo,
			String tranID, String caseID, String executeLogId) {
		// TODO Auto-generated method stub
		try {
			//将传caseName改为caseId。针对本次模拟银联。
			IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
			GWTTransaction tranInfo = TransactionService.BeanToModel(tranDAL.Get(
					Op.EQ(GWTTransaction.N_TransID, tranID)));
			Case caseInfo = GetCaseBean(caseID);
			if (caseInfo == null)
			{
				GWTCompareResult result = new GWTCompareResult();
				result.setBoolResult(false);
				result.setErrorMsg("查询不到对应的案例的信息，执行被取消");
				return result;
			}
			return GetResultCompare(sysInfo, tranInfo, caseInfo, executeLogId);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 针对断点进行处理，此时传入的是caseInstanceID.
	 */
	@Override
	public GWTCompareResult GetResultCompare4BP(GWTSimuSystem sysInfo,
			String tranID, String caseInstanceID, String executeLogId) {
		// TODO Auto-generated method stub
		try {
			//将传caseName改为caseId。针对本次模拟银联。
			IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
			GWTTransaction tranInfo = TransactionService.BeanToModel(tranDAL.Get(
					Op.EQ(GWTTransaction.N_TransID, tranID)));
			IDAL<CaseInstance> caseInstanceDAL = DALFactory.GetBeanDAL(CaseInstance.class);
			CaseInstance caseInstance = caseInstanceDAL.Get(Op.EQ("id", Integer.parseInt(caseInstanceID)));
			String caseID = caseInstance.getCaseId().toString();
			Case caseInfo = GetCaseBean(caseID);
			if (caseInfo == null)
			{
				GWTCompareResult result = new GWTCompareResult();
				result.setBoolResult(false);
				result.setErrorMsg("查询不到对应的案例的信息，执行被取消");
				return result;
			}
			//把实例的断点标记传给case.
			caseInfo.setBreakPointFlag(caseInstance.getBreakPointFlag());
			return GetResultCompare(sysInfo, tranInfo, caseInfo, executeLogId);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void ChangeBreakPointFlag(GWTCase gwtCase) {
		// TODO Auto-generated method stub
		if(gwtCase.getBreakPointFlag().equals("1")){
			gwtCase.setBreakPointFlag(0);
		}else{
			gwtCase.setBreakPointFlag(1);
		}
		EditCaseInfo(ModelToBean(null, gwtCase));
		IDAL<CaseFlow> caseflowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = caseflowDAL.Get(Op.EQ("id", Integer.parseInt(gwtCase.getCaseFlow().GetID())));
		if(gwtCase.getBreakPointFlag().equals("1")){
			if(caseFlow.getBreakPointFlag()==null || caseFlow.getBreakPointFlag()!=1){
				caseFlow.setBreakPointFlag(1);
				caseflowDAL.Edit(caseFlow);
			}
		}else{
			List<Case> caseList = caseDao.ListAll(Op.EQ("caseFlow", caseFlow));
			int flag = 0;
			for(Case casebean: caseList){
				if(casebean.getBreakPointFlag()==null || casebean.getBreakPointFlag()==0)
					continue;
				else{
					flag = 1;
					break;
				}
			}
			if(flag == 0){
				caseFlow.setBreakPointFlag(0);
				caseflowDAL.Edit(caseFlow);			
			}	
		}
	}

	@Override
	public boolean SaveOrUpdateCase(GWTCase gwtCase, Integer loginLogId) {
		// TODO Auto-generated method stub
		try{
			IDAL<CaseParameterExpectedValue> caseParaDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
			IDAL<Transaction> tranIdal = DALFactory.GetBeanDAL(Transaction.class);
			Case casebean = ModelToBean(null, gwtCase);
			if(casebean.getCaseId()==null){
				Transaction tranInfo = tranIdal.Get(Op.EQ("transactionId", casebean.getTransactionId()));
				casebean.setRequestXml(tranInfo.getRequestStruct());
				casebean.setExpectedXml(tranInfo.getResponseStruct());
				casebean.setResponseXml(tranInfo.getResponseStruct());
				casebean.setCreatedTime(new Date());
				casebean.setCreatedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
				caseDao.Add(casebean);
				OperationLogService.writeOperationLog(OpType.Case, IDUType.Insert, 
						Integer.parseInt(casebean.getCaseId()), casebean.getCaseName(),
						"caseName", null, casebean.getCaseName(), loginLogId);
				AddDefaultCaseExpectValue(tranInfo, casebean.getCaseId());
				if(casebean.getCaseFlow()!=null)
					updateCaseFlowStepCount(casebean.getCaseFlow());
			}else{
				Case c = caseDao.Get(Op.EQ(GWTCase.N_caseId, casebean.getCaseId()));
				if(!c.getTransactionId().equals(casebean.getTransactionId())){ //交易类型变换时，报文跟着变					
					//交易类型变换，删除原有已设置的预期值
					List<CaseParameterExpectedValue> list = caseParaDAL.ListAll(Op.EQ("caseId", c.getCaseId()));
					for(CaseParameterExpectedValue para : list){
						caseParaDAL.Del(para);
					}
					Transaction tranInfo = tranIdal.Get(Op.EQ("transactionId", casebean.getTransactionId()));
					casebean.setRequestXml(tranInfo.getRequestStruct());
					casebean.setExpectedXml(tranInfo.getResponseStruct());
					casebean.setResponseXml(tranInfo.getResponseStruct());
					AddDefaultCaseExpectValue(tranInfo, casebean.getCaseId());
				}
				casebean.setLastModifiedTime(new Date());
				casebean.setLastModifiedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
				caseDao.Edit(casebean);
				OperationLogService.writeUpdateOperationLog(OpType.Case, Case.class,Integer.parseInt(c.getCaseId()),
						c.getCaseName(), c, casebean, loginLogId);
			}
			return true;
		}catch (Exception e) {
			// TODO: handle exception
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 为系统参数设置有预期值的案例添加案例预期值记录
	 * @param tranInfo
	 * @param caseId
	 */
	private void AddDefaultCaseExpectValue(Transaction tranInfo, String caseId) {
		// TODO Auto-generated method stub
		IDAL<CaseParameterExpectedValue> caseParaDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
		IDAL<TransactionDynamicParameter> tranParaDAL =  DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		List<TransactionDynamicParameter> tranParameters = tranParaDAL.ListAll(Op.EQ("transactionId", tranInfo.getTransactionId()));
		for(TransactionDynamicParameter tranPara: tranParameters){
			SystemDynamicParameter sysPara = tranPara.getSystemParameter();
			if(sysPara.getDefaultExpectedValue()!=null){
				CaseParameterExpectedValue casePara = new CaseParameterExpectedValue();
				casePara.setCaseId(caseId);
				casePara.setExpectedValue(sysPara.getDefaultExpectedValue());
				casePara.setExpectedValueType(0);
				casePara.setTransParameter(tranPara);
				caseParaDAL.Add(casePara);
			}
		}
	}

	@Override
	public String GetXmlContent(String caseID) {
		// TODO Auto-generated method stub
		Case caseBean = this.GetCaseBean(caseID);
		
		return caseBean.getRequestXml();
	}

	@Override
	public String SaveXmlContent(String xmlContent, String caseID, int isClientSimu, GWTPackNeed needInfo) {

		Case caseBean = this.GetCaseBean(caseID);
		String msg = "";
		String specStr = "";
		try
		{
			specStr = GetPackContent(needInfo);
			byte[] packedMsg = SystemConfigManager.PackSpecification(specStr, xmlContent,needInfo.GetTranCode());
			if (packedMsg == null)
				msg = "error:尝试调用组包失败!";
			else {
				
				ISystemConfig config = SystemConfigManager.getConfigByTranID(caseBean.getTransactionId(), isClientSimu);
				if (config.getIsClientSimu() == 1) { //客户端，案例报文为请求报文
					caseBean.setRequestXml(xmlContent);
				}
				else { //服务端，案例报文为应答报文
					caseBean.setResponseXml(xmlContent);
				}
				caseDao.Edit(caseBean);
				//编辑后，直接在预览模式里看到效果
				msg = GetResultData(packedMsg,"utf-8");
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return msg;
	}
	
	/**
	 * 当案例进行增加或删除时，及时更新CaseFlow表中的stepCount值
	 * @param caseFlowId
	 */
	private void updateCaseFlowStepCount(CaseFlow caseFlow){
		IDAL<CaseFlow> caseFlowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		Integer count = caseDao.Count(Op.EQ("caseFlow.id", caseFlow.getId()));
		caseFlow.setStepCount(count);
		caseFlowDAL.Edit(caseFlow);
	}

	@Override
	/**
	 * 从CaseInstance获取响应报文来充当预期结果编辑的报文内容。
	 * @param caseId
	 * @return
	 */
	public GWTPack_Struct GetExpectedXmlFromCaseInstance(String caseId) {
		
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance caseInstance = null;
		List<CaseInstance> ciList = ciDAL.ListAll("beginRunTime", false, Op.EQ("caseId", Integer.parseInt(caseId)));
		if(ciList.size()==0){
			return null;   //该案例没有执行过，返回null，由界面处理
		}
		caseInstance = ciList.get(0);  //获取最后一次生成的caseInstance
		String expectedXml = caseInstance.getResponseXml(); //取响应报文的拆包XML作为预期值XML
		Case caseBean = this.GetCaseBean(caseId);
		caseBean.setExpectedXml(expectedXml); 
		try{
			ISystemConfig config = SystemConfigManager.getConfigByTranID(caseBean.getTransactionId(), 1);
			boolean isRes = GetIsResForCase(config.getIsClientSimu(), false);
			
			return TranStructTreeUtil.GetGWTTreeRoot(caseBean, false, isRes, config, 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateCaseSequence(List<GWTCase> cases) {
		// TODO Auto-generated method stub
		try {
			for(GWTCase gwtCase : cases){
				caseDao.Edit(ModelToBean(null, gwtCase));
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
}
