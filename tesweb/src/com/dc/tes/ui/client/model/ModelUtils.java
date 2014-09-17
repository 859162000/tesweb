package com.dc.tes.ui.client.model;

//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.beanutils.BeanUtils;
//
//import com.dc.gts.data.model.Transaction;
//import com.extjs.gxt.ui.client.data.ModelData;

public class ModelUtils {
//	private static Map<Class<?>, Class<? extends ModelData>> s_clsMap = new HashMap<Class<?>, Class<? extends ModelData>>();
//
//	static {
//		s_clsMap.put(Transaction.class, GWTTransaction.class);
//	}
//
//	@SuppressWarnings("unchecked")
//	public static <T extends ModelData> T ToGWTBean(Object obj)
//			throws InstantiationException, IllegalAccessException,
//			InvocationTargetException {
//		T gwtObj = (T) s_clsMap.get(obj.getClass()).newInstance();
//		BeanUtils.copyProperties(gwtObj, obj);
//		return gwtObj;
//	}
//
//	@SuppressWarnings("unchecked")
//	public static <T> T ToModelBean(ModelData gwtObj)
//			throws InstantiationException, IllegalAccessException,
//			InvocationTargetException {
//		for (Class<?> cls : s_clsMap.keySet())
//			if (gwtObj.getClass() == s_clsMap.get(cls)) {
//				T obj = (T) s_clsMap.get(cls).newInstance();
//				BeanUtils.copyProperties(obj, gwtObj);
//				return obj;
//			}
//		return null;
//	}
//
//	public static <T extends ModelData> List<T> ToGWTBeanList(List<?> objLst)
//			throws InstantiationException, IllegalAccessException,
//			InvocationTargetException {
//		ArrayList<T> lst = new ArrayList<T>();
//		for (Object obj : objLst) {
//			T gwtObj = ToGWTBean(obj);
//			lst.add(gwtObj);
//		}
//		return lst;
//	}
//
//	public static <T> List<T> ToModelBeanList(
//			List<? extends ModelData> gwtObjLst) throws InstantiationException,
//			IllegalAccessException, InvocationTargetException {
//		ArrayList<T> lst = new ArrayList<T>();
//		for (ModelData gwtObj : gwtObjLst) {
//			T obj = ToModelBean(gwtObj);
//			lst.add(obj);
//		}
//		return lst;
//	}
}
