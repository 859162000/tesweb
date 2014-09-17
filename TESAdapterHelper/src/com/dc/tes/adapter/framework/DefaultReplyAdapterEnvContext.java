package com.dc.tes.adapter.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;


public class DefaultReplyAdapterEnvContext extends AbstractAdapterEnvContext implements	IReplyAdapterEnvContext{ //extends AbstractReplyAdapterEnvContext {
	
	private static Log log = LogFactory.getLog(DefaultReplyAdapterEnvContext.class);
	
	IReplyAdapterHelper m_helper = null;
	
	public DefaultReplyAdapterEnvContext(byte[] config,	IReplyAdapterHelper helper) {
		//super(config, helper);
		super(config);
		m_helper = helper;
	}

	public IReplyAdapterHelper getHelper() {

		return m_helper;
	}
	
}
