package valiaho.distributedChat;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.swing.*;

import valiaho.gui.*;
import valiaho.security.*;
/**
 * Clienttiohjelman sokettiin tulevaa syï¿½tettï¿½ lukeva threadi
 * @author Aki
 *
 */
public class chatClientReaderThread extends Thread{
	private ObjectInputStream in;
	private UUID userID;
	private Boolean kuuntelee = true;
	private chatClient controller;
	private LocalEncryptionFactory encryptionFactory = new LocalEncryptionFactory();
	/**
	 * Konstruktori luokalle
	 * @param input Mitï¿½ kuunnellaan?
	 * @param userID Kï¿½yttï¿½jï¿½lle luotu ID
	 * @param kaikkienViestit 
	 * @throws IOException
	 */
	public chatClientReaderThread(ObjectInputStream input, UUID userID,chatClient chatClient) throws IOException {
		this.in = input;
		this.userID = userID;
		this.controller = chatClient;
	}
	@Override
	public void run() {
		try {
			encryptionFactory.setLocationToKeyString(new File("theKey.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (getKuuntelee()) {
			try {
				//puretaan sarjallistettu objekti
				Viesti viestiIn = encryptionFactory.getDecrypterSealedObject((SealedObject)in.readObject());
				String viesti = viestiIn.getViesti();
				if (viestiIn.getYllapitajan()) {
					if (viestiIn.getDisconnect()) {
						//Yllï¿½pitï¿½jï¿½ haluaa sulkea palvelimen
						JOptionPane.showMessageDialog(null, "Ylläpitäjä sulkee palvelimen");
						break;
					} else {
						//Yllï¿½pitï¿½jï¿½ haluaa sanoa jotain
						controller.kirjoitaGUIhin("Ylläpitäjä sanoo: "+viesti);
						continue;
					}
				}
				//Tutkaillaan onko viesti-paketissa kï¿½sky lopettaa threadi
				if (viestiIn.getDisconnect()) {
					break;
				}
				//TODO lisÃ¤Ã¤ tÃ¤hÃ¤n referenssi uusien viestien kenttÃ¤Ã¤n GUI:hin
				controller.kirjoitaGUIhin(viesti);
			} catch (ClassNotFoundException | IOException e) {
				//Jos palvelimelle sattuu jotakin epï¿½ilyttï¿½vï¿½ï¿½
				JOptionPane.showMessageDialog(null, "Yhteys palvelimelle on katkennut yllättäen! Yritä myöhemmin uudelleen");
				break;
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	public Boolean getKuuntelee() {
		return kuuntelee;
	}
	public Boolean setKuuntelee(Boolean kuuntelee) {
		this.kuuntelee = kuuntelee;
		return kuuntelee;
	}
}
