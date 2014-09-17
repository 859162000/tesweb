import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;


public class CUPSRecogniser {
	
	public static String Recognise(byte[] bin) {
		//银联索引是从报文中获取37域
		MsgDocument doc = MsgLoader.LoadXml(new String(bin));
		
		if(doc.get("b37") == null)
			throw new TESException(MsgErr.Pack.AttributeNotFound,"第37域");
		
		return ((MsgField)doc.get("b37")).value();
		
	}

}
