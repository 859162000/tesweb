package test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.PackSpecification;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.unpack.UnpackSpecification;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.ByteArrayUtils;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.RuntimeUtils;

public class FormatTest extends TestCase {
	public void _testTest() {
		test("test");
	}

	public void _testQueryString() {
		test("querystring");
	}

	public void _testFix() {
		test("fix");
	}

	public void _testSimpleXml() {
		test("simplexml");
	}

	public void _testSimpleXml2() {
		test("simplexml2");
	}

	public void _testXml() {
		test("xml");
	}

	public void _testXml2() {
		test("xml2");
	}

	public void _test8583() {
		test("8583");
	}

	public void _testCTS2() {
		test("cts2");
	}

	public static boolean unpack(MsgItem item, UnpackContext context) {
		int pos = context.pos, pos2;
		byte[] SOH = { 1 };

		// colCount
		pos2 = ByteArrayUtils.IndexOf(context.bytes, SOH, pos);
		String colCountStr = new String(ByteArrayUtils.SubArray(context.bytes, pos, pos2 - pos), context.spec.encoding);
		pos = pos2 + 1;
		int colCount = Integer.parseInt(colCountStr);

		// rowCount
		pos2 = ByteArrayUtils.IndexOf(context.bytes, SOH, pos);
		String rowCountStr = new String(ByteArrayUtils.SubArray(context.bytes, pos, pos2 - pos), context.spec.encoding);
		pos = pos2 + 1;
		int rowCount = Integer.parseInt(rowCountStr);

		// title
		String[] title = new String[colCount];
		for (int i = 0; i < colCount; i++) {
			pos2 = ByteArrayUtils.IndexOf(context.bytes, SOH, pos);
			title[i] = new String(ByteArrayUtils.SubArray(context.bytes, pos, pos2 - pos), context.spec.encoding);
			pos = pos2 + 1;
		}

		// body
		MsgDocument doc = (MsgDocument) item;
		MsgArray root = new MsgArray();
		doc.put("root", root);

		for (int i = 0; i < rowCount; i++) {
			MsgStruct stru = new MsgStruct();
			stru.setAttribute("name", "root");
			stru.setAttribute("isarray", true);

			for (int j = 0; j < colCount; j++) {
				pos2 = ByteArrayUtils.IndexOf(context.bytes, SOH, pos);
				String data = new String(ByteArrayUtils.SubArray(context.bytes, pos, pos2 - pos), context.spec.encoding);
				pos = pos2 + 1;

				MsgField field = new MsgField();
				field.setAttribute("name", title[j]);
				field.set(data);
				stru.put(title[j], field);
			}
			root.add(stru);
		}

		context.length = context.bytes.length - context.pos;
		return true;
	}

	public static boolean bitmap(MsgItem item, UnpackContext context) {
		// 解析位图

		int bmLen = context.bytes[2] < 0 ? 16 : 8;// 用符号位判断是否存在扩展位图

		byte[] bitmap = new byte[bmLen];
		System.arraycopy(context.bytes, 2, bitmap, 0, bmLen);

		// 根据位图中的内容 砍掉模板中不应该存在的域 以使模板与报文相匹配
		for (int bmIndex = 0; bmIndex < bitmap.length; bmIndex++)
			for (int i = 0; i < 8; i++)
				if ((1 << (7 - i) & bitmap[bmIndex]) == 0)
					((MsgStruct) context.template).remove("b" + String.valueOf(bmIndex * 8 + i + 1));

		// 设置位图长度
		context.length = bmLen;
		return true;
	}

	public void _testXmlArray() {
		test("xmlarray");
	}

	public void _testXmlArray2() {
		test("xmlarray2");
	}

	public void _testDCC() {
		test("dcc");
	}

	public void _testMML() {
		test("MML");
	}

	public void _testVGOP() {
		test("vgop");
	}

	public void _testfix2() {
		test("fix2");
	}

	public void _test太保银保通() {
		test("太保银保通");
	}

	public void _testSoap() {
		test("soap");
	}

	public void test兴业银期() {
		test("兴业银期");
	}

	public static Value md5(MsgItem item, PackContext context) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(item.getAttribute(MsgContainerUtils.C_InternalDomElementPrefix + "m").bytes);
		return new Value(md.digest());
	}

	public void testMd5() throws NoSuchAlgorithmException {
		byte[] bytes = "".getBytes();
		for (int i = 0; i < bytes.length; i++)
			if (bytes[i] == '*')
				bytes[i] = 0;

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(bytes);
		System.out.println(StringUtils.join(HexStringUtils.ToHexString(md.digest()).split("\\s")).toLowerCase());
	}

	private void test(String name) {
		IContext context = new IContext() {
			@Override
			public String getTranCode() {
				return "1100";
			}
		};

		try {
			PackSpecification style = MsgService.LoadPackSpecification(RuntimeUtils.OpenResource("test/" + name + ".style.xml"));
			MsgDocument doc = MsgLoader.Load(RuntimeUtils.OpenResource("test/" + name + ".data.xml"));

			byte[] bytes = MsgService.Pack(doc, style, context);
			System.out.println(new String(bytes, style.encoding));
			System.out.println(RuntimeUtils.PrintHex(bytes, style.encoding));

			MsgDocument template = MsgLoader.Load(RuntimeUtils.OpenResource("test/" + name + ".stru.xml"));

			UnpackSpecification rule2 = MsgService.LoadUnpackSpecification(RuntimeUtils.OpenResource("test/" + name + ".rule2.xml"));
			MsgDocument result2 = MsgService.Unpack(bytes, template, rule2, context);
			System.out.println(result2);
			assertEquals(doc.toString(), result2.toString());
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
}
