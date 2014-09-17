package com.dc.tes.data.db.mysql;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLDialect;

/**
 * 
 * 解决No Dialect mapping for JDBC type: -1
 * 
 * @author xuat
 * 
 */
public class BlobMySQLDialect extends MySQLDialect {
	public BlobMySQLDialect() {
		super();
		registerHibernateType(Types.LONGNVARCHAR, Hibernate.TEXT.getName());

		registerHibernateType(-1, Hibernate.STRING.getName());

		registerHibernateType(Types.DECIMAL, Hibernate.BIG_DECIMAL.getName());

		registerHibernateType(Types.LONGVARBINARY, Hibernate.BLOB.getName());

		registerHibernateType(-4, Hibernate.BYTE.getName());
	}
}
