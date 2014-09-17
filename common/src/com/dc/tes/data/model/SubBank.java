package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * SubBank entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class SubBank  implements java.io.Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -5001003341768487118L;
	// Fields    

     private String subBankNo;
     private String subBankName;
     private String fullName;


    // Constructors

    /** default constructor */
    public SubBank() {
    }

    
    /** full constructor */
    public SubBank(String subBankName, String fullName) {
        this.subBankName = subBankName;
        this.fullName = fullName;
    }

   
    // Property accessors

    public String getSubBankNo() {
        return this.subBankNo;
    }
    
    public void setSubBankNo(String subBankNo) {
        this.subBankNo = subBankNo;
    }

    public String getSubBankName() {
        return this.subBankName;
    }
    
    public void setSubBankName(String subBankName) {
        this.subBankName = subBankName;
    }

    public String getFullName() {
        return this.fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
   








}