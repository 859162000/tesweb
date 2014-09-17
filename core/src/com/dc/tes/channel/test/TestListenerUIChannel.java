package com.dc.tes.channel.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import test.UnpackTest;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

@ComponentClass(type = ComponentType.Channel)
public class TestListenerUIChannel extends JFrame implements IListenerChannel {
	private static final long serialVersionUID = 6585788743205668381L;
	private JTextField txtChannelName;
	private JTextArea txtMsg;

	private Core core;

	public TestListenerUIChannel() {
		this.setSize(472, 405);
		this.setTitle("TestListenerUI");
		getContentPane().setLayout(null);

		txtChannelName = new JTextField();
		txtChannelName.setBounds(10, 10, 137, 21);
		txtChannelName.setText("listen");
		getContentPane().add(txtChannelName);
		txtChannelName.setColumns(10);

		JButton cmdReceive = new JButton("Receive");
		cmdReceive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmdReceive_click(e);
			}
		});
		cmdReceive.setBounds(195, 336, 118, 23);
		getContentPane().add(cmdReceive);

		JButton cmdReceiveHEX = new JButton("Receive HEX");
		cmdReceiveHEX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmdReceiveHEX_click(e);
			}
		});
		cmdReceiveHEX.setBounds(328, 336, 118, 23);
		getContentPane().add(cmdReceiveHEX);

		txtMsg = new JTextArea();
		txtMsg.setBounds(10, 41, 436, 285);
		getContentPane().add(txtMsg);

		// 使界面可见
		this.setVisible(true);
	}

	private void cmdReceive_click(ActionEvent e) {
		this.receive(this.txtMsg.getText().getBytes());
	}

	private void cmdReceiveHEX_click(ActionEvent e) {
//		ByteArrayInputStream in = new ByteArrayInputStream(this.txtMsg.getText().getBytes());
//		try {
//			//this.receive(UnpackTest.readHex(in));
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
	}

	private void receive(final byte[] bytes) {
		final IListenerChannel _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				InMessage in = new InMessage();
				in.channel = txtChannelName.getText();
				in.bin = bytes;

				core.Notify(_this, in);
			}
		}).start();
	}

	@Override
	public void Reply(OutMessage out, Thread original) throws Exception {
		JOptionPane.showMessageDialog(this, out.toString());
	}

	@Override
	public void Reply(OutMessage[] list, Thread original) throws Exception {
		for (OutMessage msg : list)
			JOptionPane.showMessageDialog(this, msg.toString());
	}

	@Override
	public void Start(Core core) throws Exception {
		this.core = core;
	}

	@Override
	public void Stop() throws Exception {
	}

	@Override
	public boolean getChannelState() {
		return true;
	}
}
