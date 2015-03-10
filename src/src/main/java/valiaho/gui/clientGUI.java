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
	private chatClient chatClient;
	private chatClient client;
	private JButton btnParempiaIhmisi;
	private JButton lahetaViesti;
	private JFrame frmChatFrame;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JTextArea kaikkienViestit;
	private JTextArea kayttajat;
	private JTextField kirjoitaViesti;
	private JToolBar toolBar;
	private LinkedList<String> viestit;
	//Konstruktorit
	public clientGUI() throws IOException {
		viestit = new LinkedList<>();
		initialize();
		initializeController();
	}
	/**
	 * Main-ohjelma
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					clientGUI window = new clientGUI();
					window.frmChatFrame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Yhteyttä ei pystytty luomaan, palvelimet eivät ole päällä?");
				}
			}
		});
	}
	//Initialisaattorit
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JButton lahetaViesti = initializeButtonsAndGUILayout();
		addActionListenerToButton(lahetaViesti);
		frmChatFrame.getContentPane().add(lahetaViesti, "cell 1 2,grow");
	}
	/**
	 * Create the application.
	 * @throws IOException 
	 */
	private void initializeController() throws IOException {
		this.chatClient = new chatClient("localhost", 12345,this);
	}
	private JButton initializeButtonsAndGUILayout() {
		initializeComponentsWithNew();
		initializeChatFrameRelatedStuff();
		textPaneStuff();
		scrollPaneStuff();
		addEnterListenerToChatBox();
		addActionListenerToButton();
		toolbarRelatedConfigStuff();
		return lahetaViesti;
	}
	private void initializeComponentsWithNew() {
		btnParempiaIhmisi = new JButton("Anna parempia ihmisiä!");
		frmChatFrame = new JFrame();
		frmChatFrame.getContentPane().setLayout(new MigLayout("", "[:88.09%:73.23%,grow][grow]", "[][323px,grow][62px]"));
		kaikkienViestit = new JTextArea();
		kayttajat = new JTextArea();
		kirjoitaViesti = new JTextField();
		lahetaViesti = new JButton("Send message");
		scrollPane = new JScrollPane();
		scrollPane_1 = new JScrollPane();
		scrollPane_2 = new JScrollPane();
		toolBar = new JToolBar();
	}
	private void initializeChatFrameRelatedStuff() {
		frmChatFrame.setTitle("Chat client");
		frmChatFrame.setBounds(100, 100, 898, 598);
		frmChatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatFrame.getContentPane().add(toolBar, "cell 0 0");
		frmChatFrame.getContentPane().add(scrollPane, "cell 0 1,grow");
		frmChatFrame.getContentPane().add(scrollPane_1, "cell 1 1,grow");
		frmChatFrame.getContentPane().add(scrollPane_2, "cell 0 2,grow");
	}
	private void toolbarRelatedConfigStuff() {
		toolBar.add(btnParempiaIhmisi);
	}
	private void textPaneStuff() {
		kirjoitaViesti.setColumns(10);
		kaikkienViestit.setColumns(15);
	}
	private void scrollPaneStuff() {
		scrollPane.setViewportView(kaikkienViestit);
		scrollPane_1.setViewportView(kayttajat);
		scrollPane_2.setViewportView(kirjoitaViesti);
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
		//Kutsu tähän metodiin tulee serveriltä itseltään chatClient controllerin kautta
		//TODO
	}
	/**
	 * Kirjoitetaan kaikille näkyvään laatikkoon viesti.
	 * @param viesti Se mitä kirjoitetaan.
	 */
	public void kirjoitaKaikkienViesteihin(String viesti) {
		if (onkoViestejaYliRowCountin()) {
			viestit.pop();
			viestit.add(viesti);
			kaikkienViestit.setText("");
			for (String string : viestit) {
				kirjoitaKaikkienViesteihinOikein(string);
			}
		} else {
			viestit.add(viesti);
			kaikkienViestit.setText("");
			for (String string : viestit) {
				kirjoitaKaikkienViesteihinOikein(string);
			}
		}
	}
	private void kirjoitaKaikkienViesteihinOikein(String string) {
		kaikkienViestit.append(string);
		kaikkienViestit.append("\n");
		kirjoitaViesti.setText("");
	}
	private boolean onkoViestejaYliRowCountin() {
		//TODO kehitä tähän joku pätevä dynaaminen tarkistus
		int height = kaikkienViestit.getHeight();
		int rowCount = height / 17;
		if (viestit.size() > rowCount) {
			return true;
		}
		return false;
	}

	//Action-listenerit ja niiden lisäykset
	private void addActionListenerToButton() {
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
}
