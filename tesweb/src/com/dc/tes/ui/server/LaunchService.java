package com.dc.tes.ui.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dc.tes.monitor.data.Context;
import com.dc.tes.ui.client.ILaunchService;
import com.dc.tes.ui.client.model.GWTCore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class LaunchService extends RemoteServiceServlet implements ILaunchService {

	private static final long serialVersionUID = 8066071534176672647L;
	
	@Override
	public ArrayList<String> GetLaunchLog() {
		// TODO Auto-generated method stub
		ArrayList<String> log = Context.getCoreLog();
		
		return log;
	}

	@Override
	public List<GWTCore> GetCoreConfig() {
		// TODO Auto-generated method stub
			
		List<GWTCore> coreList= new ArrayList<GWTCore>();
		
		String webBasePath = new HelperService().GetRootPath();
		String configPath = webBasePath + "deploy\\";
		
		File file = new File(configPath);
		if(file.isDirectory()) {
			File[] dirs = file.listFiles();
			for(int i=0; i<dirs.length; i++) {
				//系统部署文件
				File sysfile = dirs[i];
				if(!sysfile.isDirectory())
					continue;
				if(sysfile.getName().equals("template") ||
						sysfile.getName().contains("lib"))
					continue;
				File[] filename = sysfile.listFiles();
				String core="",receiver="",sender="",corepath="";
				
				for(int j=0; j<filename.length; j++) {
					if(filename[j].isDirectory()) {
						
						String temp = filename[j].getName();
						
						if(temp.toLowerCase().contains("core")) {
							core = temp;
							corepath = filename[j].getPath();
						}
						else if(temp.toLowerCase().contains("receive"))
							receiver = temp;
						else if(temp.toLowerCase().contains("send"))
							sender = temp;
					}					
				}
				
				coreList.add(new GWTCore(sysfile.getName(),sysfile.getPath(),
						core,corepath,receiver,sender));

			}
			return coreList;
		}
		return null;
	}

	@Override
	public void LaunchCore(String corepath) {
		// TODO Auto-generated method stub		
		Context.clearLog();
		String path = corepath +"\\run.bat";	
		File file = new File(corepath);
		try { 
			Runtime.getRuntime().exec("cmd /c start "+path.replaceAll(" ", "\" \""),null,file); 
		} catch(Exception ex) {
			
		}
		
	}

	@Override
	public void StopCore(String corepath) {
		// TODO Auto-generated method stub
		String path = corepath + "\\stop.bat";
		try {
			Runtime.getRuntime().exec("cmd /k start " + path.replaceAll(" ", "\" \""));
		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<String> GetSenderLog() {
		// TODO Auto-generated method stub
		ArrayList<String> log = Context.getSenderLog();
		
		return log;
	}

	@Override
	public List<String> GetReceiverLog() {
		// TODO Auto-generated method stub
		ArrayList<String> log = Context.getReceiverLog();
		
		return log;
	}

}
