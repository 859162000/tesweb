package com.dc.tes.fcore.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeJavaObject;
import sun.org.mozilla.javascript.internal.NativeObject;
import sun.org.mozilla.javascript.internal.Undefined;
import sun.org.mozilla.javascript.internal.UniqueTag;
import sun.org.mozilla.javascript.internal.WrappedException;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.TransactionMode;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.util.DomSerializer;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.FCore;
import com.dc.tes.fcore.msg.IPacker;
import com.dc.tes.fcore.msg.IUnpacker;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.RuntimeUtils;

/**
 * 脚本执行机 用于执行脚本
 * 
 * @author lijic
 * 
 */
public class ScriptMachine {
	public static final Log log = LogFactory.getLog(ScriptMachine.class);

	private static final String C_EMPTY_DATA = "{'.nodata':true}";

	/**
	 * 执行一段脚本
	 * 
	 * @param code
	 *            脚本代码
	 * @param in
	 *            输入报文
	 * @param out
	 *            输出报文
	 * @param core
	 *            功能核心实例
	 * @param tranCode
	 *            交易码
	 * @param channel
	 *            通道名称
	 * @param mode
	 *            交易类型
	 * @param scriptName
	 * @param tag
	 * @throws Exception
	 */
	public static void Exec(String code, InMessage in, OutMessage out, final FCore core, final String tranCode, String channel, TransactionMode mode, String scriptName, String tag, String executeLogId, String userId, boolean byapi) throws Exception {
		log.debug("执行脚本");
		log.debug("code = " + code);
		log.debug("IN = " + in);
		log.debug("OUT = " + out);
		log.debug("tranCode = " + tranCode);
		log.debug("channel = " + channel);
		log.debug("mode = " + mode);

		// 预处理代码 在脚本头部放入lib.js		
		StringBuffer buffer = new StringBuffer();
		append(buffer, "com/dc/tes/fcore/script/lib.js.txt");
		// 放入脚本主体
		buffer.append(code);

		// 初始化脚本执行引擎
		ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine engine = manager.getEngineByName("js");

		// 将脚本执行上下文作为ENV变量设给脚本执行引擎
		ScriptEnv env = new ScriptEnv(core, tranCode, out != null ? out.caseName : null, mode, channel);
		engine.put("ENV", env);

		// 解析脚本 如果使用了IN对象 则将输入报文拆包
		if (code.contains("IN.")) {
			if (in == null)
				throw new ScriptException("在当前的上下文语境下不支持IN变量");

			// 如果in未拆过包 则对in进行拆包 当核心的LAZY_UNPACK被设置为true时会发生这种情况
			if (in.data == null) {
				// 获取拆包组件
				IUnpacker unpacker = core.unpackers.get(channel);

				// 拆包
				Transaction tran = core.da.GetTran(tranCode, mode);
				MsgDocument templateIn = MsgLoader.LoadXml(mode == TransactionMode.Client ? tran.getResponseStruct() : tran.getRequestStruct());
				in.data = unpacker.Unpack(in.bin, templateIn, new MsgContext(tranCode));
			}

			// 将输入报文作为IN变量设给脚本执行引擎
			String _in = DomSerializer.SerializeToJson(in.data);
			engine.put("_IN", _in);
		} else
			engine.put("_IN", C_EMPTY_DATA);

		// 将输出报文作为OUT变量设给脚本执行引擎
		Object _out = (out == null || out.data == null) ? C_EMPTY_DATA : DomSerializer.SerializeToJson(out.data);
		engine.put("_OUT", _out);

		// 将持久化数据作为REG变量设给脚本执行引擎
		Map<String, Object> pdata = core.da.GetPersistentData();
		List<String> pdataVars = new ArrayList<String>();
		for (String name : pdata.keySet())
			pdataVars.add("\"" + name + "\":" + (pdata.get(name) instanceof Number ? pdata.get(name) : "\"" + pdata.get(name) + "\""));
		engine.put("_REG", "{" + StringUtils.join(pdataVars, ",") + "}");

		//如果没有传入脚本名称，则记录日志时的标志是交易码、交易类型
		if (scriptName == null || scriptName.equals("")) {
			env.setScriptName(tranCode);
			env.setTag(mode.toString());
		} else {
			env.setScriptName(scriptName);
			env.setTag(tag);
		}
		//加入执行日志ID
		if(executeLogId != null)
			env.setExecuteLogId(executeLogId);
		env.setByApi(byapi);
		env.setUserId(userId);
		if (code != null)
			env.stateLog("开始执行脚本", 0);
		// 执行脚本
		try {
			engine.eval(buffer.toString());
		} catch (ScriptException ex) {
			if (ex.getCause() instanceof WrappedException) {
				ex = new ScriptException((Exception) ((WrappedException) ex.getCause()).getWrappedException());
			}
			env.errorLog("执行脚本出错" + ex.getMessage(), ex.getLineNumber());
			throw ex;
		}
		if (code != null)
			env.stateLog("脚本执行结束", 1);
		// 获取REG变量并保存
		NativeObject _reg = (NativeObject) engine.get("REG");
		pdata = new HashMap<String, Object>();
		for (Object id : _reg.getAllIds())
			if (id instanceof Integer)
				pdata.put(id.toString(), _reg.get((Integer) id, null));
			else
				pdata.put(id.toString(), _reg.get(id.toString(), null));
		core.da.SetPersistentData(pdata);

		// 如果向脚本执行机传入了out变量 则在脚本执行完毕后应该将值更新回去
		if (out != null) {
			OutMessage _scriptOut = prepareMessage((NativeObject) engine.get("OUT"), core, mode);
			out.bin = _scriptOut.bin;
			out.caseName = _scriptOut.caseName;
			out.channel = _scriptOut.channel;
			out.data = _scriptOut.data;
			out.delay = _scriptOut.delay;
			out.preserved2 = _scriptOut.preserved2;
			out.tranCode = _scriptOut.tranCode;
		}

		// 如果已经对IN进行了回复 则在脚本执行完毕后应该对in进行一些设置
		if (in != null)
			in.replyFlag = getObject((NativeObject) engine.get("IN"), ".replyFlag") != null;
	}

	/**
	 * 工具函数 用于将一个代码文件中的内容附加到已有代码的尾部
	 * 
	 * @param code
	 *            已有代码
	 * @param path
	 *            要附加的文件
	 * @throws IOException
	 */
	private static void append(StringBuffer code, String path) throws IOException {
		String code_append = RuntimeUtils.ReadResource(path, RuntimeUtils.utf8);
		code.append(code_append);
	}

	/**
	 * 
	 * @author lijic
	 * 
	 */
	public static class ScriptMessage {
		/**
		 * 
		 */
		public final String data;
		/**
		 * 
		 */
		public final String tranCode;
		/**
		 * 
		 */
		public final String channel;
		/**
		 * 
		 */
		public final Thread t;

		public final InMessage in;

		/**
		 * 
		 * @param data
		 * @param tranCode
		 * @param channel
		 * @param t
		 */
		public ScriptMessage(InMessage in, String data) {
			super();
			this.data = data;
			this.tranCode = in.tranCode;
			this.channel = in.channel;
			this.t = in.t;
			this.in = in;
		}
	}

	/**
	 * 将脚本执行机中的数据对象处理为可向被测系统发送的报文 进行必要的组包工作
	 * 
	 * @param obj
	 *            脚本执行机中的数据对象
	 * @return 输出报文对象
	 * @throws Exception
	 */
	static OutMessage prepareMessage(NativeObject obj, FCore core, TransactionMode mode) throws Exception {
		OutMessage msg = new OutMessage();
		msg.tranCode = getObject(obj, ".tranCode");
		msg.caseName = getObject(obj, ".caseName");
		msg.channel = getObject(obj, ".channel");
		Object delay = getObject(obj, ".delay");
		if (delay != null)
			msg.delay = (int) Math.round((Double) delay);

		Object nodata = getObject(obj, ".nodata");

		Transaction tran = core.da.GetTran(msg.tranCode, mode);

		// 判断该报文是否有案例数据
		if (nodata != null) {
			// 该报文有.nodata标记 则表示该报文没有携带数据 需要从其.caseName中获取对应的二进制报文
			msg.bin = core.da.GetCase(msg.caseName, tran).getRequestMsg();
		} else {
			// 该报文没有.nodata标记 表示该报文有案例数据
			// 读取该变量的内容 将其转为MsgDocument
			MsgDocument templateOut = MsgLoader.LoadXml(mode == TransactionMode.Client ? tran.getRequestStruct() : tran.getResponseStruct());
			msg.data = convertFromNativeObject(obj, templateOut);

			// 获取组包组件
			IPacker packer = core.packers.get(msg.channel);
			// 组包
			msg.bin = packer.Pack(msg.data, new MsgContext(msg.tranCode));
		}

		return msg;
	}

	/**
	 * 将从被测系统接到的报文处理为供脚本使用的对象 进行必要的拆包工作
	 * 
	 * @param msg
	 *            从被测系统接收到的报文
	 * @return 供脚本使用的对象
	 * @throws Exception
	 */
	static ScriptMessage parseMessage(InMessage msg, FCore core, TransactionMode mode) throws Exception {
		// 获取拆包组件
		IUnpacker unpacker = core.unpackers.get(msg.channel);

		// 拆包
		Transaction tran = core.da.GetTran(msg.tranCode, mode);
		MsgDocument templateIn = MsgLoader.LoadXml(mode == TransactionMode.Client ? tran.getResponseStruct() : tran.getRequestStruct());
		msg.data = unpacker.Unpack(msg.bin, templateIn, new MsgContext(msg.tranCode));
		String data = DomSerializer.SerializeToJson(msg.data);

		return new ScriptMessage(msg, data);
	}

	/**
	 * 工具函数 用于将从js执行机中取到的NativeObject转为MsgDocument
	 * 
	 * @param obj
	 *            从js执行机中取到的NativeObject
	 * @param template
	 *            输出报文结构 该结构将被用作模板
	 * @return 包含期望的数据的MsgDocument
	 * @throws SAXException
	 * @throws IOException
	 */
	private static MsgDocument convertFromNativeObject(NativeObject obj, MsgDocument template) {
		MsgDocument doc = new MsgDocument();

		NativeObject _doc = (NativeObject) obj;
		for (Object id : _doc.getAllIds())
			convertFromNativeObject((String) id, _doc.get((String) id, null), doc);

		doc = MsgContainerUtils.Normalize(doc, template);

		return doc;
	}

	/**
	 * 工具函数 用于将从js执行机中取到的javascript对象放到指定的MsgContainer中
	 * 
	 * @param name
	 *            对象名称
	 * @param obj
	 *            从javascript中取出的对象
	 * @param container
	 *            容器
	 */
	private static void convertFromNativeObject(String name, Object obj, MsgContainer container) {
		// 以.开头的元素为模拟器内部使用的元素
		if (name.startsWith("."))
			return;

		// 字符串
		if (obj instanceof String) {
			MsgField field = new MsgField();
			field.setAttribute("name", new Value(name));
			field.set((String) obj);
			container.put(name, field);
		} else

		// 数字
		if (obj instanceof Number) {
			MsgField field = new MsgField();
			field.setAttribute("name", new Value(name));
			field.set(obj.toString());
			container.put(name, field);
		} else

		// Java对象
		if (obj instanceof NativeJavaObject) {
			MsgField field = new MsgField();
			field.setAttribute("name", new Value(name));
			field.set(((NativeJavaObject) obj).unwrap().toString());
			container.put(name, field);
		} else

		// javascript数组
		if (obj instanceof NativeArray) {
			MsgArray array = new MsgArray();
			array.setAttribute("name", new Value(name));
			container.put(name, array);

			NativeArray _array = (NativeArray) obj;
			for (int i = 0; i < _array.getLength(); i++)
				convertFromNativeObject(String.valueOf(i), _array.get(i, null), array);
		} else

		// javascript对象（结构）
		if (obj instanceof NativeObject) {
			MsgStruct stru = new MsgStruct();
			stru.setAttribute("name", new Value(name));
			container.put(name, stru);

			NativeObject _stru = (NativeObject) obj;
			for (Object id : _stru.getAllIds())
				convertFromNativeObject((String) id, _stru.get((String) id, null), stru);
		} else

		// javascript undefined
		if (obj instanceof Undefined) {
			MsgField field = new MsgField();
			field.setAttribute("name", new Value(name));
			field.set("");
			container.put(name, field);
			log.warn("试图获取的报文元素" + container.dpath() + "." + name + "的值为undefined");
		} else

			// 其它类型 当前无法处理
			throw new UnsupportedOperationException(obj.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	private static <T> T getObject(NativeObject obj, String name) {
		try {
			Object o = obj.get(name, null);

			if (o instanceof Undefined)
				return null;
			if (o instanceof UniqueTag)
				return null;
			if (o instanceof NativeJavaObject)
				return (T) ((NativeJavaObject) o).unwrap();

			return (T) o;
		} catch (Exception ex) {
			return null;
		}
	}
}
