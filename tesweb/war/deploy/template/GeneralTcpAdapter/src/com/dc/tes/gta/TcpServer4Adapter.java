package com.dc.tes.gta;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.plugins.AdapterPlugins;
import com.dc.tes.adapterEndapi.AdapterEndServer;
import com.dc.tes.adapterapi.Adapter2Tes;

/**
 * TCP SERVER类
 * @author Conan
 *
 */
public class TcpServer4Adapter {
	/**
	 * 日志对象
	 */
	public static Log log = LogFactory.getLog(TcpServer4Adapter.class);

	/**
	 * TCP属性信息
	 */
	protected TcpProperty tpi = null;

	/**
	 * 服务端模拟监听套接字
	 */
	private ServerSocket ssk;

	/**
	 * 客户端模拟监听套接字
	 */
	private Socket sk;

	/**
	 * 客户端模拟输出流
	 */
	private OutputStream o = null;

	/**
	 * 客户端模拟输入流
	 */
	InputStream i = null;

	/**
	 * 运行标志
	 */
	private boolean runflag;

	/**
	 * 适配器端服务器
	 */
	private AdapterEndServer aes = null;

	/**
	 * 构造函数
	 * @throws Exception
	 */
	public TcpServer4Adapter() throws Exception{
		this.tpi = this.init();
		this.runflag = true;
	}

	/**
	 * 启动适配器
	 * @throws Exception
	 */
	public void start() throws Exception{
		if (this.tpi.simtype.equalsIgnoreCase("S")){
			log.debug("启动服务端模拟。");
			this.startSsim();
		}
		else if (this.tpi.simtype.equalsIgnoreCase("C")){
			log.debug("启动客户端模拟。");
			this.startCsim();
		}
	}

	/**
	 * 停止运行
	 */
	public void killme(){
		this.runflag = false;
	}

	/**
	 * 向核心进行注册获得配置信息
	 * @return
	 */
	protected TcpProperty init() throws Exception{
		String tempStr = System.getProperty("adapterAddr");
		TcpProperty tp = new TcpProperty();
		if (null == tempStr){
			tp.simtype = "S";
			log.info("启动TCP接收端适配器……");
		}
		else {
			tp.simtype = "C";
			log.info("启动TCP发起端适配器……");
		}
		

		Adapter2Tes adp2TES = new Adapter2Tes();
		byte[] config = null;		//配置信息
		try {
			config = adp2TES.reg2tes();
		} catch (Exception e) {
			log.error("0x0D17：向核心注册失败！["+e.getMessage()+"]");
			throw new Exception("0x0D17：向核心注册失败！["+e.getMessage()+"]");
		}

		log.debug("注册完成，根据配置信息进行初始化：");
		InputStream inputStream = new ByteArrayInputStream(config);
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e) {
			log.error("0x0802：加载配置信息失败！["+e.getMessage()+"]");
			throw new Exception("0x0802：加载配置信息失败！["+e.getMessage()+"]");
		}


		if ((tp.ip = p.getProperty("IP")) == null) {
			log.error("0x0802：读取配置[IP]失败！");
			throw new Exception("0x0802：读取配置[IP]失败！");
		}
		log.debug("IP："+tp.ip);

		if ((tempStr= p.getProperty("PORT")) == null) {
			log.error("0x0802：读取配置[PORT]失败！");
			throw new Exception("0x0802：读取配置[PORT]失败！");
		} 
		try {
			tp.port = Integer.parseInt(tempStr);
		}catch(NumberFormatException e){
			log.error("0x0802：配置[PORT]的值设置非法！["+e.getMessage()+"]");
			throw new Exception("0x0802：配置[PORT]的值设置非法！["+e.getMessage()+"]");
		}
		log.debug("PORT："+tp.port);

		if (tp.simtype.equalsIgnoreCase("C")){
			if ((tempStr= p.getProperty("GETPORTFIRST")) == null) {
				log.error("0x0802：读取配置[GETPORTFIRST]失败！");
				throw new Exception("0x0802：读取配置[GETPORTFIRST]失败！");
			} 
			try {
				tp.getportfirst = Integer.parseInt(tempStr);
			}catch(NumberFormatException e){
				log.error("0x0802：配置[GETPORTFIRST]的值设置非法！["+e.getMessage()+"]");
				throw new Exception("0x0802：配置[GETPORTFIRST]的值设置非法！["+e.getMessage()+"]");
			}
			log.debug("GETPORTFIRST："+tp.getportfirst);
		}

		if ((tempStr= p.getProperty("ISLAST")) == null) {
			log.error("0x0802：读取配置[ISLAST]失败！");
			throw new Exception("0x0802：读取配置[ISLAST]失败！");
		} 
		try {
			tp.islast = Integer.parseInt(tempStr);
		}catch(NumberFormatException e){
			log.error("0x0802：配置[ISLAST]的值设置非法！["+e.getMessage()+"]");
			throw new Exception("0x0802：配置[ISLAST]的值设置非法！["+e.getMessage()+"]");
		}
		log.debug("ISLAST："+tp.islast);

		if ((tempStr= p.getProperty("ISFIX")) == null) {
			log.error("0x0802：读取配置[ISFIX]失败！");
			throw new Exception("0x0802：读取配置[ISFIX]失败！");
		} 
		try {
			tp.isfix = Integer.parseInt(tempStr);
		}catch(NumberFormatException e){
			log.error("0x0802：配置[ISFIX]的值设置非法！["+e.getMessage()+"]");
			throw new Exception("0x0802：配置[ISFIX]的值设置非法！["+e.getMessage()+"]");
		}
		log.debug("ISFIX："+tp.isfix);

		if (tp.isfix <= 0){
			if ((tempStr= p.getProperty("LEN4LEN")) == null) {
				log.error("0x0802：读取配置[LEN4LEN]失败！");
				throw new Exception("0x0802：读取配置[LEN4LEN]失败！");
			} 
			try {
				tp.len4len = Integer.parseInt(tempStr);
			}catch(NumberFormatException e){
				log.error("0x0802：配置[LEN4LEN]的值设置非法！["+e.getMessage()+"]");
				throw new Exception("0x0802：配置[LEN4LEN]的值设置非法！["+e.getMessage()+"]");
			}
			log.debug("LEN4LEN："+tp.len4len);
			if (tp.len4len <= 0){
				log.error("0x0802：配置[LEN4LEN]的值["+tp.len4len+"]设置非法！");
				throw new Exception("0x0802：配置[LEN4LEN]的值["+tp.len4len+"]设置非法！");
			}

			if ((tempStr= p.getProperty("LENSTART")) == null) {
				log.error("0x0802：读取配置[LENSTART]失败！");
				throw new Exception("0x0802：读取配置[LENSTART]失败！");
			} 
			try {
				tp.lenstart = Integer.parseInt(tempStr);
			}catch(NumberFormatException e){
				log.error("0x0802：配置[LENSTART]的值设置非法！["+e.getMessage()+"]");
				throw new Exception("0x0802：配置[LENSTART]的值设置非法！["+e.getMessage()+"]");
			}
			log.debug("LENSTART："+tp.lenstart);
			if (tp.lenstart < 0){
				log.error("0x0802：配置[LENSTART]的值["+tp.lenstart+"]设置非法！");
				throw new Exception("0x0802：配置[LENSTART]的值["+tp.lenstart+"]设置非法！");
			}

			if ((tempStr= p.getProperty("LENLEN")) == null) {
				log.error("0x0802：读取配置[LENLEN]失败！");
				throw new Exception("0x0802：读取配置[LENLEN]失败！");
			} 
			try {
				tp.lenlen = Integer.parseInt(tempStr);
			}catch(NumberFormatException e){
				log.error("0x0802：配置[LENLEN]的值设置非法！["+e.getMessage()+"]");
				throw new Exception("0x0802：配置[LENLEN]的值设置非法！["+e.getMessage()+"]");
			}
			log.debug("LENLEN："+tp.lenlen);
			if (tp.lenlen <= 0){
				log.error("0x0802：配置[LENLEN]的值["+tp.lenlen+"]设置非法！");
				throw new Exception("0x0802：配置[LENLEN]的值["+tp.lenlen+"]设置非法！");
			}
		}

		if ((tempStr= p.getProperty("NEED2CORE")) == null) {
			log.error("0x0802：读取配置[NEED2CORE]失败！");
			throw new Exception("0x0802：读取配置[NEED2CORE]失败！");
		} 
		try {
			tp.need2core = Integer.parseInt(tempStr);
		}catch(NumberFormatException e){
			log.error("0x0802：配置[NEED2CORE]的值设置非法！["+e.getMessage()+"]");
			throw new Exception("0x0802：配置[NEED2CORE]的值设置非法！["+e.getMessage()+"]");
		}
		log.debug("NEED2CORE："+tp.need2core);

		if ((tempStr= p.getProperty("NEEDBACK")) == null) {
			log.error("0x0802：读取配置[NEEDBACK]失败！");
			throw new Exception("0x0802：读取配置[NEEDBACK]失败！");
		} 
		try {
			tp.needback = Integer.parseInt(tempStr);
		}catch(NumberFormatException e){
			log.error("0x0802：配置[NEEDBACK]的值设置非法！["+e.getMessage()+"]");
			throw new Exception("0x0802：配置[NEEDBACK]的值设置非法！["+e.getMessage()+"]");
		}
		log.debug("NEEDBACK："+tp.needback);

		if (tp.needback != 0){
			if ((tempStr= p.getProperty("FIXBACK")) == null) {
				log.error("0x0802：读取配置[FIXBACK]失败！");
				throw new Exception("0x0802：读取配置[FIXBACK]失败！");
			} 
			try {
				tp.fixback = Integer.parseInt(tempStr);
			}catch(NumberFormatException e){
				log.error("0x0802：配置[FIXBACK]的值设置非法！["+e.getMessage()+"]");
				throw new Exception("0x0802：配置[FIXBACK]的值设置非法！["+e.getMessage()+"]");
			}
			log.debug("FIXBACK："+tp.fixback);

			if (tp.fixback != 0){
				if ((tempStr= p.getProperty("FIXBACKPATH")) == null) {
					log.error("0x0802：读取配置[FIXBACKPATH]失败！");
					throw new Exception("0x0802：读取配置[FIXBACKPATH]失败！");
				} 
				try {
					tp.fixbackpath = tempStr;
					BufferedReader br = new BufferedReader (new FileReader(new File(tp.fixbackpath)));

					StringBuilder sb = new StringBuilder();
					String line =  "";
					while(null != (line = br.readLine())){
						sb.append(line);
					}
					tp.backmessage = sb.toString().getBytes();
				}catch(Exception e){
					log.error("0x0802：配置[FIXBACKPATH]的值设置非法！["+e.getMessage()+"]");
					throw new Exception("0x0802：配置[FIXBACKPATH]的值设置非法！["+e.getMessage()+"]");
				}
				log.debug("FIXBACKPATH："+tp.fixbackpath);
				log.debug("BACKMESSAGE："+new String(tp.backmessage));
			}
		}

		return tp;
	}

	/**
	 * 启动客户端模拟适配器
	 * @throws Exception
	 */
	private void startCsim() throws Exception{	
		//先获取端口号信息
		if (1==this.tpi.getportfirst){
			byte[] portMessge = 
				this.sendMessage(this.tpi.ip, this.tpi.port, this.tpi.backmessage);
			this.parseAddr(AdapterPlugins.getCommAddr(portMessge));
			this.closeCTcpLink(this.sk);
		}
		
		//启动适配器端服务器
		try {
			aes = new AdapterEndServer();
			aes.startServer();
		} catch (Exception e) {
			log.error(e.getMessage());
			this.closeCTcpLink(this.sk);
			throw new Exception(e.getMessage());
		}
		
		//进入交易处理
		this.clientTran();
	}

	/**
	 * 启动服务端模拟适配器
	 * @throws Exception
	 */
	private void startSsim() throws Exception{
		try {
			//绑定套接字
			this.ssk = new ServerSocket();
			this.ssk.setReuseAddress(true);
			this.ssk.bind(new InetSocketAddress(this.tpi.ip,this.tpi.port), 10);
		} catch (SocketException e) {
			log.error("0x0D23：设置套接字重用失败！["+e.getMessage()+"]");
			this.closeSTcpLink(this.ssk);
			throw new Exception("0x0D23：设置套接字重用失败！["+e.getMessage()+"]");
		} catch (IOException e) {
			log.error("0x0D24：绑定套接字失败！["+e.getMessage()+"]");
			this.closeSTcpLink(this.ssk);
			throw new Exception("0x0D24：绑定套接字失败！["+e.getMessage()+"]");
		} catch(SecurityException e){
			log.error("0x0D12：本地或远程安全策略阻止了套接字监听！["+e.getMessage()+"]");
			this.closeSTcpLink(this.ssk);
			throw new Exception("0x0D12：本地或远程安全策略阻止了套接字监听！["+e.getMessage()+"]");
		} catch(IllegalArgumentException e){
			log.error("0x0D13：绑定套接字时给定的参数非法！["+this.tpi.ip+"]["+this.tpi.port+"]["+e.getMessage()+"]");
			this.closeSTcpLink(this.ssk);
			throw new Exception("0x0D13：绑定套接字时给定的参数非法！["+this.tpi.ip+"]["+this.tpi.port+"]["+e.getMessage()+"]");
		}

		log.info("通用TCP适配器监听启动："+this.tpi.ip+":"+this.tpi.port);
		while (this.runflag) {
			Socket tempSocket = null;
			try {
				tempSocket = this.ssk.accept();
				tempSocket.setTcpNoDelay(true);
				tempSocket.setKeepAlive(true);
				InputStream ips = tempSocket.getInputStream();
				OutputStream out = tempSocket.getOutputStream();
				Thread serverHandlerThread = new Thread(new RecvHandler(tempSocket, this.tpi, ips, out));
				serverHandlerThread.start();
			} catch (SocketException e){
				log.error("0x0D23：设置客户端连接套接字属性异常！["+e.getMessage()+"]");
				this.closeCTcpLink(tempSocket);
				throw new Exception("0x0D23：设置客户端连接套接字属性异常！["+e.getMessage()+"]");
			} catch(SocketTimeoutException e){
				log.error("0x0D26：套接字连接超时！["+e.getMessage()+"]");
				this.closeCTcpLink(tempSocket);
				throw new Exception("0x0D26：套接字连接超时！["+e.getMessage()+"]");
			} catch (IOException e) {
				log.error("0x0D25：接收客户端连接请求出错！["+e.getMessage()+"]");
				this.closeCTcpLink(tempSocket);
				throw new Exception("0x0D12：接收客户端连接请求出错！["+e.getMessage()+"]");
			} catch(SecurityException e){
				log.error("0x0D12：本地或远程安全策略阻止了套接字连接！["+e.getMessage()+"]");
				this.closeCTcpLink(tempSocket);
				throw new Exception("0x0D12：本地或远程安全策略阻止了套接字链接！["+e.getMessage()+"]");
			} catch(Exception e){
				log.error("0x0DFF：建立客户端连接时，出现未知错误！["+e.getMessage()+"]");
				this.closeCTcpLink(tempSocket);
				throw new Exception("0x0DFF：建立客户端连接时，出现未知错误！["+e.getMessage()+"]");
			}
		}
		this.closeSTcpLink(this.ssk);
	}

	/**
	 * 处理客户端模拟器交易
	 * @throws Exception
	 */
	private void clientTran() throws Exception{
		byte[] req = null;			//实际请求报文
		byte[] res = null;			//真实系统的返回报文
		byte[] resMessage = null;	//要返回给核心的消息体
		while(this.runflag){
			//等待核心请求消息
			try {
				req = aes.readMessage();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			//与被测系统进行交互
			res = this.sendMessage(this.tpi.ip, this.tpi.port, req);

			//给核心返回真实系统的返回报文
			try {
				aes.writeMessage(resMessage);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (this.tpi.islast != 0){
				//非长连接，断开连接
				this.closeCTcpLink(this.sk);
			}
		}

		//停止适配器端服务器
		try {
			aes.stopServer();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 从形如“//127.0.0.1:9999”的字符串中解析出IP端口信息
	 * @tempStr 待解析字符串
	 * @throws Exception
	 */
	private void parseAddr(String addr) throws Exception{
		String[] tempStr = null;
		try{
			tempStr = addr.split(":");
			this.tpi.ip = tempStr[0].substring(2);	//获取IP
			this.tpi.port = Integer.parseInt(tempStr[1]);	//获取端口
		}catch(PatternSyntaxException e){
			log.error("0x0D19：请使用冒号对地址["+addr+"]的IP与端口进行分割！["+e.getMessage()+"]");
			throw new Exception("0x0D19：请使用冒号对地址["+addr+"]的IP与端口进行分割！["+e.getMessage()+"]");
		}catch(IndexOutOfBoundsException e){
			log.error("0x0D06：从环境变量中获取IP地址["+addr+"]时出现异常！["+e.getMessage()+"]");
			throw new Exception("0x0D06：从环境变量中获取IP地址["+addr+"]时出现异常！["+e.getMessage()+"]");
		}catch(NumberFormatException e){
			log.error("0x0D09：请将通讯端口["+addr+"]设置为数字！["+e.getMessage()+"]");
			throw new Exception("0x0D09：请将通讯端口["+addr+"]设置为数字！["+e.getMessage()+"]");
		}
	}

	
	/**
	 * 向指定的地址发送并接收报文
	 * @param ip	IP地址
	 * @param port	通讯端口
	 * @param message	要发送的报文
	 * @return	接收到的报文
	 * @throws Exception
	 */
	private byte[] sendMessage(String ip, int port, byte[] message) throws Exception {
		
		log.info("接收到需要转发的数据：" + new String(message, "UTF-8"));
		
		
		if (true){//本行是专为测试发送端核心 s是交易码
			 File file=new File("msg.xml");

		        if(!file.exists()||file.isDirectory())

		            throw new FileNotFoundException();

		        BufferedReader br=new BufferedReader(new FileReader(file));

		        String temp=null;

		        StringBuffer sb=new StringBuffer();

		        temp=br.readLine();

		        while(temp!=null){

		            sb.append(temp+"\r\n");

		            temp=br.readLine();

		        }
		        log.debug("应答消息：" + sb);
			return sb.toString().getBytes();//本行是专为测试发送端核心 s是交易码
		}//本行是专为测试发送端核心 s是交易码
		else//本行是专为测试发送端核心 s是交易码
		if (null == this.sk){
			//初始化连接信息
			this.init4client();
		}

		byte[] backMessage = null;	//待返回的报文

		//发送报文
		try{
			this.o.write(message);
			this.o.flush();
		}catch(IOException e){
			log.error("0x0D22：报文发送过程出现异常！["+e.getMessage()+"]");
			this.closeCTcpLink(this.sk);
			throw new Exception("0x0D22：报文发送过程出现异常！["+e.getMessage()+"]");
		}

		//接收报文
		if (this.tpi.isfix>0){
			//定长报文处理流程
			try{
				backMessage = this.recv4len(this.tpi.isfix);
				log.debug("接收到的定长报文长度：["+backMessage.length+"]");
				log.debug("接收到的定长报文内容：["+(new String(backMessage))+"]");
			} catch(Exception e){
				log.error(e.getMessage());
				this.closeCTcpLink(this.sk);
				throw new Exception(e.getMessage());
			}
		} else {
			//变长报文处理流程
			byte[] buff = null;
			byte[] trans = new byte[this.tpi.lenlen];
			int lenlen = -1;	//报文中长度信息的长度
			try{
				//先收取长度信息
				buff = this.recv4len(this.tpi.len4len);
				System.arraycopy(buff, this.tpi.lenstart, trans, 0, this.tpi.lenlen);
				lenlen = Integer.parseInt((new String(trans)).trim());
				if (lenlen<this.tpi.len4len){
					log.error("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+this.tpi.len4len+"]");
					this.closeCTcpLink(this.sk);
					throw new Exception("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+this.tpi.len4len+"]");
				}
				backMessage = new byte[lenlen];	//返回消息体
				System.arraycopy(buff, 0, backMessage, 0, this.tpi.len4len);
			}catch(IndexOutOfBoundsException e){
				log.error("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
				this.closeCTcpLink(this.sk);
				throw new Exception("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
			}catch(ArrayStoreException e){
				log.error("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
				this.closeCTcpLink(this.sk);
				throw new Exception("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
			}catch(NullPointerException e){
				log.error("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
				this.closeCTcpLink(this.sk);
				throw new Exception("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
			}catch(NumberFormatException e){
				log.error("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
				this.closeCTcpLink(this.sk);
				throw new Exception("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
			} catch(Exception e){
				log.error(e.getMessage());
				this.closeCTcpLink(this.sk);
				throw new Exception(e.getMessage());
			}
			if (lenlen>this.tpi.len4len){
				//收取完整报文
				try{
					buff = this.recv4len(lenlen-this.tpi.len4len);
					System.arraycopy(buff, 0, backMessage,
							this.tpi.len4len, lenlen-this.tpi.len4len);
				} catch(IndexOutOfBoundsException e){
					log.error("0x0D06：获取报文信息时，数组写操作越界！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					throw new Exception("0x0D06：获取报文信息时，数组写操作越界！["+e.getMessage()+"]");
				}catch(ArrayStoreException e){
					log.error("0x0D07：获取报文信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					throw new Exception("0x0D07：获取报文信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
				}catch(NullPointerException e){
					log.error("0x0D08：获取报文信息时，操作的数组为空！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					throw new Exception("0x0D08：获取报文信息时，操作的数组为空！["+e.getMessage()+"]");
				}catch(NumberFormatException e){
					log.error("0x0D09：获取报文信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					throw new Exception("0x0D09：获取报文信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
				} catch(Exception e){
					log.error(e.getMessage());
					this.closeCTcpLink(this.sk);
					throw new Exception(e.getMessage());
				}
			}
		}
		log.debug("转发后的应答消息：" + backMessage);
		return backMessage;
	}

	
	/**
	 * 客户端模拟器的连接初始化
	 * @return
	 */
	private void init4client() throws Exception{
		//先初始化通讯信息
		String IPaddress = this.tpi.ip;		//获取IP
		int IPport = this.tpi.port;			//获取端口
		try{
			//建立连接
			log.debug("即将建立与被测系统的连接：" + IPaddress + ":" + IPport);
			this.sk = new Socket(IPaddress, IPport);
		}catch(UnknownHostException e){
			log.error("0x0D20：远程地址["+IPaddress+"]["+IPport+"]解析失败！["+e.getMessage()+"]");
			throw new Exception("0x0D20：远程地址["+IPaddress+"]["+IPport+"]解析失败！["+e.getMessage()+"]");
		}catch(IOException e){
			log.error("0x0D21：远程地址["+IPaddress+"]["+IPport+"]连接失败！["+e.getMessage()+"]");
			throw new Exception("0x0D21：远程地址["+IPaddress+"]["+IPport+"]连接失败！["+e.getMessage()+"]");
		}catch(SecurityException e){
			log.error("0x0D12：本地或远程安全策略阻止了连接建立！["+e.getMessage()+"]");
			throw new Exception("0x0D12：本地或远程安全策略阻止了连接建立！["+e.getMessage()+"]");
		}
		try{
			this.i = this.sk.getInputStream();
			this.o = this.sk.getOutputStream();
		}catch(IOException e){
			log.error("0x0D22：输入、输出流绑定出错！["+e.getMessage()+"]");
			this.closeCTcpLink(this.sk);
			throw new Exception("0x0D22：输入、输出流绑定出错！["+e.getMessage()+"]");
		}
	}

	
	/**
	 * 按照长度接收报文
	 * @param len 要接收的报文长度
	 * @return	接收到的报文
	 * @throws Exception
	 */
	private byte[] recv4len(int len) throws Exception{
		log.debug("接收"+len+"个字节的报文。");
		byte[] trans = null;
		try { 
			int nsize, tsize, recvlen;
			nsize = 0;
			tsize = len;
			trans = new byte[tsize];
			while (nsize < tsize) {
				recvlen = this.i.read(trans, nsize, tsize - nsize);
				nsize += recvlen;
				log.debug("本次收到"+recvlen+"字节");
				log.debug("本次收到["+new String(trans)+"]");
				if (recvlen <= 0) {
					log.error("0x0D22：报文接收过程中通讯中断！");
					this.closeCTcpLink(this.sk);
					throw new Exception("0x0D22：报文接收过程中通讯中断！");
				}
			}
			return trans;
		} catch (IOException e) {
			log.error("0x0D22：报文接收过程中出现异常！["+e.getMessage()+"]");
			this.closeCTcpLink(this.sk);
			throw new Exception("0x0D22：报文接收过程中出现异常！["+e.getMessage()+"]");
		}
	}

	
	/**
	 * 关闭服务端套接字
	 * @param ss 要关闭的服务端套接字
	 * @throws Exception
	 */
	private void closeSTcpLink(ServerSocket ss) throws Exception{
		try{
			if (null != ss)
				ss.close();
			this.ssk = null;
		} catch(Exception e){
			log.error("0x0D27：服务端套接字关闭失败！["+e.getMessage()+"]");
			throw new Exception("0x0D27：服务端套接字关闭失败！["+e.getMessage()+"]");
		}
	}

	
	/**
	 * 关闭客户端套接字
	 * @param ss 要关闭的服务端套接字
	 * @throws Exception
	 */
	private void closeCTcpLink(Socket s) throws Exception{
		try{
			if (null != s)
				s.close();
			this.sk = null;
			if (this.i != null) 
				this.i.close();
			this.i = null;
			if (this.o != null) 
				this.o.close();
			this.o = null;
		} catch(Exception e){
			log.error("0x0D27：客户端套接字关闭失败！["+e.getMessage()+"]");
			throw new Exception("0x0D27：客户端套接字关闭失败！["+e.getMessage()+"]");
		}
	}
}
