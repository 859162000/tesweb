package com.dc.tes.dom;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dc.tes.dom.MsgStruct.SubItem;
import com.dc.tes.dom.util.DPathUtils;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.XmlUtils;

/**
 * 工具类 提供一些与报文结构树有关的工具函数
 * 
 * @author lijic
 * 
 */
public class MsgContainerUtils {
	/**
	 * 内部名称前缀
	 */
	public static final String C_InternalDomElementPrefix = "__tes__internal__";
	/**
	 * 内部属性名称 - 数组元素序号
	 */
	public static final String C_ArrayIndex = C_InternalDomElementPrefix + "arrayIndex";

	/**
	 * 获取某个MsgItem的以XML Attribute的形式表示的属性列表
	 * 
	 * @param internal
	 *            是否把标记为"内部"的属性打印出来
	 */
	static String PrintAttributesList(MsgItem item, boolean internal) {
		StringBuffer buffer = new StringBuffer();

		for (String key : item.getAttributes().keySet())
			if (internal || (!internal && !key.startsWith(C_InternalDomElementPrefix)))
				buffer.append(String.format(" %s=\"%s\"", key, XmlUtils.XmlEncode(item.getAttribute(key).toString())));

		return buffer.toString();
	}

	/**
	 * 将MsgStruct中的某项改名，并保持其位置不变（先remove后add会打乱原有顺序）
	 * 
	 * @param stru
	 *            容器
	 * @param name
	 *            要被改名的节点的原名称
	 * @param newName
	 *            要被改名的节点的新名称
	 */
	public static void Rename(MsgStruct stru, String name, String newName) {
	//	if (stru.containsKey(newName))
	//		throw new TESException(MsgErr.Dom.RenameNodeFail, newName);

		for (MsgStruct.SubItem item : stru.m_items)
			if (item.name.equals(name))
				item.name = newName;
	}

	/**
	 * 将某一项移到数组中 如果不存在这个数组则新建数组
	 * 
	 * @param stru
	 *            容器
	 * @param name
	 *            要被移动的节点的名称
	 * @param arrayName
	 *            要被添加到的数组的名称
	 * @return 包含了原来的项的新数组
	 */
	public static MsgArray MoveToArray(MsgStruct stru, String name, String arrayName) {
		MsgItem arrayBase = stru.get(arrayName);
		MsgItem item = stru.get(name);

		MsgArray array;
		if (arrayBase instanceof MsgArray)
			array = (MsgArray) arrayBase;
		else {
			array = new MsgArray();
			array.setAttribute("name", arrayName);

			for (SubItem subItem : stru.m_items)
				if (subItem.item == item) {
					subItem.name = arrayName;
					subItem.item = array;
					array.m_parent = stru;
					break;
				}
		}

		array.add(item);
		stru.removeItem(item);

		return array;
	}

	/**
	 * 递归拷贝模板节点及其所有子节点中各节点的所有属性信息至目标节点及其子节点<br />
	 * 如果遇到数组项则将模板中数组第0项的属性拷到目标节点中数组的每一项中<br />
	 * 不会拷贝被标为"内部"的节点的属性信息
	 * 
	 * @param target
	 *            目标节点
	 * @param template
	 *            模板
	 */
	public static void CopyAttributes(MsgItem target, MsgItem template) {
		CopyAttributes(target, template, false);
	}

	/**
	 * 递归拷贝模板节点及其所有子节点中各节点的所有属性信息至目标节点及其子节点<br />
	 * 如果遇到数组项则将模板中数组第0项的属性拷到目标节点中数组的每一项中
	 * 
	 * @param target
	 *            目标节点
	 * @param template
	 *            模板
	 * @param copyInternalAttributes
	 *            是否从模板中寻找并拷贝被标为"内部"的节点的属性
	 */
	public static void CopyAttributes(MsgItem target, MsgItem template, final boolean copyInternalNode) {
		final MsgItem _template = template;
		target.ForEach(new ISimpleForEachVisitor() {
			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				// 如果一个域被内部使用则不从模板中拷贝属性
				if (!copyInternalNode && item.name().contains(C_InternalDomElementPrefix))
					return;

				String tPath = item.dpath().replaceAll("\\.\\d+\\.", ".0.").replaceAll("\\.\\d+$", ".0");
				MsgItem templateNode = _template.root().SelectSingleNode(tPath);
				if (templateNode == null)
					return;// 在模板中没有找到目标项 直接忽略

				Map<String, Value> attribs = templateNode.getAttributes();
				item.setAttributes(attribs);
			}
		});
	}

	/**
	 * 递归清空此节点及其所有子节点中被标记为"内部"的属性
	 * 
	 * @param item
	 *            要被删除"内部属性"的节点
	 */
	public static void ClearInternalAttributes(MsgItem item) {
		item.ForEach(new ISimpleForEachVisitor() {
			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				ArrayList<String> internalAttributeNames = new ArrayList<String>();
				for (String key : item.getAttributes().keySet())
					if (key.startsWith(MsgContainerUtils.C_InternalDomElementPrefix))
						internalAttributeNames.add(key);
				for (String key : internalAttributeNames)
					item.getAttributes().remove(key);
			}
		});
	}

	/**
	 * 向MsgDocument中的某个域写入值。如果不存在这个域，则级联创建。
	 * 
	 * @param doc
	 *            容器
	 * @param dpath
	 *            要被写入的域的路径
	 * @param value
	 *            值
	 * @param attribs
	 *            域的属性 此项可为null
	 */
	public static void PutValue(MsgDocument doc, String dpath, String value, Map<String, Value> attribs) {
		MsgContainer container = doc;
		MsgItem item;

		attribs.put("name", new Value(DPathUtils.getName(dpath)));

		String[] paths = dpath.split("\\.");
		for (int i = 0; i < paths.length; i++) {
			if (StringUtils.isNumeric(paths[i]))
				container = (MsgArray) ensure(container.parent(), MsgArray.class, paths[i - 1]);

			if (i != paths.length - 1) {
				item = ensure(container, MsgStruct.class, paths[i]);
				container = (MsgContainer) item;
			} else {
				item = ensure(container, MsgField.class, paths[i]);

				MsgField field = (MsgField) item;
				field.set(value);
				if (attribs != null)
					for (String key : attribs.keySet())
						item.setAttribute(key, attribs.get(key));
				container.put(DPathUtils.getName(dpath), field);
			}
		}
	}

	/**
	 * 根据每个元素中的ArrayIndex属性重新对数组进行排序 如果所有元素都有arrayIndex则根据该值进行排序 如果所有元素都没有arrayIndex则保持原数组不变 如果有的有有的没有则报错
	 * 
	 * @param array
	 *            要被重新排序的数组
	 */
	public static void ResortArray(MsgArray array) {
		// 判断是否需要进行重排
		boolean allExist = true;
		boolean allNotExist = true;

		for (MsgItem item : array)
			if (item.getAttribute(C_ArrayIndex) != Value.empty)
				allNotExist = false;
			else
				allExist = false;

		// 判断该数组中的元素是不是都有ArrayIndex或都没有ArrayIndex
		if (!allNotExist && !allExist)
			throw new TESException(MsgErr.Dom.NotAllContains_ArrayIndex_Flag, array.toString(true, 0));
		// 如果都没有ArrayIndex则什么都不做 直接返回
		if (allNotExist)
			return;

		// 进行到这里说明该数组中的所有元素都有ArrayIndex属性 需要对其进行重排
		ArrayList<MsgItem> lst = array.m_items;
		// 使用最简单的冒泡排序
		for (int i = 0; i < lst.size(); i++)
			for (int j = 0; j < lst.size(); j++)
				if (lst.get(i).getAttribute(C_ArrayIndex).i > lst.get(j).getAttribute(C_ArrayIndex).i) {
					MsgItem t = lst.get(i);
					lst.set(i, lst.get(j));
					lst.set(j, t);
				}

	}

	/**
	 * 规整化 将给定的MsgDocument按照模板的结构进行规整
	 * 
	 * @param doc
	 *            需要进行规整化的MsgDocument
	 * @param template
	 *            模板
	 * @return 按照模板进行了规整化的MsgDocument
	 */
	public static MsgDocument Normalize(final MsgDocument doc, final MsgDocument template) {
		/*
		 * 规整化的规则：
		 * 
		 * 规整化后的报文中各元素的类型、属性与顺序与模板完全一致
		 * 原报文中没有的部分会添加上，模板中没有的部分会删掉 
		 * (X)如果原报文中的某个元素在模板中应该对应于一个数组 则将其移入该数组中
		 * (X)如果原报文中的某个数组在模板中不是一个数组 则保留其第一项 删除其余数组元素
		 *  打X的暂时未实现
		 */
		if (doc == null || template == null) {
			return null;
		}
		MsgDocument result = template.Copy();

		result.ForEach(new DefaultForEachVisitor() {
			@Override
			public void Field(MsgField field) {
				field.set(doc.SelectSingleField(field.dpath()).value());
			}
		});
		return result;
	}

	/**
	 * 工具函数 确保MsgContainer中的某个子节点存在且其类型为指定的类型
	 * 
	 * @param container
	 *            容器
	 * @param type
	 *            节点的类型
	 * @param name
	 *            节点的名称
	 * @return 期望存在的节点
	 */
	private static MsgItem ensure(MsgContainer container, Class<? extends MsgItem> type, String name) {
		// 取当前的节点
		MsgItem item = container.get(name);
		// 是否需要新建节点
		boolean needNewItem = false;

		if (item == null)
			// 如果节点为空则需要新建
			needNewItem = true;
		else {
			// 如果节点不为空
			if (item.getClass() != type && !(item instanceof MsgArray))
				// 如果节点的类型不为期待的类型，且此节点不是数组，则需要新建
				needNewItem = true;
			if (item instanceof MsgArray && !item.name().equals(name))
				// 如果节点的类型是数组但其名称与期待的名称不同，则需要新建
				needNewItem = true;
		}

		if (needNewItem)
			// 新建节点
			try {
				item = type.newInstance();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

		// 设置节点属性
		item.setAttribute("name", name);
		item.m_parent = container;
		container.put(name, item);

		// 如果节点位于数组中则设置其isarray属性
		if (item.parent() instanceof MsgArray) {
			item.setAttribute("name", item.parent().getAttribute("name").str);
			item.setAttribute("isarray", true);
		}

		return item;
	}
}
