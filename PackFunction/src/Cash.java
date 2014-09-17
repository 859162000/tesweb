import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;


public class Cash {
	
    public static Value Exec(MsgItem item, PackContext context) {
    	
    	String cash = ((MsgField)item).value();
    	if(cash.isEmpty())
    		cash = "0";
    	Boolean isRMB = true;
    	if(((MsgField)((MsgDocument)item.parent()).get("FIELD 49")) != null) {
    		String typeOfCash = ((MsgField)((MsgDocument)item.parent()).get("FIELD 49")).value();
    		isRMB = typeOfCash.equalsIgnoreCase("156");
    	}
   	
    	String[] sCash = cash.split("\\.");
    	String rCash = "";
    	
    	if(sCash.length == 1) {
    		if(isRMB) {
    			rCash = String.format("%012d", Integer.parseInt(cash+"00"));
    		} else {
    			rCash = String.format("%012d", Integer.parseInt(cash));
    		}
    	} else {
    		if(isRMB) {
        		if(sCash[1].length()==1) {
        			rCash = String.format("%012d", Integer.parseInt(sCash[0]+sCash[1]+"0"));
        		} else {
        			rCash = String.format("%012d", Integer.parseInt(sCash[0]+sCash[1]));
        		}
    		} else {
    			// 以下是外币处理方式
	    		StringBuilder decimal = new StringBuilder(sCash[1]) ;
	    		for(int i=0; i<decimal.length(); i++) {
	    			if(decimal.charAt(i) != '0')
	    				break;
	    			if( i == decimal.length()-1)
	    				rCash = String.format("%012d", Integer.parseInt(sCash[0]));
	    		}
	    		if(decimal.length() >= 3) {
	    			for(int i=2; i<decimal.length(); i++) {
	    				decimal.setCharAt(i, '0');
	    			}
	    		}
	    		String value = sCash[0] + decimal.toString();
	    		rCash = String.format("%012d", Integer.parseInt(value));
    		}
    	}
    	((MsgField)item).set(rCash);
    	return new Value(rCash);
    }

}
