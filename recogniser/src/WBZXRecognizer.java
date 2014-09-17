import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;


public class WBZXRecognizer {
	//获取发送报文的操作码
	public static String RecogniseFromXml(byte[] bin) {
		String msg = new String(bin);
		String strStartPos = "<OPERCODE>";
		String strEndPos = "</OPERCODE>";
		int startIndex = msg.indexOf(strStartPos)+10;
		int endIndex = msg.indexOf(strEndPos);
		if(startIndex == -1 || endIndex == -1){
			throw new TESException(MsgErr.Pack.AttributeNotFound,"发送报文 操作码无法获取: CFX.HEAD.OPERCODE");
		}else{
			return msg.substring(startIndex, endIndex);
		}
		/**String strStartPos = "<![CDATA[";
		String strEndPos = "]]>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element theTranCode = null, root = null;
		try{
		int startIndex = msg.indexOf(strStartPos)+9;
		int endIndex = msg.indexOf(strEndPos);
		msg = msg.substring(startIndex, endIndex);
		
		factory.setIgnoringElementContentWhitespace(true);
		
		DocumentBuilder db = factory.newDocumentBuilder();

		final byte[] bytes = msg.getBytes("GB2312");
		final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		final InputSource source = new InputSource(is);

		Document xmldoc = db.parse(source);		
		root = xmldoc.getDocumentElement();
		theTranCode = (Element) XMLRecognizer.selectSingleNode("/CFX/HEAD/OPERCODE", root);
		//Element nameNode = (Element) theTranCode.getElementsByTagName("price").item(0);
		if (theTranCode != null) {
			return theTranCode.getFirstChild().getNodeValue();
		}
		else {
			throw new TESException(MsgErr.Pack.AttributeNotFound,"发送报文 操作码无法获取: CFX.HEAD.OPERCODE");
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		*/
	}
}
