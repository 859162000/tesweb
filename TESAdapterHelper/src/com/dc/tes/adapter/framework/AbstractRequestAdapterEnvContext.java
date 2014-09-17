package com.dc.tes.adapter.framework;

import com.dc.tes.adapter.context.IRequestAdapterEnvContext;
//import com.dc.tes.adapter.context.IRequestAdapterHelper;

public abstract class AbstractRequestAdapterEnvContext extends AbstractAdapterEnvContext implements
		IRequestAdapterEnvContext {

	//IRequestAdapterHelper m_helper = null;
	
	public AbstractRequestAdapterEnvContext(byte[] config){
		super(config);
	}
	
//	public AbstractRequestAdapterEnvContext(byte[] config, IRequestAdapterHelper helper) {
//		super(config);
//		
//		m_helper = helper;
//	}

	

}
