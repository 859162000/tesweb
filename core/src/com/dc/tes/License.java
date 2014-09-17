package com.dc.tes;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import sun.misc.BASE64Decoder;

import com.dc.tes.channel.IAdapterChannel;
import com.dc.tes.channel.IChannel;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.exception.LicenseException;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;
import com.dc.tes.net.*;

/**
 * 核心License验证
 * 
 * @author lijic
 * 
 */
class License {
	/**
	 * ADAPTER列表长度 该数值表示目前支持的适配器的个数
	 */
	private static final int C_ADAPTER_LIST_SIZE = 128;
	private static  String Sys_Name;
	private static final Log log = LogFactory.getLog(License.class);
	private static String  LICENSE_SERVER_IP;
	private static int  LICENSE_SERVER_PORT;
	private static int CORE_PORT;
	/**
	 * 对License进行校验
	 * 
	 * @param core
	 *            核心对象
	 * @return License验证警告信息
	 */
	static String CheckLicense(Core core) {
		String license = RuntimeUtils.ReadResource("license.dat", RuntimeUtils.utf8);
		return Lisence(core,license);
	}

	static String CheckLicense_withServer(Core core){
		
		Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
		LICENSE_SERVER_IP = XmlUtils.SelectNodeText(doc, "//config/LICENSE_SERVER_IP");
		LICENSE_SERVER_PORT = Integer.parseInt(XmlUtils.SelectNodeText(doc, "//config/LICENSE_SERVER_PORT"));
		CORE_PORT = Integer.parseInt(XmlUtils.SelectNodeText(doc, "//config/port"));
		
		Sys_Name = core.instanceName;
		Message request = new Message(MessageType.LICENSE);
		request.put(MessageItem.LICENSE.SIGN, 0);
		request.put(MessageItem.LICENSE.PORT, CORE_PORT);
		request.put(MessageItem.LICENSE.SYSNAME,  core.instanceName);

		System.out.println("数据"+request);
		String license = LicenseServerConnect(request);
//		license = LicenseServerConnect(request);/
		HeadBeat();
		return Lisence(core,license);
	}
	
	private static String LicenseServerConnect(Message request){
		String license = "";
		Socket client=null;
		try {
			client=new Socket(LICENSE_SERVER_IP,LICENSE_SERVER_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			license = "无法链接到Lisence服务器";
			log.error(license);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			license = "和Lisence服务器通讯异常";
			log.error(license);
		}
		
		try {
			client.getOutputStream().write(request.Export());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			license = "向Lisence服务器请求数据出错";
			log.error(license);
		}
		Message response = null;
		try {
			response = new Message(client.getInputStream());	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			license = "读取Lisence服务器的返回信息出错";
			log.error(license);
		}
		if(response.getInteger(MessageItem.LICENSE.AVAILABLE) == 0){
			license = "由于License服务限制，该核心无法启动，"+response.getString(MessageItem.LICENSE.MSG);
			log.error(license);
		}
		if(request.getInteger(MessageItem.LICENSE.SIGN) ==0 ){
			license = response.getString(MessageItem.LICENSE.LICENSEFILE);
		}
		return license;
	}

	private static void HeadBeat(){
		
		new Timer(true).schedule(new TimerTask(){
			
			@Override
			public void run() {
				Message request = new Message(MessageType.LICENSE);
				request.put(MessageItem.LICENSE.SIGN, 1);
				request.put(MessageItem.LICENSE.PORT, CORE_PORT);
				request.put(MessageItem.LICENSE.SYSNAME,  Sys_Name);

				System.out.println("心跳"+request);
				String license = LicenseServerConnect(request);
				if(license.length()>0){
					log.error("lisence服务无效,导致核心关闭");
					System.exit(-1);
				}
			}
		
		},500,60*1000);
		
	}
	
	private static String Lisence(Core core,String license){
		// 适配器启用标志
		boolean[] adapterFlag = new boolean[C_ADAPTER_LIST_SIZE];
		// 适配器截止日期
		Date[] adapterDate = new Date[C_ADAPTER_LIST_SIZE];
		// 核心截止日期
		Date tesDate;
		// 适配器数量
		int adapterNum;

		try {
			// 读取License文件
			//String license = RuntimeUtils.ReadResource("license.dat", RuntimeUtils.utf8);
			byte[] _enData = new BASE64Decoder().decodeBuffer(license);

			byte[] _buffer = new byte[_enData.length - 8];
			System.arraycopy(_enData, 0, _buffer, 0, _enData.length - 8);

			license = new String(decrypt(_buffer, "nuclearg".getBytes()));

			String[] segments = StringUtils.split(license, "\r");
			for (int i = 0; i < C_ADAPTER_LIST_SIZE; i++)
				adapterFlag[i] = segments[0].charAt(i) == '1';

			String[] _adapterDate = segments[1].split("\\|");
			for (int i = 0; i < C_ADAPTER_LIST_SIZE; i++)
				if (adapterFlag[i])
					adapterDate[i] = _adapterDate[i + 1].equals("00000000") ? null : new SimpleDateFormat("yyyyMMdd").parse(_adapterDate[i + 1]);

			tesDate = segments[2].equals("00000000") ? null : new SimpleDateFormat("yyyyMMdd").parse(segments[2]);

			adapterNum = Integer.parseInt(segments[3]);
		} catch (Exception ex) {
			throw new LicenseException("读取License文件失败", ex);
		}

		if (tesDate != null && new Date().after(tesDate))
			throw new LicenseException("已超过截止日期");

		int count = 0;
		StringBuffer buffer = new StringBuffer();

		List<String> disabledChannelNames = new ArrayList<String>();
		for (String name : core.channels.getChannelNames())
			if (core.channels.getChannel(name) instanceof IAdapterChannel) {
				count++;
				Class<? extends IChannel> cls = core.channels.getChannel(name).getClass();

				String pName = core.channels.getChannel(name).getClass().getPackage().getName();
				pName = pName.substring(pName.lastIndexOf('.') + 1);
				int id = pName.hashCode() & 0x7fffffff % 64;
				if (ArrayUtils.contains(cls.getInterfaces(), IListenerChannel.class))
					id += 64;

				if (!adapterFlag[id] || (adapterDate[id] != null && new Date().after(adapterDate[id])))
					disabledChannelNames.add(name);
			}

		if (adapterNum > 0 && count > adapterNum)
			throw new LicenseException("适配器数量超出license限制");

		for (String disabledChannel : disabledChannelNames) {
			buffer.append("因为超过License限制，" + disabledChannel + "通道被禁用").append(SystemUtils.LINE_SEPARATOR);
			core.channels.getChannelNames().remove(disabledChannel);
		}

		return buffer.toString();
	}
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

		// DES算法要求有一个可信任的随机数源

		SecureRandom sr = new SecureRandom();

		// 从原始密匙数据建立一个DESKeySpec对象

		DESKeySpec dks = new DESKeySpec(key);

		// 建立一个密匙工厂，然后用它把DESKeySpec对象转换成

		// 一个SecretKey对象

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作

		Cipher cipher = Cipher.getInstance("DES");

		// 用密匙原始化Cipher对象

		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		// 现在，获取数据并解密

		// 正式执行解密操作

		return cipher.doFinal(src);

	}
}
