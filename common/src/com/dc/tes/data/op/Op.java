package com.dc.tes.data.op;

import java.util.Collection;


/**
 * 关系符 用于表示bean属性的值与某个值之间的关系
 * 
 * @author huangzx
 * @see EQ LIKE
 */
public abstract class Op {
	/**
	 * 属性名称
	 */
	public String n;
	/**
	 * 值
	 */
	public Object v;
	/**
	 * 值
	 */
	public Object v2;
	
	/**
	 * 数组 
	 */
	public Object[] c;
	
	@SuppressWarnings("rawtypes")
	public Collection c1;
	/**
	 * 建立一个EQ关系
	 * 
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n EQ v]的关系描述符
	 * @see EQ
	 */
	public static EQ EQ(String n, Object v) {
		EQ op = new EQ();
		op.n = n;
		op.v = v;
		return op;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", this.n, this.getClass().getSimpleName(), this.v);
	}

	/**
	 * 建立一个LIKE关系
	 * 
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n LIKE v]的关系描述符
	 */
	public static LIKE LIKE(String n, Object v) {
		LIKE op = new LIKE();
		op.n = n;
		op.v = v;
		return op;
	}	
	
	/**
	 * 建立一个not equal关系
	 * 
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n not equal v]的关系描述符
	 */
	public static NE NE(String n, Object v){
		NE op = new NE();
		op.n = n;
		op.v = v;
		return op;
	}
	
	/**
	 * 建立一个is null关系
	 * @param n
	 * 			属性名称
	 * @return 一个表示[bean.n == null]的关系描述符
	 */
	public static ISNULL ISNULL(String n){
		ISNULL op = new ISNULL();
		op.n = n;
		return op;
	}
	
	/**
	 * 建立一个is not null关系
	 * @param n
	 * 			属性名称
	 * @return 一个表示[bean.n <> null]的关系描述符
	 */
	public static ISNOTNULL ISNOTNULL(String n){
		ISNOTNULL op = new ISNOTNULL();
		op.n = n;
		return op;
	}
	
	/**
	 * 建立一个great than关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n > v]的关系描述符
	 */
	public static GT GT(String n, Object v){
		GT op = new GT();
		op.n = n;
		op.v = v;
		return op;
	}
	
	/**
	 * 建立一个great or equal than关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n >= v]的关系描述符
	 */
	public static GE GE(String n, Object v){
		GE op = new GE();
		op.n = n;
		op.v = v;
		return op;
	}
	
	/**
	 * 建立一个less than关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n < v]的关系描述符
	 */
	public static LT LT(String n, Object v){
		LT op = new LT();
		op.n = n;
		op.v = v;
		return op;
	}
	
	/**
	 * 建立一个less or equal than关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @return 一个表示[bean.n <= v]的关系描述符
	 */
	public static LE LE(String n, Object v){
		LE op = new LE();
		op.n = n;
		op.v = v;
		return op;
	}
	
	/**
	 * 建立一个 in 关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param c
	 *            数组
	 * @return 一个表示[bean.n in c]的关系描述符
	 */
	public static IN IN(String n, Object[] c){
		IN op = new IN();
		op.n = n;
		op.c = c;
		return op;
	}
	
	/**
	 * 建立一个 in 关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param c1
	 *            集合
	 * @return 一个表示[bean.n in c]的关系描述符
	 */
	@SuppressWarnings("rawtypes")
	public static IN IN(String n, Collection c1){
		IN op = new IN();
		op.n = n;
		op.c1 = c1;
		return op;
	}
	
	/**
	 * 建立一个between关系
	 *  by xuat
	 * @param n
	 *            属性名称
	 * @param v
	 *            值
	 * @param v2
	 *            值
	 * @return 一个表示[bean.n between v, v2]的关系描述符
	 */
	public static BETWEEN BETWEEN(String n, Object v, Object v2){
		BETWEEN op = new BETWEEN();
		op.n = n;
		op.v = v;
		op.v2 = v2;
		return op;
	}
	
}
