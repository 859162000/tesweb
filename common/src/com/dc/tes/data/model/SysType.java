package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * SysType:系统 JavaBean映射类
 * 
 * @author huangzx
 * 
 */
@BeanIdName("systemId")
public class SysType implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String systemId; // 系统ID
	private String systemNo; // 系统编码
	private String systemName; // 系统名称
	private String desc; // 系统描述信息
	private String stylestruct; // 报文样式结构
	private int flag; // 状态位

	private String ipadress; // IP 地址
	private int portnum; // 端口号

	private String channel; // 通道名称

	private String parameterGetSequence; // 系统参数获取顺序

	private String basecfg; // 界面生成的base.xml配置信息，供核心读取

	private long maxdelaytime; // 系统级延迟上限(单位:毫秒)
	private long mindelaytime; // 系统级延迟下限(单位:毫秒)

	private int delaytimetype; // 延迟类型(0-系统延迟 1-交易延迟 2-叠加延迟)
	private int isParamModified;//参数是否已改动
	private int isused; // 该系统是否被监控正在使用:0-开启(使用状态) 1-关闭

	private int needSqlCheck;
	private int transactionTimeOut;

	private int sqlGetMethod;
	private String sqlGetDbAddr;
	
	private String encoding4RequestMsg;
	private String encoding4ResponseMsg;
	
	private int isClientSimu;
	private int isSyncComm;
	private int useSameResponseStruct;
	private String responseStruct;
	private int responseMode;
	
	private String reqMsgUnpackerId;
	private String resMsgUnpackerId;
	
	private String businessProcessor; //业务处理组件
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	

	
	//end
	
	// 系统与用户
	/*
	 * private Set System2User = new HashSet();
	 * 
	 * public Set getSystem2User() { return System2User; }
	 * 
	 * public void setSystem2User(Set system2User) { System2User = system2User;
	 * }
	 */

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * 获取系统ID
	 * 
	 * @return systemId：系统ID
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * 设置系统ID,此字段由数据库自动生成
	 * 
	 * @param systemId
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * 获取系统编码
	 * 
	 * @return systemNo：系统编码
	 */
	public String getSystemNo() {
		return systemNo;
	}

	/**
	 * 设置系统编码
	 * 
	 * @param systemNo
	 *            ：系统编码
	 */
	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	/**
	 * 获取系统名称
	 * 
	 * @return systemName：系统名称
	 */
	public String getSystemName() {
		return systemName;
	}

	/**
	 * 设置系统名称
	 * 
	 * @param systemName
	 *            ：系统名称
	 */
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	/**
	 * 获取系统描述
	 * 
	 * @return desc: 系统描述
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 设置系统描述
	 * 
	 * @param desc
	 *            : 系统描述
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	

	/**
	 * 获取系统名称
	 * 
	 * @return parameterGetSequence：系统名称
	 */
	public String getParameterGetSequence() {
		return parameterGetSequence;
	}

	/**
	 * 设置系统名称
	 * 
	 * @param parameterGetSequence
	 *            ：系统名称
	 */
	public void setParameterGetSequence(String parameterGetSequence) {
		this.parameterGetSequence = parameterGetSequence;
	}

	
	/**
	 * 获取 报文样式
	 * 
	 * @return
	 */
	public String getStylestruct() {
		return stylestruct;
	}

	/**
	 * 设置 报文样式
	 * 
	 * @param stylestruct
	 */
	public void setStylestruct(String stylestruct) {
		this.stylestruct = stylestruct;
	}

	/**
	 * 获取状态位
	 * 
	 * @return flag：0—正常 1—删除
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * 设置状态位
	 * 
	 * @param flag
	 *            ：0—正常 1—删除
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * 获取 IP地址
	 * 
	 * @return IP地址
	 */
	public String getIpadress() {
		return ipadress;
	}

	/**
	 * 设置 IP 地址
	 * 
	 * @param ipadress
	 *            IP地址
	 */
	public void setIpadress(String ipadress) {
		this.ipadress = ipadress;
	}

	/**
	 * 获取 端口号
	 * 
	 * @return 端口号
	 */
	public int getPortnum() {
		return portnum;
	}

	/**
	 * 设置 端口号
	 * 
	 * @param portnum
	 *            端口号
	 */
	public void setPortnum(int portnum) {
		this.portnum = portnum;
	}

	/**
	 * 获取 通道名称
	 * 
	 * @return 通道名称
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * 设置 通道名称
	 * 
	 * @param channel
	 *            通道名称
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * 获取 界面生成的base.xml配置信息
	 * 
	 * @return 界面生成的base.xml配置信息
	 */
	public String getBasecfg() {
		return basecfg;
	}

	/**
	 * 设置 界面生成的base.xml配置信息
	 * 
	 * @param basecfg
	 *            界面生成的base.xml配置信息
	 */
	public void setBasecfg(String basecfg) {
		this.basecfg = basecfg;
	}

	/**
	 * 获取 系统级延迟上限(单位:毫秒)
	 * 
	 * @return 系统级延迟上限(单位:毫秒)
	 */
	public long getMaxdelaytime() {
		return maxdelaytime;
	}

	/**
	 * 设置 系统级延迟上限(单位:毫秒)
	 * 
	 * @param updelaytime
	 *            系统级延迟上限(单位:毫秒)
	 */
	public void setMaxdelaytime(long maxdelaytime) {
		this.maxdelaytime = maxdelaytime;
	}

	/**
	 * 获取 系统级延迟下限(单位:毫秒)
	 * 
	 * @return 系统级延迟下限(单位:毫秒)
	 */
	public long getMindelaytime() {
		return mindelaytime;
	}

	/**
	 * 设置 系统级延迟下限(单位:毫秒)
	 * 
	 * @param downdelaytime
	 *            系统级延迟下限(单位:毫秒)
	 */
	public void setMindelaytime(long mindelaytime) {
		this.mindelaytime = mindelaytime;
	}

	/**
	 * 获取 延迟类型(0-系统延迟 1-交易延迟 2-叠加延迟)
	 * 
	 * @return 延迟类型(0-系统延迟 1-交易延迟 2-叠加延迟)
	 */
	public int getDelaytimetype() {
		return delaytimetype;
	}

	/**
	 * 设置 延迟类型(0-系统延迟 1-交易延迟 2-叠加延迟)
	 * 
	 * @param delaytimetype
	 *            延迟类型(0-系统延迟 1-交易延迟 2-叠加延迟)
	 */
	public void setDelaytimetype(int delaytimetype) {
		this.delaytimetype = delaytimetype;
	}

	/**
	 * 获取 系统使用状态(被监控使用)
	 * 
	 * @return 0-开启(使用状态) 1-关闭
	 */
	public int getIsused() {
		return isused;
	}

	/**
	 * 设置 系统使用状态(被监控使用)
	 * 
	 * @param isused
	 *            : 0-开启(使用状态) 1-关闭
	 */
	public void setIsused(int isused) {
		this.isused = isused;
	}

	public void setIsParamModified(int isParamModified) {
		this.isParamModified = isParamModified;
	}

	public int getIsParamModified() {
		return isParamModified;
	}

	public void setTransactionTimeOut(int transactionTimeOut) {
		this.transactionTimeOut = transactionTimeOut;
	}

	public int getTransactionTimeOut() {
		return transactionTimeOut;
	}

	public void setNeedSqlCheck(int needSqlCheck) {
		this.needSqlCheck = needSqlCheck;
	}

	public int getNeedSqlCheck() {
		return needSqlCheck;
	}

	public int getSqlGetMethod() {
		return sqlGetMethod;
	}

	public void setSqlGetMethod(int sqlGetMethod) {
		this.sqlGetMethod = sqlGetMethod;
	}

	public String getSqlGetDbAddr() {
		return sqlGetDbAddr;
	}

	public void setSqlGetDbAddr(String sqlGetDbAddr) {
		this.sqlGetDbAddr = sqlGetDbAddr;
	}
	
	
	public String getEncoding4RequestMsg() {
		return encoding4RequestMsg;
	}

	public void setEncoding4RequestMsg(String encoding4RequestMsg) {
		this.encoding4RequestMsg = encoding4RequestMsg;
	}

	public String getEncoding4ResponseMsg() {
		return encoding4ResponseMsg;
	}

	public void setEncoding4ResponseMsg(String encoding4ResponseMsg) {
		this.encoding4ResponseMsg = encoding4ResponseMsg;
	}

	/**
	 * @param isClientSimu the isClientSimu to set
	 */
	public void setIsClientSimu(int isClientSimu) {
		this.isClientSimu = isClientSimu;
	}

	/**
	 * @return the isClientSimu
	 */
	public int getIsClientSimu() {
		return isClientSimu;
	}

	/**
	 * @param isSyncComm the isSyncComm to set
	 */
	public void setIsSyncComm(int isSyncComm) {
		this.isSyncComm = isSyncComm;
	}

	/**
	 * @return the isSyncComm
	 */
	public int getIsSyncComm() {
		return isSyncComm;
	}

	/**
	 * @param responseStruct the responseStruct to set
	 */
	public void setResponseStruct(String responseStruct) {
		this.responseStruct = responseStruct;
	}

	/**
	 * @return the responseStruct
	 */
	public String getResponseStruct() {
		return responseStruct;
	}

	/**
	 * @param useSameResponseStruct the useSameResponseStruct to set
	 */
	public void setUseSameResponseStruct(int useSameResponseStruct) {
		this.useSameResponseStruct = useSameResponseStruct;
	}

	/**
	 * @return the useSameResponseStruct
	 */
	public int getUseSameResponseStruct() {
		return useSameResponseStruct;
	}

	/**
	 * @param responseMode the responseMode to set
	 */
	public void setResponseMode(int responseMode) {
		this.responseMode = responseMode;
	}

	/**
	 * @return the responseMode
	 */
	public int getResponseMode() {
		return responseMode;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}
	
	/**
	 * 设置发起端请求报文拆包组件ID
	 * @return
	 */
	public void setReqMsgUnpackerId(String reqMsgUnpackerId) {
		this.reqMsgUnpackerId = reqMsgUnpackerId;
	}

	/**
	 * 获取发起端请求报文拆包组件ID
	 * @return
	 */
	public String getReqMsgUnpackerId() {
		return reqMsgUnpackerId;
	}

	/**
	 * 设置接收端响应报文拆包组件ID
	 * @return
	 */
	public void setResMsgUnpackerId(String resMsgUnpackerId) {
		this.resMsgUnpackerId = resMsgUnpackerId;
	}

	/**
	 * 获取接收端响应报文拆包组件ID
	 * @return
	 */
	public String getResMsgUnpackerId() {
		return resMsgUnpackerId;
	}

	public void setBusinessProcessor(String businessProcessor) {
		this.businessProcessor = businessProcessor;
	}

	public String getBusinessProcessor() {
		return businessProcessor;
	}


	/**
	 * 获取 交易码识别ID
	 * 
	 * @return 交易码识别ID
	 */
	// public String getReccodeid() {
	// return reccodeid;
	// }
	/**
	 * 设置 交易码识别ID
	 * 
	 * @param reccodeid
	 *            交易码识别ID
	 */
	// public void setReccodeid(String reccodeid) {
	// this.reccodeid = reccodeid;
	// }
	/**
	 * 获取 组包ID
	 * 
	 * @return 组包ID
	 */
	// public String getPackid() {
	// return packid;
	// }
	/**
	 * 设置 组包ID
	 * 
	 * @param packid
	 *            组包ID
	 */
	// public void setPackid(String packid) {
	// this.packid = packid;
	// }
	/**
	 * 获取 拆包ID
	 * 
	 * @return 拆包ID
	 */
	// public String getUnpackid() {
	// return unpackid;
	// }
	/**
	 * 设置 拆包ID
	 * 
	 * @param unpackid
	 *            拆包ID
	 */
	// public void setUnpackid(String unpackid) {
	// this.unpackid = unpackid;
	// }

}
