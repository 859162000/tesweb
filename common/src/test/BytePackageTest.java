package test;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.dc.tes.util.type.BytePackage;

public class BytePackageTest extends TestCase {

	public void testAppendByteArray() {
		BytePackage p = new BytePackage();
		p.Append("asdf".getBytes());
		assertEquals("asdf", new String(p.Export()));
		String s = StringUtils.leftPad("asdf", 1024, 'f');
		p.Append(s.getBytes());
		assertEquals("asdf" + s, new String(p.Export()));
	}

	public void testAppendByteArrayIntInt() {
		BytePackage p = new BytePackage();
		p.Append("xxxasdfyyy".getBytes(), 3, 4);
		assertEquals("asdf", new String(p.Export()));
	}

	public void testInsertByteArrayInt() {
		BytePackage p = new BytePackage();
		p.Append("asdfasdf".getBytes());
		p.Insert("123".getBytes(), 4);
		assertEquals("asdf123asdf", new String(p.Export()));
	}

	public void testInsertByteArrayIntIntInt() {
		BytePackage p = new BytePackage();
		p.Append("asdfasdf".getBytes());
		p.Insert("xxx123yyy".getBytes(), 3, 3, 4);
		assertEquals("asdf123asdf", new String(p.Export()));
	}

	public void testGetBytes() {
		BytePackage p = new BytePackage();
		p.Append("asdf".getBytes());
		assertEquals("asdf", new String(p.Export()));
	}

	public void testGetLength() {
		BytePackage p = new BytePackage();
		p.Append("asdf".getBytes());
		//assertEquals(4, p.length());
	}

	public void testDeleteInt() {
		BytePackage p = new BytePackage();
		p.Append("asdfxasdf".getBytes());
		p.Delete(4);
		assertEquals("asdfasdf", new String(p.Export()));
	}

	public void testDeleteIntInt() {
		BytePackage p = new BytePackage();
		p.Append("asdf123asdf".getBytes());
		p.Delete(4, 3);
		assertEquals("asdfasdf", new String(p.Export()));
	}

	public void testTruncationAt() {
		BytePackage p = new BytePackage();
		p.Append("asdf123".getBytes());
		p.TruncationAt(4);
		assertEquals("asdf", new String(p.Export()));
	}

	public void testIndexOf() {
		BytePackage p = new BytePackage("aabbccddee".getBytes());
		assertEquals(2, p.IndexOf("bbccdd".getBytes(), 0));
	}
}
