import java.util.ArrayList;
import java.util.List;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.InterfaceDef;
import com.dc.tes.data.model.InterfaceField;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.msg.unpack.UnpackContext;


/**
 * 针对WKE报文的特点，动态产生拆包模板
 * @author HO274208
 *
 */
public class DynamicUnpack {

	private static String inter = "接口";
	private static IDAL<InterfaceDef> interfaceDAL = DALFactory.GetBeanDAL(InterfaceDef.class);
	private static IDAL<InterfaceField> interfaceFieldDAL = DALFactory.GetBeanDAL(InterfaceField.class);
	
	public static boolean parse(MsgItem item, UnpackContext context) { 
		
		//根据作业头最后一个字段 FMTCNT 来动态创建接口信息模板
		if(item.name().equals("作业头")) {
			MsgStruct header = (MsgStruct)item;
			MsgField field = (MsgField) header.find("FMTCNT");
			int count = Integer.valueOf(field.value());
			MsgDocument template = context.template.getDocument();
			MsgStruct parent = (MsgStruct) template.get("作业接口");
		    //接口
			MsgStruct child = (MsgStruct) parent.get(0);
			
			for(int i=0; i<count; i++) {
				MsgStruct copy = child.Copy();
				String newname = inter+(i+1);
				copy.setAttribute("name", newname);
				parent.put(copy.name(), copy);
			}
			parent.remove(child);
			parent.setAttribute("len", count*20);
			
		} else {
			//根据接口信息模板来创建接口模板
			MsgStruct interInfo = (MsgStruct)item;
			
			int size = interInfo.keySet().size();
			
			MsgDocument template = context.template.getDocument();
			
			MsgStruct parent = (MsgStruct)template.get("作业正文");
			//清空下，兼容之前已填写好的模板
			parent.setAttribute("len", 0);
			parent.clear();
			
			for(int i=0; i<size; i++) {
				
				MsgStruct face = (MsgStruct)interInfo.get(i);		
				//直接按位置比较简单吧，已知第一个为 名称，第二个为次数，第三个为长度
				//接口名
				String name = ((MsgField)face.get(0)).value();
				//重复次数
				int count = Integer.parseInt(((MsgField)face.get(1)).value());		
				//接口长度
				int len = Integer.parseInt(((MsgField)face.get(2)).value());
				
				int totalLen = count* len;
				
				MsgStruct struct = createInterface(name);
				
				parent.put(name, struct);
				
				//重复几遍
				for(int j=0; j<count-1; j++)
					parent.add(parent.size(),name,struct);
					
				String rLen = parent.getAttribute("len").str;
				rLen = String.valueOf(Integer.parseInt(rLen) + totalLen);
				parent.setAttribute("len", rLen);		
			}

		}
		
		return true;
	}
	
	private static MsgStruct createInterface(String name) {
		
		InterfaceDef interFace = interfaceDAL.Get(Op.EQ("interfaceName", name));
		int fieldCounts = interFace.getFieldCount();
		//创建接口结构
		MsgStruct struct = new MsgStruct();
		struct.setAttribute("name", interFace.getInterfaceName());
		struct.setAttribute("desc", interFace.getChineseName());
		struct.setAttribute("len", interFace.getInterfaceLen());
		//创建接口子域
		for(int i=0; i<fieldCounts; i++) {		
			InterfaceField field = interfaceFieldDAL.Get(Op.EQ("interfaceDefId", interFace.getInterfaceId()),
														Op.EQ("sequence", i));	
			MsgField msgfield = new MsgField();
			msgfield.setAttribute("name", field.getFieldName());
			msgfield.setAttribute("desc", field.getChineseName());
			msgfield.setAttribute("len", field.getFieldLen());	
			struct.put(field.getFieldName(), msgfield);
		}	
		return struct;	
	}
	
}
