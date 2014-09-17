package com.dc.tes.ui.server;

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * 客户端Bean 与服务器端Bean的相互转换接口
 * @author scckobe
 *
 * @param <T1>	客户端Bean
 * @param <T2>	服务器端Bean
 */
public interface IBeanDaoTranslate<T1 extends ModelData,T2>{
	/**
	 * 服务器端Bean对象 转换为 客户端Bean对象
	 * @param gwtInfo	服务器端Bean对象
	 * @return			客户端Bean对象
	 */
	T1 BeanToModel(T2 serverBean);

	/**
	 * 客户端Bean对象 转换为 服务器端Bean对象
	 * @param serverBean	服务器端Bean对象（原始Bean，对于服务器端bean对象的一些属性不存于客户端bean的情况，如果更新时得传入）
	 * @param gwtInfo		客户端Bean对象
	 * @return				服务器端Bean对象
	 */
	T2 ModelToBean(T2 serverBean, T1 gwtInfo);
}
