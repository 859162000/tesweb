package test;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.dc.tes.net.Message;
import com.dc.tes.net.MessageType;
import com.dc.tes.util.RuntimeUtils;

public class MessageTest extends TestCase {
	public void testMessage() {
		System.out.println(RuntimeUtils.PrintHex(RuntimeUtils.utf8.encode(StringUtils.leftPad(String.valueOf(4), 10, '0')).array(), RuntimeUtils.utf8));
		System.out.println(RuntimeUtils.PrintHex(StringUtils.leftPad(String.valueOf(4), 10, '0').getBytes(RuntimeUtils.utf8), RuntimeUtils.utf8));

		Message msg = new Message(MessageType.MESSAGE);
		msg.put("asdf", "asdf");
		System.out.println(RuntimeUtils.PrintHex(msg.Export(), RuntimeUtils.utf8));
	}
}
