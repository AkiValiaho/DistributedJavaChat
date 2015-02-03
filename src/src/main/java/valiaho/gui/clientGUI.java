package valiaho.gui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import net.miginfocom.swing.*;
import valiaho.distributedChat.*;

public class clientGUI {
//TESTITESTITESTI
	private JFrame frmChatClient;
	private JTextField kirjoitaViesti;
	private JTextArea kaikkienViestit;
	private chatClient client;
	private chatClient chatClient;
	private LinkedList<String> viestit;
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
					JOptionPane.showMessageDialog(null, "Yhteytt‰ ei pystytty luomaan, palvelimet eiv‰t ole p‰‰ll‰?");
				}
			}
		});
	}
	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public clientGUI() throws IOException {
		viestit = new LinkedList<>();
		initialize();
		initializeController();
		
	}
	private void initializeController() throws IOException {
		this.chatClient = new chatClient("localhost", 12345,this);
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JButton lahetaViesti = initializeButtonsAndGUILayout();
		addActionListenerToButton(lahetaViesti);
		frmChatClient.getContentPane().add(lahetaViesti, "cell 1 2,grow");
	}
	private void addActionListenerToButton(JButton lahetaViesti) {
		lahetaViesti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					chatClient.lahetaViesti(kirjoitaViesti.getText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	private JButton initializeButtonsAndGUILayout() {
		frmChatClient = new JFrame();
		frmChatClient.setTitle("Chat client");
		frmChatClient.setBounds(100, 100, 898, 598);
		frmChatClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatClient.getContentPane().setLayout(new MigLayout("", "[:88.09%:73.23%,grow][grow]", "[][323px,grow][62px]"));
		JToolBar toolBar = new JToolBar();
		frmChatClient.getContentPane().add(toolBar, "cell 0 0");
		JScrollPane scrollPane = new JScrollPane();
		frmChatClient.getContentPane().add(scrollPane, "cell 0 1,grow");
		kaikkienViestit = new JTextArea();
		kaikkienViestit.setColumns(15);
		scrollPane.setViewportView(kaikkienViestit);
		JScrollPane scrollPane_1 = new JScrollPane();
		frmChatClient.getContentPane().add(scrollPane_1, "cell 1 1,grow");
		JTextArea kayttajat = new JTextArea();
		scrollPane_1.setViewportView(kayttajat);
		JScrollPane scrollPane_2 = new JScrollPane();
		frmChatClient.getContentPane().add(scrollPane_2, "cell 0 2,grow");
		kirjoitaViesti = new JTextField();
		scrollPane_2.setViewportView(kirjoitaViesti);
		kirjoitaViesti.setColumns(10);
		addEnterListenerToChatBox();
		JButton lahetaViesti = new JButton("Send message");
		JButton btnParempiaIhmisi = new JButton("Anna parempia ihmisi‰!");
		btnParempiaIhmisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MatcherGUI window = new MatcherGUI();
							window.frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		toolBar.add(btnParempiaIhmisi);
		return lahetaViesti;
	}

	private void addEnterListenerToChatBox() {
		kirjoitaViesti.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						chatClient.lahetaViesti(kirjoitaViesti.getText());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
	private void tulostaKayttajat() {
		//Kutsu t‰h‰n metodiin tulee serverilt‰ itselt‰‰n chatClient controllerin kautta
		//TODO
		
	}
	/**
	 * Kirjoitetaan kaikille n‰kyv‰‰n laatikkoon viesti.
	 * @param viesti Se mit‰ kirjoitetaan.
	 */
	public void kirjoitaKaikkienViesteihin(String viesti) {
		if (onkoViestejaYliRowCountin()) {
			viestit.pop();
			viestit.add(viesti);
			kaikkienViestit.setText("");
			for (String string : viestit) {
				kaikkienViestit.append(string);
				kaikkienViestit.append("\n");
				kirjoitaViesti.setText("");
			}
		} else {
			viestit.add(viesti);
			kaikkienViestit.setText("");
			for (String string : viestit) {
				kaikkienViestit.append(string);
				kaikkienViestit.append("\n");
				kirjoitaViesti.setText("");
			}
		}
	}

	private boolean onkoViestejaYliRowCountin() {
		//TODO kehit‰ t‰h‰n joku p‰tev‰ dynaaminen tarkistus
		int height = kaikkienViestit.getHeight();
		int rowCount = height / 17;
		if (viestit.size() > rowCount) {
			return true;
		}
		return false;
	}

}
