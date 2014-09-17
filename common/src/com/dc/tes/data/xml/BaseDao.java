package com.dc.tes.data.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dc.tes.data.IDAL;
import com.dc.tes.data.op.EQ;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.DataException;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.Inflector;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 基于xml的数据源接口实现类的基类
 * 
 * @author huangzx
 * 
 * @param <T>
 *            要用此DAL实例进行维护的bean的类型
 */
@SuppressWarnings("unchecked")
public abstract class BaseDao<T> implements IDAL<T> {
	/**
	 * xml配置文件存放的根路径
	 */
	protected String root = DalFactory.root;
	/**
	 * 要用此DAL实例维护的bean的类型
	 */
	protected Class<T> m_beanCls = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	/**
	 * 要用此DAL实例维护的bean的类型短名称 该名称将被用作xml的文件名及节点名
	 */
	protected String m_beanName = m_beanCls.getSimpleName().toLowerCase();

	/**
	 * id属性的名称
	 */
	protected String m_idProp;
	/**
	 * 主键属性的名称 此处所说的主键是指在一个xml文件中用于标识某条数据的属性
	 */
	protected String m_keyProp;
	/**
	 * 父属性的名称 父属性是指该bean的父级 比如SysType是Transaction的父级
	 */
	protected String m_parentProp;
	/**
	 * 以xml属性的形式持久化的属性名称列表 在列表之外的属性需要手工管理
	 */
	protected String[] m_properties;
	/**
	 * 对于一个bean 是否有一个目录与之对应 例如User没有对应的目录 而SysType则有
	 */
	protected boolean m_hasDir;

	/**
	 * 初始化基于xml的数据源接口实现类
	 * 
	 * @param idProperty
	 *            id属性的名称
	 * @param keyProperty
	 *            主键属性的名称 此处所说的主键是指在一个xml文件中用于标识某条数据的属性
	 * @param parentProperty
	 *            父属性的名称 父属性是指该bean的父级 比如SysType是Transaction的父级
	 * @param hasDir
	 *            对于一个bean 是否有一个目录与之对应 例如User没有对应的目录 而SysType则有
	 * @param xmlProperties
	 *            以xml属性的形式持久化的属性名称列表 在列表之外的属性需要手工管理
	 */
	public BaseDao(String idProperty, String keyProperty, String parentProperty, boolean hasDir, String... xmlProperties) {
		this.m_idProp = idProperty;
		this.m_keyProp = keyProperty;
		this.m_parentProp = parentProperty;
		this.m_properties = xmlProperties;
		this.m_hasDir = hasDir;
	}

	@Override
	public T Get(Op... conditions) {
		try {
			List<T> lst = this.listAll(conditions);

			switch (lst.size()) {
			case 0:
				// 未找到指定的数据
				return null;
			case 1:
				// 找到了指定的数据
				return lst.get(0);
			default:
				// 找到的数据多于一条
				throw ex("找到多于一条符合条件的数据", conditions);
			}
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public int Count(Op... conditions) {
		try {
			return this.listAll(conditions).size();
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public List<T> List(int start, int end, Op... conditions) {
		try {
			List<T> lst = this.listAll(conditions);

			Collections.reverse(lst); // listAll查询得到的列表是按文件内部的顺序的 外部需要倒序的列表

			// 调整start和end 使其不超出列表边界
			if (start > lst.size())
				return Collections.EMPTY_LIST;
			if (end > lst.size())
				end = lst.size();

			return lst.subList(start, end + 1);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}
	@Override
	public List<T> List(String orderBy, boolean isAsc,int start, int end, Op... conditions){
		return null;
	}
	@Override
	public List<T> ListAll(String orderBy, boolean isAsc, Op... conditions){
		return null;
	}
	@Override
	public List<T> ListAll(Op... conditions) {
		try {
			List<T> lst = this.listAll(conditions);

			Collections.reverse(lst);// listAll查询得到的列表是按文件内部的顺序的 外部需要倒序的列表

			return lst;
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public List<T> Match(String text, String[] properties, int start, int end, Op... conditions) {
		try {
			List<T> lst = this.match(this.listAll(conditions), text, properties);

			// 调整start和end 使其不超出列表边界
			if (start > lst.size())
				return Collections.EMPTY_LIST;
			if (end > lst.size())
				end = lst.size();

			return lst.subList(start, end + 1);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public int MatchCount(String text, String[] properties, Op... conditions) {
		try {
			return this.match(this.listAll(conditions), text, properties).size();
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public void Add(T bean) {
		try {
			// 如果该bean有对应的目录 则先创建目录 这样可保证IO失败不会影响到已有数据
			if (this.m_hasDir) {
				String name = String.valueOf(PropertyUtils.getProperty(bean, this.m_keyProp));

				File dir = new File(path(bean, name));
				if (dir.exists())
					throw new IOException("目录已经存在：" + dir);

				if (!dir.mkdir())
					throw new IOException("目录创建失败：" + dir);

				this.prepareDir(dir, bean);
			}

			// 定位并打开文件
			String fn = path(bean, this.m_beanName + "s.xml");
			Document doc = XmlUtils.LoadXml(new FileInputStream(fn));

			// 生成该bean的主键
			PropertyUtils.setProperty(bean, this.m_idProp, this.createId(bean));

			// 新建一个xml节点 将其附在最后面
			Element n = doc.createElement(this.m_beanName);
			doc.getDocumentElement().appendChild(n);
			this.toXml(bean, n);

			// 保存文件
			XmlUtils.SaveXml(doc, new FileOutputStream(fn));
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public void Edit(T bean) {
		try {
			String id = this.parseId(String.valueOf(PropertyUtils.getProperty(bean, this.m_idProp)));
			String name = String.valueOf(PropertyUtils.getProperty(bean, this.m_keyProp));

			// 如果该bean有对应的目录 则先进行目录重命名操作 这样可保证IO失败不会影响到已有数据
			if (this.m_hasDir) {
				if (!id.equals(name)) {
					File dir1 = new File(path(bean, id));
					if (!dir1.exists())
						throw new IOException("目录不存在：" + dir1);

					File dir2 = new File(path(bean, name));

					if (dir2.exists())
						throw new IOException("目录已经存在：" + dir2);

					if (!dir1.renameTo(dir2))
						throw new IOException("重命名目录失败：" + dir1 + "->" + dir2);
				}
			}

			// 定位并打开文件
			String fn = path(bean, this.m_beanName + "s.xml");
			Document doc = XmlUtils.LoadXml(new FileInputStream(fn));

			// 构建可以唯一定位该条数据的xpath
			String xpath = "//" + this.m_beanName + "s/" + this.m_beanName + "[@" + this.m_keyProp + "='" + id + "']";

			// 查找出这条xml节点并修改
			Node n = XmlUtils.SelectNode(doc, xpath);
			if (n == null)
				throw ex("未找到指定的节点", Op.EQ(this.m_keyProp, id));
			this.toXml(bean, n);

			// 重建主键 使之与最新的数据匹配
			PropertyUtils.setProperty(bean, this.m_idProp, this.createId(bean));

			// 保存文件
			XmlUtils.SaveXml(doc, new FileOutputStream(fn));
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	@Override
	public void Del(T bean) {
		try {
			String id = this.parseId(String.valueOf(PropertyUtils.getProperty(bean, this.m_idProp)));

			// 定位并打开文件
			String fn = path(bean, this.m_beanName + "s.xml");
			Document doc = XmlUtils.LoadXml(new FileInputStream(fn));

			// 构建可以唯一定位该条数据的xpath
			String xpath = "//" + this.m_beanName + "s/" + this.m_beanName + "[@" + this.m_keyProp + "='" + id + "']";

			// 查找出这条xml节点并删掉
			Node n = XmlUtils.SelectNode(doc, xpath);
			if (n == null)
				throw ex("未找到指定的节点", Op.EQ(this.m_keyProp, id));
			n.getParentNode().removeChild(n);

			// 保存文件
			XmlUtils.SaveXml(doc, new FileOutputStream(fn));

			// 如果该bean有对应的目录 则需要连这个目录也一起删掉 删除操作是在删除bean信息之后做的 这样可保证IO失败不会影响到已有数据
			if (this.m_hasDir) {
				File dir = new File(path(bean, id));
				if (!dir.exists())
					return; // 如果这个目录本来就不存在 则直接返回 已经不用再去删了

				RuntimeUtils.DeleteDirectory(new File(path(bean, id)));
			}
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}

	/**
	 * 生成该dao类维护的实际类型的一个实例 该函数在从xml中读取数据时会被调用
	 * 
	 * @param n
	 *            要读取的xml节点
	 * @param parent
	 *            父属性的值
	 * @param dir
	 *            该xml文件所处的目录
	 * @return 该dao类维护的实际类型的一个实例
	 */
	protected T newInstance(Node n, String parent, File dir) {
		return InstanceCreater.CreateInstance(this.m_beanCls);
	}

	/**
	 * 初始化与该bean对应的目录 该函数在Add()时会被调用
	 * 
	 * @param dir
	 *            要被初始化的目录
	 * @param bean
	 *            bean
	 * @throws Exception
	 */
	protected void prepareDir(File dir, T bean) {
	}

	/**
	 * 工具函数 用于将一个保存bean信息的xml节点转为一个bean
	 * 
	 * @param n
	 *            包含bean信息的xml节点
	 * @param parent
	 *            parent bean的主键 如果没有parent则该参数为null
	 * @param dir
	 *            该xml存放的目录
	 * @return 实例化后的bean
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DOMException
	 * @throws Exception
	 */
	protected T fromXml(Node n, String parent, File dir) throws DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// 创建一个该类型的实例 newInstance()方法可以被子类重写 用于实现延迟加载
		T bean = this.newInstance(n, parent, dir);

		// 从xml中读取属性
		this.readAttrs(bean, n, this.m_properties);

		// 设置parent属性
		if (this.m_parentProp != null)
			PropertyUtils.setProperty(bean, this.m_parentProp, parent);

		// 设置id属性
		PropertyUtils.setProperty(bean, this.m_idProp, this.createId(bean));

		// 返回该bean
		return bean;
	}

	/**
	 * 将一个bean的信息以属性的形式填充到xml节点中
	 * 
	 * @param bean
	 *            bean
	 * @param n
	 *            要将bean中的信息放入的xml节点
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DOMException
	 * @throws Exception
	 */
	protected void toXml(T bean, Node n) throws DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this.saveAttrs(bean, n, this.m_properties);
	}

	/**
	 * 生成bean的主键
	 * 
	 * @param bean
	 *            bean
	 * @return 由该bean的信息生成的主键
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected String createId(T bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String unique = (String) PropertyUtils.getProperty(bean, this.m_keyProp);
		String id = this.m_parentProp == null ? unique : (String) PropertyUtils.getProperty(bean, this.m_parentProp) + "|" + unique;
		return id;
	}

	/**
	 * 从id的值中解析出主键的值
	 * 
	 * @param id
	 *            id
	 * @return 主键的值
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected String parseId(String id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String[] segments = id.split("\\|");
		return segments[segments.length - 1];
	}

	/**
	 * 工具函数 用于根据查询条件定位保存该bean信息的xml文件所处的目录
	 * 
	 * @param conditions
	 *            查询条件
	 * @return 保存该xml文件的目录的相对路径
	 */
	protected String mapPath(Op... conditions) {
		// 如果该bean没有父级 则该xml位于root下
		if (this.m_parentProp == null)
			return "";

		// 如果传入了id作为查询条件 则分析id 返回其对应的目录的上级
		String id = op(conditions, this.m_idProp);
		if (id != null)
			return id.substring(0, id.lastIndexOf('|')).replace('|', File.separatorChar) + File.separator;

		// 如果传入了parent 则解析parent 返回其对应的目录
		String parent = op(conditions, this.m_parentProp);
		if (parent != null)
			return parent.replace('|', File.separatorChar) + File.separator;

		// 无法确定目录 抛异常
		throw ex("无法从查询条件中获取足够的信息", conditions);
	}

	/**
	 * 工具函数 用于根据bean的信息定位保存该bean信息的xml文件所处的目录
	 * 
	 * @param bean
	 *            bean
	 * @return 保存该xml文件的目录的相对路径
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected String mapPath(T bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// 如果该bean没有父级 则该xml位于root下
		if (this.m_parentProp == null)
			return "";

		// 解析该bean的parent属性 返回其对应的目录
		return ((String) PropertyUtils.getProperty(bean, this.m_parentProp)).replace('|', File.separatorChar) + File.separator;
	}

	/**
	 * 工具函数 用于列出所有符合条件的bean
	 * 
	 * @param conditions
	 *            查询条件
	 * @return 符合条件的bean列表
	 * @throws Exception
	 */
	private List<T> listAll(Op... conditions) throws Exception {
		// 读取xml
		File f = new File(DalFactory.root + File.separator + this.mapPath(conditions) + Inflector.pluralize(this.m_beanName) + ".xml");
		FileInputStream s = new FileInputStream(f);
		Document doc = XmlUtils.LoadXml(s);

		// 拼装xpath 将conditions中的每项转成xpath谓词
		StringBuffer xpath = new StringBuffer("//" + Inflector.pluralize(this.m_beanName) + "/" + this.m_beanName);
		for (Op op : conditions) {
			if (op.n.equals(this.m_idProp)) {
				xpath.append("[@").append(this.m_keyProp).append("='").append(this.parseId(String.valueOf(op.v))).append("']");
				continue;
			}
			if (op.n.equals(this.m_parentProp))
				continue;

			if (op instanceof EQ)
				xpath.append("[@").append(op.n).append("='").append(op.v).append("']");
			else
				throw new UnsupportedOperationException();
		}

		// 获取父bean的主键
		String parent = null;
		if (this.m_parentProp != null)
			if ((parent = op(conditions, this.m_parentProp)) == null) {
				String id = op(conditions, this.m_idProp);
				parent = id.substring(0, id.lastIndexOf('|'));
			}

		// 使用拼装好的xpath进行查询 并将查询结果转为bean的列表
		List<T> lst = new ArrayList<T>();
		for (Node n : XmlUtils.SelectNodes(doc, xpath.toString()))
			lst.add(this.fromXml(n, parent, f.getParentFile()));

		// 将查询结果返回
		return lst;
	}

	/**
	 * 工具函数 根据给定的模糊查询条件从bean列表中过滤掉不符合条件的bean
	 * 
	 * @param lst
	 *            供过滤的bean列表
	 * @param text
	 *            要进行模糊查询的文本
	 * @param properties
	 *            要查询的bean的属性列表
	 * @return 过滤后的bean列表 该列表为倒序
	 * @throws Exception
	 */
	private List<T> match(List<T> lst, String text, String[] properties) throws Exception {
		// 如果properties参数为null 则将properties设为当前bean的所有属性的列表
		if (properties == null) {
			ArrayList<String> propNames = new ArrayList<String>();
			BeanInfo info = Introspector.getBeanInfo(this.m_beanCls);
			for (PropertyDescriptor property : info.getPropertyDescriptors())
				if (!property.getName().equals("class"))
					propNames.add(property.getName());
			properties = propNames.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
		}

		// 使用一个栈存放过滤出的bean 这样可保证先过滤出的bean在列表的末尾
		Stack<T> result = new Stack<T>();
		for (T bean : lst) {
			// 进行过滤 如果bean的某个属性的值的字符串形式包含了模糊查询条件则认为过滤成功
			boolean match = false;
			for (String property : properties) {
				Object v = PropertyUtils.getProperty(bean, property);
				if (v == null)
					continue;
				if (String.valueOf(v).toUpperCase().contains(text.toUpperCase()))
					match = true;
			}

			// 将过滤出的bean压入栈底
			if (match)
				result.push(bean);
		}

		// 将过滤结果返回
		return result;
	}

	/**
	 * 工具函数 用于从xml节点中读取bean的属性
	 * 
	 * @param bean
	 *            bean
	 * @param n
	 *            要读取的xml节点
	 * @param names
	 *            属性名称列表
	 * @throws DOMException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void readAttrs(T bean, Node n, String... names) throws DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (String name : names) {
			Attr a = (Attr) n.getAttributes().getNamedItem(name);
			if (a == null)
				continue;

			PropertyUtils.setProperty(bean, name, RuntimeUtils.FromString(a.getNodeValue(), PropertyUtils.getPropertyType(bean, name)));
		}
	}

	/**
	 * 工具函数 用于向xml节点中设置bean的属性
	 * 
	 * @param bean
	 *            bean
	 * @param ((Element)n) 要写入到的xml节点
	 * @param name
	 *            属性名称列表
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DOMException
	 */
	private void saveAttrs(T bean, Node n, String... names) throws DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (String name : names) {
			Object v = PropertyUtils.getProperty(bean, name);
			((Element) n).setAttribute(name, v == null ? "" : String.valueOf(v));
		}
	}

	/**
	 * 工具函数 用于从关系符列表中找出与指定属性相关的运算符的值
	 * 
	 * @param conditions
	 *            关系符列表
	 * @param key
	 *            属性名称
	 * @return 指定属性的运算符的值
	 */
	protected String op(Op[] conditions, String key) {
		for (Op op : conditions)
			if (op.n.equals(key))
				return op.v == null ? "" : String.valueOf(op.v);
		return null;
	}

	/**
	 * 工具函数 用于定位一个绝对路径
	 * 
	 * @param bean
	 *            bean
	 * @param path
	 *            路径信息
	 * @return 定位到的绝对路径 该路径是以保存该bean的xml文件所在的路径为根附加提供的path得到的
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected String path(T bean, String path) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return DalFactory.root + File.separator + this.mapPath(bean) + File.separator + path;
	}

	/**
	 * 工具函数 用于初始化一个异常
	 */
	private DataException ex(String msg, Op... conditions) {
		StringBuffer buffer = new StringBuffer(msg + "  " + this.m_beanCls.getSimpleName());
		for (Op op : conditions)
			buffer.append('[').append(op).append(']');

		return new DataException(buffer.toString());
	}

	/**
	 * 王春佳 增加 IDAL接口 增加自定义查询 ＳＱＬ接口，以满足报表功能.增加此函数主要为了避免变异错误.
	 */
	@Override
	public List<?> sqlQuery(String sql, int... itemNum) {
		throw new NotImplementedException();
	}

	//	@Override
	//	public List<T> sqlQueryBean(String sql) throws Exception {
	//		throw new NotImplementedException();
	//	}

	@Override
	public boolean sqlExec(String sql) {
		return true;

	}

}
