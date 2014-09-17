package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * Card entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class Card  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = 2315449531907522348L;
	private Integer id;
     private String dbHost;
     private String subBankNo;
     private String subsidiaryNo;
     private String importBatchNo;
     private Integer sequence;
     private String cardNumber;
     private String cardType;
     private String cardPwd;
     private String cardStatus;
     private String vaildUntil;
     private String track2;
     private String track3;
     private String magnetiIcStripe;
     private String cvcCod;
     private String description;
     private String userName;
     private Integer idType;
     private String idNo;
     private String mobilePhone;


    // Constructors

    /** default constructor */
    public Card() {
    }

    
    /** full constructor */
    public Card(String dbHost, String subBankNo, String subsidiaryNo, String importBatchNo, Integer sequence, String cardNumber, String cardType, String cardPwd, String cardStatus, String vaildUntil, String track2, String track3, String magnetiIcStripe, String cvcCod, String description) {
        this.dbHost = dbHost;
        this.subBankNo = subBankNo;
        this.subsidiaryNo = subsidiaryNo;
        this.importBatchNo = importBatchNo;
        this.sequence = sequence;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cardPwd = cardPwd;
        this.cardStatus = cardStatus;
        this.vaildUntil = vaildUntil;
        this.track2 = track2;
        this.track3 = track3;
        this.magnetiIcStripe = magnetiIcStripe;
        this.cvcCod = cvcCod;
        this.description = description;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDbHost() {
        return this.dbHost;
    }
    
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getSubBankNo() {
        return this.subBankNo;
    }
    
    public void setSubBankNo(String subBankNo) {
        this.subBankNo = subBankNo;
    }

    public String getSubsidiaryNo() {
        return this.subsidiaryNo;
    }
    
    public void setSubsidiaryNo(String subsidiaryNo) {
        this.subsidiaryNo = subsidiaryNo;
    }

    public String getImportBatchNo() {
        return this.importBatchNo;
    }
    
    public void setImportBatchNo(String importBatchNo) {
        this.importBatchNo = importBatchNo;
    }

    public Integer getSequence() {
        return this.sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return this.cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardPwd() {
        return this.cardPwd;
    }
    
    public void setCardPwd(String cardPwd) {
        this.cardPwd = cardPwd;
    }

    public String getCardStatus() {
        return this.cardStatus;
    }
    
    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getVaildUntil() {
        return this.vaildUntil;
    }
    
    public void setVaildUntil(String vaildUntil) {
        this.vaildUntil = vaildUntil;
    }

    public String getTrack2() {
        return this.track2;
    }
    
    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return this.track3;
    }
    
    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public String getMagnetiIcStripe() {
        return this.magnetiIcStripe;
    }
    
    public void setMagnetiIcStripe(String magnetiIcStripe) {
        this.magnetiIcStripe = magnetiIcStripe;
    }

    public String getCvcCod() {
        return this.cvcCod;
    }
    
    public void setCvcCod(String cvcCod) {
        this.cvcCod = cvcCod;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Integer getIdType() {
		return idType;
	}


	public void setIdType(Integer idType) {
		this.idType = idType;
	}


	public String getIdNo() {
		return idNo;
	}


	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}


	public String getMobilePhone() {
		return mobilePhone;
	}


	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
   








}