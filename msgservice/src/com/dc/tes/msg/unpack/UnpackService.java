package com.dc.tes.msg.unpack;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.unpack.filter.Filter;
import com.dc.tes.msg.util.UsageStringUtils;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.type.Wrapper;

/**
 * 拆包服务核心类
 * 
 * @author lijic
 * 
 */
public class UnpackService {
	private final static Log log = LogFactory.getLog(UnpackService.class);

	private static int s_id;

	/**
	 * 对报文进行拆包 该函数是整个拆包流程的入口点
	 * 
	 * @param bytes
	 *            报文字节流
	 * @param template
	 *            模板 可以为null
	 * @param spec
	 *            拆包规则定义
	 * @param context
	 *            上下文
	 * @return
	 */
	public static MsgDocument Unpack(byte[] bytes, MsgDocument template, UnpackSpecification spec, IContext context) {
		if (spec.discardStru)
			template = null;

		// 首先调用过滤器 对报文字节进行过滤
		try {
			for (Filter filter : spec.filters)
				bytes = filter.Clean(bytes);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.FilterFail, ex);
		}

		log.debug("过滤后的报文");
		log.debug(RuntimeUtils.PrintHex(bytes, spec.encoding));

		// 构造报文根元素
		MsgDocument doc = new MsgDocument();

		// 取出根元素的拆包规则单元
		try {
			// 对根节点进行拆包
			int len = UnpackItem(bytes, 0, doc, new Wrapper<MsgItem>(template), spec.ruleRoot, spec, context);

			// 判断是否成功拆包（拆包用掉的字节数是否和报文的总字节数相同）
			if (len != bytes.length)
				throw new TESException(MsgErr.Unpack.UnpackFail_LenMismatch, "total: " + bytes.length + " unpack: " + len);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.UnpackFail_Exception, ex);
		}
		
		//针对400返回汉字,去掉前后两个字符,重新解析XML时，这两个是不可解析字符，会报错
		doc.ForEach(new ISimpleForEachVisitor() {

			@Override
			public void Visit(ForEachSource source, MsgItem item) {
				// TODO Auto-generated method stub
				if(source.equals(ForEachSource.Field)) {
					MsgField field = (MsgField)item;				
					String result1 = field.value().replaceAll(new String(HexStringUtils.FromHexString("0E")), "");
					String result2 = result1.replaceAll(new String(HexStringUtils.FromHexString("0F")), "");
					field.set(result2);
					//这里也会保存了解析不了的字符
					field.setAttribute("__tes__internal__v", "");
				} else if(source.equals(ForEachSource.StruStart)) {
					MsgStruct struct = (MsgStruct)item;
					struct.setAttribute("__tes__internal__m", "");
				}
			}
			
		});
		

		// 返回拆包结果 因为拆包过程中在doc中保存了很多内部属性，并且各个节点的id和name不对应，并且在很多情况下对数组的处理也不正确（没有将该捏成数组的捏成数组），所以需要进行清理。
		// 最简便的清理方法(从代码长度上来看)就是将这个doc转成xml形式再读取一遍
		return MsgLoader.LoadXml(doc.toString(true, 0));
	}

	/**
	 * 处理字节流，尝试拆出一个报文元素
	 * 
	 * @param bytes
	 *            报文字节流
	 * @param item
	 *            要将拆包出的数据写入的报文元素
	 * @param context
	 *            拆包上下文
	 * @return 拆出的报文元素，如果尝试失败则返回null
	 */
	public static int UnpackItem(byte[] bytes, int start, MsgItem item, Wrapper<MsgItem> template, RuleUnit rule, UnpackSpecification spec, IContext context) {
		log.debug("[UnpackItem]尝试使用拆包规则单元：" + rule + " pos=" + start + SystemUtils.LINE_SEPARATOR + "bytes=" + new String(bytes, start, bytes.length - start, spec.encoding));

		int pos = start;
		// 对拆包格式中的每个段进行匹配
		for (Segment segment : rule.segments) {
			if(!segment.toString().isEmpty())
				log.debug("进行段匹配 " + segment + " pos=" + pos);

			int len = segment.Match(bytes, item, template, pos, spec, context);

			if (len != -1) {
				// 如果成功匹配则将拆包位置后移已经匹配出的字节数
				if(!segment.toString().isEmpty())
					log.debug("段匹配成功 " + segment + " len=" + len + " match=[" + new String(bytes, pos, len, spec.encoding) + "]");
				pos += len;
			} else {
				log.debug("段匹配失败 " + segment);
				// 匹配失败 表示拆包失败
				return -1;
			}
		}

		// 返回拆包结果和用掉的字节数
		log.debug("拆包单元匹配成功：" + item);
		return pos - start;
	}

	/**
	 * 对某个MsgContainer的子元素进行拆包
	 * 
	 * @param bytes
	 *            待拆包的字节流
	 * @param start
	 *            拆包位置
	 * @param item
	 *            存放拆出的数据的报文元素
	 * @param template
	 *            模板
	 * @param spec
	 *            拆包规则
	 * @return 返回拆出的对象和用掉的字节数 如果给定的拆包规则单元尝试失败 则返回-1
	 */

	public static int UnpackMember(byte[] bytes, int start, int length, MsgContainer item, MsgContainer template, UnpackSpecification spec, IContext context) {
		int pos = start;

		log.debug("[UnpackMember] pos=" + start + " bytes=" + new String(bytes, start, bytes.length - start, spec.encoding));
		log.debug("准备拆结构");
		log.debug("该段数据 " + new String(bytes, start, bytes.length - start, spec.encoding));
		log.debug("将匹配以下模板 " + template);

		
		int templateId = 0;
		// 循环进行拆包
		while (true) {
			// 如果当前拆包位置与预先判断的本段长度相等 则认为之前判断的长度是准确的 拆包完毕
			if ((pos - start) == length) {
				log.debug("[UnpackMember] 成功：pos == length");
				return length;
			}

			boolean unpackFlag = false; // 表示当前报文元素是否拆包成功的标志

			// 选择合适的模板
			MsgItem _template = template == null ? null : template.get(templateId);

			//遍历spec中的所有RuleUnit		
			for (RuleUnit _rule : spec.rules) {
				// 创建报文元素实例
				MsgItem child;
				if (_template != null && _rule.type.Check(_template, _template.parent())) {
					child = InstanceCreater.CreateInstance(_template.getClass());
					for (String key : _template.getAttributes().keySet())
						child.setAttribute(key, new Value(_template.getAttribute(key)));
				} else
					child = InstanceCreater.CreateInstance(_template.getClass());
				
				//如果被选中的RuleUnit不符合当前的报文层次 则尝试使用该RuleUnit进行拆包
				//child代表即将拆包的是域、结构还是数组
				//item代表child在什么容器下
//				if (!(_rule.type.Check(child, item) && UsageStringUtils.Matches(_rule.usage, _template)))
//					continue;
				if (!(_rule.type.Check(child, item))) {
					continue;
				}
				
				//把所有相同层次类型的规则找出来，匹配最大usage
				List<RuleUnit> suSet = new ArrayList<RuleUnit>();
				for (RuleUnit style : spec.rules)
					if (style.type == _rule.type)
						suSet.add(style);
				
				//保证总有一个成功?
				RuleUnit rule = UsageStringUtils.UnPackMatchUsage(suSet, _template);
				
				int len = UnpackItem(bytes, pos, child, new Wrapper<MsgItem>(_template), rule, spec, context);

				if (len != -1) {
					// 拆包单元匹配成功						
					pos += len;
					templateId++;
					unpackFlag = true;

					// 判断是否已经有同名的元素 如果存在同名的元素则认为这两个元素应该位于同一个数组内
					final MsgItem _child = child;
					item.ForEach(new ISimpleForEachVisitor() {
						@Override
						public void Visit(ForEachSource source, MsgItem item) {
							if (item.name().equals(_child.name())) {
								item.setAttribute("isarray", true);
								_child.setAttribute("isarray", true);
							}
						}
					});
					// 将拆出的元素放到容器中
					item.put(String.valueOf(s_id++), child);

					break;
				}
			}

			// 对所有的拆包规则单元的尝试都失败了 表示已经无法继续拆下去了 此时认为达到了结构的结束点
			if (!unpackFlag)
				return pos - start;
		}
	}

	/**
	 * 运行拆包脚本
	 * 
	 * @param name
	 *            脚本名称
	 * @return 脚本运行结果
	 */
	public static boolean ExecUnpackScript(String name, Value value, MsgItem item, UnpackContext context) {
		for (UnpackScript script : context.spec.scripts)
			if (script.name.equals(name))
				return script.Exec(item, context);
		throw new TESException(MsgErr.Unpack.ScriptNotFound, name);
	}
}
