package com.dc.tes.data.xml;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dc.tes.data.model.Case;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.DataException;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;

public class CaseDao extends BaseDao<Case> {
	private static final String C_EMPTY_MSG_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><msg></msg>";

	public CaseDao() {
		super("caseId", "caseName", "transactionId", true, "caseName", "isParseable", "flag");
	}

	@Override
	protected void prepareDir(File dir, Case bean) {
		File req = new File(dir + File.separator + "data.xml");
		RuntimeUtils.WriteFile(req, C_EMPTY_MSG_XML, RuntimeUtils.utf8);
		File resp = new File(dir + File.separator + "data.dat");
		RuntimeUtils.WriteFile(resp, ArrayUtils.EMPTY_BYTE_ARRAY);
	}

	@Override
	protected Case newInstance(Node n, String parent, File dir) {
		final Element _e = (Element) n;
		final File _dir = dir;
		return new Case() {
			private static final long serialVersionUID = 0L;

			@Override
			public byte[] getRequestMsg() {
				if (super.getRequestMsg() == null)
					try {
						super.setRequestMsg(RuntimeUtils.ReadFile(new File(_dir + File.separator + _e.getAttribute("caseName") + File.separator + "data.dat")));
					} catch (Exception ex) {
						throw new DataException(ex);
					}

				return super.getRequestMsg();
			}

			@Override
			public String getRequestXml() {
				if (super.getRequestXml() == null)
					try {
						super.setRequestXml(RuntimeUtils.ReadFile(new File(_dir + File.separator + _e.getAttribute("caseName") + File.separator + "data.xml"), RuntimeUtils.utf8));
					} catch (Exception ex) {
						throw new DataException(ex);
					}
				return super.getRequestXml();
			}
		};
	}

	@Override
	protected void toXml(Case bean, Node n) {
		try {
			super.toXml(bean, n);

			RuntimeUtils.WriteFile(new File(path(bean, bean.getCaseName() + File.separator + "data.dat")), bean.getRequestMsg());
			RuntimeUtils.WriteFile(new File(path(bean, bean.getCaseName() + File.separator + "data.xml")), bean.getRequestXml(), RuntimeUtils.utf8);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}
}
