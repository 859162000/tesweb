package com.dc.tes.ui.client.control;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class RequireTextField extends TextField<String>{
	public RequireTextField(String labelMsg) {
		super();
		setFieldLabel(labelMsg);
		setAllowBlank(false);
		setSelectOnFocus(true);
		setAutoValidate(true);
	}
	
	@Override
	protected boolean validateValue(String value) {
		int length = value.length();
		if (length > super.getMaxLength()) {
	      String error = "最大长度要求为:" + String.valueOf(super.getMaxLength());
	      markInvalid(error);
			return false;
//	      setValue(lastValue);
//	      return false;
	    }
		
		if (value.isEmpty()) {
			markInvalid(getFieldLabel() + "不能为空");
			return false;
		} 
		return super.validateValue(value);
	}
}
