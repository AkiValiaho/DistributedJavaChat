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
 * tulevaa syï¿½tettï¿½ ja lï¿½hettï¿½ï¿½ sen eteenpï¿½in muille threadeille.
 * @author Aki Vï¿½liaho
 *
 */
public class chatServerThread extends Thread {
	private chatServer chatServer;
	private LocalEncryptionFactory encryptionFactory;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private SealedObject readObject;
	private SealedObject toWrite; 
	private Socket socket;
	private String ip;
	private UUID uuid;
	/**
	 * Konstruktori luokalle
	 * @param accept Serverin hyvï¿½ksymï¿½ soketti johon muodostettu yhteys passataan argumenttina
	 * @param chatServer Serverin objekti, ettï¿½ pï¿½ï¿½stï¿½ï¿½n kï¿½siksi listaan
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
				readObject = (SealedObject)in.readObject();
				Viesti viesti = encryptionFactory.getDecrypterSealedObject(readObject);
				if (viesti.getInformationObjectBoolean()) {
					//Haluan että nämä tiedot menisi myös jossakin välissä tietyllä timelimitillä erillisessä threadissa
					//erilliselle DB:lle!
					this.uuid = viesti.getUserID();
					this.ip = viesti.getIp();
					viesti.setViesti("Käyttäjä yhdisti palveluun");
					chatServer.kaikkiViestit.add(viesti);
					continue;
				}
				if (viesti.getDisconnect() == true) {
					//Lï¿½hetetï¿½ï¿½n soketille takas tieto ettï¿½ valmis kaatumaan
					//Poistetaan instanssi listalta
					chatServer.arrayOfClients.remove(this);
					//Ilmoitetaan aiheesta
					chatServer.changesToListBoolean = true;
					System.out.println(viesti.getIp()+" "+viesti.getUserID()+" "+" on poistunut");
					viesti.setViesti("Käyttäjä on poistunut");
					chatServer.kaikkiViestit.add(viesti);
					out.writeObject(viesti);
					break;
				}
				//Lï¿½hetetï¿½ï¿½n asiakkaiden putkeen saapunut viesti
				//Ja tulostetaan palvelimelle viesti
				chatServer.kaikkiViestit.add(viesti);
				System.out.println(viesti.getIp()+" "+":"+" "+viesti.getViesti());
				toWrite = LocalEncryptionFactory.writeSealedObjectToSocket(viesti, encryptionFactory);
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
}