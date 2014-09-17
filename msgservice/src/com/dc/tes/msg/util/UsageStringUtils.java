package com.dc.tes.msg.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.dc.tes.msg.pack.StyleUnit;
import com.dc.tes.msg.unpack.RuleUnit;
import org.apache.commons.lang.StringUtils;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.util.type.Pair;

/**
 * 解析组包样式文件和拆包规则文件中usage属性的工具类
 * 
 * @author lijic
 * 
 */
public class UsageStringUtils {
	/**
	 * 判断给定的usage是否符合指定的报文元素
	 * 
	 * @param usage
	 *            usage字符串
	 * @param item
	 *            报文元素
	 * @return 是否匹配
	 */
	public static boolean Matches(String usage, MsgItem item) {
		if (item == null || StringUtils.isEmpty(usage))
			return true;

		for (String rule : usage.split(";")) {
			String n = rule.contains("=") ? rule.split("=")[0] : rule;
			String v = rule.contains("=") ? rule.substring(n.length() + 1) : "";

			if (!item.getAttribute(n).str.equals(v))
				return false;
		}

		return true;
	}

	/**
	 * 根据报文元素本身的属性 从给定的usage列表中选择出一个合适的usage
	 * 
	 * @param usages
	 *            usage列表
	 * @param item
	 *            报文元素
	 * @return 合适的usage
	 */
	public static String MatchUsageString(Set<String> usages, MsgItem item) {
		Map<String, Value> attr = item.getAttributes();
		Pair<Integer, String> maxMatch = new Pair<Integer, String>(0, null);

		// 遍历列表，将item的属性与usage属性进行匹配
		for (String usage : usages) {
			Map<String, String> rules = UsageStringUtils.ParseUsageString(usage);
			if (rules == null) // rules=null为默认样式 不对其进行匹配
				continue;

			boolean match = true;
			for (String k : rules.keySet()) {
				boolean match2 = true;

				for (String v : rules.get(k).split("\\|")) {
					if (!attr.containsKey(k))
						match2 = v.length() == 0;// 如果属性不存在，则认为此属性的值为空字符串，通过判断v是否为空来进行判断
					else
						match2 = attr.get(k).str.equalsIgnoreCase(v);
					if (match2)
						break;
				}
				if (!match2) {
					match = false;
					break;
				}
			}

			if (match)
				if (maxMatch.getA() < rules.size())
					maxMatch = new Pair<Integer, String>(rules.size(), usage);
		}

		return maxMatch.getB();
	}
	
	/**
	 * 组包时通过匹配最大的usage来选择组包规则
	 * @param styles
	 * @param item
	 * @return
	 */
	public static StyleUnit MatchUsage(List<StyleUnit> styles, MsgItem item) {
		Map<String, Value> attr = item.getAttributes();
		Pair<Integer, StyleUnit> maxMatch = new Pair<Integer, StyleUnit>(0, styles.get(0));

		// 遍历列表，将item的属性与usage属性进行匹配
		for (StyleUnit style : styles) {
			Map<String, String> rules = UsageStringUtils.ParseUsageString(style.getUsage());
			if (rules == null) // rules=null为默认样式 不对其进行匹配
				continue;

			boolean match = true;
			for (String k : rules.keySet()) {
				boolean match2 = true;

				for (String v : rules.get(k).split("\\|")) {
					if (!attr.containsKey(k))
						match2 = v.length() == 0;// 如果属性不存在，则认为此属性的值为空字符串，通过判断v是否为空来进行判断
					else
						match2 = attr.get(k).str.equals(v);
					if (match2)
						break;
				}
				if (!match2) {
					match = false;
					break;
				}
			}

			if (match)
				if (maxMatch.getA() < rules.size())
					maxMatch = new Pair<Integer, StyleUnit>(rules.size(), style);
		}

		return maxMatch.getB();
	
	}

	/**
	 * 拆包时通过匹配最大的usage来选择组包规则
	 * @param styles
	 * @param item
	 * @return
	 */
	public static RuleUnit UnPackMatchUsage(List<RuleUnit> styles, MsgItem item) {
		Map<String, Value> attr = item.getAttributes();
		Pair<Integer, RuleUnit> maxMatch = new Pair<Integer, RuleUnit>(-1, styles.get(0));

		// 遍历列表，将item的属性与usage属性进行匹配
		for (RuleUnit style : styles) {
			Map<String, String> rules = UsageStringUtils.ParseUsageString(style.getUsage());		
			//rules=null为默认样式 不对其进行匹配,算匹配成功0次
			if (rules == null) {
				if (maxMatch.getA() < 0)
					maxMatch = new Pair<Integer, RuleUnit>(0, style);
				continue;
			}

			boolean match = true;
			for (String k : rules.keySet()) {
				boolean match2 = true;
				
				for (String v : rules.get(k).split("\\|")) {
					if (!attr.containsKey(k))
						match2 = v.length() == 0;// 如果属性不存在，则认为此属性的值为空字符串，通过判断v是否为空来进行判断
					else
						match2 = attr.get(k).str.equals(v);
					if (match2)
						break;
				}
				if (!match2) {
					match = false;
					break;
				}
			}
			//匹配成功
			if (match) {
				//比较 上次匹配成功的usage的个数(usage="format=XX;type=XX",成功的话为2)
				//与 本次 成功的个数 rules.size()可以表示这个数
				if (maxMatch.getA() < rules.size())
					//替换该规则匹配成功了多少个usage(usage="format=XX;type=XX",成功的话为2)
					maxMatch = new Pair<Integer, RuleUnit>(rules.size(), style);		
			}
		}

		return maxMatch.getB();
	
	}
	
	private static Map<String, String> ParseUsageString(String usage) {
		if (usage == null || usage.length() == 0)
			return null;

		HashMap<String, String> map = new HashMap<String, String>();
		for (String rule : usage.split(";")) {
			String attrName = rule.substring(0, rule.indexOf('='));
			String attrValue = rule.substring(rule.indexOf('=') + 1);

			map.put(attrName, attrValue);
		}
		return map;
	}
}
