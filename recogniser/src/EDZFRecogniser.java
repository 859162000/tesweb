import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;


public class EDZFRecogniser {
	
	public static String RecogniseFromXml(byte[] bin) {
		MsgDocument doc = MsgLoader.LoadXml(new String(bin));
		MsgField field = doc.SelectSingleField("root.head.transid");
		if(field == null) {
			throw new TESException(MsgErr.Pack.AttributeNotFound,"请求报文较易识别标识符: root.head.transid");
		}
	
		return field.value();
	}
	
	//获取发送报文的流水号
	public static String RecogniseFromOut(byte[] bin) {
		MsgDocument doc = MsgLoader.LoadXml(new String(bin));
		//MsgField field = doc.SelectSingleField("PacketHeader.Header.MesgID");
		//MsgField field = doc.SelectSingleField("1101.COCMMHDRY1.YKEYVAL");
		MsgField field = doc.SelectSingleField("1101.COCMMHDRY1.YKEYVAL");
		if(field == null)
			throw new TESException(MsgErr.Pack.AttributeNotFound,"发送报文 通讯级标识符: PacketHeader.Header.MesgID");
		
		return field.value();
		
	}
	
	//获取返回报文的流水号
	//在最后一个KPMSGHDRX接口中(KPMSGHDRX可能有返回多个)
	public static String RecogniseFromIn(byte[] bin) {
		MsgDocument doc = MsgLoader.LoadXml(new String(bin));
		MsgItem item = doc.SelectSingleNode("作业正文.KPMSGHDRX");
		if(item == null) {
			item = doc.SelectSingleNode("作业正文.KPMSGFERX");
			if(item == null) {
				throw new TESException(MsgErr.Pack.AttributeNotFound,"返回报文 报文头接口: KPMSGHDRX 或 异常接口KPMSGFERX");
			}
		}
		
		if(item instanceof MsgArray) {
			item = ((MsgArray) item).get(((MsgArray) item).size()-1);
		}
			
		MsgField field = item.SelectSingleField("xCmmIdn");
		
		if(field == null)
			throw new TESException(MsgErr.Pack.AttributeNotFound,"返回报文 通讯级标识符: xCmmIdn");
	
		return field.value();
	}

}
