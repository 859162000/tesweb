/**
 * 
 */
package com.dc.tes.gta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapterapi.Adapter2Tes;

/**
 * 负责处理TCP请求的类
 * @author Conan
 *
 */
public class RecvHandler implements Runnable {
	/**
	 * 日志对象
	 */
	public static Log log = LogFactory.getLog(TcpServer4Adapter.class);

	/**
	 * 连接套接字
	 */
	private Socket sk = null;

	/**
	 * 输入流
	 */
	private InputStream ips = null;

	/**
	 * 输出流
	 */
	private OutputStream out = null;

	/**
	 * 接收到的请求报文
	 */
	protected byte[] reqmessage = null;

	/**
	 * 返回报文
	 */
	protected byte[] resmessage = null;

	/**
	 * TCP适配器配置属性
	 */
	private TcpProperty tpi = null;

	/**
	 * 构造函数
	 * @param s	连接套接字
	 * @param tp TCP属性信息
	 */
	public RecvHandler(Socket s, TcpProperty tp, InputStream in, OutputStream out) throws Exception{
		this.sk = s;
		this.tpi = tp;
		this.ips = in;
		this.out = out;
	}

	/**
	 * 处理TCP连接
	 */
	public void run() {
		long inTime = System.currentTimeMillis();		//接到请求的时间
		long outTime = -1;								//核心处理完成的时间
		long usedTime = 0;								//核心处理时间
		int delayTime = -1;								//系统返回的延时时间
		//log.debug("接收到来自于：["+this.sk.getInetAddress().toString().substring(1)+"]的报文请求。");
		this.reqmessage = null;	//请求消息体
		this.resmessage = null;	//返回消息体
		do{
			if (this.tpi.isfix>0){
				//定长报文处理流程
				try{
					reqmessage = this.recv4len(this.tpi.isfix);
					log.debug("接收到的定长报文长度：["+reqmessage.length+"]");
					log.debug("接收到的定长报文内容：["+(new String(reqmessage))+"]");
					if (this.tpi.need2core > 0)
						resmessage = this.send2core(reqmessage);
				} catch(Exception e){
					log.error(e.getMessage());
					this.closeCTcpLink(this.sk);
					return;
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
						return;
					}
					reqmessage = new byte[lenlen];	//返回消息体
					System.arraycopy(buff, 0, reqmessage, 0, this.tpi.len4len);
				}catch(IndexOutOfBoundsException e){
					log.error("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					return;
				}catch(ArrayStoreException e){
					log.error("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					return;
				}catch(NullPointerException e){
					log.error("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					return;
				}catch(NumberFormatException e){
					log.error("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					return;
				} catch(Exception e){
					log.error(e.getMessage());
					this.closeCTcpLink(this.sk);
					return;
				}		
				try{
					if (lenlen>this.tpi.len4len){
						//收取完整报文
						try{
							buff = this.recv4len(lenlen-this.tpi.len4len);
							System.arraycopy(buff, 0, reqmessage,
									this.tpi.len4len, lenlen-this.tpi.len4len);
						} catch(IndexOutOfBoundsException e){
							log.error("0x0D06：获取报文信息时，数组写操作越界！["+e.getMessage()+"]");
							this.closeCTcpLink(this.sk);
							return;
						}catch(ArrayStoreException e){
							log.error("0x0D07：获取报文信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
							this.closeCTcpLink(this.sk);
							return;
						}catch(NullPointerException e){
							log.error("0x0D08：获取报文信息时，操作的数组为空！["+e.getMessage()+"]");
							this.closeCTcpLink(this.sk);
							return;
						}catch(NumberFormatException e){
							log.error("0x0D09：获取报文信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
							this.closeCTcpLink(this.sk);
							return;
						} catch(Exception e){
							log.error(e.getMessage());
							this.closeCTcpLink(this.sk);
							return;
						}
					}
					if (this.tpi.need2core > 0)
						resmessage = this.send2core(reqmessage);
				} catch(Exception e){
					log.error(e.getMessage());
					this.closeCTcpLink(this.sk);
					return;
				}
			}

			//处理延时
			outTime = System.currentTimeMillis();
			usedTime = outTime - inTime;
			log.debug("核心的处理时间：["+usedTime+"]");
			if (usedTime < delayTime)
			{
				log.debug("进行延时处理：["+(delayTime-usedTime)+"]"+delayTime+"-"+usedTime);
				try {
					Thread.sleep(delayTime-usedTime);
				} catch (InterruptedException e) {
					log.error("0x0803：适配器延时处理失败！["+e.getMessage()+"]");
					this.closeCTcpLink(this.sk);
					return;
				}
			}

			try {
				if (this.tpi.needback > 0){
					//进行报文返回
					if (this.tpi.fixback > 0){
						log.debug("返回报文:["+new String(this.tpi.backmessage)+"]");
						out.write(this.tpi.backmessage);
						out.flush();
					}
					else{
						log.debug("返回报文:["+new String(resmessage)+"]");
						out.write(resmessage);
						out.flush();
					}
				}
			} catch (IOException e) {
				log.error("0x0D22：报文返回过程出现异常！["+e.getMessage()+"]");
				this.closeCTcpLink(this.sk);
				return;
			}
		}while(1==this.tpi.islast);//判断是否为长连接
		this.closeCTcpLink(this.sk);
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
				recvlen = this.ips.read(trans, nsize, tsize - nsize);
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
	 * 将报文发送往核心
	 * @param message	要发往核心的请求报文REQMESSAGE
	 * @return	核心返回的响应报文RESMESSAGE
	 * @throws Exception
	 */
	private byte[] send2core(byte[] message) throws Exception{	
		byte[] resMessage = null;
		try {
			resMessage = new Adapter2Tes().sendToCore(message);
		} catch (Exception e) {
			log.error("0x0800：与核心交互失败！["+e.getMessage()+"]");
			throw new Exception("0x0800：与核心交互失败！["+e.getMessage()+"]");
		}

		return resMessage;
	}

	/**
	 * 关闭客户端套接字
	 * @param ss 要关闭的服务端套接字
	 * @throws Exception
	 */
	private void closeCTcpLink(Socket s) {
		try{
			if (null != s)
				s.close();
			this.sk = null;
		} catch(Exception e){
			log.error("0x0D27：套接字关闭失败！["+e.getMessage()+"]");
		}
	}

}
