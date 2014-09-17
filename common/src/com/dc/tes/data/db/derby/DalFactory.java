package com.dc.tes.data.db.derby;

import com.dc.tes.data.db.HibernateDALFactory;
import com.dc.tes.data.db.HibernateUtils;

/**
 * derby数据源接口工厂类
 * 
 * @author huangzx
 * 
 */
public class DalFactory extends HibernateDALFactory {
	static {
		HibernateUtils.Init("com/dc/tes/data/db/derby/hibernate.cfg.xml");
	}
}
