import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

public class TripleDES {

private static final String Algorithm = "DES"; //定义 加密算法,可用 DES,DESede,Blowfish

    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
             //生成密钥
             SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

             //加密
             Cipher c1 = Cipher.getInstance("DES/ECB/NoPadding");
             c1.init(Cipher.ENCRYPT_MODE, deskey);
             return c1.doFinal(src);
         } catch (java.security.NoSuchAlgorithmException e1) {
             e1.printStackTrace();
         } catch (javax.crypto.NoSuchPaddingException e2) {
             e2.printStackTrace();
         } catch (java.lang.Exception e3) {
             e3.printStackTrace();
         }
         
         return null;
     }
    
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {      
    
    	try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            //解密
            Cipher c1 = Cipher.getInstance("DES/ECB/NoPadding");
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
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
    
    static byte[] compress(byte[] src, int len)
    {
    	byte[] dest = new byte[(len+1)/2];
    	
        byte ch;
        int i;
        
        for (i=0; i<(len+1)/2; i++)
        {
            if (src[i*2]<='9' && src[i*2]>='0') 		
            	ch = (byte) ((src[i*2]-0x30)*0x10);
            else if (src[i*2]<='F' && src[i*2]>='A') 
            	ch = (byte) ((src[i*2]-0x37)*0x10);
            else if (src[i*2]<='f' && src[i*2]>='a') 
            	ch = (byte) ((src[i*2]-0x57)*0x10);
            else if (src[i*2]=='=') ch = (byte) 0xd0;
            else ch = 0x00;
    		if (src[i*2+1]<='9' && src[i*2+1]>='0') 		
    			ch = (byte) (ch + (src[i*2+1]-0x30));
            else if (src[i*2+1]<='F' && src[i*2+1]>='A') 
            	ch =  (byte) (ch + (src[i*2+1]-0x37));
            else if (src[i*2+1]<='f' && src[i*2+1]>='a') 
            	ch = (byte) (ch + (src[i*2+1]-0x57));
            else if (src[i*2+1]=='=') ch =  (byte) (ch + 0x0d);
            else ch = (byte) (ch + 0x00);
            
			dest[i] = (byte) ch;
    	}
        
        return dest;
    }
     
    public static byte[] getEnPin(String pin, String pan, byte[] pinkey) {
    	
    	String subPan;
    	
    	subPan = pan.substring(pan.length()-13,pan.length()-1);

    	byte[] sP = hexStringToBytes(subPan);
    	byte[] tmppan = new byte[8];
    	System.arraycopy(sP, 0, tmppan, 2, sP.length);
    	
    	byte[] ascpinblock= new byte[16];
    	for(int i=0; i<16; i++) {
    		ascpinblock[i] = 0x46;
    	}
    	
    	String tempin = String.format("%02d", pin.length())+pin;
    	   	
    	System.arraycopy(tempin.getBytes(), 0, ascpinblock, 0, pin.length() > 14+2 ? 14+2 : pin.length()+2);
    	
    	byte[] pinblock = compress(ascpinblock,ascpinblock.length);
    	
    	for( int i=0; i<8; i++ )
     	    tmppan[i] ^= pinblock[i];
    	
    	
    	byte[] temp = new byte[24];
    	System.arraycopy(pinkey,0,temp,0,pinkey.length);
    	System.arraycopy(pinkey,0,temp,16,8);
    	return My3Des(pinkey, tmppan, 1);
    	
    }

       
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
          
    public static Value Exec(MsgItem item, PackContext context) {  
    	
    	String masterKey = item.parent().getAttribute("masterKey").str;
    	byte[] mk = hexStringToBytes(masterKey);
    	
    	String enPin = item.parent().getAttribute("enPinKey").str;
    	byte[] ep = hexStringToBytes(enPin);        
  		 	
    	byte[] encoded = My3Des(mk, ep, 0);
    	
        String cardNum = ((MsgField)item.parent().get("b2")).value();
        encoded = getEnPin(((MsgField)item).value(), cardNum, encoded);
        
        return new Value(encoded);
    }
    
    
}

