package com.dc.tes.exception;

public class ExceptionInfo {
	
	public static StringBuffer getTraceInfo(Exception e) {
	    StringBuffer sb = new StringBuffer();
		StackTraceElement[] stacks = e.getStackTrace();
		sb.append(e);
		sb.append("\n");
		for (int i = 0; i < stacks.length; i++) {	   
		    sb.append("Class: ").append(stacks[i].getClassName())
		      .append(";  Method: ").append(stacks[i].getMethodName())
		      .append(";  Line: ").append(stacks[i].getLineNumber())
		      .append("\n");	   
		 }	
		
		return sb;
    }

}
