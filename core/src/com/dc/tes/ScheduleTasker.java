package com.dc.tes;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.HisExecuteLog;
import com.dc.tes.data.op.Op;


public class ScheduleTasker {
	
	private int corePoolSize = 1;
	private ScheduledThreadPoolExecutor scheduler;

	public ScheduleTasker() {		
	    scheduler = new ScheduledThreadPoolExecutor(corePoolSize);
	}

	public ScheduleTasker(int quantity) {
	    corePoolSize = quantity;
	    scheduler = new ScheduledThreadPoolExecutor(corePoolSize);
	}

	public void schedule(Runnable command, long delay) {
	    scheduler.schedule(command, delay, TimeUnit.SECONDS);
	}
	
	public void scheduleAtFixedRate(Runnable command,long initialDelay,long period) {
		scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.SECONDS);
	}
		
	public void Shutdown() {
	    scheduler.shutdown();
	}
	
	public void Start() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String now = sdf.format(new Date());
		long diff =  (sdf.parse("09:10:00").getTime() - sdf.parse(now).getTime())/1000;
		
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				  IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
				  IDAL<HisExecuteLog> hisExecuteLogDAL = DALFactory.GetBeanDAL(HisExecuteLog.class);
				  
				  List<ExecuteLog> all = executeLogDAL.ListAll(Op.NE("id", -1));
				  
				  for(int i=all.size()-1; i>-1; i--) {
					  ExecuteLog log = all.get(i);
					  HisExecuteLog hisLog = new HisExecuteLog();
					  hisLog.setCreateTime(log.getCreateTime());
					  hisLog.setDescription(log.getDescription());
					  hisLog.setExecuteBatchNo(log.getExecuteBatchNo());
					  hisLog.setExecuteSetId(log.getExecuteSetId());
					  hisLog.setSystemId(log.getSystemId());
					  hisLog.setUserId(log.getUserId());
					  hisExecuteLogDAL.Add(hisLog);
				  }
			}
			
		},diff,24*3600,TimeUnit.SECONDS);
	}

	     
}
