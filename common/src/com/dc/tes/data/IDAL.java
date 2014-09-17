package com.dc.tes.data;

import java.util.List;
import com.dc.tes.data.op.Op;

/**
 * 数据源访问接口 用于对bean进行增删改查
 * 
 * @author huangzx
 * 
 * @param <T>
 *            bean的类型
 */
public interface IDAL<T> {
	/**
	 * 查询一个指定的bean<br/>
	 * 例：查询sys对象下面的名称为asdf的交易bean<br/>
	 * <code>
	 * Transaction tran = tranDal.Get(Op.EQ("systemId",sys.getId()),Op.EQ("name","asdf"));
	 * </code>
	 * 
	 * @param conditions
	 *            查询条件 描述bean的属性与某个值的关系的列表 列表中的各个项是AND关系
	 * @return 满足查询条件的bean
	 */
	public T Get(Op... conditions);

	/**
	 * 查询满足条件的bean的个数<br/>
	 * 例：查询sys对象下面的所有交易的个数<br/>
	 * <code>
	 * int tranCount = tranDal.Count(Op.EQ("systemId",sys.getId()));
	 * </code>
	 * 
	 * @param conditions
	 *            查询条件 描述bean的属性与某个值的关系的列表 列表中的各个项是AND关系
	 * @return 满足条件的bean的个数
	 * @throws Exception
	 */
	public int Count(Op... conditions);

	/**
	 * 分页查询满足条件的bean列表<br/>
	 * 例：查询sys对象下面的交易，从第10行查到第19行</br><code>
	 * List&lt;Tran&gt; trans = tranDal.List(10,19,
	 * </code>
	 * 
	 * @param start
	 *            列表起始位置
	 * @param end
	 *            列表结束位置
	 * @param conditions
	 *            查询条件 描述bean的属性与某个值的关系的列表 列表中的各个项是AND关系
	 * @return 满足查询条件的bean分页列表
	 * @throws Exception
	 */
	public List<T> List(int start, int end, Op... conditions);
	/**
	 * 分页查询满足条件的bean列表，并指定根据orderByColumn来排序，isAsc为true时正序，否则为倒序<br/>
	 * 例：查询sys对象下面的交易，从第10行查到第19行，并按sysNo正序排序</br><code>
	 * List&lt;Tran&gt; trans = tranDal.List(sysNo, true, 10,19,
	 * </code>
	 * * @param orderByColumn   
	 * 			      根据哪一列排序，此值与javaBean的变量一致。
	 * @param isAsc   
	 * 			      是否为正序排序
	 * @param start
	 *            列表起始位置
	 * @param end
	 *            列表结束位置
	 * @param conditions
	 *            查询条件 描述bean的属性与某个值的关系的列表 列表中的各个项是AND关系
	 * @return 满足查询条件的bean分页列表
	 * @throws Exception
	 */
	public List<T> List(String orderByColumn, boolean isAsc, int start, int end, Op... conditions);

	/**
	 * 查询所有满足条件的bean列表<br/>
	 * 例：查询sys对象下面的所有交易</br><code>
	 * List&lt;Tran&gt; trans = tranDal.ListAll(Op.EQ("systemId",sys.getId()));
	 * </code>
	 * 
	 * @param conditions
	 *            查询条件 描述bean的属性与某个值的关系的列表 列表中的各个项是AND关系
	 * @return 所有满足查询条件的bean列表
	 * @throws Exception
	 */
	public List<T> ListAll(Op... conditions);
	
	
	/**
	 * 查询所有满足条件的bean列表,并指定根据orderByColumn来排序，isAsc为true时正序，否则为倒序<br/>
	 * 例：查询CaseInstance对象下执行日志ID为executeLogId的所有交易，并按caseNo正序排序</br><code>
	 * List&lt;CaseInstance&gt; caseInsList = caseInstanceDAL.ListAll("caseNo", true, Op.EQ("executeLogId", executeLogID));
	 * </code>
	 * @param orderByColumn   根据哪一列排序，此值与javaBean的变量一致。
	 * @param isAsc   是否为正序排序
	 * @param conditions
	 * 			查询条件 描述bean的属性与某个值的关系的列表 列表中的各个项是AND关系
	 * @return 所有满足查询条件的bean列表
	 * @author xuat
	 */
	public List<T> ListAll(String orderByColumn, boolean isAsc, Op... conditions);
	/**
	 * 根据给定的文本和限制条件进行全表模糊分页查询<br/>
	 * 例：用“123”模糊查询sys对象下面的交易，查询其name和desc属性，从第10行查到第19行</br><code>
	 * List&lt;Tran&gt; trans = tranDal.Match(10,19,"123",new String[]{"name","desc"},
	 *                                Op.EQ("systemId",sys.getId()));
	 * </code>
	 * 
	 * 
	 * @param text
	 *            要模糊查询的文本
	 * @param properties
	 *            要查询的bean的属性列表 有些属性是不适合查询的 所以需要在这里把要查询的属性列出来 如果为null表示查询全部属性
	 * @param start
	 *            列表起始位置
	 * @param end
	 *            列表结束位置
	 * @param conditions
	 *            限制条件
	 * @return 所有满足查询条件的bean列表
	 * @throws Exception
	 */
	public List<T> Match(String text, String[] properties, int start, int end, Op... conditions);

	/**
	 * 根据给定的文本和限制条件进行全表模糊查询 得到满足条件的数据个数
	 * 
	 * @param text
	 *            要模糊查询的文本
	 * @param properties
	 *            要查询的bean的属性列表 有些属性是不适合查询的 所以需要在这里把要查询的属性列出来 如果为null表示查询全部属性
	 * @param conditions
	 *            限制条件
	 * @return 所有满足查询条件的bean的个数
	 * @throws Exception
	 */
	public int MatchCount(String text, String[] properties, Op... conditions);

	/**
	 * 将一个bean添加到持久存储
	 * 
	 * @param bean
	 *            要添加的bean
	 * @throws Exception
	 */
	public void Add(T bean);

	/**
	 * 将一个bean更新到持久存储
	 * 
	 * @param bean
	 *            要更新的bean
	 * @throws Exception
	 */
	public void Edit(T bean);

	/**
	 * 将一个bean从持久存储中删除
	 * 
	 * @param bean
	 *            要删除的bean
	 * @throws Exception
	 */
	public void Del(T bean);

	/**
	 * 执行自定义查询sql语句
	 * 
	 * @param sql
	 *            查询sql语句
	 * @param int... 分页的纪录数，如果要显示 前10条，调用方式 sqlQuery(sql,0,9)
	 * @return 查询结果 Object 数组
	 * @throws Exception
	 * @see 处理 select 语句
	 */
	public List<?> sqlQuery(String sql, int... itemNum);

	/**
	 * 执行自定义查询sql语句
	 * 
	 * @param sql
	 *            查询sql语句
	 * @return 查询结果 bean 列表
	 * @throws Exception
	 */
	//spublic List<T> sqlQueryBean(String sql) throws Exception;

	/**
	 * 执行自定义插入 删除sql语句
	 * 
	 * @param sql
	 *            执行自定义插入 删除sql语句
	 * @return 执行是否成功
	 * @throws Exception
	 * @see insert delete 语句
	 */
	public boolean sqlExec(String sql);
}
