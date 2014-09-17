package com.dc.tes.net.jre14;

/**
 * 表示模拟器各部分之间传递的报文的类型
 * 适用于jdk1.4.2
 * 
 * @author guhb
 * 
 */
public class MessageType {	

	public static final MessageType UNREG = new MessageType(0);// 适配器反注册消息
	
	public static final MessageType REG = new MessageType(1);// 适配器注册消息	

    public static final MessageType MESSAGE = new MessageType(2);//适配器交易消息

    public static final MessageType UI = new MessageType(3);//界面控制消息

    public static final MessageType LOGREG = new MessageType(4);//日志监控注册消息

    public static final MessageType LOG = new MessageType(5);//日志流水消息

    public static MessageType valueOf(String typestr) throws Exception{
    	for(int i=0; i<m_typeList.length; i++){
    		if(m_typeList[i].equals(typestr)){
    			switch(i)
    			{
    				case 0:
    					return UNREG;
	    			case 1:
	    				return REG;
	    			case 2:
	    				return MESSAGE;
	    			case 3:
	    				return UI;
	    			case 4:
	    				return LOGREG;
	    			case 5:
	    				return LOG;
    				default:
    					;//直接抛异常
    			}
    		}
    	}
    	throw new Exception("消息类型" + typestr + "非法！"); 
    }
    
    public String name(){
    	return m_typeList[this.m_value];
    }
    
    private static String[] m_typeList = {
    		"UNREG",
    		"REG", //
    		"MESSAGE", //
    		"UI", //
    		"LOGREG", //
    		"LOG" //
    };
    private int m_value = -1;
    private MessageType(int i){
        this.m_value = i;
    }
}
