package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * TransactionCatetory entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class TransactionCatetory  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = -1654230408901407036L;
	private String id;
     private String systemId;
     private String categoryName;
     private String description;


    // Constructors

    /** default constructor */
    public TransactionCatetory() {
    }

	/** minimal constructor */
    public TransactionCatetory(String systemId, String categoryName) {
        this.systemId = systemId;
        this.categoryName = categoryName;
    }
    
    /** full constructor */
    public TransactionCatetory(String systemId, String categoryName, String description) {
        this.systemId = systemId;
        this.categoryName = categoryName;
        this.description = description;
    }

   
    // Property accessors

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return this.systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getCategoryName() {
        return this.categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
   








}