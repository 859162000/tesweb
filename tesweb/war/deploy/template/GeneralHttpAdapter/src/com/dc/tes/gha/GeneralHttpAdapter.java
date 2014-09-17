package com.dc.tes.gha;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapterapi.Adapter2Tes;
import java.util.Properties;

/**
 * ͨ��HTTP������
 * 
 * @author Conan
 * 
 * 
 * �������ݣ� 
 *     1�����ղ����Ľ� 
 *        ԭ�а汾����:ԭ�а汾ֻ�ܽ���GET��POST��ʽ�ĵ�һ�������������д���
 *        �Ľ����ݣ�
 *            1)����û�����û�в��������쳣����¼��־
 *            2)����û��������ҽ���1����������ֱ�Ӵ���
 *            3)����û������������1���������ȫ�ֱ�����ã�����ȫ�ֱ������õĲ���;
 *              ��ȫ�ֱ�������õĲ���δ�ҵ�����Ĭ�϶�ȡ��һ�����������ݴ���
 *              
 * @modify ������
 */

public class GeneralHttpAdapter extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * ���ĵĲ�������
	 * ���� HTTP GET ������
	 *      http://127.0.0.1:8080/axisTest/HelloWord.jws?method=sayHello
	 *      ���е�  parameter Ϊ: method
	 */
	private String parameter = "";

	/**
	 * ��־����
	 */
	public static Log log = LogFactory.getLog(GeneralHttpAdapter.class);

	public String encoding = null;

	/**
	 * ���캯��
	 */
	public GeneralHttpAdapter() {
		super();
	}

	/**
	 * ��������
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * ����HTTP GET����ķ���
	 * 
	 * Ŀǰ�÷�����Ȼ��˳�����ն������GET���� ���ǣ�ֻ��ȡ��һ���������͸����Ľ��д���
	 * 
	 * @param request
	 *            �ͻ��˷��͵�����
	 * @param response
	 *            ���ظ��ͻ��˵Ľ��
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		long inTime = System.currentTimeMillis(); // �ӵ������ʱ��
		long outTime = -1; // ���Ĵ�����ɵ�ʱ��
		long usedTime = 0; // ���Ĵ���ʱ��
		int delayTime = -1; // ϵͳ���ص���ʱʱ��

		//*******************************��� ��ʼ****************************
		
		// 1)����û�����û�в��������쳣����¼��־
		Enumeration enu = request.getParameterNames();
		if (false == enu.hasMoreElements()) {
			log.error("0x0800��GET����δ�����뱨�ģ�");
			throw new ServletException("0x0800��GET����δ�����뱨�ģ�");
		}
		
		// 2)����û��������ҽ���1����������ֱ�Ӵ���
		String pName = "";
		int count = this.getEnuNumber(enu);
		if (count ==1){
			enu = request.getParameterNames();
			pName = (String) enu.nextElement();
		}else if(count > 1){
			// 3)����û������������1���������ȫ�ֱ�����ã�����ȫ�ֱ������õĲ���;
			//             ��ȫ�ֱ�������õĲ���δ�ҵ�����Ĭ�϶�ȡ��һ�����������ݴ���
			enu = request.getParameterNames();
			if ( this.judgeEnuContent(enu, parameter)){
				pName = parameter;
			}else{	// δ�ҵ�  ȫ�ֱ������ò���,ֱ�Ӷ�ȡ��һ������
				enu = request.getParameterNames();
				pName = (String) enu.nextElement();
			}
		}
		
		if ("".equals(pName)){
			log.error("��������쳣");
			throw new ServletException("��������쳣");
		}
		log.info("�������Ϊ:" + pName);
		//*******************************��� ����****************************		

		byte[] reqMessage = request.getParameter(pName).getBytes();
		

		byte[] resMessage = null;
		try {
			resMessage = new Adapter2Tes().SendToCore(reqMessage);
		} catch (Exception e) {
			log.error("0x0800������Ľ���ʧ�ܣ�[" + e.getMessage() + "]");
			throw new ServletException("0x0800������Ľ���ʧ�ܣ�[" + e.getMessage()
					+ "]");
		}

		delayTime = 0; //��ʱ��������ʱ

		// ������ʱ
		outTime = System.currentTimeMillis();
		usedTime = outTime - inTime;
		log.debug("���ĵĴ���ʱ�䣺[" + usedTime + "]");
		try {
			if (usedTime < delayTime) {
				log.debug("������ʱ����[" + (delayTime - usedTime) + "]" + delayTime
						+ "-" + usedTime);
				Thread.sleep(delayTime - usedTime);
			}
		} catch (InterruptedException e) {
			log.error("0x0803����������ʱ����ʧ�ܣ�[" + e.getMessage() + "]");
			throw new ServletException("0x0803����������ʱ����ʧ�ܣ�[" + e.getMessage()
					+ "]");
		}
		// ����������Ӧ
		response.getOutputStream().write(resMessage);
	}

	/**
	 * ����HTTP POST����ķ��� Ŀǰ��ʵ����ɶ�����ǵ����ĵ���doGet����
	 * 
	 * @param request
	 *            �ͻ��˷��͵�����
	 * @param response
	 *            ���ظ��ͻ��˵Ľ��
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * ��ʼ�������� �������ĵ�ע��
	 * 
	 * @throws ServletException
	 *             ���ע��ʧ��
	 */
	public void init() throws ServletException {
		
		byte[] config = null;
		try {
			config = new Adapter2Tes().Reg2TES();
		} catch (Exception e) {
			log.error("0x0D17�������ע��ʧ�ܣ�[" + e.getMessage() + "]");
			throw new ServletException("0x0D17�������ע��ʧ�ܣ�[" + e.getMessage()
					+ "]");
		}
		
		InputStream inputStream = new ByteArrayInputStream(config);
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e) {
			log.error("0x0802������������Ϣʧ�ܣ�[" + e.getMessage() + "]");
			throw new ServletException("0x0802������������Ϣʧ�ܣ�[" + e.getMessage()
					+ "]");
		}
	}
	
	/**
	 * ��ȡ ö��Ԫ�ظ���
	 * @param enu  ö��
	 * @return ö��Ԫ�ظ���
	 */
	private int getEnuNumber(Enumeration enu){
		int count = 0;
		
		// enu ����û�� Ԫ��
		if (enu.hasMoreElements() == false)
			return count;
		
		// ѭ����ȡ enu ��Ԫ�ظ���
		while(enu.hasMoreElements()){
			enu.nextElement();
			count ++;
		}
		
		return count;
	}
	
	/**
	 * �ж� �趨�Ĳ��������� ö�� �б����Ƿ����
	 * @param enu ö��
	 * @param value ���жϵ�Ԫ��ֵ
	 * @return false����������;true�����ɹ�
	 */
	private boolean judgeEnuContent(Enumeration enu, String value){
		boolean result = false;
		
		while(enu.hasMoreElements()){
			String tempStr = (String) enu.nextElement();
			if (tempStr.equals(value)){
				result = true;
				break;
			}else{
				continue;
			}
		}
		
		return result;
	}
	
}



