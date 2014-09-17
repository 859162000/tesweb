package com.dc.tes.ui.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.InterfaceDef;
import com.dc.tes.data.model.InterfaceField;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IInterfaceService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.dc.tes.ui.client.model.GWTInterfaceField;
import com.dc.tes.ui.client.model.GWTPack_Field;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.MsgAttribute;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.dc.tes.data.model.User;

public class InterfaceService extends RemoteServiceServlet implements
		IInterfaceService, IBeanDaoTranslate<GWTInterfaceDef, InterfaceDef> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -901279084877116968L;
	private static final Log log = LogFactory.getLog(InterfaceService.class);
	private IDAL<InterfaceDef> iDefDAL = DALFactory.GetBeanDAL(InterfaceDef.class);
	private IDAL<InterfaceField> iFieldDAL = DALFactory.GetBeanDAL(InterfaceField.class);
	@Override
	public PagingLoadResult<GWTInterfaceDef> GetInterfaceDefList(
			String systemID, String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try{
			Op[] conditions = new Op[] {
					Op.EQ(GWTInterfaceDef.N_SystemID, systemID)
			};
			int count;
			List<InterfaceDef> lstDefs;
			if(searchKey.isEmpty()){
				count = iDefDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lstDefs = iDefDAL.List(pse.getStart(), pse.getEnd(), conditions);
			}else{
				String[] properties = {
						GWTInterfaceDef.N_InterfaceName,
						GWTInterfaceDef.N_ChineseName,
						GWTInterfaceDef.N_Memo
				};
				count = iDefDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lstDefs = iDefDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}
			List<GWTInterfaceDef> returnList = new ArrayList<GWTInterfaceDef>();
			for(InterfaceDef def : lstDefs){
				returnList.add(BeanToModel(def));
			}
			return new BasePagingLoadResult<GWTInterfaceDef>(returnList, config.getOffset(), count);
		}catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Boolean SaveOrUpdateInterfaceDef(GWTInterfaceDef interfaceDef, Integer loginLogId) {
		// TODO Auto-generated method stub
		try{
			InterfaceDef def = iDefDAL.Get(new HelperService().GetDistinctOpArray(interfaceDef, 
					interfaceDef.GetInterfaceName()));
			if(def != null && def.getInterfaceId().intValue() != interfaceDef.GetInterfaceID().intValue()){
				return false;
			}
			InterfaceDef iDef = ModelToBean(def, interfaceDef);
			if(interfaceDef.IsNew()){
				iDefDAL.Add(iDef);
				OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Insert, 
						iDef.getInterfaceId(), iDef.getInterfaceName(),
						"interfaceName", null, iDef.getInterfaceName(), loginLogId);
			}else{
				def = iDefDAL.Get(Op.EQ("interfaceId", interfaceDef.GetInterfaceID()));
				iDefDAL.Edit(iDef);
				OperationLogService.writeUpdateOperationLog(OpType.InterfaceDef, InterfaceDef.class,
						def.getInterfaceId(), def.getInterfaceName(), def, iDef, loginLogId);
			}			
			return true;
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}	
	}

	@Override
	public void DeleteInterfaceDef(List<GWTInterfaceDef> interfaceDefs, Integer loginLogId) {
		// TODO Auto-generated method stub
		try{
			for(GWTInterfaceDef gwtDef : interfaceDefs){
				InterfaceDef def = ModelToBean(null, gwtDef);
				OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Delete,
						def.getInterfaceId(), def.getInterfaceName(),
						"interfaceName", def.getInterfaceName(), null, loginLogId);
				iDefDAL.Del(def);
			}
			
		}catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<GWTInterfaceField> GetInterfaceFields(
			GWTInterfaceDef interfaceDef) {
		// TODO Auto-generated method stub
		if(interfaceDef == null){
			return null;
		}
		List<InterfaceField> fields = iFieldDAL.ListAll(GWTInterfaceField.N_Sequence, true, 
				Op.EQ(GWTInterfaceField.N_InterfaceDefID, interfaceDef.GetInterfaceID()));
		List<GWTInterfaceField> result = new ArrayList<GWTInterfaceField>();
		for(InterfaceField field : fields){
			result.add(BeanToModel(field));
		}
		return result;
	}

	@Override
	public GWTInterfaceDef BeanToModel(InterfaceDef serverBean) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String importTime = sdf.format(serverBean.getImportTime());
		User user = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", serverBean.getImportUserId()));
		GWTInterfaceDef def = new GWTInterfaceDef(serverBean.getInterfaceId(),
				serverBean.getSystemId(), serverBean.getInterfaceName(), serverBean.getChineseName(),
				serverBean.getInterfaceLen(), serverBean.getFieldCount(),
				serverBean.getImportUserId(), importTime, serverBean.getMemo());
		def.SetUserName(user.getName());
		return def;
		
	}

	@Override
	public InterfaceDef ModelToBean(InterfaceDef serverBean,
			GWTInterfaceDef gwtInfo) {
		// TODO Auto-generated method stub
		if(gwtInfo == null){
			return null;
		}
		InterfaceDef bean = serverBean;
		if(bean == null){
			bean = new InterfaceDef();
			if(!gwtInfo.IsNew()){
				bean.setInterfaceId(gwtInfo.GetInterfaceID());
			}
		}
		bean.setFieldCount(gwtInfo.GetFieldCount());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			bean.setImportTime(sdf.parse(gwtInfo.GetImportTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		bean.setImportUserId(gwtInfo.GetImportUserID().equals("Administrator") ? "0" : gwtInfo.GetImportUserID());
		bean.setInterfaceLen(gwtInfo.GetInterfaceLen());
		bean.setInterfaceName(gwtInfo.GetInterfaceName());
		bean.setChineseName(gwtInfo.GetChineseName());
		bean.setMemo(gwtInfo.GetMemo());
		bean.setSystemId(gwtInfo.GetSystemID());
		return bean;
	}
	
	public GWTInterfaceField BeanToModel(InterfaceField bean){
		return new GWTInterfaceField(bean.getFieldId(), bean.getInterfaceDefId(), 
				bean.getSequence(), bean.getFieldName(), bean.getChineseName(), bean.getFieldTypeExpr(), bean.getFieldType(), 
				bean.getFieldLen(), bean.getDecimalDigits(), bean.getOptional(), bean.getDefaultValue(),
				bean.getMemo());
	}

	public InterfaceField ModelToBean(InterfaceField bean, GWTInterfaceField model){
		if(model == null){
			return null;
		}
		InterfaceField field = bean;
		if(field == null){
			field = new InterfaceField();
			if(!model.IsNew()){
				field = iFieldDAL.Get(Op.EQ(GWTInterfaceField.N_FieldID, model.GetFieldID()));
			}
		}
		field.setInterfaceDefId(model.GetInterfaceDefID());
		field.setDecimalDigits(model.GetDecimalDigits());
		field.setDefaultValue(model.GetDefaultValue());
		field.setFieldLen(model.GetFieldLen());
		field.setFieldName(model.GetFieldName());
		field.setChineseName(model.GetChineseName());
		field.setFieldType(model.GetFieldType());
		field.setFieldTypeExpr(model.GetFieldTypeExpr());
		field.setMemo(model.GetMemo());
		field.setOptional(model.GetOptional());
		field.setSequence(model.GetSequence());
		return field;
	}

	@Override
	public GWTInterfaceField SaveOrUpdateInterfaceField(GWTInterfaceField field, Integer loginLogId) {
		try{
			InterfaceField bean = iFieldDAL.Get(new HelperService().GetDistinctOpArray(field, 
					field.GetFieldName()));
			if(bean != null && bean.getFieldId().intValue() != field.GetFieldID().intValue()){
				throw new RuntimeException("保存失败，请联系管理员！");
			}
			bean = ModelToBean(bean, field);
			InterfaceDef def = iDefDAL.Get(Op.EQ("interfaceId", field.GetInterfaceDefID()));
			if(field.IsNew()){
				OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Insert, 
						def.getInterfaceId(), def.getInterfaceName(),
						"field", null, field.GetFieldName(), loginLogId);
				iFieldDAL.Add(bean);
			}else{
				InterfaceField oldField = iFieldDAL.Get(Op.EQ("fieldId", field.GetFieldID()));
				OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Update, 
						def.getInterfaceId(), def.getInterfaceName(),
						"field", oldField.getFieldName(), field.GetFieldName(), loginLogId);
				iFieldDAL.Edit(bean);
			}			
			return BeanToModel(bean);
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}	
	}

	@Override
	public void updateFieldSequence(List<GWTInterfaceField> fields) {
		// TODO Auto-generated method stub
		try {
			for(GWTInterfaceField field : fields){
				iFieldDAL.Edit(ModelToBean(null, field));
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void DeleteInterfaceField(List<GWTInterfaceField> interfaceFileds, Integer loginLogId) {
		// TODO Auto-generated method stub
		try {
			for(GWTInterfaceField field : interfaceFileds){
				InterfaceField interfaceField = ModelToBean(null, field);
				InterfaceDef def = iDefDAL.Get(Op.EQ("interfaceId", field.GetInterfaceDefID()));
				OperationLogService.writeOperationLog(OpType.InterfaceDef, IDUType.Delete,
						def.getInterfaceId(), def.getInterfaceName(),
						"interfaceField", interfaceField.getFieldName(), null, loginLogId);
				iFieldDAL.Del(interfaceField);
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTPack_Struct translateInterfaceToXML(
			List<GWTInterfaceDef> gwtInterfaceDefs) {
		// TODO Auto-generated method stub
		GWTPack_Struct root = new GWTPack_Struct();
		ISystemConfig config = SystemConfigManager
				.getConfigBySysID(gwtInterfaceDefs.get(0).GetSystemID(), 0);
		final List<MsgAttribute> fieldAttrs = config.getRespFieldAttributes();
		final List<MsgAttribute> structAttrs =  config.getRespStructAttributes();
		for(GWTInterfaceDef interfaceDef: gwtInterfaceDefs){
			InterfaceDef def = ModelToBean(null, interfaceDef);
			GWTPack_Struct struct = new GWTPack_Struct();
			for(MsgAttribute attribute: structAttrs){
				struct.set(attribute.getName(),attribute.getDefaultValue());
			}
			struct.set("name", def.getInterfaceName());
			struct.set("desc", def.getChineseName());
			struct.set("len", def.getInterfaceLen().toString());
			List<InterfaceField> fields = iFieldDAL.ListAll(GWTInterfaceField.N_Sequence, true, 
					Op.EQ(GWTInterfaceField.N_InterfaceDefID, def.getInterfaceId()));
			for(InterfaceField field : fields){
				GWTPack_Field gwtField = new GWTPack_Field();
				for(MsgAttribute attribute : fieldAttrs){
					gwtField.set(attribute.getName(), attribute.getDefaultValue());
				}
				gwtField.set("name", field.getFieldName());
				gwtField.set("desc", field.getChineseName());
				gwtField.set("len", field.getFieldLen().toString());
				gwtField.set("type", field.getFieldType());
				struct.add(gwtField);
			}
			root.add(struct);			
		}
		return root;
	}
}
