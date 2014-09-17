package com.dc.tes.data;

import java.util.List;
import java.util.Map;

import com.dc.tes.TransactionMode;
import com.dc.tes.component.IComponentConfigLoader;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;

/**
 * 模拟器运行时数据访问接口
 * 
 * @author lijic
 * 
 */
public interface IRuntimeDAL extends IComponentConfigLoader {
	// 全局相关
	/**
	 * 获取当前系统对象
	 * 
	 * @return 表示当前被模拟系统的对象
	 */
	public SysType GetSystem();

	/**
	 * 获取持久化数据列表
	 * 
	 * @return 持久化数据列表
	 */
	public Map<String, Object> GetPersistentData();

	/**
	 * 更新持久化数据列表
	 * 
	 * @param rdata
	 *            持久化数据列表
	 */
	public void SetPersistentData(Map<String, Object> pdata);

	// 交易相关
	/**
	 * 获取指定的交易对象
	 * 
	 * @param tranCode
	 *            交易码
	 * @param mode
	 *            交易类型
	 * @return 表示指定交易的对象
	 */
	public Transaction GetTran(String tranCode, TransactionMode mode);

	/**
	 * 获取指定交易的延时时间
	 * 
	 * @param tran
	 *            交易
	 * @return 延时时间
	 */
	public long GetDelayTIme(Transaction tran);

	// 案例相关
	/**
	 * 获取一个交易下的默认案例
	 * 
	 * @param tran
	 *            交易
	 * @return 该交易下的默认案例
	 */
	public Case GetDefaultCase(Transaction tran);

	/**
	 * 获取指定交易下的所有案例的名称
	 * 
	 * @param tran
	 *            交易
	 * @return 案例名称列表
	 */
	public List<String> ListCases(Transaction tran);

	/**
	 * 获取指定的案例对象
	 * 
	 * @param caseName
	 *            案例名称
	 * @param tran
	 *            交易
	 * @return 表示指定案例的对象
	 */
	public Case GetCase(String caseName, Transaction tran);

	// 其它

	/**
	 * 与数据源进行同步 该操作将会清除数据访问接口实例中所有的缓存并从数据源进行一次全量刷新
	 */
	public void Refresh();
	
	/**
	 * 模拟器是否作为发起交易方？
	 * @return 
	 */
	public boolean isClient();
	
	/**
	 * 通讯模式
	 * ture -- 同步
	 * false -- 异步
	 */	
	public boolean isSync();
	
	/**
	 * 是否用同一响应报文
	 */
	public boolean isSameResponseStruct();

}
