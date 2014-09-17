package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCard extends BaseModelData implements Serializable, IDistValidate {

	private static final long serialVersionUID = 1L;
	
	
	public static String N_ID = "id";
	public static String N_cmbHost = "cmbHost";
	public static String N_subBankNo = "subBankNo";
	public static String N_subsidiaryNo = "subsidiaryNo";
	public static String N_importBatchNo = "importBatchNo";
	public static String N_sequence = "sequence";
	public static String N_cardNo = "cardNumber";
	public static String N_cardType = "cardType";
	public static String N_cardPwd = "cardPwd";
	public static String N_cardStatus = "cardStatus";
	public static String N_vaildUntil = "vaildUntil";
	public static String N_track2 = "track2";
	public static String N_track3 = "track3";
	public static String N_magnetiIcStripe = "magnetiIcStripe";
	public static String N_cvcCod = "cvcCod";
	public static String N_description = "description";

	
	public GWTCard(){
	}
	
	public String GetCardID(){
		return this.get(N_ID);
	}
	public void SetCardID(String cardId){
		this.set(N_ID, cardId);
	}
	
	public GWTCard(String id, String cmbHost, String subBankNo, 
			String subsidiaryNo, String importBatchNo, 
			Integer sequence, String cardNumber, 
			String cardType, String cardPwd,
			String cardStatus, String vaildUntil, 
			String track2, String track3, String magnetiIcStripe, 
			String cvcCod, String description){
		this.set(N_ID, id);
		this.set(N_cmbHost, cmbHost);
		this.set(N_subBankNo, subBankNo);
		this.set(N_subsidiaryNo, subsidiaryNo);
		this.set(N_importBatchNo, importBatchNo);
		this.set(N_sequence, sequence.toString());
		this.set(N_cardNo, cardNumber);
		this.set(N_cardType, cardType);
		this.set(N_cardPwd, cardPwd);
		this.set(N_cardStatus, cardStatus);
		this.set(N_vaildUntil, vaildUntil);
		this.set(N_track2, track2);
		this.set(N_track3, track3);
		this.set(N_magnetiIcStripe, magnetiIcStripe);
		this.set(N_cvcCod, cvcCod);
		this.set(N_description, description);		
	}
	
	public GWTCard(String id, String cmbHost, String importBatchNo,
			Integer sequence, String CardNo, String cardPwd, String validUntil,
			String track2, String track3){
		this(id, cmbHost, "", "", importBatchNo, sequence, CardNo, "", cardPwd, "", validUntil, track2, track3, "", "", "");
	}
	
	public GWTCard(String batchNo){
		this("","","","",batchNo,0,"","","","","","","","","","");
	}
	
	public String getCmbHost() {
		return this.get(N_cmbHost);
	}

	public String getSubBankNo() {
		return this.get(N_subBankNo);
	}

	public String getSubsidiaryNo() {
		return this.get(N_subsidiaryNo);
	}

	public String getImportBatchNo() {
		return this.get(N_importBatchNo);
	}

	public String getSequence() {
		return this.get(N_sequence);
	}

	public String getCardNo() {
		return this.get(N_cardNo);
	}
	
	public void setCardNo(String cardNo){
		this.set(N_cardNo, cardNo);
	}

	public String getCardType() {
		return this.get(N_cardType);
	}

	public String getCardPwd() {
		return this.get(N_cardPwd);
	}

	public String getCardStatus() {
		return this.get(N_cardStatus);
	}

	public String getVaildUntil() {
		return this.get(N_vaildUntil);
	}

	public String getTrack2() {
		return this.get(N_track2);
	}

	public String getTrack3() {
		return this.get(N_track3);
	}

	public String getMagnetiIcStripe() {
		return this.get(N_magnetiIcStripe);
	}

	public String getCvcCod() {
		return this.get(N_cvcCod);
	}

	public String getDescription() {
		return this.get(N_description);
	}
	
	public void setImportBatchNo(String batchNo){
		this.set(N_importBatchNo, batchNo);
	}

	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "Card";
	}
	
	public boolean IsNew(){
		return GetCardID().isEmpty();
	}
	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_importBatchNo, getImportBatchNo());
		fieldValuePair.put(N_cardNo, validateValue);
		return fieldValuePair;
	}
	
	public void SetEditValue(String cmbHost, String subBankNo, 
			String subsidiaryNo,
			String sequence, String cardNumber, 
			String cardType, String cardPwd,
			String cardStatus, String vaildUntil, 
			String track2, String track3){
		this.set(N_cmbHost, cmbHost);
		this.set(N_subBankNo, subBankNo);
		this.set(N_subsidiaryNo, subsidiaryNo);
		this.set(N_sequence, sequence);
		this.set(N_cardNo, cardNumber);
		this.set(N_cardType, cardType);
		this.set(N_cardPwd, cardPwd);
		this.set(N_cardStatus, cardStatus);
		this.set(N_vaildUntil, vaildUntil);
		this.set(N_track2, track2);
		this.set(N_track3, track3);		
	}

}
