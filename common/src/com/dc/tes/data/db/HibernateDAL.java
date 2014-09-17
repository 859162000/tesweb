package com.dc.tes.data.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.mapping.Collection;

import com.dc.tes.data.model.tag.BeanIdName;
import com.dc.tes.data.op.BETWEEN;
import com.dc.tes.data.op.EQ;
import com.dc.tes.data.op.GE;
import com.dc.tes.data.op.GT;
import com.dc.tes.data.op.IN;
import com.dc.tes.data.op.ISNOTNULL;
import com.dc.tes.data.op.ISNULL;
import com.dc.tes.data.op.LE;
import com.dc.tes.data.op.LIKE;
import com.dc.tes.data.op.LT;
import com.dc.tes.data.op.NE;
import com.dc.tes.data.op.Op;

/**
 * 基于Hibernate的数据源接口实现类
 * 
 * @author huangzx
 */
public class HibernateDAL<T> {
	/**
	 * 要用此DAL实例维护的bean的类型
	 */
	private Class<T> m_beanCls;

	/**
	 * 基于被维护的bean的类型初始化一个Hibernate数据源实现类
	 * 
	 * @param beanCls
	 *            被维护的bean的类型
	 */
	public HibernateDAL(Class<T> beanCls) {
		this.m_beanCls = beanCls;
	}

	@SuppressWarnings("unchecked")
	public T Get(Session session, Op... conditions) throws Exception {
		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareCriteria(c, false, conditions);

		return (T) c.uniqueResult();
	}

	public int Count(Session session, Op... conditions) throws Exception {
		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareCriteria(c, false, conditions);

		c.setProjection(Projections.rowCount());

		return (Integer) c.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<T> List(Session session, int start, int end, Op... conditions) throws Exception {
		/*
		 * 1、 起始条数 < 0 2、 起始条数 > 结束条数 如上述2种情况出现任意,异常处理
		 */
		if ((start < 0) || (start > end)) {
			throw new Exception();
		}

		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareCriteria(c, true, conditions);

		c.setFirstResult(start);
		c.setMaxResults(end - start + 1);

		return c.list();
	}
	
	//add by xuat
	@SuppressWarnings("unchecked")
	public List<T> List(Session session, String orderByColumn, boolean isAsc, int start, int end, Op... conditions) throws Exception {
		/*
		 * 1、 起始条数 < 0 2、 起始条数 > 结束条数 如上述2种情况出现任意,异常处理
		 */
		if ((start < 0) || (start > end)) {
			throw new Exception();
		}

		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareCriteria(c, orderByColumn, isAsc, true, conditions);

		c.setFirstResult(start);
		c.setMaxResults(end - start + 1);

		return c.list();
	}
	@SuppressWarnings("unchecked")
	public List<T> ListAll(Session session, Op... conditions) throws Exception {
		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareCriteria(c, true, conditions);

		return c.list();
	}
	
	//add by xuat
	@SuppressWarnings("unchecked")
	public List<T> ListAll(Session session, String orderByColumn, boolean isAsc, Op... conditions) throws Exception{
		
		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareCriteria(c, orderByColumn, isAsc, true, conditions);

		return c.list();
		
	}
	


	@SuppressWarnings("unchecked")
	public List<T> Match(Session session, String text, String[] properties, int start, int end, Op... conditions) throws Exception {
		if (properties == null)
			throw new Exception("MatchCount function's properties can not be set null!");

		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareMatchCriteria(c, text, properties, conditions);

		c.setFirstResult(start);
		c.setMaxResults(end - start + 1);

		return c.list();
	}

	public int MatchCount(Session session, String text, String[] properties, Op... conditions) throws Exception {
		if (properties == null)
			throw new Exception("MatchCount function's properties can not be set null!");

		Criteria c = session.createCriteria(this.m_beanCls);

		this.prepareMatchCriteria(c, text, properties, conditions);

		c.setProjection(Projections.rowCount());

		return (Integer) c.uniqueResult();
	}

	public void Add(Session session, T bean) throws Exception {
		session.saveOrUpdate(bean);
	}

	public void Edit(Session session, T bean) throws Exception {
		session.saveOrUpdate(bean);
	}

	public void Del(Session session, T bean) throws Exception {
		session.delete(bean);
	}

	public List<?> sqlQuery(Session session, String sql, int... itemNum) throws Exception {
		List<Integer> ls = new ArrayList<Integer>();
		for (int num : itemNum)
			ls.add(num);

		if (ls.size() == 2 && ls.size() > 0) {
			Query query = session.createSQLQuery(sql);
			query.setFirstResult(ls.get(0));
			query.setMaxResults(ls.get(1) - ls.get(0) + 1);
			return query.list();
		}

		return session.createSQLQuery(sql).list();
	}

	public void sqlExec(Session session, String sql) throws Exception {
		session.createSQLQuery(sql).executeUpdate();
	}

	/**
	 * 工具函数 用于将查询条件设置到Criteria对象中
	 * 
	 * @param c
	 *            要设置的Criteria对象
	 * @param sort
	 *            true表示排序 false表示不排序
	 * @param conditions
	 *            查询条件
	 * @throws Exception
	 */
	private void prepareCriteria(Criteria c, boolean sort, Op... conditions) throws Exception {
		// 添加conditions
		for (Op op : conditions) {
			if (op instanceof EQ)
				c.add(Restrictions.eq(op.n, op.v));
			else if (op instanceof LIKE)
				c.add(Restrictions.ilike(op.n, String.valueOf(op.v), MatchMode.ANYWHERE));
			else if (op instanceof NE)
				c.add(Restrictions.ne(op.n, op.v));
			else if (op instanceof GT)
				c.add(Restrictions.gt(op.n, op.v));
			else if (op instanceof GE)
				c.add(Restrictions.ge(op.n, op.v));
			else if (op instanceof LT)
				c.add(Restrictions.lt(op.n, op.v));
			else if (op instanceof LE)
				c.add(Restrictions.le(op.n, op.v));
			else if (op instanceof IN){
				if(op.c1!=null)
					c.add(Restrictions.in(op.n, op.c1));
				else
					c.add(Restrictions.in(op.n, op.c));
			}
			else if (op instanceof ISNULL)
				c.add(Restrictions.isNull(op.n));
			else if (op instanceof ISNOTNULL)
				c.add(Restrictions.isNotNull(op.n));
			else if (op instanceof BETWEEN)
				c.add(Restrictions.between(op.n, op.v, op.v2));
			else
				throw new UnsupportedOperationException();
		}

		// 按照ID字段倒序
		if (sort) {
			String idName = this.m_beanCls.getAnnotation(BeanIdName.class).value();
			c.addOrder(Order.desc(idName));
		}
	}
	
	
	
	//add by xuat
	private void prepareCriteria(Criteria c, String orderByColumn,
			boolean isAsc, boolean sort, Op[] conditions) {
		// TODO Auto-generated method stub
		for (Op op : conditions) {
			if (op instanceof EQ)
				c.add(Restrictions.eq(op.n, op.v));
			else if (op instanceof LIKE)
				c.add(Restrictions.ilike(op.n, String.valueOf(op.v), MatchMode.ANYWHERE));
			else if (op instanceof NE)
				c.add(Restrictions.ne(op.n, op.v));
			else if (op instanceof GT)
				c.add(Restrictions.gt(op.n, op.v));
			else if (op instanceof GE)
				c.add(Restrictions.ge(op.n, op.v));
			else if (op instanceof LT)
				c.add(Restrictions.lt(op.n, op.v));
			else if (op instanceof LE)
				c.add(Restrictions.le(op.n, op.v));
			else if (op instanceof IN){
				if(op.c1!=null)
					c.add(Restrictions.in(op.n, op.c1));
				else
					c.add(Restrictions.in(op.n, op.c));
			}
			else if (op instanceof ISNULL)
				c.add(Restrictions.isNull(op.n));
			else if (op instanceof ISNOTNULL)
				c.add(Restrictions.isNotNull(op.n));
			else if (op instanceof BETWEEN)
				c.add(Restrictions.between(op.n, op.v, op.v2));
			else
				throw new UnsupportedOperationException();
		}

		if (sort) {
			if(isAsc){
				c.addOrder(Order.asc(orderByColumn));
			}else{
				c.addOrder(Order.desc(orderByColumn));
			}
		}
	}

	/**
	 * 工具函数 用于将模糊查询条件设置到Criteria对象中
	 * 
	 * @param c
	 *            要设置的Criteria对象
	 * @param text
	 *            要模糊查询的文本
	 * @param properties
	 *            要查询的bean的属性列表
	 * @param conditions
	 *            查询条件
	 * @throws Exception
	 */
	private void prepareMatchCriteria(Criteria c, String text, String[] properties, Op... conditions) throws Exception {
		// 先按条件过滤
		prepareCriteria(c, true, conditions);

		Criterion matches = null;
		for (String property : properties) {
			Criterion criterion = Restrictions.ilike(property, text, MatchMode.ANYWHERE);

			matches = matches == null ? criterion : Restrictions.or(matches, criterion);
		}

		if (matches != null)
			c.add(matches);
	}
}
