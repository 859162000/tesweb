import java.util.ArrayList;
import java.util.List;

import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.msg.unpack.UnpackContext;


public class AlterStru {

	//动态修改拆包模板结构,用于CIF系统的返回格式
	private static String used = MsgContainerUtils.C_InternalDomElementPrefix + "used";
	private static String inter = "接口";
	public static boolean parse(MsgItem item, UnpackContext context) { 
		
		//根据作业头最后一个字段 FMTCNT 来动态创建接口
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
	
			MsgStruct interInfo = (MsgStruct)item;
			
			int size = interInfo.keySet().size();
			
			MsgStruct parent = null;
			
			for(int i=0; i<size; i++) {
				
				MsgStruct face = (MsgStruct)interInfo.get(i);		
				//直接按位置比较简单吧，已知第一个为 名称，第二个为次数，第三个为长度
				//接口名
				String name = ((MsgField)face.get(0)).value();
				//重复次数
				int count = Integer.parseInt(((MsgField)face.get(1)).value());		
				//接口长度
				int len = Integer.parseInt(((MsgField)face.get(2)).value());
				
				int totalLen = (count-1) * len;
				
				MsgDocument template = context.template.getDocument();
				
				MsgStruct struct = (MsgStruct)template.get(name);
				//标记使用到的接口
				struct.setAttribute(used, true);
				
				parent = (MsgStruct) struct.parent();
				int index = parent.indexOf(struct);
				for(int j=0; j<count-1; j++)
					parent.add(index,struct.name(),struct);
				
				String rLen = parent.getAttribute("len").str;
				rLen = String.valueOf(Integer.parseInt(rLen) + totalLen);
				parent.setAttribute("len", rLen);
				
			}
			//清除没用到的接口
			List<MsgItem> itemList = new ArrayList<MsgItem>();
			if(parent != null)
				for(int i=0; i<parent.size(); i++) {
					MsgItem Item = parent.get(i);
					if(!Item.getAttribute(used).bool) {
						itemList.add(Item);
						String rLen = parent.getAttribute("len").str;
						String len = Item.getAttribute("len").str;
						rLen = String.valueOf(Integer.parseInt(rLen) - Integer.parseInt(len));
						parent.setAttribute("len", rLen);
					}
				}
			for(MsgItem Item : itemList) {
				parent.remove(Item);
			}
		}
		
			
		return true;
	}
}
