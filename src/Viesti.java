import java.io.*;
import java.util.*;
/**
 * Viesti-olio kapsuloi lähettäjän IP-osoitteen ja lähetetyn viestin sarjallistettavaksi
 * luokaksi, jonka olioita voidaan lähetellä ObjectOutputStreamin kautta soketteihin.
 * @author Aki Väliaho
 *
 */
public class Viesti implements Serializable{
	private static final long serialVersionUID = 1L;
	private String ip = "0",viesti ="0";
	private Boolean disconnect = false;
	private UUID userID;
	private Boolean kryptattu = false;
	private Boolean yllapitajan = false;
	/**
	 * Konstuktori luokalle
	 * @param viesti Viestin sisältö
	 * @param ip Lähettäjän IP-osoite
	 * @param userID2 Lähettäjän uniikki UIID
	 */
	public Viesti(String viesti, String ip, UUID userID2) {
		this.setIp(ip);
		this.setViesti(viesti);
		this.setUserID(userID2);
	}
	public Viesti() {
	}
	/**
	 * Korvaa oletusarvoisen writeObjectin lisäämällä päälle yksinkertaisen salakirjoituslayerin
	 * @param out Mihin kirjoitetaan
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		//"Encryptataan" tiedot Caesarilla, että ei aivan suoraan saa napsittua serializable-esitystä verkosta
		//Tässä voisi käyttää jotakin hienompaakin suhteellisen helposti. Lähettäisi vaikka privaattikeyt jonkun
		//turvallisemman verkkoprotokollan kautta serveriltä clientille ja purkaisi sitten tässä oliossa käyttäen tuota
		//keytä (tuhoten avaimen tietysti oliosta ennen eteenpäin lähettämistä).
		//Helpompi tapa olisi käyttää tietysti jotakin valmislibraryä objektin signaamiseen ja sealaamiseen. API:sta löytyy itseasiassa
		//kaksi hyödyllistä luokkaa: https://docs.oracle.com/javase/7/docs/api/java/security/SignedObject.html ja
		//https://docs.oracle.com/javase/7/docs/api/javax/crypto/SealedObject.html joilla voisi tällaisen wrapperin helposti kehitellä
		//ja täten välttyisi näiden writeObjektin ja readObjektin kirjoittamiselta.
		if (getKryptattu()) {
			//On jo kryptattu, sarjallistetaan
			out.defaultWriteObject();
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < getIp().length(); i++){
				char c = getIp().charAt(i);
				stringBuilder.append((char)c+1);
			    stringBuilder.append(',');
			}
			stringBuilder.append("");
			setIp(stringBuilder.toString());
			stringBuilder = new StringBuilder();
			for (int i = 0; i < getViesti().length(); i++){
			    char c = getViesti().charAt(i);
			    stringBuilder.append((char)c+1);
			    stringBuilder.append(',');
			}
			setViesti(stringBuilder.toString());
			//Asetetaan tieto, että objektin fieldit on nyt 'salakirjoitettu'
			setKryptattu(true);
			out.defaultWriteObject();
			//Kirjoitetaan ulos defaultilla
		}
		}
	/**
	 * Puretaan sarjallistus
	 * @param in Mitä kuunnellaan
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
	//Ja käännetään koko homma takaisin ykkös-Caesarista
		in.defaultReadObject();
		StringBuilder stringBuilder = new StringBuilder();
		List<String> ipArray =Arrays.asList(getIp().split(","));
		for (String string : ipArray) {
			stringBuilder.append((char)(Integer.parseInt(string)-1));
		}
		setIp(stringBuilder.toString());
		stringBuilder = new StringBuilder();
		ipArray = Arrays.asList(getViesti().split(","));
		for (String string : ipArray) {
			  stringBuilder.append((char)(Integer.parseInt(string)-1));
		}
		setViesti(stringBuilder.toString());
		//Asetetaan tieto, että objektin fieldit on purettu
		setKryptattu(false);
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getViesti() {
		return viesti;
	}
	public void setViesti(String viesti) {
		this.viesti = viesti;
	}
	public UUID getUserID() {
		return userID;
	}
	public void setUserID(UUID userID) {
		this.userID = userID;
	}
	public Boolean getDisconnect() {
		return disconnect;
	}
	public void setDisconnect(Boolean disconnect) {
		this.disconnect = disconnect;
	}
	public Boolean getYllapitajan() {
		return yllapitajan;
	}
	public void setYllapitajan(Boolean yllapitajan) {
		this.yllapitajan = yllapitajan;
	}
	public Boolean getKryptattu() {
		return kryptattu;
	}
	public void setKryptattu(Boolean kryptattu) {
		this.kryptattu = kryptattu;
	}
}