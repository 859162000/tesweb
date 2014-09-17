package com.dc.tes.monitor;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.dc.tes.monitor.data.Config;
import com.dc.tes.monitor.data.Context;
import com.dc.tes.monitor.data.LogDetail;
import com.dc.tes.monitor.data.LogMessage;
import com.dc.tes.util.RuntimeUtils;

/**
 * 监控服务Servlet
 * 
 * @author songljb
 *
 */
public class MonitorSrv extends HttpServlet {

	private static final long serialVersionUID = 8794098693218107142L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		int port = Integer.parseInt(config.getInitParameter("monitorPort"));
		int num = Integer.parseInt(config.getInitParameter("LogMaxNum"));
		int size = Integer.parseInt(config.getInitParameter("LogMaxSize"));
		Config.setPort(port);
		Config.setMAX_LOG_NUM(num);
		Config.setMAX_SEND_SIZE(size);

		// 开始tcp侦听
		Thread t = new Thread(new TCPThread());
		//t.setDaemon(true);//设置当服务停止时,线程也停止
		t.start();
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		TCPSnooping.close();
		Config.run = false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 解析queryString
		String queryString = URLDecoder.decode(req.getQueryString(), "utf-8");
		LinkedHashMap<String, Integer> pairs = new LinkedHashMap<String, Integer>();
		for (String item : queryString.split("\\&"))
			pairs.put(item.split("\\=")[0], Integer.parseInt(item.split("\\=")[1]));

		// 拼返回给页面的json
		StringBuffer buffer = new StringBuffer("{");
		for (String core : pairs.keySet()) {
			if (Context.checkLog(core)) {
				LogMessage manager = Context.getLogMsg(core);

				buffer.append(core).append(":{");
				// 适配器状态
				buffer.append("adapters:{").append(manager.getAdpflag()).append("},");

				// 日志
				buffer.append("log:[");

				LogDetail[] logs = manager.getLog(pairs.get(core));
				int lastId = manager.getmaxid();
				String[] logJsons = new String[logs.length];

				for (int i = 0; i < logs.length; i++) {
					LogDetail log = logs[i];

					StringBuffer logBuffer = new StringBuffer();
					logBuffer.append("{");

					logBuffer.append("tranState:").append(log.getTRANSTATE()).append(",");
					logBuffer.append("tranTime:\"").append(StringEscapeUtils.escapeJavaScript(log.getTRANTIME().toString())).append("\",");
					logBuffer.append("tranCode:\"").append(StringEscapeUtils.escapeJavaScript(log.getTRANCODE())).append("\",");
					logBuffer.append("caseName:\"").append(StringEscapeUtils.escapeJavaScript(log.getCASENAME())).append("\",");
					logBuffer.append("msgIn:\"").append(StringEscapeUtils.escapeJavaScript(RuntimeUtils.PrintHex(log.getMSGIN(), RuntimeUtils.utf8))).append("\",");
					logBuffer.append("msgOut:\"").append(StringEscapeUtils.escapeJavaScript(RuntimeUtils.PrintHex(log.getMSGOUT(), RuntimeUtils.utf8))).append("\",");
					logBuffer.append("dataIn:\"").append(StringEscapeUtils.escapeJavaScript(log.getDATAIN())).append("\",");
					logBuffer.append("dataOut:\"").append(StringEscapeUtils.escapeJavaScript(log.getDATAOUT())).append("\",");
					logBuffer.append("errMsg:\"").append(StringEscapeUtils.escapeJavaScript(log.getERRMSG())).append("\"");

					logBuffer.append("}");

					logJsons[i] = logBuffer.toString();
				}

				buffer.append(StringUtils.join(logJsons, ","));
				buffer.append("],");

				buffer.append("lastId:").append(lastId);
				buffer.append("}");
			}
		}
		buffer.append("}");

		// 将json返回给页面
		resp.getOutputStream().write(buffer.toString().getBytes(RuntimeUtils.utf8));
	}
	
}
