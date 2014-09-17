package com.dc.tes.exception;

/**
 * 由报文服务定义的错误码
 * 
 * @author lijic
 * 
 */
public class MsgErr {
	public static final ErrCode FormatStringSyntaxError = new ErrCode("MSG001", "格式字符串没有正常结束");
	public static final ErrCode SimpleFormatStringContainsParam = new ErrCode("MSG002", "在简单格式字符串中不应含有格式描述符");
	public static final ErrCode CreateNullValueObject = new ErrCode("MSG003", "试图将null值设给Value对象");

	/** 一些与dom处理相关的错误 */
	public static class Dom {
		public static final ErrCode NoNameAttribute = new ErrCode("MSGD01", "元素没有name属性");
		public static final ErrCode UnknownElementName = new ErrCode("MSGD02", "不支持的元素名称");
		public static final ErrCode SaxFail = new ErrCode("MSGD03", "读取报文xml时发生错误");
		public static final ErrCode RenameNodeFail = new ErrCode("MSGD04", "重命名报文节点时发生错误_已存在指定名称的节点");
		public static final ErrCode NullAttribute = new ErrCode("MSGD05", "试图将null值赋给节点的属性");
		public static final ErrCode NullAttributes = new ErrCode("MSGD05", "试图将null列表值赋给节点的属性列表");
		public static final ErrCode NotAllContains_ArrayIndex_Flag = new ErrCode("MSGD06", "在对数组的元素进行重排序时，发现该数组中不是所有的元素都有ArrayIndex属性");
		public static final ErrCode NullTranstruct = new ErrCode("MSGD07","交易模板为空");
	}

	/** 一些与组包相关的错误 */
	public static class Pack {
		public static final ErrCode LoadPackSpecificationFail = new ErrCode("MSGP01", "读取组包配置时发生错误");
		public static final ErrCode PackSpecificationStreamIsNull = new ErrCode("MSGP02", "用于提供组包配置的输入流为null");
		public static final ErrCode PackSpecificationStringIsNull = new ErrCode("MSGP03", "用于提供组包配置的字符串为null");
		public static final ErrCode EncodingNotFound = new ErrCode("MSGP04", "必须在Document节点的encoding属性中指定整篇报文的编码");
		public static final ErrCode EncodingNotSupported = new ErrCode("MSGP05", "当前系统不支持指定的编码");
		public static final ErrCode SpecialNoTarget = new ErrCode("MSGP06", "未在Special中指定target属性");
		public static final ErrCode NoEnoughParam = new ErrCode("MSGP07", "未向格式字符串提供足够的参数");
		public static final ErrCode PackItemFail = new ErrCode("MSGP08", "对元素进行组包时发生错误");
		public static final ErrCode BackspaceUnparseable = new ErrCode("MSGP09", "无法解析回退段的回退长度");
		public static final ErrCode BackspaceMustPositive = new ErrCode("MSGP10", "回退段的回退长度必须为正数");
		public static final ErrCode ItemIsNull = new ErrCode("MSGP11", "被组包的报文元素为null");
		public static final ErrCode SpecIsNull = new ErrCode("MSGP12", "组包样式定义为null");
		public static final ErrCode ContextIsNull = new ErrCode("MSGP13", "上下文为null");
		public static final ErrCode NoSuitableStyleUnit = new ErrCode("MSGP14", "找不到适合指定报文元素的组包样式单元");
		public static final ErrCode BackspaceTooMany = new ErrCode("MSGP15", "要回退的长度大于目前该报文元素已经组包出的总字节数");
		public static final ErrCode CalculatorNotFound = new ErrCode("MSGP16", "未找到对应于指定的格式参数的Calculator");
		public static final ErrCode ParamPostfixNotFound = new ErrCode("MSGP17", "未向格式参数提供后缀");
		public static final ErrCode ParamShouldNotOnField = new ErrCode("MSGP18", "格式参数不能应用于域元素");
		public static final ErrCode ParamShouldNotOnArray = new ErrCode("MSGP19", "格式参数不能应用于数组元素");
		public static final ErrCode ParamShouldNotOnStruct = new ErrCode("MSGP20", "格式参数不能应用于结构元素");
		public static final ErrCode ParamWithPostfixShouldNotOnField = new ErrCode("MSGP21", "带后缀的格式参数不能应用于域元素");
		public static final ErrCode ParamWithPostfixShouldNotOnArray = new ErrCode("MSGP22", "带后缀的格式参数不能应用于数组元素");
		public static final ErrCode ParamWithPostfixShouldNotOnStruct = new ErrCode("MSGP23", "带后缀的格式参数不能应用于结构元素");
		public static final ErrCode AttributeNotFound = new ErrCode("MSGP24", "未找到指定的属性");
		public static final ErrCode ContextNameNotFound = new ErrCode("MSGP25", "未找到指定的上下文名称");
		public static final ErrCode FunctionNameUnparseable = new ErrCode("MSGP26", "给定的外部函数名称无法解析");
		public static final ErrCode ClassNotFound = new ErrCode("MSGP27", "给定的外部函数所在的类找不到");
		public static final ErrCode FunctionNotFound = new ErrCode("MSGP28", "给定的外部函数找不到");
		public static final ErrCode CallFunctionFail = new ErrCode("MSGP29", "调用外部函数时发生错误");
		public static final ErrCode FunctionResultTypeError = new ErrCode("MSGP30", "外部函数的返回值的类型不正确");
		public static final ErrCode VTargetNull = new ErrCode("MSGP31", "使用带后缀的v参数指定的报文元素不存在或不是一个域");
		public static final ErrCode MTargetNull = new ErrCode("MSGP32", "使用带后缀的m参数指定的报文元素不存在");
		public static final ErrCode MTargetNotContainer = new ErrCode("MSGP33", "使用带后缀的m参数指定的报文元素不存在或不是一个结构或数组）");
		public static final ErrCode ScriptNameNull = new ErrCode("MSGP34", "未对组包脚本命名");
		public static final ErrCode ScriptCompileFail = new ErrCode("MSGP35", "组包脚本中存在语法错误");
		public static final ErrCode ScriptNotFound = new ErrCode("MSGP36", "未找到指定名称的组包脚本");
		public static final ErrCode ScriptExecFail = new ErrCode("MSGP37", "组包脚本执行失败");
		public static final ErrCode ProcessorNotFound = new ErrCode("MSGP38", "未找到指定的Processor，这通常表示格式字符串中存在错误（是否未使用%%代替%）");
		public static final ErrCode InvalidValue = new ErrCode("MSGP39", "无法对被组包的值进行合理的解释");
		public static final ErrCode InvalidIntLength = new ErrCode("MSGP40", "指定的整数长度不合理。只能为1、2、3、4或8");
		public static final ErrCode InvalidFloatLength = new ErrCode("MSGP41", "指定的浮点数长度不合理。只能为4或8");
		public static final ErrCode NullCalculateResult = new ErrCode("MSGP42", "由Calculator计算出的值为null");
		public static final ErrCode RefIndexNotInteger = new ErrCode("MSGP43", "向r参数指定的引用位置无法被解析为一个整数");
		public static final ErrCode RefIndexMustPositive = new ErrCode("MSGP44", "向r参数指定的引用位置不能为负数");
		public static final ErrCode SelfReference = new ErrCode("MSGP45", "引用段不能引用自身");
		public static final ErrCode ReferenceToBackspaceSegment = new ErrCode("MSGP46", "试图引用一个回退段");
		public static final ErrCode ReferenceNotFound = new ErrCode("MSGP47", "没有找到被引用的段");
		public static final ErrCode UnsupportedParamPrefix_Calculator_Calculate = new ErrCode("MSGP48", "在Calculator::Calculate()中遇到了不被支持的前缀类型");
	}

	/** 一些与拆包相关的错误 */
	public static class Unpack {
		public static final ErrCode LoadUnpackSpecificationFail = new ErrCode("MSGU01", "读取拆包配置时发生错误");
		public static final ErrCode UnpackSpecificationStreamIsNull = new ErrCode("MSGU02", "用于提供拆包配置的输入流为null");
		public static final ErrCode UnpackSpecificationStringIsNull = new ErrCode("MSGU03", "用于提供拆包配置的字符串为null");
		public static final ErrCode EncodingNotFound = new ErrCode("MSGU04", "必须在Document节点的encoding属性中指定整篇报文的编码");
		public static final ErrCode EncodingNotSupported = new ErrCode("MSGU05", "当前系统不支持指定的编码");
		public static final ErrCode NoEnoughParam = new ErrCode("MSGU06", "未向格式字符串提供足够的参数");
		public static final ErrCode UnsupportedFilter = new ErrCode("MSGU07", "找不到指定的过滤器类型");
		public static final ErrCode InitializeFilterFail = new ErrCode("MSGU08", "初始化过滤器时发生错误");
		public static final ErrCode FilterArgumentNotFound = new ErrCode("MSGU09", "找不到需要为过滤器提供的参数");
		public static final ErrCode LoadFilterArgumentError = new ErrCode("MSGU10", "为过滤器提供的参数存在错误");
		public static final ErrCode RegexGroupUnparseable = new ErrCode("MSGU11", "无法解析正则表达式的编组序号");
		public static final ErrCode RegexGroupMustPositive = new ErrCode("MSGU12", "正则表达式编组序号不能为负数");
		public static final ErrCode FunctionNameUnparseable = new ErrCode("MSGU13", "给定的外部函数名称无法解析");
		public static final ErrCode Filter_ClassNotFound = new ErrCode("MSGU14", "给定的外部函数所在的类找不到");
		public static final ErrCode Filter_FunctionNotFound = new ErrCode("MSGU15", "给定的外部函数找不到");
		public static final ErrCode Filter_CallFunctionFail = new ErrCode("MSGU16", "调用外部函数时发生错误");
		public static final ErrCode Filter_ScriptCompileFail = new ErrCode("MSGU17", "过滤器脚本中存在语法错误");
		public static final ErrCode Filter_ScriptExecFail = new ErrCode("MSGU18", "过滤器脚本执行失败");
		public static final ErrCode FilterFail = new ErrCode("MSGU19", "对报文字节流进行过滤时发生错误");
		public static final ErrCode ItemIsNull = new ErrCode("MSGP20", "被拆包的报文元素为null");
		public static final ErrCode SpecIsNull = new ErrCode("MSGP21", "拆包规则定义为null");
		public static final ErrCode ContextIsNull = new ErrCode("MSGP22", "上下文为null");
		public static final ErrCode UnpackFail_LenMismatch = new ErrCode("MSGU23", "拆包失败_长度不匹配");
		public static final ErrCode UnpackFail_Exception = new ErrCode("MSGU24", "拆包过程中发生错误");
		public static final ErrCode NoSuitableRuleUnit = new ErrCode("MSGU25", "找不到适合指定报文元素的拆包规则单元");
		public static final ErrCode ParserLenUnparseable = new ErrCode("MSGU26", "无法解析向Parser提供的len参数");
		public static final ErrCode ParserLenMustPositive = new ErrCode("MSGU27", "向Parser提供的len参数不能为负数");
		public static final ErrCode AnotherParsedLenUnparseable = new ErrCode("MSGU28", "无法解析通过其它参数提供长度的方式得到的长度值");
		public static final ErrCode AnotherParsedLenMustPositive = new ErrCode("MSGU29", "通过其它参数提供长度的方式得到的长度值不能为负数");
		public static final ErrCode ReferenceLenUnparseable = new ErrCode("MSGU30", "无法解析通过引用方式得到的长度值");
		public static final ErrCode ReferenceLenMustPositive = new ErrCode("MSGU31", "通过引用方式得到的长度值不能为负数");
		public static final ErrCode ScriptNameNull = new ErrCode("MSGU32", "未对拆包脚本命名");
		public static final ErrCode ScriptCompileFail = new ErrCode("MSGU33", "拆包脚本中存在语法错误");
		public static final ErrCode ScriptNotFound = new ErrCode("MSGU34", "未找到指定名称的拆包脚本");
		public static final ErrCode ScriptExecFail = new ErrCode("MSGU35", "拆包脚本执行失败");
		public static final ErrCode ParserNotFound = new ErrCode("MSGU36", "未找到指定的Parser，这通常表示格式字符串中存在错误（是否未使用%%代替%）");
		public static final ErrCode CalculatorNotFound = new ErrCode("MSGU37", "未找到对应于指定的格式参数的Calculator");
		public static final ErrCode LenConvertNotSupported = new ErrCode("MSGU38", "Parser不支持由数据长度到字节长度的转换");
		public static final ErrCode UnsupportedIntLength = new ErrCode("MSGU39", "%d和%D不支持指定的整数长度(支持的长度为1 2 3 4 8)");
		public static final ErrCode UnpackIntFail = new ErrCode("MSGU40", "尝试将字节转换为整数时发生错误");
		public static final ErrCode UnsupportedFloatLength = new ErrCode("MSGU41", "%f和%F不支持指定的整数长度(支持的长度为4 8)");
		public static final ErrCode UnpackFloatFail = new ErrCode("MSGU42", "尝试将字节转换为浮点数时发生错误");
		public static final ErrCode ClassNotFound = new ErrCode("MSGU43", "给定的外部函数所在的类找不到");
		public static final ErrCode FunctionNotFound = new ErrCode("MSGU44", "给定的外部函数找不到");
		public static final ErrCode CallFunctionFail = new ErrCode("MSGU45", "调用外部函数时发生错误");
		public static final ErrCode FunctionResultTypeError = new ErrCode("MSGU46", "外部函数的返回值的类型不正确");
	}
}
