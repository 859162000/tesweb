package com.dc.tes.adapter.local;

import java.util.Properties;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.helper.IRequestAdapterHelper;
import com.dc.tes.adapter.util.RequestListLocal;

public class RequestAdapterHelperLocal extends AbstractAdapterHelperLocal implements IRequestAdapterHelper{

	/**
	 * 通道名称
	 */
	private final String m_channleName;
	
	/**
	 * 发起端适配器插件实例
	 */
	private final IRequestAdapter m_adapterInst;
		
	public RequestAdapterHelperLocal(Properties props,String channleName,IRequestAdapter adapterInst ) {
		super(props);
		this.m_channleName = channleName;
		this.m_adapterInst = adapterInst;
	}

	public void startServer() {
		// 维护通道名称与发起端适配器实例列表
		RequestListLocal.setChannelListItem(m_channleName, m_adapterInst);
	}

	public void stopServer() {
		RequestListLocal.clearChannelListItem();
	}

}
