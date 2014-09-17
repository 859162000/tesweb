package com.dc.tes.ui.client;

import com.dc.tes.ui.client.model.GWTCopiedSystem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ICopiedSystemServiceAsync {
	
	//boolean Save(GWTCopiedSystem gwtCopiedSystem);
	void Save(GWTCopiedSystem gwtCopiedSystem, AsyncCallback<Boolean> callback);
	
}
