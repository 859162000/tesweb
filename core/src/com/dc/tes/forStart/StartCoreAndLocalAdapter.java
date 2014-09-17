package com.dc.tes.forStart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.dc.tes.adapter.startup.IStartUp;
import com.dc.tes.adapter.startup.local.*;
import com.dc.tes.adapter.util.StartUpUtils;
import com.dc.tes.fcore.FCore;
import com.dc.tes.pcore.PCore;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

public class StartCoreAndLocalAdapter {

	private static final Log log = LogFactory.getLog(StartCoreAndLocalAdapter.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int localnum=0;
			if(args.length ==1){
				if(args[0].equals("PCORE")){
					localnum = PCore.StartCore();
				}else{
					localnum = FCore.StartCore();
				}
			}else{
				localnum = FCore.StartCore();
			}
			if(localnum>0){
				IStartUp localAdapter = StartUpUtils.getIStartUpInst("local");
				localAdapter.startUp();
			}
	}

}
