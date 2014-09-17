import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;


public class TraceNum {	
	
    public static Value Exec(MsgItem item, PackContext context) {
		
    	java.util.Random rand = new java.util.Random(); 
 
    	String traceNum = String.format("%06d", rand.nextInt(999999));
    	if(item instanceof MsgField) {
    		((MsgField)item).set(traceNum);
    	}
    	return new Value(traceNum);
    	
    }

}
