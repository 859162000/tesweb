package com.dc.tes.gta;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.dc.tes.adapterapi.Adapter2Tes;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RecvHandlerTest extends TestCase {
	/**
	 * 被测试对象
	 */
	private RecvHandler rh = null;
	
	/**
	 * 适配器属性
	 */
	private TcpProperty tp = null;
	
	/**
	 * 输入流
	 */
	private InputStream in = null;
	
	/**
	 * 输出流
	 */
	private OutputStream out = null;
	
	/**
	 * 初始化
	 */
	public void setUp(){
		this.tp = new TcpProperty();
	}

	/**
	 * 清理，其实俺啥都不用干
	 */
	public void tearDown(){

	}

	/**
	 * 定长报文处理流程测试
	 */
	public void testRun_01(){
		this.tp.isfix = 10;
		this.tp.need2core = 1;
		byte[] intput = "I'm the inputstream!!!".getBytes();
		byte[] output = null;
		this.in = new ByteArrayInputStream(intput);
		try {
			System.setProperty("testfilename", "./testdata/testOutputStream");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("./testdata/testOutputStream"));
			ByteArrayOutputStream out = new ByteArrayOutputStream(64*1024);

			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			in.close();			
			output = out.toByteArray();
			this.rh = new RecvHandler(null, this.tp, this.in, this.out);
			this.rh.run();
		} catch (Exception e) {
			Assert.fail();
		}
		//开始验证
		Assert.assertEquals(this.tp.isfix, this.rh.reqmessage.length);
		for (int i=0; i<this.tp.isfix; i++)
			Assert.assertEquals(intput[i], this.rh.reqmessage[i]);
		Assert.assertEquals(output.length, this.rh.resmessage.length);
		for (int i=0; i<output.length; i++)
			Assert.assertEquals(output[i], this.rh.resmessage[i]);
	}
	
	/**
	 * 变长报文接收流程测试
	 */
	public void testRun_02(){
		this.tp.isfix = 0;
		this.tp.len4len = 10;
		this.tp.lenstart = 4;
		this.tp.lenlen = 5;
		this.tp.need2core = 1;
		byte[] intput = "abcd00015F12345".getBytes();
		byte[] output = null;
		this.in = new ByteArrayInputStream(intput);
		try {
			System.setProperty("testfilename", "./testdata/testOutputStream");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("./testdata/testOutputStream"));
			ByteArrayOutputStream out = new ByteArrayOutputStream(64*1024);

			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			in.close();			
			output = out.toByteArray();
			this.rh = new RecvHandler(null, this.tp, this.in, this.out);
			this.rh.run();
		} catch (Exception e) {
			Assert.fail();
		}
		//开始验证
		Assert.assertEquals(15, this.rh.reqmessage.length);
		for (int i=0; i<this.tp.isfix; i++)
			Assert.assertEquals(intput[i], this.rh.reqmessage[i]);
		Assert.assertEquals(output.length, this.rh.resmessage.length);
		for (int i=0; i<output.length; i++)
			Assert.assertEquals(output[i], this.rh.resmessage[i]);
	}
}
