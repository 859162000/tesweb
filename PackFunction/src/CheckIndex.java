import java.util.Random;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgField;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

public class CheckIndex {
	
	private static Random random = new Random();
	
    public static Value Exec(MsgItem item, PackContext context) {
    	
    	java.text.SimpleDateFormat sm = new  java.text.SimpleDateFormat("ddHHmmss");
    	String checkIndex = sm.format(new java.util.Date());

    	int iRandom = Math.abs(random.nextInt());
    	int iRnd4 = iRandom % 10000;
    	String strRnd4 = String.format("%04d", iRnd4);
    	String index = checkIndex + strRnd4;
    	if(item instanceof MsgField) {
    		((MsgField)item).set(index);
    	}
    	return new Value(index);
    }

}
