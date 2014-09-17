import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.dc.tes.util.RuntimeUtils;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class LicenseGenerator {
	private JFrame m_frame;
	private JCheckBox chkDate;
	private JTextField txtDate;
	private JCheckBox chkAdapterNum;
	private JTextField txtAdapterNum;
	private JCheckBox chkAdapter;
	private JCheckBox chkHTTP_C;
	private JTextField txtHTTP_C;
	private JCheckBox chkSOAP_C;
	private JTextField txtSOAP_C;
	private JCheckBox chkTCP_C;
	private JTextField txtTCP_C;
	private JCheckBox chkUDP_C;
	private JTextField txtUDP_C;
	private JCheckBox chkTUXEDO_C;
	private JTextField txtTUXEDO_C;
	private JCheckBox chkMQ_C;
	private JTextField txtMQ_C;
	private JCheckBox chkHTTP_S;
	private JTextField txtHTTP_S;
	private JCheckBox chkSOAP_S;
	private JTextField txtSOAP_S;
	private JCheckBox chkTCP_S;
	private JTextField txtTCP_S;
	private JCheckBox chkUDP_S;
	private JTextField txtUDP_S;
	private JCheckBox chkTUXEDO_S;
	private JTextField txtTUXEDO_S;
	private JCheckBox chkMQ_S;
	private JTextField txtMQ_S;

	private JTextArea txtLicense;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LicenseGenerator window = new LicenseGenerator();
					window.m_frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LicenseGenerator() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (UnsupportedLookAndFeelException ex) {
		}

		m_frame = new JFrame();
		m_frame.setTitle("License文件生成工具");
		m_frame.setResizable(false);
		m_frame.setBounds(100, 100, 496, 483);
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.getContentPane().setLayout(null);

		chkDate = new JCheckBox("限制日期");
		chkDate.setSelected(true);
		chkDate.setBounds(6, 6, 103, 23);
		m_frame.getContentPane().add(chkDate);

		txtDate = new JTextField();
		txtDate.setText("20131231");
		txtDate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtDate.setBounds(131, 7, 193, 21);
		m_frame.getContentPane().add(txtDate);
		txtDate.setColumns(10);

		chkAdapterNum = new JCheckBox("限制适配器数量");
		chkAdapterNum.setSelected(true);
		chkAdapterNum.setBounds(6, 37, 119, 23);
		m_frame.getContentPane().add(chkAdapterNum);

		txtAdapterNum = new JTextField();
		txtAdapterNum.setText("000012");
		txtAdapterNum.setHorizontalAlignment(SwingConstants.RIGHT);
		txtAdapterNum.setColumns(10);
		txtAdapterNum.setBounds(131, 38, 193, 21);
		m_frame.getContentPane().add(txtAdapterNum);

		chkAdapter = new JCheckBox("限制适配器种类 (下面的适配器列表 选中的为向客户提供的)");
		chkAdapter.setSelected(true);
		chkAdapter.setBounds(6, 76, 465, 23);
		m_frame.getContentPane().add(chkAdapter);

		chkHTTP_C = new JCheckBox("http.c");
		chkHTTP_C.setSelected(true);
		chkHTTP_C.setBounds(43, 130, 76, 23);
		m_frame.getContentPane().add(chkHTTP_C);

		txtHTTP_C = new JTextField();
		txtHTTP_C.setText("20131231");
		txtHTTP_C.setHorizontalAlignment(SwingConstants.RIGHT);
		txtHTTP_C.setBounds(131, 131, 103, 21);
		m_frame.getContentPane().add(txtHTTP_C);
		txtHTTP_C.setColumns(10);

		chkSOAP_C = new JCheckBox("soap.c");
		chkSOAP_C.setSelected(true);
		chkSOAP_C.setBounds(43, 161, 76, 23);
		m_frame.getContentPane().add(chkSOAP_C);

		txtSOAP_C = new JTextField();
		txtSOAP_C.setText("20131231");
		txtSOAP_C.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSOAP_C.setColumns(10);
		txtSOAP_C.setBounds(131, 162, 103, 21);
		m_frame.getContentPane().add(txtSOAP_C);

		chkTCP_C = new JCheckBox("tcp.c");
		chkTCP_C.setSelected(true);
		chkTCP_C.setBounds(43, 192, 76, 23);
		m_frame.getContentPane().add(chkTCP_C);

		txtTCP_C = new JTextField();
		txtTCP_C.setText("20131231");
		txtTCP_C.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTCP_C.setColumns(10);
		txtTCP_C.setBounds(131, 193, 103, 21);
		m_frame.getContentPane().add(txtTCP_C);

		chkUDP_C = new JCheckBox("udp.c");
		chkUDP_C.setSelected(true);
		chkUDP_C.setBounds(43, 223, 76, 23);
		m_frame.getContentPane().add(chkUDP_C);

		txtUDP_C = new JTextField();
		txtUDP_C.setText("20131231");
		txtUDP_C.setHorizontalAlignment(SwingConstants.RIGHT);
		txtUDP_C.setColumns(10);
		txtUDP_C.setBounds(131, 224, 103, 21);
		m_frame.getContentPane().add(txtUDP_C);

		chkTUXEDO_C = new JCheckBox("tuxedo.c");
		chkTUXEDO_C.setSelected(true);
		chkTUXEDO_C.setBounds(43, 254, 76, 23);
		m_frame.getContentPane().add(chkTUXEDO_C);

		txtTUXEDO_C = new JTextField();
		txtTUXEDO_C.setText("20131231");
		txtTUXEDO_C.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTUXEDO_C.setColumns(10);
		txtTUXEDO_C.setBounds(131, 255, 103, 21);
		m_frame.getContentPane().add(txtTUXEDO_C);

		chkMQ_C = new JCheckBox("mq.c");
		chkMQ_C.setSelected(true);
		chkMQ_C.setBounds(43, 286, 76, 23);
		m_frame.getContentPane().add(chkMQ_C);

		txtMQ_C = new JTextField();
		txtMQ_C.setText("20131231");
		txtMQ_C.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMQ_C.setColumns(10);
		txtMQ_C.setBounds(131, 287, 103, 21);
		m_frame.getContentPane().add(txtMQ_C);

		chkHTTP_S = new JCheckBox("http.s");
		chkHTTP_S.setSelected(true);
		chkHTTP_S.setBounds(283, 130, 76, 23);
		m_frame.getContentPane().add(chkHTTP_S);

		txtHTTP_S = new JTextField();
		txtHTTP_S.setText("20131231");
		txtHTTP_S.setHorizontalAlignment(SwingConstants.RIGHT);
		txtHTTP_S.setColumns(10);
		txtHTTP_S.setBounds(368, 130, 103, 21);
		m_frame.getContentPane().add(txtHTTP_S);

		chkSOAP_S = new JCheckBox("soap.s");
		chkSOAP_S.setSelected(true);
		chkSOAP_S.setBounds(283, 161, 76, 23);
		m_frame.getContentPane().add(chkSOAP_S);

		txtSOAP_S = new JTextField();
		txtSOAP_S.setText("20131231");
		txtSOAP_S.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSOAP_S.setColumns(10);
		txtSOAP_S.setBounds(368, 161, 103, 21);
		m_frame.getContentPane().add(txtSOAP_S);

		chkTCP_S = new JCheckBox("tcp.s");
		chkTCP_S.setSelected(true);
		chkTCP_S.setBounds(283, 192, 76, 23);
		m_frame.getContentPane().add(chkTCP_S);

		txtTCP_S = new JTextField();
		txtTCP_S.setText("20131231");
		txtTCP_S.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTCP_S.setColumns(10);
		txtTCP_S.setBounds(368, 192, 103, 21);
		m_frame.getContentPane().add(txtTCP_S);

		chkUDP_S = new JCheckBox("udp.s");
		chkUDP_S.setSelected(true);
		chkUDP_S.setBounds(283, 223, 76, 23);
		m_frame.getContentPane().add(chkUDP_S);

		txtUDP_S = new JTextField();
		txtUDP_S.setText("20131231");
		txtUDP_S.setHorizontalAlignment(SwingConstants.RIGHT);
		txtUDP_S.setColumns(10);
		txtUDP_S.setBounds(368, 223, 103, 21);
		m_frame.getContentPane().add(txtUDP_S);

		chkTUXEDO_S = new JCheckBox("tuxedo.s");
		chkTUXEDO_S.setSelected(true);
		chkTUXEDO_S.setBounds(283, 254, 76, 23);
		m_frame.getContentPane().add(chkTUXEDO_S);

		txtTUXEDO_S = new JTextField();
		txtTUXEDO_S.setText("20131231");
		txtTUXEDO_S.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTUXEDO_S.setColumns(10);
		txtTUXEDO_S.setBounds(368, 254, 103, 21);
		m_frame.getContentPane().add(txtTUXEDO_S);

		chkMQ_S = new JCheckBox("mq.s");
		chkMQ_S.setSelected(true);
		chkMQ_S.setBounds(283, 286, 76, 23);
		m_frame.getContentPane().add(chkMQ_S);

		txtMQ_S = new JTextField();
		txtMQ_S.setText("20131231");
		txtMQ_S.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMQ_S.setColumns(10);
		txtMQ_S.setBounds(368, 286, 103, 21);
		m_frame.getContentPane().add(txtMQ_S);

		JLabel lblAdapterType1 = new JLabel("发起端适配器种类");
		lblAdapterType1.setBounds(43, 105, 115, 15);
		m_frame.getContentPane().add(lblAdapterType1);

		JLabel lblAdapterType2 = new JLabel("接收端适配器种类");
		lblAdapterType2.setBounds(283, 105, 103, 15);
		m_frame.getContentPane().add(lblAdapterType2);

		JLabel lblAdapterDate1 = new JLabel("限制日期");
		lblAdapterDate1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdapterDate1.setBounds(168, 106, 66, 15);
		m_frame.getContentPane().add(lblAdapterDate1);

		JLabel lblAdapterDate2 = new JLabel("限制日期");
		lblAdapterDate2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdapterDate2.setBounds(405, 105, 66, 15);
		m_frame.getContentPane().add(lblAdapterDate2);

		JButton cmdLicense = new JButton("生成license");
		cmdLicense.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmdLicense_click(e);
			}
		});
		cmdLicense.setBounds(302, 332, 169, 23);
		m_frame.getContentPane().add(cmdLicense);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 367, 478, 82);
		m_frame.getContentPane().add(scrollPane);

		txtLicense = new JTextArea();
		txtLicense.setEditable(false);
		scrollPane.setViewportView(txtLicense);
	}

	private void cmdLicense_click(ActionEvent e) {
		// 生成license主体
		StringBuffer buffer = new StringBuffer();

		int[] adapterFlag = new int[128];
		String[] adapterDate = new String[128];
		Arrays.fill(adapterDate, "00000000");

		if (chkAdapter.isSelected()) {
			checkAdapter(chkHTTP_C, txtHTTP_C, adapterFlag, adapterDate);
			checkAdapter(chkHTTP_S, txtHTTP_S, adapterFlag, adapterDate);
			checkAdapter(chkSOAP_C, txtSOAP_C, adapterFlag, adapterDate);
			checkAdapter(chkSOAP_S, txtSOAP_S, adapterFlag, adapterDate);
			checkAdapter(chkTCP_C, txtTCP_C, adapterFlag, adapterDate);
			checkAdapter(chkTCP_S, txtTCP_S, adapterFlag, adapterDate);
			checkAdapter(chkUDP_C, txtUDP_C, adapterFlag, adapterDate);
			checkAdapter(chkUDP_S, txtUDP_S, adapterFlag, adapterDate);
			checkAdapter(chkTUXEDO_C, txtTUXEDO_C, adapterFlag, adapterDate);
			checkAdapter(chkTUXEDO_S, txtTUXEDO_S, adapterFlag, adapterDate);
			checkAdapter(chkMQ_C, txtMQ_C, adapterFlag, adapterDate);
			checkAdapter(chkMQ_S, txtMQ_S, adapterFlag, adapterDate);
		} else {
			Arrays.fill(adapterFlag, 1);
		}

		buffer.append(StringUtils.join(ArrayUtils.toObject(adapterFlag))).append("\r");
		buffer.append("|" + StringUtils.join(adapterDate, "|")).append("\r");
		buffer.append(chkDate.isSelected() ? txtDate.getText() : "00000000").append("\r");
		buffer.append(chkAdapterNum.isSelected() ? txtAdapterNum.getText() : "0");

		System.out.print(buffer);

		// 对license进行加密
		try {
			byte[] data = buffer.toString().getBytes(RuntimeUtils.utf8);
			byte[] key = "nuclearg".getBytes();

			// 进行des加密
			byte[] enData = encrypt(data, key);
			enData = ArrayUtils.addAll(enData, key);

			// 生成最终的license
			String license = new BASE64Encoder().encode(enData);

			// 对license进行验证

			// base64decode
			byte[] _enData = new BASE64Decoder().decodeBuffer(license);
			System.out.println("_enData == enData " + Arrays.equals(_enData, enData));

			// 解密
			byte[] _buffer = new byte[_enData.length - 8];
			System.arraycopy(_enData, 0, _buffer, 0, _enData.length - 8);
			byte[] _data = decrypt(_buffer, "nuclearg".getBytes());
			System.out.println("_data == data " + Arrays.equals(_data, data));

			System.out.println(new String(_data, RuntimeUtils.utf8));

			txtLicense.setText(license);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void checkAdapter(JCheckBox chk, JTextField txt, int[] adapterFlag, String[] adapterDate) {
		if (chk.isSelected()) {
			String name = chk.getText();

			int i;
			i = (name.substring(0, name.lastIndexOf('.')).hashCode() & 0x7fffffff) % 64;
			if (name.endsWith(".s"))
				i += 64;

			System.out.println("adapter = " + name + " id = " + i + " limit = " + txt.getText());

			adapterFlag[i] = 1;
			adapterDate[i] = txt.getText();
		}
	}

	/**
	 * 
	 * 加密
	 * 
	 * @param src
	 *            数据源
	 * 
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * 
	 * @return 返回加密后的数据
	 * 
	 * @throws Exception
	 * 
	 */

	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {

		//DES算法要求有一个可信任的随机数源

		SecureRandom sr = new SecureRandom();

		// 从原始密匙数据建立 DESKeySpec对象

		DESKeySpec dks = new DESKeySpec(key);

		// 建立一个密匙工厂，然后用它把DESKeySpec转换成

		// 一个SecretKey对象

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作

		Cipher cipher = Cipher.getInstance("DES");

		// 用密匙原始化Cipher对象

		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		// 现在，获取数据并加密

		// 正式执行加密操作

		return cipher.doFinal(src);

	}

	/**
	 * 
	 * 解密
	 * 
	 * @param src
	 *            数据源
	 * 
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * 
	 * @return 返回解密后的原始数据
	 * 
	 * @throws Exception
	 * 
	 */

	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

		// DES算法要求有一个可信任的随机数源

		SecureRandom sr = new SecureRandom();

		// 从原始密匙数据建立一个DESKeySpec对象

		DESKeySpec dks = new DESKeySpec(key);

		// 建立一个密匙工厂，然后用它把DESKeySpec对象转换成

		// 一个SecretKey对象

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作

		Cipher cipher = Cipher.getInstance("DES");

		// 用密匙原始化Cipher对象

		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		// 现在，获取数据并解密

		// 正式执行解密操作

		return cipher.doFinal(src);

	}
}
