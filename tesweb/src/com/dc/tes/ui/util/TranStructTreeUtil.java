package com.dc.tes.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.ui.client.model.GWTMsgAttribute;
import com.dc.tes.ui.client.model.GWTPack_Field;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;


public class TranStructTreeUtil {
	
	private static final Log log = LogFactory.getLog(TranStructTreeUtil.class);

	/**
	 * 交易（请求报文、响应报文）
	 * 
	 * @param tran
	 * @param isRes
	 * @param config
	 * @return
	 */
	public static GWTPack_Struct GetGWTTreeRoot(Transaction tran, boolean isRes, final ISystemConfig config) {
		
		GWTPack_Struct root = new GWTPack_Struct((isRes ? " 响应报文" : "请求报文") + String.format("[%s]", tran.getTranCode()));
		
		String xmlTreeStr = isRes ? tran.getResponseStruct() : tran.getRequestStruct();
		
		return GetGWTTreeRoot(xmlTreeStr, isRes, root, config);
	}

	/**
	 * 案例（案例数据、预期结果）
	 * 
	 * @param tran
	 * @param ca
	 * @param isCaseData
	 * @param config
	 * @return
	 */
	public static GWTPack_Struct GetGWTTreeRoot(Case ca, boolean isCaseData, boolean isRes, final ISystemConfig config, int isClientSimu) {

		GWTPack_Struct root = new GWTPack_Struct((isCaseData ? " 案例数据" : "预期结果") + String.format("[%s]", ca.getCaseName()));
		
		String xmlTreeStr;
		if (isClientSimu == 1) { //Client（取请求报文）
			xmlTreeStr = isCaseData ? ca.getRequestXml() : ca.getExpectedXml();
		}
		else { //Server（取应答报文）
			xmlTreeStr = ca.getResponseXml();
		}

		return GetGWTTreeRoot(xmlTreeStr, isRes, root, config);
	}


	public static GWTPack_Struct GetGWTTreeRoot(String xmlTreeStr, boolean isRes, final GWTPack_Struct root, ISystemConfig config) {
		
		if (config == null) {
			log.info("没有取到对应的SystemConfig信息，请检查配置文件/r/n");
			config = new DefaultConfig();
		}

//		List<MsgAttribute> fieldAttrstmp = isRes ? config
//				.getRespFieldAttributes() : config.getReqFieldAttributes();
//		MsgAttribute defaultAttr = new MsgAttribute(GWTPack_Base.m_defaultValue, "默认值", "", "", "100");
//		fieldAttrstmp.add(defaultAttr);
		final List<MsgAttribute> fieldAttrs = isRes ? config.getRespFieldAttributes() : config.getReqFieldAttributes();
		final List<MsgAttribute> structAttrs = isRes ? config.getRespStructAttributes() : config.getReqStructAttributes();

		if (xmlTreeStr != null && !xmlTreeStr.equals("")) {
			MsgDocument doc = MsgLoader.LoadXml(xmlTreeStr);
			doc.ForEach(new ISimpleForEachVisitor() {

				BaseTreeModel curr = root;

				@Override
				public void Visit(ForEachSource source, MsgItem item) {
					if (item instanceof MsgField) {
						GWTPack_Field f = new GWTPack_Field();
						for (MsgAttribute attr : fieldAttrs) {
							f.set(attr.getName(), item.getAttribute(attr.getName()).getStr());
							f.set("data", ((MsgField) item).value());
						}
						curr.add(f);
					} else if (source == ForEachSource.StruStart) {
						GWTPack_Struct s = new GWTPack_Struct();
						for (MsgAttribute attr : structAttrs) {
							s.set(attr.getName(), item.getAttribute(attr.getName()).getStr());
						}
						curr.add(s);
						s.setParent(curr);
						curr = s;
					} else if (source == ForEachSource.StruEnd) {
						curr = (BaseTreeModel) curr.getParent();						
					} else
						return;
				}
			});
		}

		// scckobe 进行统一组装 交易的报文结构、案例的数据、预期结果，以及案例的比对结果具有相同的操作
		return EncapStructAttr(root, fieldAttrs, structAttrs);
	}

	public static GWTPack_Struct GetCompResultRoot(String xmlTreeStr, String caseName, final ISystemConfig config) {
		
		GWTPack_Struct root = new GWTPack_Struct("预期结果比对"	+ String.format("[%s]", caseName));

		// 对相应报文样式做处理
		config.getRespFieldAttributes().add(new MsgAttribute("expect_result", "预期值", "", "", "100"));

		return GetGWTTreeRoot(xmlTreeStr, true, root, config);
	}

	
	/**
	 * 封装报文树形结构 设置其相应字段属性以及结构属性等
	 * 
	 * @param root
	 *            报文结构树
	 * @param fAttrs
	 *            字段属性列表
	 * @param sAttrs
	 *            结构属性列表
	 * @return 报文树形结构
	 */
	private static GWTPack_Struct EncapStructAttr(GWTPack_Struct root, List<MsgAttribute> fAttrs, List<MsgAttribute> sAttrs) {
		
		GWTPack_Struct treeRoot = new GWTPack_Struct();

		if (root != null) {
			root.set("isArray", "");
			treeRoot.add(root);
		}

		List<GWTMsgAttribute> gwtfattrs = new ArrayList<GWTMsgAttribute>();
		List<GWTMsgAttribute> gwtsattrs = new ArrayList<GWTMsgAttribute>();
		for (MsgAttribute attr : fAttrs) {
			GWTMsgAttribute m = new GWTMsgAttribute();
			m.set("name", attr.getName());
			m.set("display", attr.getDisplayName());
			m.set("list", attr.getListItems());
			m.set("default", attr.getDefaultValue());
			m.set("width", attr.getWidth());
			gwtfattrs.add(m);
		}
		for (MsgAttribute attr : sAttrs) {
			GWTMsgAttribute m = new GWTMsgAttribute();
			m.set("name", attr.getName());
			m.set("display", attr.getDisplayName());
			m.set("list", attr.getListItems());
			m.set("default", attr.getDefaultValue());
			m.set("width", attr.getWidth());
			gwtsattrs.add(m);
		}
		treeRoot.setFieldAttrList(gwtfattrs.toArray(new GWTMsgAttribute[] {}));
		treeRoot.setStructAttrList(gwtsattrs.toArray(new GWTMsgAttribute[] {}));

		return treeRoot;
	}

	/**
	 * 将界面展示的GWTPack_Struct
	 * root转换为报文服务用的MsgDocument结构，通过toString()方法生成XML文件并储存到数据库
	 * 
	 * @param root
	 * @param isRes
	 *            请求报文or响应报文
	 * @param ISystemConfig
	 *            系统字段配置
	 * @return MsgDocument
	 */
	public static MsgDocument GetMsgDocument(GWTPack_Struct root, boolean isRes, ISystemConfig config, int iTransOrCase) {

		List<MsgAttribute> fAttrs = isRes ? config.getRespFieldAttributes()
				: config.getReqFieldAttributes();

		List<MsgAttribute> sAttrs = isRes ? config.getRespStructAttributes()
				: config.getReqStructAttributes();

		MsgDocument doc = new MsgDocument();
		for (ModelData model : root.getChildren()) {
			//之前通过name来区别，现改为id，因为数组的情况会出现name相同的情况
			doc.put(model.get("id").toString(), ConstructTree((BaseTreeModel) model, sAttrs, fAttrs, iTransOrCase));
		}

		return doc;
	}

	/**
	 * 内部递归构造树
	 * 
	 * @param model
	 * @param sAttrs
	 *            结构Attribute列表
	 * @param fAttrs
	 *            字段Attribute列表
	 * @return MsgItem
	 */
	private static MsgItem ConstructTree(BaseTreeModel model, List<MsgAttribute> sAttrs, List<MsgAttribute> fAttrs, int iTransOrCase) {

		MsgItem item = null;

		if (model instanceof GWTPack_Field) {
			GWTPack_Field field = (GWTPack_Field) model;
			item = new MsgField();
			item.setAttribute("name", field.getName());
			for (MsgAttribute attr : fAttrs) {
				String name = attr.getName();
				String value = field.get(attr.getName());
				item.setAttribute(name, value);
			}
			if (iTransOrCase == 1 && item.getAttribute("defaultValue") != null) { //transaction, 设置value为defaultValue
				((MsgField) item).set((String) field.get("defaultValue"));
			}
			else if (field.get("data") != null) {
				((MsgField) item).set((String) field.get("data"));
			}
			return item;
		} else {
			GWTPack_Struct struct = (GWTPack_Struct) model;
			item = new MsgStruct();
			item.setAttribute("name", struct.getName().trim());
			for (MsgAttribute attr : sAttrs) {
				String name = attr.getName();
				String value = struct.get(name);
				if ("name".equals(name)) {
					value = value.trim();
				}
				item.setAttribute(name.trim(), value);
			}
			for (ModelData child : model.getChildren()) {
				((MsgStruct) item).put(child.get("id").toString(), ConstructTree((BaseTreeModel) child, sAttrs, fAttrs, iTransOrCase));
			}
			return item;
		}
	}

	/**
	 * 对案例的报文结构进行清洗操作
	 * @param tranStru	交易报文结构
	 * @param caseStru	案例报文结构
	 */
	public static MsgDocument CleanCaseStruct(MsgDocument tranStru,	MsgDocument caseStru) {
		
		MsgDocument returnCaseStru = caseStru.Copy();
		returnCaseStru.clear();
		RecyleClean(tranStru, caseStru, returnCaseStru);
		return returnCaseStru;
	}

	/**
	 * 根据名称，将当前案例节点进行名值对的对应
	 * @param structMap	结构类型名值对
	 * @param fieldMap	域类型名值对
	 * @param item		案例节点
	 */
	private static void SetChildSplit(Map<String, List<MsgItem>> structMap,	Map<String, List<MsgItem>> fieldMap, MsgItem item) {
		
		String itemName = item.getAttribute("name").getStr();
		if (item instanceof MsgArray) {
			MsgArray arrayItem = (MsgArray) item;
			for (int i = 0; i < arrayItem.size(); i++)
				SetChildSplit(structMap, fieldMap, arrayItem.get(i));
		} else if (item instanceof MsgStruct) {
			if (structMap.containsKey(itemName)) {
				structMap.get(itemName).add(item);
			} else {
				List<MsgItem> itemList = new ArrayList<MsgItem>();
				itemList.add(item);
				structMap.put(itemName, itemList);
			}
		} else if (item instanceof MsgField) {
			if (fieldMap.containsKey(itemName)) {
				fieldMap.get(itemName).add(item);
			} else {
				List<MsgItem> itemList = new ArrayList<MsgItem>();
				itemList.add(item);
				fieldMap.put(itemName, itemList);
			}
		}
	}

	/**
	 * 递归清洗
	 * @param tranInput	报文结构、子结构
	 * @param caseInput 案例结构、子结构
	 * @param returnCaseStru	填充结构
	 */
	private static void RecyleClean(MsgItem tranInput, MsgItem caseInput, MsgContainer returnCaseStru) {
		
		if (tranInput instanceof MsgField)
			return;
		if (caseInput instanceof MsgField)
			return;

		MsgContainer tranStru = (MsgContainer) tranInput;
		MsgContainer caseStru = (MsgContainer) caseInput;

		int caseChildCount = caseStru.size();

		//为当前案例结构的子节点分组
		Map<String, List<MsgItem>> structMap = new HashMap<String, List<MsgItem>>();
		Map<String, List<MsgItem>> fieldMap = new HashMap<String, List<MsgItem>>();
		for (int i = 0; i < caseChildCount; i++) {
			SetChildSplit(structMap, fieldMap, caseStru.get(i));
		}

		int tranChildCount = tranStru.size();
		for (int tranIndex = 0; tranIndex < tranChildCount; tranIndex++) {
			MsgItem tranItem = tranStru.get(tranIndex);
			boolean isArray = false;
			boolean isStruct = true;
			if (tranItem instanceof MsgArray) {
				tranItem = ((MsgArray) tranItem).get(0);
				isArray = true;
			}
			if (tranItem instanceof MsgField)
				isStruct = false;

			String tranItemName = tranItem.getAttribute("name").getStr();
			List<MsgItem> sameNameItem = isStruct ? structMap.get(tranItemName)
					: fieldMap.get(tranItemName);

			//案例缺少该结构，或者域类型变化  拷贝一份
			if (sameNameItem == null) {
				MsgItem copyItem = tranItem.Copy();
				returnCaseStru.put(tranItemName, copyItem);
			} else {
				//不是数组，只留下第一个
				if (!isArray) {
					MsgItem fristItem = sameNameItem.get(0);
					sameNameItem.clear();
					sameNameItem.add(fristItem);
				}

				int tmpCount = sameNameItem.size();
				for (int i = 0; i < tmpCount; i++) {
					//结构、域
					if (isStruct)
					{
						//清空tempItem的子节点 scckobe 2010-03-17
						MsgItem tmpItem = sameNameItem.get(i);
						MsgContainer noChildStru = (MsgContainer)tmpItem.Copy();
						int childCount = noChildStru.size() - 1;
						for(int j = childCount; j >= 0 ; j--)
						{
							noChildStru.removeAt(j);
						}
						noChildStru.setAttributes(tranItem.getAttributes());
						returnCaseStru.put(tranItemName, noChildStru);
						
						RecyleClean(tranItem, tmpItem,noChildStru);
					}
					else
					{
						MsgItem tmpItem = sameNameItem.get(i);
						tmpItem.setAttributes(tranItem.getAttributes());
						returnCaseStru.put(tranItemName, tmpItem);
					}
				}
			}
		}
	}
}
