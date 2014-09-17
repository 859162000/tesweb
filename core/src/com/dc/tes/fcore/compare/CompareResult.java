package com.dc.tes.fcore.compare;

import com.dc.tes.dom.MsgDocument;

public class CompareResult {
	private MsgDocument com_result;
	private int difference = -1;
	
	public String toString(){
		return this.com_result.toString();
	}
	public MsgDocument getCom_result() {
		return com_result;
	}
	public void setCom_result(MsgDocument com_result) {
		this.com_result = com_result;
	}
	public int getDifference() {
		return difference;
	}
	public void setDifference(int difference) {
		this.difference = difference;
	}
}
