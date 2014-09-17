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
 * ͨ��HTTP������
 * @author Conan
 *
 */
public class GeneralHttpAdapterTest extends TestCase {
	/**
	 * Servlet���л���
	 */
	private ServletRunner sr;
	
	/**
	 * Servlet���ʿͻ���
	 */
	private ServletUnitClient sc;
	
	/**
	 * ��ʼ��
	 */
	public void setUp(){
		//����Servlet�����л���
		this.sr = new ServletRunner();
		//�򻷾���ע��Servlet
		sr.registerServlet( "GeneralHttpAdapter", GeneralHttpAdapter.class.getName());
		//��������Servlet�Ŀͻ���
		this.sc = sr.newClient();
	}

	/**
	 * ������ʵ��ɶ�����ø�
	 */
	public void tearDown(){

	}

	/**
	 * ����GET��������Ӧ��
	 * ��֤��Ϣ���ܳ��ȣ�ӦΪ30λ��Ϣͷ����Ĭ�ϵ���Ϣ���ʼ����С1024
	 * ��֤��Ϣͷ�ܳ�����Ϣ��Ӧ������Ϣ���ܳ������
	 * ��֤��Ϣͷ�����ó�����Ϣ��Ӧ��Ϊ��Ϣ��ĳ���
	 */
	public void testDoGet_01(){
		//���û�������
		System.setProperty("tesAddr", "//127.0.0.1:9099");
		System.setProperty("adapterConfig", "./testdata/testdata");
		System.setProperty("channelName", "testChannel");
		/*
		//����һ��TCP SERVER
		ServerSocket ssk = null;
		byte[] backMessage = null;	//��ʼ��������Ϣ��
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
		
		//��������
		String reqmessage = "I'm XML~~";
		WebRequest request   = new GetMethodWebRequest("http://localhost/GeneralHttpAdapter?xml="+reqmessage);

		//���ģ�����������Ϣ
		WebResponse response;
		String resmessage = null;
		try {
			response = sc.getResponse(request);
			resmessage = response.getText();
		} catch (Exception e) {
			//e.printStackTrace();
			Assert.fail();
		}
		
		//��֤������Ϣ
		Assert.assertEquals(realResmessage, resmessage);
		*/
	}
}
