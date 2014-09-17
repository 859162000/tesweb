package com.dc.tes.fcore.compare;

import java.util.HashMap;

import com.dc.tes.dom.DefaultForEachVisitor;
import com.dc.tes.dom.MsgField;

/**
 * 
 * @author songljb
 * 
 */
public class CompareForEachVisitor extends DefaultForEachVisitor {

	/**
	 * 容器,记录所有用户填充了预期结果的field
	 */
	protected HashMap<String, CompareFiled> m_comparefields = new HashMap<String, CompareFiled>();
	protected boolean expectcompare = true;
	protected int difference = 0;

	@Override
	public void Field(MsgField field) {
		if (this.expectcompare == true) {
			if (!field.value().isEmpty()) {
				String str = field.getAttribute("desc").getStr();
				CompareFiled cf = new CompareFiled(field.value(), (String) str);
				this.m_comparefields.put(field.dpath(), cf);
				difference++;
			}
		} else {
			String dpath = field.dpath();
			if (this.m_comparefields.get(dpath) != null) {
				String expectvalue = this.m_comparefields.get(dpath).value;
				String desc = this.m_comparefields.get(dpath).desc;
				field.setAttribute("expect_result", expectvalue);
				field.setAttribute("desc", desc);
				this.m_comparefields.remove(dpath);
				if (expectvalue.compareTo(field.value()) == 0) {
					difference--;
				}
			} else {
				field.setAttribute("expect_result", "");
			}
		}
	}

	public void begcompare() {
		this.expectcompare = false;
	}

}
