import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Clienttiohjelman sokettiin tulevaa syötettä lukeva threadi
 * @author Aki
 *
 */
public class chatClientReaderThread extends Thread{
	private ObjectInputStream in;
	private UUID userID;
	private Boolean kuuntelee = true;
	/**
	 * Konstruktori luokalle
	 * @param input Mitä kuunnellaan?
	 * @param userID Käyttäjälle luotu ID
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
						//Ylläpitäjä haluaa sulkea palvelimen
						System.out.println("Ylläpitäjä on sammuttanut palvelimen, paina enter lopettaaksesi");
						break;
					} else {
						//Ylläpitäjä haluaa sanoa jotain
						System.out.println("Ylläpitäjä sanoo: "+viesti);
						continue;
					}
				}
				//Tutkaillaan onko viesti-paketissa käsky lopettaa threadi
				if (viestiIn.getDisconnect()) {
					break;
				}
				//Samoja viestejä on turha näyttää ne lähettäneelle käyttäjälle
				//joten jäädään odottamaan seuraavaa viestiä
				if (viesti == null || userID.equals(viestiIn.getUserID())) {
					continue;
				}
				System.out.println(viesti);
			} catch (ClassNotFoundException | IOException e) {
				//Jos palvelimelle sattuu jotakin epäilyttävää
				System.out.println("Yhteys palvelimelle on katkennut yllättäen! Yritä myöhemmin uudelleen");
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