package com.dc.tes.data.xml;

import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dc.tes.data.model.Transaction;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.DataException;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;

/**
 * 对Transaction进行CRUD的dao类
 * 
 * @author huangzx
 * 
 */
public class TransactionDao extends BaseDao<Transaction> {
	private static final String C_EMPTY_CASES_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><cases></cases>";
	private static final String C_EMPTY_MSG_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><msg></msg>";

	public TransactionDao() {
		super("transactionId", "tranCode", "systemId", true, "tranCode", "tranName", "isClientSimu", "description", "category", "flag");
	}

	@Override
	protected void prepareDir(File dir, Transaction bean) {
		File trans = new File(dir + File.separator + "cases.xml");
		RuntimeUtils.WriteFile(trans, C_EMPTY_CASES_XML, RuntimeUtils.utf8);
		File req = new File(dir + File.separator + "req.xml");
		RuntimeUtils.WriteFile(req, C_EMPTY_MSG_XML, RuntimeUtils.utf8);
		File resp = new File(dir + File.separator + "resp.xml");
		RuntimeUtils.WriteFile(resp, C_EMPTY_MSG_XML, RuntimeUtils.utf8);
		File script = new File(dir + File.separator + "script.js");
		RuntimeUtils.WriteFile(script, "", RuntimeUtils.utf8);
	}

	@Override
	protected Transaction newInstance(Node n, String parent, File dir) {
		final Element _e = (Element) n;
		final File _dir = dir;
		return new Transaction() {
			private static final long serialVersionUID = 2830920983961730844L;

			@Override
			public String getRequestStruct() {
				if (super.getRequestStruct() == null)
					try {
						super.setRequestStruct(RuntimeUtils.ReadFile(new File(_dir + File.separator + _e.getAttribute("tranCode") + File.separator + "req.xml"), RuntimeUtils.utf8));
					} catch (Exception ex) {
						throw new DataException(ex);
					}

				return super.getRequestStruct();
			}

			@Override
			public String getResponseStruct() {
				if (super.getResponseStruct() == null)
					try {
						super.setResponseStruct(RuntimeUtils.ReadFile(new File(_dir + File.separator + _e.getAttribute("tranCode") + File.separator + "resp.xml"), RuntimeUtils.utf8));
					} catch (Exception ex) {
						throw new DataException(ex);
					}

				return super.getResponseStruct();
			}

			@Override
			public String getScript() {
				if (super.getScript() == null)
					try {
						super.setScript(RuntimeUtils.ReadFile(new File(_dir + File.separator + _e.getAttribute("tranCode") + File.separator + "script.txt"), RuntimeUtils.utf8));
					} catch (Exception ex) {
						throw new DataException(ex);
					}

				return super.getScript();
			}
		};
	}

	@Override
	protected void toXml(Transaction bean, Node n) {
		try {
			super.toXml(bean, n);

			RuntimeUtils.WriteFile(new File(path(bean, bean.getTranCode() + File.separator + "req.xml")), bean.getRequestStruct(), RuntimeUtils.utf8);
			RuntimeUtils.WriteFile(new File(path(bean, bean.getTranCode() + File.separator + "resp.xml")), bean.getResponseStruct(), RuntimeUtils.utf8);
			RuntimeUtils.WriteFile(new File(path(bean, bean.getTranCode() + File.separator + "script.js")), bean.getScript(), RuntimeUtils.utf8);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.XmlFail, ex);
		}
	}
}
