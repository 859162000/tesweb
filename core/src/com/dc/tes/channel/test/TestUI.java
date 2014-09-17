package com.dc.tes.channel.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.util.RuntimeUtils;

public class TestUI extends JFrame {
	private static final long serialVersionUID = 6585788743205668381L;

	private final Core m_core;

	public TestUI(Core core) {
		super();
		this.setSize(300, 200);
		this.getContentPane().setLayout(null);
		this.setTitle("TestUI");
		this.m_core = core;
		final TestUI _form = this;

		// 交易码文本框
		final JTextField txtTranCode = new JTextField();
		txtTranCode.setBounds(50, 30, 160, 20);
		txtTranCode.setText("7020");
		this.add(txtTranCode, null);

		// 案例名文本框
		final JTextField txtCaseName = new JTextField();
		txtCaseName.setBounds(50, 70, 160, 20);
		txtCaseName.setText("0");
		this.add(txtCaseName, null);

		// 发送按钮
		JButton cmdSend = new JButton();
		cmdSend.setBounds(103, 130, 71, 27);
		cmdSend.setText("SEND");
		cmdSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OutMessage out = new OutMessage();
				out.channel = "Send";
				out.tranCode = txtTranCode.getText();
				out.caseName = txtCaseName.getText();

				try {
					InMessage in = m_core.Send(out, 0);
					JOptionPane.showMessageDialog(_form, in);
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(_form, RuntimeUtils.PrintEx(ex));
				}
			}
		});
		this.add(cmdSend, null);

		// 使界面可见
		this.setVisible(true);
	}
}
