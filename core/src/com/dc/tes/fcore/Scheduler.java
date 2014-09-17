package com.dc.tes.fcore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dc.tes.data.model.ExecutePlan;
import com.dc.tes.data.model.ExecuteSetExecutePlan;
import com.dc.tes.data.model.SysType;


public class Scheduler {
	
	public static ThreadLocal<SysType> m_sysType = new ThreadLocal<SysType>();
	
	//private SysType m_sysType = null;
		
	/*public Scheduler(SysType sysType) {
		m_sysType = sysType;
	}*/

	
	public static int Run(SysType sysType) {
		
		m_sysType.set(sysType);
		
		int iScheduledTaskCount = 0;
		
		//每次后台启动都要跑一次的
		/*List<ExecutePlan> executePlanList0 = DbGet.getExecutePlan(sysType.getSystemId(), 0);
		for (int i=0; i<executePlanList0.size(); i++) {
			ExecutePlan executePlan = executePlanList0.get(i);
			iScheduledTaskCount += runCompulsoryExecutePlans(executePlan);
		}*/
		
		//只跑一次的
		List<ExecutePlan> executePlanList1 = DbGet.getExecutePlan(sysType.getSystemId(), 1);
		for (int i=0; i<executePlanList1.size(); i++) {
			ExecutePlan executePlan = executePlanList1.get(i);
			iScheduledTaskCount += traverseRunOncePlans(executePlan);
		}
		
		//每天跑一次的
		List<ExecutePlan> executePlanList2 = DbGet.getExecutePlan(sysType.getSystemId(), 2);
		for (int i=0; i<executePlanList2.size(); i++) {
			ExecutePlan executePlan = executePlanList2.get(i);
			iScheduledTaskCount += traverseDailyPlans(executePlan);
		}
		
		//每周跑一次的
		List<ExecutePlan> executePlanList3 = DbGet.getExecutePlan(sysType.getSystemId(), 3);
		for (int i=0; i<executePlanList3.size(); i++) {
			ExecutePlan executePlan = executePlanList3.get(i);
			iScheduledTaskCount += traverseWeeklyPlans(executePlan);
		}

		//每月跑一次的
		List<ExecutePlan> executePlanList4 = DbGet.getExecutePlan(sysType.getSystemId(), 4);
		for (int i=0; i<executePlanList4.size(); i++) {
			ExecutePlan executePlan = executePlanList4.get(i);
			iScheduledTaskCount += traverseMonthlyPlans(executePlan);
		}
		
		return iScheduledTaskCount;
	}

	//每次后台启动都要跑一次的执行计划（已经选定了一个特定的执行计划记录）
	/*private static int runCompulsoryExecutePlans(ExecutePlan executePlan) {
		
		int iScheduledTaskCount = 0;
		
		int iExecutePlanId = executePlan.getId();
		List<ExecuteSetExecutePlan> queueListExecutePlanList = DbGet.getExecuteSetExecutePlan(iExecutePlanId);
		//遍历其下所有的任务队列的执行计划表
		for (int i=0; i<queueListExecutePlanList.size(); i++) {
			ExecuteSetExecutePlan executeSetExecutePlan = queueListExecutePlanList.get(i);
			iScheduledTaskCount += processOneQueueListExecutePlan(executeSetExecutePlan);
		}
		return iScheduledTaskCount;
	}*/
	

	//遍历 选定的 “只执行一次” 的执行计划下的 所有 任务队列
	private static int traverseRunOncePlans(ExecutePlan executePlan) {

		int iScheduledTaskCount = 0;
		
		//当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDate = sdf.format(new Date());
		
		//执行计划中所设定的执行日期
		String scheduleRunWeekDay =  executePlan.getScheduleRunWeekDay();
		//当前日期 <> 计划日期
		if (currentDate.compareTo(scheduleRunWeekDay) != 0) {
			//日期不符合，无须执行
			return iScheduledTaskCount;
		}

		//当前钟点
		sdf = new SimpleDateFormat("HH:MM");
		String currentHour = sdf.format(new Date());

		//执行计划中所设定的执行钟点
		String scheduleRunHour =  executePlan.getScheduleRunHour();
		if (!scheduleRunHour.isEmpty()) {
			//当前钟点 > 计划钟点
			if (currentHour.compareTo(scheduleRunHour) < 0) {
				//钟点还未到，无须执行
				return iScheduledTaskCount;
			}
		}
		
		int iExecutePlanId = executePlan.getId();
		List<ExecuteSetExecutePlan> executeSetExecutePlanList = DbGet.getExecuteSetExecutePlan(iExecutePlanId);
		//遍历其下所有的任务队列的执行计划表
		for (int i=0; i<executeSetExecutePlanList.size(); i++) {
			ExecuteSetExecutePlan executeSetExecutePlan = executeSetExecutePlanList.get(i);
			Date lastScheduledRunTime = executeSetExecutePlan.getBeginRunTime();
			if (lastScheduledRunTime != null && executeSetExecutePlan.getScheduledRunStatus() != 0) {
				//执行过了的！
				continue;
			}
			iScheduledTaskCount += processOneQueueListExecutePlan(executeSetExecutePlan);
		}
		
		return iScheduledTaskCount;
	}
	
	
	//每天都要跑的执行计划
	private static int traverseDailyPlans(ExecutePlan executePlan) {
		
		int iScheduledTaskCount = 0;
		
		//当前钟点
		SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
		String currentHour = sdf.format(new Date());

		//执行计划中所设定的执行钟点
		String scheduleRunHour =  executePlan.getScheduleRunHour();
		if (!scheduleRunHour.isEmpty()) {
			//当前钟点 > 计划钟点
			if (currentHour.compareTo(scheduleRunHour) < 0) {
				//钟点还未到，无须执行
				return iScheduledTaskCount;
			}
		}
		
		int iExecutePlanId = executePlan.getId();
		List<ExecuteSetExecutePlan> executeSetExecutePlanList = DbGet.getExecuteSetExecutePlan(iExecutePlanId);
		//遍历其下所有的任务队列的执行计划表
		for (int i=0; i<executeSetExecutePlanList.size(); i++) {
			ExecuteSetExecutePlan executeSetExecutePlan = executeSetExecutePlanList.get(i);
			Date lastScheduledRunTime = executeSetExecutePlan.getBeginRunTime();
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			String currentDate = sdf.format(new Date());
			if (lastScheduledRunTime != null) {
				String lastRunDate = lastScheduledRunTime.toString().substring(0, 10);
				if (currentDate.compareTo(lastRunDate) == 0 && executeSetExecutePlan.getScheduledRunStatus() != 0) {
					//今天执行过了的，不要再执行了！
					continue;
				}
			}
			iScheduledTaskCount += processOneQueueListExecutePlan(executeSetExecutePlan);
		}
		
		return iScheduledTaskCount;
	}
	
	
	//每个星期都要跑的执行计划
	private static int traverseWeeklyPlans(ExecutePlan executePlan) {

		int iScheduledTaskCount = 0;
		
		//执行计划中所设定的执行日期
		String scheduleRunWeekDay =  executePlan.getScheduleRunWeekDay();
		if (scheduleRunWeekDay == null || scheduleRunWeekDay.isEmpty()) {
			return 0;
		}
		
		String strWeekDay = "";
		for (int i=0; i<scheduleRunWeekDay.length(); i++) {
			strWeekDay = scheduleRunWeekDay.substring(i, i+1);
			if (strWeekDay.isEmpty()) {
				continue;
			}
			try {
				int iWeekDay = Integer.parseInt(strWeekDay);
				iScheduledTaskCount += processOneWeekDay(executePlan, iWeekDay);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return iScheduledTaskCount;
	}
	
	
	private static int processOneWeekDay(ExecutePlan executePlan, int iWeekDay) {

		int iScheduledTaskCount = 0;
		
		String scheduleRunWeekDay = "";
		switch (iWeekDay) {
			case 1: 
				scheduleRunWeekDay = "星期一";
				break;
			case 2: 
				scheduleRunWeekDay = "星期二";
				break;
			case 3: 
				scheduleRunWeekDay = "星期三";
				break;
			case 4: 
				scheduleRunWeekDay = "星期四";
				break;
			case 5: 
				scheduleRunWeekDay = "星期五";
				break;
			case 6: 
				scheduleRunWeekDay = "星期六";
				break;
			case 0:
			case 7:
				scheduleRunWeekDay = "星期日";
				break;
			default:
		}
		
		//当前星期
		SimpleDateFormat sdf = new SimpleDateFormat("E");
		String currentWeekDay = sdf.format(new Date());
		
		//当前星期 <> 计划星期
		if (currentWeekDay.compareTo(scheduleRunWeekDay) != 0) {
			//不是指定的星期，无须执行
			return 0;
		}

		//当前钟点
		sdf = new SimpleDateFormat("HH:MM");
		String currentHour = sdf.format(new Date());

		//执行计划中所设定的执行钟点
		String scheduleRunHour =  executePlan.getScheduleRunHour();
		if (!scheduleRunHour.isEmpty()) {
			//当前钟点 > 计划钟点
			if (currentHour.compareTo(scheduleRunHour) < 0) {
				//钟点还未到，无须执行
				return 0;
			}
		}
		
		int iExecutePlanId = executePlan.getId();
		List<ExecuteSetExecutePlan> queueListExecutePlanList = DbGet.getExecuteSetExecutePlan(iExecutePlanId);
		//遍历其下所有的任务队列的执行计划表
		for (int i=0; i<queueListExecutePlanList.size(); i++) {
			ExecuteSetExecutePlan executeSetExecutePlan = queueListExecutePlanList.get(i);
			Date lastScheduledRunTime = executeSetExecutePlan.getBeginRunTime();
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			String currentMoment = sdf.format(new Date());
			if (lastScheduledRunTime != null){
				String lastRunDate = lastScheduledRunTime.toString().substring(0, 10);
				if (currentMoment.compareTo(lastRunDate) == 0 && executeSetExecutePlan.getScheduledRunStatus() != 0) {
					//今天执行过了的，不要再执行了！
					continue;
				}
			}
			iScheduledTaskCount += processOneQueueListExecutePlan(executeSetExecutePlan);
		}
		
		return iScheduledTaskCount;
	}
	
	
	private static int traverseMonthlyPlans(ExecutePlan executePlan) {
		
		int iScheduledTaskCount = 0;
		
		//今天几号？
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		String currentWeekDay = sdf.format(new Date());
		
		//执行计划中所设定的执行日期
		String scheduleRunWeekDay =  executePlan.getScheduleRunWeekDay();
		//当前日期 <> 计划日期
		if (currentWeekDay.compareTo(scheduleRunWeekDay) != 0) {
			//不是指定的日期，无须执行
			return 0;
		}

		//当前钟点
		sdf = new SimpleDateFormat("HH:MM");
		String currentHour = sdf.format(new Date());

		//执行计划中所设定的执行钟点
		String scheduleRunHour =  executePlan.getScheduleRunHour();
		if (!scheduleRunHour.isEmpty()) {
			//当前钟点 > 计划钟点
			if (currentHour.compareTo(scheduleRunHour) < 0) {
				//钟点还未到，无须执行
				return 0;
			}
		}
		
		int iExecutePlanId = executePlan.getId();
		List<ExecuteSetExecutePlan> queueListExecutePlanList = DbGet.getExecuteSetExecutePlan(iExecutePlanId);
		//遍历其下所有的任务队列的执行计划表
		for (int i=0; i<queueListExecutePlanList.size(); i++) {
			ExecuteSetExecutePlan executeSetExecutePlan = queueListExecutePlanList.get(i);
			Date lastScheduledRunTime = executeSetExecutePlan.getBeginRunTime();
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			String currentMoment = sdf.format(new Date());
			if (lastScheduledRunTime != null){
				String lastRunDate = lastScheduledRunTime.toString().substring(0, 10);
				if (currentMoment.compareTo(lastRunDate) == 0 && executeSetExecutePlan.getScheduledRunStatus() != 0) {
					//今天执行过了的，不要再执行了！
					continue;
				}
			}
			iScheduledTaskCount += processOneQueueListExecutePlan(executeSetExecutePlan);
		}
		
		return iScheduledTaskCount;
	}
	
	
	//确定要执行的了（已经选定了一个特定的 任务队列）
	private static int processOneQueueListExecutePlan(ExecuteSetExecutePlan executeSetExecutePlan) {
	
		Executor executor = new Executor(m_sysType.get());
		executor.execute(executeSetExecutePlan);
		return 1;
	}
	
}
