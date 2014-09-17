package com.dc.tes.ui.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.model.Transaction;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.msg.util.Value;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.dc.tes.ui.util.ExcelSerializer;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.util.type.Pair;

public class PackTemplateDownload extends HttpServlet{
	private static final long serialVersionUID = -3531600685251999930L;
	private static final Log log = LogFactory.getLog(PackTemplateDownload.class);
	
	public PackTemplateDownload() {
		super();

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse resp)
	{
		try
		{
		}
		catch(Exception ex)
		{
			log.error(ex, ex);
			ex.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try
		{
			Map<String, Pair<MsgDocument, MsgDocument>> map = new HashMap<String, Pair<MsgDocument, MsgDocument>>();
			MsgDocument docIn = null;
			MsgDocument docOut = null;
			
			//获得系统ID
			String systemID = req.getParameter(GWTSimuSystem.N_SystemID);
			int isClientSimu = Integer.parseInt(req.getParameter("isClientSimu"));
			
			//获得系统名称,获得系统配置
			GWTSimuSystem system = new SimuSystemService().GetInfo(systemID);
			ISystemConfig systemConfig = SystemConfigManager.getConfig(system.GetSystemName(),isClientSimu);
			
			File file = new HelperService().CreateTempFile("PackTemplate_", ".xls");
			FileOutputStream stream = new FileOutputStream(file);
			System.out.println(file.getAbsolutePath());
			Transaction tran = new Transaction();
			tran.setTranCode("交易码");
			tran.setTranName("请填写交易名称");
			tran.setDescription("请填写交易描述信息");
			
			docIn = GetDemoDocument(tran,systemConfig.getReqFieldAttributes(),systemConfig.getReqStructAttributes());
			docOut = GetDemoDocument(tran,systemConfig.getRespFieldAttributes(),systemConfig.getRespStructAttributes());
			map.put(tran.getTranName(), new Pair<MsgDocument, MsgDocument>(docIn, docOut));
			
			ExcelSerializer.toExcel(map, stream, systemConfig, false);

			stream.flush();
			stream.close();
		
			String fileName = file.getName();
			resp.getOutputStream().write(fileName.getBytes());
			resp.flushBuffer();
		}
		catch(Exception ex)
		{
//			log.error(ex, ex);
			ex.printStackTrace();
			resp.getOutputStream().write("error:报文结构模板获取失败".getBytes());
			resp.flushBuffer();
		}
		
		
	}
	
	private MsgDocument GetDemoDocument(Transaction tran,List<MsgAttribute> attsField,List<MsgAttribute> attsStruct)
	{
		MsgDocument demoDoc = new MsgDocument();
		
		MsgField demoField = new MsgField();
		demoField.setAttributes(GetAttrMap(attsField,true));
		MsgStruct demoStru = new MsgStruct();
		demoStru.setAttributes(GetAttrMap(attsStruct,false));
		demoStru.put("1", demoField);

		demoDoc.put("2", demoStru);

		demoDoc.setAttribute("tranName", new Value(tran.getTranName()));
		demoDoc.setAttribute("tranDesc", new Value(tran.getDescription()));
		return demoDoc;
	}
	
	private Map<String,Value> GetAttrMap(List<MsgAttribute> atts,boolean isField)
	{
		Map<String,Value> attrMap = new HashMap<String,Value>();
		for(MsgAttribute attr : atts)
		{
			String attrName = attr.getName();
			String defaultValue = attr.getDefaultValue();
			if(defaultValue.isEmpty())
			{
				if(attrName.toLowerCase().compareTo("name") == 0)
				{
					defaultValue = isField ? "field_demo" : "struct_demo";
				}
				else if(attrName.toLowerCase().compareTo("desc") == 0)
				{
					defaultValue = isField ? "字段类型" : "结构类型";
				}
				else if(!attr.getListItems().isEmpty())
				{
					try
					{
						String[] lists = attr.getListItems().split("|");
						defaultValue = lists[0];
					}
					catch (Exception e) {
						defaultValue = attr.getListItems();
					}
				}
				else
				{
					defaultValue = "*";
				}
			}
			attrMap.put(attrName, new Value(defaultValue));
		}
		return attrMap;
	}
}
