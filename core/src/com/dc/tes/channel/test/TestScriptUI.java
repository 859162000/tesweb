package com.dc.tes.channel.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.dc.tes.fcore.FCore;
import com.dc.tes.util.RuntimeUtils;

public class TestScriptUI extends JFrame {
	private static final long serialVersionUID = 6585788743205668381L;

	private final FCore m_core;

	public TestScriptUI(FCore core) {
		super();
		this.setSize(300, 450);
		this.getContentPane().setLayout(null);
		this.setTitle("TestScriptUI");
		this.m_core = core;
		final TestScriptUI _form = this;

		// 脚本文本框
		final JTextArea txtScript = new JTextArea();
		txtScript.setBounds(5, 5, 280, 362);
		getContentPane().add(txtScript, null);

		// 发送按钮
		JButton cmdSend = new JButton();
		cmdSend.setBounds(90, 377, 100, 25);
		cmdSend.setText("SEND");
		cmdSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							m_core.RunScript(txtScript.getText(), UUID.randomUUID().toString(), "","","",false);
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(_form, RuntimeUtils.PrintEx(ex));
						}
						JOptionPane.showMessageDialog(_form, "script ok");
					}
				}).start();
			}
		});
		getContentPane().add(cmdSend, null);

		// 使界面可见
		this.setVisible(true);
	}
}
