package com.dc.tes.dom.util;

import java.util.Map;
import java.util.Stack;

import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.StackForEachVisitor;

/**
 * 报文构造器
 * 
 * @author lijic
 * 
 */
public class DocBuilder {
	private static final String C_ID = MsgContainerUtils.C_InternalDomElementPrefix + "buiderID";
	/**
	 * 将被构造的文档对象
	 */
	private MsgDocument m_doc = new MsgDocument();
	/**
	 * 用于保存文档层次关系的栈
	 */
	private Stack<MsgStruct> m_parents = new Stack<MsgStruct>();

	/**
	 * 元素编号 在构造时该编号将作为节点的临时名称 以绕开isarray的判断问题
	 */
	private int m_id = 0;

	/**
	 * 初始化一个报文构造器
	 */
	public DocBuilder() {
		this.m_parents.push(this.m_doc = new MsgDocument());
	}

	/**
	 * 构造一个结构的开始部分
	 * 
	 * @param attrs
	 *            结构的属性列表
	 */
	public void BeginStru(Map<String, String> attrs) {
		MsgStruct stru = new MsgStruct();

		this.setAttributes(stru, attrs);
		stru.setAttribute(C_ID, this.m_id);

		this.m_parents.peek().put(String.valueOf(this.m_id), stru);

		this.m_parents.push(stru);

		this.m_id++;
	}

	/**
	 * 构造一个结构的结束部分
	 */
	public void EndStru() {
		//		this.m_parents.pop();
		EndStru(false);
	}

	/**
	 * 构造一个结构的结束部分（暂时只用于案例上传功能）
	 * 
	 * @param neddAbandon
	 *            当无子节点时是否正常标识符 如果neddAbandon为true，并且无子节点则删除该结构
	 */
	public void EndStru(boolean neddAbandon) {
		MsgStruct abStu = this.m_parents.peek();
		if (neddAbandon && abStu.size() == 0) {
			MsgContainer parent = abStu.parent();
			parent.removeItem(abStu);
			this.m_parents.pop();
		} else {
			this.m_parents.pop();
		}
	}

	/**
	 * 构造一个域
	 * 
	 * @param value
	 *            域的值
	 * @param attrs
	 *            域的属性列表
	 */
	public void Field(String value, Map<String, String> attrs) {
		MsgField field = new MsgField();

		this.setAttributes(field, attrs);
		field.setAttribute(C_ID, this.m_id);

		this.m_parents.peek().put(String.valueOf(this.m_id), field);

		field.set(value.trim());

		this.m_id++;
	}

	/**
	 * 导出构造好的文档对象 对于一个DocBuilder对象来说该方法只应被调用一次
	 * 
	 * @return 构造好的文档对象
	 */
	public MsgDocument Export() {
		this.buildArray();
		System.out.println(this.m_doc.toString());
		this.renameNode();
		MsgContainerUtils.ClearInternalAttributes(this.m_doc);
		return this.m_doc;
	}

	/**
	 * 工具函数 用于将属性列表写入MsgItem中
	 */
	private void setAttributes(MsgItem item, Map<String, String> attrs) {
		for (String key : attrs.keySet())
			item.setAttribute(key, attrs.get(key));
	}

	/**
	 * 将isarray="true"的项捏成数组
	 */
	private void buildArray() {
		this.m_doc.ForEach(new StackForEachVisitor() {
			@Override
			public void StruEnd(MsgStruct stru) {
				super.StruEnd(stru);
				if (stru.getAttribute("isarray").bool) {
					MsgContainerUtils.MoveToArray((MsgStruct) this.m_containers.peek(), stru.getAttribute(C_ID).str, stru.name());
					MsgContainerUtils.ResortArray((MsgArray) this.m_containers.peek().get(stru.name()));
				}
			}

			@Override
			public void Field(MsgField field) {
				if (field.getAttribute("isarray").bool) {
					MsgContainerUtils.MoveToArray((MsgStruct) this.m_containers.peek(), field.getAttribute(C_ID).str, field.name());
					MsgContainerUtils.ResortArray((MsgArray) this.m_containers.peek().get(field.name()));
				}
			}

			@Override
			public void ArrayStart(MsgArray array) {
				// 不进行栈操作
			}

			@Override
			public void ArrayEnd(MsgArray array) {
				// 不进行栈操作
			}
		});
	}

	/**
	 * 将各个项的名称改为name属性里的名称
	 */
	private void renameNode() {
		this.m_doc.ForEach(new StackForEachVisitor() {
			@Override
			public void StruEnd(MsgStruct stru) {
				super.StruEnd(stru);
				if (this.m_containers.peek() instanceof MsgStruct)
					MsgContainerUtils.Rename((MsgStruct) this.m_containers.peek(), stru.getAttribute(C_ID).str, stru.name());
			}

			@Override
			public void Field(MsgField field) {
				if (this.m_containers.peek() instanceof MsgStruct)
					MsgContainerUtils.Rename((MsgStruct) this.m_containers.peek(), field.getAttribute(C_ID).str, field.name());
			}
		});
	}
}
