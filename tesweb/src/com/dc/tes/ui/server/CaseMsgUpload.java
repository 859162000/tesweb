package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.apache.log4j.lf5.util.StreamUtils;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.MsgPacker;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.DownLoadException;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.unpack.UnpackSpecification;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTChannel;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.util.HexStringUtils;

/**
 * 直接上传TXT或XML格式的原始报文至案例数据
 * 上传后根据系统配置设定的组包组件进行拆包
 * @author HO274218
 *
 */
public class CaseMsgUpload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -260070498199390304L;
	private static final Log log = LogFactory
	.getLog(CaseMsgUpload.class);
	
	public CaseMsgUpload(){
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException ,IOException {
		resp.setCharacterEncoding("utf8");
		List<String> resultList = new ArrayList<String>();
		ServletFileUpload upload = new ServletFileUpload();
		String caseID = req.getParameter("caseID");
		String systemID = req.getParameter("systemID");
		int isClient = Integer.parseInt(req.getParameter("isClient"));
		boolean isCaseData = Boolean.parseBoolean(req.getParameter("isCaseData"));
		int loginLogId = Integer.parseInt(req.getParameter("loginLogId"));
		int msgtype = Integer.parseInt(req.getParameter("msgType"));
		int uploadWay = Integer.parseInt(req.getParameter("uploadWay"));
		String content = req.getParameter("content");
		if(uploadWay == 0){
			try {			
				FileItemIterator iter = upload.getItemIterator(req);
				while (iter.hasNext()) {	
					FileItemStream item = iter.next();
					if (item.isFormField()) {					
						continue;
						
					} else {
						InputStream stream = item.openStream();
						byte[] originalMsg = StreamUtils.getBytes(stream);
						resultList = UploadCaseMsg(caseID, systemID, isClient, isCaseData, loginLogId, msgtype, originalMsg, resp);							
						String msg = resultList.toString();
						resp.getOutputStream().write(msg.getBytes("utf8"));
						resp.flushBuffer();			
						break;
					}
				} 
			}catch (DownLoadException ex) {
				
				ex.printStackTrace();
				resp.getOutputStream()
						.write(ex.getMessage().getBytes("utf8"));
				resp.flushBuffer();
				return;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resultList.add("error:" + "读取文件失败！");
				log.error("读取文件失败！", e);
			}catch (Exception ex) {
				ex.printStackTrace();
				resp.getOutputStream().write(
						("error:发生异常，请与管理员联系。"+ex.getMessage()).getBytes("utf8"));
				resp.flushBuffer();
				return;
			}
			
		}else{
			try {
				byte[] originalMsg = content.getBytes();
				resultList = UploadCaseMsg(caseID, systemID, isClient, isCaseData, loginLogId, msgtype, originalMsg, resp);							
				String msg = resultList.toString();
				resp.getOutputStream().write(msg.getBytes("utf8"));
				resp.flushBuffer();
			}catch (Exception ex) {
				ex.printStackTrace();
				resp.getOutputStream().write(
						("error:发生异常，请与管理员联系。"+ex.getMessage()).getBytes("utf8"));
				resp.flushBuffer();
				return;
			}
		}
		
	}

	private List<String> UploadCaseMsg(String caseID, String systemID, int isClient,
			boolean isCaseData, int loginLogId, int msgType, byte[] originalMsg, 
			HttpServletResponse resp) {
		// TODO Auto-generated method stub
		MsgDocument doc = null;
		List<String> resultList = new ArrayList<String>();
		
		try {
			byte[] msg = processMsg(originalMsg, msgType);
			if(msg == null){
				resultList.add("error:" + "输入上传内容有误，请检查报文。");
				throw new DownLoadException("输入上传内容有误，请检查报文。");

			}
			SysType sysType = DALFactory.GetBeanDAL(SysType.class)
					.Get(Op.EQ("systemId", systemID));
			Case casebean = DALFactory.GetBeanDAL(Case.class)
					.Get(Op.EQ(GWTCase.N_caseId, caseID));
			Transaction tran = DALFactory.GetBeanDAL(Transaction.class)
					.Get(Op.EQ(Transaction.N_TransID, casebean.getTransactionId()));
			String templateXML = "";
			if(isCaseData && isClient == 1){//如果是发起端，则取请求报文样式
				templateXML = tran.getRequestStruct();
			}else{ //否则取响应报文样式
				templateXML = tran.getResponseStruct();
			}
			try{
				MsgDocument templateDoc = MsgLoader.LoadXml(templateXML);
				String rule; 
				if(isCaseData){ //案例数据，取系统设置的拆包组件
					if(isClient == 1){ 
						if(sysType.getReqMsgUnpackerId() == null || sysType.getReqMsgUnpackerId().isEmpty()){
							resultList.add("error:" + "未配置发起端请求报文的拆包组件，请检查系统设置。");
							throw new DownLoadException("未配置发起端请求报文的拆包组件，请检查系统设置。");
						}
					}else{
						if(sysType.getResMsgUnpackerId() == null || sysType.getResMsgUnpackerId().isEmpty()){
							resultList.add("error:" + "未配置接收端响应报文的拆包组件，请检查系统设置。");
							throw new DownLoadException("未配置接收端响应报文的拆包组件，请检查系统设置。");
						}
					}
					rule = DALFactory.GetBeanDAL(MsgPacker.class).Get(Op.EQ("id", isClient == 1 ? 
							sysType.getReqMsgUnpackerId():sysType.getResMsgUnpackerId())).getContent();
				}else{//请求报文的预期结果， 需取发起方通道下的拆包组件
					IDAL<Channel> channelDAL = DALFactory.GetBeanDAL(Channel.class);
					String channelID = "";
					if(tran.getChannel()!=null && !tran.getChannel().isEmpty()){
						channelID = tran.getChannel();		
					}else if(sysType.getChannel()!=null && !sysType.getChannel().isEmpty()){
						channelID = sysType.getChannel();
					}else{
						resultList.add("error:" + "找不到发起方通道，请检查系统配置。");
						throw new DownLoadException("找不到发起方通道，请检查系统配置。");
					}
					Channel channel = channelDAL.Get(Op.EQ(GWTChannel.N_ChannelId, channelID));
					if(channel == null){ //一般不会出现这种情况
						resultList.add("error:" + "找不到发起方通道，请检查系统配置。");
						throw new DownLoadException("找不到发起方通道，请检查系统配置。");
					}
					rule = channel.getPackChannel().getContent();
				}
				try
				{
					UnpackSpecification spec = MsgService.LoadUnpackSpecification(rule);
					doc = MsgService.Unpack(msg, templateDoc, spec, new MsgContext(tran.getTranCode()));
				}
				catch (Exception e) {
					resultList.add("error:拆包失败：" + e.getMessage());
				}
				String specStr = "";
				try
				{
					specStr = CaseService.GetPackContent(new GWTPackNeed(sysType.getSystemId(),
							sysType.getSystemName(), sysType.getChannel(), isClient == 1, tran.getChannel(),tran.getTranCode()));
				}
				catch (Exception e) {
					resultList.add("error:" + e.getMessage());
				}
				
				if (doc != null) {
					String result = new CaseService().SaveCaseContent(
							specStr, tran.getTranCode(), casebean, isCaseData, isClient,
							doc.toString(), loginLogId);
					resultList.add("edit:" +"上传报文成功");
					if(!result.isEmpty())
						resultList.add(result);
				} else {
					resultList.add("error:" + "结构解析失败，请检查报文格式。");
					throw new DownLoadException("结构解析失败，请检查报文格式。");
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				resultList.add("error:" + "上传失败，请检查报文格式。");
				log.error("上传失败", e);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultList.add("error:" + "读取报文失败！");
			log.error("读取报文失败！", e);
		}
		
		return resultList;
	}

	/**
	 * 加工文件原始数据，转换成BYTE
	 * @param originalMsg 原始数据
	 * @param msgType 报文类型： 0：字符串；1：16进制；2: 字节字符
	 * @return
	 */
	private byte[] processMsg(byte[] originalMsg, int msgType) {
		// TODO Auto-generated method stub
		String str = new String(originalMsg).trim(); //去除开头与结尾的空格;
		switch (msgType) {
		case 0:  //字符串，无需处理直接返回
			return originalMsg;
		case 1:  //16进制，得先判断是何种格式，是 有空格间隔的(如AE 2C)，还是无间隔的(AE2C)，只支持这两种
			if(str.split(" ").length>1){ //如果字串中还存在空格，则认为是有空格间隔的
				return HexStringUtils.FromHexStringWithSpace(str);
			}else{//否则认为是没空格的
				return HexStringUtils.FromHexString(str);
			}
		case 2: //字节字符(格式如 [32, 34, 48])
			if(str.startsWith("[")){ //如存在[]， 则先去除
				str = str.substring(1);
			}
			if(str.endsWith("]")){
				str = str.substring(0, str.length()-1);
			}
			String[] bytes = str.split(","); 
			if(bytes.length < 2){
				return null;
			}
			byte[] msg = new byte[bytes.length];
			for(int i = 0; i < bytes.length; i++){
				msg[i] = (byte) Integer.parseInt(bytes[i].trim());
			}
			return msg;
		default:
			return originalMsg;
		}
	}
}
