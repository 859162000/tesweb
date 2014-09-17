package com.dc.tes.data.model;

import java.io.Serializable;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * ExecuteSetTaskItem: 任务队列任务 JavaBean 映射
 * 
 * 
 */
@BeanIdName("id")
public class ExecuteSetTaskItem implements Serializable {

	private static final long serialVersionUID = -3437949181832345963L;

	private String id; // ID
	//private String queuelistid; // 关联任务队列ID

	private ExecuteSet executeSet;	//关联 任务队列
	
	private String taskId; // 业务流ID\案例ID
	private int type; // 关联TASKID类型:0关联案例;1关联业务流
	private int repCount; // 自我执行次数
	private String name; //任务名称
	private String transactionId; // 如果taskid关联案例,则此字段应为该案例关联的交易id,否则为空

	/**
	 * 	获取 关联 任务队列
	 * @return 任务队列
	 */
	public ExecuteSet getExecuteSet() {
		return executeSet;
	}

	/**
	 * 设置 关联 任务队列
	 * @param queuelist 任务队列
	 */
	public void setExecuteSet(ExecuteSet executeSet) {
		this.executeSet = executeSet;
	}

	/**
	 * 获取 ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 ID
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 任务队列ID
	 * @return 任务队列ID
	 */
//	public String getQueuelistid() {
//		return queuelistid;
//	}

	/**
	 * 设置 任务队列ID
	 * @param queuelistid 任务队列ID
	 */
//	public void setQueuelistid(String queuelistid) {
//		this.queuelistid = queuelistid;
//	}

	/**
	 * 获取  业务流ID\案例ID
	 * @return 业务流ID\案例ID
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 设置 业务流ID\案例ID
	 * @param taskid 业务流ID\案例ID
	 */ 
	public void setTaskId(String taskid) {
		this.taskId = taskid;
	}

	/**
	 * 获取 关联TASKID类型
	 * @return 0关联案例;1关联业务流
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置 关联TASKID类型
	 * @param type  0关联案例;1关联业务流
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取 自我执行次数
	 * @return 自我执行次数
	 */
	public int getRepCount() {
		return repCount;
	}

	/**
	 * 设置 自我执行次数
	 * @param repCount 自我执行次数
	 */
	public void setRepCount(int repCount) {
		this.repCount = repCount;
	}

	/**
	 * 获取 交易id
	 * @return 交易id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * 设置 交易id
	 * @param transactionid 交易id
	 */
	public void setTransactionId(String transactionid) {
		this.transactionId = transactionid;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	
	
}
