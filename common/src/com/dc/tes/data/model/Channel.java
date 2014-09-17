package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * 
 * @author huangzx
 *
 */

@BeanIdName("id")
public class Channel implements Serializable{

	private static final long serialVersionUID = 2704388100252178163L;

	private String id;	//id
	private String name; //名称
	//private int adapterid;	//适配器ID
	//private int reccodeid;  //交易识别ID
	//private int packid;     //组包ID
	//private int unpackid;   //拆包ID
	//private int defaultfalg; //默认通道标示:0是默认通道;1不是默认通道
	
	private String sendAdapterIP;	//发起端适配器监听核心请求IP
	private int sendAdapterPort;	//发起端适配器监听核心请求端口
	private String adapterCfgInfo;	//适配器配置信息,配置数据
	private String recognizerCfgInfo;	//交易识别配置信息,配置数据
	
	private String systemId;	//关联系统ID
	
	private Adapter adapter;	//channel 关联的 适配器
	private TransRecognizer transRecognizer;	//channel 关联的 交易识别
	private MsgPacker packChannel;	//channel 关联的 组包
	private MsgPacker unpackChannel;	//channel 关联的 拆包
	
	private int channelType; //通道类型 0-远程通道 1-本地通道

	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	/**
	 * 	获取  channel 关联的 适配器
	 * @return channel 关联的 适配器
	 */
	public Adapter getAdapter() {
		return adapter;
	}

	/**
	 * 设置 channel 关联的 适配器
	 * @param adapter channel 关联的 适配器
	 */
	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * 获取 channel 关联的 交易识别
	 * @return channel 关联的 交易识别
	 */
	public TransRecognizer getTransRecognizer() {
		return transRecognizer;
	}

	/**
	 * 设置 channel 关联的 交易识别
	 * @param transRecognizer channel 关联的 交易识别
	 */
	public void setTransRecognizer(TransRecognizer transRecognizer) {
		this.transRecognizer = transRecognizer;
	}

	/**
	 * 获取 	channel 关联的 组包
	 * @return 设置 channel 关联的 组包
	 */
	public MsgPacker getPackChannel() {
		return packChannel;
	}

	/**
	 * 设置 channel 关联的 组包
	 * @param packchannel channel 关联的 组包
	 */
	public void setPackChannel(MsgPacker packchannel) {
		this.packChannel = packchannel;
	}

	/**
	 * 获取 channel 关联的 拆包
	 * @return channel 关联的 拆包
	 */
	public MsgPacker getUnpackChannel() {
		return unpackChannel;
	}

	/**
	 * 设置 channel 关联的 拆包
	 * @param unpackchannel channel 关联的 拆包
	 */
	public void setUnpackChannel(MsgPacker unpackchannel) {
		this.unpackChannel = unpackchannel;
	}

	/**
	 * 获取 id
	 * @return  id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 名称
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置 名称
	 * @param name 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取  适配器ID
	 * @return 适配器ID
	 */
//	public int getAdapterid() {
//		return adapterid;
//	}

	/**
	 * 设置  适配器ID
	 * @param adapterid 适配器ID
	 */
//	public void setAdapterid(int adapterid) {
//		this.adapterid = adapterid;
//	}

	/**
	 * 获取 交易识别ID
	 * @return 交易识别ID
	 */
//	public int getReccodeid() {
//		return reccodeid;
//	}

	/**
	 * 设置 交易识别ID
	 * @param reccodeid 交易识别ID
	 */
//	public void setReccodeid(int reccodeid) {
//		this.reccodeid = reccodeid;
//	}

	/**
	 * 获取 组包ID
	 * @return 组包ID
	 */
//	public int getPackid() {
//		return packid;
//	}

	/**
	 * 设置 组包ID
	 * @param packid 组包ID
	 */
//	public void setPackid(int packid) {
//		this.packid = packid;
//	}

	/**
	 * 获取 拆包ID
	 * @return 拆包ID
	 */
//	public int getUnpackid() {
//		return unpackid;
//	}

	/**
	 * 设置 拆包ID
	 * @param unpackid 拆包ID
	 */
//	public void setUnpackid(int unpackid) {
//		this.unpackid = unpackid;
//	}

	/**
	 * 获取 默认通道标示
	 * @return 0是默认通道;1不是默认通道
	 */
//	public int getDefaultfalg() {
//		return defaultfalg;
//	}

	/**
	 * 设置 默认通道标示
	 * @param defaultfalg 0是默认通道;1不是默认通道
	 */
//	public void setDefaultfalg(int defaultfalg) {
//		this.defaultfalg = defaultfalg;
//	}

	/**
	 * 获取 关联系统ID
	 * @return 关联系统ID
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * 设置 关联系统ID
	 * @param systemid 关联系统ID
	 */
	public void setSystemId(String systemid) {
		this.systemId = systemid;
	}

	/**
	 * 获取 发起端适配器监听核心请求IP
	 * @return 发起端适配器监听核心请求IP
	 */
	public String getSendAdapterIP() {
		return sendAdapterIP;
	}

	/**
	 * 设置 发起端适配器监听核心请求IP
	 * @param sendadapterip 发起端适配器监听核心请求IP
	 */
	public void setSendAdapterIP(String sendadapterip) {
		this.sendAdapterIP = sendadapterip;
	}

	/**
	 * 获取 发起端适配器监听核心请求端口
	 * @return 发起端适配器监听核心请求端口
	 */
	public int getSendAdapterPort() {
		return sendAdapterPort;
	}

	/**
	 * 设置 发起端适配器监听核心请求端口
	 * @param sendadapterport 发起端适配器监听核心请求端口
	 */
	public void setSendAdapterPort(int sendadapterport) {
		this.sendAdapterPort = sendadapterport;
	}

	/**
	 * 获取 适配器配置信息,配置数据
	 * @return 适配器配置信息,配置数据
	 */
	public String getAdapterCfgInfo() {
		return adapterCfgInfo;
	}

	/**
	 * 设置 适配器配置信息,配置数据
	 * @param adaptercfginfo 适配器配置信息,配置数据
	 */
	public void setAdapterCfgInfo(String adaptercfginfo) {
		this.adapterCfgInfo = adaptercfginfo;
	}

	/**
	 * 获取 交易识别配置信息,配置数据
	 * @return 交易识别配置信息,配置数据
	 */
	public String getRecognizerCfgInfo() {
		return recognizerCfgInfo;
	}

	/**
	 * 设置 交易识别配置信息,配置数据
	 * @param reccodecfginfo 交易识别配置信息,配置数据
	 */
	public void setRecognizerCfgInfo(String recognizerCfgInfo) {
		this.recognizerCfgInfo = recognizerCfgInfo;
	}

	/**
	 * 获取 通道类型 0-远程通道 1-本地通道
	 * @return 通道类型 0-远程通道 1-本地通道
	 */
	public int getChannelType() {
		return channelType;
	}

	/**
	 * 设置 通道类型 0-远程通道 1-本地通道
	 * @param channeltype 通道类型 0-远程通道 1-本地通道
	 */
	public void setChannelType(int channeltype) {
		this.channelType = channeltype;
	}

	/**
	 * @param createdUserId the createdUserId to set
	 */
	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	/**
	 * @return the createdUserId
	 */
	public String getCreatedUserId() {
		return createdUserId;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedUserId the lastModifiedUserId to set
	 */
	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * @return the lastModifiedUserId
	 */
	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}
	
	
	
}
