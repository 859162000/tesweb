package com.dc.tes.msg.pack;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.util.DPathUtils;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.util.ElementType;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.type.BytePackage;
import com.dc.tes.msg.util.UsageStringUtils;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.dom.MsgStruct;

/**
 * 组包服务核心类
 * 
 * @author lijic
 * 
 */
public class PackService {
	
	private static final String C_InternalAttribute_OutputBytes = MsgContainerUtils.C_InternalDomElementPrefix + "m";

	//延迟组包标识位：空则没有域需要延迟组包，-1表示已经延迟处理，其他值表示下次组包后应该插入到整个组包的位置
	public static final String C_InternalAttribute_DelayPackFlag = MsgContainerUtils.C_InternalDomElementPrefix + "d";
	
	/**
	 * 将报文元素组包为字节流
	 * 
	 * @param item
	 *            报文元素
	 * @param spec
	 *            组包样式定义
	 * @param context
	 *            上下文
	 * @return 将该报文元素组包后得到的结果
	 */
	public static byte[] PackItem(MsgItem item, PackSpecification spec, IContext context) {
		if (item == null)
			throw new TESException(MsgErr.Pack.ItemIsNull);
		if (spec == null)
			throw new TESException(MsgErr.Pack.SpecIsNull);
		if (context == null)
			throw new TESException(MsgErr.Pack.ContextIsNull);

		try {
			// 建立一个字节包用作缓存 
			BytePackage p = new BytePackage();

			// 选择适合该报文元素的组包样式单元
			StyleUnit style = selectStyleUnit(item, spec);
			for (Segment segment : style.segments) {
				// 对该段进行组包
				byte[] bytes = segment.Pack(item, spec, context);

				// 判断该段是否需要回退
				if (!(segment instanceof ParamSegment && ((ParamSegment) segment).backspace > 0))
					// 该段是普通的段 将该段组包出的字节附加到字节包中
					p.Append(bytes);
				else {
					// 获取要回退的字节数
					int backspace = ((ParamSegment) segment).backspace;

					if (p.getLength() < backspace)
						throw new TESException(MsgErr.Pack.BackspaceTooMany, String.valueOf(backspace));

					// 该段是回退段 将字节包回退指定的字节
					p.Backspace(backspace);
				}
				
				//保存每组好一个域后的长度
			    if(context instanceof MsgContext) {
			    	((MsgContext) context).posOfMsgContainer = ((MsgContext) context).posOfMsgContainer + bytes.length;
			    	((MsgContext) context).posOfMsgDocument = ((MsgContext) context).posOfMsgDocument + bytes.length;
			    }
			    
				//假如第一次遇到延迟组包域，则忽略后面剩下的段组包
				if((segment instanceof ParamSegment 
						&& ((ParamSegment) segment).param.body.equalsIgnoreCase("d"))
						&& item.getDocument().getAttribute(PackService.C_InternalAttribute_DelayPackFlag).i
						!= -1) {				
					break;
				}
			}
			
			// 返回组包得到的字节流
			return p.getBytes();
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.PackItemFail, "path: " + item.dpath() + " item: " + item, ex);
		}
	}

	/**
	 * 将报文元素容器的各个子项组包为字节流
	 * 
	 * @param container
	 *            报文元素容器
	 * @param spec
	 *            组包上下文
	 * @return 将该报文元素窝的各个子项组包后得到的字节流
	 */
	public static Value PackMember(MsgContainer container, PackContext context) {
		if (!(container.getAttribute(C_InternalAttribute_OutputBytes) == Value.empty))
			return container.getAttribute(C_InternalAttribute_OutputBytes);

		BytePackage p = new BytePackage();
		for (MsgItem item : container) {
			if(item instanceof MsgStruct)
				((MsgContext) context.context).posOfMsgContainer = 0;
			
			byte[] pack = PackService.PackItem(item, context.spec, context.context);
			p.Append(pack);
			String attr = "___"+item.name();
			container.setAttribute(attr, pack);		
		} 
		
		//在其父结构组包完成返回前，完成子域的延迟组包。
		if(!(container.getDocument().getAttribute(C_InternalAttribute_DelayPackFlag) == Value.empty) && 
				!(container.getDocument().getAttribute(C_InternalAttribute_DelayPackFlag).i == -1)) 				
			PackService.PackDelayField(container,context.spec,context.context,p);
		
		container.setAttribute(C_InternalAttribute_OutputBytes, new Value(p.getBytes()));
		return container.getAttribute(C_InternalAttribute_OutputBytes);
	}
	
	
	public static void PackDelayField(MsgItem item, PackSpecification spec, IContext context, BytePackage p) {

		Value v = item.getDocument().getAttribute(C_InternalAttribute_DelayPackFlag);		
		int pos = v.i;
		
		//获取延迟组包的域
		String dpath = item.getDocument().getAttribute("dpathOfField").str;
		MsgItem field = item.getDocument().SelectSingleField(dpath);
		
		item.getDocument().setAttribute(C_InternalAttribute_DelayPackFlag, -1);
		byte[] bytes = PackService.PackItem(field, spec, context);
	
		p.Insert(bytes, pos);
	}

	/**
	 * 将指定的属性值转换为别名 如果未定义针对该属性的别名 则返回属性值本身
	 * 
	 * @param v
	 *            属性的值
	 * @param item
	 *            被组包的元素
	 * @param spec
	 *            组包样式定义
	 * @return
	 */
	public static Value TranslateAlias(Value v, MsgItem item, PackSpecification spec) {
		ElementType type = spec.types.CheckType(item, item.parent());

		for (Alias alias : spec.alias)
			if (alias.usage != null && ArrayUtils.contains(alias.usage, type))
				if (Pattern.matches(alias.pattern, v.str))
					return new Value(alias.value);

		return v;
	}

	/**
	 * 执行指定名称的组包脚本
	 * 
	 * @param name
	 *            组包脚本名称
	 * @param item
	 *            被组包的元素
	 * @param context
	 *            组包上下文
	 * @return 脚本的执行结果
	 */
	public static Value ExecScript(String name, MsgItem item, PackContext context) {
		for (PackScript script : context.spec.scripts)
			if (script.name.equals(name))
				return script.Exec(item, context);
		throw new TESException(MsgErr.Pack.ScriptNotFound, name);
	}

	/**
	 * 计算对某个参数的引用
	 * 
	 * @param item
	 *            被组包的元素
	 * @param refIndex
	 *            引用的参数段编号
	 * @param context
	 *            组包上下文
	 * @return 被引用的参数段组包出的字节流
	 */
	public static byte[] CalcReference(MsgItem item, int refIndex, PackContext context) {
		int i = 0;
		// 遍历与该报文元素对应的StyleUnit的段列表，找出第refIndex个参数段
		for (Segment segment : selectStyleUnit(item, context.spec).segments)
			if (segment instanceof ParamSegment) {
				// 找到了第refIndex个参数段
				if (i == refIndex) {
					ParamSegment _segment = (ParamSegment) segment;
					if (_segment.param == context.param)
						throw new TESException(MsgErr.Pack.SelfReference, String.valueOf(refIndex));

					byte[] bytes = _segment.Pack(item, context.spec, context.context);
					if (_segment.backspace > 0)
						throw new TESException(MsgErr.Pack.ReferenceToBackspaceSegment, String.valueOf(refIndex));

					return bytes;
				}
				i++;
			}

		// 未找到被引用的段
		throw new TESException(MsgErr.Pack.ReferenceNotFound, String.valueOf(refIndex));
	}

	/**
	 * 选择一个适合指定报文元素的组包样式单元
	 * 
	 * @param item
	 *            要被组包的报文元素
	 * @param spec
	 *            组包样式定义
	 * @return 适合该报文元素的组包样式单元
	 */
	private static StyleUnit selectStyleUnit(MsgItem item, PackSpecification spec) {
		// 首先查看是不是有special
		for (String key : spec.specials.keySet())
			if (DPathUtils.IsDPathMatch(item.dpath(), key))
				return spec.specials.get(key);

		// 根据元素的层次类型选择合适的组包样式单元
		ElementType type = spec.types.CheckType(item, item.parent());
		if (type != null){
			List <StyleUnit> suSet = new ArrayList<StyleUnit>() ;
			for (StyleUnit style : spec.styles)
				if (style.type == type)
					suSet.add(style);	    			
			StyleUnit su = UsageStringUtils.MatchUsage(suSet, item);
			return su;
					
		}
		throw new TESException(MsgErr.Pack.NoSuitableStyleUnit, item.toString());
	}


	/////////////////////s
	// 与旧代码兼容
	/////////////////////
	/**
	 * use "MsgService.LoadPackSpecification(stream)"<br/>
	 * 该方法将在2010/4/30删除
	 */
	@Deprecated
	public static PackSpecification LoadPackSpecification(InputStream stream) {
		return MsgService.LoadPackSpecification(stream);
	}

	/**
	 * use "MsgService.LoadPackSpecification(style)"<br/>
	 * 该方法将在2010/4/30删除
	 */
	@Deprecated
	public static PackSpecification LoadPackSpecification(String style) {
		return MsgService.LoadPackSpecification(style);
	}

	/**
	 * use "MsgService.Pack()"<br/>
	 * 该方法将在2010/4/30删除
	 */
	@Deprecated
	public static byte[] PackDocument(MsgDocument doc, PackSpecification spec, IContext context) {
		return MsgService.Pack(doc, spec, context
				);
	}
}
