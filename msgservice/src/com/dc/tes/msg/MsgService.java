package com.dc.tes.msg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.lf5.util.StreamUtils;

import com.dc.tes.dom.IForEachVisitor;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.pack.PackSpecification;
import com.dc.tes.msg.unpack.UnpackService;
import com.dc.tes.msg.unpack.UnpackSpecification;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.XmlUtils;

/**
 * 报文服务
 * 
 * @author lijic
 * 
 */
public class MsgService {
	/**
	 * 组包
	 * 
	 * @param doc
	 *            要被组包的报文数据
	 * @param spec
	 *            组包样式定义
	 * @param context
	 *            上下文
	 * @return 组出的字节流
	 */
	public static byte[] Pack(MsgDocument doc, PackSpecification spec, IContext context) {		
		
		if(spec.clearEmptyOptional) {			
			doc.ForEach(new IForEachVisitor() {

				@Override
				public void DocStart(MsgDocument doc) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void DocEnd(MsgDocument doc) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void StruStart(MsgStruct stru) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void StruEnd(MsgStruct stru) {
					// TODO Auto-generated method stub
					if(stru.isEmpty())
						stru.parent().removeItem(stru);
				}

				@Override
				public void ArrayStart(MsgArray array) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void ArrayEnd(MsgArray array) {
					// TODO Auto-generated method stub
					if(array.isEmpty())
						array.parent().removeItem(array);
				}

				@Override
				public void Field(MsgField field) {
					// TODO Auto-generated method stub
					if (field.value() == "") {
						field.parent().removeItem(field);
					}
				}			
			});
			//以下需在模板增加一个 optional字段
			/*
			doc.ForEach(new IForEachVisitor() {

				@Override
				public void DocStart(MsgDocument doc) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void DocEnd(MsgDocument doc) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void StruStart(MsgStruct stru) {
					// TODO Auto-generated method stub
					if(stru.getAttribute("optional").bool)
						stru.setAttribute("internal_clear", true);
					else 
						stru.setAttribute("internal_clear", false);
				}

				@Override
				public void StruEnd(MsgStruct stru) {
					// TODO Auto-generated method stub
					if(stru.getAttribute("internal_clear").bool) {
						stru.parent().removeItem(stru);
					}
				}

				@Override
				public void ArrayStart(MsgArray array) {
					// TODO Auto-generated method stub
					if(array.getAttribute("optional").bool)
						array.setAttribute("internal_clear", true);
					else 
						array.setAttribute("internal_clear", false);
				}

				@Override
				public void ArrayEnd(MsgArray array) {
					// TODO Auto-generated method stub
					if(array.getAttribute("internal_clear").bool) {
						array.parent().removeItem(array);
					}
				}

				@Override
				public void Field(MsgField field) {
					// TODO Auto-generated method stub
					if(field.value() != "") {
						if(field.parent().getAttribute("internal_clear").bool)
							field.parent().setAttribute("internal_clear", false);					
					}
					
					if(field.getAttribute("optional").bool
							&& field.value() == "") {
						field.parent().removeItem(field);
					}
				}				
			});		*/	
		}
		
		return PackService.PackItem(doc, spec, context);
	}

	/**
	 * 读取组包样式定义
	 * 
	 * @param s
	 *            指向一篇组包样式定义的输入流
	 */
	public static PackSpecification LoadPackSpecification(InputStream s) {
		if (s == null)
			throw new TESException(MsgErr.Pack.PackSpecificationStreamIsNull);

		try {
			return new PackSpecification(XmlUtils.LoadXml(s));
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.LoadPackSpecificationFail, ex);
		}
	}

	/**
	 * 读取组包样式定义
	 * 
	 * @param style
	 *            一段表示组包样式定义的xml字符串
	 */
	public static PackSpecification LoadPackSpecification(String style) {
		if (style == null)
			throw new TESException(MsgErr.Pack.PackSpecificationStringIsNull);

		try {
			return new PackSpecification(XmlUtils.LoadXml(style));
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.LoadPackSpecificationFail, style, ex);
		}
	}

	/**
	 * 拆包
	 * 
	 * @param bytes
	 *            报文字节流
	 * @param template
	 *            报文结构模板
	 * @param spec
	 *            拆包规则定义
	 * @param context
	 *            上下文
	 * @return 拆出的报文数据
	 */
	public static MsgDocument Unpack(byte[] bytes, MsgDocument template, UnpackSpecification spec, IContext context) {
		return UnpackService.Unpack(bytes, template, spec, context);
	}

	/**
	 * 读取拆包规则定义
	 * 
	 * @param s
	 *            指向一篇拆包规则定义的输入流
	 */
	public static UnpackSpecification LoadUnpackSpecification(InputStream s) {
		if (s == null)
			throw new TESException(MsgErr.Unpack.UnpackSpecificationStreamIsNull);

		try {
			return new UnpackSpecification(XmlUtils.LoadXml(s));
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.LoadUnpackSpecificationFail, ex);
		}
	}

	/**
	 * 读取拆包规则定义
	 * 
	 * @param rule
	 *            一段表示拆包规则定义的xml字符串
	 */
	public static UnpackSpecification LoadUnpackSpecification(String rule) {
		if (rule == null)
			throw new TESException(MsgErr.Unpack.UnpackSpecificationStringIsNull);

		try {
			return new UnpackSpecification(XmlUtils.LoadXml(rule));
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.LoadUnpackSpecificationFail, rule, ex);
		}
	}
	
	/**
	 * 用于调试
	 * @param content
	 * @param xml
	 * @param spec
	 * @param trancode
	 * @return
	 */
	public static MsgDocument UnpackByFile(String content, String xml, String spec, String trancode) {	    
		InputStream in = null;
	    File file = null;
	    
	    try {
		      file = new File(xml);
		      in = new FileInputStream(file);
		      MsgDocument template = MsgLoader.Load(in);
	
		      file = new File(spec);
		      in = new FileInputStream(file);
		      UnpackSpecification sp = LoadUnpackSpecification(in);
	
		      in = new FileInputStream(new File(content));
		      byte[] b = StreamUtils.getBytes(in);
	
		      return Unpack(b, template, sp, new MsgContext(trancode));
	    } catch (Exception localException){
	    }
	    return null;
	 }
	
	public static MsgDocument UnpackByFile(byte[] content, String xml, String spec, String trancode) {	    
		InputStream in = null;
	    File file = null;
	    
	    try {
		      file = new File(xml);
		      in = new FileInputStream(file);
		      MsgDocument template = MsgLoader.Load(in);
	
		      file = new File(spec);
		      in = new FileInputStream(file);
		      UnpackSpecification sp = LoadUnpackSpecification(in);
	
		      return Unpack(content, template, sp, new MsgContext(trancode));
	    } catch (Exception localException){
	    }
	    return null;
	 }

}
