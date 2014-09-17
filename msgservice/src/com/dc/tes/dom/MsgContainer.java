package com.dc.tes.dom;

/**
 * 可以用来包容其它MsgItem的容器类
 * 
 * @author lijic
 * @see MsgArray, MsgStruct
 */
public abstract class MsgContainer extends MsgItem implements Iterable<MsgItem> {
	/**
	 * 获取某个名称的子元素
	 * 
	 * @param name
	 *            元素的名称 对于数组来说是指元素的位置
	 * @return 返回所需的元素。如果不存在这样的元素则返回null
	 */
	public abstract MsgItem get(String name);

	/**
	 * 获取指定位置的元素
	 * 
	 * @param index
	 *            元素的位置
	 * @return 返回所需的元素。如果不存在这样的元素则返回null
	 */
	public abstract MsgItem get(int index);

	/**
	 * 判断容器中是否存在指定名称的元素
	 * 
	 * @param name
	 *            元素的名称 对于数组来说是指元素的位置
	 * @return 如果存在此名称的元素则返回true 否则返回false
	 */
	public abstract boolean contains(String name);

	/**
	 * 获取容器中元素的数目
	 * 
	 * @return 返回容器中元素的数目
	 */
	public abstract int size();

	/**
	 * 向容器中放元素
	 * 
	 * @param name
	 *            元素的名称 对于数组来说是指元素的位置
	 * @param item
	 *            要放进容器中的元素
	 * @return 返回刚被放进去的元素
	 */
	public abstract MsgItem put(String name, MsgItem item);

	/**
	 * 查询某个元素在容器中的位置
	 * 
	 * @param o
	 *            要查询的元素 此对象必须为MsgItem类型 否则向外抛出ClassCastException异常
	 * @return 元素的位置 如果容器中不存在此元素则返回-1
	 */
	public abstract int indexOf(Object o);

	/**
	 * 移除容器中某个位置的元素
	 * 
	 * @param index
	 *            要移除的元素在容器中的位置
	 */
	public abstract void removeAt(int index);

	/**
	 * 移除容器中的某个元素 如果容器中原本就没有这个元素则不进行任何操作
	 * 
	 * @param item
	 *            被移除的元素
	 */
	public abstract void removeItem(MsgItem item);
	


}
