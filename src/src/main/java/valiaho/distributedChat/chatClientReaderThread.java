package valiaho.distributedChat;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Clienttiohjelman sokettiin tulevaa sy�tett� lukeva threadi
 * @author Aki
 *
 */
public class chatClientReaderThread extends Thread{
	private ObjectInputStream in;
	private UUID userID;
	private Boolean kuuntelee = true;
	/**
	 * Konstruktori luokalle
	 * @param input Mit� kuunnellaan?
	 * @param userID K�ytt�j�lle luotu ID
	 * @throws IOException
	 */
	public chatClientReaderThread(ObjectInputStream input, UUID userID) throws IOException {
		this.in = input;
		this.userID = userID;
	}
	@Override
	public void run() {
		while (getKuuntelee()) {
			try {
				//puretaan sarjallistettu objekti
				Viesti viestiIn = (Viesti)in.readObject();
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
				//Samoja viestej� on turha n�ytt�� ne l�hett�neelle k�ytt�j�lle
				//joten j��d��n odottamaan seuraavaa viesti�
				if (viesti == null || userID.equals(viestiIn.getUserID())) {
					continue;
				}
				System.out.println(viesti);
			} catch (ClassNotFoundException | IOException e) {
				//Jos palvelimelle sattuu jotakin ep�ilytt�v��
				System.out.println("Yhteys palvelimelle on katkennut yll�tt�en! Yrit� my�hemmin uudelleen");
				break;
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
