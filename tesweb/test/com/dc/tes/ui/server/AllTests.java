package com.dc.tes.ui.server;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.dc.tes.ui.server");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestCaseService.class);
		suite.addTestSuite(TestUserService.class);
		suite.addTestSuite(TestTransactionService.class);
		suite.addTestSuite(TestSimuSystemService.class);
		//$JUnit-END$
		return suite;
	}
	
	public static void main(String[] args){

		junit.textui.TestRunner.run(suite());
		//以下2种图形界面方式    有未知错误,请参用上面的 文本方式 做集成测试
		//junit.awtui.TestRunner.run(TestSysType.class);
		//junit.swingui.TestRunner.run(TestSysType.class);
	}

}
