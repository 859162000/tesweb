package com.dc.tes.ui.client.common;

import com.dc.tes.ui.client.IBatchService;
import com.dc.tes.ui.client.ILoginLogService;
import com.dc.tes.ui.client.ICaseStatisticsService;
import com.dc.tes.ui.client.IOperationLogService;
import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.IClientTransaction;
import com.dc.tes.ui.client.IDbHostService;
import com.dc.tes.ui.client.IRecordedCaseService;
import com.dc.tes.ui.client.IComponent;
import com.dc.tes.ui.client.ICopiedSystemService;
import com.dc.tes.ui.client.IExecutePlanService;
import com.dc.tes.ui.client.IExecuteSetService;
import com.dc.tes.ui.client.IHelperService;
import com.dc.tes.ui.client.IInterfaceService;
import com.dc.tes.ui.client.ILaunchService;
import com.dc.tes.ui.client.IMonitorService;
import com.dc.tes.ui.client.IPersistentDataService;
import com.dc.tes.ui.client.IExecuteSetExecutePlanService;
import com.dc.tes.ui.client.IQueueService;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.ISimuStatus;
import com.dc.tes.ui.client.ISimuSystemService;
import com.dc.tes.ui.client.IStatics;
import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.ITestRoundService;
import com.dc.tes.ui.client.IUseCaseService;
import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserSysService;
import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * 服务帮助类，提供返回各个异步服务的接口
 * 
 * @author scckobe
 *
 */
public class ServiceHelper {
	
	/**
	 * 通过泛型创建异步服务接口，带缓存
	 * 
	 * @param <T> 异步服务接口类型
	 * @param classLiteral RemoteService接口类型
	 * @param servletName  Service对应Servlet的地址
	 * @return  异步服务接口
	 */
	@SuppressWarnings("unchecked")
	public static <T> T GetDynamicService(String servletName, Class<?> classLiteral){
		
		T service = null;
		service = (T)Registry.get(servletName);
		if(service != null)
			return service;
		else{
			if(servletName.equals("transerver"))
				service = (T)GWT.create(IClientTransaction.class);
			else if(servletName.equals("simuSys"))
				service = (T)GWT.create(ISimuSystemService.class);
			else if(servletName.equals("user"))
				service = (T)GWT.create(IUserService.class);
			else if(servletName.equals("case"))
				service = (T)GWT.create(ICaseService.class);
			else if(servletName.equals("scriptFlow"))
				service = (T)GWT.create(IScriptFlowService.class);
			else if(servletName.equals("queue"))
				service = (T)GWT.create(IQueueService.class);
			else if(servletName.equals("helper"))
				service = (T)GWT.create(IHelperService.class);
			else if(servletName.equals("pData"))
				service = (T)GWT.create(IPersistentDataService.class);
			else if(servletName.equals("statistic"))
				service = (T)GWT.create(IStatics.class);
			else if(servletName.equals("userSys"))
				service = (T)GWT.create(IUserSysService.class);
			else if(servletName.equals("copiedSystem"))
				service = (T)GWT.create(ICopiedSystemService.class);			
			else if(servletName.equals("moniserver"))
				service = (T)GWT.create(IMonitorService.class);
			else if(servletName.equals("simustatus"))
				service = (T)GWT.create(ISimuStatus.class);
			else if(servletName.equals("component"))
				service = (T)GWT.create(IComponent.class);
			else if(servletName.equals("result"))
				service = (T)GWT.create(IResultService.class);
			else if(servletName.equals("batchNo"))
				service = (T)GWT.create(IBatchService.class);
			else if(servletName.equals("sysPara"))
				service = (T)GWT.create(ISysDynamicParameter.class);
			else if(servletName.equals("recordedCase"))
				service = (T)GWT.create(IRecordedCaseService.class);
			else if(servletName.equals("dbHost"))
				service = (T)GWT.create(IDbHostService.class);			
			else if(servletName.equals("launchserver"))
				service = (T)GWT.create(ILaunchService.class);
			else if(servletName.equals("useCase"))
				service = (T)GWT.create(IUseCaseService.class);
			else if(servletName.equals("executeSet"))
				service = (T)GWT.create(IExecuteSetService.class);
			else if(servletName.equals("testRound"))
				service = (T)GWT.create(ITestRoundService.class);
			else if(servletName.equals("executePlan"))
				service = (T)GWT.create(IExecutePlanService.class);
			else if(servletName.equals("executeSetExecutePlan"))
				service = (T)GWT.create(IExecuteSetExecutePlanService.class);
			else if(servletName.equals("interface")){
				service = (T)GWT.create(IInterfaceService.class);
			}else if(servletName.equals("operationLog"))
				service = (T)GWT.create(IOperationLogService.class);
			else if(servletName.equals("loginLog"))
				service = (T)GWT.create(ILoginLogService.class);
			else if(servletName.equals("caseStatistics"))
				service = (T)GWT.create(ICaseStatisticsService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) service;
			String moduleRelativeURL = GWT.getHostPageBaseURL() + servletName;
			endpoint.setServiceEntryPoint(moduleRelativeURL);
			
			Registry.register(servletName, service);
		}
		
		return service;
	}
}
