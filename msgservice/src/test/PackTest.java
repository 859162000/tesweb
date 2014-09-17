package test;

import java.io.IOException;

import junit.framework.TestCase;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.pack.PackSpecification;
import com.dc.tes.util.RuntimeUtils;

public class PackTest extends TestCase {
	public void testPack() throws IOException {
		PackSpecification spec = MsgService.LoadPackSpecification(RuntimeUtils.OpenResource("test/pack.style.xml"));
		MsgDocument doc = MsgLoader.Load(RuntimeUtils.OpenResource("test/pack.data.xml"));

		byte[] bytes = MsgService.Pack(doc, spec, new IContext() {
			@Override
			public String getTranCode() {
				return "1110";
			}
		});

		System.out.print(new String(bytes, spec.encoding));
		System.out.print(RuntimeUtils.PrintHex(bytes, spec.encoding));
	}
}
