package com.dc.tes.ui.client.model;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

/**
 * 处理服务器端分页中，每页取数的起止行号
 * 起始行号为0，如只有一条记录，则从0到0
 * 
 * @author scckobe
 *
 */
public class PageStartEnd {
	/**
	 * 开始行号
	 */
	private int start;
	
	/**
	 * 结束行号
	 */
	private int end;
	
	/**
	 *	配置
	 * 
	 * @param config 客户端传进来的分页配置
	 * @param count  服务器查询得到的总行数
	 */
	public PageStartEnd(PagingLoadConfig config,int count)
	{
		start = config.getOffset();
		if(count == 0)
			end = count;
		else
			end = count - 1;
		if (config.getLimit() > 0) {
			end = Math.min(start + config.getLimit() - 1, end) ;
		}
	}
	
	/**
	 * 获得结束行号
	 * @return 结束行号
	 */
	public int getEnd()
	{
		return end;
	}
	
	/**
	 * 获得开始行号
	 * @return 开始行号
	 */
	public int getStart()
	{
		return start;
	}
}
