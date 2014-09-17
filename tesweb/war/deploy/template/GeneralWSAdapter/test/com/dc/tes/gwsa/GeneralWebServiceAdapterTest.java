package com.dc.tes.gwsa;

import java.io.IOException;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class GeneralWebServiceAdapterTest extends TestCase {

	private ServletRunner sr;
	private ServletUnitClient sc;
	
	public void setUp() {
		// 创建Servlet的运行环境
		sr = new ServletRunner();

		// 向环境中注册Servlet
		sr.registerServlet("GeneralWebServiceAdapter",
				GeneralWebServiceAdapter.class.getName());

		// 创建访问Servlet的客户端
		sc = sr.newClient();
	}

	public void tearDown() {

	}

	public void testDoPost() {

		//WebRequest request = new GetMethodWebRequest(
		//		"http://localhost/GeneralWSAdapter/servlet/GeneralWebServiceAdapter");
		
		WebRequest request = new GetMethodWebRequest(
		"http://localhost/GeneralWebServiceAdapter");
		
		request.setParameter("name", "11111111");
		request.setParameter("value", "1232");
	
		try {

		 InvocationContext ic = sc.newInvocation(request);
			WebResponse wr = sc.getResource(request);
			String s = wr.getText();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}

	}

}
