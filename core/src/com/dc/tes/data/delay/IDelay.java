package com.dc.tes.data.delay;

import java.util.Map;

import com.dc.tes.data.RuntimeTranDAL;

/**
 * 用于生成计算延时时间的组建
 * @author songlj
 *
 */
public interface IDelay {
	public long getDelayTime(String tranCode);
	public void setDelayTime(long sysDelayMin, long sysDelayMax, Map<String, RuntimeTranDAL> dal);
}
