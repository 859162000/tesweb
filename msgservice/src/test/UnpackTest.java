package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.unpack.UnpackSpecification;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.type.BytePackage;

public class UnpackTest extends TestCase {
	public void testUnpack() throws IOException {
		UnpackSpecification rule2 = MsgService.LoadUnpackSpecification(RuntimeUtils.OpenResource("test/unpack.rule.xml"));
		MsgDocument result2 = MsgService.Unpack(RuntimeUtils.ReadResource("test/data.txt"), null, rule2, null);
		System.out.println(result2);
	}

	public static byte[] readHex(InputStream in) throws IOException {
		BytePackage p = new BytePackage();

		BufferedReader r = new BufferedReader(new InputStreamReader(in));

		String ln = r.readLine();
		while ((ln = r.readLine()) != null) {
			String body = ln.split("\\|")[1].trim();
			p.Append(HexStringUtils.FromHexString(body));
		}

		return p.Export();
	}
}
