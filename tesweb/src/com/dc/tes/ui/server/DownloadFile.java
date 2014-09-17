package com.dc.tes.ui.server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.lf5.util.StreamUtils;

/**
 * 下载文件
 * 传入参数：fileName  ： 文件名称
 * @author Administrator
 *
 */
public class DownloadFile extends HttpServlet{ 
	private static final long serialVersionUID = -6071043442413361803L;

	@Override
	  protected void doGet(HttpServletRequest request, HttpServletResponse response)
	 throws  ServletException, IOException { 
		 Common(request,response);
	   } 
	 
	   // 覆写 doGet 方法， 参数 request 是来自客户端的请求对象
	   // 参数 response 是服务器端的响应对象
	   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws 
	   ServletException, IOException { 
		   Common(request,response);
	   } 
	   
	   private void Common(HttpServletRequest request, HttpServletResponse response)
	   throws ServletException, IOException
	   {
			// 取文件名
			String filename = request.getParameter("fileName");
			if (filename.contains("\\") || filename.contains("/")) {
				return;
			}
			
			try
			{
				InputStream stream = new HelperService().GetTempStream(filename);
				// 如果存在指定文件则将该文件的内容写到返回流中
				if (stream != null) {
					// 设HTTP头 否则会导致文件在IE中被直接打开
					response.setContentType("application/octet-stream");
					// 设下载时的文件名
					response.setHeader("Content-disposition", "attachment; filename=" + filename);
					
					StreamUtils.copy(stream, response.getOutputStream());
					response.getOutputStream().flush();
//					response.flushBuffer();
					return;
//					log.debug(200);
				} else {
					// 如果不存在该文件则返回404
//					resp.setStatus(404);
//					log.debug(404);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				response.reset();
//				response.setStatus(404);
				return;
			}
			
			
	   }
	}
