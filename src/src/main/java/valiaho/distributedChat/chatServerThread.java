package valiaho.distributedChat;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;

import valiaho.gui.*;
import valiaho.security.*;
/**
 * Serverin jokaiselle clientille luoma threadi, joka tutkii asiakkaalta
 * tulevaa sy�tett� ja l�hett�� sen eteenp�in muille threadeille.
 * @author Aki V�liaho
 *
 */
public class chatServerThread extends Thread {
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket socket;
	private chatServer chatServer;
	private UUID uuid;
	private String ip;
	private LocalEncryptionFactory encryptionFactory; 
	/**
	 * Konstruktori luokalle
	 * @param accept Serverin hyv�ksym� soketti johon muodostettu yhteys passataan argumenttina
	 * @param chatServer Serverin objekti, ett� p��st��n k�siksi listaan
	 */
	public chatServerThread(Socket accept, chatServer chatServer) {
		try {
			this.out = new ObjectOutputStream(accept.getOutputStream());
			this.in = new ObjectInputStream(accept.getInputStream());
			this.setSocket(accept);
			this.encryptionFactory = chatServer.getEncryptionFactory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		try {
			encryptionFactory.setLocationToKeyString(new File("theKey.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {
				//Vastaanotetaan asiakkaalta tai serverilt� Viesti-objekti
				SealedObject readObject = (SealedObject)in.readObject();
				Viesti viesti = encryptionFactory.getDecrypterSealedObject(readObject);
				if (viesti.getInformationObjectBoolean()) {
					this.uuid = viesti.getUserID();
					this.ip = viesti.getIp();
					continue;
				}
				if (viesti.getDisconnect() == true) {
					//L�hetet��n soketille takas tieto ett� valmis kaatumaan
					//Poistetaan instanssi listalta
					chatServer.arrayOfClients.remove(this);
					//Ilmoitetaan aiheesta
					chatServer.changesToListBoolean = true;
					System.out.println(viesti.getIp()+" "+viesti.getUserID()+" "+" on poistunut");
					out.writeObject(viesti);
					break;
				}
				//L�hetet��n asiakkaiden putkeen saapunut viesti
				//Ja tulostetaan palvelimelle viesti
				System.out.println(viesti.getIp()+" "+":"+" "+viesti.getViesti());
				SealedObject toWrite = writeSealedObjectToSocket(viesti);
				for (chatServerThread thrad : chatServer.arrayOfClients) {
					thrad.out.writeObject(toWrite);
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				chatServer.arrayOfClients.remove(this);
				System.out.println("Yhteys on katkaistu turvattomasti!");
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
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public ObjectInputStream getIn() {
		return in;
	}
	public void setIn(ObjectInputStream in) {
		this.in = in;
	}
	public ObjectOutputStream getOut() {
		return out;
	}
	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}
	private SealedObject writeSealedObjectToSocket(Viesti viestiOlio)
			throws IOException {
		encryptionFactory.setTt(viestiOlio);
		SealedObject object;
		try {
			object = encryptionFactory.getSealedObject();
			return object;
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
}
}