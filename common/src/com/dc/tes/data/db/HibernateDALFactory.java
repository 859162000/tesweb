package com.dc.tes.data.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;

/**
 * 数据源接口工厂类 用于创建一个基于Hibernate的数据源接口
 * 
 * @author huangzx
 * 
 */
public abstract class HibernateDALFactory extends DALFactory {
	private static final Log log = LogFactory.getLog(HibernateDALFactory.class);

	/**
	 * DAO对象的缓存
	 */
	private static Map<Class<?>, IDAL<?>> s_daoBeans = new HashMap<Class<?>, IDAL<?>>();

	//	protected void finalize(){
	//		try {
	//			super.finalize();
	//		} catch (Throwable e) {
	//			e.printStackTrace();
	//		}
	//	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> IDAL<T> getBeanDAL(final Class<T> beanCls) throws Exception {
		if (!s_daoBeans.containsKey(beanCls))
			// 如果DAO缓存中不存在指定的类型 则建立一个对应的DAO实例放到缓存中
			// DAO实例是一个IDAL<T>接口的代理 链接到指定的类型的HibernateDAO类的一个实例
			s_daoBeans.put(beanCls, (IDAL<?>) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { IDAL.class }, new InvocationHandler() {
				/**
				 * 用于进行实际的操作的实际HibernateDAL实例
				 */
				private HibernateDAL<T> dal = new HibernateDAL<T>(beanCls);

				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					// 在本次操作中使用的Hibernate session对象
					Session session = HibernateUtils.GetSession();

					// 令该session不使用缓存
					session.setFlushMode(FlushMode.AUTO);
					session.setCacheMode(CacheMode.REFRESH);

					// 在该session上启动事务
					session.beginTransaction();

					// 执行操作
					try {
						// 将HibernateDAO类的每个方法都需要的session对象设到参数列表中
						Object[] _args = new Object[args.length + 1];
						_args[0] = session;
						System.arraycopy(args, 0, _args, 1, args.length);

						// 将详细的DAO请求记入日志
						//						if (log.isDebugEnabled()) {
						//							StringBuffer buffer = new StringBuffer(method.toString());
						//							for (int i = 0; i < args.length; i++)
						//								buffer.append(SystemUtils.LINE_SEPARATOR).append("{").append(i).append("} = ").append(args[i].getClass().isArray() ? Arrays.toString((Object[]) args[i]) : args[i]);
						//							log.debug(buffer);
						//						}

						// 执行在HibernateDAO中与请求该接口的操作对应的方法
						Object result = MethodUtils.invokeMethod(dal, method.getName(), _args);

						// 提交事务
						session.getTransaction().commit();
						// 返回操作结果
						return result;
					} catch (Exception ex) {
						// 操作过程中出现异常 回滚数据库事务
						try {
							session.getTransaction().rollback();
						} catch (Throwable ex2) {
							log.warn("Hibernate事务回滚失败", ex2);
						}
						throw new TESException(CommonErr.Dal.GetDataFail, ex);
					
					} finally {
						// 操作结束 关闭session
						session.close();
					}
				}
			}));

		return (IDAL<T>) s_daoBeans.get(beanCls);
	}

	@Override
	protected void close() {
		HibernateUtils.Close();
	}
}
