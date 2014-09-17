package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTProperties;
import com.dc.tes.ui.client.model.IDistValidate;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 服务类异步接口
 * 1)名称唯一性判断
 * 2)根据版本获得版本的菜单Json数据，共页面解析
 * @author scckobe
 *
 */
public interface IHelperService extends RemoteService{
	/**
	 * 名称唯一性判断
	 * @param validator   	 实现IDistValidate接口的类
	 * @param validateValue  被验证的值
	 * @return				 返回是否唯一，true：唯一；false：已重复
	 */
	boolean IsNameDistinct(IDistValidate validator,String validateValue);
	/**
	 * 获得TES平台配置文件 另外同时将菜单信息加载进来
	 * @param confRoot  配置文件根目录
	 * @return          配置信息列表
	 * @throws Exception 
	 */
	List<GWTProperties> GetTESConfig(String confRoot) throws Exception;
}
