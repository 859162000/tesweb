import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.type.BytePackage;


public class MAC {
	
	private static final String Algorithm = "DES"; //定义 加密算法,可用 DES,DESede,Blowfish
	
    public static byte[] Des(byte[] keybyte, byte[] src, int flag) {
    	
    	try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            
            if(flag == 1) //加密
            	cipher.init(Cipher.ENCRYPT_MODE, deskey);
            else          //解密
            	cipher.init(Cipher.DECRYPT_MODE, deskey);
            
            return cipher.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        
        return null;
    }
    
    
    public static byte[] My3Des(byte[] keybyte, byte[] src, int flag) {
    	
    	byte[] key1 = new byte[8];
    	byte[] key2 = new byte[8];
    	
    	System.arraycopy(keybyte,0,key1,0,8);
    	System.arraycopy(keybyte,8,key2,0,8);
    	
    	byte[] encoded = Des(key1, src, flag);
    	encoded = Des(key2, encoded, flag == 1 ? 0:1);
    	encoded = Des(key1, encoded, flag);
    	
    	return encoded;
    }
    
    
    public static String bytesToHexString(byte[] src){  
    	     StringBuilder stringBuilder = new StringBuilder("");  
    	     if (src == null || src.length <= 0) {  
    	        return null;  
    	     }  
    	     for (int i = 0; i < src.length; i++) {  
    	         int v = src[i] & 0xFF;  
    	         String hv = Integer.toHexString(v);  
    	         if (hv.length() < 2) {  
    	             stringBuilder.append(0);  
    	         }  
    	         stringBuilder.append(hv);  
    	     }  
    	     return stringBuilder.toString();  
    }  
    
    public static byte[] hexStringToBytes(String hexString) {  
    	    if (hexString == null || hexString.equals("")) {  
    	         return null;  
    	     }  
    	     hexString = hexString.toUpperCase();  
    	     int length = hexString.length() / 2;  
    	     char[] hexChars = hexString.toCharArray();  
    	     byte[] d = new byte[length];  
    	     for (int i = 0; i < length; i++) {  
    	         int pos = i * 2;  
    	         d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
    	     }  
    	     return d;  
    } 

    
    private static byte charToByte(char c) {  
    	    return (byte) "0123456789ABCDEF".indexOf(c);  
    }  

    public static Value CalculateMacField(MsgItem item, String tranCode, byte[] mackey) {
    	
        int[] fieldlist =  {2,3,4,7,11,18,25,28,32,33,38,39,41,42,90,102,103};
        
        BytePackage MacField = new BytePackage();
        
        MacField.Append(tranCode.getBytes());
        
        
        for(int i=0; i<fieldlist.length; i++) {
        	
        	MsgContainer parent = item.parent();
        	
        	String fieldName = "b"+fieldlist[i];
        	
        	Value field = parent.getAttribute(fieldName);
            
        	if(field != Value.empty) {
        		BytePackage macfield = new BytePackage();
        		  		
        			
                //String format = parent.get(fieldName).getAttribute("format").str;                               
            	//if(!format.equalsIgnoreCase("LLVAR") && 
            	//		!format.equalsIgnoreCase("LLLVAR")) {
            	if(fieldName.equalsIgnoreCase("b90")) {  //90域只取20位
            		macfield.Append(field.bytes,0,20);
            	} else {
            		macfield.Append(field.bytes);
            	}            		
            			         		
//            	} else {
//            		int len = format.equalsIgnoreCase("LLVAR")? 2 : 3;
//            		String outFormat = "%0" + len + "d";
//            		String lenFormat = String.format(outFormat, field.bytes.length);
//            		macfield.Append(lenFormat.getBytes());
//            		macfield.Append(field.bytes);           		          		
//            	}
            	
            	byte[] cleanField = combineMacfield(macfield.Export());
        		if(cleanField != null) {
        			MacField.Append(" ".getBytes()); //域与域之间加上空格
        			MacField.Append(cleanField);     
        		}
        		
            }
        }
        
        if(MacField.getLength()%8 != 0) {   //右补0x00凑足8的倍数
        	int len = 8 - MacField.getLength()%8;
        	byte[] nu = new byte[len];
        	for(int i=0; i<len; i++) {
        		nu[i] = 0x00;
        	}
        	MacField.Append(nu);
        }
                
        byte[] macbuf = new byte[8];
        byte[] macfield = MacField.Export();
        
        for(int i=0; i<MacField.getLength(); i+=8) {
        	for(int j=0; j<8; j++) {
        		macbuf[j] ^= macfield[i+j];
        	}
        	byte[] temp = Des(mackey,macbuf,1);
        	System.arraycopy(temp, 0, macbuf, 0, 8);
        }
        
        String hex = bytesToHexString(macbuf);
        
        byte[] mac = new byte[8];
        System.arraycopy(hex.toUpperCase().getBytes(), 0, mac, 0, 8);        
               
        return new Value(mac);
        
    }  
    
    public static byte[] combineMacfield(byte[] macfield) {
    	
    	int length = macfield.length;
    	byte[] field = new byte[length];
    	int pos = 0;
    	
    	for(int i=0; i<length; i++) {
    		if(macfield[i] >= 'a' && macfield[i] <= 'z')
    			field[pos++] = (byte) (macfield[i] - 32);
    		else if((macfield[i] >= 'A' && macfield[i] <= 'Z')||
    				(macfield[i] >= '0' && macfield[i] <= '9')||
    				(macfield[i] == ',') || (macfield[i] == '.'))
    			field[pos++] = macfield[i];
    		
    		else if(macfield[i] == ' '){
    			field[pos++] = macfield[i];
    			while(++i < length && macfield[i] == ' ')  //忽略连续空格
    				;
    			i -= 1;  //退回一个
    		}
    	}
    	
    	pos -= 1;
    	
    	int start = 0;
    	
    	if(pos < 0) 
    		return null;
    	
    	if(field[0] == ' ') {  //头空格不要
    		start += 1;
    	}
    	
    	if(field[pos] == ' ') {  //尾空格不要
    		pos -= 1;   
    	}
    	
    	if(start > pos || pos < 0)
    		return null;
    	
    	byte[] cleanField = new byte[pos+1];
    	System.arraycopy(field, start, cleanField, 0, pos+1);
    	
    	return cleanField;   	
    	
    }
     
    public static Value Exec(MsgItem item, PackContext context) {
    	byte[] key = null;
    	
    	String enmac = item.parent().getAttribute("enMacKey").str;
			
		byte[] em = hexStringToBytes(enmac);
    	key = em;
    	
    	String masterKey = item.parent().getAttribute("masterKey").str;
    	if(masterKey !=null && !masterKey.isEmpty()) {
    		byte[] mk = hexStringToBytes(masterKey);
			byte[] mackey = My3Des(mk,em,0);
			key = mackey;
    	}
		
		return CalculateMacField(item, context.context.getTranCode(), key);
        
    }
	
}
