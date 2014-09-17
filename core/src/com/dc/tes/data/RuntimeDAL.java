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
import com.dc.tes.data.model.PersistentData;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.fcore.ParameterProcess;

/**
 * 连接到实际数据源的运行时数据访问接口 该数据源用于功能核心 通过本数据访问接口进行的所有查询均访问数据源
 * 
 * @author lijic
 * 
 */
public class RuntimeDAL extends BaseRuntimeDAL {
	
	private static final Log log = LogFactory.getLog(CacheRuntimeDAL.class);
	
	/**
	 * 初始化一个运行时数据访问器
	 * 
	 * @param instanceName
	 *            系统名称
	 * @param config
	 *            核心基础配置
	 */
	public RuntimeDAL(String instanceName, Config config) throws Exception {
		super(instanceName, config);

		//this.Refresh();
	}

	@Override
	public SysType GetSystem() {
		return super.instance;
	}

	@Override
	public Map<String, Object> GetPersistentData() {
		List<PersistentData> lst = DALFactory.GetBeanDAL(PersistentData.class).ListAll(Op.EQ("systemid", this.instanceId));
		Map<String, Object> pdata = new HashMap<String, Object>();

		for (PersistentData bean : lst)
			pdata.put(bean.getParameter(), bean.getType() == 0 ? bean.getCurvalue() : Double.parseDouble(bean.getCurvalue()));

		return pdata;
	}

	@Override
	public void SetPersistentData(Map<String, Object> pdata) {
		List<PersistentData> lst = DALFactory.GetBeanDAL(PersistentData.class).ListAll(Op.EQ("systemid", this.instanceId));

		for (String name : pdata.keySet()) {
			PersistentData bean = null;
			for (PersistentData _bean : lst)
				if (_bean.getParameter().equals(name)) {
					bean = _bean;
					break;
				}
			if (bean == null) {
				bean = new PersistentData();
				bean.setSystemid(this.instanceId);
			}

			bean.setParameter(name);
			bean.setType((pdata.get(name) instanceof Number) ? 1 : 0);
			bean.setCurvalue(pdata.get(name).toString());

			if (bean.getId() == null)
				DALFactory.GetBeanDAL(PersistentData.class).Add(bean);
			else
				DALFactory.GetBeanDAL(PersistentData.class).Edit(bean);
		}
	}

	@Override
	public Transaction GetTran(String tranCode, TransactionMode mode) {
		Transaction tran = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ("systemId", super.instanceId), Op.EQ("tranCode", tranCode), Op.EQ("isClientSimu", mode == TransactionMode.Client ? 0 : 1));

		if (tran == null)
			throw new TESException(CoreErr.TranNotFound, "tranCode: " + tranCode + " tranMode: " + mode);

		return tran;
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
		List<String> caseNames = this.ListCases(tran);
		if (caseNames.size() == 0)
			throw new TESException(CoreErr.TranNoCase, "tranCode: " + tran.getTranCode() + " tranMode: " + TransactionMode.Server);

		for (String caseName : caseNames) {
			Case c = this.GetCase(caseName, tran);
			if (c.getIsdefault() == 0)
				return c;
		}
		throw new TESException(CoreErr.TranNoDefaultCase, "tranCode: " + tran.getTranCode() + " tranMode: " + TransactionMode.Server);
	}

	@Override
	public List<String> ListCases(Transaction tran) {
		ArrayList<String> lst = new ArrayList<String>();

		for (Case c : DALFactory.GetBeanDAL(Case.class).ListAll(Op.EQ("transactionId", tran.getTransactionId())))
			lst.add(c.getCaseName());

		return lst;
	}

	@Override
	public Case GetCase(String caseName, Transaction tran) {
		Case c = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("transactionId", tran.getTransactionId()), Op.EQ("caseName", caseName));
		if (c == null)
			throw new TESException(CoreErr.CaseNotFound, "caseId: " + caseName + " tranCode: " + tran.getTranCode() + " tranMode: " + (tran.getIsClientSimu() == 1 ? TransactionMode.Client : TransactionMode.Server));
		return c;
	}

	@Override
	public synchronized void Refresh() {
		
		SysType sysType =  DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemName", instanceName));
		if (1 == sysType.getIsParamModified()) {
			log.info("后台参数发生了变化，重新装载参数");
			ParameterProcess.getSystemDynamicParameterList();
			sysType.setIsParamModified(0);
			DALFactory.GetBeanDAL(SysType.class).Edit(sysType);
		}
	}

	@Override
	public boolean isClient() {
		// TODO Auto-generated method stub
		return super.instance.getIsClientSimu() == 1 ? true : false;
	}

	@Override
	public boolean isSync() {
		// TODO Auto-generated method stub
		return super.instance.getIsSyncComm() == 1 ? true : false;
	}

	@Override
	public boolean isSameResponseStruct() {
		// TODO Auto-generated method stub
		return super.instance.getUseSameResponseStruct() == 1 ? true : false;
	}
}
