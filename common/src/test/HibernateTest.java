package test;

import org.hibernate.Session;

import com.dc.tes.data.IDAL;

import com.dc.tes.data.db.HibernateDAL;
import com.dc.tes.data.db.HibernateDALFactory;
import com.dc.tes.data.db.HibernateUtils;
import com.dc.tes.data.model.Case;
import com.sun.tools.javac.util.List;

public class HibernateTest {
	public static void Main(String[] args){
		IDAL<Case> caseFlow = HibernateDALFactory.GetBeanDAL(Case.class);
		List<Case> caseList = (List<Case>) caseFlow.sqlQuery("selete * from Case");
	}

}
