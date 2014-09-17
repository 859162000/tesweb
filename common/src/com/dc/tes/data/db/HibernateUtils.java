package com.dc.tes.data.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * Hibernate工具类
 * 
 * @author huangzx
 * 
 */
public class HibernateUtils {
	private static final Log log = LogFactory.getLog(HibernateUtils.class);

	private static Configuration s_config;
	private static SessionFactory s_factory;

	/**
	 * 初始化Hibernate配置
	 */
	public static void Init(String url) {
		log.info("Hibernate initializing... ");
		try {
			s_config = new Configuration();

			Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
			String conn = XmlUtils.SelectNodeText(doc, "//config/conn");
			String user = XmlUtils.SelectNodeText(doc, "//config/username");
			String pwd = XmlUtils.SelectNodeText(doc, "//config/password");

			s_config.configure(url);

			s_config.setProperty("hibernate.connection.url", conn);
			s_config.setProperty("hibernate.connection.username", user);
			s_config.setProperty("hibernate.connection.password", pwd);

			s_factory = s_config.buildSessionFactory();

			log.info("Hibernate initialization finished.");
		} catch (Exception ex) {
			ex.printStackTrace();
			s_config = null;
			s_factory.close();
			throw new TESException(CommonErr.Dal.HibernateInitFail, ex);
		}
	}

	/**
	 * 创建一个HibernateSession实例
	 */
	public static Session GetSession() {
		if (s_factory == null)
			throw new TESException(CommonErr.Dal.HibernateNotInitialized);

		return s_factory.openSession();
	}

	/**
	 * 关闭Hibernate连接
	 */
	public synchronized static void Close() {
		if (s_factory != null) {
			try {
				s_factory.close();
			} catch (Exception ex) {
				log.error("HibernateUtils中的Close方法关闭SessionFactory失败", ex);
			}
		}
	}
}
