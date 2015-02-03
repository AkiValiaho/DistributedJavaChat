package valiaho.distributedChat;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.crypto.*;
import javax.swing.*;

import valiaho.gui.*;
/**
 * Yksinkertainen chatclienttiohjelma 
 * multithreadaavalle sokettiserverille.
 * @author Aki V�liaho
 *
 */
public class chatClient {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private UUID userID;
	private int portNumber;
	private String hostName;
	private Socket clientSideSocket;
	private chatClientReaderThread readerThread;
	private clientGUI GUI;

	/**
	 * Clienttiohjelman konstruktori, joka
	 * py�r�ytt�� ohjelman k�yntiin 
	 * @param passedHostName mihin osoitteeseen yhdistet��n
	 * @param passedPortNumber mihin porttiin yhdistet��n
	 * @throws IOException
	 */
	public chatClient(String passedHostName, int passedPortNumber,clientGUI GUI) throws IOException {
		assignVariables(passedHostName, passedPortNumber, GUI);
		startReaderThread();
	}
	private void startReaderThread() throws IOException {
		readerThread = new chatClientReaderThread(input,userID,this);
		readerThread.start();
	}
	private void assignVariables(String passedHostName, int passedPortNumber,
			clientGUI GUI) throws UnknownHostException, IOException {
		this.GUI = GUI;
		portNumber = passedPortNumber;
		hostName = passedHostName;
		clientSideSocket = new Socket(hostName, portNumber);
		output = new ObjectOutputStream(clientSideSocket.getOutputStream());
		input = new ObjectInputStream(clientSideSocket.getInputStream());
		userID = UUID.randomUUID();
		Viesti viestiOlio;
		viestiOlio = new Viesti();
		viestiOlio.setInformationObjectBoolean(true);
		viestiOlio.setUserID(userID);
		output.writeObject(viestiOlio);
		
	}
	public void lahetaViesti(String text) throws IOException {
		// TODO Auto-generated method stub
			if (!text.isEmpty()) {
				Viesti viestiOlio;
				if (text.equals("LOPETA")) {
					writeDisconnectObjectToOutputSocket(text);
					try {
						//Ja suljetaan ylim��r�iset jutut pois
						closePipes();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				writeViestiToOutputSocket(text);
		}
	}
	private void writeDisconnectObjectToOutputSocket(String text)
			throws UnknownHostException, IOException {
		Viesti viestiOlio;
		viestiOlio = new Viesti(text, InetAddress.getLocalHost().getHostAddress(),userID);
		//Tallennetaan disconnect-k�sky olioon
		viestiOlio.setDisconnect(true);
		//Kirjoitetaan disconnect-k�skyn sis�lt�v� olio sokettiin
		output.writeObject(viestiOlio);
	}
	private void writeViestiToOutputSocket(String text)
			throws UnknownHostException, IOException {
		Viesti viestiOlio;
		viestiOlio = new Viesti(text, InetAddress.getLocalHost().getHostAddress(),userID);
		output.writeObject(viestiOlio);
	}
	private void closePipes() throws InterruptedException, IOException {
		readerThread.join();
		clientSideSocket.close();
		input.close();
		output.close();
		System.exit(-1);
	}
	public void kirjoitaGUIhin(String viesti) {
		GUI.kirjoitaKaikkienViesteihin(viesti);
	}
}