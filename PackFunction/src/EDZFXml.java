import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.unpack.UnpackContext;


public class EDZFXml {
	private static int s_id;
	
	public static void main(String args[]) {
		MsgService.UnpackByFile("C:\\in.txt", "C:\\xml.txt", "C:\\spec.txt", "0200");
	}
	
	public static boolean parse(MsgItem item, UnpackContext context) {   
        
		try {   
        	SAXReader reader = new SAXReader();  
        	byte[] bytes = new byte[context.length];
        	System.arraycopy(context.bytes, context.pos, bytes, 0, context.length);
            Document doc = reader.read((new ByteArrayInputStream(bytes)));
            Element root = doc.getRootElement();
            ((MsgStruct)item).put(root.getName(), readNode(root));                 
        } catch (DocumentException e) {   
            e.printStackTrace();   
        }  
        
        return true; 
    }   
     

    public static MsgItem readNode(Element element) {   
    	
    	MsgItem item;
    	
		@SuppressWarnings("unchecked")
		List<Element> childNodes = element.elements();   
		if(childNodes.size() > 0) {
			item = new MsgStruct();
			item.setAttribute("name", element.getName());
	        for (Element e : childNodes) {   	       
		        // 判断是否已经有同名的元素 如果存在同名的元素则认为这两个元素应该位于同一个数组内
				final MsgItem _child = readNode(e);
				item.ForEach(new ISimpleForEachVisitor() {
					@Override
					public void Visit(ForEachSource source, MsgItem item) {
						if (item.name().equals(_child.name())) {
							item.setAttribute("isarray", true);
							_child.setAttribute("isarray", true);
						}
					}
				});
				
				((MsgStruct)item).put(String.valueOf(s_id++), _child);
	        
	        }
		} else {
			item = new MsgField();
			item.setAttribute("name", element.getName());
			((MsgField)item).set(element.getText().trim());
		}
		
		return item;

    }   
}
