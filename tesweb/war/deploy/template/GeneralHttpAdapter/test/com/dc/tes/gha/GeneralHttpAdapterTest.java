package com.dc.tes.gha;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

import com.dc.tes.adapterapi.Adapter2Tes;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 通用HTTP适配器
 * @author Conan
 *
 */
public class GeneralHttpAdapterTest extends TestCase {
	/**
	 * Servlet运行环境
	 */
	private ServletRunner sr;
	
	/**
	 * Servlet访问客户端
	 */
	private ServletUnitClient sc;
	
	/**
	 * 初始化
	 */
	public void setUp(){
		//创建Servlet的运行环境
		this.sr = new ServletRunner();
		//向环境中注册Servlet
		sr.registerServlet( "GeneralHttpAdapter", GeneralHttpAdapter.class.getName());
		//创建访问Servlet的客户端
		this.sc = sr.newClient();
	}

	/**
	 * 清理，其实俺啥都不用干
	 */
	public void tearDown(){

	}

	/**
	 * 测试GET方法的响应：
	 * 验证消息体总长度，应为30位消息头加上默认的消息体初始化大小1024
	 * 验证消息头总长度信息，应该与消息体总长度相等
	 * 验证消息头的已用长度信息，应该为消息体的长度
	 */
	public void testDoGet_01(){
		//设置环境变量
		System.setProperty("tesAddr", "//127.0.0.1:9099");
		System.setProperty("adapterConfig", "./testdata/testdata");
		System.setProperty("channelName", "testChannel");
		/*
		//启动一个TCP SERVER
		ServerSocket ssk = null;
		byte[] backMessage = null;	//初始化返回消息体
		String realResmessage = "ONLY ME!!!";
		try{
			backMessage = Adapter2Tes.messageInit();
			backMessage = Adapter2Tes.addContent(backMessage, "RESMESSAGE", realResmessage.getBytes());
			backMessage = Adapter2Tes.addContent(backMessage, "DELAYTIME", (100+"").getBytes());
			ssk = new ServerSocket();
			ssk.setReuseAddress(true);
			ssk.bind(new InetSocketAddress("127.0.0.1",9099), 10);
			Thread TcpServerThread = new Thread(new TcpServerStub(ssk, ));
			TcpServerThread.start();
		}catch(Exception e){
			//e.printStackTrace();
			Assert.fail();
		}
		
		//发送请求
		String reqmessage = "I'm XML~~";
		WebRequest request   = new GetMethodWebRequest("http://localhost/GeneralHttpAdapter?xml="+reqmessage);

		//获得模拟服务器的信息
		WebResponse response;
		String resmessage = null;
		try {
			response = sc.getResponse(request);
			resmessage = response.getText();
		} catch (Exception e) {
			//e.printStackTrace();
			Assert.fail();
		}
		
		//验证返回消息
		Assert.assertEquals(realResmessage, resmessage);
		*/
	}
}
