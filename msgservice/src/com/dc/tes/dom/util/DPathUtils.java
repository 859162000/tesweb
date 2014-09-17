package com.dc.tes.dom.util;

import java.util.ArrayList;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import com.dc.tes.dom.DefaultForEachVisitor;
import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;

/**
 * 与DPath相关的工具类
 * 
 * @author lijic
 * 
 */
public class DPathUtils {
	/**
	 * 判断两个DPath是否匹配
	 * 
	 * @param dpath
	 *            要与模板进行匹配的dpath
	 * @param template
	 *            作为模板的dpath
	 * @return 如果两个DPath匹配则返回true，否则返回false
	 */
	public static boolean IsDPathMatch(String dpath, String template) {
		String[] a = dpath.split("\\.");
		String[] b = template.split("\\.");

		if (a.length != b.length)
			return false;

		for (int i = 0; i < a.length; i++)
			if (!a[i].equalsIgnoreCase(b[i]) && !b[i].equals("*"))
				return false;
		return true;
	}

	/**
	 * 获取一个dpath的名称部分
	 * 
	 * @param dpath
	 * @return
	 */
	public static String getName(String dpath) {
		if (dpath == null)
			throw new NullArgumentException("dpath");

		String[] segments = dpath.split("\\.");
		return segments[segments.length - 1];
	}

	/**
	 * 
	 * @param dpath
	 * @param segment
	 * @return
	 */
	public static String Append(String dpath, String segment) {
		if (StringUtils.isEmpty(dpath))
			return segment;

		return dpath + "." + segment;
	}

	/**
	 * 选择一个节点
	 * 
	 * @param item
	 *            作为根的节点
	 * @param dpath
	 *            DPath 如果此值为空或null 则返回根节点 此处的dpath支持{root}和{parent}语法
	 * @return 被选中的节点。如果没有这样的一个节点，则返回null
	 */
	public static MsgItem SelectSingleNode(MsgItem item, String dpath) {
		if (StringUtils.isEmpty(dpath))
			return item;

		String[] segments = dpath.split("\\.");
		for (String segment : segments) {
			if (item == null)
				return null;
			else if (segment.equalsIgnoreCase("{root}") || segment.equalsIgnoreCase("{r}"))
				item = item.root();
			else if (segment.equalsIgnoreCase("{parent}") || segment.equalsIgnoreCase("{p}"))
				item = item.parent();
			else if (item instanceof MsgContainer) {
				item = ((MsgContainer) item).get(segment);
			} else
				return null;
		}

		return item;
	}

	/**
	 * 选择一批节点
	 * 
	 * @param item
	 *            作为根的节点
	 * @param dpath
	 *            DPath 此处的dpath支持*语法
	 * @return 由被选中的节点组成的数组。如果没有这样的节点，则返回一个空数组
	 */
	public static MsgItem[] SelectNodes(MsgItem item, final String dpath) {
		final ArrayList<MsgItem> items = new ArrayList<MsgItem>();
		if (StringUtils.isEmpty(dpath))
			return items.toArray(new MsgItem[0]);

		item.ForEach(new DefaultForEachVisitor() {
			@Override
			public void StruEnd(MsgStruct stru) {
				if (IsDPathMatch(stru.dpath(), dpath))
					items.add(stru);
			}

			@Override
			public void ArrayEnd(MsgArray array) {
				if (IsDPathMatch(array.dpath(), dpath))
					items.add(array);
			}

			@Override
			public void Field(MsgField field) {
				if (IsDPathMatch(field.dpath(), dpath))
					items.add(field);
			}
		});

		return items.toArray(new MsgItem[] {});
	}
}
