package com.dc.tes.ui.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

import com.dc.tes.data.model.Card;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.util.DocBuilder;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.ui.server.TransactionService;

public class XmlPackUtil {
	public static String PackXmlContent(Case caseBean, Card card) {
		// TODO Auto-generated method stub
		Transaction tran = new TransactionService().GetSingle(caseBean
				.getTransactionId());
		LinkedHashMap<String, String> field = new LinkedHashMap<String,String>();//用来暂时存放域名和域值
		field.put("b2", card.getCardNumber());
		field.put("b14", card.getVaildUntil());
		field.put("b52", card.getCardPwd());
		field.put("b35", card.getTrack2());
		field.put("b36", card.getTrack3());
		field.put("b4", caseBean.getAmount()!=null ? caseBean.getAmount().toString():""); 
		return PackXmlContent(field, tran, 0);
	}
	
	/**
	 * 初始化报文内容，把报文内容中案例里没用到的域值全置为默认值,用到的域用实际值填入
	 * @param field 需要写入的域
	 * @param tranInfo 用于获取交易报文格式
	 * @param isClientSimu 是否为发起方交易
	 * @return xml格式的8583报文
	 */
	public static String PackXmlContent(LinkedHashMap<String, String> field,
			Transaction tranInfo, int isClientSimu) {
		
		MsgDocument dataStruct = MsgLoader.LoadXml(isClientSimu == 1 ?
				tranInfo.getRequestStruct() : tranInfo.getResponseStruct());
		//解析案例数据行
		DocBuilder builder = new DocBuilder();
		Iterator<MsgItem> it = dataStruct.iterator();			
		while(it.hasNext()){
			MsgItem item = it.next();
			LinkedHashMap<String, String> items = new LinkedHashMap<String, String>();
			for(String itemName : item.getAttributes().keySet()){
				items.put(itemName, item.getAttributes().get(itemName).str);
			}
			String fieldName=items.get("name");
			if(field.get(fieldName)!=null){
				builder.Field(field.get(fieldName),items);
			}else
				builder.Field(item.getAttribute("defaultValue").toString(), items);
		}
		//保存
		MsgDocument doc = builder.Export();	
		doc = TranStructTreeUtil.CleanCaseStruct(dataStruct, doc);
		return doc.toString();
	}
}
