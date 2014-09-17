package com.dc.tes.ui.client;

import com.dc.tes.ui.client.model.GWTCopiedSystem;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ICopiedSystemService extends RemoteService {
	
	//boolean Save(GWTCopiedSystem gwtCopiedSystem);
	Boolean Save(GWTCopiedSystem gwtCopiedSystem);
	
}
