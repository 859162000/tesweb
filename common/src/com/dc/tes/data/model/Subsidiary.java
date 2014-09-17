package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * Subsidiary entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class Subsidiary  implements java.io.Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 4184653163130258801L;
	// Fields    

     private String subsidiaryNo;
     private String subsidiaryName;
     private String departmentType;
     private String subBankNo;


    // Constructors

    /** default constructor */
    public Subsidiary() {
    }

    
    /** full constructor */
    public Subsidiary(String subsidiaryName, String departmentType, String subBankNo) {
        this.subsidiaryName = subsidiaryName;
        this.departmentType = departmentType;
        this.subBankNo = subBankNo;
    }

   
    // Property accessors

    public String getSubsidiaryNo() {
        return this.subsidiaryNo;
    }
    
    public void setSubsidiaryNo(String subsidiaryNo) {
        this.subsidiaryNo = subsidiaryNo;
    }

    public String getSubsidiaryName() {
        return this.subsidiaryName;
    }
    
    public void setSubsidiaryName(String subsidiaryName) {
        this.subsidiaryName = subsidiaryName;
    }

    public String getDepartmentType() {
        return this.departmentType;
    }
    
    public void setDepartmentType(String departmentType) {
        this.departmentType = departmentType;
    }

    public String getSubBankNo() {
        return this.subBankNo;
    }
    
    public void setSubBankNo(String subBankNo) {
        this.subBankNo = subBankNo;
    }
   








}