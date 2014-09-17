package com.dc.tes.adapter.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.context.IAdapterEnvContext;

public abstract class AbstractAdapterEnvContext implements IAdapterEnvContext{
	private static Log log = LogFactory.getLog(AbstractAdapterEnvContext.class);
	
	
	private byte[] m_context = null;
	
	public AbstractAdapterEnvContext(byte[] config){
		m_context = (byte[]) config.clone();
	}
	
	public byte[] getEvnContext() {
		return (byte[]) m_context.clone();
	}
}
