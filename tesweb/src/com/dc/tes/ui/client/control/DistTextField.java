package com.dc.tes.ui.client.control;

import com.dc.tes.ui.client.IHelperService;
import com.dc.tes.ui.client.IHelperServiceAsync;
import com.dc.tes.ui.client.model.IDistValidate;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.dc.tes.ui.client.common.ServiceHelper;

/**
 * 唯一性文本框控件
 * 
 * @author scckobe
 * 
 */
public class DistTextField extends TextField<String> {
	/**
	 * 原始值,用于判断是否进行唯一性判断的根据
	 * 若已修改才会传入后台进行异步的判断
	 */
	private String oldValue;
	
	/**
	 * 上一次验证的值
	 * 作为文本框的值发生变化才进行唯一性判断
	 */
	private String lastValue = "";
	/**
	 * 唯一性判断接口
	 */
	private IDistValidate validator;
	/**
	 * 唯一性错误提示信息
	 */
	private String failMsg;
	/**
	 * 上次验证的结果
	 */
	private boolean lastValidate = false;
	/**
	 * 是否进行强制验证
	 */
	private boolean isEnforce = false;
	/**
	 * 构造函数
	 * 
	 * @param validator 被验证的对象<T implement IDistValidate>
	 * @param oldValue  原始值
	 */
	
	public DistTextField(IDistValidate validator, String oldValue) {
		this(validator, oldValue, "", "");
	}

	/**
	 * 构造函数
	 * 
	 * @param validator 被验证的对象<T implement IDistValidate>
	 * @param oldValue  原始值
	 * @param labelMsg  文本框Label标签值
	 */
	public DistTextField(IDistValidate validator, String oldValue,
			String labelMsg) {
		this(validator, oldValue, labelMsg, "");
	}

	/**
	 * 构造函数
	 * 
	 * @param validator 被验证的对象<T implement IDistValidate>
	 * @param oldValue  原始值
	 * @param labelMsg  文本框Label标签值
	 * @param failMsg	唯一性错误提示信息
	 */
	public DistTextField(IDistValidate validator, String oldValue,
			String labelMsg, String failMsg) {
		super();
		setFieldLabel(labelMsg);
		setAllowBlank(false);
		setSelectOnFocus(true);
//		setAutoValidate(true);

		this.oldValue = oldValue;
		this.validator = validator;
		this.failMsg = failMsg;
	}

	/**
	 * 设置原始值
	 * 
	 * @param oldValue 原始值
	 */
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * 获得原始值
	 * @return 原始值
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * 设置唯一性失败错误信息
	 * @param failMsg 唯一性失败错误信息
	 */
	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
	}

	/**
	 * 获得唯一性失败错误信息
	 * @return 唯一性失败错误信息
	 */
	public String getFailMsg() {
		return failMsg;
	}
	
	/**
	 * 进行强制验证
	 */
	public void EnforceValidate()
	{
		isEnforce = true;
		validate();
		isEnforce = false;
	}


	
	@Override
	protected boolean validateValue(String value) {
		int length = value.length();
		if (length > super.getMaxLength()) {
			String error = "最大大度要求为:" + String.valueOf(super.getMaxLength());
			markInvalid(error);
			return false;
		}
		
		if (value.isEmpty()) {
			markInvalid(getFieldLabel() + "不能为空");
			lastValidate = false;
		} 
		else if (value.compareTo(oldValue) == 0) {
			lastValidate = true;
		}
		else if(lastValue.compareTo(value) != 0 || isEnforce){
			lastValue = value;
			IHelperServiceAsync helperService = ServiceHelper.GetDynamicService("helper", IHelperService.class);
			helperService.IsNameDistinct(validator, value,
					new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							lastValidate = false;
							markInvalid("验证服务失败，请与管理员联系");
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result)
							{
								lastValidate = true;
								clearInvalid();
							}
							else
							{
								lastValidate = false;
								markInvalid(failMsg.isEmpty() ? getFieldLabel()
										+ "要求唯一" : failMsg);
							}
						}
						
					});
			if(!lastValidate)
				markInvalid("唯一性验证中");
		}
		return lastValidate;
	}
	
//	@Override
//	public void setValue(String value) {
//		int length = value.length();
//		if (length > super.getMaxLength()) {
//	      super.setValue(lastValue);
//	      return ;
//		}
//		super.setValue(value);
//	}
	
//	@Override
//	protected void onRender(Element parent, int index) 
//	{
//		 super.onRender(parent, index);
//		 Element inputElement = super.getElement();
//		 int tmpLength = super.getMaxLength();
//		 if(tmpLength != 0)
//			 inputElement.setAttribute("maxlength", String.valueOf(tmpLength));
//	}
	
//	public boolean isValid(boolean preventMark) {
//	    if (disabled) {
//	      return true;
//	    }
//	    boolean restore = this.preventMark;
//	    this.preventMark = preventMark;
//	    boolean result = false;
//	    if (result) {
//	      activeErrorMessage = null;
//	    }
//	    this.preventMark = restore;
//	    return result;
//	  }
}
