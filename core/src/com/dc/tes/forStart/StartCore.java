package com.dc.tes.forStart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.dc.tes.fcore.FCore;
import com.dc.tes.pcore.PCore;

public class StartCore {
	private static final Log log = LogFactory.getLog(StartCoreAndLocalAdapter.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			if(args.length ==1){
				if(args[0].equals("PCORE")){
					PCore.StartCore();
				}else{
					FCore.StartCore();
				}
			}else{
				FCore.StartCore();
			}
	}
}
