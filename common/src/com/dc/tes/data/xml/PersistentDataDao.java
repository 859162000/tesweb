package com.dc.tes.data.xml;

import com.dc.tes.data.model.PersistentData;

/**
 * 对PersistentData进行CRUD的dao类
 * 
 * @author huangzx
 * 
 */
public class PersistentDataDao extends BaseDao<PersistentData> {
	public PersistentDataDao() {
		super("id", "parameter", "systemid", false, "systemid", "parameter", "curvalue", "type");
	}
}
