package com.dc.tes.gta;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TcpServer4AdapterTest extends TestCase {
	private TcpServer4Adapter  t4a = null;
	
	/**
	 * 初始化
	 */
	public void setUp(){
		try {
			System.setProperty("testfilename", "./testdata/testdata");
			this.t4a = new TcpServer4Adapter();
		} catch (Exception e) {
			Assert.fail();
		}
	}

	/**
	 * 清理，其实俺啥都不用干
	 */
	public void tearDown(){

	}

	/**
	 * 测试配置初始化函数
	 */
	public void testInit_01(){
		Assert.assertEquals("192.168.99.102", this.t4a.tpi.ip);
		Assert.assertEquals(7777, this.t4a.tpi.port);
		Assert.assertEquals(0, this.t4a.tpi.islast);
		Assert.assertEquals(0, this.t4a.tpi.isfix);
		Assert.assertEquals(10, this.t4a.tpi.len4len);
		Assert.assertEquals(0, this.t4a.tpi.lenstart);
		Assert.assertEquals(10, this.t4a.tpi.lenlen);
		Assert.assertEquals(1, this.t4a.tpi.needback);
		Assert.assertEquals(0, this.t4a.tpi.fixback);
	}
}
