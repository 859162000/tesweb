package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Adapter;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.MsgPacker;
import com.dc.tes.data.model.TransRecognizer;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IComponent;
import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.model.GWTAdapter;
import com.dc.tes.ui.client.model.GWTChannel;
import com.dc.tes.ui.client.model.GWTComponent;
import com.dc.tes.ui.client.model.GWTMsgType;
import com.dc.tes.ui.client.model.GWTRecCode;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.SystemDeploy;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ComponentService extends RemoteServiceServlet implements IComponent {

	private static final long serialVersionUID = 7100927349097463783L;
	private static final Log log = LogFactory.getLog(ComponentService.class);
	
	IDAL<SysType> sysDao = DALFactory.GetBeanDAL(SysType.class);
	IDAL<Adapter> adapterDao = DALFactory.GetBeanDAL(Adapter.class);
	IDAL<MsgPacker> msgPackerDao = DALFactory.GetBeanDAL(MsgPacker.class);
	IDAL<TransRecognizer> transRecognizerDao = DALFactory.GetBeanDAL(TransRecognizer.class);
	IDAL<Channel> channelDao = DALFactory.GetBeanDAL(Channel.class);

	public List<GWTComponent> GetComponentListByType(ComponentEnum compEnum){
		List<GWTComponent> returnList = new ArrayList<GWTComponent>();
		
		try{
			switch(compEnum){
				case Adapter:
				{
					List<Adapter> adapterList = adapterDao.ListAll();;
					
					for(Adapter ada : adapterList)
						returnList.add(BeanToModel(ada));
					
					break;
				}
				case MsgType:
				{
					List<MsgPacker> msgPackerList = msgPackerDao.ListAll();
					
					for(MsgPacker msgPacker : msgPackerList)
						returnList.add(BeanToModel(msgPacker));
					
					break;
				}
				case RecCode:
				{
					List<TransRecognizer> codeList = transRecognizerDao.ListAll();
					
					for(TransRecognizer code : codeList)
						returnList.add(BeanToModel(code));
					
					break;
				}
				default:
					return returnList;
			}
			
		}catch(Exception ex){
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
		return returnList;
	}
	
	@Override
	public PagingLoadResult<GWTComponent> GetComponentListByType(String searchKey,
			PagingLoadConfig config, ComponentEnum compEnum) {
		
		int count = 0;
		List<GWTComponent> returnList = new ArrayList<GWTComponent>();
		
		try{
			
			switch(compEnum){
				case Adapter:
				{
					List<Adapter> adapterList = null;
					
					if (searchKey.isEmpty()) {
						count = adapterDao.Count();
						PageStartEnd pse = new PageStartEnd(config, count);
						adapterList = adapterDao.List(pse.getStart(), pse.getEnd());
					} else {
						String[] searchField = new String[]{"protocoltype", "description", "pluginname"};
						count = adapterDao.MatchCount(searchKey, searchField);
						PageStartEnd pse = new PageStartEnd(config, count);
						adapterList = adapterDao.Match(searchKey, searchField, pse.getStart(), pse.getEnd());
					}
					
					for(Adapter ada : adapterList)
						returnList.add(BeanToModel(ada));
					
					break;
				}
				case MsgType:
				{
					List<MsgPacker> msgPackerList = null;
					
					if (searchKey.isEmpty()) {
						count = msgPackerDao.Count();
						PageStartEnd pse = new PageStartEnd(config, count);
						msgPackerList = msgPackerDao.List(pse.getStart(), pse.getEnd());
					} else {
						String[] searchField = new String[]{"stylename",  "messagetype", "classname"};
						count = msgPackerDao.MatchCount(searchKey, searchField);
						PageStartEnd pse = new PageStartEnd(config, count);
						msgPackerList = msgPackerDao.Match(searchKey, searchField, pse.getStart(), pse.getEnd());
					}
					
					for(MsgPacker msgPacker : msgPackerList)
						returnList.add(BeanToModel(msgPacker));
					
					break;
				}
				case RecCode:
				{
					List<TransRecognizer> codeList = null;
					
					if (searchKey.isEmpty()) {
						count = transRecognizerDao.Count();
						PageStartEnd pse = new PageStartEnd(config, count);
						codeList = transRecognizerDao.List(pse.getStart(), pse.getEnd());
					} else {
						String[] searchField = new String[]{"name", "type","description", "classname"};
						count = transRecognizerDao.MatchCount(searchKey, searchField);
						PageStartEnd pse = new PageStartEnd(config, count);
						codeList = transRecognizerDao.Match(searchKey, searchField, pse.getStart(), pse.getEnd());
					}
					
					for(TransRecognizer code : codeList)
						returnList.add(BeanToModel(code));
					
					break;
				}
				default:
					return new BasePagingLoadResult<GWTComponent>(returnList, config.getOffset(), count);
			}
			
		}catch(Exception ex){
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
		return new BasePagingLoadResult<GWTComponent>(returnList, config.getOffset(), count);
	}

	@Override
	public void AddNewComponent(GWTComponent comp) {
		try{
			if(comp instanceof GWTAdapter){
				adapterDao.Add(ModelToBean((GWTAdapter)comp, null));
			}else if(comp instanceof GWTMsgType){
				msgPackerDao.Add(ModelToBean((GWTMsgType)comp, null));
			}else if(comp instanceof GWTRecCode){
				transRecognizerDao.Add(ModelToBean((GWTRecCode)comp, null));
			}else{
				log.error("不支持的GWT类型");
				throw new RuntimeException("undefined type");
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void UpdateComponent(GWTComponent comp) {
		try{
			if(comp instanceof GWTAdapter){
				Adapter adapter = adapterDao.Get(Op.EQ("id", comp.get(GWTAdapter.N_ComponentId)));
				adapterDao.Edit(ModelToBean((GWTAdapter)comp, adapter));
			}else if(comp instanceof GWTMsgType){
				MsgPacker msgPacker = msgPackerDao.Get(Op.EQ("id", comp.get(GWTMsgType.N_ComponentId)));
				msgPackerDao.Edit(ModelToBean((GWTMsgType)comp, msgPacker));
			}else if(comp instanceof GWTRecCode){
				TransRecognizer code = transRecognizerDao.Get(Op.EQ("id", comp.get(GWTRecCode.N_ComponentId)));
				transRecognizerDao.Edit(ModelToBean((GWTRecCode)comp, code));
			}else{
				log.error("不支持的GWT类型");
				throw new RuntimeException("undefined type");
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void DeletComponent(List<GWTComponent> complist) {
		try{
			for(GWTComponent comp : complist){
				if(comp instanceof GWTAdapter){
					Adapter adapter = adapterDao.Get(Op.EQ("id", comp.get(GWTAdapter.N_ComponentId)));
					adapterDao.Del(ModelToBean((GWTAdapter)comp, adapter));
				}else if(comp instanceof GWTMsgType){
					MsgPacker msgPacker = msgPackerDao.Get(Op.EQ("id", comp.get(GWTMsgType.N_ComponentId)));
					msgPackerDao.Del(ModelToBean((GWTMsgType)comp, msgPacker));
				}else if(comp instanceof GWTRecCode){
					TransRecognizer code = transRecognizerDao.Get(Op.EQ("id", comp.get(GWTRecCode.N_ComponentId)));
					transRecognizerDao.Del(ModelToBean((GWTRecCode)comp, code));
				}else{
					log.error("不支持的GWT类型");
					throw new RuntimeException("undefined type");
				}
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
		
	}
	
	@Override
	public void SaveChannelList(String sysId, List<GWTChannel> channelist, String defChannelName) {
		
		try{
			List<Channel> dataList = channelDao.ListAll(Op.EQ("systemId", sysId));
			for(Channel data : dataList)
				channelDao.Del(data);
			
			for(GWTChannel channel : channelist){
				Channel cBean = ModelToBean(channel);
				cBean.setSystemId(sysId);
				channelDao.Add(cBean);
			}
			
			dataList = channelDao.ListAll(Op.EQ("systemId", sysId));
			new SystemDeploy().SaveBaseConfig(sysId, dataList, defChannelName);
		}catch(Exception ex){
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<GWTChannel> GetChannelListBySystemId(String sysId) {
		
		List<GWTChannel> returnList = new ArrayList<GWTChannel>();
		
		try{
			SysType sys = sysDao.Get(Op.EQ(GWTSimuSystem.N_SystemID, sysId));
			List<Channel> dataList = GetChannelBeanListBySystemId(sysId);
			for(Channel c : dataList){
				GWTChannel channel = BeanToModel(c, sys.getChannel());
				returnList.add(channel);
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
		return returnList;
	}
	
	
	public List<Channel> GetChannelBeanListBySystemId(String sysId) throws RuntimeException {
		try{
			return channelDao.ListAll(Op.EQ("systemId", sysId));
		}catch(Exception ex){
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
//		return new ArrayList<Channel>();
	}
	
	@Override
	public String DeploySystem(String sysId, String defChannelName) {
		
		List<Channel> dataList = new ArrayList<Channel>();
		try {
			dataList = channelDao.ListAll(Op.EQ("systemId", sysId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SystemDeploy().Deploy(sysId, dataList, defChannelName);
	}
	
	@Override
	public boolean IsChannelExist(String sysId, String channelName) {
		Channel channel = null;
		try {
			channel = GetChanelBean(sysId,channelName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return channel != null;
	}
	
	/**
	 * 获得通道信息
	 * @param sysId			系统标识
	 * @param channelName	通道名称
	 * @return				通道信息
	 */
	public Channel GetChanelBean(String sysId, String channelName) {
		
		Channel channel = null;
		try {
			channel = channelDao.Get(Op.EQ("systemId", sysId), Op.EQ("name", channelName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return channel;
	}
	
	private GWTComponent BeanToModel(Adapter adapter) {
		if (adapter == null)
			return null;
		
		GWTComponent comp = new GWTAdapter();
		comp.set(GWTComponent.N_ComponentId, adapter.getId());
		comp.set(GWTAdapter.N_Protocol, adapter.getProtocoltype()); //通讯协议类型
		comp.set(GWTAdapter.N_Desc, adapter.getDescription()); 		//描述
		comp.set(GWTAdapter.N_CsType, adapter.getCstype());			//发起端: 0/ 接收端: 1
		comp.set(GWTAdapter.N_PlugIn, adapter.getPluginname());		//组件类
		comp.set(GWTAdapter.N_ConfigTemplate, adapter.getCfginfo());//默认配置模板
		
		return comp;
	}
	
	private GWTComponent BeanToModel(MsgPacker msgPacker) {
		if (msgPacker == null)return null;
		
		GWTComponent comp = new GWTMsgType();
		comp.set(GWTComponent.N_ComponentId, msgPacker.getId());
		comp.set(GWTMsgType.N_StyleName, msgPacker.getStylename()); 		//报文样式名称
		comp.set(GWTMsgType.N_Type, msgPacker.getType()); 				//组包标示:0组包;1拆包
		comp.set(GWTMsgType.N_Protocol, msgPacker.getMessagetype());		//
		comp.set(GWTMsgType.N_Class, msgPacker.getClassname());			//组件类
		comp.set(GWTMsgType.N_Content, msgPacker.getContent());			//Style样式内容
		
		return comp;
	}
	
	private GWTComponent BeanToModel(TransRecognizer code) {
		if (code == null)return null;
		
		GWTComponent comp = new GWTRecCode();
		comp.set(GWTComponent.N_ComponentId, code.getId());
		comp.set(GWTRecCode.N_Name, code.getName()); 			//交易识别名称
		comp.set(GWTRecCode.N_Desc, code.getDescription()); 	//描述
		comp.set(GWTRecCode.N_Type, code.getType());			//识别类型
		comp.set(GWTRecCode.N_Class, code.getClassname());		//组件类
		comp.set(GWTRecCode.N_Template, code.getCfginfo());		//默认配置参数模板
		
		return comp;
	}
	
	private GWTChannel BeanToModel(Channel bean, String defaultChannelName){
		if(bean == null)return null;
		
		GWTChannel model = new GWTChannel();
		model.set(GWTChannel.N_ChannelId, bean.getId());
		model.set(GWTChannel.N_ChannelName, bean.getName());
		if(bean.getName().toString().equals(defaultChannelName))
			model.set(GWTChannel.N_IsSysDefault, true);
		model.set(GWTChannel.N_Adapter, BeanToModel(bean.getAdapter()));
		model.set(GWTChannel.N_IP, bean.getSendAdapterIP());
		model.set(GWTChannel.N_Port, String.valueOf(bean.getSendAdapterPort()));
		model.set(GWTChannel.N_AdapterConfig, bean.getAdapterCfgInfo());
		model.set(GWTChannel.N_TransRecognizer, BeanToModel(bean.getTransRecognizer()));
		model.set(GWTChannel.N_RecognizerCfgInfo, bean.getRecognizerCfgInfo());
		model.set(GWTChannel.N_Pack, BeanToModel(bean.getPackChannel()));
		model.set(GWTChannel.N_UnPack, BeanToModel(bean.getUnpackChannel()));
		
		return model;
	}

	private Adapter ModelToBean(GWTAdapter ada, Adapter adapter){
		
		String id = ada.get(GWTComponent.N_ComponentId).toString().isEmpty() ? null 
				: ada.get(GWTComponent.N_ComponentId).toString();
		String protocol = ada.get(GWTAdapter.N_Protocol);
		if(protocol == null)protocol="";
		String desc = ada.get(GWTAdapter.N_Desc);
		if(desc == null)desc="";
		Integer cstype = ada.get(GWTAdapter.N_CsType);
		String plugin = ada.get(GWTAdapter.N_PlugIn);
		if(plugin == null)plugin="";
		String config = ada.get(GWTAdapter.N_ConfigTemplate);
		if(config == null)config="";
		
		if(adapter == null){
			adapter = new Adapter();
			adapter.setId(id);
		}
		
		adapter.setProtocoltype(protocol);
		adapter.setDescription(desc);
		adapter.setCstype(cstype);
		adapter.setPluginname(plugin);
		adapter.setCfginfo(config);
		
		return adapter;
	}
	
	private MsgPacker ModelToBean(GWTMsgType msg, MsgPacker msgPacker){
		
		String id = msg.get(GWTComponent.N_ComponentId).toString().isEmpty() ? null 
				: msg.get(GWTComponent.N_ComponentId).toString();
		String styleName = msg.get(GWTMsgType.N_StyleName);
		if(styleName == null)styleName="";
		Integer type = msg.get(GWTMsgType.N_Type);
		String protocol = msg.get(GWTMsgType.N_Protocol);
		if(protocol == null)protocol="";
		String className = msg.get(GWTMsgType.N_Class);
		if(className == null)className="";
		String content = msg.get(GWTMsgType.N_Content);
		if(content == null)content="";
		
		if(msgPacker == null){
			msgPacker = new MsgPacker();
			msgPacker.setId(null);
		}
		
		msgPacker.setId(id);
		msgPacker.setStylename(styleName);
		msgPacker.setType(type);
		msgPacker.setMessagetype(protocol);
		msgPacker.setClassname(className);
		msgPacker.setContent(content);
		
		return msgPacker;
	}
	
	private TransRecognizer ModelToBean(GWTRecCode code, TransRecognizer c){
		
		String id = code.get(GWTComponent.N_ComponentId).toString().isEmpty() ? null 
				: code.get(GWTComponent.N_ComponentId).toString();
		String name = code.get(GWTRecCode.N_Name);
		if(name == null)name="";
		String desc = code.get(GWTRecCode.N_Desc);
		if(desc == null)desc="";
		String type = code.get(GWTRecCode.N_Type);
		if(type == null)type="";
		String cls = code.get(GWTRecCode.N_Class);
		if(cls == null)cls="";
		String temp = code.get(GWTRecCode.N_Template);
		if(temp == null)temp="";
		
		if(c == null){
			c = new TransRecognizer();
			c.setId(null);
		}
		
		c.setId(id);
		c.setName(name);
		c.setDescription(desc);
		c.setType(type);
		c.setClassname(cls);
		c.setCfginfo(temp);
		
		return c;
	}
	
	private Channel ModelToBean(GWTChannel channel){
		
		//String id = channel.get(GWTChannel.N_ChannelId);
		String name = channel.get(GWTChannel.N_ChannelName);
		if(name == null)name = "";
		//GWTAdapter
		GWTAdapter adapter = channel.get(GWTChannel.N_Adapter);
		String adapterId = adapter.get(GWTAdapter.N_ComponentId);
		String adapterIp = channel.get(GWTChannel.N_IP);
		if(adapterIp == null)adapterIp = "";
		String adapterPortStr = channel.get(GWTChannel.N_Port);
		String adapterConfig = channel.get(GWTChannel.N_AdapterConfig);
		if(adapterConfig == null)adapterConfig= "";
		int adapterPort = adapterPortStr.isEmpty() ? 0 : Integer.parseInt(adapterPortStr);
		//GWTPack
		GWTMsgType pack = channel.get(GWTChannel.N_Pack);
		GWTMsgType unpack = channel.get(GWTChannel.N_UnPack);
		//GWTRecCode
		GWTRecCode reccode = channel.get(GWTChannel.N_TransRecognizer);
		
		Adapter ada = new Adapter();
		ada.setId(adapterId);
		
		Channel c = new Channel();
		c.setId(null);
		c.setName(name);
		c.setAdapter(ada);
		c.setSendAdapterIP(adapterIp);
		c.setSendAdapterPort(adapterPort);
		c.setAdapterCfgInfo(adapterConfig);
		if(pack != null){
			MsgPacker p = new MsgPacker();
			String pid = pack.get(GWTMsgType.N_ComponentId);
			p.setId(pid);
			c.setPackChannel(p);
		}
		if(unpack != null){
			MsgPacker p = new MsgPacker();
			String pid = unpack.get(GWTMsgType.N_ComponentId);
			p.setId(pid);
			c.setUnpackChannel(p);
		}
		if(reccode != null){
			TransRecognizer rc = new TransRecognizer();
			String rcid = reccode.get(GWTRecCode.N_ComponentId);
			rc.setId(rcid);
			c.setTransRecognizer(rc);
			String config = channel.get(GWTChannel.N_RecognizerCfgInfo);
			c.setRecognizerCfgInfo(config);
		}
		return c;
	}

	


	
}
