package com.dc.tes.data.db.mysql;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.db.HibernateDALFactory;
import com.dc.tes.data.db.HibernateUtils;
import com.dc.tes.data.model.SysType;

/**
 * mysql数据源接口工厂类
 * 
 * @author huangzx
 * 
 */
public class DalFactory extends HibernateDALFactory {
	private static Log logger = LogFactory.getLog(DalFactory.class);

	static {
		HibernateUtils.Init("com/dc/tes/data/db/mysql/hibernate.cfg.xml");

		// 心跳查询 5分钟起跳 半小时一跳 保证数据库连接上有数据 免得被掐掉
		new Timer(true).schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					DALFactory.GetBeanDAL(SysType.class).Count();
					logger.info("数据库连接心跳测试...");
				} catch (Exception ex) {
					// 如果心跳时发生异常 直接扣掉
					this.cancel();
				}
			}
		}, 1000 * 60 * 5, 1000 * 60 * 30);
	}
}
