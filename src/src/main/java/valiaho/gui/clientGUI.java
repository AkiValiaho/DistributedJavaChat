package valiaho.gui;

import java.awt.*;

import javax.swing.*;

import valiaho.distributedChat.*;
import net.miginfocom.swing.MigLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.*;

public class clientGUI {

	private JFrame frmChatClient;
	private JTextField kirjoitaViesti;
	private JTextArea kaikkienViestit;
	private chatClient client;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					clientGUI window = new clientGUI();
					window.frmChatClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public clientGUI() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatClient = new JFrame();
		frmChatClient.setTitle("Chat client");
		frmChatClient.setBounds(100, 100, 741, 533);
		frmChatClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatClient.getContentPane().setLayout(new MigLayout("", "[:88.09%:73.23%,grow][grow]", "[323px,grow][62px]"));
		JScrollPane scrollPane = new JScrollPane();
		frmChatClient.getContentPane().add(scrollPane, "cell 0 0,grow");
		kaikkienViestit = new JTextArea();
		kaikkienViestit.setColumns(15);
		scrollPane.setViewportView(kaikkienViestit);
		JScrollPane scrollPane_1 = new JScrollPane();
		frmChatClient.getContentPane().add(scrollPane_1, "cell 1 0,grow");
		JTextArea kayttajat = new JTextArea();
		scrollPane_1.setViewportView(kayttajat);
		JScrollPane scrollPane_2 = new JScrollPane();
		frmChatClient.getContentPane().add(scrollPane_2, "cell 0 1,grow");
		kirjoitaViesti = new JTextField();
		scrollPane_2.setViewportView(kirjoitaViesti);
		kirjoitaViesti.setColumns(10);
		
		JButton lahetaViesti = new JButton("Send message");
		lahetaViesti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		frmChatClient.getContentPane().add(lahetaViesti, "cell 1 1,grow");
	}
}
