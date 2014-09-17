package com.dc.tes.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Config;
import com.dc.tes.TransactionMode;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.pcore.plog.PLogServ;

/**
 * 将所有数据进行缓存的运行时数据访问接口 该数据源用于性能核心 通过本数据访问接口进行的所有查询均不访问数据源
 * 
 * @author lijic
 * 
 */
public class CacheRuntimeDAL extends BaseRuntimeDAL {
	
	private static final Log log = LogFactory.getLog(CacheRuntimeDAL.class);

	/**
	 * 发起端交易列表
	 */
	private Map<String, Transaction> clientTrans = new HashMap<String, Transaction>();
	/**
	 * 发起端案例名称列表
	 */
	private Map<String, List<String>> clientCaseNames = new HashMap<String, List<String>>();
	/**
	 * 发起端案例列表
	 */
	private Map<String, List<Case>> clientCases = new HashMap<String, List<Case>>();
	/**
	 * 接收端交易列表
	 */
	private Map<String, Transaction> serverTrans = new HashMap<String, Transaction>();
	/**
	 * 接收端默认案例列表
	 */
	private Map<String, Case> serverDefaultCases = new HashMap<String, Case>();
	/**
	 * 接收端案例名称列表
	 */
	private Map<String, List<String>> serverCaseNames = new HashMap<String, List<String>>();
	/**
	 * 接收端案例列表
	 */
	private Map<String, List<Case>> serverCases = new HashMap<String, List<Case>>();

	/**
	 * 持久化数据列表
	 */
	private Map<String, Object> pdata = new HashMap<String, Object>();

	/**
	 * 初始化一个运行时数据访问器
	 * 
	 * @param instanceName
	 *            实例名称
	 * @throws Exception
	 */
	public CacheRuntimeDAL(String instanceName, Config config) throws Exception {
		super(instanceName, config);

		this.Refresh();
	}

	@Override
	public SysType GetSystem() {
		return super.instance;
	}

	@Override
	public Map<String, Object> GetPersistentData() {
		return this.pdata;
	}

	@Override
	public synchronized void SetPersistentData(Map<String, Object> pdata) {
		this.pdata = pdata;
	}

	@Override
	public Transaction GetTran(String tranCode, TransactionMode mode) {
		Map<String, Transaction> trans = mode == TransactionMode.Client ? this.clientTrans : this.serverTrans;

		if (trans.containsKey(tranCode))
			return trans.get(tranCode);

		throw new TESException(CoreErr.TranNotFound, "tranCode: " + tranCode + " tranMode: " + mode);
	}

	@Override
	public long GetDelayTIme(Transaction tran) {
		long sysDelay = (long) (super.instance.getMindelaytime() + (super.instance.getMaxdelaytime() - super.instance.getMindelaytime()) * RandomUtils.nextFloat());
		long tranDelay = (long) (tran.getMindelaytime() + (tran.getMaxdelaytime() - tran.getMindelaytime()) * RandomUtils.nextFloat());

		switch (super.instance.getDelaytimetype()) {
		case 0://系统延时
			return sysDelay;
		case 1://交易延时
			return tranDelay;
		case 2://叠加延时
			return sysDelay + tranDelay;
		default:
			throw new TESException(CoreErr.UnsupportedDelayType, String.valueOf(super.instance.getDelaytimetype()));
		}
	}

	@Override
	public Case GetDefaultCase(Transaction tran) {
		if (tran.getIsClientSimu() == 0)
			throw new TESException(CoreErr.InvalidTranMode, "tranCode; " + tran.getTranCode());

		if (!this.serverDefaultCases.containsKey(tran.getTranCode()))
			throw new TESException(CoreErr.TranNoDefaultCase, "tranCode: " + tran.getTranCode() + " tranMode: " + TransactionMode.Server);

		return this.serverDefaultCases.get(tran.getTranCode());
	}

	@Override
	public List<String> ListCases(Transaction tran) {
		return tran.getIsClientSimu() == 0 ? this.clientCaseNames.get(tran.getTranCode()) : this.serverCaseNames.get(tran.getTranCode());
	}

	@Override
	public Case GetCase(String caseName, Transaction tran) {
		List<Case> cases = tran.getIsClientSimu() == 0 ? this.clientCases.get(tran.getTranCode()) : this.serverCases.get(tran.getTranCode());
		for (Case c : cases)
			if (c.getCaseName().equals(caseName))
				return c;
		throw new TESException(CoreErr.CaseNotFound, "caseName: " + caseName + " tranCode: " + tran.getTranCode() + " tranMode: " + (tran.getIsClientSimu() == 0 ? TransactionMode.Client : TransactionMode.Server));
	}

	@Override
	public synchronized void Refresh() {
		try {
			log.info("刷新数据源...");

			// 装载接收端数据
			log.info("刷新接收端交易...");
			Map<String, Transaction> serverTrans = new HashMap<String, Transaction>();
			Map<String, Case> serverDefaultCases = new HashMap<String, Case>();
			Map<String, List<String>> serverCaseNames = new HashMap<String, List<String>>();
			Map<String, List<Case>> serverCases = new HashMap<String, List<Case>>();

			// 获取所有接收端交易
			for (Transaction tran : DALFactory.GetBeanDAL(Transaction.class).ListAll(Op.EQ("systemId", instance.getSystemId()), Op.EQ("isClientSimu", 1/* TransactionMode.Server */))) {
				log.debug("刷新交易[" + tran.getTranCode() + "]");

				serverTrans.put(tran.getTranCode(), tran);
				serverCaseNames.put(tran.getTranCode(), new ArrayList<String>());
				serverCases.put(tran.getTranCode(), new ArrayList<Case>());

				for (Case c : DALFactory.GetBeanDAL(Case.class).ListAll(Op.EQ("transactionId", tran.getTransactionId()), Op.NE("responseMsg", "".getBytes()))) {
					if (c.getIsdefault() == 1)
						serverDefaultCases.put(tran.getTranCode(), c);
					serverCaseNames.get(tran.getTranCode()).add(c.getCaseName());
					serverCases.get(tran.getTranCode()).add(c);
				}
			}

			// 装载发起端数据
			log.info("刷新发起端交易...");
			Map<String, Transaction> clientTrans = new HashMap<String, Transaction>();
			Map<String, List<String>> clientCaseNames = new HashMap<String, List<String>>();
			Map<String, List<Case>> clientCases = new HashMap<String, List<Case>>();

			// 获取所有发起端交易
			for (Transaction tran : DALFactory.GetBeanDAL(Transaction.class).ListAll(Op.EQ("systemId", instance.getSystemId()), Op.EQ("isClientSimu", 0/* TransactionMode.Client */))) {
				log.debug("刷新交易[" + tran.getTranCode() + "]");

				clientTrans.put(tran.getTranCode(), tran);
				clientCaseNames.put(tran.getTranCode(), new ArrayList<String>());
				clientCases.put(tran.getTranCode(), new ArrayList<Case>());

				for (Case c : DALFactory.GetBeanDAL(Case.class).ListAll(Op.EQ("transactionId", tran.getTransactionId()), Op.NE("responseMsg", "".getBytes()))) {
					if (c.getIsdefault() == 1)
						serverDefaultCases.put(tran.getTranCode(), c);
					serverCaseNames.get(tran.getTranCode()).add(c.getCaseName());
					serverCases.get(tran.getTranCode()).add(c);
				}
			}

			// 数据刷新成功 更改指针目标
			this.serverTrans = serverTrans;
			this.serverDefaultCases = serverDefaultCases;
			this.serverCaseNames = serverCaseNames;
			this.serverCases = serverCases;
			this.clientTrans = clientTrans;
			this.clientCaseNames = clientCaseNames;
			this.clientCases = clientCases;

			PLogServ.CreatePLog(this.instanceName);
			System.gc();

			log.info("刷新数据源成功");
		} catch (Exception ex) {
			throw new TESException(CoreErr.InitPDalFail, ex);
		}
	}

	@Override
	public boolean isClient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSync() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSameResponseStruct() {
		// TODO Auto-generated method stub
		return false;
	}
}
