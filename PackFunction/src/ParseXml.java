import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dc.tes.dom.ISimpleForEachVisitor;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.ISimpleForEachVisitor.ForEachSource;
import com.dc.tes.msg.unpack.UnpackContext;


public class ParseXml {

	private static int s_id;
public static boolean parse(MsgItem item, UnpackContext context) {   
        
		try {   
        	SAXReader reader = new SAXReader(); 
			String dfString;
			try {
				dfString = new String(context.bytes, "GBK");
				System.out.print(dfString);	
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
            Document doc = reader.read((new ByteArrayInputStream(context.bytes)));
            Element root = doc.getRootElement();
            ((MsgDocument)item).put(root.getName(), readNode(root));                 
        } catch (DocumentException e) {   
            e.printStackTrace();   
        }  
        
        return true; 
    }   
         
    @SuppressWarnings("unchecked")
	public static MsgItem readNode(Element element) {      	
    	MsgItem item;
		List<Element> childNodes = element.elements();   
		if(childNodes.size() > 0) {
			item = new MsgStruct();
			item.setAttribute("name", element.getName());
			if(element.attributes().size()!=0){ //存在属性则组属性
				String attrStr = "";
				List<Attribute> lst = element.attributes();
				for(Attribute attr: lst){					
					if(!attrStr.isEmpty()){
						attrStr += " ";
					}
					attrStr += attr.getName() + "=\"" + attr.getValue() + "\"";
				}
				item.setAttribute("attr", attrStr);			
			}
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
			if(element.attributes().size()!=0){ //存在属性则组属性
				String attrStr = "";
				List<Attribute> lst = element.attributes();
				for(Attribute attr: lst){					
					if(!attrStr.isEmpty()){
						attrStr += " ";
					}
					attrStr += attr.getName() + "=\"" + attr.getValue() + "\"";
				}
				item.setAttribute("attr", attrStr);			
			}
			((MsgField)item).set(element.getText().trim());
		}
		
		return item;

    }   
}
