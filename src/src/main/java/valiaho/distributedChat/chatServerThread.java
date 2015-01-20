package valiaho.distributedChat;
import java.io.*;
import java.net.*;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		while (true) {
			try {
				//Vastaanotetaan asiakkaalta tai serverilt� Viesti-objekti
				Viesti viesti = (Viesti)in.readObject();
				if (viesti == null) {
					continue;
				}
				if (viesti.getDisconnect() == true) {
					//L�hetet��n soketille takas tieto ett� valmis kaatumaan
					//Poistetaan instanssi listalta
					chatServer.arrayOfClients.remove(this);
					//Ilmoitetaan aiheesta
					System.out.println(viesti.getIp()+" "+viesti.getUserID()+" "+" on poistunut");
					out.writeObject(viesti);
					break;
				}
				//L�hetet��n asiakkaiden putkeen saapunut viesti
				//Ja tulostetaan palvelimelle viesti
				System.out.println(viesti.getIp()+" "+":"+" "+viesti.getViesti());
				for (chatServerThread thrad : chatServer.arrayOfClients) {
					thrad.out.writeObject(viesti);
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				chatServer.arrayOfClients.remove(this);
				System.out.println("Yhteys on katkaistu turvattomasti!");
				break;
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