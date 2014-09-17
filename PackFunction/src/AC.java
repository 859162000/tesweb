import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.PackUtils;
import com.dc.tes.util.RuntimeUtils;
import com.sun.jna.Library; 
import com.sun.jna.Native; 
 
public class AC { 
  
	private static final int MAXLEN = 255*2; 
 
	public interface ARQC extends Library { 
    	ARQC INSTANCE = (ARQC)Native.loadLibrary("ic_arqc", ARQC.class); 
  
        int parse_field55(byte[] des, String IMKEY, String PAN_SN, String src); 
    
        int readconfig(String fileName);
	} 
  
    public static void main(String[] args) { 
    	String IMKEY = "CE8670B03D910D5D7CA1D6372A5146A2";
    	String PAN_SN = "621483655000037200";
    	byte[] des = new byte[MAXLEN];
    	String src = "1679F2608DCCAF6275B716BDA9F2701809F100807010199A0B806019F3704000000009F360201C2950500001800009A031209129C01009F02060000000100005F2A02015682027D009F1A0201569F03060000000000009F3303E0F0F09F34036003029F3501119F1E0832303033313233318405FFFFFFFFFF9F090220069F410400000001DF310500000000009F74064543433030319F631030313032303030308030303030303030";
    	//ARQC.INSTANCE.readconfig("C:\\1.txt");
    	int c = ARQC.INSTANCE.parse_field55(des,IMKEY,PAN_SN,src); 
        System.out.println(new String(des));  
   
    }
    
    private static String writeConfig(MsgItem item) {
    	String path = RuntimeUtils.startDir + "\\icconfig";
 
    	MsgContainer container = item.parent();
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("9F10_CVR_INDEX = 6");
    	buffer.append(System.getProperty("line.separator"));
    	for(MsgItem field : container) {
    		String fieldName = field.getAttribute("name").str;
    		buffer.append(fieldName);
    		buffer.append(",");
    	}   	
    	RuntimeUtils.WriteFile(new File(path), buffer.toString(), Charset.forName("utf-8")); 	
    	
    	return path;
    }
	
    public static Value Exec(MsgItem item, PackContext context) {
    	//组包预览与core运行时Lib路径不太一样。
    	String previewLibPath = RuntimeUtils.startDir+"\\lib";
    	String coreRunPath = RuntimeUtils.classDir+"\\lib";;
    	String coreDebugPath = RuntimeUtils.classDir+"\\ic_arqc.dll";
    	if(new File(previewLibPath).exists())
    		System.load(previewLibPath+"\\ic_arqc.dll");
    	else if(new File(coreRunPath).exists()) {
    		System.load(coreRunPath+"\\ic_arqc.dll");
    	} else if(new File(coreDebugPath).exists()) {
    		System.load(coreDebugPath);
    	}
    	
    	MsgField accountNo = (MsgField)item.getDocument().get("b2");
    	String PAN_SN = accountNo.value() + "00";  	
    	
    	String IMKEY = item.getDocument().getAttribute("IMKEY").str;
    	
    	byte[] des = new byte[MAXLEN];
    	
    	String src = pieceSubField(item);
	       
		if(ARQC.INSTANCE.parse_field55(des,IMKEY,PAN_SN,src) != 0) {
		    //默认配置错误时
		    ARQC.INSTANCE.readconfig(writeConfig(item));
		    ARQC.INSTANCE.parse_field55(des,IMKEY,PAN_SN,src);
		}
		
        String desOfString = new String(des).substring(3, 25);
           	
        return new Value(desOfString);
        
    }
    
    private static String pieceSubField(MsgItem item) {
    	
    	MsgContainer container = item.parent();
    	
    	//动态组包的域，如日期是动态值，需要从组好的包中取值，其他域可以直接从报文中取值
    	String[] dynamicField = {"9A"};
    	StringBuffer src = new StringBuffer();
    	
    	for(MsgItem field : container) {
    		
    		String name = field.getAttribute("name").str;
    		
    		if(name.equals("9F26")) {
    			src.append("9F26080000000000000000");
    			continue;
    		}
    			    		
    		//动态域另外处理
    		boolean isDynamic = false;
    		for(String df : dynamicField) {
    			if(name.equals(df)) {
    				src.append(parseField(name,field));
    				isDynamic = true;
    				break;
    			}
    		}
    		if(isDynamic)
    			continue;
    		
    		src.append(name);
    		
    		String len = field.getAttribute("len").str;
    		byte[] bytes = {(byte)Integer.parseInt(len)};
    		src.append(HexStringUtils.ToHexString(bytes));
    		
    		src.append(((MsgField)field).value());
    	}
    	
    	if(src.length()%2 != 0) {
    		throw new TESException(CommonErr.LENGTHOFFIELDERROR,src.toString());
    	}
    	src.insert(0, src.length()/2);
    	
    	return src.toString();
    }
    
    private static String parseField(String name, MsgItem field) {
    	byte[] bytes = field.parent().getAttribute(name).bytes;
    	
    	int lenOfName = name.length() / 2;
    	int lenOfLen = 1;
    	int srcPos = lenOfName + lenOfLen;
    	int length = bytes.length-srcPos;
    	byte[] valueOfBytes = new byte[length];
    	byte[] lenOfBytes = new byte[lenOfLen];
    	
    	//取长度值
    	System.arraycopy(bytes, lenOfName, lenOfBytes, 0, lenOfLen);
        
    	//取值
    	System.arraycopy(bytes, srcPos, valueOfBytes, 0, length);
	    
    	String type = field.getAttribute("type").str;
    	String valueOfString = "";
    	if(type.equals("b")) {  //bcd
    		valueOfString = PackUtils.ReadBCD(valueOfBytes,0,valueOfBytes.length,false,0);
    	} else if(type.equals("B")) { //byte
    		valueOfString = HexStringUtils.ToHexString(valueOfBytes);
    	}
    	
    	StringBuffer buffer = new StringBuffer();
    	
    	buffer.append(name);
    	buffer.append(HexStringUtils.ToHexString(lenOfBytes));	
		buffer.append(valueOfString);
    	
		return buffer.toString();
    }
} 
