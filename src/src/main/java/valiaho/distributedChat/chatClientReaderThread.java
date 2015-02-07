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
 * Clienttiohjelman sokettiin tulevaa sy�tett� lukeva threadi
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
	 * @param input Mit� kuunnellaan?
	 * @param userID K�ytt�j�lle luotu ID
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
						//Yll�pit�j� haluaa sulkea palvelimen
						System.out.println("Yll�pit�j� on sammuttanut palvelimen, paina enter lopettaaksesi");
						break;
					} else {
						//Yll�pit�j� haluaa sanoa jotain
						System.out.println("Yll�pit�j� sanoo: "+viesti);
						continue;
					}
				}
				//Tutkaillaan onko viesti-paketissa k�sky lopettaa threadi
				if (viestiIn.getDisconnect()) {
					break;
				}
				//TODO lisää tähän referenssi uusien viestien kenttään GUI:hin
				controller.kirjoitaGUIhin(viesti);
			} catch (ClassNotFoundException | IOException e) {
				//Jos palvelimelle sattuu jotakin ep�ilytt�v��
				System.out.println("Yhteys palvelimelle on katkennut yll�tt�en! Yrit� my�hemmin uudelleen");
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
