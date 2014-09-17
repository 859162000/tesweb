package com.dc.tes.data.xml;

import java.io.File;

import com.dc.tes.data.model.SysType;
import com.dc.tes.util.RuntimeUtils;

/**
 * 对SysType类进行CRUD的dao类
 * 
 * @author huangzx
 * 
 */
public class SysTypeDao extends BaseDao<SysType> {
	private static final String C_EMPTY_TRANS_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><transactions></transactions>";

	public SysTypeDao() {
		super("systemId", "systemName", null, true, "systemNo", "systemName", "desc", "flag");
	}

	@Override
	protected void prepareDir(File dir, SysType bean) {
		File trans = new File(dir + File.separator + "transactions.xml");
		RuntimeUtils.WriteFile(trans, C_EMPTY_TRANS_XML, RuntimeUtils.utf8);
	}
}
